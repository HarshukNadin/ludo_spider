package View;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.PlayerColor;

class SetupViewTest {
    private SetupView view;
    private final List<Component> parts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        view = new SetupView();
        collect(view);
    }

    private void collect(Component c) {
        parts.add(c);
        if (c instanceof Container k) for (Component x : k.getComponents()) collect(x);
    }

    private JTextField nameField(int player) {
        return parts.stream().filter(c -> c instanceof JTextField).map(c -> (JTextField) c).toList().get(player);
    }

    private ColorSwatch swatch(int player, PlayerColor color) {
        return parts.stream().filter(c -> c instanceof ColorSwatch).map(c -> (ColorSwatch) c).toList()
                .get(player * PlayerColor.values().length + color.ordinal());
    }

    @Test
    void counterShowsLengthAndTurnsRedAfterFifteen() {
        nameField(0).setText("Bothyna");
        assertEquals("7/15", view.counters.get(0).getText());
        assertEquals(Theme.MUTED, view.counters.get(0).getForeground());

        nameField(0).setText("123456789012345");
        assertEquals("15/15", view.counters.get(0).getText());
        assertEquals(Theme.MUTED, view.counters.get(0).getForeground(), "15 is still allowed");

        nameField(0).setText("1234567890123456");
        assertEquals("16/15", view.counters.get(0).getText());
        assertEquals(Theme.ERROR, view.counters.get(0).getForeground());
    }

    @Test
    void messageGoesAwayWhenItsFieldChangesAndOtherInputStays() {
        nameField(0).setText("Nadin");
        view.showNameError(1, "Enter a name or nickname.");
        view.showColorError(1, "Pick a color.");
        view.showError("Fix the fields marked in red to start the game.");

        nameField(1).setText("David");
        assertFalse(view.nameErrors.get(1).isVisible());
        assertTrue(view.colorErrors.get(1).isVisible(), "only the changed field loses its message");
        assertEquals("Nadin", view.getName(0), "valid input is kept");

        swatch(1, PlayerColor.GREEN).doClick();
        assertFalse(view.colorErrors.get(1).isVisible());
        assertEquals(PlayerColor.GREEN, view.getColor(1));
    }

    @Test
    void resetClearsMessagesAndAnswers() {
        view.showCountError("Choose how many people will play.");
        view.showDifficultyError("Choose Easy, Medium or Hard.");
        nameField(2).setText("Mahmoud");
        view.reset();
        assertFalse(view.countError.isVisible());
        assertFalse(view.difficultyError.isVisible());
        assertNull(view.getPlayerCount());
        assertNull(view.getDifficulty());
        assertEquals("", view.getName(2));
        assertEquals("0/15", view.counters.get(2).getText());
    }
}
