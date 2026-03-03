package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/UserDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1300, 760);
        primaryStage.setTitle("TripX");
        primaryStage.setScene(scene);
        primaryStage.show();

//        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/user/Login.fxml"));
//        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/admin/AdminShell.fxml"));
//        Parent root2 = loader2.load();
//        Scene scene2 = new Scene(root2, 1360, 760);
//        primaryStage.setTitle("TripX - Admin Panel");
//        primaryStage.setScene(scene2);
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}