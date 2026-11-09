package view;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Main menu: new game, game history, question management, exit. */
public class MainMenuView extends JPanel {
    private final JButton newGame = new JButton("New game");
    private final JButton history = new JButton("Game history");
    private final JButton questions = new JButton("Manage questions");
    private final JButton exit = new JButton("Exit");

    public MainMenuView() {
        super(new GridBagLayout());
        JPanel box = new JPanel(new GridLayout(0, 1, 0, 12));
        JLabel title = new JLabel("Spider Ludo", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        box.add(title);
        box.add(new JLabel("Race home and answer questions about Agile, MVC, patterns and QA", SwingConstants.CENTER));
        box.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        box.add(newGame);
        box.add(history);
        questions.setToolTipText("Coming in a later step");
        questions.setEnabled(false);
        box.add(questions);
        box.add(exit);
        add(box);
    }

    public void onNewGame(ActionListener l) { newGame.addActionListener(l); }
    public void onHistory(ActionListener l) { history.addActionListener(l); }
    public void onQuestions(ActionListener l) { questions.addActionListener(l); }
    public void onExit(ActionListener l) { exit.addActionListener(l); }
}
