package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The first screen (FR-1.01, EI-UI-1): New game, Game history, Question management and Exit,
 * plus How to play for first-time players. A picture of the board fills the right side.
 */
public class MainMenuView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JButton newGame = option("New game", "2 to 4 players on one computer", KeyEvent.VK_N, true);
    private final JButton history = option("Game history", "Winners, difficulty and time of past games", KeyEvent.VK_H, false);
    private final JButton rules = option("How to play", "The rules in nine short points", KeyEvent.VK_P, false);
    private final JButton questions = option("Question management", "For administrators, comes in a later version", KeyEvent.VK_Q, false);
    private final JButton exit = option("Exit", "Close Spider Ludo", KeyEvent.VK_E, false);

    /** Builds the menu. */
    public MainMenuView() {
        super(new BorderLayout());
        setBackground(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(48, 64, 48, 64));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(Theme.label("Spider Ludo", Theme.TITLE, Theme.TEXT));
        left.add(Box.createVerticalStrut(10));
        left.add(Theme.label("<html><body style='width:330px'>Roll the dice, answer the question, get all four pieces home first. "
                + "The questions are about Agile, MVC, design patterns and testing.</body></html>", Theme.BODY, Theme.MUTED));
        left.add(Box.createVerticalStrut(32));
        for (JButton b : new JButton[] {newGame, history, rules, questions, exit}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(400, 64));
            left.add(b);
            left.add(Box.createVerticalStrut(10));
        }

        JPanel west = new JPanel(new GridBagLayout());
        west.setOpaque(false);
        west.add(left);
        add(west, BorderLayout.WEST);

        JPanel art = new JPanel(new GridBagLayout());
        art.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1;
        c.insets = new Insets(0, 48, 0, 0);
        art.add(new BoardArt(), c);
        add(art, BorderLayout.CENTER);
    }

    private static JButton option(String title, String hint, int key, boolean main) {
        String hintColor = main ? "#4A5578" : "#A3AECB";
        String html = "<html><div style='font-size:14pt'><b>" + title + "</b></div>"
                + "<div style='font-size:10pt; color:" + hintColor + "'>" + hint + "</div></html>";
        JButton b = main ? Theme.primary(html) : Theme.secondary(html);
        b.setFont(Theme.BODY);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMnemonic(key);
        b.getAccessibleContext().setAccessibleName(title);
        return b;
    }

    /** @param l runs when New game is chosen */
    public void onNewGame(ActionListener l) { newGame.addActionListener(l); }

    /** @param l runs when Game history is chosen */
    public void onHistory(ActionListener l) { history.addActionListener(l); }

    /** @param l runs when How to play is chosen */
    public void onRules(ActionListener l) { rules.addActionListener(l); }

    /** @param l runs when Question management is chosen */
    public void onQuestions(ActionListener l) { questions.addActionListener(l); }

    /** @param l runs when Exit is chosen */
    public void onExit(ActionListener l) { exit.addActionListener(l); }

    /** Puts the keyboard focus on New game, so Enter starts a game. */
    public void focusFirst() { newGame.requestFocusInWindow(); }
}
