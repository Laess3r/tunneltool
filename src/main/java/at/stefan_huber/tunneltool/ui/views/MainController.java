package at.stefan_huber.tunneltool.ui.views;

import at.stefan_huber.tunneltool.ui.Main;
import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;
import at.stefan_huber.tunneltool.ui.tools.DatabaseOpener;
import at.stefan_huber.tunneltool.ui.tools.FileUploader;
import at.stefan_huber.tunneltool.ui.tools.ScpOpener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class MainController {

    public Button startScpConnection;
    private PropertiesHandler props;
    public ChoiceBox databaseChoiceBox;
    public ListView fileUploadList;
    public Button startUploadButton;
    public Button startDbConnection;
    public ChoiceBox scpChoiceBox;
    public Label statusMsg;
    private Scene scene;

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {

        try {
            props = PropertiesHandler.getInstance();
        }
        catch (IOException e) {
            DialogManager.showError("Settings not found", "Please setup the application using the setup menu", e);
            return;
        }

        fileUploadList.setItems(FXCollections.observableArrayList());
        databaseChoiceBox.setItems(FXCollections.observableArrayList(props.getDatabases()));
        scpChoiceBox.setItems(FXCollections.observableArrayList(props.getScpScripts()));

        databaseChoiceBox.getSelectionModel().selectFirst();
        scpChoiceBox.getSelectionModel().selectFirst();
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void openDatabase(ActionEvent actionEvent) {
        DialogManager.showInfoAndBlock("Plink.exe windows will pop up!", "Some windows will pop up in the background!\n Leave them open as long as you need the connection.");
        startDbConnection.setDisable(true);
        statusMsg.setTextFill(Color.RED);
        statusMsg.setText("DB connection running. Close plink windows to terminate connection.");

        try {
            DatabaseOpener.doOpenDatabase(databaseChoiceBox.getSelectionModel().getSelectedItem(), s -> {
                statusMsg.setText(s);
                statusMsg.setTextFill(Color.WHITE);
                startDbConnection.setDisable(false);

                if (s != null && !s.trim().isEmpty() && !s.startsWith("ERFOLG")) {
                    DialogManager.showErrorAndBlock("Error running database script", s);
                }
            });
        }
        catch (Exception e) {
            DialogManager.showError(e);
            startDbConnection.setDisable(false);
            statusMsg.setTextFill(Color.WHITE);
            statusMsg.setText(e.getLocalizedMessage());
        }
    }

    public void openScpScript(ActionEvent actionEvent) {
        DialogManager
                .showInfoAndBlock("Plink.exe windows will pop up!", "Some black plink.exe windows will pop up in the background!\n Leave them open as long as you need the files.");
        startScpConnection.setDisable(true);
        statusMsg.setTextFill(Color.RED);
        statusMsg.setText("WinSCP opened. Close WinSCP to and wait 20 sec terminate connection.");

        try {
            ScpOpener.doOpenScpScript(scpChoiceBox.getSelectionModel().getSelectedItem(), (Consumer<String>) s -> {
                statusMsg.setText(s);
                statusMsg.setTextFill(Color.WHITE);
                startScpConnection.setDisable(false);

                if (s != null && !s.trim().isEmpty() && !s.startsWith("ERFOLG")) {
                    DialogManager.showErrorAndBlock("Error running scp script", s);
                }
            });
        }
        catch (Exception e) {
            DialogManager.showError(e);
            startScpConnection.setDisable(false);
            statusMsg.setTextFill(Color.WHITE);
            statusMsg.setText(e.getLocalizedMessage());
        }
    }

    public void openSettings(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("settings.fxml"));
        Parent page = null;
        try {
            page = loader.load();
        }
        catch (IOException e) {
            DialogManager.showError(e);
            return;
        }

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Settings");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(databaseChoiceBox.getScene().getWindow());
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        SettingsDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        dialogStage.showAndWait();

        // reinit after settings were closed
        initialize();
    }

    public void clearUploadList(ActionEvent actionEvent) {
        fileUploadList.getItems().clear();
    }

    public void startUploadList(ActionEvent actionEvent) {
        startUploadButton.setDisable(true);
        statusMsg.setTextFill(Color.RED);
        statusMsg.setText("UPLOADING IN PROGRESS. PLEASE WAIT!");

        Alert alert = DialogManager.showInfo("Please WAIT and accept popping-up dialogs!");
        alert.getDialogPane().setDisable(true);

        try {
            FileUploader.doUploadFiles(fileUploadList.getItems(), s -> {
                statusMsg.setText(s);
                statusMsg.setTextFill(Color.WHITE);
                alert.getDialogPane().setDisable(false);
                alert.close();

                DialogManager.showInfoAndBlock("Finished!", "Upload finished!");
                startUploadButton.setDisable(false);
                clearUploadList(null);
            });
        }
        catch (Exception e) {
            alert.getDialogPane().setDisable(false);
            alert.close();
            DialogManager.showError(e);
            startUploadButton.setDisable(false);
            statusMsg.setTextFill(Color.WHITE);
            statusMsg.setText(e.getLocalizedMessage());
        }
    }

    private void initDragListeners() {
        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            else {
                event.consume();
            }
        });
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file : db.getFiles()) {
                    filePath = file.getAbsolutePath();
                    if (file.isFile() && file.exists()) {
                        fileUploadList.getItems().add(filePath);
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        initDragListeners();
    }
}
