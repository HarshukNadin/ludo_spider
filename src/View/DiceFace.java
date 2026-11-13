package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * One die on the game screen. The Normal die is white with dots; the Boost and UltraBoost die
 * is dark and shows its multiplier, like "x2", so the two dice cannot be mixed up.
 */
public class DiceFace extends JComponent {
    private static final long serialVersionUID = 1L;
    /** Dot positions on a 3 x 3 grid for each value, 1 to 6. */
    private static final int[][][] DOTS = {
        {},
        {{1, 1}},
        {{0, 0}, {2, 2}},
        {{0, 0}, {1, 1}, {2, 2}},
        {{0, 0}, {2, 0}, {0, 2}, {2, 2}},
        {{0, 0}, {2, 0}, {1, 1}, {0, 2}, {2, 2}},
        {{0, 0}, {2, 0}, {0, 1}, {2, 1}, {0, 2}, {2, 2}},
    };

    private final boolean multiplier;
    private int value;

    /**
     * @param multiplier false for the Normal die, true for the Boost or UltraBoost die
     */
    public DiceFace(boolean multiplier) {
        this.multiplier = multiplier;
        setPreferredSize(new Dimension(56, 56));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
    }

    /**
     * @param value the rolled value, or 0 before the roll (shows "?")
     */
    public void setValue(int value) {
        this.value = value;
        setToolTipText(value == 0 ? "Not rolled yet" : multiplier ? "Boost die: times " + value : "Normal die: " + value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int s = Math.min(getWidth(), getHeight()) - 4, arc = s / 4;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillRoundRect(2, 4, s, s, arc, arc);
        g.setColor(multiplier ? Theme.INK : Color.WHITE);
        g.fillRoundRect(0, 0, s, s, arc, arc);
        Color ink = multiplier ? Color.WHITE : Theme.INK;
        if (value == 0 || multiplier) {
            String t = value == 0 ? "?" : "×" + value;
            g.setColor(value == 0 ? Theme.mix(ink, multiplier ? Theme.INK : Color.WHITE, 0.6f) : ink);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, s * 2 / 5));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(t, (s - fm.stringWidth(t)) / 2, (s + fm.getAscent()) / 2 - fm.getDescent() / 2);
        } else {
            int d = s / 6, step = s / 4;
            g.setColor(ink);
            for (int[] p : DOTS[value]) g.fillOval(step * (p[0] + 1) - d / 2, step * (p[1] + 1) - d / 2, d, d);
        }
        g.dispose();
    }
}
