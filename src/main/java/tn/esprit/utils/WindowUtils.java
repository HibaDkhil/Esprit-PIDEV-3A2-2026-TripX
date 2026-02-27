package tn.esprit.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class WindowUtils {
    // Standard Laptop Resolution (roughly 1366x768)
    // 1366 - 4px left - 4px right = 1358
    // 768 - 4px top - 4px bottom = 760
    public static final double STANDARD_WIDTH = 1358;
    public static final double STANDARD_HEIGHT = 760;

    /**
     * Switch scene while maintaining the standardized window size.
     */
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Get current dimensions if stage is already showing, otherwise use defaults
            double width = (stage.getWidth() > 0) ? stage.getWidth() : STANDARD_WIDTH;
            double height = (stage.getHeight() > 0) ? stage.getHeight() : STANDARD_HEIGHT;

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(true);
            
            if (!stage.isShowing()) {
                stage.setWidth(STANDARD_WIDTH);
                stage.setHeight(STANDARD_HEIGHT);
                stage.centerOnScreen();
            }
            
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to switch scene to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set the standard initial size for a stage.
     */
    public static void setStandardSize(Stage stage) {
        stage.setWidth(STANDARD_WIDTH);
        stage.setHeight(STANDARD_HEIGHT);
        stage.centerOnScreen();
    }
}
