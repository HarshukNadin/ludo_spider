package Model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void newPlayerHasFourPiecesInTheCastle() {
        Player p = new Player("Mahmoud", PlayerColor.BLUE);
        assertEquals(Player.PIECES, p.getPieces().size());
        for (int i = 1; i <= 4; i++) {
            Piece piece = p.getPiece(i);
            assertEquals(i, piece.getNumber());
            assertSame(p, piece.getOwner());
            assertEquals(PieceState.IN_CASTLE, piece.getState());
            assertEquals(-1, piece.getTrackIndex());
        }
        assertFalse(p.hasFinished());
    }

    @Test
    void nameIsTrimmedAndLimitedTo15() {
        assertEquals("Mahmoud", new Player("  Mahmoud ", PlayerColor.RED).getName());
        assertDoesNotThrow(() -> new Player("a".repeat(15), PlayerColor.RED));
        assertThrows(IllegalArgumentException.class, () -> new Player("a".repeat(16), PlayerColor.RED));
        assertThrows(IllegalArgumentException.class, () -> new Player("   ", PlayerColor.RED));
    }

    @Test
    void piecesListCannotBeChangedFromOutside() {
        Player p = new Player("Nadin", PlayerColor.GREEN);
        assertThrows(UnsupportedOperationException.class, () -> p.getPieces().clear());
    }

    @Test
    void pieceStateFollowsProgress() {
        Piece piece = new Player("David", PlayerColor.YELLOW).getPiece(1);
        piece.setProgress(0);
        assertEquals(PieceState.ON_TRACK, piece.getState());
        assertEquals(PlayerColor.YELLOW.getStartSquare(), piece.getTrackIndex());
        piece.setProgress(Board.TRACK_LENGTH - 1);
        assertEquals(PieceState.IN_HOME_PATH, piece.getState());
        piece.setProgress(Board.FINAL_INDEX);
        assertEquals(PieceState.FINISHED, piece.getState());
        piece.sendToCastle();
        assertEquals(PieceState.IN_CASTLE, piece.getState());
    }
}
