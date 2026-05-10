package tn.esprit.controllers.user;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Review;
import tn.esprit.services.ReviewService;
import tn.esprit.services.DestinationService;
import tn.esprit.services.ActivityService;
import tn.esprit.utils.SessionManager;
import tn.esprit.utils.ThemeManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyReviewsDialogController implements Initializable {

    @FXML private VBox reviewsContainer;
    @FXML private ScrollPane reviewsScroll;

    private ReviewService reviewService;
    private DestinationService destinationService;
    private ActivityService activityService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reviewService = new ReviewService();
        destinationService = new DestinationService();
        activityService = new ActivityService();
        loadMyReviews();
    }

    private void loadMyReviews() {
        reviewsContainer.getChildren().clear();
        int userId = SessionManager.getCurrentUserId();
        
        new Thread(() -> {
            List<Review> myReviews = reviewService.getReviewsByUser(userId);
            Platform.runLater(() -> {
                if (myReviews.isEmpty()) {
                    Label emptyLabel = new Label("You haven't written any reviews yet.");
                    emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 50;");
                    emptyLabel.setMaxWidth(Double.MAX_VALUE);
                    emptyLabel.setAlignment(Pos.CENTER);
                    reviewsContainer.getChildren().add(emptyLabel);
                } else {
                    for (Review review : myReviews) {
                        reviewsContainer.getChildren().add(createReviewCard(review));
                    }
                }
            });
        }).start();
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(10);
        boolean dark = ThemeManager.isDarkMode();
        String cardBg = dark ? "#2a2a3d" : "white";
        String borderColor = dark ? "#3f3f5c" : "#e0e0e0";
        card.setStyle("-fx-background-color: " + cardBg + "; -fx-padding: 15; -fx-background-radius: 10; " +
                      "-fx-border-color: " + borderColor + "; -fx-border-radius: 10;");

        // Header: Target Name + Stars
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        String targetName = getTargetName(review);
        Label titleLabel = new Label(targetName);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + (dark ? "#e0e0e0" : "#2c3e50") + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String stars = "";
        for (int i = 0; i < review.getRating(); i++) stars += "⭐";
        Label starsLabel = new Label(stars);
        starsLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");

        header.getChildren().addAll(titleLabel, spacer, starsLabel);

        // Subtitle: Type
        Label typeLabel = new Label(review.getTargetType().toString());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #60a5fa; -fx-font-weight: bold;");

        // Comment
        Label commentLabel = new Label(review.getComment() != null ? review.getComment() : "");
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-text-fill: " + (dark ? "#c4c4dc" : "#555") + ";");

        // Footer: Date + Delete Button
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        String dateStr = "Unknown Date";
        if (review.getCreatedAt() != null) {
            try {
                dateStr = review.getCreatedAt().toLocalDateTime().toLocalDate().toString();
            } catch (Exception ignored) {}
        }
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 5;");
        deleteBtn.setOnAction(e -> handleDeleteReview(review));

        footer.getChildren().addAll(dateLabel, footerSpacer, deleteBtn);

        card.getChildren().addAll(header, typeLabel, commentLabel, footer);
        return card;
    }

    private String getTargetName(Review review) {
        if (review.getTargetType() == Review.TargetType.DESTINATION) {
             tn.esprit.entities.Destination d = destinationService.getDestinationById(review.getTargetId());
             return d != null ? d.getName() : "Unknown Destination";
        } else {
             tn.esprit.entities.Activity a = activityService.getActivityById(review.getTargetId());
             return a != null ? a.getName() : "Unknown Activity";
        }
    }

    private void handleDeleteReview(Review review) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Review");
        alert.setHeaderText("Delete your review for " + getTargetName(review) + "?");
        alert.setContentText("This action cannot be undone.");
        
        if (ThemeManager.isDarkMode()) {
            ThemeManager.applyTheme(alert.getDialogPane().getScene());
        }

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (reviewService.deleteReview(review.getReviewId())) {
                loadMyReviews();
            } else {
                showError("Could not delete review.");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        if (ThemeManager.isDarkMode()) {
            ThemeManager.applyTheme(alert.getDialogPane().getScene());
        }
        alert.showAndWait();
    }
}
