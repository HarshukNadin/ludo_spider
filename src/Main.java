

import java.nio.file.Path;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Controller.AppController;

/** Starts Spider Ludo. Data files are read from the "data" folder next to where the app runs. */
public class Main {
    public static void main(String[] args) {
        Path dataDir = Path.of(args.length > 0 ? args[0] : "data");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to the default look and feel.
            }
            new AppController(dataDir).start();
        });
    }
}
