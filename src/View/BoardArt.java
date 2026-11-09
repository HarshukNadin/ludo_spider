package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import javax.swing.JComponent;

import Model.PlayerColor;

/**
 * A picture of a Ludo board for the main menu: four castles with their pieces,
 * the cross-shaped track and the colored home paths meeting in the center.
 * It is only a picture, the real board is drawn by {@link BoardPanel}.
 */
public class BoardArt extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int N = 15;

    /** Creates the picture with a square preferred size. */
    public BoardArt() {
        setPreferredSize(new Dimension(420, 420));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(getWidth(), getHeight()) - 40;
        int c = size / N, s = c * N;
        int x0 = (getWidth() - s) / 2, y0 = (getHeight() - s) / 2;

        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(x0 + 6, y0 + 10, s, s, 28, 28);
        g.setColor(Theme.PAPER);
        g.fillRoundRect(x0 - 8, y0 - 8, s + 16, s + 16, 28, 28);

        g.setColor(Theme.mix(Theme.PAPER, Theme.INK, 0.18f));
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if ((i >= 6 && i <= 8) || (j >= 6 && j <= 8)) g.drawRect(x0 + i * c, y0 + j * c, c, c);

        castle(g, PlayerColor.RED, x0, y0, c);
        castle(g, PlayerColor.GREEN, x0 + 9 * c, y0, c);
        castle(g, PlayerColor.YELLOW, x0 + 9 * c, y0 + 9 * c, c);
        castle(g, PlayerColor.BLUE, x0, y0 + 9 * c, c);

        for (int k = 1; k <= 5; k++) {
            cell(g, Colors.of(PlayerColor.RED), x0 + k * c, y0 + 7 * c, c);
            cell(g, Colors.of(PlayerColor.GREEN), x0 + 7 * c, y0 + k * c, c);
            cell(g, Colors.of(PlayerColor.YELLOW), x0 + (14 - k) * c, y0 + 7 * c, c);
            cell(g, Colors.of(PlayerColor.BLUE), x0 + 7 * c, y0 + (14 - k) * c, c);
        }
        int cx = x0 + 6 * c, cy = y0 + 6 * c, m = 3 * c;
        triangle(g, Colors.of(PlayerColor.RED), cx, cy, cx, cy + m, cx + m / 2, cy + m / 2);
        triangle(g, Colors.of(PlayerColor.GREEN), cx, cy, cx + m, cy, cx + m / 2, cy + m / 2);
        triangle(g, Colors.of(PlayerColor.YELLOW), cx + m, cy, cx + m, cy + m, cx + m / 2, cy + m / 2);
        triangle(g, Colors.of(PlayerColor.BLUE), cx, cy + m, cx + m, cy + m, cx + m / 2, cy + m / 2);
        g.dispose();
    }

    private static void castle(Graphics2D g, PlayerColor color, int x, int y, int c) {
        Color col = Colors.of(color);
        g.setColor(col);
        g.fillRoundRect(x + c / 4, y + c / 4, 6 * c - c / 2, 6 * c - c / 2, c, c);
        g.setColor(Theme.PAPER);
        g.fillRoundRect(x + c + c / 4, y + c + c / 4, 4 * c - c / 2, 4 * c - c / 2, c, c);
        int d = 6 * c / 5, near = 9 * c / 4, far = 15 * c / 4;
        int[][] centers = {{near, near}, {far, near}, {near, far}, {far, far}};
        for (int[] p : centers) {
            int px = x + p[0] - d / 2, py = y + p[1] - d / 2;
            g.setColor(Theme.mix(col, Theme.INK, 0.35f));
            g.fillOval(px + 1, py + 3, d, d);
            g.setColor(col);
            g.fillOval(px, py, d, d);
            g.setColor(Theme.mix(col, Color.WHITE, 0.45f));
            g.fillOval(px + d / 4, py + d / 5, d / 3, d / 4);
        }
    }

    private static void cell(Graphics2D g, Color col, int x, int y, int c) {
        g.setColor(col);
        g.fillRect(x + 1, y + 1, c - 1, c - 1);
    }

    private static void triangle(Graphics2D g, Color col, int... xy) {
        g.setColor(col);
        g.fillPolygon(new Polygon(new int[] {xy[0], xy[2], xy[4]}, new int[] {xy[1], xy[3], xy[5]}, 3));
    }
}
