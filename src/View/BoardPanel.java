package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import javax.swing.JPanel;

import Model.Board;
import Model.Game;
import Model.GamePhase;
import Model.Piece;
import Model.PieceState;
import Model.Player;
import Model.PlayerColor;
import Model.Square;

/**
 * Draws the game board from the Model (FR-1.08). It is the usual cross-shaped Ludo board on a
 * 15 x 15 grid: the 52 shared squares go around the cross, each color's home path runs along the
 * middle of its arm to the center, and the castles sit in the four corners.
 */
public class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    /** Cells per side of the grid. */
    static final int N = 15;
    /** The shared track in walking order, starting one square before Red's start square. */
    private static final Point[] PATH = buildPath();

    private static final Color STAR = new Color(0xD9A400);
    private static final Color QUESTION = new Color(0xF0A020);
    private static final Color SURPRISE = new Color(0x8E5BD0);

    private Game game;
    private IntConsumer pieceClick;
    /** Cell size and board origin from the last paint, used to find the square under the mouse. */
    private int cellSize, originX, originY;

    /** Creates an empty board; it draws nothing until {@link #setGame} is called. */
    public BoardPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(620, 620));
        setToolTipText("");
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int n = pieceUnder(e);
                if (n > 0 && pieceClick != null) pieceClick.accept(n);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(pieceUnder(e) > 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    /**
     * @param l gets the piece number (1-4) when the active player clicks one of their glowing pieces
     */
    public void onPieceClick(IntConsumer l) { pieceClick = l; }

    private int pieceUnder(MouseEvent e) {
        if (game == null || cellSize == 0) return 0;
        return movablePieceAt(game, (e.getX() - originX) / (double) cellSize, (e.getY() - originY) / (double) cellSize);
    }

    /**
     * Finds the glowing piece at a point of the board, so it can be picked with the mouse (FR-3.05).
     *
     * @param game  the game on the board
     * @param cellX x in cell units (0 is the left edge of the board)
     * @param cellY y in cell units
     * @return the number of the active player's movable piece there, or 0 if there is none
     */
    static int movablePieceAt(Game game, double cellX, double cellY) {
        if (game.getPhase() != GamePhase.CHOOSING_PIECE) return 0;
        Player p = game.getCurrentPlayer();
        for (Piece piece : game.legalMoves()) {
            Point2D.Double at = place(p, piece);
            if (at.distance(cellX, cellY) <= 0.55) return piece.getNumber();
        }
        return 0;
    }

    /**
     * @param game the game to draw
     */
    public void setGame(Game game) {
        this.game = game;
        repaint();
    }

    private static Point[] buildPath() {
        List<Point> p = new ArrayList<>();
        for (int x = 0; x <= 5; x++) p.add(new Point(x, 6));
        for (int y = 5; y >= 0; y--) p.add(new Point(6, y));
        p.add(new Point(7, 0));
        for (int y = 0; y <= 5; y++) p.add(new Point(8, y));
        for (int x = 9; x <= 14; x++) p.add(new Point(x, 6));
        p.add(new Point(14, 7));
        for (int x = 14; x >= 9; x--) p.add(new Point(x, 8));
        for (int y = 9; y <= 14; y++) p.add(new Point(8, y));
        p.add(new Point(7, 14));
        for (int y = 14; y >= 9; y--) p.add(new Point(6, y));
        for (int x = 5; x >= 0; x--) p.add(new Point(x, 8));
        p.add(new Point(0, 7));
        return p.toArray(new Point[0]);
    }

    /**
     * @param trackIndex 0 to 51, as in {@link Board}
     * @return the grid cell of that shared square
     */
    static Point trackCell(int trackIndex) {
        return PATH[Math.floorMod(trackIndex - PlayerColor.RED.getStartSquare() + 1, Board.TRACK_LENGTH)];
    }

    /**
     * @param color the owner of the home path
     * @param step  0 to 5; step 5 is the Final Home in the center
     * @return the grid cell of that home path step
     */
    static Point homeCell(PlayerColor color, int step) {
        Point arrow = trackCell(Board.arrowSquare(color));
        int dx = Integer.signum(N / 2 - arrow.x), dy = Integer.signum(N / 2 - arrow.y);
        return new Point(arrow.x + dx * (step + 1), arrow.y + dy * (step + 1));
    }

    /** @return the top-left cell of a color's 6 x 6 castle corner */
    static Point castleCorner(PlayerColor color) {
        return switch (color) {
            case RED -> new Point(0, 0);
            case GREEN -> new Point(9, 0);
            case YELLOW -> new Point(9, 9);
            case BLUE -> new Point(0, 9);
        };
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (game == null) return;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int c = (Math.min(getWidth(), getHeight()) - 24) / N, s = c * N;
        int x0 = (getWidth() - s) / 2, y0 = (getHeight() - s) / 2;
        cellSize = c;
        originX = x0;
        originY = y0;
        g.translate(x0, y0);

        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(4, 8, s, s, 24, 24);
        g.setColor(Theme.PAPER);
        g.fillRoundRect(-8, -8, s + 16, s + 16, 24, 24);

        for (Square sq : game.getBoard().getTrack()) square(g, sq, trackCell(sq.getIndex()), c);
        for (PlayerColor pc : PlayerColor.values()) {
            for (int k = 0; k < Board.HOME_PATH_LENGTH - 1; k++)
                cell(g, homeCell(pc, k), c, Theme.mix(Colors.of(pc), Color.WHITE, 0.55f));
            castle(g, pc, c);
        }
        center(g, c);
        drawPieces(g, c);
        g.dispose();
    }

    private void square(Graphics2D g, Square sq, Point p, int c) {
        Color fill = Color.WHITE;
        for (PlayerColor pc : PlayerColor.values())
            if (pc.getStartSquare() == sq.getIndex()) fill = Theme.mix(Colors.of(pc), Color.WHITE, 0.35f);
        cell(g, p, c, fill);
        int x = p.x * c, y = p.y * c;
        switch (sq.getType()) {
            case PROTECTION -> star(g, x + c / 2, y + c / 2, c * 0.36);
            case QUESTION -> badge(g, x, y, c, QUESTION, "?");
            case SURPRISE -> gift(g, x, y, c);
            case ARROW -> arrow(g, x, y, c, arrowOwner(sq.getIndex()));
            default -> { }
        }
    }

    private static PlayerColor arrowOwner(int trackIndex) {
        for (PlayerColor pc : PlayerColor.values()) if (Board.arrowSquare(pc) == trackIndex) return pc;
        return null;
    }

    private static void star(Graphics2D g, int cx, int cy, double r) {
        Path2D.Double p = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double a = Math.PI / 2 + i * Math.PI / 5, rr = i % 2 == 0 ? r : r * 0.45;
            double px = cx + rr * Math.cos(a), py = cy - rr * Math.sin(a);
            if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
        }
        p.closePath();
        g.setColor(STAR);
        g.fill(p);
    }

    private static void badge(Graphics2D g, int x, int y, int c, Color col, String text) {
        int m = c / 5, w = c - 2 * m;
        g.setColor(col);
        g.fillRoundRect(x + m, y + m, w, w, c / 4, c / 4);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, w * 3 / 4)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x + (c - fm.stringWidth(text)) / 2, y + (c + fm.getAscent()) / 2 - 2);
    }

    private static void gift(Graphics2D g, int x, int y, int c) {
        int m = c / 4, w = c - 2 * m, lid = w / 3;
        g.setColor(SURPRISE);
        g.fillRect(x + m, y + m + lid / 2, w, w - lid / 2);
        g.setColor(Theme.mix(SURPRISE, Color.WHITE, 0.25f));
        g.fillRect(x + m - 2, y + m, w + 4, lid);
        g.setColor(Color.WHITE);
        g.fillRect(x + c / 2 - 1, y + m, 3, w);
    }

    private static void arrow(Graphics2D g, int x, int y, int c, PlayerColor owner) {
        Point from = trackCell(Board.arrowSquare(owner)), to = homeCell(owner, 0);
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        Graphics2D a = (Graphics2D) g.create();
        a.translate(x + c / 2.0, y + c / 2.0);
        a.rotate(angle);
        a.setColor(Colors.of(owner));
        a.setStroke(new BasicStroke(Math.max(2f, c / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int r = c * 3 / 10;
        a.drawLine(-r, 0, r, 0);
        a.drawLine(r, 0, r - r * 2 / 3, -r * 2 / 3);
        a.drawLine(r, 0, r - r * 2 / 3, r * 2 / 3);
        a.dispose();
    }

    /** Says what the square under the mouse does, so players can learn the marks while playing. */
    @Override
    public String getToolTipText(MouseEvent e) {
        if (game == null || cellSize == 0) return null;
        Point p = new Point(Math.floorDiv(e.getX() - originX, cellSize), Math.floorDiv(e.getY() - originY, cellSize));
        for (Square sq : game.getBoard().getTrack()) {
            if (!trackCell(sq.getIndex()).equals(p)) continue;
            for (PlayerColor pc : PlayerColor.values())
                if (pc.getStartSquare() == sq.getIndex()) return name(pc) + " start square. Pieces here are safe.";
            return switch (sq.getType()) {
                case PROTECTION -> "Protection square. Pieces here cannot be captured.";
                case QUESTION -> "Question square. Answer right to move forward, wrong to move back.";
                case SURPRISE -> "Surprise square. Something random happens.";
                case ARROW -> name(arrowOwner(sq.getIndex())) + " turns into its home path after this square.";
                default -> null;
            };
        }
        return null;
    }

    private static String name(PlayerColor pc) {
        return pc.name().charAt(0) + pc.name().substring(1).toLowerCase();
    }

    private static void cell(Graphics2D g, Point p, int c, Color fill) {
        g.setColor(fill);
        g.fillRect(p.x * c, p.y * c, c, c);
        g.setColor(Theme.mix(Theme.PAPER, Theme.INK, 0.22f));
        g.drawRect(p.x * c, p.y * c, c, c);
    }

    private void castle(Graphics2D g, PlayerColor pc, int c) {
        Point o = castleCorner(pc);
        int x = o.x * c, y = o.y * c;
        if (game.getPhase() != GamePhase.GAME_OVER && game.getCurrentPlayer().getColor() == pc) {
            // The active player's castle gets a frame, so it is clear whose turn it is (EI-UI-2).
            g.setColor(Theme.mix(Colors.of(pc), Theme.INK, 0.25f));
            g.setStroke(new BasicStroke(Math.max(3f, c / 9f)));
            g.drawRoundRect(x + c / 10, y + c / 10, 6 * c - c / 5, 6 * c - c / 5, c + c / 4, c + c / 4);
            g.setStroke(new BasicStroke(1f));
        }
        g.setColor(Colors.of(pc));
        g.fillRoundRect(x + c / 4, y + c / 4, 6 * c - c / 2, 6 * c - c / 2, c, c);
        g.setColor(Color.WHITE);
        g.fillRoundRect(x + c, y + c, 4 * c, 4 * c, c, c);
        g.setColor(Theme.mix(Colors.of(pc), Color.WHITE, 0.6f));
        g.setStroke(new BasicStroke(2f));
        for (int n = 1; n <= 4; n++) {
            Point2D.Double spot = castleSpot(pc, n);
            int d = c * 5 / 4;
            g.drawOval((int) (spot.x * c) - d / 2, (int) (spot.y * c) - d / 2, d, d);
        }
        g.setStroke(new BasicStroke(1f));
    }

    /** Center of the waiting spot of piece n (1-4) inside its castle, in cell units. */
    private static Point2D.Double castleSpot(PlayerColor pc, int n) {
        Point o = castleCorner(pc);
        double near = 2.25, far = 3.75;
        return new Point2D.Double(o.x + ((n - 1) % 2 == 0 ? near : far), o.y + ((n - 1) / 2 == 0 ? near : far));
    }

    private static void center(Graphics2D g, int c) {
        int x = 6 * c, y = 6 * c, m = 3 * c, mx = x + m / 2, my = y + m / 2;
        for (PlayerColor pc : PlayerColor.values()) {
            Point h = homeCell(pc, 0);
            int[] xs, ys;
            if (h.x < 6) { xs = new int[] {x, x, mx}; ys = new int[] {y, y + m, my}; }
            else if (h.x > 8) { xs = new int[] {x + m, x + m, mx}; ys = new int[] {y, y + m, my}; }
            else if (h.y < 6) { xs = new int[] {x, x + m, mx}; ys = new int[] {y, y, my}; }
            else { xs = new int[] {x, x + m, mx}; ys = new int[] {y + m, y + m, my}; }
            g.setColor(Colors.of(pc));
            g.fillPolygon(new Polygon(xs, ys, 3));
        }
    }

    /** Where a piece is drawn, in cell units (the middle of its cell or castle spot). */
    private static Point2D.Double place(Player owner, Piece piece) {
        PlayerColor pc = owner.getColor();
        if (piece.getState() == PieceState.IN_CASTLE) return castleSpot(pc, piece.getNumber());
        Point p = piece.getState() == PieceState.ON_TRACK ? trackCell(piece.getTrackIndex())
                : homeCell(pc, piece.getProgress() - (Board.TRACK_LENGTH - 1));
        return new Point2D.Double(p.x + 0.5, p.y + 0.5);
    }

    private void drawPieces(Graphics2D g, int c) {
        Map<Point2D.Double, List<Color>> cells = new HashMap<>();
        List<Piece> movable = game.getPhase() == GamePhase.CHOOSING_PIECE ? game.legalMoves() : List.of();
        for (Player pl : game.getPlayers()) {
            for (Piece pc : pl.getPieces()) {
                Point2D.Double at = place(pl, pc);
                List<Color> here = cells.computeIfAbsent(at, k -> new ArrayList<>());
                here.add(Colors.of(pl.getColor()));
                int shift = (here.size() - 1) * c / 6;
                piece(g, (int) (at.x * c) + shift, (int) (at.y * c) - shift, c, Colors.of(pl.getColor()),
                        pc.getNumber(), movable.contains(pc));
            }
        }
    }

    private static void piece(Graphics2D g, int cx, int cy, int c, Color col, int number, boolean movable) {
        int d = c * 4 / 5, x = cx - d / 2, y = cy - d / 2;
        if (movable) {
            g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 90));
            g.fillOval(x - 6, y - 6, d + 12, d + 12);
        }
        g.setColor(Theme.mix(col, Theme.INK, 0.4f));
        g.fillOval(x + 1, y + 2, d, d);
        g.setColor(col);
        g.fillOval(x, y, d, d);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(movable ? 2.5f : 1.5f));
        g.drawOval(x + 1, y + 1, d - 2, d - 2);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, d / 2)));
        FontMetrics fm = g.getFontMetrics();
        String t = String.valueOf(number);
        g.drawString(t, cx - fm.stringWidth(t) / 2, cy + fm.getAscent() / 2 - 2);
    }
}
