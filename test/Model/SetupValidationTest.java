package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class SetupValidationTest {
    private static final List<PlayerColor> FOUR_COLORS = List.of(PlayerColor.values());

    private static SetupValidation check(String... names) {
        return new SetupValidation(names.length, Difficulty.EASY, Arrays.asList(names), FOUR_COLORS);
    }

    @Test
    void validFormPasses() {
        assertTrue(check("Nadin", "David", "Bothyna", "Mahmoud").isValid());
    }

    @Test
    void nameLengthBoundaries() {
        assertNotNull(check("", "David").getNameError(0), "0 characters");
        assertNull(check("N", "David").getNameError(0), "1 character");
        assertNull(check("a".repeat(15), "David").getNameError(0), "15 characters");
        assertEquals("This name has 16 characters. Use 15 or fewer.", check("a".repeat(16), "David").getNameError(0));
    }

    @Test
    void spacesAroundTheNameDontCount() {
        assertNull(check("  Nadin  ", "David").getNameError(0));
        assertNotNull(check("   ", "David").getNameError(0));
    }

    @Test
    void onlyLettersDigitsSpaceDashUnderscore() {
        assertNull(check("נדין", "David_2", "Bo-thyna", "Mah moud").getNameError(0));
        assertTrue(check("נדין", "David_2", "Bo-thyna", "Mah moud").isValid());
        assertEquals("Use letters, digits, spaces, - or _ only.", check("Nadin!", "David").getNameError(0));
        assertNotNull(check("Nadin", "a,b").getNameError(1), "a comma would break the CSV");
    }

    @Test
    void sameNameTwiceIsRejectedIgnoringCase() {
        SetupValidation v = check("Nadin", "nadin");
        assertNull(v.getNameError(0));
        assertNotNull(v.getNameError(1));
        assertFalse(v.isValid());
    }

    @Test
    void colorsMustBeDifferent() {
        SetupValidation v = new SetupValidation(3, Difficulty.EASY, List.of("A", "B", "C"),
                Arrays.asList(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.RED));
        assertNull(v.getColorError(0));
        assertEquals("Player 1 has this color. Pick another one.", v.getColorError(2));
        assertFalse(v.isValid());
    }

    @Test
    void missingChoicesHaveMessages() {
        SetupValidation v = new SetupValidation(null, null, List.of(), List.of());
        assertNotNull(v.getPlayerCountError());
        assertNotNull(v.getDifficultyError());
        assertFalse(v.isValid());
        SetupValidation noColor = new SetupValidation(2, Difficulty.HARD, List.of("A", "B"), Arrays.asList(PlayerColor.RED, null));
        assertEquals("Pick a color.", noColor.getColorError(1));
    }

    @Test
    void unusedRowsAreIgnored() {
        SetupValidation v = new SetupValidation(2, Difficulty.MEDIUM, Arrays.asList("A", "B", "", null),
                Arrays.asList(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED, null));
        assertTrue(v.isValid());
    }
}
