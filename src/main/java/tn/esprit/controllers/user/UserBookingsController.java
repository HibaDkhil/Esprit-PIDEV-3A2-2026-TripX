package tn.esprit.controllers.user;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.esprit.entities.PacksBooking;
import tn.esprit.entities.Pack;
import tn.esprit.services.BookingService;
import tn.esprit.services.PackService;
import tn.esprit.services.LookupService;
import tn.esprit.entities.Destination;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserBookingsController {

    @FXML private VBox bookingsList;
    @FXML private Label lblBookingCount;
    @FXML private ToggleButton btnAllBookings, btnPending, btnConfirmed, btnCompleted, btnCancelled;

    private BookingService bookingService;
    private PackService packService;
    private LookupService lookupService;
    
    private List<PacksBooking> allBookings;
    private ToggleGroup filterGroup;

    public void initialize() {
        bookingService = new BookingService();
        packService = new PackService();
        lookupService = new LookupService();
        
        setupFilterButtons();
        loadUserBookings();
    }

    private void setupFilterButtons() {
        filterGroup = new ToggleGroup();
        btnAllBookings.setToggleGroup(filterGroup);
        btnPending.setToggleGroup(filterGroup);
        btnConfirmed.setToggleGroup(filterGroup);
        btnCompleted.setToggleGroup(filterGroup);
        btnCancelled.setToggleGroup(filterGroup);
        
        btnAllBookings.setOnAction(e -> filterBookings(null));
        btnPending.setOnAction(e -> filterBookings(PacksBooking.Status.PENDING));
        btnConfirmed.setOnAction(e -> filterBookings(PacksBooking.Status.CONFIRMED));
        btnCompleted.setOnAction(e -> filterBookings(PacksBooking.Status.COMPLETED));
        btnCancelled.setOnAction(e -> filterBookings(PacksBooking.Status.CANCELLED));
    }

    private void loadUserBookings() {
        try {
            int userId = LoginController.LOGGED_IN_USER_ID;
            allBookings = bookingService.getByUserId(userId);
            displayBookings(allBookings);
            lblBookingCount.setText(allBookings.size() + " booking" + (allBookings.size() != 1 ? "s" : ""));
        } catch (SQLException e) {
            showError("Failed to load bookings: " + e.getMessage());
        }
    }

    private void displayBookings(List<PacksBooking> bookings) {
        bookingsList.getChildren().clear();
        
        if (bookings.isEmpty()) {
            Label emptyLabel = new Label("No bookings found");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #94A3B8; -fx-padding: 40;");
            bookingsList.getChildren().add(emptyLabel);
            return;
        }
        
        for (PacksBooking booking : bookings) {
            bookingsList.getChildren().add(createBookingCard(booking));
        }
    }

    private HBox createBookingCard(PacksBooking booking) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 24; " +
                     "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 16; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 2);");
        
        try {
            // Left: Pack info
            VBox leftSection = new VBox(12);
            HBox.setHgrow(leftSection, Priority.ALWAYS);
            
            Pack pack = packService.getById(booking.getPackId());
            
            Label packName = new Label(pack != null ? pack.getTitle() : "Pack #" + booking.getPackId());
            packName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");
            
            if (pack != null) {
                Destination dest = lookupService.getDestinationById(pack.getDestinationId());
                HBox packInfo = new HBox(16);
                Label destLabel = new Label("📍 " + (dest != null ? dest.getName() : "N/A"));
                destLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5568;");
                Label durLabel = new Label("📅 " + pack.getDurationDays() + " days");
                durLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5568;");
                packInfo.getChildren().addAll(destLabel, durLabel);
                leftSection.getChildren().addAll(packName, packInfo);
            } else {
                leftSection.getChildren().add(packName);
            }
            
            // Travel dates
            HBox datesBox = new HBox(12);
            datesBox.setAlignment(Pos.CENTER_LEFT);
            datesBox.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; -fx-padding: 10;");
            
            Label travelDates = new Label("🗓 " + booking.getTravelStartDate() + " → " + booking.getTravelEndDate());
            travelDates.setStyle("-fx-font-size: 13px; -fx-text-fill: #2D3748; -fx-font-weight: 600;");
            
            Label travelers = new Label("👥 " + booking.getNumTravelers());
            travelers.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5568;");
            
            datesBox.getChildren().addAll(travelDates, new Separator(), travelers);
            leftSection.getChildren().add(datesBox);
            
            // Booking date
            Label bookingDate = new Label("Booked on: " + booking.getBookingDate().toString().substring(0, 10));
            bookingDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");
            leftSection.getChildren().add(bookingDate);
            
            // Middle: Status & Price
            VBox middleSection = new VBox(16);
            middleSection.setAlignment(Pos.CENTER);
            middleSection.setMinWidth(200);
            
            // Status badge
            Label statusBadge = new Label(getStatusEmoji(booking.getStatus()) + " " + booking.getStatus().name());
            String statusColor = switch (booking.getStatus()) {
                case CONFIRMED -> "-fx-background-color: #C6F6D5; -fx-text-fill: #22543D;";
                case PENDING -> "-fx-background-color: #FED7AA; -fx-text-fill: #7C2D12;";
                case CANCELLED -> "-fx-background-color: #FED7D7; -fx-text-fill: #742A2A;";
                case COMPLETED -> "-fx-background-color: #DBEAFE; -fx-text-fill: #1E3A8A;";
            };
            statusBadge.setStyle(statusColor + "-fx-padding: 8 16; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: 700;");
            
            // Price
            VBox priceBox = new VBox(4);
            priceBox.setAlignment(Pos.CENTER);
            
            Label priceLabel = new Label(String.format("%.2f TND", booking.getFinalPrice()));
            priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
            
            if (booking.getDiscountApplied().doubleValue() > 0) {
                Label discountLabel = new Label("Saved " + booking.getDiscountApplied() + "%");
                discountLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2F9D94; -fx-font-weight: 600;");
                priceBox.getChildren().addAll(priceLabel, discountLabel);
            } else {
                priceBox.getChildren().add(priceLabel);
            }
            
            middleSection.getChildren().addAll(statusBadge, priceBox);
            
            // Right: Actions
            VBox rightSection = new VBox(10);
            rightSection.setAlignment(Pos.CENTER);
            rightSection.setMinWidth(120);
            
            // Cancel button (only for PENDING or CONFIRMED)
            if (booking.getStatus() == PacksBooking.Status.PENDING || booking.getStatus() == PacksBooking.Status.CONFIRMED) {
                Button cancelBtn = new Button("❌ Cancel");
                cancelBtn.setStyle("-fx-background-color: #FED7D7; -fx-text-fill: #C53030; -fx-font-weight: 600; " +
                                 "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
                cancelBtn.setOnAction(e -> handleCancelBooking(booking));
                rightSection.getChildren().add(cancelBtn);
            }
            
            card.getChildren().addAll(leftSection, new Separator(), middleSection, rightSection);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return card;
    }

    private String getStatusEmoji(PacksBooking.Status status) {
        return switch (status) {
            case PENDING -> "⏳";
            case CONFIRMED -> "✅";
            case CANCELLED -> "❌";
            case COMPLETED -> "🎉";
        };
    }

    private void filterBookings(PacksBooking.Status status) {
        if (allBookings == null) return;
        
        if (status == null) {
            displayBookings(allBookings);
        } else {
            List<PacksBooking> filtered = allBookings.stream()
                .filter(b -> b.getStatus() == status)
                .toList();
            displayBookings(filtered);
        }
    }

    private void handleCancelBooking(PacksBooking booking) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel this booking?");
        confirm.setContentText("Are you sure you want to cancel your booking? This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookingService.updateStatus(booking.getIdBooking(), PacksBooking.Status.CANCELLED);
                loadUserBookings();
                showInfo("Booking cancelled successfully!");
            } catch (SQLException e) {
                showError("Failed to cancel booking: " + e.getMessage());
            }
        }
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
