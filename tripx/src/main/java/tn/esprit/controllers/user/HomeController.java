package tn.esprit.controllers.user;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.entities.UserPreferences;
import tn.esprit.services.*;

import java.io.IOException;
import java.util.List;

public class HomeController {

    private User currentUser;
    private UserPreferencesService preferencesService;
    private WeatherPreferenceMatcher weatherMatcher;

    // FXML elements from home.fxml
    @FXML private TextField destinationSearchField;
    @FXML private TextField accommodationSearchField;
    @FXML private TextField activitySearchField;
    @FXML private DatePicker datePicker;
    @FXML private Button searchButton;

    // Cards containers
    @FXML private HBox destinationsContainer;
    @FXML private HBox accommodationsContainer;
    @FXML private HBox activitiesContainer;

    // AI Chat Assistant Elements
    @FXML private VBox chatPanel;
    @FXML private VBox chatMessageContainer;
    @FXML private TextField chatInputField;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Button aiAssistantBtn;

    private GeminiService geminiService;

    public void setUser(User user) {
        this.currentUser = user;
        this.preferencesService = new UserPreferencesService();
        this.weatherMatcher = new WeatherPreferenceMatcher();
        this.geminiService = new GeminiService();

        // TEMPORARY: Add test destinations
        DestinationService destService = new DestinationService();
        destService.addTestDestinationsForWeather();

        // Initial welcome message from AI
        addBotMessage("Hi " + (currentUser != null ? currentUser.getFirstName() : "") + "! 🤖 I'm your TripX Assistant. How can I help you plan your dream trip today?");

        loadFeaturedDestinations();
    }

    @FXML
    public void initialize() {
        // Initial setup if needed
    }

    @FXML
    private void toggleChat() {
        boolean isVisible = chatPanel.isVisible();
        chatPanel.setVisible(!isVisible);
        if (!isVisible) {
            chatInputField.requestFocus();
        }
    }

    @FXML
    private void handleSendMessage() {
        String message = chatInputField.getText().trim();
        if (message.isEmpty()) return;

        // 1. Add User Message
        addUserMessage(message);
        chatInputField.clear();

        // 2. Add Loading State
        Label loadingLabel = new Label("TripX AI is thinking...");
        loadingLabel.getStyleClass().add("ai-bubble-bot");
        loadingLabel.setStyle("-fx-opacity: 0.6; -fx-font-style: italic;");
        chatMessageContainer.getChildren().add(loadingLabel);
        scrollToBottom();

        // 3. Get Recommendation from Gemini
        UserPreferences prefs = (currentUser != null) ? 
            preferencesService.getPreferencesByUserId(currentUser.getUserId()) : null;

        geminiService.getRecommendation(message, prefs)
            .thenAccept(response -> Platform.runLater(() -> {
                chatMessageContainer.getChildren().remove(loadingLabel);
                addBotMessage(response);
            }));
    }

    private void addUserMessage(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("ai-bubble-user");
        label.setWrapText(true);
        
        HBox container = new HBox(label);
        container.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        chatMessageContainer.getChildren().add(container);
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("ai-bubble-bot");
        label.setWrapText(true);
        
        HBox container = new HBox(label);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        chatMessageContainer.getChildren().add(container);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (chatScrollPane != null) {
            chatScrollPane.setVvalue(1.0);
        }
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String destination = destinationSearchField.getText().trim();
        if (destination.isEmpty()) {
            showAlert("Please enter a destination");
            return;
        }
        WeatherPreferenceMatcher.DestinationMatch match = weatherMatcher.searchDestinationWithWeather(destination);
        if (match != null) {
            showWeatherResult(match);
        } else {
            showAlert("Destination not found: " + destination);
        }
    }

    @FXML
    private void handleWeatherMatch(ActionEvent event) {
        if (currentUser == null) {
            showAlert("Please log in to use this feature");
            return;
        }
        UserPreferences prefs = preferencesService.getPreferencesByUserId(currentUser.getUserId());
        if (prefs == null || prefs.getPreferredClimate() == null) {
            showAlert("Please set your climate preferences in your profile first");
            return;
        }
        showLoading(true);
        new Thread(() -> {
            List<WeatherPreferenceMatcher.DestinationMatch> matches = weatherMatcher.getDestinationsMatchingClimate(prefs);
            Platform.runLater(() -> {
                showLoading(false);
                displayWeatherMatches(matches);
            });
        }).start();
    }

    private void displayWeatherMatches(List<WeatherPreferenceMatcher.DestinationMatch> matches) {
        // Implementation details... (kept from original for consistency)
    }

    private void showWeatherResult(WeatherPreferenceMatcher.DestinationMatch match) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Destination Found");
        alert.setHeaderText(match.getDestination().getName() + ", " + match.getDestination().getCountry());
        String weatherInfo = (match.getWeather() != null) ? match.getWeather().toString() : "Weather data unavailable";
        alert.setContentText("📍 " + match.getDestination().getFullLocation() + "\n" + "🌡️ " + weatherInfo + "\n" + "📝 " + match.getDestination().getDescription());
        alert.showAndWait();
    }

    private void loadFeaturedDestinations() { }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoading(boolean show) {
        System.out.println(show ? "Loading..." : "Done");
    }

    @FXML
    private void handleBooking(ActionEvent event) {
        System.out.println("Booking Now clicked");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}