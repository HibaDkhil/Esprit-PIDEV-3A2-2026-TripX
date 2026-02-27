package tn.esprit.controllers.user;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Destination;
import tn.esprit.entities.User;
import tn.esprit.services.DestinationService;
import tn.esprit.services.UserAnalyticsService;

import java.util.List;
import java.util.Map;

public class UserAnalyticsController {

    @FXML private Label topPageLabel;
    @FXML private Label peakHourLabel;
    @FXML private Label totalInteractionsLabel;

    @FXML private PieChart featuresPieChart;
    @FXML private BarChart<String, Number> activityBarChart;

    @FXML private VBox searchHistoryList;
    @FXML private VBox recommendationsList;

    private User currentUser;
    private final UserAnalyticsService analyticsService = new UserAnalyticsService();
    private final DestinationService destinationService = new DestinationService();

    /**
     * Set user and load analytics data.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            loadAnalytics();
        }
    }

    private void loadAnalytics() {
        int userId = currentUser.getUserId();

        // 1. Load Stats
        Map<String, Integer> features = analyticsService.getMostUsedFeatures(userId);
        Map<Integer, Integer> activeHours = analyticsService.getActiveHours(userId);
        List<String> searchHistory = analyticsService.getSearchHistory(userId);
        List<Long> recommendations = analyticsService.getRecommendations(userId, 3);

        Platform.runLater(() -> {
            updateKPIs(features, activeHours);
            populateFeaturesChart(features);
            populateActivityChart(activeHours);
            populateSearchHistory(searchHistory);
            populateRecommendations(recommendations);
        });
    }

    private void updateKPIs(Map<String, Integer> features, Map<Integer, Integer> activeHours) {
        // Most visited page
        if (!features.isEmpty()) {
            topPageLabel.setText(features.keySet().iterator().next());
        }

        // Peak hour
        int peakHr = -1;
        int maxCount = -1;
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : activeHours.entrySet()) {
            total += entry.getValue();
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                peakHr = entry.getKey();
            }
        }
        if (peakHr != -1) {
            peakHourLabel.setText(String.format("%02d:00", peakHr));
        }
        totalInteractionsLabel.setText(String.valueOf(total));
    }

    private void populateFeaturesChart(Map<String, Integer> features) {
        featuresPieChart.getData().clear();
        
        int total = features.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return;

        for (Map.Entry<String, Integer> entry : features.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / total;
            String label = String.format("%s (%.1f%%)", entry.getKey(), percentage);
            featuresPieChart.getData().add(new PieChart.Data(label, entry.getValue()));
        }
    }

    private void populateActivityChart(Map<Integer, Integer> activeHours) {
        activityBarChart.getData().clear();
        activityBarChart.setAnimated(false);
        activityBarChart.setCategoryGap(5.0);
        activityBarChart.setBarGap(1.0);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Ensure all 24 hours are represented
        for (int i = 0; i < 24; i++) {
            // Only show labels for even hours to save space
            String label = (i % 2 == 0) ? String.format("%02dh", i) : "";
            series.getData().add(new XYChart.Data<>(label, activeHours.getOrDefault(i, 0)));
        }
        activityBarChart.getData().add(series);
    }

    private void populateSearchHistory(List<String> history) {
        searchHistoryList.getChildren().clear();
        if (history.isEmpty()) {
            searchHistoryList.getChildren().add(new Label("No searches yet."));
            return;
        }

        for (String query : history) {
            Label label = new Label("🔍 " + query);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-padding: 8; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
            label.setMaxWidth(Double.MAX_VALUE);
            searchHistoryList.getChildren().add(label);
        }
    }

    private void populateRecommendations(List<Long> ids) {
        recommendationsList.getChildren().clear();
        if (ids.isEmpty()) {
            recommendationsList.getChildren().add(new Label("Start exploring to get recommendations!"));
            return;
        }

        for (Long id : ids) {
            Destination d = destinationService.getDestinationById(id);
            if (d != null) {
                HBox card = new HBox(15);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");
                
                VBox info = new VBox(2);
                Label name = new Label(d.getName());
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label loc = new Label("📍 " + d.getCountry());
                loc.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                info.getChildren().addAll(name, loc);
                
                card.getChildren().add(info);
                recommendationsList.getChildren().add(card);
            }
        }
    }
}
