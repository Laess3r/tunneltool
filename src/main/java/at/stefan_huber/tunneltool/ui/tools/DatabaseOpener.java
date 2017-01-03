package at.stefan_huber.tunneltool.ui.tools;

import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;
import javafx.application.Platform;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class DatabaseOpener {

    public static void openDatabase(Object dbName, Consumer<String> logging) throws Exception {
        new Thread(() -> {
            try {
                doOpenDatabase(dbName, logging);
            }
            catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }).start();
    }

    private static void doOpenDatabase(Object dbName, Consumer<String> logging) throws Exception {

        if (dbName == null) {
            throw new UnsupportedOperationException("No Database was selected");
        }

        PropertiesHandler props = PropertiesHandler.getInstance();
        String databaseScript = props.getDatabaseScript(dbName.toString());
        String sqlDevPath = props.getSqlDeveloperPath();

        if (sqlDevPath != null && !sqlDevPath.trim().isEmpty()) {
            databaseScript = databaseScript + "\nstart " + sqlDevPath;
        }

        long randomness = new Date().getTime();
        String tmpFileName = "tmp_" + randomness + ".cmd";

        File parent = new File(".");
        File tempFile = new File(parent, tmpFileName);
        File plinkFile = new File(parent, "plink.exe");

        Files.write(tempFile.toPath(), databaseScript.getBytes());

        tryToDeletePlink(plinkFile.toPath());

        if (!plinkFile.exists()) {
            InputStream plink = DatabaseOpener.class.getResourceAsStream("plink.exe");
            Files.copy(plink, plinkFile.toPath());
        }

        CommandLine cmdLine = CommandLine.parse("cmd.exe");

        cmdLine.addArgument("/C ");
        cmdLine.addArgument(tmpFileName);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(new int[] { -1, 0, 1 });

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);

        executor.execute(cmdLine);

        Files.delete(tempFile.toPath());
        tryToDeletePlink(plinkFile.toPath());

        Platform.runLater(() -> logging.accept(outputStream.toString()));
    }

    private static void tryToDeletePlink(Path plinkFile) {
        try {
            Files.deleteIfExists(plinkFile);
        }
        catch (IOException e) {
            // ignore
        }
    }

}
