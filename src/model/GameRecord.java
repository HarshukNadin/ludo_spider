package model;

import java.util.List;

/** One finished game, as one row of the course game-history CSV. */
public record GameRecord(int gameId, Difficulty difficulty, String timestamp, List<String> places, String duration) {
    public GameRecord {
        places = List.copyOf(places);
    }

    /** Name in the given place (1-4), or an empty string. */
    public String place(int n) { return n <= places.size() ? places.get(n - 1) : ""; }
}
