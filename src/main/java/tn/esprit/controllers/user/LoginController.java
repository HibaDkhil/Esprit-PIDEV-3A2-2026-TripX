package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkSignup;

    // Static variable to store logged-in user ID (simple session simulation)
    public static int LOGGED_IN_USER_ID = -1;
    public static String LOGGED_IN_USERNAME = "";

    public void initialize() {
        btnLogin.setOnAction(e -> handleLogin());
        linkSignup.setOnAction(e -> handleSignup());
        
        // Allow Enter key to submit
        txtPassword.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // TODO: Replace with actual database check
        // For now, hardcoded demo user
        if (username.equals("user") && password.equals("123")) {
            LOGGED_IN_USER_ID = 1;
            LOGGED_IN_USERNAME = username;
            openUserDashboard();
        } else {
            showError("Invalid username or password");
        }
    }

    private void openUserDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/UserDashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
            stage.setTitle("TripX - User Dashboard");
            
        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSignup() {
        showError("Signup feature - to be implemented");
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
