package View;

import java.awt.Color;

import Model.PlayerColor;
import Model.SquareType;

/** Screen colors for players and squares. */
final class Colors {
    private Colors() {}

    static Color of(PlayerColor c) {
        return switch (c) {
            case RED -> new Color(214, 48, 49);
            case GREEN -> new Color(39, 155, 74);
            case YELLOW -> new Color(230, 180, 20);
            case BLUE -> new Color(40, 100, 210);
        };
    }

    static Color of(SquareType t) {
        return switch (t) {
            case NORMAL -> Color.WHITE;
            case START -> new Color(220, 220, 220);
            case ARROW -> new Color(235, 235, 235);
            case PROTECTION -> new Color(170, 210, 240);
            case QUESTION -> new Color(250, 225, 140);
            case SURPRISE -> new Color(215, 175, 240);
        };
    }
}
