package at.stefan_huber.tunneltool.ui.views;

import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Stefan Huber
 */
public class SettingsDialogController {

    private PropertiesHandler props;
    private Stage dialogStage;

    public TextField sqlDevPath;
    public TextField dbNameField;
    public TextArea dbScriptField;
    public ListView databaseSettingsList;

    @FXML
    public void initialize() {
        databaseSettingsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            props = PropertiesHandler.getInstance();
            databaseSettingsList.setItems(FXCollections.observableArrayList(props.getDatabases()));
            sqlDevPath.setText(props.getSqlDeveloperPath());
        }
        catch (IOException e) {
            // do nothing
        }

        databaseSettingsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                dbNameField.setEditable(false);
                if (oldValue != null) {
                    updateDbEntry(null);
                }

                if (newValue == null) {
                    return;
                }

                dbNameField.setText(newValue);
                dbScriptField.setText(props.getDatabaseScript(newValue));
            }
        });

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void updateDbEntry(ActionEvent actionEvent) {
        String name = dbNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String dbScript = dbScriptField.getText();

        boolean existed = props.getDataBasesMap().containsKey(name);
        props.getDataBasesMap().put(name, dbScript);

        if (!existed) {
            databaseSettingsList.getItems().add(name);
            databaseSettingsList.getSelectionModel().selectLast();
        }
    }

    public void addDbEntry(ActionEvent actionEvent) {
        updateDbEntry(null);
        dbNameField.setText(null);
        dbScriptField.setText(null);
        databaseSettingsList.getSelectionModel().clearSelection();
        dbNameField.setEditable(true);
    }

    public void removeDbEntry(ActionEvent actionEvent) {
        Object selectedItem = databaseSettingsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        int selectedIndex = databaseSettingsList.getSelectionModel().getSelectedIndex();
        databaseSettingsList.getItems().remove(selectedIndex);

        props.getDataBasesMap().remove(selectedItem.toString());
    }

    public void saveSettings(ActionEvent actionEvent) {
        props.setSqlDeveloperPath(sqlDevPath.getText());

        updateDbEntry(null);

        try {
            props.writePropertyFile();
            dialogStage.close();
        }
        catch (IOException e) {
            DialogManager.showError(e);
        }
    }
}
