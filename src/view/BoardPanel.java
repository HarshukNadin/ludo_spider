package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

import model.Board;
import model.Game;
import model.Piece;
import model.PieceState;
import model.Player;
import model.PlayerColor;
import model.Square;

/**
 * Draws the board from the Model. The 52-square track runs around the edge of a 14x14 grid,
 * each color's home path runs from the middle of a side toward the center (its last cell is the Final Home),
 * and castles sit in the corners. Finished pieces stay on the Final Home cell.
 */
public class BoardPanel extends JPanel {
    private static final int GRID = 14;
    private Game game;

    public BoardPanel() { setPreferredSize(new Dimension(620, 620)); }

    public void setGame(Game game) {
        this.game = game;
        repaint();
    }

    /** Grid cell of a track square: clockwise around the edge, starting at the top-left corner. */
    private static Point trackCell(int i) {
        int side = GRID - 1;
        if (i < side) return new Point(i, 0);
        if (i < 2 * side) return new Point(side, i - side);
        if (i < 3 * side) return new Point(side - (i - 2 * side), side);
        return new Point(0, side - (i - 3 * side));
    }

    /** Grid cell of home-path step k (0-5) for a color: from the square before its start, toward the center. */
    private static Point homeCell(PlayerColor c, int k) {
        Point entry = trackCell((c.getStartSquare() + Board.TRACK_LENGTH - 1) % Board.TRACK_LENGTH);
        int dx = Integer.signum(GRID / 2 - entry.x), dy = Integer.signum(GRID / 2 - entry.y);
        if (entry.y == 0 || entry.y == GRID - 1) dx = 0; else dy = 0;
        return new Point(entry.x + dx * (k + 1), entry.y + dy * (k + 1));
    }

    private static Point castleCell(PlayerColor c, int piece) {
        Point corner = switch (c) {
            case RED -> new Point(10, 2);
            case GREEN -> new Point(10, 10);
            case YELLOW -> new Point(2, 10);
            case BLUE -> new Point(2, 2);
        };
        return new Point(corner.x + (piece - 1) % 2, corner.y + (piece - 1) / 2);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (game == null) return;
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cell = Math.min(getWidth(), getHeight()) / GRID;

        for (Square s : game.getBoard().getTrack()) {
            Point p = trackCell(s.getIndex());
            g.setColor(Colors.of(s.getType()));
            g.fillRect(p.x * cell, p.y * cell, cell, cell);
            g.setColor(Color.GRAY);
            g.drawRect(p.x * cell, p.y * cell, cell, cell);
            String mark = switch (s.getType()) { case QUESTION -> "?"; case SURPRISE -> "!"; case PROTECTION -> "◆"; default -> ""; };
            g.setFont(g.getFont().deriveFont(Font.BOLD, cell / 2.5f));
            g.drawString(mark, p.x * cell + cell / 3, p.y * cell + 2 * cell / 3);
        }
        for (Player pl : game.getPlayers()) {
            Color c = Colors.of(pl.getColor());
            Point start = trackCell(pl.getColor().getStartSquare());
            g.setColor(c);
            g.setStroke(new BasicStroke(3));
            g.drawRect(start.x * cell + 2, start.y * cell + 2, cell - 4, cell - 4);
            g.setStroke(new BasicStroke(1));
            for (int k = 0; k < Board.HOME_PATH_LENGTH; k++) {
                Point h = homeCell(pl.getColor(), k);
                g.setColor(c.brighter());
                g.fillRect(h.x * cell, h.y * cell, cell, cell);
                g.setColor(Color.GRAY);
                g.drawRect(h.x * cell, h.y * cell, cell, cell);
            }
        }
        drawPieces(g, cell);
    }

    private void drawPieces(Graphics2D g, int cell) {
        Map<Point, Integer> stack = new HashMap<>();
        List<Piece> movable = game.getPhase() == model.GamePhase.CHOOSING_PIECE ? game.legalMoves() : List.of();
        for (Player pl : game.getPlayers()) {
            for (Piece pc : pl.getPieces()) {
                Point p;
                if (pc.getState() == PieceState.IN_CASTLE) p = castleCell(pl.getColor(), pc.getNumber());
                else if (pc.getState() == PieceState.ON_TRACK) p = trackCell(pc.getTrackIndex());
                else p = homeCell(pl.getColor(), pc.getProgress() - (Board.TRACK_LENGTH - 1));
                int n = stack.merge(p, 1, Integer::sum) - 1;
                int size = cell * 2 / 3, off = (cell - size) / 2 + n * 4;
                g.setColor(Colors.of(pl.getColor()));
                g.fillOval(p.x * cell + off, p.y * cell + off, size, size);
                g.setColor(movable.contains(pc) ? Color.BLACK : Color.DARK_GRAY);
                g.setStroke(new BasicStroke(movable.contains(pc) ? 3 : 1));
                g.drawOval(p.x * cell + off, p.y * cell + off, size, size);
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, size / 2f));
                g.drawString(String.valueOf(pc.getNumber()), p.x * cell + off + size / 3, p.y * cell + off + 2 * size / 3);
            }
        }
        g.setStroke(new BasicStroke(1));
    }
}
