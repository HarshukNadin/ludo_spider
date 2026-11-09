package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.GameRecord;

/** Shows finished games from the history CSV (SRS Feature 5). */
public class HistoryView extends JPanel {
    private final DefaultTableModel table = new DefaultTableModel(
            new String[] {"Game", "Difficulty", "Date and time", "1st", "2nd", "3rd", "4th", "Duration"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JLabel status = new JLabel(" ");
    private final JButton back = new JButton("Back");

    public HistoryView() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel title = new JLabel("Game history");
        title.setFont(title.getFont().deriveFont(24f));
        add(title, BorderLayout.NORTH);
        JTable t = new JTable(table);
        t.setAutoCreateRowSorter(true);
        add(new JScrollPane(t), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(back);
        bottom.add(status);
        add(bottom, BorderLayout.SOUTH);
    }

    public void showRecords(List<GameRecord> records) {
        table.setRowCount(0);
        for (GameRecord r : records)
            table.addRow(new Object[] {r.gameId(), r.difficulty().csvName(), r.timestamp(), r.place(1), r.place(2), r.place(3), r.place(4), r.duration()});
        status.setText(records.isEmpty() ? "No games have been played yet." : records.size() + " games");
    }

    public void showError(String message) {
        table.setRowCount(0);
        status.setText(message);
    }

    public void onBack(ActionListener l) { back.addActionListener(l); }
}
