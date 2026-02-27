package tn.esprit.services;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GoogleOAuthService {

    private static final String CLIENT_ID = tn.esprit.utils.Config.getGoogleClientId();
    private static final String CLIENT_SECRET = tn.esprit.utils.Config.getGoogleClientSecret();
    private static final String REDIRECT_URI = "http://localhost:8080/Callback";

    private GoogleAuthorizationCodeFlow flow;
    private OAuthCallbackHandler callbackHandler;

    public interface OAuthCallbackHandler {
        void onSuccess(String email, String name, String id);
        void onError(String error);
    }

    public GoogleOAuthService() {
        try {
            if (CLIENT_ID == null || CLIENT_ID.isBlank() || CLIENT_SECRET == null || CLIENT_SECRET.isBlank()) {
                throw new IllegalStateException("Missing Google OAuth credentials");
            }
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    CLIENT_ID,
                    CLIENT_SECRET,
                    Arrays.asList("email", "profile"))
                    .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                    .setAccessType("offline")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startOAuthFlow(WebView webView, OAuthCallbackHandler handler) {
        this.callbackHandler = handler;
        if (flow == null) {
            callbackHandler.onError("Google OAuth is not configured. Add GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET.");
            return;
        }

        // Generate authorization URL
        String authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .build();

        WebEngine engine = webView.getEngine();

        // Handle redirect
        engine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation != null && newLocation.startsWith(REDIRECT_URI)) {
                handleRedirect(newLocation);
            }
        });

        // Load the Google login page
        engine.load(authorizationUrl);
    }

    private void handleRedirect(String url) {
        // Extract authorization code from URL
        String code = extractCodeFromUrl(url);
        if (code == null) {
            callbackHandler.onError("No authorization code received");
            return;
        }

        // Exchange code for token and user info
        Task<Void> tokenTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                            .setRedirectUri(REDIRECT_URI)
                            .execute();

                    String accessToken = tokenResponse.getAccessToken();

                    // Get user info using the token
                    GoogleIdToken idToken = tokenResponse.parseIdToken();
                    GoogleIdToken.Payload payload = idToken.getPayload();

                    String email = payload.getEmail();
                    String name = (String) payload.get("name");
                    String id = payload.getSubject();

                    callbackHandler.onSuccess(email, name, id);

                } catch (Exception e) {
                    callbackHandler.onError(e.getMessage());
                }
                return null;
            }
        };

        new Thread(tokenTask).start();
    }

    private String extractCodeFromUrl(String url) {
        try {
            String query = url.split("\\?")[1];
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("code")) {
                    return pair[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}