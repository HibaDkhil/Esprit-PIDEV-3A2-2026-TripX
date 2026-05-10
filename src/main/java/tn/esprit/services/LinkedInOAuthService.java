package tn.esprit.services;

import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LinkedInOAuthService {

    private static final String CLIENT_ID = tn.esprit.utils.Config.getLinkedInClientId();
    private static final String CLIENT_SECRET = tn.esprit.utils.Config.getLinkedInClientSecret();
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String SCOPE = "openid profile email";

    private OAuth20Service service;
    private OAuthCallbackHandler callbackHandler;

    public interface OAuthCallbackHandler {
        void onSuccess(String email, String name, String id);
        void onError(String error);
    }

    public LinkedInOAuthService() {
        if (CLIENT_ID == null || CLIENT_ID.isBlank() || CLIENT_SECRET == null || CLIENT_SECRET.isBlank()) {
            throw new IllegalStateException("Missing LinkedIn OAuth credentials");
        }
        service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI)
                .defaultScope(SCOPE)
                .build(LinkedInApi20.instance());
    }

    public void startOAuthFlow(WebView webView, OAuthCallbackHandler handler) {
        this.callbackHandler = handler;
        if (service == null) {
            callbackHandler.onError("LinkedIn OAuth is not configured. Add LINKEDIN_CLIENT_ID and LINKEDIN_CLIENT_SECRET.");
            return;
        }

        // Generate authorization URL
        String authorizationUrl = service.getAuthorizationUrl();

        WebEngine engine = webView.getEngine();

        // Handle redirect
        engine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation != null && newLocation.startsWith(REDIRECT_URI)) {
                handleRedirect(newLocation);
            }
        });

        // Load LinkedIn login page
        engine.load(authorizationUrl);
    }

    private void handleRedirect(String url) {
        // Extract code from URL
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
                    OAuth2AccessToken accessToken = service.getAccessToken(code);

                    // Get user info using the token
                    OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v2/userinfo");
                    service.signRequest(accessToken, request);

                    Response response = service.execute(request);
                    String body = response.getBody();

                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();

                    String email = json.get("email").getAsString();
                    String name = json.get("name").getAsString();
                    String id = json.get("sub").getAsString();

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
            String[] parts = url.split("\\?code=");
            if (parts.length > 1) {
                return parts[1].split("&")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}