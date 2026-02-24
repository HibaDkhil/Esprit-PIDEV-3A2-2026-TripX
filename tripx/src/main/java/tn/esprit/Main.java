package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // path to login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/login.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("ERROR: Cannot find login.fxml!");
                System.out.println("Looking for: /fxml/user/login.fxml");


                System.out.println("\n--- Chemins disponibles dans resources ---");
                java.net.URL url = getClass().getResource("/");
                if (url != null) {
                    java.io.File file = new java.io.File(url.toURI());
                    listFiles(file, "");
                }
                return;
            }

            Scene scene = new Scene(loader.load(), tn.esprit.utils.WindowUtils.STANDARD_WIDTH, tn.esprit.utils.WindowUtils.STANDARD_HEIGHT);

            primaryStage.setTitle("TRIPX - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour debug - affiche la structure des ressources
    private void listFiles(java.io.File dir, String indent) {
        java.io.File[] files = dir.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                System.out.println(indent + (file.isDirectory() ? "📁 " : "📄 ") + file.getName());
                if (file.isDirectory()) {
                    listFiles(file, indent + "  ");
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}