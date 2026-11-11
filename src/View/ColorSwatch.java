package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;
import javax.swing.JToggleButton;

import Model.PlayerColor;

/**
 * A round button in a player color. When another player already has the color the swatch
 * is disabled, faded and crossed out, and its tooltip says who has it (FR-1.05).
 */
public class ColorSwatch extends JToggleButton {
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 34;

    private final PlayerColor color;

    /**
     * @param color the player color this swatch stands for
     */
    public ColorSwatch(PlayerColor color) {
        this.color = color;
        String name = color.name().charAt(0) + color.name().substring(1).toLowerCase(Locale.ROOT);
        getAccessibleContext().setAccessibleName(name);
        setToolTipText(name);
        setPreferredSize(new Dimension(SIZE + 6, SIZE + 6));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** @return the player color of this swatch */
    public PlayerColor getColor() { return color; }

    /**
     * Marks the swatch as taken by another player, or frees it again.
     *
     * @param owner the player number that has this color, or 0 if nobody has it
     */
    public void setTakenBy(int owner) {
        setEnabled(owner == 0);
        String name = getAccessibleContext().getAccessibleName();
        setToolTipText(owner == 0 ? name : name + " is taken by Player " + owner);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (getWidth() - SIZE) / 2, y = (getHeight() - SIZE) / 2;
        Color fill = Colors.of(color);
        if (!isEnabled()) fill = Theme.mix(fill, Theme.INK, 0.65f);
        g.setColor(fill);
        g.fillOval(x, y, SIZE, SIZE);
        g.setStroke(new BasicStroke(2.5f));
        if (isSelected()) {
            g.setColor(Theme.PAPER);
            g.drawOval(x - 2, y - 2, SIZE + 4, SIZE + 4);
            g.drawLine(x + 10, y + 18, x + 15, y + 23);
            g.drawLine(x + 15, y + 23, x + 25, y + 12);
        } else if (!isEnabled()) {
            g.setColor(Theme.MUTED);
            g.drawLine(x + 9, y + 9, x + SIZE - 9, y + SIZE - 9);
        } else if (getModel().isRollover()) {
            g.setColor(Theme.MUTED);
            g.drawOval(x - 2, y - 2, SIZE + 4, SIZE + 4);
        }
        if (isFocusOwner()) {
            g.setColor(Theme.FOCUS);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(x - 3, y - 3, SIZE + 6, SIZE + 6);
        }
        g.dispose();
    }
}
