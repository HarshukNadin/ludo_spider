package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** How to play: the rules in nine short points, for first-time players (CR-13, EI-UI-5). */
public class RulesView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[][] RULES = {
        {"Goal", "Bring all four pieces from your castle to the Final Home in the center. "
                + "Places go by who finishes first, and the game ends when one player is left."},
        {"Getting out", "You need a 6 on the normal die to put a piece on your start square, the square in your color next to your castle. "
                + "If no piece is out and you roll 1 to 5, the turn passes."},
        {"Outer routes", "Medium adds a ring of 64 squares around the board, and Hard a second ring of 72. "
                + "Your start square is the outer corner next to your castle. Walk clockwise; at an arrow in your color, "
                + "step one ring inward and go around it, until the arrow that leads into your home path."},
        {"Moving", "Pick any piece that can move. On Medium you also roll a Boost die (1-3), on Hard an UltraBoost die (1-6): "
                + "pieces on the shared route move normal × boost. Pieces in your home path use the normal die only."},
        {"Rolling a 6", "A 6 gives you another roll. Three 6s in a row cancel the third roll and the turn passes."},
        {"Capturing", "Land on a square with one opponent piece and it goes back to its castle. "
                + "Start, protection (★) and surprise squares are safe."},
        {"Double Block", "Two of your pieces on one square make a wall. Opponents can't land on it or pass it, "
                + "forward or back, so they stop on the square before it."},
        {"Getting home", "You need the exact number to reach the Final Home. If it is too big, move another piece or the turn passes."},
        {"Question squares", "Answer a question about software engineering. Green squares ask easy questions, yellow medium "
                + "and red hard ones. They move you 1, 2 or 3 steps: forward if you are right, back if you are wrong."},
        {"Surprise squares", "Something random happens: a jump to the Final Home, a push for an opponent, back to the castle, "
                + "3 steps back, a lost turn, or an extra turn with the UltraBoost die."},
    };

    private final JButton back = Theme.secondary("Back to menu");

    /** Builds the rules screen. */
    public RulesView() {
        super(new BorderLayout());
        setBackground(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(40, 64, 32, 64));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(Theme.label("How to play", Theme.TITLE, Theme.TEXT), BorderLayout.NORTH);
        head.add(Theme.label("Everything you need for your first game.", Theme.BODY, Theme.MUTED), BorderLayout.SOUTH);
        head.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        add(head, BorderLayout.NORTH);

        JPanel list = new JPanel(new GridBagLayout());
        list.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 14, 24);
        for (int i = 0; i < RULES.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 0;
            list.add(Theme.label(RULES[i][0], Theme.BODY_BOLD, Theme.TEXT), c);
            c.gridx = 1;
            c.weightx = 1;
            list.add(Theme.label("<html><body style='width:560px'>" + RULES[i][1] + "</body></html>", Theme.BODY, Theme.MUTED), c);
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(list, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrap);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        Theme.darkScrollBars(scroll);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    /** @param l runs when Back to menu is clicked */
    public void onBack(ActionListener l) { back.addActionListener(l); }
}
