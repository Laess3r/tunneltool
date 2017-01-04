package at.stefan_huber.tunneltool.ui.tools;

import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class FileUploader {

    public static void doUploadFiles(List filePathsToUpload, Consumer<String> logging) throws Exception {
        if (filePathsToUpload == null || filePathsToUpload.isEmpty()) {
            throw new UnsupportedOperationException("Please drag&drop files to this window for uploading.");
        }

        new Thread(() -> {
            try {
                asyncThread(filePathsToUpload, logging);

            }
            catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }).start();
    }

    private static void asyncThread(List filePathsToUpload, Consumer<String> logging) throws IOException {
        // TODO REFACTOR das sollte sauber sein!

        PropertiesHandler props = PropertiesHandler.getInstance();

        // Open a tunnel, upload file to the TMP folder of the server
        String baseUploadScript = props.getFileUploadScript();
        // this script moves the file from TMP folder to the targeted folder. executed in PUTTY
        String baseFileMovementScript = props.getFileMovementScript();

        int i = 0;
        for (Object fileToUpload : filePathsToUpload) {
            i++;
            String fileNameWithoutPath = fileToUpload.toString();
            fileNameWithoutPath = fileNameWithoutPath.substring(fileNameWithoutPath.lastIndexOf("\\") + 1);
            String thisFileMovementScript = FileNameReplacer.placeFileName(baseFileMovementScript, fileNameWithoutPath);

            long randomness = new Date().getTime();
            String puttyFileName = "putty_" + i + randomness + ".txt";

            File parent = new File(".");
            File puttyFile = new File(parent, puttyFileName);
            Files.write(puttyFile.toPath(), thisFileMovementScript.getBytes());

            String tmpUploadScript = FileNameReplacer.placePuttyFile(baseUploadScript, puttyFileName);

            String scriptToRun = FileNameReplacer.placeFileName(tmpUploadScript, fileToUpload.toString());

            CmdExecutor.executeCmdSync(s -> {
            }, scriptToRun);

            Files.delete(puttyFile.toPath());

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                //ignore
            }

        }
        Platform.runLater(() -> logging.accept("Finished"));
    }

}
