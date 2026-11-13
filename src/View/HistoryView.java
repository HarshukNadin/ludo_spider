package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Model.GameRecord;

/** Shows finished games from the history CSV (SRS Feature 5), in the same look as the other screens. */
public class HistoryView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel table = new DefaultTableModel(
            new String[] {"Game", "Difficulty", "Date and time", "1st", "2nd", "3rd", "4th", "Duration"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JLabel status = Theme.label(" ", Theme.BODY, Theme.MUTED);
    private final JButton back = Theme.secondary("Back to menu");

    /** Builds the screen with an empty table. */
    public HistoryView() {
        super(new BorderLayout(0, 16));
        setBackground(Theme.INK);
        setBorder(BorderFactory.createEmptyBorder(24, 48, 32, 48));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        back.setFont(Theme.SMALL);
        back.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        backRow.add(back);
        for (JComponent c : new JComponent[] {backRow, (JComponent) Box.createVerticalStrut(16),
                Theme.label("Game history", Theme.TITLE, Theme.TEXT), (JComponent) Box.createVerticalStrut(6), status}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
            top.add(c);
        }
        add(top, BorderLayout.NORTH);

        JTable t = new JTable(table);
        t.setAutoCreateRowSorter(true);
        t.setRowHeight(30);
        t.setFont(Theme.BODY);
        t.setForeground(Theme.TEXT);
        t.setBackground(Theme.PANEL);
        t.setGridColor(Theme.LINE);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(Theme.LINE);
        t.setSelectionForeground(Theme.TEXT);
        t.setFillsViewportHeight(true);
        t.setDefaultRenderer(Object.class, padded(false));
        t.setDefaultRenderer(Integer.class, padded(false));
        int[] widths = {70, 110, 200, 150, 150, 150, 150, 110};
        for (int i = 0; i < widths.length; i++) t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JTableHeader header = t.getTableHeader();
        header.setDefaultRenderer(padded(true));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(t);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        scroll.getViewport().setBackground(Theme.PANEL);
        Theme.darkScrollBars(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    /** Cells with some space on the left; the header row is darker and bold. */
    private static DefaultTableCellRenderer padded(boolean headerRow) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focus, row, column);
                setHorizontalAlignment(LEFT);
                if (headerRow) {
                    setBackground(Theme.INK);
                    setForeground(Theme.MUTED);
                    setFont(Theme.BODY_BOLD);
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }
                return this;
            }
        };
        r.setOpaque(true);
        return r;
    }

    /**
     * @param records the finished games to show, oldest first
     */
    public void showRecords(List<GameRecord> records) {
        table.setRowCount(0);
        for (GameRecord r : records)
            table.addRow(new Object[] {r.gameId(), r.difficulty().csvName(), r.timestamp(), r.place(1), r.place(2), r.place(3), r.place(4), r.duration()});
        status.setForeground(Theme.MUTED);
        status.setText(records.isEmpty() ? "No games have been played yet. Finished games show up here."
                : records.size() == 1 ? "1 finished game. Click a column title to sort."
                : records.size() + " finished games. Click a column title to sort.");
    }

    /**
     * @param message what went wrong while reading the history file
     */
    public void showError(String message) {
        table.setRowCount(0);
        status.setForeground(Theme.ERROR);
        status.setText(message);
    }

    /** @param l runs when Back to menu is clicked */
    public void onBack(ActionListener l) { back.addActionListener(l); }
}
