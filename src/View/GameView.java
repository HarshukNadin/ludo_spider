package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Model.Difficulty;
import Model.Game;
import Model.GamePhase;
import Model.Piece;
import Model.PieceState;
import Model.Player;
import Model.Question;

/**
 * The game screen (prototype screens 2.1 and 2.2): the board on the left and, on the right,
 * a turn panel in the active player's color with the dice and the Roll dice button,
 * the list of players and what happened so far. It only shows the Model and passes clicks on.
 */
public class GameView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final BoardPanel board = new BoardPanel();
    private final JLabel difficulty = Theme.label(" ", Theme.SMALL, Theme.MUTED);

    private final Card turnCard = new Card();
    private final JLabel turnOf = Theme.label("Turn of", Theme.SMALL, Color.WHITE);
    private final JLabel turnName = Theme.label(" ", Theme.TITLE.deriveFont(28f), Color.WHITE);
    private final JLabel turnColor = Theme.label(" ", Theme.SMALL, Color.WHITE);
    private final DiceFace normalDie = new DiceFace(false);
    private final JLabel times = Theme.label("×", Theme.HEADING, Color.WHITE);
    private final DiceFace boostDie = new DiceFace(true);
    private final JLabel move = Theme.label(" ", Theme.HEADING, Color.WHITE);
    private final JLabel instruction = Theme.label(" ", Theme.BODY_BOLD, Color.WHITE);
    private final JButton roll = Theme.primary("Roll dice");
    private final JButton[] pieceButtons = new JButton[Player.PIECES];
    private final JPanel pieceRow = transparent(new GridLayout(1, Player.PIECES, 6, 0));
    private final JLabel shortcut = Theme.label(" ", Theme.SMALL, Color.WHITE);
    private final List<JLabel> turnTexts = List.of(turnOf, turnName, turnColor, times, move, instruction, shortcut);

    private final JPanel playerRows = transparent(null);
    private final JTextArea log = new JTextArea();
    private final JButton quit = Theme.secondary("Quit to menu");

    /** Builds the screen; it shows a game once {@link #render} is called. */
    public GameView() {
        super(new BorderLayout(16, 0));
        setBackground(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 16));
        add(board, BorderLayout.CENTER);

        JPanel side = transparent(null);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(340, 0));

        JPanel top = transparent(new BorderLayout());
        quit.setFont(Theme.SMALL);
        quit.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        top.add(quit, BorderLayout.WEST);
        top.add(difficulty, BorderLayout.EAST);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        side.add(top);
        side.add(Box.createVerticalStrut(10));
        side.add(buildTurnCard());
        side.add(Box.createVerticalStrut(10));
        side.add(buildPlayersCard());
        side.add(Box.createVerticalStrut(10));
        side.add(buildLogCard());
        for (Component c : side.getComponents()) ((JComponent) c).setAlignmentX(LEFT_ALIGNMENT);
        add(side, BorderLayout.EAST);
    }

    private JComponent buildTurnCard() {
        turnCard.setLayout(new BoxLayout(turnCard, BoxLayout.Y_AXIS));
        turnCard.setBorder(BorderFactory.createEmptyBorder(14, 18, 16, 18));
        turnCard.add(turnOf);
        turnCard.add(turnName);
        turnCard.add(turnColor);
        turnCard.add(Box.createVerticalStrut(12));

        JPanel dice = transparent(new BorderLayout());
        JPanel faces = transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        faces.add(normalDie);
        faces.add(times);
        faces.add(boostDie);
        dice.add(faces, BorderLayout.WEST);
        dice.add(move, BorderLayout.EAST);
        turnCard.add(dice);
        turnCard.add(Box.createVerticalStrut(12));
        turnCard.add(instruction);
        turnCard.add(Box.createVerticalStrut(10));

        for (int i = 0; i < Player.PIECES; i++) {
            pieceButtons[i] = Theme.secondary("Piece " + (i + 1));
            pieceButtons[i].setFont(Theme.SMALL);
            pieceButtons[i].setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
            pieceRow.add(pieceButtons[i]);
        }
        turnCard.add(roll);
        turnCard.add(pieceRow);
        turnCard.add(Box.createVerticalStrut(8));
        turnCard.add(shortcut);
        for (Component c : turnCard.getComponents()) ((JComponent) c).setAlignmentX(LEFT_ALIGNMENT);
        roll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        pieceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        dice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        turnCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        return turnCard;
    }

    private JComponent buildPlayersCard() {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.add(Theme.label("Players", Theme.BODY_BOLD, Theme.TEXT), BorderLayout.NORTH);
        playerRows.setLayout(new BoxLayout(playerRows, BoxLayout.Y_AXIS));
        card.add(playerRows, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        return card;
    }

    private JComponent buildLogCard() {
        Card card = new Card();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.add(Theme.label("What happened", Theme.BODY_BOLD, Theme.TEXT), BorderLayout.NORTH);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(Theme.SMALL);
        log.setForeground(Theme.MUTED);
        log.setBackground(Theme.PANEL);
        log.setBorder(null);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(null);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /**
     * Redraws everything from the Model's current state.
     *
     * @param game    the game to show
     * @param message what just happened, added to the top of the log; may be null
     */
    public void render(Game game, String message) {
        board.setGame(game);
        Player p = game.getCurrentPlayer();
        GamePhase phase = game.getPhase();
        boolean over = phase == GamePhase.GAME_OVER;
        Difficulty d = game.getDifficulty();
        difficulty.setText(name(d.name()) + " board");

        Color color = over ? Theme.PANEL : Colors.of(p.getColor());
        Color text = readableOn(color);
        turnCard.color = color;
        turnTexts.forEach(l -> l.setForeground(text));
        turnOf.setText(over ? "Game over" : "Turn of");
        turnName.setText(over ? game.getFinishOrder().get(0).getName() + " won" : p.getName());
        turnColor.setText(over ? " " : name(p.getColor().name()) + " pieces");

        boolean extraDie = d != Difficulty.EASY;
        times.setVisible(extraDie);
        boostDie.setVisible(extraDie);
        boolean rolled = phase != GamePhase.WAITING_FOR_ROLL && game.getLastRoll() != null;
        normalDie.setValue(rolled ? game.getLastRoll().normal() : 0);
        boostDie.setValue(rolled ? game.getLastRoll().multiplier() : 0);
        move.setText(rolled ? "Move " + game.getLastRoll().total() : " ");

        List<Piece> movable = phase == GamePhase.CHOOSING_PIECE ? game.legalMoves() : List.of();
        instruction.setText(switch (phase) {
            case WAITING_FOR_ROLL -> "Roll the dice to start your turn.";
            case CHOOSING_PIECE -> movable.size() == 1 ? "Choose the glowing piece." : "Choose one of the " + movable.size() + " glowing pieces.";
            case ANSWERING_QUESTION -> "Answer the question.";
            case GAME_OVER -> "Back to the menu to play again.";
        });
        shortcut.setText(switch (phase) {
            case WAITING_FOR_ROLL -> "Shortcut: press R or Space.";
            case CHOOSING_PIECE -> "Shortcut: press the piece number, or click it.";
            default -> " ";
        });
        roll.setVisible(phase != GamePhase.CHOOSING_PIECE);
        pieceRow.setVisible(phase == GamePhase.CHOOSING_PIECE);
        roll.setEnabled(phase == GamePhase.WAITING_FOR_ROLL);
        for (int i = 0; i < Player.PIECES; i++) {
            pieceButtons[i].setEnabled(movable.contains(p.getPiece(i + 1)));
        }
        turnCard.revalidate();
        turnCard.repaint();

        showPlayers(game);
        if (message != null && !message.isBlank()) log.insert(message + "\n\n", 0);
        log.setCaretPosition(0);
    }

    private void showPlayers(Game game) {
        playerRows.removeAll();
        for (Player pl : game.getPlayers()) {
            long castle = count(pl, PieceState.IN_CASTLE), home = count(pl, PieceState.FINISHED);
            long onBoard = Player.PIECES - castle - home;
            boolean active = pl == game.getCurrentPlayer() && game.getPhase() != GamePhase.GAME_OVER;
            JPanel row = transparent(new BorderLayout(10, 0));
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            row.add(new Dot(Colors.of(pl.getColor()), active), BorderLayout.WEST);
            row.add(Theme.label("<html><b>" + pl.getName() + "</b><br><span style='font-size:9pt; color:#A3AECB'>"
                    + castle + " in castle, " + onBoard + " on board, " + home + " home</span></html>",
                    Theme.SMALL, Theme.TEXT), BorderLayout.CENTER);
            row.setAlignmentX(LEFT_ALIGNMENT);
            playerRows.add(row);
        }
        playerRows.revalidate();
        playerRows.repaint();
    }

    private static long count(Player p, PieceState state) {
        return p.getPieces().stream().filter(x -> x.getState() == state).count();
    }

    /** Dark text on light colors (yellow), white text on the others. */
    private static Color readableOn(Color bg) {
        double lum = 0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue();
        return lum > 160 ? Theme.INK : Color.WHITE;
    }

    private static String name(String upper) {
        return upper.charAt(0) + upper.substring(1).toLowerCase(Locale.ROOT);
    }

    private static JPanel transparent(LayoutManager layout) {
        JPanel p = layout == null ? new JPanel() : new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    /** Empties the log, for a new game. */
    public void clearLog() { log.setText(""); }

    /**
     * Shows a question and waits for an answer.
     *
     * @param q the question
     * @return the chosen answer, 1 to 4
     */
    public int askQuestion(Question q) {
        Object[] options = q.getAnswers().toArray();
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(this, "<html><body style='width:320px'>" + q.getText() + "</body></html>",
                    "Question (" + q.getTopic() + ")", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        } while (choice < 0);
        return choice + 1;
    }

    /** @param text a message to show in a dialog */
    public void showMessage(String text) { JOptionPane.showMessageDialog(this, text); }

    /** @param l runs when Roll dice is clicked */
    public void onRoll(ActionListener l) { roll.addActionListener(l); }

    /** @param l gets the piece number (1-4) when a piece button is clicked */
    public void onPiece(IntConsumer l) {
        for (int i = 0; i < Player.PIECES; i++) {
            int n = i + 1;
            pieceButtons[i].addActionListener(e -> l.accept(n));
        }
    }

    /** @param l runs when Quit to menu is clicked */
    public void onQuit(ActionListener l) { quit.addActionListener(l); }

    /** A rounded panel in one color. */
    private static class Card extends JPanel {
        private static final long serialVersionUID = 1L;
        Color color = Theme.PANEL;

        Card() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g.dispose();
        }
    }

    /** The color dot in the players list; the active player's dot has a ring. */
    private static class Dot extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private final boolean active;

        Dot(Color color, boolean active) {
            this.color = color;
            this.active = active;
            setPreferredSize(new Dimension(24, 24));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int y = (getHeight() - 20) / 2;
            g.setColor(color);
            g.fillOval(2, y + 2, 16, 16);
            if (active) {
                g.setColor(Theme.TEXT);
                g.drawOval(0, y, 20, 20);
            }
            g.dispose();
        }
    }
}
