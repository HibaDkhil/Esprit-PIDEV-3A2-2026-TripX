package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Review;
import tn.esprit.services.ReviewService;
import tn.esprit.utils.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewDialogController {

    @FXML private Label titleLabel;
    @FXML private HBox starsBox;
    @FXML private TextArea commentArea;
    @FXML private Button submitBtn;
    @FXML private VBox reviewsContainer;
    @FXML private ScrollPane reviewsScroll;

    private ReviewService reviewService;
    private Review.TargetType targetType;
    private long targetId;
    private int selectedRating = 0;
    private Button[] starButtons = new Button[5];

    public void setTarget(Review.TargetType targetType, long targetId, String targetName) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reviewService = new ReviewService();

        titleLabel.setText("Reviews for: " + targetName);
        setupStarButtons();
        loadReviews();

        submitBtn.setOnAction(e -> handleSubmit());
    }

    private void setupStarButtons() {
        starsBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            final int rating = i + 1;
            Button star = new Button("☆");
            star.setStyle("-fx-font-size: 28px; -fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-cursor: hand; -fx-padding: 2 6;");

            star.setOnMouseEntered(e -> highlightStars(rating));
            star.setOnMouseExited(e -> highlightStars(selectedRating));
            star.setOnAction(e -> {
                selectedRating = rating;
                highlightStars(rating);
            });

            starButtons[i] = star;
            starsBox.getChildren().add(star);
        }
    }

    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            if (i < count) {
                starButtons[i].setText("★");
                starButtons[i].setStyle("-fx-font-size: 28px; -fx-background-color: transparent; -fx-text-fill: #f39c12; -fx-cursor: hand; -fx-padding: 2 6;");
            } else {
                starButtons[i].setText("☆");
                starButtons[i].setStyle("-fx-font-size: 28px; -fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-cursor: hand; -fx-padding: 2 6;");
            }
        }
    }

    private void handleSubmit() {
        if (selectedRating == 0) {
            showAlert("Rating Required", "Please select a star rating before submitting.");
            return;
        }

        String comment = commentArea.getText() != null ? commentArea.getText().trim() : "";

        Review review = new Review(
                SessionManager.getCurrentUserId(),
                targetType,
                targetId,
                selectedRating,
                comment
        );

        boolean success = reviewService.addReview(review);
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Review Submitted");
            alert.setHeaderText(null);
            alert.setContentText("Thank you for your review! ⭐");
            alert.showAndWait();

            // Reset form and reload reviews
            selectedRating = 0;
            highlightStars(0);
            commentArea.clear();
            loadReviews();
        } else {
            showAlert("Error", "Failed to submit review. Please try again.");
        }
    }

    private void loadReviews() {
        reviewsContainer.getChildren().clear();

        List<Review> reviews = reviewService.getReviews(targetType, targetId);

        if (reviews.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to review!");
            noReviews.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 20;");
            reviewsContainer.getChildren().add(noReviews);
        } else {
            // Show average rating at top
            double avg = reviewService.getAverageRating(targetType, targetId);
            Label avgLabel = new Label(String.format("Average Rating: ★ %.1f / 5.0  (%d reviews)", avg, reviews.size()));
            avgLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-padding: 5 0 10 0;");
            reviewsContainer.getChildren().add(avgLabel);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            for (Review r : reviews) {
                VBox reviewCard = createReviewCard(r, fmt);
                reviewsContainer.getChildren().add(reviewCard);
            }
        }
    }

    private VBox createReviewCard(Review review, DateTimeFormatter fmt) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: rgba(0,0,0,0.03); -fx-padding: 12; -fx-background-radius: 8;");
        card.setPadding(new Insets(10));

        // Stars line
        Label stars = new Label(review.getStarsDisplay());
        stars.setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12;");

        // Date line
        String dateStr = "";
        if (review.getCreatedAt() != null) {
            dateStr = review.getCreatedAt().toLocalDateTime().format(fmt);
        }
        Label dateLabel = new Label("User #" + review.getUserId() + "  •  " + dateStr);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");

        HBox header = new HBox(10, stars, dateLabel);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        card.getChildren().add(header);

        // Comment
        if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
            Label commentLabel = new Label(review.getComment());
            commentLabel.setWrapText(true);
            commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-padding: 5 0 0 0;");
            card.getChildren().add(commentLabel);
        }

        return card;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
