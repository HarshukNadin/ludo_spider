package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The Easy board: the standard 52-square track plus a 6-square home path per color (FR-2.02).
 * Each color walks its own path of shared squares, from its Start square to its Arrow square,
 * and then the home path. Special squares are placed at random according to the difficulty.
 * {@link OuterBoard} adds the outer routes of Medium and Hard.
 */
public class Board {
    public static final int TRACK_LENGTH = 52;
    public static final int HOME_PATH_LENGTH = 6;
    /** Final Home on the Easy board: 51 track squares (0..50), then 6 home squares. */
    public static final int FINAL_INDEX = TRACK_LENGTH - 2 + HOME_PATH_LENGTH; // 56

    private final Difficulty difficulty;
    private final List<Route> routes = new ArrayList<>();
    private final Map<PlayerColor, List<Square>> paths = new EnumMap<>(PlayerColor.class);

    /**
     * Builds the 52-square track with the fixed Start and Arrow squares and the random special squares.
     *
     * @param difficulty decides how many special squares there are
     * @param random     used to place them
     */
    public Board(Difficulty difficulty, Random random) {
        this(difficulty, random, 0);
    }

    /**
     * Builds the standard track and the given number of outer routes.
     * {@link #buildPath} is called from here, so subclasses must not use their own fields in it.
     *
     * @param difficulty  decides how many special squares there are
     * @param random      used to place them
     * @param outerRoutes 0 for Easy, 1 for Medium, 2 for Hard
     */
    protected Board(Difficulty difficulty, Random random, int outerRoutes) {
        this.difficulty = difficulty;
        for (int r = Route.STANDARD; r <= outerRoutes; r++) routes.add(new Route(r));
        for (PlayerColor c : PlayerColor.values()) paths.put(c, Collections.unmodifiableList(buildPath(c)));
        markFixedSquares();
        placeSpecialSquares(random);
    }

    /**
     * The shared squares a color walks, in order. On the Easy board it starts on the color's
     * Start square and goes once around the track to the color's Arrow square.
     *
     * @param color the color
     * @return the squares from the Start square to the last Arrow square
     */
    protected List<Square> buildPath(PlayerColor color) {
        List<Square> path = new ArrayList<>();
        for (int k = 0; k < TRACK_LENGTH - 1; k++) path.add(getRoute(Route.STANDARD).getSquare(color.getStartSquare() + k));
        return path;
    }

    /**
     * The first square of each path is a Start square (FR-2.06). A square where a path turns to
     * another route or to the home path is an Arrow square of that color (FR-3.05).
     */
    private void markFixedSquares() {
        for (List<Square> path : paths.values()) {
            path.get(0).setType(SquareType.START);
            for (int i = 0; i < path.size(); i++) {
                boolean last = i == path.size() - 1;
                if (last || path.get(i + 1).getRoute() != path.get(i).getRoute()) path.get(i).setType(SquareType.ARROW);
            }
        }
    }

    /** Places the Protection, Question and Surprise squares on free squares of all shared routes (FR-2.05). */
    private void placeSpecialSquares(Random random) {
        List<Square> free = new ArrayList<>();
        for (Route r : routes) for (Square s : r.getSquares()) if (s.getType() == SquareType.NORMAL) free.add(s);
        Collections.shuffle(free, random);
        int i = 0;
        for (int k = 0; k < difficulty.getProtectionSquares(); k++) free.get(i++).setType(SquareType.PROTECTION);
        for (int k = 0; k < difficulty.getQuestionSquares(); k++) free.get(i++).setType(SquareType.QUESTION);
        for (int k = 0; k < difficulty.getSurpriseSquares(); k++) free.get(i++).setType(SquareType.SURPRISE);
    }

    /** @return the difficulty the board was built for */
    public Difficulty getDifficulty() { return difficulty; }
    /** @return the standard track first, then the outer routes, read only */
    public List<Route> getRoutes() { return Collections.unmodifiableList(routes); }
    /**
     * @param number {@link Route#STANDARD}, {@link Route#RING_64} or {@link Route#RING_72}
     * @return that route
     * @throws IndexOutOfBoundsException if the board does not have it
     */
    public Route getRoute(int number) { return routes.get(number); }
    /** @return the 52 squares of the standard track in order, read only */
    public List<Square> getTrack() { return getRoute(Route.STANDARD).getSquares(); }
    /**
     * @param trackIndex 0 to 51
     * @return the square at that place on the standard track
     */
    public Square getSquare(int trackIndex) { return getRoute(Route.STANDARD).getSquare(trackIndex); }

    /**
     * @param color a player color
     * @return the shared squares that color walks, from its Start square to its last Arrow square
     */
    public List<Square> getPath(PlayerColor color) { return paths.get(color); }

    /** @return the progress where the home path starts; the same for every color */
    public int getHomeStart() { return paths.get(PlayerColor.RED).size(); }

    /** @return the progress of the Final Home square */
    public int getFinalIndex() { return getHomeStart() + HOME_PATH_LENGTH - 1; }

    /**
     * @param color    the piece's color
     * @param progress steps from the Start square
     * @return the shared square at that progress, or null in the castle, the home path or Final Home
     */
    public Square squareAt(PlayerColor color, int progress) {
        List<Square> path = paths.get(color);
        return progress >= 0 && progress < path.size() ? path.get(progress) : null;
    }

    /** Fixed Arrow square of a color on the standard track: the last shared square before its home path (FR-2.06). */
    public static int arrowSquare(PlayerColor color) {
        return toTrackIndex(color, TRACK_LENGTH - 2);
    }

    /**
     * Converts a piece's progress to a standard-track index, for the Easy board.
     *
     * @return the track index, or -1 if the piece is not on the track
     */
    public static int toTrackIndex(PlayerColor color, int progress) {
        if (progress < 0 || progress >= TRACK_LENGTH - 1) return -1;
        return (color.getStartSquare() + progress) % TRACK_LENGTH;
    }

    /** Count of squares of the given type on all routes, used by tests and the view. */
    public long count(SquareType type) {
        return routes.stream().flatMap(r -> r.getSquares().stream()).filter(s -> s.getType() == type).count();
    }
}
