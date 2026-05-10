package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.services.GoogleOAuthService;
import tn.esprit.services.LinkedInOAuthService;

public class OAuthPopupController {

    @FXML private WebView webView;
    @FXML private ProgressIndicator loadingIndicator;

    private GoogleOAuthService googleOAuthService;
    private LinkedInOAuthService linkedInOAuthService;
    private OAuthCallback callback;
    private String currentProvider;

    public interface OAuthCallback {
        void onSuccess(String email, String name);
        void onFailure(String error);
    }

    public void initialize() {
        // Show loading when page loads
        webView.getEngine().locationProperty().addListener((obs, oldLoc, newLoc) -> {
            loadingIndicator.setVisible(true);
        });

        webView.getEngine().documentProperty().addListener((obs, oldDoc, newDoc) -> {
            loadingIndicator.setVisible(false);
        });
    }

    public void startGoogleOAuth(OAuthCallback callback) {
        this.callback = callback;
        this.currentProvider = "Google";
        try {
            googleOAuthService = new GoogleOAuthService();
        } catch (Exception e) {
            callback.onFailure("Google OAuth is not configured. Please set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET in .env.");
            return;
        }

        googleOAuthService.startOAuthFlow(webView, new GoogleOAuthService.OAuthCallbackHandler() {
            @Override
            public void onSuccess(String email, String name, String id) {
                javafx.application.Platform.runLater(() -> {
                    closeWindow();
                    callback.onSuccess(email, name);
                });
            }

            @Override
            public void onError(String error) {
                javafx.application.Platform.runLater(() -> {
                    closeWindow();
                    callback.onFailure(error);
                });
            }
        });
    }

    public void startLinkedInOAuth(OAuthCallback callback) {
        this.callback = callback;
        this.currentProvider = "LinkedIn";
        try {
            linkedInOAuthService = new LinkedInOAuthService();
        } catch (Exception e) {
            callback.onFailure("LinkedIn OAuth is not configured. Please set LINKEDIN_CLIENT_ID and LINKEDIN_CLIENT_SECRET in .env.");
            return;
        }

        linkedInOAuthService.startOAuthFlow(webView, new LinkedInOAuthService.OAuthCallbackHandler() {
            @Override
            public void onSuccess(String email, String name, String id) {
                javafx.application.Platform.runLater(() -> {
                    closeWindow();
                    callback.onSuccess(email, name);
                });
            }

            @Override
            public void onError(String error) {
                javafx.application.Platform.runLater(() -> {
                    closeWindow();
                    callback.onFailure(error);
                });
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}