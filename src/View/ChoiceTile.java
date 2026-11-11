package View;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 * One choice in a group, like "3 players" or "Medium". The chosen tile turns light,
 * the others stay dark, so the current answer can be seen at a glance.
 * Put the tiles of one question in a {@link javax.swing.ButtonGroup}.
 */
public class ChoiceTile extends JToggleButton {
    private static final long serialVersionUID = 1L;

    /**
     * @param text tile text (HTML is allowed for a title and a short description)
     */
    public ChoiceTile(String text) {
        super(text);
        setFont(Theme.BODY);
        setForeground(Theme.TEXT);
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.TOP);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addItemListener(e -> setForeground(isSelected() ? Theme.INK : Theme.TEXT));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), arc = 14;
        boolean over = getModel().isRollover();
        g.setColor(isSelected() ? Theme.PAPER : over ? Theme.mix(Theme.PANEL, Theme.PAPER, 0.06f) : Theme.PANEL);
        g.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        g.setColor(isSelected() ? Theme.PAPER : over ? Theme.MUTED : Theme.LINE);
        g.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
        if (isFocusOwner()) {
            g.setColor(Theme.FOCUS);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
