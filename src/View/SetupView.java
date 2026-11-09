package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Model.Difficulty;
import Model.PlayerColor;

/** New-game screen: difficulty, number of players, names and colors (SRS Feature 1). */
public class SetupView extends JPanel {
    private final JComboBox<Difficulty> difficulty = new JComboBox<>(Difficulty.values());
    private final JComboBox<Integer> playerCount = new JComboBox<>(new Integer[] {2, 3, 4});
    private final List<JTextField> names = new ArrayList<>();
    private final List<JComboBox<PlayerColor>> colors = new ArrayList<>();
    private final List<JLabel> rowLabels = new ArrayList<>();
    private final JLabel error = new JLabel(" ");
    private final JButton start = new JButton("Start game");
    private final JButton back = new JButton("Back");

    public SetupView() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel title = new JLabel("New game");
        title.setFont(title.getFont().deriveFont(24f));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;
        form.add(new JLabel("Difficulty"), c);
        form.add(difficulty, c);
        c.gridy++;
        form.add(new JLabel("Players"), c);
        form.add(playerCount, c);
        PlayerColor[] all = PlayerColor.values();
        for (int i = 0; i < 4; i++) {
            c.gridy++;
            JLabel l = new JLabel("Player " + (i + 1));
            JTextField name = new JTextField(15);
            JComboBox<PlayerColor> color = new JComboBox<>(all);
            color.setSelectedIndex(i);
            rowLabels.add(l);
            names.add(name);
            colors.add(color);
            form.add(l, c);
            form.add(name, c);
            form.add(color, c);
        }
        c.gridy++;
        c.gridwidth = 3;
        error.setForeground(new Color(180, 30, 30));
        form.add(error, c);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(back);
        buttons.add(start);
        add(buttons, BorderLayout.SOUTH);

        playerCount.addActionListener(e -> updateRows());
        updateRows();
    }

    private void updateRows() {
        int n = getPlayerCount();
        for (int i = 0; i < 4; i++) {
            boolean on = i < n;
            rowLabels.get(i).setEnabled(on);
            names.get(i).setEnabled(on);
            colors.get(i).setEnabled(on);
        }
    }

    public void reset() {
        names.forEach(f -> f.setText(""));
        for (int i = 0; i < 4; i++) colors.get(i).setSelectedIndex(i);
        showError(" ");
    }

    public Difficulty getDifficulty() { return (Difficulty) difficulty.getSelectedItem(); }
    public int getPlayerCount() { return (Integer) playerCount.getSelectedItem(); }
    public String getName(int i) { return names.get(i).getText(); }
    public PlayerColor getColor(int i) { return (PlayerColor) colors.get(i).getSelectedItem(); }
    public void showError(String message) { error.setText(message); }

    public void onStart(ActionListener l) { start.addActionListener(l); }
    public void onBack(ActionListener l) { back.addActionListener(l); }
}
