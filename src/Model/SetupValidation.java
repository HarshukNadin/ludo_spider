package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Checks the new game form before a game starts (FR-1.04, FR-1.05, FR-1.07, SEC-2).
 * Every problem gets its own message that says how to fix it, so the screen can show
 * it next to the right field (EI-UI-4).
 */
public class SetupValidation {
    /** Letters of any language, digits, space, "-" and "_" (CR-15). */
    private static final Pattern NAME_CHARS = Pattern.compile("[\\p{L}\\p{N} _-]+");

    private final String[] nameErrors = new String[Game.MAX_PLAYERS];
    private String playerCountError;
    private String difficultyError;

    /**
     * Checks the form.
     *
     * @param playerCount how many people will play, or null if not chosen yet
     * @param difficulty  the chosen difficulty, or null if not chosen yet
     * @param names       the names typed for each player (only the first playerCount are checked)
     */
    public SetupValidation(Integer playerCount, Difficulty difficulty, List<String> names) {
        if (playerCount == null) playerCountError = "Choose how many people will play.";
        else if (playerCount < Game.MIN_PLAYERS || playerCount > Game.MAX_PLAYERS)
            playerCountError = "A game needs 2 to 4 players.";
        if (difficulty == null) difficultyError = "Choose Easy, Medium or Hard.";
        if (playerCount != null && playerCountError == null) checkNames(names.subList(0, playerCount));
    }

    private void checkNames(List<String> names) {
        List<String> seen = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i) == null ? "" : names.get(i).trim();
            nameErrors[i] = nameError(name, seen);
            seen.add(name.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * The problem with one name, or null if it is fine.
     *
     * @param name  the trimmed name
     * @param taken lower-case names of the players above this one
     */
    static String nameError(String name, List<String> taken) {
        if (name.isEmpty()) return "Enter a name or nickname.";
        if (name.length() > Player.MAX_NAME_LENGTH)
            return "This name has " + name.length() + " characters. Use " + Player.MAX_NAME_LENGTH + " or fewer.";
        if (!NAME_CHARS.matcher(name).matches()) return "Use letters, digits, spaces, - or _ only.";
        if (taken.contains(name.toLowerCase(Locale.ROOT))) return "\"" + name + "\" is already playing. Pick another name.";
        return null;
    }

    /** @return the problem with the player count, or null */
    public String getPlayerCountError() { return playerCountError; }

    /** @return the problem with the difficulty, or null */
    public String getDifficultyError() { return difficultyError; }

    /**
     * @param player 0 to 3
     * @return the problem with that player's name, or null
     */
    public String getNameError(int player) { return nameErrors[player]; }

    /** @return true when the game can start */
    public boolean isValid() {
        return playerCountError == null && difficultyError == null && Arrays.stream(nameErrors).allMatch(e -> e == null);
    }
}
