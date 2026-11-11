package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Model.Difficulty;
import Model.Game;
import Model.PlayerColor;

/**
 * New game screen (FR-1.02, FR-1.03, prototype screen 1.2). Three steps on one page:
 * how many players, which difficulty, and who is playing with which color.
 * The "Your game" panel on the right repeats the choices and holds the Start game button.
 * The screen only collects the answers; the Controller checks them with the Model.
 */
public class SetupView extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int[] COUNTS = {2, 3, 4};

    private final ChoiceTile[] countTiles = new ChoiceTile[COUNTS.length];
    private final ChoiceTile[] difficultyTiles = new ChoiceTile[Difficulty.values().length];
    private final ButtonGroup countGroup = new ButtonGroup();
    private final ButtonGroup difficultyGroup = new ButtonGroup();

    private final List<JPanel> rows = new ArrayList<>();
    private final List<JTextField> names = new ArrayList<>();
    private final List<List<ColorSwatch>> swatches = new ArrayList<>();
    private final List<ButtonGroup> colorGroups = new ArrayList<>();
    private final JLabel playersHint = Theme.label("Choose the number of players first.", Theme.BODY, Theme.MUTED);

    private final JLabel summaryPlayers = Theme.label("", Theme.BODY, Theme.TEXT);
    private final JLabel summaryDifficulty = Theme.label("", Theme.BODY, Theme.TEXT);
    private final JLabel message = Theme.label(" ", Theme.SMALL, Theme.ERROR);
    private final JButton start = Theme.primary("Start game");
    private final JButton back = Theme.secondary("Back to menu");
    private final JScrollPane scroll;

    /** Builds the screen with nothing chosen yet. */
    public SetupView() {
        super(new BorderLayout());
        setBackground(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(24, 48, 24, 48));

        JPanel top = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        back.setFont(Theme.SMALL);
        back.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        top.add(back);
        add(top, BorderLayout.NORTH);

        JPanel steps = transparent(null);
        steps.setLayout(new BoxLayout(steps, BoxLayout.Y_AXIS));
        steps.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 32));
        steps.add(left(Theme.label("New game", Theme.TITLE, Theme.TEXT)));
        steps.add(Box.createVerticalStrut(6));
        steps.add(left(Theme.label("Three quick steps. You can change anything before you start.", Theme.BODY, Theme.MUTED)));
        steps.add(Box.createVerticalStrut(28));

        steps.add(step(1, "How many players?"));
        JPanel countRow = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        for (int i = 0; i < COUNTS.length; i++) {
            ChoiceTile t = new ChoiceTile(String.valueOf(COUNTS[i]));
            t.setFont(Theme.HEADING);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            t.setVerticalAlignment(SwingConstants.CENTER);
            t.setPreferredSize(new Dimension(72, 52));
            t.getAccessibleContext().setAccessibleName(COUNTS[i] + " players");
            t.addActionListener(e -> updatePlayers());
            countGroup.add(t);
            countTiles[i] = t;
            countRow.add(t);
            countRow.add(Box.createHorizontalStrut(10));
        }
        steps.add(indent(countRow));
        steps.add(Box.createVerticalStrut(28));

        steps.add(step(2, "Choose difficulty"));
        JPanel diffRow = transparent(new GridLayout(1, 3, 12, 0));
        for (Difficulty d : Difficulty.values()) {
            ChoiceTile t = new ChoiceTile(describe(d));
            t.getAccessibleContext().setAccessibleName(title(d));
            t.addActionListener(e -> updateSummary());
            difficultyGroup.add(t);
            difficultyTiles[d.ordinal()] = t;
            diffRow.add(t);
        }
        diffRow.setMaximumSize(new Dimension(760, 130));
        steps.add(indent(diffRow));
        steps.add(Box.createVerticalStrut(28));

        steps.add(step(3, "Who is playing?"));
        steps.add(indent(playersHint));
        for (int i = 0; i < Game.MAX_PLAYERS; i++) {
            JPanel row = playerRow(i);
            rows.add(row);
            steps.add(indent(row));
        }

        JPanel stepsHolder = transparent(new BorderLayout());
        stepsHolder.add(steps, BorderLayout.NORTH);
        scroll = new JScrollPane(stepsHolder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        add(summaryCard(), BorderLayout.EAST);

        reset();
    }

    private JPanel playerRow(int i) {
        JPanel row = transparent(new GridBagLayout());
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 0, 14);

        JLabel label = Theme.label("Player " + (i + 1), Theme.BODY_BOLD, Theme.MUTED);
        label.setPreferredSize(new Dimension(80, 24));
        row.add(label, c);

        JTextField name = new JTextField(16);
        name.setFont(Theme.BODY);
        name.setBackground(Theme.PAPER);
        name.setForeground(Theme.INK);
        name.setCaretColor(Theme.INK);
        name.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.LINE), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        name.getAccessibleContext().setAccessibleName("Name of player " + (i + 1));
        label.setLabelFor(name);
        names.add(name);
        row.add(name, c);

        ButtonGroup group = new ButtonGroup();
        List<ColorSwatch> own = new ArrayList<>();
        c.insets = new Insets(0, 0, 0, 4);
        for (PlayerColor pc : PlayerColor.values()) {
            ColorSwatch s = new ColorSwatch(pc);
            s.addActionListener(e -> updateTakenColors());
            group.add(s);
            own.add(s);
            row.add(s, c);
        }
        c.weightx = 1;
        row.add(Box.createHorizontalGlue(), c);
        colorGroups.add(group);
        swatches.add(own);
        row.setMaximumSize(new Dimension(660, 60));
        return row;
    }

    private JPanel summaryCard() {
        JPanel card = new JPanel(new GridBagLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.PANEL);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g.setColor(Theme.LINE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 22, 22));
        card.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        card.add(Theme.label("Your game", Theme.HEADING, Theme.TEXT), c);

        BoardArt preview = new BoardArt();
        preview.setPreferredSize(new Dimension(240, 240));
        preview.setMinimumSize(new Dimension(200, 200));
        c.insets = new Insets(4, 0, 8, 0);
        card.add(preview, c);

        c.insets = new Insets(2, 0, 2, 0);
        card.add(summaryPlayers, c);
        card.add(summaryDifficulty, c);
        c.insets = new Insets(12, 0, 12, 0);
        card.add(message, c);
        c.weighty = 1;
        card.add(Box.createVerticalGlue(), c);
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(start, c);

        JPanel holder = transparent(new BorderLayout());
        holder.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        holder.add(card, BorderLayout.CENTER);
        return holder;
    }

    /** Shows only the rows of the chosen number of players. */
    private void updatePlayers() {
        Integer n = getPlayerCount();
        playersHint.setVisible(n == null);
        for (int i = 0; i < rows.size(); i++) rows.get(i).setVisible(n != null && i < n);
        updateTakenColors();
        revalidate();
        repaint();
    }

    /** Disables in every row the colors that another visible player already picked (FR-1.05). */
    private void updateTakenColors() {
        Integer n = getPlayerCount();
        int active = n == null ? 0 : n;
        for (int row = 0; row < rows.size(); row++) {
            for (ColorSwatch s : swatches.get(row)) {
                int owner = 0;
                for (int other = 0; other < active; other++)
                    if (other != row && s.getColor() == getColor(other)) owner = other + 1;
                s.setTakenBy(owner);
            }
        }
        updateSummary();
    }

    private void updateSummary() {
        Integer n = getPlayerCount();
        Difficulty d = getDifficulty();
        summaryPlayers.setText(summaryLine("Players", n == null ? null : String.valueOf(n)));
        summaryDifficulty.setText(summaryLine("Difficulty", d == null ? null : title(d)));
    }

    private static String summaryLine(String what, String value) {
        String shown = value == null ? "<span style='color:#A3AECB'>Not chosen</span>" : "<b>" + value + "</b>";
        return "<html><span style='color:#A3AECB'>" + what + "</span>&nbsp;&nbsp;" + shown + "</html>";
    }

    private static String title(Difficulty d) {
        return d.name().charAt(0) + d.name().substring(1).toLowerCase();
    }

    private static String describe(Difficulty d) {
        String dice = switch (d) {
            case EASY -> "Normal die";
            case MEDIUM -> "Normal die × Boost (1 to 3)";
            case HARD -> "Normal die × UltraBoost (1 to 6)";
        };
        String squares = d.getQuestionSquares() == 0 ? "No question squares"
                : d.getQuestionSquares() + " question squares, " + d.getSurpriseSquares() + " surprises";
        return "<html><div style='font-size:13pt'><b>" + title(d) + "</b></div>"
                + "<div style='font-size:11pt'>" + dice + "<br>" + squares + "<br>"
                + d.getProtectionSquares() + " protection squares</div></html>";
    }

    private static JPanel step(int number, String text) {
        JPanel p = transparent(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel n = new JLabel(String.valueOf(number), SwingConstants.CENTER) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.LINE);
                g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        n.setFont(Theme.BODY_BOLD);
        n.setForeground(Theme.TEXT);
        n.setPreferredSize(new Dimension(30, 30));
        p.add(n);
        p.add(Box.createHorizontalStrut(12));
        p.add(Theme.label(text, Theme.HEADING, Theme.TEXT));
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return left(p);
    }

    private static JPanel transparent(LayoutManager layout) {
        JPanel p = layout == null ? new JPanel() : new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    private static <T extends JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private static JComponent indent(JComponent c) {
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 42, 0, 0), c.getBorder()));
        return left(c);
    }

    /** Clears every answer, as on a fresh screen. */
    public void reset() {
        countGroup.clearSelection();
        difficultyGroup.clearSelection();
        colorGroups.forEach(ButtonGroup::clearSelection);
        names.forEach(f -> f.setText(""));
        showError(" ");
        updatePlayers();
        scroll.getVerticalScrollBar().setValue(0);
    }

    /** @return the chosen number of players, or null if none is chosen yet */
    public Integer getPlayerCount() {
        for (int i = 0; i < countTiles.length; i++) if (countTiles[i].isSelected()) return COUNTS[i];
        return null;
    }

    /** @return the chosen difficulty, or null if none is chosen yet */
    public Difficulty getDifficulty() {
        for (Difficulty d : Difficulty.values()) if (difficultyTiles[d.ordinal()].isSelected()) return d;
        return null;
    }

    /**
     * @param player 0 to 3
     * @return the name typed for that player, as typed
     */
    public String getName(int player) { return names.get(player).getText(); }

    /**
     * @param player 0 to 3
     * @return the color picked by that player, or null if none is picked
     */
    public PlayerColor getColor(int player) {
        for (ColorSwatch s : swatches.get(player)) if (s.isSelected()) return s.getColor();
        return null;
    }

    /**
     * Shows a message above the Start game button.
     *
     * @param text what is wrong and how to fix it, or a blank string to clear it
     */
    public void showError(String text) {
        message.setText("<html>" + text + "</html>");
    }

    /** @param l runs when Start game is clicked */
    public void onStart(ActionListener l) { start.addActionListener(l); }

    /** @param l runs when Back to menu is clicked */
    public void onBack(ActionListener l) { back.addActionListener(l); }
}
