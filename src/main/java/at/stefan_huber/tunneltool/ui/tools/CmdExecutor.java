package at.stefan_huber.tunneltool.ui.tools;

import javafx.application.Platform;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class CmdExecutor {

    private static final String[] APP_NAMES = new String[] { "winscp.exe", "plink.exe", "putty.exe" };

    /**
     * ASYNC execution of the given script
     */
    static void executeCmd(Consumer<String> finishedConsumer, String scriptToRun) {

        new Thread(() -> {
            try {
                executeCmdSync(finishedConsumer, scriptToRun);
            }
            catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }).start();
    }

    public static void executeCmdSync(Consumer<String> logging, String scriptToRun) throws IOException {
        if (scriptToRun == null) {
            Platform.runLater(() -> logging.accept("NO SCRIPT FOUND in settings!"));
        }
        long randomness = new Date().getTime();
        String tmpFileName = "tmp_" + randomness + ".cmd";

        File parent = new File(".");
        File tempFile = new File(parent, tmpFileName);

        List<File> appsToCopy = new ArrayList<>();

        for (String appName : APP_NAMES) {
            appsToCopy.add(new File(parent, appName));
        }

        Files.write(tempFile.toPath(), scriptToRun.getBytes());

        tryToDeleteApplicationFile(appsToCopy);

        for (File applicationFile : appsToCopy) {
            if (!applicationFile.exists()) {
                InputStream app = DatabaseOpener.class.getResourceAsStream(applicationFile.getName());
                Files.copy(app, applicationFile.toPath());
            }
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
        tryToDeleteApplicationFile(appsToCopy);

        Platform.runLater(() -> logging.accept(outputStream.toString()));
    }

    private static void tryToDeleteApplicationFile(List<File> appsToDelete) {
        try {
            for (File file : appsToDelete) {
                Files.deleteIfExists(file.toPath());
            }

        }
        catch (IOException e) {
            // ignore
        }
    }

}
