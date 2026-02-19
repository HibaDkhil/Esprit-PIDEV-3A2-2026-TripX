package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.sql.SQLException;

public class UserPackDetailsController {

    @FXML private Button btnBack, btnBook;
    @FXML private Label lblTitle, lblDestination, lblDuration, lblCategory, lblDescription;
    @FXML private Label lblAccommodation, lblActivity, lblTransport;
    @FXML private Label lblBasePrice, lblOfferDiscount, lblLoyaltyDiscount, lblFinalPrice, lblUserLevel;
    @FXML private HBox offerRow;

    private Pack currentPack;
    
    private int getCurrentUserId() {
        return LoginController.LOGGED_IN_USER_ID;
    }

    private PackService packService;
    private LookupService lookupService;
    private PackCategoryService categoryService;
    private OfferService offerService;
    private LoyaltyPointsService loyaltyService;

    public void initialize() {
        packService = new PackService();
        lookupService = new LookupService();
        categoryService = new PackCategoryService();
        offerService = new OfferService();
        loyaltyService = new LoyaltyPointsService();

        btnBack.setOnAction(e -> goBack());
        btnBook.setOnAction(e -> bookPack());

        // TODO: Get pack ID from navigation parameter
        loadPackDetails(1); // Demo: load pack ID 1
    }

    public void loadPackDetails(int packId) {
        try {
            currentPack = packService.getById(packId);
            if (currentPack == null) {
                showError("Pack not found");
                return;
            }

            // Basic info
            lblTitle.setText(currentPack.getTitle());
            lblDescription.setText(currentPack.getDescription() != null ? currentPack.getDescription() : "No description available.");
            lblDuration.setText(currentPack.getDurationDays() + " days");

            // Destination
            Destination dest = lookupService.getDestinationById(currentPack.getDestinationId());
            lblDestination.setText(dest != null ? dest.getName() : "N/A");

            // Category
            PackCategory cat = categoryService.getById(currentPack.getCategoryId());
            lblCategory.setText(cat != null ? cat.getName() : "N/A");

            // Accommodation
            Accommodation acc = lookupService.getAccommodationById(currentPack.getAccommodationId());
            lblAccommodation.setText(acc != null ? acc.getName() : "N/A");

            // Activity
            Activity act = lookupService.getActivityById(currentPack.getActivityId());
            lblActivity.setText(act != null ? act.getName() : "N/A");

            // Transport
            Transport trans = lookupService.getTransportById(currentPack.getTransportId());
            lblTransport.setText(trans != null ? trans.getType() : "N/A");

            // Pricing
            calculatePricing();

        } catch (SQLException e) {
            showError("Failed to load pack details: " + e.getMessage());
        }
    }

    private void calculatePricing() throws SQLException {
        double basePrice = currentPack.getBasePrice().doubleValue();
        lblBasePrice.setText(String.format("%.2f TND", basePrice));

        // Check for active offer
        Offer offer = offerService.getActiveOfferByPackId(currentPack.getIdPack());
        double offerDiscountPercent = 0;

        if (offer != null && offer.getDiscountType() == Offer.DiscountType.PERCENTAGE) {
            offerDiscountPercent = offer.getDiscountValue().doubleValue();
            lblOfferDiscount.setText("-" + offerDiscountPercent + "%");
            offerRow.setVisible(true);
            offerRow.setManaged(true);
        } else {
            offerRow.setVisible(false);
            offerRow.setManaged(false);
        }

        // Get user loyalty
        LoyaltyPoints loyalty = loyaltyService.getByUserId(getCurrentUserId());
        double loyaltyDiscountPercent = (loyalty != null) ? loyalty.getLoyaltyDiscountPercent() : 0;
        lblLoyaltyDiscount.setText("-" + loyaltyDiscountPercent + "%");

        if (loyalty != null) {
            String icon = loyalty.computeLevel() == LoyaltyPoints.Level.GOLD ? "🥇" :
                         loyalty.computeLevel() == LoyaltyPoints.Level.SILVER ? "🥈" : "🥉";
            lblUserLevel.setText(icon + " " + loyalty.computeLevel().name() + " Member");
        }

        // Final price
        double finalPrice = loyaltyService.calculateFinalPrice(basePrice, getCurrentUserId(), offerDiscountPercent);
        lblFinalPrice.setText(String.format("%.2f TND", finalPrice));
    }

    private void goBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/user/UserBrowsePacks.fxml")
            );
            javafx.scene.layout.VBox page = loader.load();
            
            // Replace current content
            javafx.scene.layout.VBox currentView = (javafx.scene.layout.VBox) btnBack.getScene().getRoot();
            javafx.scene.layout.StackPane parent = (javafx.scene.layout.StackPane) currentView.getParent();
            parent.getChildren().clear();
            parent.getChildren().add(page);
            
        } catch (Exception e) {
            showError("Failed to go back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookPack() {
        showInfo("Booking functionality - to be implemented");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}
