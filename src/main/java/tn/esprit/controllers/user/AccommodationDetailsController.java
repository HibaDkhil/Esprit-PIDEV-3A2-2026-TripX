package tn.esprit.controllers.user;

import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import netscape.javascript.JSObject;
import tn.esprit.entities.Accommodation;
import tn.esprit.entities.Room;
import tn.esprit.entities.RoomImage;
import tn.esprit.entities.User;
import tn.esprit.services.NearbyAiPlannerService;
import tn.esprit.services.RoomImageService;
import tn.esprit.services.RoomService;
import tn.esprit.services.UserService;
import tn.esprit.utils.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javafx.util.Duration;

public class AccommodationDetailsController {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    @FXML private ScrollPane detailsScrollPane;
    @FXML private ScrollPane galleryScrollPane;
    @FXML private VBox detailsContentBox;
    @FXML private VBox overviewSection;
    @FXML private VBox roomsSection;
    @FXML private VBox locationSection;
    @FXML private VBox aiSection;

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label locationLabel;
    @FXML private Label userNameLabel;
    @FXML private Label avatarInitials;
    @FXML private Label starsLabel;
    @FXML private Label statusLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label amenitiesSummaryLabel;
    @FXML private Label aiSummaryLabel;
    @FXML private VBox aiSectionsContainer;
    @FXML private VBox roomsTableContainer;
    @FXML private ImageView mainImageView;
    @FXML private HBox galleryThumbsContainer;
    @FXML private Label galleryHintLabel;
    @FXML private WebView mapWebView;
    @FXML private Button backToListButton;
    @FXML private Button generateAiNearbyButton;

    private final RoomService roomService = new RoomService();
    private final RoomImageService roomImageService = new RoomImageService();
    private final NearbyAiPlannerService nearbyAiPlannerService = new NearbyAiPlannerService();
    private final UserService userService = new UserService();

    private Accommodation accommodation;
    private WebEngine mapEngine;
    private boolean mapReady = false;
    private final List<GalleryImage> galleryImages = new ArrayList<>();
    private int selectedGalleryIndex = -1;
    private Timeline scrollTimeline;
    private Timeline autoCarouselTimeline;
    private Timeline galleryStripTimeline;
    private User currentUser;

    @FXML
    public void initialize() {
        if (backToListButton != null) {
            backToListButton.setOnAction(e -> goBackToList());
        }
        if (generateAiNearbyButton != null) {
            generateAiNearbyButton.setOnAction(e -> generateNearbySuggestions());
        }
        configureMap();
        ensureCurrentUserLoaded();
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        renderAccommodationData();
        refreshMapMarker();
    }

    public void setCurrentUser(User currentUser) {
        if (currentUser == null) {
            int sessionId = SessionManager.getCurrentUserId();
            if (sessionId > 0) {
                currentUser = userService.findById(sessionId);
            }
        }
        this.currentUser = currentUser;
        if (currentUser != null && currentUser.getUserId() > 0) {
            SessionManager.setCurrentUserId(currentUser.getUserId());
        }
        if (avatarInitials != null) {
            if (currentUser == null) {
                avatarInitials.setText("");
                avatarInitials.setGraphic(null);
            } else {
                String first = currentUser.getFirstName() != null && !currentUser.getFirstName().isBlank()
                        ? currentUser.getFirstName().substring(0, 1).toUpperCase() : "";
                String last = currentUser.getLastName() != null && !currentUser.getLastName().isBlank()
                        ? currentUser.getLastName().substring(0, 1).toUpperCase() : "";
                String initials = (first + last).isBlank() ? "U" : (first + last);
                avatarInitials.setText(initials);
                applyAvatarGraphic();
            }
        }
        if (userNameLabel != null) {
            userNameLabel.setText(currentUser == null ? "" : (safeName(currentUser.getFirstName()) + " " + safeName(currentUser.getLastName())).trim());
        }
    }

    @FXML
    private void scrollToOverview() {
        scrollToSection(overviewSection);
    }

    @FXML
    private void scrollToRooms() {
        scrollToSection(roomsSection);
    }

    @FXML
    private void scrollToLocation() {
        scrollToSection(locationSection);
    }

    @FXML
    private void scrollToAiNearby() {
        scrollToSection(aiSection);
    }

    private void scrollToSection(VBox section) {
        if (detailsScrollPane == null || detailsContentBox == null || section == null) return;
        Platform.runLater(() -> {
            double contentHeight = detailsContentBox.getBoundsInLocal().getHeight();
            double viewportHeight = detailsScrollPane.getViewportBounds().getHeight();
            double y = section.getBoundsInParent().getMinY();
            double denominator = Math.max(1, contentHeight - viewportHeight);
            double target = Math.max(0, Math.min(1, y / denominator));
            animateScroll(detailsScrollPane, target, 300);
        });
    }

    private void animateScroll(ScrollPane scrollPane, double targetValue, int durationMs) {
        if (scrollPane == null) return;
        if (scrollTimeline != null) {
            scrollTimeline.stop();
        }
        scrollTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(scrollPane.vvalueProperty(), scrollPane.getVvalue())),
                new KeyFrame(Duration.millis(durationMs), new KeyValue(scrollPane.vvalueProperty(), targetValue))
        );
        scrollTimeline.play();
    }

    private void renderAccommodationData() {
        if (accommodation == null) return;

        titleLabel.setText(safe(accommodation.getName(), "Accommodation"));
        subtitleLabel.setText(safe(accommodation.getType(), "Stay") + " in " + safe(accommodation.getCity(), "Unknown city"));
        locationLabel.setText("📍 " + safe(accommodation.getAddress(), "Address unavailable") + ", " + safe(accommodation.getCountry(), ""));
        starsLabel.setText(buildStars(accommodation.getStars()));
        statusLabel.setText(safe(accommodation.getStatus(), "Active"));

        descriptionLabel.setText(safe(accommodation.getDescription(),
                "No description provided yet. Explore rooms and nearby places below."));
        amenitiesSummaryLabel.setText(safe(accommodation.getAccommodationAmenities(), "Amenities information unavailable."));

        renderRoomsTable();
        loadGalleryImages();

        aiSummaryLabel.setText("Generate smart nearby suggestions based on this accommodation location.");
        aiSectionsContainer.getChildren().clear();
    }

    private void loadGalleryImages() {
        stopAutoCarousel();
        galleryImages.clear();
        galleryThumbsContainer.getChildren().clear();
        selectedGalleryIndex = -1;

        if (accommodation == null) return;

        Set<String> seenPaths = new HashSet<>();
        String mainPath = safeTrim(accommodation.getImagePath());
        if (!mainPath.isEmpty()) {
            galleryImages.add(new GalleryImage(mainPath, "Main property image"));
            seenPaths.add(mainPath);
        }

        List<Room> rooms = roomService.getRoomsByAccommodationId(accommodation.getId());
        for (Room room : rooms) {
            List<RoomImage> roomImages = roomImageService.getByRoomId(room.getId());
            for (RoomImage image : roomImages) {
                String imagePath = safeTrim(image.getFilePath());
                if (imagePath.isEmpty() || seenPaths.contains(imagePath)) continue;
                galleryImages.add(new GalleryImage(imagePath, safe(room.getRoomName(), "Room")));
                seenPaths.add(imagePath);
                if (galleryImages.size() >= 18) break;
            }
            if (galleryImages.size() >= 18) break;
        }

        if (galleryImages.isEmpty()) {
            galleryHintLabel.setText("No photos yet.");
            loadMainImage(null);
            return;
        }

        galleryHintLabel.setText(galleryImages.size() + " photos");
        for (int i = 0; i < galleryImages.size(); i++) {
            int index = i;
            GalleryImage item = galleryImages.get(i);
            ImageView thumb = new ImageView(loadImageByPath(item.path));
            thumb.getStyleClass().add("details-gallery-thumb");
            thumb.setFitWidth(150);
            thumb.setFitHeight(92);
            thumb.setPreserveRatio(false);
            thumb.setSmooth(true);
            thumb.setOnMouseClicked(e -> {
                selectGalleryImage(index, true);
                restartAutoCarousel();
            });
            galleryThumbsContainer.getChildren().add(thumb);
        }

        selectGalleryImage(0, false);
        startAutoCarousel();
    }

    private void selectGalleryImage(int index, boolean animate) {
        if (index < 0 || index >= galleryImages.size()) return;
        selectedGalleryIndex = index;

        for (int i = 0; i < galleryThumbsContainer.getChildren().size(); i++) {
            if (galleryThumbsContainer.getChildren().get(i) instanceof ImageView thumb) {
                thumb.setStyle(i == index
                        ? "-fx-border-color: #2563EB; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
                        : "-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }

        GalleryImage selected = galleryImages.get(index);
        Image image = loadImageByPath(selected.path);
        if (image == null || image.isError()) {
            loadMainImage(null);
        } else {
            setMainImageWithTransition(image, animate);
        }
        smoothScrollGalleryStrip(index);
        galleryHintLabel.setText((index + 1) + "/" + galleryImages.size() + " • " + selected.caption);
    }

    private void setMainImageWithTransition(Image image, boolean animate) {
        if (!animate || mainImageView.getImage() == null) {
            mainImageView.setOpacity(1.0);
            mainImageView.setImage(image);
            mainImageView.setStyle("");
            return;
        }
        FadeTransition fadeOut = new FadeTransition(Duration.millis(180), mainImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.2);
        fadeOut.setOnFinished(evt -> {
            mainImageView.setImage(image);
            mainImageView.setStyle("");
            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), mainImageView);
            fadeIn.setFromValue(0.2);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void smoothScrollGalleryStrip(int selectedIndex) {
        if (galleryScrollPane == null || galleryImages.size() <= 1) return;
        double target = (double) selectedIndex / (double) (galleryImages.size() - 1);
        if (galleryStripTimeline != null) {
            galleryStripTimeline.stop();
        }
        galleryStripTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(galleryScrollPane.hvalueProperty(), galleryScrollPane.getHvalue())),
                new KeyFrame(Duration.millis(260), new KeyValue(galleryScrollPane.hvalueProperty(), target))
        );
        galleryStripTimeline.play();
    }

    private void startAutoCarousel() {
        stopAutoCarousel();
        if (galleryImages.size() <= 1) return;
        autoCarouselTimeline = new Timeline(new KeyFrame(Duration.seconds(3.8), e -> advanceCarousel()));
        autoCarouselTimeline.setCycleCount(Timeline.INDEFINITE);
        autoCarouselTimeline.play();
    }

    private void restartAutoCarousel() {
        if (galleryImages.size() <= 1) return;
        startAutoCarousel();
    }

    private void stopAutoCarousel() {
        if (autoCarouselTimeline != null) {
            autoCarouselTimeline.stop();
            autoCarouselTimeline = null;
        }
    }

    private void advanceCarousel() {
        if (galleryImages.isEmpty()) return;
        int nextIndex = (selectedGalleryIndex + 1) % galleryImages.size();
        selectGalleryImage(nextIndex, true);
    }

    private void configureMap() {
        if (mapWebView == null) return;
        mapEngine = mapWebView.getEngine();
        mapEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                mapReady = true;
                try {
                    JSObject window = (JSObject) mapEngine.executeScript("window");
                    window.setMember("javaBridge", new Object());
                } catch (Exception ignored) {
                }
                refreshMapMarker();
            }
        });

        var mapUrl = getClass().getResource("/map.html");
        if (mapUrl != null) {
            mapEngine.load(mapUrl.toExternalForm());
        }
    }

    private void refreshMapMarker() {
        if (!mapReady || mapEngine == null || accommodation == null) return;
        if (accommodation.getLatitude() == null || accommodation.getLongitude() == null) return;

        String markerJson = "[{\"id\":" + accommodation.getId() +
                ",\"name\":\"" + escapeJs(safe(accommodation.getName(), "Accommodation")) + "\"" +
                ",\"city\":\"" + escapeJs(safe(accommodation.getCity(), "")) + "\"" +
                ",\"country\":\"" + escapeJs(safe(accommodation.getCountry(), "")) + "\"" +
                ",\"latitude\":" + accommodation.getLatitude() +
                ",\"longitude\":" + accommodation.getLongitude() + "}]";

        try {
            mapEngine.executeScript("setMarkers(" + markerJson + ");");
            mapEngine.executeScript("if(typeof invalidateMapSize==='function'){invalidateMapSize();}");
        } catch (Exception ignored) {
        }
    }

    private void renderRoomsTable() {
        roomsTableContainer.getChildren().clear();
        if (accommodation == null) return;

        List<Room> rooms = roomService.getRoomsByAccommodationId(accommodation.getId());
        if (rooms.isEmpty()) {
            Label empty = new Label("No rooms available yet for this accommodation.");
            empty.getStyleClass().add("details-room-empty");
            roomsTableContainer.getChildren().add(empty);
            return;
        }

        HBox header = new HBox(12);
        header.getStyleClass().add("details-room-table-header");
        header.getChildren().addAll(
                headerLabel("Room type", 340),
                headerLabel("Guests", 130),
                headerLabel("Status", 120),
                headerLabel("Price", 180)
        );
        roomsTableContainer.getChildren().add(header);

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        for (Room room : rooms) {
            HBox row = new HBox(12);
            row.getStyleClass().add("details-room-table-row");

            VBox roomCol = new VBox(4);
            roomCol.setPrefWidth(340);
            Label roomName = new Label(safe(room.getRoomName(), "Room"));
            roomName.getStyleClass().add("details-room-name");
            Label roomMeta = new Label(safe(room.getRoomType(), "Type") + " • " + safe(room.getAmenities(), "Amenities not specified"));
            roomMeta.getStyleClass().add("details-room-meta");
            roomMeta.setWrapText(true);
            roomCol.getChildren().addAll(roomName, roomMeta);

            Label guests = new Label("👥 " + room.getCapacity() + " • 🛏 " + room.getBedType());
            guests.setPrefWidth(130);
            guests.getStyleClass().add("details-room-guests");

            Label status = new Label(room.isAvailable() ? "Available" : "Unavailable");
            status.setPrefWidth(120);
            status.getStyleClass().add(room.isAvailable() ? "details-room-status-available" : "details-room-status-unavailable");

            HBox priceCol = new HBox(8);
            priceCol.setPrefWidth(180);
            Label price = new Label(currency.format(room.getPricePerNight()));
            price.getStyleClass().add("details-room-price");
            Button cta = new Button("Book Accommodation");
            cta.getStyleClass().add("details-room-cta");
            cta.setDisable(!room.isAvailable());
            cta.setOnAction(e -> openAccommodationBookingDialog(room));
            priceCol.getChildren().addAll(price, cta);

            row.getChildren().addAll(roomCol, guests, status, priceCol);
            roomsTableContainer.getChildren().add(row);
        }
    }

    private Label headerLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.getStyleClass().add("details-room-header-label");
        return label;
    }

    private void generateNearbySuggestions() {
        if (accommodation == null) return;

        generateAiNearbyButton.setDisable(true);
        generateAiNearbyButton.setText("Generating...");
        aiSummaryLabel.setText("AI is preparing nearby ideas...");
        aiSectionsContainer.getChildren().clear();

        CompletableFuture
                .supplyAsync(() -> nearbyAiPlannerService.generateNearbyPlan(accommodation))
                .thenAccept(result -> Platform.runLater(() -> {
                    generateAiNearbyButton.setDisable(false);
                    generateAiNearbyButton.setText("Refresh AI Nearby");

                    if (result == null || !result.success) {
                        aiSummaryLabel.setText(result == null
                                ? "Unable to generate suggestions right now."
                                : safe(result.errorMessage, "Unable to generate suggestions right now."));
                        return;
                    }

                    aiSummaryLabel.setText(safe(result.summary, "Nearby suggestions ready."));
                    aiSectionsContainer.getChildren().clear();

                    for (NearbyAiPlannerService.NearbySection section : result.sections) {
                        VBox sectionCard = new VBox(6);
                        sectionCard.getStyleClass().add("details-ai-card");

                        Label sectionTitle = new Label(safe(section.title, "Suggestions"));
                        sectionTitle.getStyleClass().add("details-ai-card-title");
                        sectionCard.getChildren().add(sectionTitle);

                        for (String item : section.items) {
                            Label line = new Label("• " + safe(item, ""));
                            line.setWrapText(true);
                            line.getStyleClass().add("details-ai-card-line");
                            sectionCard.getChildren().add(line);
                        }
                        aiSectionsContainer.getChildren().add(sectionCard);
                    }
                    refreshMapMarker();
                }));
    }

    @FXML
    private void goBackToList() {
        try {
            stopAutoCarousel();
            ensureCurrentUserLoaded();
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/fxml/user/AccommodationsView.fxml"))
            );
            Parent root = loader.load();
            AccommodationsController controller = loader.getController();
            if (controller != null && currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            if (backToListButton != null && backToListButton.getScene() != null) {
                backToListButton.getScene().setRoot(root);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to return to accommodations list.");
            alert.showAndWait();
        }
    }

    private void loadMainImage(String imagePath) {
        Image image = loadImageByPath(imagePath);
        if (image == null || image.isError()) {
            mainImageView.setImage(null);
            mainImageView.setStyle("-fx-background-color: linear-gradient(to bottom right, #E2E8F0, #DBEAFE); -fx-background-radius: 14;");
        } else {
            mainImageView.setImage(image);
            mainImageView.setStyle("");
        }
    }

    private Image loadImageByPath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return null;

        try {
            String cleanPath = imagePath.trim();
            File absoluteFile = new File(cleanPath);
            if (absoluteFile.exists() && absoluteFile.isFile()) {
                return new Image(new FileInputStream(absoluteFile));
            }

            Path projectRelativePath = Paths.get(PROJECT_ROOT, cleanPath);
            if (Files.exists(projectRelativePath) && Files.isRegularFile(projectRelativePath)) {
                return new Image(new FileInputStream(projectRelativePath.toFile()));
            }

            if (cleanPath.startsWith("/")) {
                String withoutLeadingSlash = cleanPath.substring(1);
                Path noSlashPath = Paths.get(PROJECT_ROOT, withoutLeadingSlash);
                if (Files.exists(noSlashPath) && Files.isRegularFile(noSlashPath)) {
                    return new Image(new FileInputStream(noSlashPath.toFile()));
                }
            }

            String platformPath = cleanPath.replace("/", File.separator).replace("\\", File.separator);
            Path platformSpecific = Paths.get(PROJECT_ROOT, platformPath);
            if (Files.exists(platformSpecific) && Files.isRegularFile(platformSpecific)) {
                return new Image(new FileInputStream(platformSpecific.toFile()));
            }

            var resource = getClass().getResource(cleanPath.startsWith("/") ? cleanPath : "/" + cleanPath);
            if (resource != null) {
                return new Image(resource.toExternalForm());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String buildStars(int stars) {
        int bounded = Math.max(0, Math.min(5, stars));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bounded; i++) sb.append("★");
        for (int i = bounded; i < 5; i++) sb.append("☆");
        return sb.toString();
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeJs(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    private void openAccommodationBookingDialog(Room room) {
        if (room == null || accommodation == null) {
            return;
        }

        if (!room.isAvailable()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Room unavailable");
            alert.setHeaderText(null);
            alert.setContentText("This room is marked as unavailable.");
            alert.showAndWait();
            return;
        }

        int bookingUserId = resolveBookingUserId();
        if (bookingUserId <= 0) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/fxml/user/accommodation-booking-dialog.fxml"))
            );
            VBox bookingDialogRoot = loader.load();
            UserAccommodationBookingController controller = loader.getController();
            controller.setContext(accommodation, room, bookingUserId);
            controller.setOnBookingCreated(this::renderRoomsTable);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            if (backToListButton != null && backToListButton.getScene() != null && backToListButton.getScene().getWindow() != null) {
                dialog.initOwner(backToListButton.getScene().getWindow());
            }
            dialog.setScene(new Scene(bookingDialogRoot));
            dialog.setResizable(false);
            dialog.centerOnScreen();
            dialog.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Booking error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open booking dialog.\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    private int resolveBookingUserId() {
        ensureCurrentUserLoaded();
        if (currentUser != null && currentUser.getUserId() > 0) {
            return currentUser.getUserId();
        }

        int sessionUserId = SessionManager.getCurrentUserId();
        if (sessionUserId > 0) {
            return sessionUserId;
        }

        TextInputDialog userIdDialog = new TextInputDialog();
        userIdDialog.setTitle("User Required");
        userIdDialog.setHeaderText("Enter your user id to continue booking");
        userIdDialog.setContentText("User ID:");

        Optional<String> result = userIdDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int parsed = Integer.parseInt(result.get().trim());
                if (parsed > 0) {
                    SessionManager.setCurrentUserId(parsed);
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
                // Handled by warning below.
            }
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid user id");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a valid numeric user id.");
        alert.showAndWait();
        return -1;
    }

    // ========== NAVIGATION METHODS ==========

    @FXML
    private void handleHomeNav(MouseEvent event) {
        navigateTo("/fxml/user/home.fxml");
    }

    @FXML
    private void handleDestinationsNav(MouseEvent event) {
        navigateTo("/fxml/user/user_destinations.fxml");
    }

    @FXML
    private void handleActivitiesNav(MouseEvent event) {
        navigateTo("/fxml/user/user_activities.fxml");
    }

    @FXML
    private void handleTransportNav(MouseEvent event) {
        navigateTo("/fxml/user/TransportUserInterface.fxml");
    }

    @FXML
    private void handleBlogNav(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming soon");
        alert.setHeaderText(null);
        alert.setContentText("Blog page coming soon!");
        alert.showAndWait();
    }

    @FXML
    private void handleProfile(MouseEvent event) {
        navigateTo("/fxml/user/profile.fxml");
    }

    @FXML
    private void handleMyBookings(ActionEvent event) {
        navigateTo("/fxml/user/my_bookings.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        navigateTo("/fxml/user/login.fxml");
    }

    private void navigateTo(String fxmlPath) {
        if (backToListButton == null || backToListButton.getScene() == null) return;
        try {
            ensureCurrentUserLoaded();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object targetController = loader.getController();

            if (currentUser != null) {
                if (targetController instanceof HomeController homeController) {
                    homeController.setUser(currentUser);
                } else if (targetController instanceof UserDestinationsController destinationsController) {
                    destinationsController.setCurrentUser(currentUser);
                } else if (targetController instanceof UserActivitiesController activitiesController) {
                    activitiesController.setCurrentUser(currentUser);
                } else if (targetController instanceof UserBookingsController bookingsController) {
                    bookingsController.setCurrentUser(currentUser);
                } else if (targetController instanceof AccommodationsController accommodationsController) {
                    accommodationsController.setCurrentUser(currentUser);
                } else if (targetController instanceof ProfileController profileController) {
                    profileController.setUser(currentUser);
                } else if (targetController instanceof TransportUserInterfaceController transportController) {
                    transportController.setCurrentUser(currentUser);
                }
            }

            Parent currentRoot = backToListButton.getScene().getRoot();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(140), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                backToListButton.getScene().setRoot(root);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(170), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open " + fxmlPath + ".");
            alert.showAndWait();
        }
    }

    private String safeName(String text) {
        return text == null ? "" : text.trim();
    }

    private void applyAvatarGraphic() {
        if (avatarInitials == null || currentUser == null) return;
        String avatarId = currentUser.getAvatarId();
        if (avatarId == null || !avatarId.contains(":")) {
            avatarInitials.setGraphic(null);
            return;
        }
        String[] parts = avatarId.split(":");
        if (parts.length < 2) {
            avatarInitials.setGraphic(null);
            return;
        }
        String url = "https://api.dicebear.com/9.x/" + parts[0] + "/png?seed=" + parts[1] + "&size=40";
        Image image = new Image(url, 36, 36, true, true, true);
        ImageView view = new ImageView(image);
        view.setFitWidth(36);
        view.setFitHeight(36);
        view.setPreserveRatio(true);
        avatarInitials.setText("");
        avatarInitials.setGraphic(view);
    }

    private void ensureCurrentUserLoaded() {
        if (currentUser != null) {
            return;
        }
        int userId = SessionManager.getCurrentUserId();
        if (userId > 0) {
            User sessionUser = userService.findById(userId);
            if (sessionUser != null) {
                setCurrentUser(sessionUser);
            }
        }
    }

    private static class GalleryImage {
        final String path;
        final String caption;

        GalleryImage(String path, String caption) {
            this.path = path;
            this.caption = caption;
        }
    }
}