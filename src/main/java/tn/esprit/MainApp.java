package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Adjust this path based on where you put AdminShell.fxml
        // If it's in resources/fxml/admin/AdminShell.fxml, use this:
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/AdminShell.fxml"));

        // Or if AdminShell.fxml is directly in resources/, use:
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminShell.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1400, 900);

        primaryStage.setTitle("TripX - Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}