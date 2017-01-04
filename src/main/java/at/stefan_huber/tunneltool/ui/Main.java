package at.stefan_huber.tunneltool.ui;

import at.stefan_huber.tunneltool.ui.views.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        FXMLLoader fxmlLoader = new FXMLLoader();

        Parent root = fxmlLoader.load(getClass().getResourceAsStream("main.fxml"));

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/appicon.png")));
        primaryStage.setTitle("Remote Server Access Tool");
        Scene scene = new Scene(root, 400, 320);
        primaryStage.setScene(scene);
        primaryStage.show();

        MainController myController = fxmlLoader.getController();
        myController.setScene(scene);
    }
}
