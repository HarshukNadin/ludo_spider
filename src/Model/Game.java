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
    private String noMoveReason;

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
        this.board = difficulty == Difficulty.EASY ? new Board(difficulty, random) : new OuterBoard(difficulty, random);
        for (Player p : this.players) p.setBoard(board);
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
    /** @return the level chosen in setup */
    public Difficulty getDifficulty() { return difficulty; }
    /** @return the players in turn order */
    public List<Player> getPlayers() { return players; }
    /** @return the board of this game */
    public Board getBoard() { return board; }
    /** @return the player whose turn it is */
    public Player getCurrentPlayer() { return players.get(current); }
    /** @return what the game is waiting for */
    public GamePhase getPhase() { return phase; }
    /** @return the last roll, or null before the first roll */
    public DiceResult getLastRoll() { return lastRoll; }
    /** @return the question to answer, or null when there is none */
    public Question getPendingQuestion() { return pendingQuestion; }
    /** @return the players who finished, first place first */
    public List<Player> getFinishOrder() { return Collections.unmodifiableList(finishOrder); }
    /** @return when the game started */
    public LocalDateTime getStartedAt() { return startedAt; }
    /** @return when the game ended, or null while it is running */
    public LocalDateTime getEndedAt() { return endedAt; }
    /** @return the game time so far, or the full time once the game is over */
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

    /**
     * Uses a given roll for the current player; public so tests can control the dice.
     * Only the active player can roll, because the game always rolls for {@link #getCurrentPlayer()} (FR-3.01).
     *
     * @param result the dice values
     * @throws IllegalStateException if the game is not waiting for a roll
     */
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
            // Nothing moves (FR-3.07). The game waits so the players can see why, then passTurn() moves on.
            noMoveReason = whyNoMove();
            phase = GamePhase.NO_MOVE;
            fire(p.getName() + " rolled " + result + ". " + noMoveReason);
            return;
        }
        phase = GamePhase.CHOOSING_PIECE;
        fire(p.getName() + " rolled " + result + ". Choose a piece.");
    }

    /**
     * Ends a turn in which no piece could move (FR-3.07). Nothing on the board changes.
     *
     * @throws IllegalStateException if the last roll left a legal move
     */
    public void passTurn() {
        requirePhase(GamePhase.NO_MOVE);
        noMoveReason = null;
        nextTurn(getCurrentPlayer().getName() + " passes the turn.");
    }

    /** @return why no piece can move with the last roll, or null when the game is not in {@link GamePhase#NO_MOVE} */
    public String getNoMoveReason() { return noMoveReason; }

    /** Explains, for the screen, why none of the current player's pieces can move. */
    private String whyNoMove() {
        boolean overshoot = false, blocked = false, onBoard = false;
        for (Piece piece : getCurrentPlayer().getPieces()) {
            PieceState state = piece.getState();
            if (state == PieceState.FINISHED || state == PieceState.IN_CASTLE) continue;
            onBoard = true;
            int steps = state == PieceState.IN_HOME_PATH ? lastRoll.normal() : lastRoll.total();
            if (piece.getProgress() + steps > piece.getOwner().getFinalIndex()) overshoot = true;
            else blocked = true;
        }
        if (!onBoard) return "You need a 6 to bring a piece out.";
        if (overshoot && !blocked) return "Your pieces need an exact roll to reach Final Home.";
        if (blocked && !overshoot) return "A Double Block is in front of your pieces.";
        return "Your pieces are blocked or need an exact roll to reach Final Home.";
    }

    /**
     * The pieces the current player may choose after the last roll (FR-3.05).
     * The player can pick any of them; a piece that is not in this list cannot move.
     *
     * @return the movable pieces, empty before the first roll or when nothing can move
     */
    public List<Piece> legalMoves() {
        List<Piece> out = new ArrayList<>();
        if (lastRoll == null) return out;
        for (Piece piece : getCurrentPlayer().getPieces()) if (targetOf(piece) != null) out.add(piece);
        return out;
    }

    /**
     * Where a piece would end up with the last roll.
     * <ul>
     * <li>From the castle: only with a 6 on the Normal die, to the start square (progress 0).
     *     The Boost or UltraBoost die is not used for leaving the castle (FR-3.04).</li>
     * <li>In the home path: the Normal die only (CR-01).</li>
     * <li>On the shared track: Normal die x extra die. The route turns into the home path
     *     after the color's arrow square, so no extra step is needed for the arrow (FR-3.05).</li>
     * </ul>
     *
     * @param piece one of the current player's pieces
     * @return the progress after the move, or null if this piece cannot move
     */
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
        Player p = getCurrentPlayer();
        if (piece.getOwner() != p)
            throw new IllegalArgumentException("It is " + p.getName() + "'s turn. Move one of " + p.getName() + "'s pieces.");
        Integer to = targetOf(piece);
        if (to == null) throw new IllegalArgumentException("Piece " + piece.getNumber() + " cannot move with this roll. Pick a glowing piece.");
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
        Square square = piece.getSquare();
        SquareType type = square == null ? SquareType.NORMAL : square.getType();
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
            case GO_TO_FINAL_HOME -> piece.setProgress(piece.getOwner().getFinalIndex());
            case BACK_TO_CASTLE -> piece.sendToCastle();
            case THREE_STEPS_BACK -> { Integer to = walk(piece, -3); if (to != null) piece.setProgress(to); }
            case SKIP_NEXT_TURN -> piece.getOwner().setSkipNextTurn(true);
            case ULTRABOOST_ROLL -> ultraBoostNext = true;
            case OPPONENT_DICE -> { /* TODO: agree with the group how this effect works; for now it does nothing. */ }
        }
        return s.getMessage();
    }

    /**
     * Walks step by step, forward or back.
     * An opponent's Double Block stops the piece on the square before it, in both directions (CR-02).
     * Going back never goes behind the start square (CR-10). Going past the Final Home is not allowed (FR-3.10).
     *
     * @return the progress where the piece stops, or null if it cannot move at all
     */
    private Integer walk(Piece piece, int steps) {
        int pos = piece.getProgress(), dir = Integer.signum(steps);
        for (int i = 0; i < Math.abs(steps); i++) {
            int next = pos + dir;
            if (next > piece.getOwner().getFinalIndex()) return dir > 0 ? null : pos;
            if (next < 0) return pos;
            if (blockedByOpponent(piece.getOwner(), next)) return pos == piece.getProgress() ? null : pos;
            pos = next;
        }
        return pos == piece.getProgress() && steps != 0 ? null : pos;
    }

    private boolean blockedByOpponent(Player mover, int progress) {
        Square square = board.squareAt(mover.getColor(), progress);
        if (square == null) return false;
        for (Player other : players) {
            if (other == mover) continue;
            if (piecesAt(other, square).size() >= 2) return true;
        }
        return false;
    }

    private List<Piece> piecesAt(Player player, Square square) {
        return player.getPieces().stream().filter(x -> x.getSquare() == square).toList();
    }

    private void captureAt(Piece piece, StringBuilder msg) {
        Square square = piece.getSquare();
        if (square == null || square.getType().isSafe()) return;
        for (Player other : players) {
            if (other == piece.getOwner()) continue;
            List<Piece> there = piecesAt(other, square);
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
        if (phase == expected) return;
        throw new IllegalStateException(switch (phase) {
            case WAITING_FOR_ROLL -> getCurrentPlayer().getName() + " has to roll the dice first.";
            case CHOOSING_PIECE -> getCurrentPlayer().getName() + " has to choose a piece to move first.";
            case NO_MOVE -> getCurrentPlayer().getName() + " cannot move. Click Next player to go on.";
            case ANSWERING_QUESTION -> "Answer the question first.";
            case GAME_OVER -> "The game is over. Start a new game from the menu.";
        });
    }
}
