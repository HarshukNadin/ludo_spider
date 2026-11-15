import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Controller.AppController;

/** Starts Spider Ludo. */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * Opens the game window.
     *
     * @param args optional path of the data folder; without it the folder is looked up
     */
    public static void main(String[] args) {
        Path dataDir = args.length > 0 ? Path.of(args[0]) : findDataDir();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.log(Level.INFO, "system look and feel not available, using default", e);
            }
            new AppController(dataDir).start();
        });
    }

    /**
     * Finds the data folder. Uses ./data when it exists, otherwise the data folder next to the jar,
     * so double clicking the jar works no matter what the working directory is.
     */
    static Path findDataDir() {
        Path local = Path.of("data");
        if (Files.isDirectory(local)) return local;
        try {
            Path jar = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path nextToJar = jar.getParent().resolve("data");
            if (Files.isDirectory(nextToJar)) return nextToJar;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "could not locate the jar folder", e);
        }
        return local;
    }
}
