package tn.esprit.controllers.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.User;
import tn.esprit.services.EmailService;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Handles the email verification screen shown right after signup.
 *
 * Flow:
 *  1. LoginController creates the user in DB (status=pending_verification, email_verified=0)
 *  2. LoginController generates a 6-digit code, stores it here via setData(), sends the email
 *  3. User enters the code → verify() checks it
 *  4. On success: email_verified=1, status=active written to DB → redirect to onboarding
 */
public class EmailVerificationController {

    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML private Label emailHintLabel;
    @FXML private TextField codeField;
    @FXML private Button verifyBtn;
    @FXML private Button resendBtn;
    @FXML private Label errorLabel;
    @FXML private Label timerLabel;
    @FXML private VBox rootBox;

    // ── State ─────────────────────────────────────────────────────────────────
    private User pendingUser;
    private String expectedCode;
    private LocalDateTime codeExpiry;

    private final UserService userService = new UserService();
    private Timeline countdown;
    private int secondsLeft = 600; // 10 min

    // ── Called by LoginController right after this screen is loaded ───────────

    /**
     * @param user the newly-created user (has userId, email, firstName — email_verified=false)
     * @param code the 6-digit code already sent to their email
     */
    public void setData(User user, String code) {
        this.pendingUser  = user;
        this.expectedCode = code;
        this.codeExpiry   = LocalDateTime.now().plusMinutes(10);

        if (emailHintLabel != null) {
            String masked = maskEmail(user.getEmail());
            emailHintLabel.setText("We sent a 6-digit code to " + masked);
        }

        startCountdown();
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        if (errorLabel != null) errorLabel.setVisible(false);

        // Allow only digits, max 6 chars
        if (codeField != null) {
            codeField.textProperty().addListener((obs, old, val) -> {
                if (!val.matches("\\d*")) codeField.setText(val.replaceAll("[^\\d]", ""));
                if (val.length() > 6)    codeField.setText(val.substring(0, 6));
            });

            // Submit on Enter
            codeField.setOnAction(e -> handleVerify(null));
        }
    }

    @FXML
    private void handleVerify(ActionEvent event) {
        if (pendingUser == null || expectedCode == null) return;

        String entered = codeField.getText().trim();

        if (entered.isEmpty()) {
            showError("Please enter the 6-digit code.");
            return;
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(codeExpiry)) {
            showError("⏱ Code expired. Click Resend to get a new one.");
            return;
        }

        if (!entered.equals(expectedCode)) {
            showError("❌ Incorrect code. Please try again.");
            codeField.clear();
            return;
        }

        // ✅ Correct code — mark as verified in the shared DB
        hideError();
        if (countdown != null) countdown.stop();

        boolean ok = userService.markEmailVerified(pendingUser.getUserId());
        if (!ok) {
            showError("Database error. Please try again.");
            return;
        }
        pendingUser.setEmailVerified(true);
        pendingUser.setStatus("active");

        // Navigate to onboarding
        redirectToOnboarding(event);
    }

    @FXML
    private void handleResend(ActionEvent event) {
        if (pendingUser == null) return;

        // Generate new code
        String newCode = generateCode();
        this.expectedCode = newCode;
        this.codeExpiry   = LocalDateTime.now().plusMinutes(10);

        // Disable button to prevent spam
        resendBtn.setDisable(true);
        resendBtn.setText("Sending…");

        // Send in background so UI stays responsive
        Thread t = new Thread(() -> {
            boolean sent = EmailService.getInstance()
                    .sendVerificationEmail(pendingUser.getEmail(), pendingUser.getFirstName(), newCode);
            Platform.runLater(() -> {
                resendBtn.setDisable(false);
                resendBtn.setText("Resend Code");
                if (sent) {
                    hideError();
                    showTemporaryInfo("✅ New code sent!");
                    secondsLeft = 600;
                    startCountdown();
                } else {
                    showError("Failed to send email. Check your connection.");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (countdown != null) countdown.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/login.fxml"));
            Parent root = loader.load();
            Stage stage = getStage(event);
            if (stage == null) return;
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not return to login screen.");
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void redirectToOnboarding(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/user/onboarding.fxml"));
            Parent root = loader.load();

            OnboardingController controller = loader.getController();
            controller.setUser(pendingUser);

            // Get the stage from any available node
            Stage stage = getStage(event);
            if (stage == null) return;

            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root, w, h));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open onboarding screen.");
        }
    }

    private Stage getStage(ActionEvent event) {
        // Prefer event source
        if (event != null && event.getSource() instanceof Node) {
            return (Stage) ((Node) event.getSource()).getScene().getWindow();
        }
        // Fallback: use verifyBtn
        if (verifyBtn != null && verifyBtn.getScene() != null) {
            return (Stage) verifyBtn.getScene().getWindow();
        }
        return null;
    }

    private void startCountdown() {
        if (countdown != null) countdown.stop();
        secondsLeft = 600;

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            if (timerLabel != null) {
                int m = secondsLeft / 60;
                int s = secondsLeft % 60;
                timerLabel.setText(String.format("Code expires in %02d:%02d", m, s));
            }
            if (secondsLeft <= 0) {
                countdown.stop();
                if (timerLabel != null) timerLabel.setText("⏱ Code expired. Please resend.");
            }
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 13px;");
            errorLabel.setVisible(true);
        }
    }

    private void hideError() {
        if (errorLabel != null) errorLabel.setVisible(false);
    }

    private void showTemporaryInfo(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setStyle("-fx-text-fill: #38a169; -fx-font-size: 13px;");
            errorLabel.setVisible(true);
        }
    }

    private static String maskEmail(String email) {
        if (email == null) return "your email";
        int at = email.indexOf('@');
        if (at <= 1) return email;
        String local  = email.substring(0, at);
        String domain = email.substring(at);
        // show first 2 chars + *** + last char + domain
        String visible = local.substring(0, Math.min(2, local.length()));
        String hidden  = "*".repeat(Math.max(0, local.length() - 3));
        String last    = local.length() > 2 ? local.substring(local.length() - 1) : "";
        return visible + hidden + last + domain;
    }

    /** Generate a random 6-digit code */
    public static String generateCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }
}
