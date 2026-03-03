package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UserDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button btnBrowsePacks, btnLoyalty, btnExchange, btnMyBookings;
    @FXML private Label lblUsername;
    @FXML private MenuButton menuUser;
    @FXML private MenuItem menuProfile, menuSettings, menuLogout;

    public void initialize() {
        // Set username from login
        lblUsername.setText(LoginController.LOGGED_IN_USERNAME);

        // Navigation buttons
        btnBrowsePacks.setOnAction(e -> loadPage("/fxml/user/UserBrowsePacks.fxml"));
        btnLoyalty.setOnAction(e -> loadPage("/fxml/user/UserLoyalty.fxml"));
        btnExchange.setOnAction(e -> loadPage("/fxml/user/UserExchange.fxml"));
        btnMyBookings.setOnAction(e -> loadPage("/fxml/user/UserBookings.fxml"));

        // Menu items
        menuProfile.setOnAction(e -> showInfo("Profile - to be implemented"));
        menuSettings.setOnAction(e -> showInfo("Settings - to be implemented"));
        menuLogout.setOnAction(e -> handleLogout());

        // Load Browse Packs by default
        loadPage("/fxml/user/UserBrowsePacks.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
            
        } catch (IOException e) {
            System.err.println("Failed to load page: " + fxmlPath);
            e.printStackTrace();
            showError("Failed to load page: " + e.getMessage());
        }
    }

    private void handleLogout() {
        try {
            LoginController.LOGGED_IN_USER_ID = -1;
            LoginController.LOGGED_IN_USERNAME = "";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnBrowsePacks.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 700));
            stage.setTitle("TripX - Login");
            
        } catch (IOException e) {
            showError("Failed to logout: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
