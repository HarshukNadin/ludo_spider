package View;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** The single application window. Screens are swapped with a CardLayout. */
public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public MainFrame() {
        super("Spider Ludo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        root.setBackground(Theme.INK);
        setContentPane(root);
        setMinimumSize(new Dimension(1000, 720));
        setLocationRelativeTo(null);
    }

    public void addScreen(String name, JPanel screen) { root.add(screen, name); }

    public void showScreen(String name) {
        cards.show(root, name);
        root.revalidate();
        root.repaint();
    }
}
