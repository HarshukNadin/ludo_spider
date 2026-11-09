package Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The game itself: turns, dice, legal moves, captures, Double Blocks, special squares and the finishing order.
 * Contains no UI code, so it can be tested on its own (Model in MVC).
 */
public class Game {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;

    private final Difficulty difficulty;
    private final List<Player> players;
    private final Board board;
    private final Dice dice;
    private final QuestionBank questionBank;
    private final Random random;
    private final List<GameListener> listeners = new ArrayList<>();
    private final List<Player> finishOrder = new ArrayList<>();
    private final LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime endedAt;

    private int current;
    private GamePhase phase = GamePhase.WAITING_FOR_ROLL;
    private DiceResult lastRoll;
    private int sixesInRow;
    private boolean ultraBoostNext;
    private Question pendingQuestion;
    private Piece questionPiece;

    /**
     * Starts a new game. Every player's pieces begin in their castle and the first player rolls first.
     *
     * @param difficulty   Easy, Medium or Hard
     * @param players      2 to 4 players with different names and colors
     * @param questionBank questions for Question squares, or null for no questions (tests)
     * @param random       source of randomness for the board and the dice
     * @throws IllegalArgumentException if the players are not valid
     */
    public Game(Difficulty difficulty, List<Player> players, QuestionBank questionBank, Random random) {
        validatePlayers(players);
        this.difficulty = difficulty;
        this.players = List.copyOf(players);
        this.random = random;
        this.board = new Board(difficulty, random);
        this.dice = new Dice(random);
        this.questionBank = questionBank;
        if (questionBank != null) questionBank.resetUsed();
    }

    /**
     * Checks the player list: 2 to 4 players, no repeated color, no repeated name (case is ignored).
     *
     * @param players the players of a new game
     * @throws IllegalArgumentException with a message saying what is wrong
     */
    public static void validatePlayers(List<Player> players) {
        if (players.size() < MIN_PLAYERS || players.size() > MAX_PLAYERS)
            throw new IllegalArgumentException("A game needs 2 to 4 players");
        Set<PlayerColor> colors = EnumSet.noneOf(PlayerColor.class);
        for (Player p : players)
            if (!colors.add(p.getColor())) throw new IllegalArgumentException("Each player needs a different color");
        if (players.stream().map(p -> p.getName().toLowerCase()).distinct().count() != players.size())
            throw new IllegalArgumentException("Each player needs a different name");
    }

    // ---------- Observer ----------
    /** @param l called after every change of the game state */
    public void addListener(GameListener l) { listeners.add(l); }
    private void fire(String message) { for (GameListener l : listeners) l.gameChanged(this, message); }

    // ---------- Getters ----------
    public Difficulty getDifficulty() { return difficulty; }
    public List<Player> getPlayers() { return players; }
    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return players.get(current); }
    public GamePhase getPhase() { return phase; }
    public DiceResult getLastRoll() { return lastRoll; }
    public Question getPendingQuestion() { return pendingQuestion; }
    public List<Player> getFinishOrder() { return Collections.unmodifiableList(finishOrder); }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public Duration getDuration() { return Duration.between(startedAt, endedAt != null ? endedAt : LocalDateTime.now()); }

    // ---------- Turn flow ----------
    /**
     * Rolls the dice for the current player.
     *
     * @throws IllegalStateException if the game is not waiting for a roll
     */
    public void roll() {
        roll(dice.roll(difficulty, ultraBoostNext));
    }

    /** Uses a given roll; public so tests can control the dice. */
    public void roll(DiceResult result) {
        requirePhase(GamePhase.WAITING_FOR_ROLL);
        ultraBoostNext = false;
        lastRoll = result;
        Player p = getCurrentPlayer();
        if (result.isSix() && ++sixesInRow == 3) {
            nextTurn(p.getName() + " rolled a third 6 in a row. The roll is cancelled.");
            return;
        }
        if (legalMoves().isEmpty()) {
            boolean allInCastle = p.getPieces().stream().allMatch(x -> x.getState() == PieceState.IN_CASTLE);
            nextTurn(p.getName() + " rolled " + result + ". " + (allInCastle ? "A 6 is needed to leave the castle." : "No piece can move."));
            return;
        }
        phase = GamePhase.CHOOSING_PIECE;
        fire(p.getName() + " rolled " + result + ". Choose a piece.");
    }

    /** Pieces of the current player that can move with the last roll. */
    public List<Piece> legalMoves() {
        List<Piece> out = new ArrayList<>();
        if (lastRoll == null) return out;
        for (Piece piece : getCurrentPlayer().getPieces()) if (targetOf(piece) != null) out.add(piece);
        return out;
    }

    /** Where the piece would end up with the last roll, or null if it cannot move. */
    public Integer targetOf(Piece piece) {
        switch (piece.getState()) {
            case FINISHED: return null;
            case IN_CASTLE: return lastRoll.isSix() && !blockedByOpponent(piece.getOwner(), 0) ? 0 : null;
            case IN_HOME_PATH: return walk(piece, lastRoll.normal()); // the home path uses the Normal die only
            default: return walk(piece, lastRoll.total());
        }
    }

    /**
     * Moves one of the current player's pieces by the last roll.
     *
     * @param piece a piece from {@link #legalMoves()}
     * @throws IllegalStateException    if the game is not waiting for a piece choice
     * @throws IllegalArgumentException if the piece is not the current player's or cannot move
     */
    public void move(Piece piece) {
        requirePhase(GamePhase.CHOOSING_PIECE);
        if (piece.getOwner() != getCurrentPlayer()) throw new IllegalArgumentException("Not your piece");
        Integer to = targetOf(piece);
        if (to == null) throw new IllegalArgumentException("This piece cannot move");
        piece.setProgress(to);
        StringBuilder msg = new StringBuilder(piece.getOwner().getName() + " moved piece " + piece.getNumber() + ".");
        captureAt(piece, msg);
        applySquare(piece, msg);
    }

    /**
     * Answers the pending question and moves the piece forward or back by the difficulty's steps.
     *
     * @param answer the chosen answer, 1 to 4
     * @throws IllegalStateException if no question is waiting for an answer
     */
    public void answerQuestion(int answer) {
        requirePhase(GamePhase.ANSWERING_QUESTION);
        int steps = difficulty.getQuestionSteps();
        boolean right = pendingQuestion.isCorrect(answer);
        Integer to = walk(questionPiece, right ? steps : -steps);
        if (to != null) questionPiece.setProgress(to);
        StringBuilder msg = new StringBuilder(right ? "Correct! " : "Wrong answer. ");
        msg.append("Piece ").append(questionPiece.getNumber()).append(right ? " moves " + steps + " forward." : " moves " + steps + " back.");
        captureAt(questionPiece, msg);
        pendingQuestion = null;
        questionPiece = null;
        endMove(msg.toString());
    }

    // ---------- Rules ----------
    private void applySquare(Piece piece, StringBuilder msg) {
        int idx = piece.getTrackIndex();
        SquareType type = idx < 0 ? SquareType.NORMAL : board.getSquare(idx).getType();
        if (type == SquareType.QUESTION && questionBank != null) {
            var q = questionBank.randomQuestion(difficulty, random);
            if (q.isPresent()) {
                pendingQuestion = q.get();
                questionPiece = piece;
                phase = GamePhase.ANSWERING_QUESTION;
                fire(msg + " Question square!");
                return;
            }
        }
        if (type == SquareType.SURPRISE) msg.append(" Surprise: ").append(applySurprise(piece, Surprise.draw(random)));
        endMove(msg.toString());
    }

    String applySurprise(Piece piece, Surprise s) {
        switch (s) {
            case GO_TO_FINAL_HOME -> piece.setProgress(Board.FINAL_INDEX);
            case BACK_TO_CASTLE -> piece.sendToCastle();
            case THREE_STEPS_BACK -> { Integer to = walk(piece, -3); if (to != null) piece.setProgress(to); }
            case SKIP_NEXT_TURN -> piece.getOwner().setSkipNextTurn(true);
            case ULTRABOOST_ROLL -> ultraBoostNext = true;
            case OPPONENT_DICE -> { /* TODO: agree with the group how this effect works; for now it does nothing. */ }
        }
        return s.getMessage();
    }

    /** Walks step by step; stops before an opponent's Double Block and needs an exact roll for the Final Home. */
    private Integer walk(Piece piece, int steps) {
        int pos = piece.getProgress(), dir = Integer.signum(steps);
        for (int i = 0; i < Math.abs(steps); i++) {
            int next = pos + dir;
            if (next > Board.FINAL_INDEX) return dir > 0 ? null : pos;
            if (next < 0) return pos;
            if (blockedByOpponent(piece.getOwner(), next)) return pos == piece.getProgress() ? null : pos;
            pos = next;
        }
        return pos == piece.getProgress() && steps != 0 ? null : pos;
    }

    private boolean blockedByOpponent(Player mover, int progress) {
        int idx = Board.toTrackIndex(mover.getColor(), progress);
        if (idx < 0) return false;
        for (Player other : players) {
            if (other == mover) continue;
            if (piecesAt(other, idx).size() >= 2) return true;
        }
        return false;
    }

    private List<Piece> piecesAt(Player player, int trackIndex) {
        return player.getPieces().stream().filter(x -> x.getTrackIndex() == trackIndex).toList();
    }

    private void captureAt(Piece piece, StringBuilder msg) {
        int idx = piece.getTrackIndex();
        if (idx < 0 || board.getSquare(idx).getType().isSafe()) return;
        for (Player other : players) {
            if (other == piece.getOwner()) continue;
            List<Piece> there = piecesAt(other, idx);
            if (there.size() == 1) {
                there.get(0).sendToCastle();
                msg.append(" Captured ").append(other.getName()).append("'s piece!");
            }
        }
    }

    private void endMove(String message) {
        Player p = getCurrentPlayer();
        if (p.hasFinished() && !finishOrder.contains(p)) {
            finishOrder.add(p);
            message += " " + p.getName() + " finished in place " + finishOrder.size() + "!";
        }
        if (finishOrder.size() >= players.size() - 1) {
            for (Player x : players) if (!finishOrder.contains(x)) finishOrder.add(x);
            endedAt = LocalDateTime.now();
            phase = GamePhase.GAME_OVER;
            fire(message + " Game over.");
            return;
        }
        if (lastRoll != null && lastRoll.isSix() && !p.hasFinished()) {
            phase = GamePhase.WAITING_FOR_ROLL;
            fire(message + " A 6 gives " + p.getName() + " another roll.");
        } else {
            nextTurn(message);
        }
    }

    private void nextTurn(String message) {
        sixesInRow = 0;
        do {
            current = (current + 1) % players.size();
            Player p = getCurrentPlayer();
            if (p.isSkipNextTurn() && !finishOrder.contains(p)) {
                p.setSkipNextTurn(false);
                message += " " + p.getName() + " skips this turn.";
                continue;
            }
            if (!finishOrder.contains(p)) break;
        } while (true);
        phase = GamePhase.WAITING_FOR_ROLL;
        fire(message + " " + getCurrentPlayer().getName() + "'s turn.");
    }

    private void requirePhase(GamePhase expected) {
        if (phase != expected) throw new IllegalStateException("Expected " + expected + " but game is in " + phase);
    }
}
