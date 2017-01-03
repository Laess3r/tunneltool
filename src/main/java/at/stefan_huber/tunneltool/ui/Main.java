package at.stefan_huber.tunneltool.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main entry point of the tunnel tool
 *
 * @author Stefan Huber
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Tunnel Tool");
        primaryStage.setScene(new Scene(root, 320, 240));
        primaryStage.show();
    }
}
