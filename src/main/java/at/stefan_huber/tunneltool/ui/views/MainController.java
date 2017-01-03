package at.stefan_huber.tunneltool.ui.views;

import at.stefan_huber.tunneltool.ui.Main;
import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;
import at.stefan_huber.tunneltool.ui.tools.DatabaseOpener;
import at.stefan_huber.tunneltool.ui.tools.ScpOpener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class MainController {

    private PropertiesHandler props;

    public ChoiceBox databaseChoiceBox;
    public ChoiceBox scpChoiceBox;
    public Label statusMsg;

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

        databaseChoiceBox.setItems(FXCollections.observableArrayList(props.getDatabases()));
        scpChoiceBox.setItems(FXCollections.observableArrayList(props.getScpScripts()));

        databaseChoiceBox.getSelectionModel().selectFirst();
        scpChoiceBox.getSelectionModel().selectFirst();
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void openDatabase(ActionEvent actionEvent) {
        try {
            DialogManager.showInfoAndBlock("Windows will pop up!", "Some windows will pop up now!\n Leave them open as long as you need the connection.");
            DatabaseOpener.doOpenDatabase(databaseChoiceBox.getSelectionModel().getSelectedItem(), s -> {
                statusMsg.setText(s);

                if (s != null && !s.trim().isEmpty()) {
                    DialogManager.showErrorAndBlock("Error running database script", s);
                }
            });
        }
        catch (Exception e) {
            DialogManager.showError(e);
        }
    }

    public void openScpScript(ActionEvent actionEvent) {
        try {
            DialogManager.showInfoAndBlock("Windows will pop up!", "Some windows will pop up now!\n Leave them open as long as you need the files.");
            ScpOpener.doOpenScpScript(scpChoiceBox.getSelectionModel().getSelectedItem(), (Consumer<String>) s -> {
                statusMsg.setText(s);

                if (s != null && !s.trim().isEmpty()) {
                    DialogManager.showErrorAndBlock("Error running scp script", s);
                }
            });
        }
        catch (Exception e) {
            DialogManager.showError(e);
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
}
