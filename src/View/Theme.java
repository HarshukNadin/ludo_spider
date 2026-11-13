package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Colors, fonts and buttons shared by all screens, so the whole game looks like one product.
 * The screens are an ink-blue table; the four player colors are the only bright colors.
 */
public final class Theme {
    private Theme() {}

    /** Screen background. */
    public static final Color INK = new Color(0x172038);
    /** Raised panels on the background. */
    public static final Color PANEL = new Color(0x1F2A47);
    /** Borders and separators on dark surfaces. */
    public static final Color LINE = new Color(0x34416A);
    /** Light surface: the board, form fields and the main button. */
    public static final Color PAPER = new Color(0xEEF1F6);
    /** Main text on dark surfaces. */
    public static final Color TEXT = new Color(0xF2F4F8);
    /** Secondary text on dark surfaces. */
    public static final Color MUTED = new Color(0xA3AECB);
    /** Error text on dark surfaces. */
    public static final Color ERROR = new Color(0xFF8A80);
    /** Keyboard focus ring. */
    public static final Color FOCUS = new Color(0x8AB4FF);

    /** SansSerif maps to a clean system font on both Windows and macOS (EI-SW-1). */
    private static final String FAMILY = Font.SANS_SERIF;
    public static final Font TITLE = new Font(FAMILY, Font.BOLD, 34);
    public static final Font HEADING = new Font(FAMILY, Font.BOLD, 20);
    public static final Font BODY = new Font(FAMILY, Font.PLAIN, 15);
    public static final Font BODY_BOLD = new Font(FAMILY, Font.BOLD, 15);
    public static final Font SMALL = new Font(FAMILY, Font.PLAIN, 13);

    /**
     * @param text the label text
     * @param font one of the theme fonts
     * @param color one of the theme colors
     * @return a transparent label
     */
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /**
     * The main action of a screen: light button with dark text.
     *
     * @param text the button text, saying what happens when it is clicked
     * @return the button
     */
    public static JButton primary(String text) {
        return new RoundButton(text, PAPER, INK, PAPER);
    }

    /**
     * Any other action: dark button with a thin border.
     *
     * @param text the button text
     * @return the button
     */
    public static JButton secondary(String text) {
        return new RoundButton(text, PANEL, TEXT, LINE);
    }

    /** A rounded button that paints itself, with hover, pressed, disabled and keyboard focus states. */
    public static class RoundButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color fill;
        private final Color border;

        /**
         * @param text   button text (HTML is allowed)
         * @param fill   background color
         * @param fg     text color
         * @param border border color
         */
        public RoundButton(String text, Color fill, Color fg, Color border) {
            super(text);
            this.fill = fill;
            this.border = border;
            setForeground(fg);
            setFont(BODY_BOLD);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setMargin(new Insets(12, 18, 12, 18));
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = 14;
            Color bg = fill;
            if (!isEnabled()) bg = mix(fill, INK, 0.55f);
            else if (getModel().isPressed()) bg = mix(fill, INK, 0.25f);
            else if (getModel().isRollover()) bg = mix(fill, PAPER, fill == PAPER ? 0f : 0.08f);
            g.setColor(bg);
            g.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
            g.setColor(getModel().isRollover() && isEnabled() ? mix(border, PAPER, 0.3f) : border);
            g.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
            if (isFocusOwner()) {
                g.setColor(FOCUS);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /**
     * Gives a scroll pane a thin dark scroll bar that fits the ink-blue screens,
     * instead of the light default one.
     *
     * @param scroll the scroll pane to style
     */
    public static void darkScrollBars(JScrollPane scroll) {
        for (JScrollBar bar : new JScrollBar[] {scroll.getVerticalScrollBar(), scroll.getHorizontalScrollBar()}) {
            bar.setOpaque(false);
            bar.setUnitIncrement(16);
            bar.setPreferredSize(new Dimension(10, 10));
            bar.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    thumbColor = LINE;
                    trackColor = INK;
                }

                @Override
                protected JButton createDecreaseButton(int orientation) { return noButton(); }

                @Override
                protected JButton createIncreaseButton(int orientation) { return noButton(); }

                private JButton noButton() {
                    JButton b = new JButton();
                    b.setPreferredSize(new Dimension(0, 0));
                    return b;
                }
            });
        }
    }

    /**
     * @param a first color
     * @param b second color
     * @param t 0 gives a, 1 gives b
     * @return the color between them
     */
    public static Color mix(Color a, Color b, float t) {
        return new Color(Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
