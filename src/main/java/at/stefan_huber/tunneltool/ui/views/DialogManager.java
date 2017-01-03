package at.stefan_huber.tunneltool.ui.views;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Stefan Huber
 */
public class DialogManager {

    public static void showError(Throwable ex) {
        show("Error Occurred", ex.getLocalizedMessage(), ex, Alert.AlertType.ERROR, true);
    }

    public static void showInfo(String message) {
        show(message, message, null, Alert.AlertType.INFORMATION, false);
    }

    public static void showInfoAndBlock(String title, String message) {
        show(title, message, null, Alert.AlertType.INFORMATION, true);
    }

    public static void showErrorAndBlock(String title, String message) {
        show(title, message, null, Alert.AlertType.ERROR, true);
    }

    public static void showError(String content, Throwable ex) {
        show(content, content, ex, Alert.AlertType.ERROR, true);
    }

    public static void showError(String header, String content, Throwable ex) {
        show(header, content, ex, Alert.AlertType.ERROR, true);
    }

    private static void show(String header, String content, Throwable ex, Alert.AlertType alertType, boolean block) {
        Alert alert = new Alert(alertType);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(content);

        if (ex != null) {
            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
        }

        if(block){
            alert.showAndWait();
        }else{
            alert.show();
        }
    }

}
