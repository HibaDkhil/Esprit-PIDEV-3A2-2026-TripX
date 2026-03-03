package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load Admin Blog Management FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/user/login.fxml")
            );

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle("TripX - Admin Blog Management");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}