package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Game;
import model.GamePhase;
import model.Piece;
import model.Player;
import model.Question;

/** The game screen: board, turn panel, dice, piece buttons and a move log. */
public class GameView extends JPanel {
    private final BoardPanel board = new BoardPanel();
    private final JLabel turn = new JLabel(" ");
    private final JLabel diceLabel = new JLabel("Dice: -");
    private final JButton roll = new JButton("Roll dice");
    private final JButton[] pieceButtons = new JButton[4];
    private final JTextArea log = new JTextArea();
    private final JButton quit = new JButton("Quit to menu");

    public GameView() {
        super(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(board, BorderLayout.CENTER);

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(300, 0));
        turn.setFont(turn.getFont().deriveFont(Font.BOLD, 18f));
        side.add(turn);
        side.add(diceLabel);
        side.add(roll);
        JPanel pieces = new JPanel(new GridLayout(1, 4, 4, 4));
        for (int i = 0; i < 4; i++) {
            pieceButtons[i] = new JButton(String.valueOf(i + 1));
            pieces.add(pieceButtons[i]);
        }
        side.add(new JLabel("Move piece:"));
        side.add(pieces);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        side.add(new JScrollPane(log));
        side.add(quit);
        add(side, BorderLayout.EAST);
    }

    /** Redraws everything from the Model's current state. */
    public void render(Game game, String message) {
        board.setGame(game);
        Player p = game.getCurrentPlayer();
        turn.setText(game.getPhase() == GamePhase.GAME_OVER ? "Game over" : p.getName() + "'s turn");
        turn.setForeground(Colors.of(p.getColor()));
        diceLabel.setText("Dice: " + (game.getLastRoll() == null ? "-" : game.getLastRoll()));
        roll.setEnabled(game.getPhase() == GamePhase.WAITING_FOR_ROLL);
        List<Piece> movable = game.getPhase() == GamePhase.CHOOSING_PIECE ? game.legalMoves() : List.of();
        for (int i = 0; i < 4; i++) pieceButtons[i].setEnabled(movable.contains(p.getPiece(i + 1)));
        if (message != null && !message.isBlank()) log.insert(message + "\n", 0);
        log.setCaretPosition(0);
    }

    public void clearLog() { log.setText(""); }

    /** Shows a question and returns the chosen answer (1-4). */
    public int askQuestion(Question q) {
        Object[] options = q.getAnswers().toArray();
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(this, "<html><body style='width:320px'>" + q.getText() + "</body></html>",
                    "Question (" + q.getTopic() + ")", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        } while (choice < 0);
        return choice + 1;
    }

    public void showMessage(String text) { JOptionPane.showMessageDialog(this, text); }

    public void onRoll(ActionListener l) { roll.addActionListener(l); }
    public void onPiece(IntConsumer l) {
        for (int i = 0; i < 4; i++) {
            int n = i + 1;
            pieceButtons[i].addActionListener(e -> l.accept(n));
        }
    }
    public void onQuit(ActionListener l) { quit.addActionListener(l); }
}
