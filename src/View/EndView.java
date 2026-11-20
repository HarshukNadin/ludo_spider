package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.Game;
import Model.GameHistory;
import Model.GameRecord;
import Model.Player;

/**
 * The end of a game (FR-5.03): the final ranking, the level and the game time,
 * and whether the result was saved to the history (FR-5.07).
 */
public class EndView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel winner = Theme.label(" ", Theme.TITLE, Theme.TEXT);
    private final JLabel details = Theme.label(" ", Theme.BODY, Theme.MUTED);
    private final JPanel ranking = new JPanel();
    private final JLabel saved = Theme.label(" ", Theme.SMALL, Theme.MUTED);
    private final JButton playAgain = Theme.primary("Play again");
    private final JButton history = Theme.secondary("Game history");
    private final JButton menu = Theme.secondary("Main menu");
    /** Text of the ranking rows, kept for tests. */
    private final List<String> rows = new ArrayList<>();

    /** Builds the empty screen; {@link #show} fills it in. */
    public EndView() {
        super(new GridBagLayout());
        setBackground(Theme.INK);

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        ranking.setOpaque(false);
        ranking.setLayout(new BoxLayout(ranking, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        buttons.add(playAgain);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(history);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(menu);

        for (JComponent c : new JComponent[] {Theme.label("Game over", Theme.HEADING, Theme.MUTED), winner, (JComponent) Box.createVerticalStrut(6), details,
                (JComponent) Box.createVerticalStrut(24), ranking, (JComponent) Box.createVerticalStrut(16), saved,
                (JComponent) Box.createVerticalStrut(28), buttons}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
            card.add(c);
        }
        add(card);
    }

    /**
     * Fills the screen for a finished game.
     *
     * @param game   the finished game
     * @param record the saved history record, or null if it was not saved
     * @param error  why saving failed, or null
     */
    public void show(Game game, GameRecord record, String error) {
        List<Player> order = game.getFinishOrder();
        winner.setText(order.isEmpty() ? "No winner" : order.get(0).getName() + " wins!");
        String level = game.getDifficulty().csvName();
        details.setText(Character.toUpperCase(level.charAt(0)) + level.substring(1) + " board, game time "
                + GameHistory.formatDuration(game.getDuration()));

        ranking.removeAll();
        rows.clear();
        for (int i = 0; i < order.size(); i++) {
            Player p = order.get(i);
            String row = placeName(i + 1) + "  " + p.getName();
            rows.add(row);
            ranking.add(rankRow(i + 1, p));
            ranking.add(Box.createVerticalStrut(8));
        }

        if (error != null) {
            saved.setForeground(Theme.ERROR);
            saved.setText(error);
        } else if (record != null) {
            saved.setForeground(Theme.MUTED);
            saved.setText("Saved to the game history as game " + record.gameId() + ".");
        } else {
            saved.setText(" ");
        }
        revalidate();
        repaint();
        playAgain.requestFocusInWindow();
    }

    /**
     * @param place 1 to 4
     * @return "1st", "2nd", "3rd" or "4th"
     */
    static String placeName(int place) {
        return switch (place) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> place + "th";
        };
    }

    private static JPanel rankRow(int place, Player p) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);
        JLabel pl = Theme.label(placeName(place), place == 1 ? Theme.HEADING : Theme.BODY_BOLD, place == 1 ? Theme.TEXT : Theme.MUTED);
        pl.setPreferredSize(new Dimension(48, 28));
        row.add(pl);
        row.add(new Dot(Colors.of(p.getColor())));
        row.add(Theme.label(p.getName(), place == 1 ? Theme.HEADING : Theme.BODY, Theme.TEXT));
        return row;
    }

    /** @return the ranking as shown, one line per player, for tests */
    List<String> getRows() { return List.copyOf(rows); }
    /** @return the line under the winner's name */
    String getDetails() { return details.getText(); }
    /** @return the save message */
    String getSavedText() { return saved.getText(); }

    /** @param l runs when Play again is clicked */
    public void onPlayAgain(ActionListener l) { playAgain.addActionListener(l); }
    /** @param l runs when Game history is clicked */
    public void onHistory(ActionListener l) { history.addActionListener(l); }
    /** @param l runs when Main menu is clicked */
    public void onMenu(ActionListener l) { menu.addActionListener(l); }

    /** A small circle in a player's color. */
    private static class Dot extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Color color;

        Dot(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(18, 18));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fillOval(1, 1, 16, 16);
            g.dispose();
        }
    }
}
