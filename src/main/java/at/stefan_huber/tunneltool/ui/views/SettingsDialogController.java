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

    public TextField scpNameField;
    public TextArea scpScriptField;
    public ListView scpSettingsList;

    @FXML
    public void initialize() {
        databaseSettingsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        scpSettingsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        addSelectionListeners();

        try {
            props = PropertiesHandler.getInstance();
            databaseSettingsList.setItems(FXCollections.observableArrayList(props.getDatabases()));
            scpSettingsList.setItems(FXCollections.observableArrayList(props.getScpScripts()));
            sqlDevPath.setText(props.getSqlDeveloperPath());

            databaseSettingsList.getSelectionModel().selectFirst();
            scpSettingsList.getSelectionModel().selectFirst();

        }
        catch (IOException e) {
            // do nothing
        }
    }

    private void addSelectionListeners() {
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

        scpSettingsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                scpNameField.setEditable(false);
                if (oldValue != null) {
                    updateScpEntry(null);
                }

                if (newValue == null) {
                    return;
                }

                scpNameField.setText(newValue);
                scpScriptField.setText(props.getScpScript(newValue));
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

    public void updateScpEntry(ActionEvent actionEvent) {
        String name = scpNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String scpScript = scpScriptField.getText();

        boolean existed = props.getScpScriptsMap().containsKey(name);
        props.getScpScriptsMap().put(name, scpScript);

        if (!existed) {
            scpSettingsList.getItems().add(name);
            scpSettingsList.getSelectionModel().selectLast();
        }
    }

    public void addScpEntry(ActionEvent actionEvent) {
        updateScpEntry(null);
        scpNameField.setText(null);
        scpScriptField.setText(null);
        scpSettingsList.getSelectionModel().clearSelection();
        scpNameField.setEditable(true);
    }

    public void removeScpEntry(ActionEvent actionEvent) {
        Object selectedItem = scpSettingsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        int selectedIndex = scpSettingsList.getSelectionModel().getSelectedIndex();
        scpSettingsList.getItems().remove(selectedIndex);

        props.getScpScriptsMap().remove(selectedItem.toString());
    }

    public void saveSettings(ActionEvent actionEvent) {
        props.setSqlDeveloperPath(sqlDevPath.getText());

        updateDbEntry(null);
        updateScpEntry(null);

        try {
            props.writePropertyFile();
            dialogStage.close();
        }
        catch (IOException e) {
            DialogManager.showError(e);
        }
    }
}
