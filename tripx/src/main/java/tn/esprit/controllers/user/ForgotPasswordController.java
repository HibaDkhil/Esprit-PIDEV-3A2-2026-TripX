package tn.esprit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.EmailUtils;
import tn.esprit.utils.ValidationUtils;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private VBox stepEmail;
    @FXML private VBox stepCode;
    @FXML private VBox stepNewPassword;

    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Label emailError;
    @FXML private Label codeError;
    @FXML private Label passwordError;

    private String generatedCode;
    private String userEmail;
    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        showStep(1);
    }

    private void showStep(int step) {
        stepEmail.setVisible(step == 1);
        stepEmail.setManaged(step == 1);
        stepCode.setVisible(step == 2);
        stepCode.setManaged(step == 2);
        stepNewPassword.setVisible(step == 3);
        stepNewPassword.setManaged(step == 3);
    }

    @FXML
    void handleSendCode(ActionEvent event) {
        emailError.setText("");
        userEmail = emailField.getText().trim();

        if (userEmail.isEmpty() || !ValidationUtils.isValidEmail(userEmail)) {
            emailError.setText("Please enter a valid email address.");
            return;
        }

        User user = userService.findByEmail(userEmail);
        if (user == null) {
            emailError.setText("No account found with this email.");
            return;
        }

        // Generate 6-digit code
        generatedCode = String.format("%06d", new Random().nextInt(999999));
        System.out.println("DEBUG: Generated Verification Code: " + generatedCode);

        try {
            EmailUtils.sendVerificationCode(userEmail, generatedCode);
            showStep(2);
        } catch (Exception e) {
            emailError.setText("Note: Email failed (auth error). Get the code from the console.");
            e.printStackTrace();
            // FOR TESTING: allow proceeding even if email fails
            showStep(2); 
        }
    }

    @FXML
    void handleVerifyCode(ActionEvent event) {
        codeError.setText("");
        String enteredCode = codeField.getText().trim();

        if (enteredCode.equals(generatedCode)) {
            showStep(3);
        } else {
            codeError.setText("Invalid verification code.");
        }
    }

    @FXML
    void handleResetPassword(ActionEvent event) {
        passwordError.setText("");
        String newPwd = newPasswordField.getText();
        String confirmPwd = confirmPasswordField.getText();

        if (!ValidationUtils.isValidPassword(newPwd)) {
            passwordError.setText(ValidationUtils.getPasswordError());
            return;
        }

        if (!ValidationUtils.passwordsMatch(newPwd, confirmPwd)) {
            passwordError.setText(ValidationUtils.getPasswordMismatchError());
            return;
        }

        User user = userService.findByEmail(userEmail);
        if (user != null) {
            String hashedPassword = BCrypt.hashpw(newPwd, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            if (userService.updateUserPassword(user)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully! You can now log in.");
                handleBackToLogin(event);
            } else {
                passwordError.setText("Failed to update password in database.");
            }
        }
    }

    @FXML
    void handleBackToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/user/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
