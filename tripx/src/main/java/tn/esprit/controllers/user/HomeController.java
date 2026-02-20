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
import tn.esprit.services.DestinationService;
import tn.esprit.services.UserPreferencesService;
import tn.esprit.services.WeatherPreferenceMatcher;
import tn.esprit.services.WeatherService;

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

    // NEW: Weather preference search
    @FXML private Button matchWeatherBtn;
    @FXML private VBox weatherResultsContainer; // You'll need to add this to FXML

    // Cards containers
    @FXML private HBox destinationsContainer;
    @FXML private HBox accommodationsContainer;
    @FXML private HBox activitiesContainer;

    public void setUser(User user) {
        this.currentUser = user;
        this.preferencesService = new UserPreferencesService();
        this.weatherMatcher = new WeatherPreferenceMatcher();

        this.weatherMatcher = new WeatherPreferenceMatcher();

        // TEMPORARY: Add test destinations (remove after testing)
        DestinationService destService = new DestinationService();
        destService.addTestDestinationsForWeather();

        // Load initial data
        loadFeaturedDestinations();
    }

    @FXML
    public void initialize() {
        // Add hover effects or initial setup
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

        // Search destination with weather
        WeatherPreferenceMatcher.DestinationMatch match =
                weatherMatcher.searchDestinationWithWeather(destination);

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

        // Get user preferences
        UserPreferences prefs = preferencesService.getPreferencesByUserId(currentUser.getUserId());

        if (prefs == null || prefs.getPreferredClimate() == null) {
            showAlert("Please set your climate preferences in your profile first");
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Run in background thread
        new Thread(() -> {
            List<WeatherPreferenceMatcher.DestinationMatch> matches =
                    weatherMatcher.getDestinationsMatchingClimate(prefs);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                showLoading(false);
                displayWeatherMatches(matches);
            });
        }).start();
    }

    private void displayWeatherMatches(List<WeatherPreferenceMatcher.DestinationMatch> matches) {
        weatherResultsContainer.getChildren().clear();

        if (matches.isEmpty()) {
            Label noResults = new Label("No destinations match your climate preferences");
            noResults.setStyle("-fx-padding: 20; -fx-font-size: 14px;");
            weatherResultsContainer.getChildren().add(noResults);
            return;
        }

        Label title = new Label("🌤️ Destinations matching your climate preferences:");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 0;");
        weatherResultsContainer.getChildren().add(title);

        for (WeatherPreferenceMatcher.DestinationMatch match : matches) {
            VBox card = createDestinationCard(match);
            weatherResultsContainer.getChildren().add(card);
        }
    }

    private VBox createDestinationCard(WeatherPreferenceMatcher.DestinationMatch match) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        card.setPrefWidth(320);

        // Title with emoji
        String weatherEmoji = (match.getWeather() != null) ? match.getWeather().getWeatherEmoji() : "🌍";
        Label nameLabel = new Label(weatherEmoji + " " + match.getDestination().getName() +
                ", " + match.getDestination().getCountry());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");

        // Weather details box
        VBox weatherBox = new VBox(5);
        weatherBox.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 10; -fx-padding: 12;");

        if (match.getWeather() != null) {
            WeatherService.WeatherInfo w = match.getWeather();

            Label tempLabel = new Label(String.format("🌡️ Temperature: %.1f°C (feels like %.1f°C)",
                    w.getTemperature(), w.getFeelsLike()));
            tempLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

            Label conditionLabel = new Label("☁️ Condition: " + w.getCondition());
            conditionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

            Label humidityLabel = new Label(String.format("💧 Humidity: %d%%", w.getHumidity()));
            humidityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

            Label windLabel = new Label(String.format("🌬️ Wind: %.1f m/s", w.getWindSpeed()));
            windLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

            weatherBox.getChildren().addAll(tempLabel, conditionLabel, humidityLabel, windLabel);
        } else {
            Label noWeatherLabel = new Label("🌡️ Weather data currently unavailable");
            noWeatherLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #999; -fx-font-style: italic;");
            weatherBox.getChildren().add(noWeatherLabel);
        }

        // Match score
        Label matchLabel = new Label("⭐ Match Score: " + match.getMatchScore() + "%");
        matchLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        // Description
        Label descLabel = new Label(match.getDestination().getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-padding: 5 0;");

        // Best season info
        Label seasonLabel = new Label("📅 Best season: " + match.getDestination().getBestSeason());
        seasonLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");

        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #4cccad; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        viewButton.setOnMouseEntered(e ->
                viewButton.setStyle("-fx-background-color: #3baa9a; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));
        viewButton.setOnMouseExited(e ->
                viewButton.setStyle("-fx-background-color: #4cccad; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));

        card.getChildren().addAll(nameLabel, weatherBox, matchLabel, seasonLabel, descLabel, viewButton);

        return card;
    }

    private void showWeatherResult(WeatherPreferenceMatcher.DestinationMatch match) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Destination Found");
        alert.setHeaderText(match.getDestination().getName() + ", " + match.getDestination().getCountry());

        String weatherInfo = (match.getWeather() != null) ?
                match.getWeather().toString() : "Weather data unavailable";

        alert.setContentText(
                "📍 " + match.getDestination().getFullLocation() + "\n" +
                        "🌡️ " + weatherInfo + "\n" +
                        "📝 " + match.getDestination().getDescription()
        );
        alert.showAndWait();
    }

    private void loadFeaturedDestinations() {
        // This will be implemented to load initial cards
        // For now, keep your placeholder cards
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoading(boolean show) {
        // You can add a loading indicator to your FXML
        // For now, just print
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