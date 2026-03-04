package tn.esprit.controllers.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import netscape.javascript.JSObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import tn.esprit.entities.Comments;
import tn.esprit.entities.Posts;
import tn.esprit.entities.Story;
import tn.esprit.entities.TravelStory;
import tn.esprit.entities.User;
import tn.esprit.entities.Country;
import tn.esprit.services.*;
import tn.esprit.services.FollowingsService;
import tn.esprit.services.SavedPostsService;
import tn.esprit.utils.SessionManager;
import tn.esprit.utils.ApiConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogController implements Initializable {

    // Top nav bar (same as Home / Packs & Offers)
    @FXML private Label navAvatarInitials;
    @FXML private ImageView navUserAvatarView;
    @FXML private Label navUserNameLabel;

    // Top/search
    @FXML private TextField searchField;

    // Sidebar
    @FXML private Label profileInitialsLabel;
    @FXML private ImageView profileImageView;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label postsCountLabel;
    @FXML private Label followersCountLabel;
    @FXML private Label followingCountLabel;
    @FXML private VBox suggestionsContainer;
    @FXML private Label statPostsLabel;
    @FXML private Label statFollowersLabel;
    @FXML private Label statFollowingLabel;
    @FXML private Label statPostsCreatedLabel;
    @FXML private Label statStoriesCreatedLabel;
    @FXML private VBox peakHoursChartContainer;
    @FXML private Button chipAll;
    @FXML private Button chipStory;
    @FXML private Button chipReview;
    @FXML private Button chipAdvice;
    @FXML private Button chipInquiry;
    @FXML private Button chipTravelStory;

    private String currentFeedFilter = "all";

    // Instagram stories row (bubbles)
    @FXML private HBox storiesRow;
    @FXML private VBox storiesSection;
    @FXML private ScrollPane storiesScroll;

    // Main content containers
    @FXML private VBox feedContainer;
    @FXML private VBox myPostsContainer;
    @FXML private VBox myStoriesContainer;
    @FXML private VBox savedContainer;

    // View switch: only one scroll pane is shown at a time (so it gets bounded height and scrollbar works)
    @FXML private StackPane contentStack;
    @FXML private ScrollPane feedScroll;
    @FXML private ScrollPane myPostsScroll;
    @FXML private ScrollPane myStoriesScroll;
    @FXML private ScrollPane savedScroll;
    @FXML private ScrollPane statsScroll;
    @FXML private VBox statsPageContainer;

    // Modals / overlays
    @FXML private Pane modalDimmer;

    @FXML private VBox createPostForm;
    @FXML private VBox createStoryForm;   // ✅ matches blog.fxml
    @FXML private VBox editPostForm;
    @FXML private VBox editStoryForm;

    // Create Post fields
    @FXML private TextField postTitleField;
    @FXML private TextArea postContentField;
    @FXML private Label postImageNameLabel;
    @FXML private ImageView postImagePreview;
    @FXML private Label postImagesInfoLabel;
    @FXML private FlowPane postImagesPreview;

    // Create TravelStory fields (summary-only)
    @FXML private TextField storyTitleField;
    @FXML private javafx.scene.control.ComboBox<Country> storyCountryCombo;
    @FXML private TextField storyDestinationField;
    @FXML private TextArea storySummaryField;
    @FXML private Button generateSummaryBtn;
    @FXML private TextArea storyTipsField;
    @FXML private DatePicker storyStartDatePicker;
    @FXML private DatePicker storyEndDatePicker;
    @FXML private ChoiceBox<String> storyTravelTypeChoice;
    @FXML private ChoiceBox<String> storyTravelStyleChoice;
    @FXML private CheckBox storyWouldRecommendCheck;
    @FXML private CheckBox storyWouldGoAgainCheck;

    // ✅ Multi-images (Create)
    @FXML private Label storyImagesInfoLabel;
    @FXML private FlowPane storyImagesPreview;

    // ✅ Clickable stars (Create)
    @FXML private HBox storyRatingStars;

    // Edit Post fields
    @FXML private TextField editPostTitleField;
    @FXML private TextArea editPostContentField;
    @FXML private Label editPostImagesInfoLabel;
    @FXML private FlowPane editPostImagesPreview;

    // Edit TravelStory fields
    @FXML private TextField editStoryTitleField;
    @FXML private javafx.scene.control.ComboBox<Country> editStoryCountryCombo;
    @FXML private TextField editStoryDestinationField;
    @FXML private TextArea editStorySummaryField;
    @FXML private TextArea editStoryTipsField;
    @FXML private DatePicker editStoryStartDatePicker;
    @FXML private DatePicker editStoryEndDatePicker;
    @FXML private ChoiceBox<String> editStoryTravelTypeChoice;
    @FXML private ChoiceBox<String> editStoryTravelStyleChoice;
    @FXML private CheckBox editStoryWouldRecommendCheck;
    @FXML private CheckBox editStoryWouldGoAgainCheck;

    // ✅ Multi-images (Edit)
    @FXML private Label editStoryImagesInfoLabel;
    @FXML private FlowPane editStoryImagesPreview;

    // ✅ Clickable stars (Edit)
    @FXML private HBox editStoryRatingStars;

    // Instagram Story modal
    @FXML private VBox createInstaStoryForm;
    @FXML private Label instaStoryImageNameLabel;
    @FXML private ImageView instaStoryImagePreview;
    @FXML private TextField instaStoryCaptionField;

    // Services
    private PostService postService;
    private TravelStoryService travelStoryService;
    private CommentService commentService;
    private UserService userService;
    private ReactionsService reactionsService;
    private StoryService storyService;
    private SharesService sharesService;
    private CountryService countryService;
    private FollowingsService followingsService;
    private SavedPostsService savedPostsService;

    // State
    private User currentUser;
    private final List<File> selectedPostImages = new ArrayList<>();
    private final List<File> selectedEditPostImages = new ArrayList<>();
    private File selectedInstaStoryImageFile;
    private Posts editingPost;
    private TravelStory editingStory;

    // ✅ Ratings (stars)
    private int createStoryRating = 5;
    private int editStoryRating = 5;

    // ✅ Selected images lists
    private final List<File> selectedStoryImages = new ArrayList<>();
    private final List<File> selectedEditStoryImages = new ArrayList<>();

    private boolean mapPickerForEdit = false;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    private static final SimpleDateFormat DATE_ONLY = new SimpleDateFormat("MMM dd, yyyy");
    private static final PseudoClass FILLED = PseudoClass.getPseudoClass("filled");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        postService = new PostService();
        travelStoryService = new TravelStoryService();
        commentService = new CommentService();
        userService = new UserService();
        reactionsService = new ReactionsService();
        storyService = new StoryService();
        sharesService = new SharesService();
        countryService = new CountryService();
        followingsService = new FollowingsService();
        savedPostsService = new SavedPostsService();

        setupTravelStoryFormControls();
        setupCountryCombos();
        setupFilterChips();

        hideAllForms();
        resetCreatePostForm();
        resetCreateStoryForm();
        resetInstaStoryForm();

        showOnly(feedScroll);
        loadStoriesBubbles();

        if (feedScroll != null) {
            feedScroll.vvalueProperty().addListener((obs, oldV, newV) -> updateStoriesFade(newV.doubleValue()));
            updateStoriesFade(feedScroll.getVvalue());
        }
    }

    // =========================================================
    // TravelStory form setup (choices + clickable stars + previews)
    // =========================================================
    private void setupTravelStoryFormControls() {
        List<String> types = Arrays.asList("Leisure", "Business", "Adventure", "Family", "Solo", "Road Trip", "Honeymoon", "Other");
        List<String> styles = Arrays.asList("Budget", "Mid-range", "Luxury", "Backpacking", "Eco", "Cultural", "Food", "Other");

        if (storyTravelTypeChoice != null) storyTravelTypeChoice.getItems().setAll(types);
        if (storyTravelStyleChoice != null) storyTravelStyleChoice.getItems().setAll(styles);
        if (editStoryTravelTypeChoice != null) editStoryTravelTypeChoice.getItems().setAll(types);
        if (editStoryTravelStyleChoice != null) editStoryTravelStyleChoice.getItems().setAll(styles);

        if (storyTravelTypeChoice != null) storyTravelTypeChoice.setValue("Leisure");
        if (storyTravelStyleChoice != null) storyTravelStyleChoice.setValue("Mid-range");

        if (storyWouldRecommendCheck != null) storyWouldRecommendCheck.setSelected(true);
        if (storyWouldGoAgainCheck != null) storyWouldGoAgainCheck.setSelected(false);

        initStarRating(storyRatingStars, 5, v -> createStoryRating = v);
        initStarRating(editStoryRatingStars, 5, v -> editStoryRating = v);

        refreshImagesUI(storyImagesPreview, storyImagesInfoLabel, selectedStoryImages);
        refreshImagesUI(editStoryImagesPreview, editStoryImagesInfoLabel, selectedEditStoryImages);
    }

    private void setupCountryCombos() {
        List<Country> countries = countryService.getAll();
        javafx.collections.ObservableList<Country> items =
                javafx.collections.FXCollections.observableArrayList(countries);

        if (storyCountryCombo != null) {
            storyCountryCombo.setEditable(true); // so map-picked country name can be shown when no Country match
            Platform.runLater(() -> bindCountryCombo(storyCountryCombo, items));
        }
        if (editStoryCountryCombo != null) {
            editStoryCountryCombo.setEditable(true);
            Platform.runLater(() -> bindCountryCombo(editStoryCountryCombo, items));
        }
    }

    private void bindCountryCombo(javafx.scene.control.ComboBox<Country> combo,
                                  javafx.collections.ObservableList<Country> baseItems) {
        combo.setItems(baseItems);
        combo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Country c) { return c == null ? "" : c.getName(); }
            @Override public Country fromString(String s) {
                if (s == null) return null;
                String q = s.trim().toLowerCase();
                for (Country c : baseItems) {
                    if (c.getName().equalsIgnoreCase(q) || c.getAlpha2().equalsIgnoreCase(q)) return c;
                }
                for (Country c : baseItems) { // startsWith fallback
                    if (c.getName().toLowerCase().startsWith(q)) return c;
                }
                return null;
            }
        });
        combo.setCellFactory(listView -> new ListCell<>() {
            private final ImageView iv = new ImageView();
            @Override protected void updateItem(Country c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) { setText(null); setGraphic(null); return; }
                setText(c.getName() + " (" + c.getAlpha2() + ")");
                iv.setFitWidth(20); iv.setFitHeight(14); iv.setPreserveRatio(true);
                try { iv.setImage(new Image(c.getFlagPngUrl(20), true)); } catch (Exception ignored) {}
                setGraphic(iv);
            }
        });
        combo.setButtonCell(new ListCell<>() {
            private final ImageView iv = new ImageView();
            @Override protected void updateItem(Country c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) { setText(null); setGraphic(null); return; }
                setText(null);
                iv.setFitWidth(20); iv.setFitHeight(14); iv.setPreserveRatio(true);
                try { iv.setImage(new Image(c.getFlagPngUrl(20), true)); } catch (Exception ignored) {}
                setGraphic(new HBox(6, iv, new Label(c.getName())));
            }
        });
    }

    private interface IntConsumer { void accept(int v); }

    private void initStarRating(HBox box, int initial, IntConsumer onChange) {
        if (box == null) return;

        box.getChildren().clear();
        final int[] value = { clampRating(initial) };
        onChange.accept(value[0]);

        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            Label star = new Label("★");
            star.getStyleClass().add("rating-star");
            star.setFocusTraversable(false);

            star.setOnMouseEntered(e -> renderStars(box, starValue));
            star.setOnMouseExited(e -> renderStars(box, value[0]));
            star.setOnMouseClicked(e -> {
                value[0] = starValue;
                onChange.accept(value[0]);
                renderStars(box, value[0]);
            });

            box.getChildren().add(star);
        }
        renderStars(box, value[0]);
    }

    private void setStarRating(HBox box, int rating) {
        if (box == null) return;
        renderStars(box, clampRating(rating));
    }

    private void renderStars(HBox box, int rating) {
        int r = clampRating(rating);
        for (int i = 0; i < box.getChildren().size(); i++) {
            if (!(box.getChildren().get(i) instanceof Label star)) continue;
            boolean filled = (i + 1) <= r;
            star.pseudoClassStateChanged(FILLED, filled);
        }
    }

    private int clampRating(int r) {
        return Math.max(1, Math.min(5, r));
    }

    // =========================================================
    // User/session
    // =========================================================
    public void setUser(User user) {
        this.currentUser = user;
        if (currentUser != null && currentUser.getUserId() > 0) {
            SessionManager.setCurrentUserId(currentUser.getUserId());
        }
        updateNavBarAvatar();
        refreshAll();
    }

    private void updateNavBarAvatar() {
        if (navUserNameLabel != null && currentUser != null) {
            String name = (safe(currentUser.getFirstName()) + " " + safe(currentUser.getLastName())).trim();
            navUserNameLabel.setText(name.isEmpty() ? "User" : name);
        }
        if (navAvatarInitials != null && currentUser != null) {
            String first = currentUser.getFirstName() != null && !currentUser.getFirstName().isBlank()
                    ? currentUser.getFirstName().substring(0, 1).toUpperCase() : "";
            String last = currentUser.getLastName() != null && !currentUser.getLastName().isBlank()
                    ? currentUser.getLastName().substring(0, 1).toUpperCase() : "";
            navAvatarInitials.setText((first + last).isBlank() ? "U" : (first + last));
        }
        if (currentUser != null && navUserAvatarView != null && navAvatarInitials != null) {
            String avatarId = currentUser.getAvatarId();
            if (avatarId != null && avatarId.contains(":")) {
                String[] parts = avatarId.split(":", 2);
                if (parts.length == 2 && "emoji".equals(parts[0])) {
                    navAvatarInitials.setText(parts[1]);
                    navUserAvatarView.setVisible(false);
                    navUserAvatarView.setManaged(false);
                } else if (parts.length == 2 && "url".equals(parts[0])) {
                    try {
                        navUserAvatarView.setImage(new Image(parts[1], true));
                        navUserAvatarView.setVisible(true);
                        navUserAvatarView.setManaged(true);
                        navAvatarInitials.setVisible(false);
                        navAvatarInitials.setManaged(false);
                    } catch (Exception ignored) {}
                } else if (parts.length == 2) {
                    // DiceBear-style avatar (e.g. avataaars:seed) — same as Home/Transport
                    String style = parts[0];
                    String seed = parts[1];
                    String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed + "&size=40&backgroundColor=4cccad";
                    ImageView finalView = navUserAvatarView;
                    Label finalInitials = navAvatarInitials;
                    new Thread(() -> {
                        try {
                            Image img = new Image(avatarUrl, 40, 40, true, true, true);
                            Platform.runLater(() -> {
                                if (finalView != null && !img.isError()) {
                                    finalView.setImage(img);
                                    finalView.setClip(new Circle(20, 20, 20));
                                    finalView.setVisible(true);
                                    finalView.setManaged(true);
                                    if (finalInitials != null) {
                                        finalInitials.setVisible(false);
                                        finalInitials.setManaged(false);
                                    }
                                }
                            });
                        } catch (Exception ignored) {}
                    }).start();
                }
            }
        }
    }

    // =========================================================
    // Top-bar navigation (to other modules)
    // =========================================================
    @FXML private void handleHomeNav(MouseEvent e) { navigateTo("/fxml/user/home.fxml"); }
    @FXML private void handleDestinationsNav(MouseEvent e) { navigateTo("/fxml/user/user_destinations.fxml"); }
    @FXML private void handleAccommodationsNav(MouseEvent e) { navigateTo("/fxml/user/AccommodationsView.fxml"); }
    @FXML private void handleActivitiesNav(MouseEvent e) { navigateTo("/fxml/user/user_activities.fxml"); }
    @FXML private void handleTransportNav(MouseEvent e) { navigateTo("/fxml/user/TransportUserInterface.fxml"); }
    @FXML private void handlePacksOffersNav(MouseEvent e) { navigateTo("/fxml/user/UserPacksOffersView.fxml"); }
    @FXML private void handleProfileNav(MouseEvent e) { navigateTo("/fxml/user/profile.fxml"); }
    @FXML private void handleMyBookingsNav(ActionEvent e) { navigateTo("/fxml/user/my_bookings.fxml"); }
    @FXML private void handleLogout(ActionEvent e) {
        Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO).showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            SessionManager.setCurrentUserId(-1);
            navigateTo("/fxml/user/login.fxml");
        }
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object ctrl = loader.getController();

            if (currentUser != null && ctrl != null) {
                if (ctrl instanceof HomeController c) c.setUser(currentUser);
                else if (ctrl instanceof UserDestinationsController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof UserActivitiesController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof AccommodationsController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof TransportUserInterfaceController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof UserPacksOffersController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof UserBookingsController c) c.setCurrentUser(currentUser);
                else if (ctrl instanceof ProfileController c) c.setUser(currentUser);
                else if (ctrl instanceof BlogController c) c.setUser(currentUser);
            }

            // Use any node in the current blog view to get the window (top bar label or main content)
            Node anchor = navUserNameLabel != null ? navUserNameLabel : (searchField != null ? searchField : feedContainer);
            if (anchor == null) return;
            Scene scene = anchor.getScene();
            if (scene == null) return;
            Window window = scene.getWindow();
            if (!(window instanceof Stage)) return;
            Stage stage = (Stage) window;
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root, w, h));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Navigation error: " + ex.getMessage());
        }
    }

    // =========================================================
    // Sidebar Navigation
    // =========================================================
    @FXML public void goFeed() { showOnly(feedScroll); refreshFeed(); if (feedScroll != null) updateStoriesFade(feedScroll.getVvalue()); }
    @FXML public void goExplore() { showOnly(feedScroll); refreshFeed(); if (feedScroll != null) updateStoriesFade(feedScroll.getVvalue()); }
    @FXML public void goStories() { showOnly(myStoriesScroll); refreshMyStories(); } // TravelStory list
    @FXML public void goSaved() { showOnly(savedScroll); refreshSaved(); }
    @FXML public void goStats() { showOnly(statsScroll); refreshStatsPage(); }

    private void showOnly(ScrollPane target) {
        if (feedScroll != null) { feedScroll.setVisible(false); feedScroll.setManaged(false); }
        if (myPostsScroll != null) { myPostsScroll.setVisible(false); myPostsScroll.setManaged(false); }
        if (myStoriesScroll != null) { myStoriesScroll.setVisible(false); myStoriesScroll.setManaged(false); }
        if (savedScroll != null) { savedScroll.setVisible(false); savedScroll.setManaged(false); }
        if (statsScroll != null) { statsScroll.setVisible(false); statsScroll.setManaged(false); }

        if (target != null) {
            target.setVisible(true);
            target.setManaged(true);
            if (contentStack != null) target.toFront(); // so visible pane is on top and receives events
        }

        // Hide Stories row on My Activity page; show it on Feed and other views
        if (storiesSection != null) {
            boolean onStatsPage = (target == statsScroll);
            storiesSection.setVisible(!onStatsPage);
            storiesSection.setManaged(!onStatsPage);
        }
    }

    private void updateStoriesFade(double v) {
        if (storiesSection == null) return;
        double factor = Math.min(1.0, v * 12.0);
        double opacity = Math.max(0.0, 1.0 - factor * 1.0);
        storiesSection.setOpacity(opacity);

        if (storiesScroll != null) {
            double base = 150.0;
            double h = Math.max(0.0, base * (1.0 - factor));
            storiesScroll.setPrefHeight(h);
            storiesScroll.setMinHeight(h);
            storiesScroll.setMaxHeight(h);
        }

        boolean show = opacity > 0.02;
        storiesSection.setVisible(show);
        storiesSection.setManaged(show);
    }

    // =========================================================
    // Refresh
    // =========================================================
    private void refreshAll() {
        refreshFeed();
        refreshMyPosts();
        refreshMyStories();
        refreshSaved();
        updateSidebar();
        loadStoriesBubbles();
        loadSuggestions();
        updateRightStats();
    }

    private void refreshFeed() {
        if (feedContainer == null) return;
        feedContainer.getChildren().clear();

        List<Object> items = new ArrayList<>();
        if ("Travel Stories".equals(currentFeedFilter)) {
            items.addAll(safeList(travelStoryService.findAll()));
        } else {
            List<Posts> posts = safeList(postService.findAll());
            if (!"all".equals(currentFeedFilter)) {
                String typeFilter = currentFeedFilter.toLowerCase();
                posts = posts.stream().filter(p -> typeFilter.equals(safe(p.getType()))).toList();
            }
            items.addAll(posts);
            if ("all".equals(currentFeedFilter))
                items.addAll(safeList(travelStoryService.findAll()));
        }

        items.sort((a, b) -> {
            Date da = getCreatedAt(a);
            Date db = getCreatedAt(b);
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });

        for (Object it : items) {
            if (it instanceof Posts p) feedContainer.getChildren().add(buildPostCard(p));
            if (it instanceof TravelStory s) feedContainer.getChildren().add(buildTravelStoryCard(s));
        }
    }

    private void setupFilterChips() {
        if (chipAll != null) chipAll.setUserData("all");
        if (chipStory != null) chipStory.setUserData("story");
        if (chipReview != null) chipReview.setUserData("review");
        if (chipAdvice != null) chipAdvice.setUserData("advice");
        if (chipInquiry != null) chipInquiry.setUserData("inquiry");
        if (chipTravelStory != null) chipTravelStory.setUserData("Travel Stories");
        applyFilterChipStyle();
    }

    @FXML
    private void filterFeedByChip(javafx.event.ActionEvent e) {
        if (!(e.getSource() instanceof Button btn)) return;
        Object data = btn.getUserData();
        if (data instanceof String) {
            currentFeedFilter = (String) data;
            applyFilterChipStyle();
            refreshFeed();
        }
    }

    private void applyFilterChipStyle() {
        String active = "-fx-background-color: linear-gradient(to right, #A78BFA, #F472B6); -fx-text-fill: white; -fx-font-weight: 700;";
        String inactive = "-fx-background-color: rgba(167,139,250,0.15); -fx-text-fill: #6D28D9; -fx-font-weight: 600;";
        for (Button b : new Button[]{ chipAll, chipStory, chipReview, chipAdvice, chipInquiry, chipTravelStory }) {
            if (b == null) continue;
            Object data = b.getUserData();
            boolean isActive = (data != null && data.toString().equals(currentFeedFilter));
            b.setStyle(isActive ? active : inactive);
        }
    }

    @FXML
    private void handleOpenMap() {
        try {
            Stage mapStage = new Stage();
            mapStage.setTitle("Map");
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();
            java.net.URL url = getClass().getResource("/map.html");
            if (url != null) engine.load(url.toExternalForm());
            mapStage.setScene(new Scene(webView, 900, 600));
            mapStage.show();
        } catch (Exception ex) {
            showAlert("Could not open map: " + ex.getMessage());
        }
    }

    @FXML
    private void handlePickDestinationOnMap() {
        mapPickerForEdit = false;
        openMapPicker();
    }

    @FXML
    private void handleGenerateStorySummary() {
        String title = storyTitleField != null ? safe(storyTitleField.getText()).trim() : "";
        String country = "";
        if (storyCountryCombo != null) {
            Country sel = storyCountryCombo.getSelectionModel().getSelectedItem();
            country = sel != null ? sel.getName() : safe(storyCountryCombo.getEditor() != null ? storyCountryCombo.getEditor().getText() : "").trim();
        }
        if (title.isEmpty() || country.isEmpty()) {
            showAlert("Please enter a title and select a destination country first.");
            return;
        }
        String apiKey = ApiConfig.GROQ_API_KEY;
        if (apiKey == null || apiKey.isBlank()) {
            showAlert("Groq API key is not configured. Add GROQ_API_KEY to your .env file.");
            return;
        }
        if (generateSummaryBtn != null) {
            generateSummaryBtn.setDisable(true);
            generateSummaryBtn.setText("Generating…");
        }
        String prompt = "Write a short, engaging travel story summary in 2 to 4 sentences for a trip titled \"" + title + "\" in " + country + ". Write only the summary paragraph, no bullet points or headings.";
        new Thread(() -> {
            try {
                String summary = fetchSummaryFromGroq(apiKey, prompt);
                Platform.runLater(() -> {
                    if (storySummaryField != null && summary != null && !summary.isBlank())
                        storySummaryField.setText(summary.trim());
                    if (generateSummaryBtn != null) {
                        generateSummaryBtn.setDisable(false);
                        generateSummaryBtn.setText("✨ Generate with AI");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Could not generate summary: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    if (generateSummaryBtn != null) {
                        generateSummaryBtn.setDisable(false);
                        generateSummaryBtn.setText("✨ Generate with AI");
                    }
                });
            }
        }).start();
    }

    private static final String GROQ_MODEL = "llama-3.1-8b-instant";

    private String fetchSummaryFromGroq(String apiKey, String userPrompt) throws Exception {
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", userPrompt);
        JsonArray messages = new JsonArray();
        messages.add(message);
        JsonObject payload = new JsonObject();
        payload.addProperty("model", GROQ_MODEL);
        payload.add("messages", messages);
        payload.addProperty("max_tokens", 256);
        payload.addProperty("temperature", 0.7);
        String body = new Gson().toJson(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.GROQ_CHAT_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(15)).build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
        if (res.statusCode() != 200) {
            String errBody = res.body() != null && !res.body().isBlank() ? res.body() : "";
            throw new RuntimeException("Groq API returned " + res.statusCode() + (errBody.isEmpty() ? "" : ": " + errBody));
        }
        JsonObject root = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!root.has("choices") || !root.get("choices").isJsonArray() || root.getAsJsonArray("choices").size() == 0)
            throw new RuntimeException("No response from Groq");
        JsonObject choice = root.getAsJsonArray("choices").get(0).getAsJsonObject();
        if (!choice.has("message") || !choice.get("message").isJsonObject())
            throw new RuntimeException("Invalid Groq response");
        String content = choice.getAsJsonObject("message").has("content") ? choice.getAsJsonObject("message").get("content").getAsString() : "";
        return content != null ? content.trim() : "";
    }

    @FXML
    private void handlePickDestinationOnMapEdit() {
        mapPickerForEdit = true;
        openMapPicker();
    }

    /**
     * Map picker: same pattern as Accommodation module — use map-picker.html,
     * Dialog, bridge onMapClicked(String, String), initMapPicker + fixMapSize after load.
     */
    private void openMapPicker() {
        Dialog<Void> mapDialog = new Dialog<>();
        mapDialog.initModality(Modality.APPLICATION_MODAL);
        Stage owner = null;
        if (feedScroll != null && feedScroll.getScene() != null && feedScroll.getScene().getWindow() != null)
            owner = (Stage) feedScroll.getScene().getWindow();
        if (owner != null) mapDialog.initOwner(owner);
        mapDialog.setTitle("Pick destination on map");
        mapDialog.setHeaderText("Click on the map to select a location");
        mapDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        WebView webView = new WebView();
        webView.setPrefSize(860, 560);
        VBox wrapper = new VBox(10);
        wrapper.setPadding(new Insets(8));
        wrapper.getChildren().add(webView);
        mapDialog.getDialogPane().setContent(wrapper);
        mapDialog.getDialogPane().setPrefSize(900, 650);

        WebEngine engine = webView.getEngine();
        double defaultLat = 36.8065;
        double defaultLng = 10.1815;

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) engine.executeScript("window");
                    if (window != null)
                        window.setMember("javaBridge", new BlogMapPickerBridge(this, mapDialog, mapPickerForEdit));
                    Platform.runLater(() -> {
                        try {
                            engine.executeScript("if(window.initMapPicker){window.initMapPicker(" + defaultLat + "," + defaultLng + ");}");
                            engine.executeScript("if(window.fixMapSize){window.fixMapSize();}");
                        } catch (Exception ignored) {}
                    });
                } catch (Exception e) {
                    showAlert("Map picker bridge error: " + e.getMessage());
                }
            }
        });

        webView.widthProperty().addListener((obs, o, n) -> {
            try { engine.executeScript("if(window.fixMapSize){window.fixMapSize();}"); } catch (Exception ignored) {}
        });
        webView.heightProperty().addListener((obs, o, n) -> {
            try { engine.executeScript("if(window.fixMapSize){window.fixMapSize();}"); } catch (Exception ignored) {}
        });
        mapDialog.setOnShown(event -> Platform.runLater(() -> {
            try { engine.executeScript("if(window.fixMapSize){window.fixMapSize();}"); } catch (Exception ignored) {}
        }));

        java.net.URL url = getClass().getResource("/map-picker.html");
        if (url != null) {
            engine.load(url.toExternalForm());
        } else {
            showAlert("Map picker resource not found: /map-picker.html");
            return;
        }
        mapDialog.showAndWait();
    }

    public void onLocationPickedFromMap(String countryName, String displayName) {
        Platform.runLater(() -> {
            List<Country> countries = countryService != null ? countryService.getAll() : Collections.emptyList();
            String country = (countryName != null) ? countryName.trim() : "";
            String display = (displayName != null) ? displayName.trim() : "";
            if (mapPickerForEdit) {
                if (editStoryCountryCombo != null) {
                    editStoryCountryCombo.getSelectionModel().clearSelection();
                    if (editStoryCountryCombo.getEditor() != null) editStoryCountryCombo.getEditor().setText(country);
                    for (Country c : countries) {
                        if (c.getName() != null && c.getName().equalsIgnoreCase(country)) {
                            editStoryCountryCombo.getSelectionModel().select(c);
                            break;
                        }
                    }
                }
                if (editStoryDestinationField != null) {
                    editStoryDestinationField.setText(display);
                    editStoryDestinationField.setVisible(true);
                    editStoryDestinationField.setManaged(true);
                }
            } else {
                if (storyCountryCombo != null) {
                    storyCountryCombo.getSelectionModel().clearSelection();
                    if (storyCountryCombo.getEditor() != null) storyCountryCombo.getEditor().setText(country);
                    for (Country c : countries) {
                        if (c.getName() != null && c.getName().equalsIgnoreCase(country)) {
                            storyCountryCombo.getSelectionModel().select(c);
                            break;
                        }
                    }
                }
                if (storyDestinationField != null) {
                    storyDestinationField.setText(display);
                    storyDestinationField.setVisible(true);
                    storyDestinationField.setManaged(true);
                }
            }
        });
    }

    private static final class BlogMapPickerBridge {
        private final BlogController controller;
        private final Dialog<Void> mapDialog;
        private final boolean forEdit;

        BlogMapPickerBridge(BlogController controller, Dialog<Void> mapDialog, boolean forEdit) {
            this.controller = controller;
            this.mapDialog = mapDialog;
            this.forEdit = forEdit;
        }

        /**
         * Called from map-picker.html when user clicks the map.
         * Close dialog immediately and do reverse geocoding synchronously on FX thread.
         */
        public void onMapClicked(String latStr, String lngStr) {
            double latitude, longitude;
            try {
                latitude = Double.parseDouble(latStr);
                longitude = Double.parseDouble(lngStr);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    controller.showAlert("Invalid coordinates from map.");
                    if (mapDialog != null) mapDialog.close();
                });
                return;
            }
            final double lat = latitude;
            final double lng = longitude;

            // Close dialog immediately (like Transport)
            Platform.runLater(() -> {
                if (mapDialog != null) mapDialog.close();
                
                // Do reverse geocoding synchronously on FX thread
                String countryName = "";
                String displayName = "";
                try {
                    String url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2"
                            + "&lat=" + java.net.URLEncoder.encode(String.valueOf(lat), java.nio.charset.StandardCharsets.UTF_8)
                            + "&lon=" + java.net.URLEncoder.encode(String.valueOf(lng), java.nio.charset.StandardCharsets.UTF_8)
                            + "&addressdetails=1";
                    
                    HttpClient httpClient = HttpClient.newBuilder()
                            .connectTimeout(java.time.Duration.ofSeconds(10))
                            .build();
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("User-Agent", "TripX-Blog/1.0")
                            .header("Accept", "application/json")
                            .timeout(java.time.Duration.ofSeconds(10))
                            .GET()
                            .build();
                    HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() == 200) {
                        JsonObject root = JsonParser.parseString(res.body()).getAsJsonObject();
                        if (root.has("address") && !root.get("address").isJsonNull()) {
                            JsonObject addr = root.getAsJsonObject("address");
                            countryName = getString(addr, "country");
                        }
                        if (root.has("display_name") && !root.get("display_name").isJsonNull())
                            displayName = root.get("display_name").getAsString();
                    }
                } catch (Exception e) {
                    controller.showAlert("Could not resolve address for this location.");
                }
                
                // Fill the form fields
                controller.onLocationPickedFromMap(countryName, displayName);
            });
        }

        private static String getString(JsonObject object, String key) {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) return "";
            try { return object.get(key).getAsString(); } catch (Exception e) { return ""; }
        }
    }

    private void refreshMyPosts() {
        if (myPostsContainer == null) return;
        myPostsContainer.getChildren().clear();
        if (currentUser == null) return;

        for (Posts p : safeList(postService.findByUserId(currentUser.getUserId()))) {
            myPostsContainer.getChildren().add(buildPostCard(p));
        }
    }

    private void refreshMyStories() {
        if (myStoriesContainer == null) return;
        myStoriesContainer.getChildren().clear();
        if (currentUser == null) return;

        for (TravelStory s : safeList(travelStoryService.findByUserId(currentUser.getUserId()))) {
            myStoriesContainer.getChildren().add(buildTravelStoryCard(s));
        }
    }

    private void refreshSaved() {
        if (savedContainer == null) return;
        savedContainer.getChildren().clear();

        if (currentUser == null) {
            Label msg = new Label("Log in to see saved posts.");
            msg.getStyleClass().add("muted");
            savedContainer.getChildren().add(msg);
            return;
        }

        List<Integer> savedIds = savedPostsService.getSavedPostIds(currentUser.getUserId());
        if (savedIds.isEmpty()) {
            VBox emptyState = new VBox(12);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 60 0;");
            Label icon = new Label("🔖");
            icon.setStyle("-fx-font-size: 48px;");
            Label title = new Label("No saved posts yet");
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #111827;");
            Label sub = new Label("Tap the bookmark icon on any post to save it here.");
            sub.getStyleClass().add("muted");
            emptyState.getChildren().addAll(icon, title, sub);
            savedContainer.getChildren().add(emptyState);
            return;
        }

        // Build a map for O(1) lookup
        List<Posts> allPosts = safeList(postService.findAll());
        System.out.println("[SavedPosts] savedIds=" + savedIds + "  allPosts.size=" + allPosts.size());
        Map<Integer, Posts> postMap = new HashMap<>();
        for (Posts p : allPosts) {
            postMap.put(p.getId(), p);
            System.out.println("[SavedPosts] post in map: id=" + p.getId() + " title=" + p.getTitle());
        }

        for (int id : savedIds) {
            Posts p = postMap.get(id);
            if (p == null) continue;

            VBox wrapper = new VBox(6);
            wrapper.getChildren().add(buildPostCard(p));

            // Unsave button row
            HBox actions = new HBox(8);
            actions.setAlignment(Pos.CENTER_RIGHT);
            actions.setStyle("-fx-padding: 0 4 4 0;");
            Button unsaveBtn = new Button("🔖 Unsave");
            unsaveBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: 700; -fx-font-size: 12px; -fx-background-radius: 12; -fx-padding: 5 14; -fx-cursor: hand;");
            unsaveBtn.setOnAction(e -> {
                savedPostsService.unsave(currentUser.getUserId(), p.getId());
                refreshSaved();
                updateSidebar();
            });
            actions.getChildren().add(unsaveBtn);
            wrapper.getChildren().add(actions);
            savedContainer.getChildren().add(wrapper);
        }
    }

    private void refreshStatsPage() {
        if (statsPageContainer == null) return;
        statsPageContainer.getChildren().clear();
        if (currentUser == null) {
            Label msg = new Label("Log in to see your activity.");
            msg.getStyleClass().add("muted");
            statsPageContainer.getChildren().add(msg);
            return;
        }

        List<Posts> myPosts = safeList(postService.findByUserId(currentUser.getUserId()));
        List<TravelStory> myStories = safeList(travelStoryService.findByUserId(currentUser.getUserId()));
        int postsCreated = myPosts.size();
        int storiesCreated = myStories.size();
        int followers = followingsService.countFollowers(currentUser.getUserId());
        int following = followingsService.countFollowing(currentUser.getUserId());
        int savedCount = savedPostsService.getSavedPostIds(currentUser.getUserId()).size();

        // ── Title ──
        Label title = new Label("📊  My Activity");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #111827; -fx-padding: 0 0 16 0;");
        statsPageContainer.getChildren().add(title);

        // ── Stat cards row (pastel colors, fixed layout so they show properly) ──
        HBox cardsRow = new HBox(14);
        cardsRow.setStyle("-fx-padding: 0 0 20 0; -fx-alignment: CENTER_LEFT;");
        cardsRow.getChildren().addAll(
                buildStatCard("📝", "Posts", String.valueOf(postsCreated), "#E9D5FF", "#7C3AED"),
                buildStatCard("📖", "Stories", String.valueOf(storiesCreated), "#FBCFE8", "#BE185D"),
                buildStatCard("👥", "Followers", String.valueOf(followers), "#BFDBFE", "#1D4ED8"),
                buildStatCard("💜", "Following", String.valueOf(following), "#A7F3D0", "#047857"),
                buildStatCard("🔖", "Saved", String.valueOf(savedCount), "#FDE68A", "#B45309")
        );
        statsPageContainer.getChildren().add(cardsRow);

        // ── Peak Posting Hours bar chart ──
        Label chartTitle1 = new Label("🕐  Peak Posting Hours");
        chartTitle1.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #111827; -fx-padding: 0 0 8 0;");
        statsPageContainer.getChildren().add(chartTitle1);

        List<Date> allDates = new ArrayList<>();
        for (Posts p : myPosts) if (p.getCreatedAt() != null) allDates.add(p.getCreatedAt());
        for (TravelStory s : myStories) {
            Object d = getByAnyGetter(s, "getCreatedAt");
            if (d instanceof Date) allDates.add((Date) d);
        }
        statsPageContainer.getChildren().add(buildPeakHoursChartNode(allDates));

        // ── Posts per day-of-week chart (wrapped so x-axis labels are not clipped) ──
        Label chartTitle2 = new Label("📅  Activity by Day of Week");
        chartTitle2.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #111827; -fx-padding: 20 0 8 0;");
        statsPageContainer.getChildren().add(chartTitle2);
        Node dayChart = buildDayOfWeekChart(allDates);
        VBox.setMargin(dayChart, new Insets(0, 0, 32, 0));
        statsPageContainer.getChildren().add(dayChart);

        // ── Content breakdown ──
        Label breakdownTitle = new Label("📋  Content Breakdown");
        breakdownTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #111827; -fx-padding: 20 0 8 0;");
        statsPageContainer.getChildren().add(breakdownTitle);

        // Count post types
        Map<String, Integer> typeCounts = new LinkedHashMap<>();
        typeCounts.put("Story", 0); typeCounts.put("Review", 0);
        typeCounts.put("Advice", 0); typeCounts.put("Inquiry", 0); typeCounts.put("Other", 0);
        for (Posts p : myPosts) {
            String type = p.getType() != null ? p.getType() : "Other";
            typeCounts.merge(type, 1, Integer::sum);
        }

        HBox breakdown = new HBox(10);
        breakdown.setStyle("-fx-padding: 0 0 20 0;");
        String[] colors = {"#E9D5FF", "#FBCFE8", "#BFDBFE", "#A7F3D0", "#FDE68A"};
        String[] textColors = {"#7C3AED", "#BE185D", "#1D4ED8", "#047857", "#B45309"};
        int ci = 0;
        for (Map.Entry<String, Integer> e : typeCounts.entrySet()) {
            VBox typeCard = new VBox(4);
            typeCard.setAlignment(Pos.CENTER);
            typeCard.setStyle(String.format(
                    "-fx-background-color: %s; -fx-background-radius: 14; -fx-padding: 14 18; -fx-min-width: 90;",
                    colors[ci % colors.length]));
            Label cnt = new Label(String.valueOf(e.getValue()));
            cnt.setStyle(String.format("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: %s;", textColors[ci % textColors.length]));
            Label lbl = new Label(e.getKey());
            lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #374151;");
            typeCard.getChildren().addAll(cnt, lbl);
            breakdown.getChildren().add(typeCard);
            ci++;
        }
        statsPageContainer.getChildren().add(breakdown);

        // ── Recent activity list ──
        Label recentTitle = new Label("🕓  Recent Posts");
        recentTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #111827; -fx-padding: 4 0 8 0;");
        statsPageContainer.getChildren().add(recentTitle);

        List<Posts> recent = new ArrayList<>(myPosts);
        recent.sort((a, b) -> {
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        int shown = 0;
        for (Posts p : recent) {
            if (shown++ >= 5) break;
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10 14; -fx-border-color: #EEF0F3; -fx-border-radius: 12;");
            Label typeTag = new Label(p.getType() != null ? p.getType() : "Post");
            typeTag.setStyle("-fx-background-color: #EDE9FE; -fx-text-fill: #7C3AED; -fx-font-size: 10px; -fx-font-weight: 800; -fx-background-radius: 8; -fx-padding: 3 8;");
            Label postTitle = new Label(p.getTitle() != null ? p.getTitle() : "Untitled");
            postTitle.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: #111827;");
            HBox.setHgrow(postTitle, Priority.ALWAYS);
            Label dateL = new Label(p.getCreatedAt() != null ? new java.text.SimpleDateFormat("MMM d").format(p.getCreatedAt()) : "");
            dateL.getStyleClass().add("muted");
            row.getChildren().addAll(typeTag, postTitle, dateL);
            VBox.setMargin(row, new Insets(0, 0, 6, 0));
            statsPageContainer.getChildren().add(row);
        }
        if (recent.isEmpty()) {
            Label none = new Label("No posts yet. Create your first post!");
            none.getStyleClass().add("muted");
            none.setStyle("-fx-padding: 10;");
            statsPageContainer.getChildren().add(none);
        }
    }

    private VBox buildStatCard(String emoji, String label, String value, String bgColor, String textColor) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 18; -fx-padding: 18 20; -fx-min-width: 110; -fx-pref-width: 120;",
                bgColor));
        Label emojiL = new Label(emoji);
        emojiL.setStyle("-fx-font-size: 26px;");
        Label valueL = new Label(value);
        valueL.setStyle(String.format("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: %s;", textColor));
        Label labelL = new Label(label);
        labelL.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #6B7280;");
        card.getChildren().addAll(emojiL, valueL, labelL);
        return card;
    }

    private Node buildDayOfWeekChart(List<Date> dates) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Posts");
        xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(days));
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.getStyleClass().add("peak-hours-chart");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int[] counts = new int[7];
        if (dates != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (Date d : dates) { cal.setTime(d); counts[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]++; }
        }
        for (int i = 0; i < 7; i++) series.getData().add(new XYChart.Data<>(days[i], counts[i]));
        chart.getData().add(series);
        chart.setPrefHeight(320);
        chart.setMinHeight(300);
        return chart;
    }

    private Node buildPeakHoursChartNode(List<Date> dates) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.getStyleClass().add("peak-hours-chart");
        xAxis.setLabel("Hour");
        yAxis.setLabel("Posts");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int[] counts = new int[24];
        if (dates != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (Date d : dates) {
                cal.setTime(d);
                counts[cal.get(java.util.Calendar.HOUR_OF_DAY)]++;
            }
        }
        for (int h = 0; h < 24; h++)
            series.getData().add(new XYChart.Data<>(String.valueOf(h), counts[h]));
        chart.getData().add(series);
        chart.setPrefHeight(260);
        chart.setMinHeight(240);
        return chart;
    }

    private Date getCreatedAt(Object o) {
        if (o instanceof Posts p) return p.getCreatedAt();
        if (o instanceof TravelStory s) return (Date) getByAnyGetter(s, "getCreatedAt", "getCreated_at");
        return null;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    // =========================================================
    // Sidebar rendering
    // =========================================================
    private void updateSidebar() {
        if (fullNameLabel == null || usernameLabel == null ||
                postsCountLabel == null || followersCountLabel == null || followingCountLabel == null) return;

        if (currentUser == null) {
            fullNameLabel.setText("Guest");
            usernameLabel.setText("@guest");
            postsCountLabel.setText("0");
            followersCountLabel.setText("0");
            followingCountLabel.setText("0");
            return;
        }

        String full = (safe(currentUser.getFirstName()) + " " + safe(currentUser.getLastName())).trim();
        if (full.isEmpty()) full = "User";
        fullNameLabel.setText(full);

        String uname = null;
        try { uname = (String) currentUser.getClass().getMethod("getUsername").invoke(currentUser); }
        catch (Exception ignored) {}
        if (uname == null || uname.trim().isEmpty()) uname = "user" + currentUser.getUserId();
        usernameLabel.setText("@" + uname);

        int myPosts = safeList(postService.findByUserId(currentUser.getUserId())).size();
        int myTravelStories = safeList(travelStoryService.findByUserId(currentUser.getUserId())).size();
        postsCountLabel.setText(String.valueOf(myPosts + myTravelStories));

        followersCountLabel.setText(String.valueOf(
                followingsService.countFollowers(currentUser.getUserId())));
        followingCountLabel.setText(String.valueOf(
                followingsService.countFollowing(currentUser.getUserId())));

        applySidebarAvatar();
    }

    private void applySidebarAvatar() {
        if (profileImageView == null || currentUser == null) return;
        if (profileInitialsLabel != null) {
            String first = currentUser.getFirstName() != null && !currentUser.getFirstName().isBlank()
                    ? currentUser.getFirstName().substring(0, 1).toUpperCase() : "";
            String last = currentUser.getLastName() != null && !currentUser.getLastName().isBlank()
                    ? currentUser.getLastName().substring(0, 1).toUpperCase() : "";
            profileInitialsLabel.setText((first + last).isBlank() ? "?" : (first + last));
        }
        String avatarId = currentUser.getAvatarId();
        if (avatarId != null && avatarId.contains(":")) {
            String[] parts = avatarId.split(":", 2);
            if (parts.length == 2 && "emoji".equals(parts[0])) {
                if (profileInitialsLabel != null) {
                    profileInitialsLabel.setText(parts[1]);
                    profileInitialsLabel.setVisible(true);
                    profileInitialsLabel.setManaged(true);
                }
                profileImageView.setVisible(false);
                profileImageView.setManaged(false);
                return;
            }
            if (parts.length == 2 && "url".equals(parts[0])) {
                try {
                    profileImageView.setImage(new Image(parts[1], true));
                    profileImageView.setVisible(true);
                    profileImageView.setManaged(true);
                    if (profileInitialsLabel != null) {
                        profileInitialsLabel.setVisible(false);
                        profileInitialsLabel.setManaged(false);
                    }
                } catch (Exception ignored) {}
                return;
            }
            if (parts.length == 2) {
                // DiceBear-style avatar (e.g. avataaars:seed)
                String style = parts[0];
                String seed = parts[1];
                String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed + "&size=84&backgroundColor=4cccad";
                ImageView finalView = profileImageView;
                Label finalInitials = profileInitialsLabel;
                new Thread(() -> {
                    try {
                        Image img = new Image(avatarUrl, 84, 84, true, true, true);
                        Platform.runLater(() -> {
                            if (finalView != null && !img.isError()) {
                                finalView.setImage(img);
                                finalView.setClip(new Circle(42, 42, 42));
                                finalView.setVisible(true);
                                finalView.setManaged(true);
                                if (finalInitials != null) {
                                    finalInitials.setVisible(false);
                                    finalInitials.setManaged(false);
                                }
                            }
                        });
                    } catch (Exception ignored) {}
                }).start();
                return;
            }
        }
        profileImageView.setImage(null);
        profileImageView.setVisible(false);
        profileImageView.setManaged(false);
        if (profileInitialsLabel != null) {
            profileInitialsLabel.setVisible(true);
            profileInitialsLabel.setManaged(true);
        }
    }

    // =========================================================
    // Right panel: "Who to Follow" suggestions
    // =========================================================
    private void loadSuggestions() {
        if (suggestionsContainer == null) return;
        suggestionsContainer.getChildren().clear();

        if (currentUser == null) return;

        List<User> allUsers = safeList(userService.getAllUsers());
        int shown = 0;
        for (User u : allUsers) {
            if (u.getUserId() == currentUser.getUserId()) continue;
            suggestionsContainer.getChildren().add(buildSuggestionRow(u));
            if (++shown >= 8) break;
        }

        if (shown == 0) {
            Label empty = new Label("No other users yet.");
            empty.getStyleClass().add("muted");
            suggestionsContainer.getChildren().add(empty);
        }
    }

    private HBox buildSuggestionRow(User u) {
        String name = (safe(u.getFirstName()) + " " + safe(u.getLastName())).trim();
        if (name.isEmpty()) name = "User " + u.getUserId();

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("suggestion-name");
        nameLabel.setMaxWidth(140);

        int uid = u.getUserId();
        boolean following = currentUser != null && followingsService.isFollowing(currentUser.getUserId(), uid);
        Button followBtn = new Button(following ? "Following" : "Follow");
        followBtn.getStyleClass().add(following ? "follow-btn-active" : "follow-btn");
        followBtn.setFocusTraversable(false);
        followBtn.setOnAction(e -> {
            if (currentUser == null) { showAlert("Please log in to follow."); return; }
            if (currentUser.getUserId() == uid) return;
            boolean ok = followingsService.toggleFollow(currentUser.getUserId(), uid);
            if (ok) refreshAll();
            else showAlert("Could not update follow. Try again.");
        });

        HBox row = new HBox(10, nameLabel, new Region(), followBtn);
        HBox.setHgrow(row.getChildren().get(1), Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("suggestion-row");
        row.setCursor(javafx.scene.Cursor.HAND);
        row.setOnMouseClicked(e -> {
            if (e.getClickCount() != 1) return;
            Node target = e.getTarget() instanceof Node ? (Node) e.getTarget() : null;
            for (Node n = target; n != null; n = n.getParent())
                if (n == followBtn) return; // click was on Follow button, don't show popup
            showUserInfoPopup(u);
        });
        return row;
    }

    /** Small popup with user avatar and stats (posts, followers, following). */
    private void showUserInfoPopup(User u) {
        if (u == null || suggestionsContainer == null || suggestionsContainer.getScene() == null) return;
        int uid = u.getUserId();
        String name = (safe(u.getFirstName()) + " " + safe(u.getLastName())).trim();
        if (name.isEmpty()) name = "User " + uid;

        int postsCount = safeList(postService.findByUserId(uid)).size() + safeList(travelStoryService.findByUserId(uid)).size();
        int followersCount = followingsService.countFollowers(uid);
        int followingCount = followingsService.countFollowing(uid);

        Node avatarNode = buildAuthorAvatar(u, 64);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        nameLabel.setMaxWidth(200);
        nameLabel.setWrapText(true);

        Label postsLbl = new Label(postsCount + " posts");
        Label followersLbl = new Label(followersCount + " followers");
        Label followingLbl = new Label(followingCount + " following");
        for (Label l : new Label[]{ postsLbl, followersLbl, followingLbl })
            l.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");

        HBox statsRow = new HBox(16, postsLbl, followersLbl, followingLbl);
        statsRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(12, avatarNode, nameLabel, statsRow);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4); " +
                "-fx-border-color: rgba(99,102,241,0.2); -fx-border-radius: 16; -fx-border-width: 1;");

        Popup popup = new Popup();
        popup.getContent().add(content);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        Window owner = suggestionsContainer.getScene().getWindow();
        popup.show(owner, owner.getX() + owner.getWidth() - 320, owner.getY() + 180);
    }

    // =========================================================
    // Right panel: quick stats
    // =========================================================
    private void updateRightStats() {
        if (statPostsLabel == null || statFollowersLabel == null || statFollowingLabel == null) return;
        if (currentUser == null) {
            statPostsLabel.setText("0");
            statFollowersLabel.setText("0");
            statFollowingLabel.setText("0");
            if (statPostsCreatedLabel != null) statPostsCreatedLabel.setText("0");
            if (statStoriesCreatedLabel != null) statStoriesCreatedLabel.setText("0");
            buildPeakHoursChart(null);
            return;
        }
        int posts = safeList(postService.findByUserId(currentUser.getUserId())).size()
                + safeList(travelStoryService.findByUserId(currentUser.getUserId())).size();
        statPostsLabel.setText(String.valueOf(posts));
        statFollowersLabel.setText(String.valueOf(followingsService.countFollowers(currentUser.getUserId())));
        statFollowingLabel.setText(String.valueOf(followingsService.countFollowing(currentUser.getUserId())));

        int postsCreated = safeList(postService.findByUserId(currentUser.getUserId())).size();
        int storiesCreated = safeList(travelStoryService.findByUserId(currentUser.getUserId())).size();
        if (statPostsCreatedLabel != null) statPostsCreatedLabel.setText(String.valueOf(postsCreated));
        if (statStoriesCreatedLabel != null) statStoriesCreatedLabel.setText(String.valueOf(storiesCreated));

        List<Date> allDates = new ArrayList<>();
        for (Posts p : safeList(postService.findByUserId(currentUser.getUserId())))
            if (p.getCreatedAt() != null) allDates.add(p.getCreatedAt());
        for (TravelStory s : safeList(travelStoryService.findByUserId(currentUser.getUserId()))) {
            Object d = getByAnyGetter(s, "getCreatedAt");
            if (d instanceof Date) allDates.add((Date) d);
        }
        buildPeakHoursChart(allDates);
    }

    private void buildPeakHoursChart(List<Date> dates) {
        if (peakHoursChartContainer == null) return;
        peakHoursChartContainer.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.getStyleClass().add("peak-hours-chart");
        xAxis.setLabel("Hour");
        yAxis.setLabel("Posts");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int[] counts = new int[24];
        if (dates != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            for (Date d : dates) {
                cal.setTime(d);
                counts[cal.get(java.util.Calendar.HOUR_OF_DAY)]++;
            }
        }
        for (int h = 0; h < 24; h++)
            series.getData().add(new XYChart.Data<>(String.valueOf(h), counts[h]));
        chart.getData().add(series);
        chart.setPrefHeight(120);
        chart.setMaxHeight(140);
        peakHoursChartContainer.getChildren().add(chart);
    }

    // =========================================================
    // Instagram Stories bubbles (NOT TravelStory)
    // =========================================================
    private void loadStoriesBubbles() {
        if (storiesRow == null) return;

        storiesRow.getChildren().clear();
        storiesRow.getChildren().add(createAddStoryBubble(0));

        List<Story> active = safeList(storyService.findActiveStories());
        LinkedHashMap<Integer, Story> latestByUser = new LinkedHashMap<>();
        active.sort(Comparator.comparing(Story::getCreatedAt).reversed());
        for (Story s : active) {
            if (!latestByUser.containsKey(s.getUserId())) latestByUser.put(s.getUserId(), s);
        }

        Story myLatest = null;
        if (currentUser != null) {
            myLatest = latestByUser.remove(currentUser.getUserId());
        }
        int idx = 1;
        if (myLatest != null) {
            storiesRow.getChildren().add(createStoryBubble(myLatest, idx++));
        }
        for (Story s : latestByUser.values()) {
            storiesRow.getChildren().add(createStoryBubble(s, idx++));
        }
    }

    private static final String[] STORY_GRADIENTS = {
            "linear-gradient(to bottom right, #F58529, #DD2A7B, #8134AF)",
            "linear-gradient(to bottom right, #515BD4, #8134AF, #DD2A7B)",
            "linear-gradient(to bottom right, #F093FB, #F5576C)",
            "linear-gradient(to bottom right, #4FACFE, #00F2FE)",
            "linear-gradient(to bottom right, #43E97B, #38F9D7)",
            "linear-gradient(to bottom right, #FA7091, #FEE140)",
            "linear-gradient(to bottom right, #A8EDEA, #FED6E3)",
            "linear-gradient(to bottom right, #667EEA, #764BA2)"
    };

    private Node createAddStoryBubble(int index) {
        StackPane outer = new StackPane();
        outer.getStyleClass().addAll("story-bubble", "story-bubble-add");

        StackPane inner = new StackPane();
        inner.getStyleClass().add("story-image-wrapper-add");

        Label plus = new Label("+");
        plus.getStyleClass().add("story-add-plus");
        inner.getChildren().add(plus);
        outer.getChildren().add(inner);

        outer.setOnMouseClicked(e -> handleShowCreateInstaStory());

        Label name = new Label("Add");
        name.getStyleClass().add("story-bubble-username");

        VBox tile = new VBox(6, outer, name);
        tile.setAlignment(Pos.CENTER);
        tile.getStyleClass().add("story-tile");
        return tile;
    }

    private Node createStoryBubble(Story story, int gradientIndex) {
        User u = userService.findById(story.getUserId());
        String uname = null;
        try { uname = (String) u.getClass().getMethod("getUsername").invoke(u); } catch (Exception ignored) {}
        if (uname == null || uname.trim().isEmpty()) {
            String full = (safe(u != null ? u.getFirstName() : "") + " " + safe(u != null ? u.getLastName() : "")).trim();
            uname = !full.isEmpty() ? full : ("user" + story.getUserId());
        }
        String ttl = timeLeftText(story.getExpiresAt());

        StackPane outer = new StackPane();
        outer.getStyleClass().add("story-bubble");
        outer.getStyleClass().add("story-bubble-color-" + (gradientIndex % STORY_GRADIENTS.length));

        StackPane inner = new StackPane();
        inner.getStyleClass().add("story-image-wrapper");

        ImageView iv = new ImageView();
        iv.getStyleClass().add("story-image");
        iv.setFitWidth(60);
        iv.setFitHeight(60);
        iv.setPreserveRatio(false);

        try {
            File f = new File(story.getImageUrl());
            if (f.exists()) iv.setImage(new Image(f.toURI().toString(), true));
        } catch (Exception ignored) {}

        iv.setClip(new Circle(30, 30, 30));
        inner.getChildren().add(iv);
        outer.getChildren().add(inner);

        outer.setOnMouseClicked(e -> openStoriesCarousel(story.getUserId(), story.getId()));
        if (currentUser != null && currentUser.getUserId() == story.getUserId()) {
            ContextMenu menu = new ContextMenu();
            MenuItem del = new MenuItem("Delete");
            del.setOnAction(e -> { storyService.delete(story.getId()); loadStoriesBubbles(); });
            menu.getItems().add(del);
            outer.setOnContextMenuRequested(e -> menu.show(outer, e.getScreenX(), e.getScreenY()));
        }

        Label timeLbl = new Label(ttl);
        timeLbl.getStyleClass().add("story-time-pill");
        StackPane.setAlignment(timeLbl, Pos.TOP_RIGHT);
        StackPane.setMargin(timeLbl, new Insets(4, 4, 0, 0));
        outer.getChildren().add(timeLbl);

        Region mini = new Region();
        mini.getStyleClass().add("story-mini-avatar");
        StackPane.setAlignment(mini, Pos.BOTTOM_LEFT);
        StackPane.setMargin(mini, new Insets(0, 0, 4, 4));
        outer.getChildren().add(mini);

        String display = (currentUser != null && currentUser.getUserId() == story.getUserId()) ? "Your stories" : uname;
        Label name = new Label(display);
        name.getStyleClass().add("story-bubble-username");

        VBox tile = new VBox(4, outer, name);
        tile.setAlignment(Pos.CENTER);
        return tile;
    }

    private void openStoriesCarousel(Integer startUserId) { openStoriesCarousel(startUserId, null); }

    private void openStoriesCarousel(Integer startUserId, Integer startStoryId) {
        try {
            List<Story> active = safeList(storyService.findActiveStories());
            if (active.isEmpty()) return;
            LinkedHashMap<Integer, List<Story>> byUser = new LinkedHashMap<>();
            for (Story s : active) {
                byUser.computeIfAbsent(s.getUserId(), k -> new ArrayList<>()).add(s);
            }
            for (List<Story> list : byUser.values()) {
                list.sort(Comparator.comparing(Story::getCreatedAt));
            }
            List<Integer> userOrder = new ArrayList<>(byUser.keySet());
            final int[] userIndex = new int[]{0};
            if (startUserId != null) {
                int idx = userOrder.indexOf(startUserId);
                if (idx >= 0) userIndex[0] = idx;
            }

            Stage st = new Stage();
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: black;");
            Stage owner = getStageSafe();
            double appW = (owner != null && owner.getWidth() > 0) ? owner.getWidth() : 1200;
            double appH = (owner != null && owner.getHeight() > 0) ? owner.getHeight() : 900;
            double viewerW = 520; // match TravelStory width
            double viewerH = Math.max(600, appH - 140); // shorter than app window
            st.setScene(new Scene(root, viewerW, viewerH));
            st.setTitle("Stories");

            StackPane center = new StackPane();
            ImageView iv = new ImageView();
            iv.setPreserveRatio(true);
            iv.setFitWidth(viewerW);
            iv.setFitHeight(viewerH - 180);
            center.getChildren().add(iv);

            HBox bars = new HBox(6);
            bars.setPadding(new Insets(8, 12, 0, 12));
            bars.setAlignment(Pos.CENTER);

            Label caption = new Label();
            caption.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 700;");
            caption.setWrapText(true);
            caption.setPadding(new Insets(10, 12, 16, 12));

            HBox topRight = new HBox(10);
            topRight.setPadding(new Insets(8, 12, 0, 12));
            topRight.setAlignment(Pos.CENTER_RIGHT);
            Label expiryLbl = new Label();
            expiryLbl.getStyleClass().add("story-view-expiry");
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: 800; -fx-background-radius: 10;");
            deleteBtn.setVisible(false);
            deleteBtn.setManaged(false);
            topRight.getChildren().addAll(expiryLbl, deleteBtn);

            // avatar + username under it (left), expiry to the right is handled above
            ImageView avatarIv = new ImageView();
            avatarIv.setFitWidth(36);
            avatarIv.setFitHeight(36);
            avatarIv.setPreserveRatio(true);
            avatarIv.setSmooth(true);
            avatarIv.setClip(new Circle(18, 18, 18));

            Label unameLbl = new Label();
            unameLbl.getStyleClass().add("story-view-username");

            VBox leftColumn = new VBox(2, avatarIv, unameLbl);
            leftColumn.setAlignment(Pos.CENTER_LEFT);
            HBox topLeft = new HBox(10, leftColumn);
            topLeft.setPadding(new Insets(8, 12, 0, 12));
            topLeft.setAlignment(Pos.CENTER_LEFT);

            StackPane left = new StackPane();
            left.setMinSize(viewerW/2, viewerH - 180);
            left.setMaxSize(viewerW/2, viewerH - 180);
            left.setStyle("-fx-background-color: transparent;");
            StackPane right = new StackPane();
            right.setMinSize(viewerW/2, viewerH - 180);
            right.setMaxSize(viewerW/2, viewerH - 180);
            right.setStyle("-fx-background-color: transparent;");
            StackPane overlay = new StackPane(left, right);
            StackPane.setAlignment(left, Pos.CENTER_LEFT);
            StackPane.setAlignment(right, Pos.CENTER_RIGHT);
            center.getChildren().add(overlay);

            BorderPane header = new BorderPane();
            header.setLeft(topLeft);
            header.setCenter(bars);
            header.setRight(topRight);
            VBox topBox = new VBox(header);
            root.setTop(topBox);
            root.setCenter(center);
            root.setBottom(caption);

            Timeline[] timer = new Timeline[1];
            int[] storyIndex = new int[]{0};
            if (startStoryId != null) {
                List<Story> curr = byUser.get(userOrder.get(userIndex[0]));
                for (int si = 0; si < curr.size(); si++) {
                    if (Objects.equals(curr.get(si).getId(), startStoryId)) { storyIndex[0] = si; break; }
                }
            }


            final Runnable[] showCurrent = new Runnable[1];

            Runnable renderBars = () -> {
                bars.getChildren().clear();
                List<Story> stories = byUser.get(userOrder.get(userIndex[0]));
                for (int i = 0; i < stories.size(); i++) {
                    Region r = new Region();
                    r.setPrefHeight(2);
                    HBox.setHgrow(r, Priority.ALWAYS);
                    if (i < storyIndex[0]) r.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 999;");
                    else if (i == storyIndex[0]) r.setStyle("-fx-background-color: rgba(255,255,255,0.6); -fx-background-radius: 999;");
                    else r.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 999;");
                    bars.getChildren().add(r);
                }
            };

            showCurrent[0] = () -> {
                if (userOrder.isEmpty()) { st.close(); return; }
                List<Story> stories = byUser.get(userOrder.get(userIndex[0]));
                if (storyIndex[0] >= stories.size()) {
                    userIndex[0] = userIndex[0] + 1;
                    storyIndex[0] = 0;
                    if (userIndex[0] >= userOrder.size()) { st.close(); return; }
                    stories = byUser.get(userOrder.get(userIndex[0]));
                }
                Story s = stories.get(storyIndex[0]);
                try {
                    File f = new File(s.getImageUrl());
                    if (f.exists()) iv.setImage(new Image(f.toURI().toString(), true));
                    else iv.setImage(null);
                } catch (Exception ignored) { iv.setImage(null); }
                caption.setText(safe(s.getCaption()));
                String unameTxt;
                User uNow = userService.findById(s.getUserId());
                String unameRaw = null;
                try { unameRaw = (String) uNow.getClass().getMethod("getUsername").invoke(uNow); } catch (Exception ignored) {}
                if (unameRaw == null || unameRaw.isEmpty()) {
                    unameTxt = (safe(uNow != null ? uNow.getFirstName() : "") + " " + safe(uNow != null ? uNow.getLastName() : "")).trim();
                } else unameTxt = unameRaw;
                unameLbl.setText(unameTxt.isEmpty() ? ("user" + s.getUserId()) : unameTxt);
                expiryLbl.setText(timeLeftText(s.getExpiresAt()));
                try {
                    File f = new File(s.getImageUrl());
                    if (f.exists()) avatarIv.setImage(new Image(f.toURI().toString(), true));
                } catch (Exception ignored) {}
                boolean canDelete = currentUser != null && currentUser.getUserId() == s.getUserId();
                deleteBtn.setVisible(canDelete);
                deleteBtn.setManaged(canDelete);
                deleteBtn.setOnAction(ev -> {
                    if (timer[0] != null) timer[0].stop();
                    storyService.delete(s.getId());
                    List<Story> lst = byUser.get(userOrder.get(userIndex[0]));
                    lst.removeIf(sty -> sty.getId() == s.getId());
                    if (lst.isEmpty()) {
                        byUser.remove(userOrder.get(userIndex[0]));
                        userOrder.remove(userIndex[0]);
                        if (userIndex[0] >= userOrder.size()) { st.close(); loadStoriesBubbles(); return; }
                    }
                    loadStoriesBubbles();
                    showCurrent[0].run();
                });
                renderBars.run();
                if (timer[0] != null) timer[0].stop();
                timer[0] = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                    storyIndex[0] = storyIndex[0] + 1;
                    showCurrent[0].run();
                }));
                timer[0].setCycleCount(1);
                timer[0].playFromStart();
            };

            left.setOnMouseClicked(e -> {
                if (timer[0] != null) timer[0].stop();
                if (storyIndex[0] > 0) storyIndex[0] = storyIndex[0] - 1;
                else if (userIndex[0] > 0) {
                    userIndex[0] = userIndex[0] - 1;
                    List<Story> stories = byUser.get(userOrder.get(userIndex[0]));
                    storyIndex[0] = Math.max(0, stories.size() - 1);
                }
                showCurrent[0].run();
            });
            right.setOnMouseClicked(e -> {
                if (timer[0] != null) timer[0].stop();
                storyIndex[0] = storyIndex[0] + 1;
                showCurrent[0].run();
            });

            st.setOnHidden(e -> { if (timer[0] != null) timer[0].stop(); });

            st.show();
            showCurrent[0].run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================
    // Instagram Story modal handlers
    // =========================================================
    @FXML public void handleShowCreateInstaStory() {
        hideAllForms();
        resetInstaStoryForm();
        showModal(createInstaStoryForm);
    }

    @FXML public void handleCancelCreateInstaStory() { hideAllForms(); }

    @FXML public void handleChooseInstaStoryImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Story Image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = getStageSafe();
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        selectedInstaStoryImageFile = file;
        if (instaStoryImageNameLabel != null) instaStoryImageNameLabel.setText(file.getName());
        if (instaStoryImagePreview != null) instaStoryImagePreview.setImage(new Image(file.toURI().toString(), true));
    }

    @FXML public void handleCreateInstaStory() {
        if (currentUser == null) { showAlert("No user session found."); return; }
        if (selectedInstaStoryImageFile == null) { showAlert("Please choose an image."); return; }

        String caption = instaStoryCaptionField != null ? safe(instaStoryCaptionField.getText()) : "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 24);
        Date expiresAt = cal.getTime();

        Story story = new Story(currentUser.getUserId(), selectedInstaStoryImageFile.getAbsolutePath(), caption, expiresAt);

        if (storyService.create(story)) {
            hideAllForms();
            loadStoriesBubbles();
            showAlert("Story added ✔");
        } else {
            showAlert("Failed to add story.");
        }
    }

    @FXML public void handleWatchAllStories() { openStoriesCarousel(null); }

    private void resetInstaStoryForm() {
        selectedInstaStoryImageFile = null;
        if (instaStoryImageNameLabel != null) instaStoryImageNameLabel.setText("No image selected");
        if (instaStoryImagePreview != null) instaStoryImagePreview.setImage(null);
        if (instaStoryCaptionField != null) instaStoryCaptionField.clear();
    }

    // =========================================================
    // Search + Post create
    // =========================================================
    @FXML
    public void handleSearch() {
        String q = searchField != null ? searchField.getText() : null;
        if (q == null || q.trim().isEmpty()) { refreshAll(); return; }

        showOnly(feedScroll);

        if (feedContainer != null) {
            feedContainer.getChildren().clear();
            for (Posts p : safeList(postService.searchByBodyOrTitle(q.trim()))) feedContainer.getChildren().add(buildPostCard(p));
            for (TravelStory s : safeList(travelStoryService.search(q.trim()))) feedContainer.getChildren().add(buildTravelStoryCard(s));
        }
    }

    @FXML
    public void handleShowCreatePost() {
        hideAllForms();
        resetCreatePostForm();
        showModal(createPostForm);
    }

    @FXML public void handleCancelCreatePost() { hideAllForms(); }

    @FXML
    public void handleChoosePostImage() {
        Stage stage = getStageSafe();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Images");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty()) return;

        boolean editing = editPostForm != null && editPostForm.isVisible();
        List<File> target = editing ? selectedEditPostImages : selectedPostImages;
        for (File f : files) {
            if (f != null && f.exists() && target.stream().noneMatch(x -> x.getAbsolutePath().equals(f.getAbsolutePath()))) {
                target.add(f);
            }
        }
        if (editing) refreshImagesUI(editPostImagesPreview, editPostImagesInfoLabel, selectedEditPostImages);
        else refreshImagesUI(postImagesPreview, postImagesInfoLabel, selectedPostImages);
    }

    @FXML
    public void handleCreatePost() {
        if (currentUser == null) { showAlert("No user session found."); return; }

        String title = safe(postTitleField != null ? postTitleField.getText() : null);
        String body  = safe(postContentField != null ? postContentField.getText() : null);
        if (title.isEmpty() || body.isEmpty()) { showAlert("Please fill title and content."); return; }

        Posts post = new Posts(currentUser.getUserId(), 0, title, body, "other");
        if (!selectedPostImages.isEmpty()) {
            post.setImageUrl(joinPaths(selectedPostImages));
        }

        if (postService.create(post)) { hideAllForms(); refreshAll(); }
        else showAlert("Failed to create post.");
    }

    // =========================================================
    // ✅ TravelStory: choose multiple images (Create / Edit)
    // =========================================================
    @FXML
    public void handleChooseStoryImages() {
        Stage stage = getStageSafe();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Travel Story Images");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty()) return;

        for (File f : files) {
            if (f != null && f.exists() && selectedStoryImages.stream().noneMatch(x -> x.getAbsolutePath().equals(f.getAbsolutePath()))) {
                selectedStoryImages.add(f);
            }
        }
        refreshImagesUI(storyImagesPreview, storyImagesInfoLabel, selectedStoryImages);
    }

    @FXML
    public void handleChooseEditStoryImages() {
        Stage stage = getStageSafe();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Travel Story Images");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty()) return;

        for (File f : files) {
            if (f != null && f.exists() && selectedEditStoryImages.stream().noneMatch(x -> x.getAbsolutePath().equals(f.getAbsolutePath()))) {
                selectedEditStoryImages.add(f);
            }
        }
        refreshImagesUI(editStoryImagesPreview, editStoryImagesInfoLabel, selectedEditStoryImages);
    }

    /**
     * ✅ Better thumbnails layout:
     * - consistent thumbnail size
     * - remove (✕) button overlay
     * - FlowPane padding and gaps (matches the container nicely)
     */
    private void refreshImagesUI(FlowPane pane, Label info, List<File> files) {
        if (info != null) {
            info.setText(files == null || files.isEmpty()
                    ? "No images selected"
                    : (files.size() + " image(s) selected"));
        }
        if (pane == null) return;

        pane.getChildren().clear();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(6, 2, 2, 2));

        if (files == null || files.isEmpty()) return;

        // thumbnail sizing tuned for your scroll container
        final double W = 160;
        final double H = 110;

        for (File f : new ArrayList<>(files)) {
            if (f == null || !f.exists()) continue;

            ImageView iv = new ImageView(new Image(f.toURI().toString(), W, H, true, true, true));
            iv.getStyleClass().add("thumb-img");
            iv.setFitWidth(W);
            iv.setFitHeight(H);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            StackPane imgWrap = new StackPane(iv);
            imgWrap.getStyleClass().add("thumb-card");
            imgWrap.setMinSize(W, H);
            imgWrap.setPrefSize(W, H);
            imgWrap.setMaxSize(W, H);

            // remove button
            Button remove = new Button("✕");
            remove.getStyleClass().add("thumb-remove");
            remove.setFocusTraversable(false);
            StackPane.setAlignment(remove, Pos.TOP_RIGHT);
            StackPane.setMargin(remove, new Insets(6));

            remove.setOnAction(e -> {
                files.removeIf(x -> x.getAbsolutePath().equals(f.getAbsolutePath()));
                refreshImagesUI(pane, info, files);
            });

            imgWrap.getChildren().add(remove);
            pane.getChildren().add(imgWrap);
        }
    }

    // =========================================================
    // ✅ TravelStory Create/Edit (SUMMARY ONLY + MULTI PICS)
    // =========================================================
    @FXML
    public void handleShowCreateStory() {
        hideAllForms();
        resetCreateStoryForm();
        showModal(createStoryForm);
    }

    @FXML public void handleCancelCreateStory() { hideAllForms(); }

    @FXML
    public void handleCreateStory() {
        if (currentUser == null) { showAlert("No user session found."); return; }

        String title   = safe(storyTitleField != null ? storyTitleField.getText() : null);
        String dest = "";
        if (storyCountryCombo != null) {
            Country sel = storyCountryCombo.getSelectionModel().getSelectedItem();
            dest = sel != null ? sel.getName() : safe(storyCountryCombo.getEditor() != null ? storyCountryCombo.getEditor().getText() : "");
        }
        String summary = safeTextArea(storySummaryField);

        boolean invalidDest = dest.isEmpty();
        markInvalid(storyCountryCombo, invalidDest);
        if (title.isEmpty() || summary.isEmpty() || invalidDest) {
            showAlert(invalidDest ? "Please select a destination country." : "Please fill title and summary.");
            return;
        }

        // Keep compatibility: your service likely still uses "content"
        TravelStory story = new TravelStory(currentUser.getUserId(), title, summary, dest);

        setByAnySetter(story, summary, "setSummary");
        setByAnySetter(story, summary, "setContent"); // safe even if removed later (reflection)
        setByAnySetter(story, safeTextArea(storyTipsField), "setTips");

        setByAnySetter(story, toDate(storyStartDatePicker), "setStartDate");
        setByAnySetter(story, toDate(storyEndDatePicker), "setEndDate");

        setByAnySetter(story, safeChoice(storyTravelTypeChoice), "setTravelType");
        setByAnySetter(story, safeChoice(storyTravelStyleChoice), "setTravelStyle");

        setByAnySetter(story, createStoryRating, "setOverallRating", "setRating");

        Boolean rec = storyWouldRecommendCheck != null && storyWouldRecommendCheck.isSelected();
        Boolean again = storyWouldGoAgainCheck != null && storyWouldGoAgainCheck.isSelected();
        setByAnySetter(story, rec, "setWouldRecommend");
        setByAnySetter(story, again, "setWouldGoAgain");

        setByAnySetter(story, dest, "setDestinationText", "setDestination");

        applyImagesToStory(story, selectedStoryImages);

        if (travelStoryService.create(story)) {
            hideAllForms();
            refreshAll();
        } else showAlert("Failed to create travel story.");
    }

    @FXML
    public void handleUpdateStory() {
        if (editingStory == null) return;

        String title   = safe(editStoryTitleField != null ? editStoryTitleField.getText() : null);
        String dest = "";
        if (editStoryCountryCombo != null) {
            Country sel = editStoryCountryCombo.getSelectionModel().getSelectedItem();
            dest = sel != null ? sel.getName() : safe(editStoryCountryCombo.getEditor() != null ? editStoryCountryCombo.getEditor().getText() : "");
        }
        if (editStoryDestinationField != null && editStoryDestinationField.getText() != null && !editStoryDestinationField.getText().trim().isEmpty())
            dest = editStoryDestinationField.getText().trim();
        String summary = safeTextArea(editStorySummaryField);

        boolean invalidDest = dest.isEmpty();
        markInvalid(editStoryCountryCombo, invalidDest);
        if (title.isEmpty() || summary.isEmpty() || invalidDest) {
            showAlert(invalidDest ? "Please select a destination country or pick on map." : "Please fill title and summary.");
            return;
        }

        setByAnySetter(editingStory, title, "setTitle");
        setByAnySetter(editingStory, dest, "setDestinationText", "setDestination");

        setByAnySetter(editingStory, summary, "setSummary");
        setByAnySetter(editingStory, summary, "setContent"); // compatibility

        setByAnySetter(editingStory, safeTextArea(editStoryTipsField), "setTips");

        setByAnySetter(editingStory, toDate(editStoryStartDatePicker), "setStartDate");
        setByAnySetter(editingStory, toDate(editStoryEndDatePicker), "setEndDate");

        setByAnySetter(editingStory, safeChoice(editStoryTravelTypeChoice), "setTravelType");
        setByAnySetter(editingStory, safeChoice(editStoryTravelStyleChoice), "setTravelStyle");

        setByAnySetter(editingStory, editStoryRating, "setOverallRating", "setRating");

        Boolean rec = editStoryWouldRecommendCheck != null && editStoryWouldRecommendCheck.isSelected();
        Boolean again = editStoryWouldGoAgainCheck != null && editStoryWouldGoAgainCheck.isSelected();
        setByAnySetter(editingStory, rec, "setWouldRecommend");
        setByAnySetter(editingStory, again, "setWouldGoAgain");

        applyImagesToStory(editingStory, selectedEditStoryImages);

        if (travelStoryService.update(editingStory)) {
            editingStory = null;
            hideAllForms();
            refreshAll();
        } else showAlert("Failed to update story.");
    }

    @FXML
    public void handleCancelEditStory() {
        editingStory = null;
        hideAllForms();
    }

    private void applyImagesToStory(TravelStory story, List<File> files) {
        if (story == null) return;
        if (files == null || files.isEmpty()) return; // don't overwrite existing

        String coverPath = files.get(0).getAbsolutePath();
        setByAnySetter(story, coverPath, "setCoverImageUrl");

        List<String> paths = new ArrayList<>();
        for (File f : files) if (f != null && f.exists()) paths.add(f.getAbsolutePath());
        setByAnySetter(story, toJsonArray(paths), "setImageUrlsJson");
    }

    // =========================================================
    // Edit handlers (posts)
    // =========================================================
    @FXML
    public void handleUpdatePost() {
        if (editingPost == null) return;

        String title = safe(editPostTitleField != null ? editPostTitleField.getText() : null);
        String body  = safe(editPostContentField != null ? editPostContentField.getText() : null);
        if (title.isEmpty() || body.isEmpty()) { showAlert("Please fill title and content."); return; }

        editingPost.setTitle(title);
        editingPost.setBody(body);
        if (!selectedEditPostImages.isEmpty()) {
            editingPost.setImageUrl(joinPaths(selectedEditPostImages));
        }

        if (postService.update(editingPost)) {
            editingPost = null;
            hideAllForms();
            refreshAll();
        } else showAlert("Failed to update post.");
    }

    @FXML
    public void handleCancelEditPost() {
        editingPost = null;
        hideAllForms();
    }

    private void showEditPostForm(Posts post) {
        editingPost = post;
        hideAllForms();
        if (editPostTitleField != null) editPostTitleField.setText(safe(post.getTitle()));
        if (editPostContentField != null) editPostContentField.setText(safe(post.getBody()));
        selectedEditPostImages.clear();
        for (String p : splitPaths(safe(post.getImageUrl()))) {
            File f = new File(p);
            if (f.exists()) selectedEditPostImages.add(f);
        }
        refreshImagesUI(editPostImagesPreview, editPostImagesInfoLabel, selectedEditPostImages);
        showModal(editPostForm);
    }

    private void showEditStoryForm(TravelStory story) {
        editingStory = story;
        hideAllForms();

        if (editStoryTitleField != null) editStoryTitleField.setText(safe((String) getByAnyGetter(story, "getTitle")));
        String dest = safe((String) getByAnyGetter(story, "getDestinationText", "getDestination"));
        if (editStoryCountryCombo != null) {
            // try to select matching country by name or code
            Country match = null;
            if (!dest.isEmpty() && countryService != null) {
                for (Country c : countryService.getAll()) {
                    if (c.getName().equalsIgnoreCase(dest) || c.getAlpha2().equalsIgnoreCase(dest)) { match = c; break; }
                }
            }
            if (match != null) editStoryCountryCombo.getSelectionModel().select(match);
            else if (editStoryCountryCombo.getEditor() != null) editStoryCountryCombo.getEditor().setText(dest);
        }
        if (editStoryDestinationField != null) {
            editStoryDestinationField.setText(dest);
            editStoryDestinationField.setVisible(true);
            editStoryDestinationField.setManaged(true);
        }

        String summary = safe((String) getByAnyGetter(story, "getSummary"));
        if (summary.isEmpty()) summary = safe((String) getByAnyGetter(story, "getContent"));
        if (editStorySummaryField != null) editStorySummaryField.setText(summary);

        if (editStoryTipsField != null) editStoryTipsField.setText(safe((String) getByAnyGetter(story, "getTips")));

        if (editStoryStartDatePicker != null) editStoryStartDatePicker.setValue(toLocalDate((Date) getByAnyGetter(story, "getStartDate")));
        if (editStoryEndDatePicker != null) editStoryEndDatePicker.setValue(toLocalDate((Date) getByAnyGetter(story, "getEndDate")));

        String type = safe((String) getByAnyGetter(story, "getTravelType"));
        String style = safe((String) getByAnyGetter(story, "getTravelStyle"));
        if (editStoryTravelTypeChoice != null && !type.isEmpty()) editStoryTravelTypeChoice.setValue(type);
        if (editStoryTravelStyleChoice != null && !style.isEmpty()) editStoryTravelStyleChoice.setValue(style);

        Object ratingObj = getByAnyGetter(story, "getOverallRating", "getRating");
        int r = (ratingObj instanceof Integer rr) ? rr : 5;
        editStoryRating = clampRating(r);
        setStarRating(editStoryRatingStars, editStoryRating);

        Object recObj = getByAnyGetter(story, "isWouldRecommend", "getWouldRecommend");
        Object againObj = getByAnyGetter(story, "isWouldGoAgain", "getWouldGoAgain");
        if (editStoryWouldRecommendCheck != null && recObj instanceof Boolean b) editStoryWouldRecommendCheck.setSelected(b);
        if (editStoryWouldGoAgainCheck != null && againObj instanceof Boolean b) editStoryWouldGoAgainCheck.setSelected(b);

        // load images to edit list
        selectedEditStoryImages.clear();

        String cover = safe((String) getByAnyGetter(story, "getCoverImageUrl"));
        if (!cover.isEmpty()) {
            File f = new File(cover);
            if (f.exists()) selectedEditStoryImages.add(f);
        }

        String json = safe((String) getByAnyGetter(story, "getImageUrlsJson"));
        for (String p : parseJsonArray(json)) {
            File f = new File(p);
            if (f.exists() && selectedEditStoryImages.stream().noneMatch(x -> x.getAbsolutePath().equals(f.getAbsolutePath()))) {
                selectedEditStoryImages.add(f);
            }
        }

        refreshImagesUI(editStoryImagesPreview, editStoryImagesInfoLabel, selectedEditStoryImages);
        showModal(editStoryForm);
    }

    // =========================================================
    // Post Card
    // =========================================================
    private VBox buildPostCard(Posts post) {
        VBox card = baseCard();
        card.getStyleClass().add("post-card");

        User author = userService.findById(post.getUserId());
        String authorName = author != null ? (safe(author.getFirstName()) + " " + safe(author.getLastName())).trim() : "Unknown";
        if (authorName.isEmpty()) authorName = "Unknown";
        String timeText = post.getCreatedAt() != null ? SDF.format(post.getCreatedAt()) : "";

        HBox header = buildHeader(
                author, authorName, timeText,
                () -> showEditPostForm(post),
                () -> { postService.delete(post.getId()); refreshAll(); },
                isOwner(post.getUserId())
        );

        Label titleLbl = new Label(safe(post.getTitle()));
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: 800;");

        Label bodyLbl = new Label(safe(post.getBody()));
        bodyLbl.setWrapText(true);

        card.getChildren().addAll(header, titleLbl, bodyLbl);
        addPostImages(card, post.getImageUrl());

        CommentsSection cs = buildPostCommentsSection(post.getId());
        card.getChildren().add(buildActionsRowPost(post.getId(), cs));
        card.getChildren().add(cs.root);

        return card;
    }

    // =========================================================
    // ✅ Travel Story Card (cover + gallery + summary)
    // =========================================================
    private VBox buildTravelStoryCard(TravelStory story) {
        VBox card = baseCard();
        card.getStyleClass().addAll("post-card", "story-card");

        User author = userService.findById(story.getUserId());
        String authorName = author != null ? (safe(author.getFirstName()) + " " + safe(author.getLastName())).trim() : "Unknown";
        if (authorName.isEmpty()) authorName = "Unknown";
        Date createdAt = (Date) getByAnyGetter(story, "getCreatedAt");
        String timeText = createdAt != null ? SDF.format(createdAt) : "";

        HBox header = buildHeader(
                author, authorName, timeText,
                () -> showEditStoryForm(story),
                () -> { travelStoryService.delete(story.getTravelStoryId()); refreshAll(); },
                isOwner(story.getUserId())
        );

        VBox cover = buildStoryCover(story);
        VBox gallery = buildStoryGallery(story);

        String dest = safe((String) getByAnyGetter(story, "getDestinationText", "getDestination"));
        Label destPill = pill(dest.isEmpty() ? "Unknown destination" : ("📍 " + dest), "pill-blue");

        String type = safe((String) getByAnyGetter(story, "getTravelType"));
        String style = safe((String) getByAnyGetter(story, "getTravelStyle"));

        HBox pillsRow = new HBox(8);
        pillsRow.setAlignment(Pos.CENTER_LEFT);
        pillsRow.getChildren().add(destPill);
        if (!type.isEmpty()) pillsRow.getChildren().add(pill(type, "pill-gray"));
        if (!style.isEmpty()) pillsRow.getChildren().add(pill(style, "pill-gray"));

        Label titleLbl = new Label(safe((String) getByAnyGetter(story, "getTitle")));
        titleLbl.getStyleClass().add("story-title");

        Integer rating = null;
        Object ratingObj = getByAnyGetter(story, "getOverallRating", "getRating");
        if (ratingObj instanceof Integer r) rating = r;

        Label stars = new Label(starsLabel(rating));
        stars.getStyleClass().add("story-stars");
        if (rating == null) { stars.setVisible(false); stars.setManaged(false); }

        Label metaDates = new Label(composeDateLine(
                (Date) getByAnyGetter(story, "getStartDate"),
                (Date) getByAnyGetter(story, "getEndDate")
        ));
        metaDates.getStyleClass().add("story-meta");
        if (metaDates.getText().isEmpty()) { metaDates.setVisible(false); metaDates.setManaged(false); }

        String summary = safe((String) getByAnyGetter(story, "getSummary"));
        if (summary.isEmpty()) summary = safe((String) getByAnyGetter(story, "getContent"));
        Label summaryLbl = new Label(summary);
        summaryLbl.setWrapText(true);
        summaryLbl.getStyleClass().add("story-summary");
        if (summary.isEmpty()) { summaryLbl.setVisible(false); summaryLbl.setManaged(false); }

        String tips = safe((String) getByAnyGetter(story, "getTips"));
        TitledPane tipsPane = buildTipsPane(tips);

        card.getChildren().addAll(header, cover);
        if (gallery != null) card.getChildren().add(gallery);
        card.getChildren().addAll(pillsRow, titleLbl, stars, metaDates, summaryLbl);
        if (tipsPane != null) card.getChildren().add(tipsPane);

        CommentsSection cs = buildStoryCommentsSection(story.getTravelStoryId());
        card.getChildren().add(buildActionsRowStory(story.getTravelStoryId(), cs));
        card.getChildren().add(cs.root);

        return card;
    }

    private VBox buildStoryGallery(TravelStory story) {
        String json = safe((String) getByAnyGetter(story, "getImageUrlsJson"));
        List<String> paths = new ArrayList<>(parseJsonArray(json));

        String cover = safe((String) getByAnyGetter(story, "getCoverImageUrl"));
        paths.removeIf(p -> !cover.isEmpty() && p.equals(cover));

        if (paths.isEmpty()) return null;

        int n = paths.size();
        if (n == 1) {
            String p = paths.get(0);
            File f = new File(p);
            if (!f.exists()) return null;
            ImageView iv = new ImageView(new Image(f.toURI().toString(), true));
            iv.setFitWidth(520);
            iv.setFitHeight(300);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.getStyleClass().add("story-gallery-single");
            StackPane card = new StackPane(iv);
            card.getStyleClass().add("story-gallery-tile");
            card.setOnMouseClicked(e -> openImageViewer(f));
            return new VBox(6, card);
        }

        GridPane grid = new GridPane();
        grid.getStyleClass().add("story-gallery-grid");
        grid.setHgap(10);
        grid.setVgap(10);

        double cardWidth = 520.0;
        double gap = 10.0;
        int cols = 3;
        double baseW = Math.floor((cardWidth - gap * (cols - 1)) / cols);
        double baseH = Math.floor(baseW * 0.66);

        if (n == 2) {
            double w2 = Math.floor((cardWidth - gap) / 2);
            double h2 = Math.floor(w2 * 0.66);
            for (int i = 0; i < 2; i++) {
                try {
                    File f = new File(paths.get(i));
                    if (!f.exists()) continue;
                    ImageView iv = new ImageView(new Image(f.toURI().toString(), w2, h2, true, true, true));
                    iv.getStyleClass().add("story-gallery-thumb-img");
                    iv.setFitWidth(w2);
                    iv.setFitHeight(h2);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    StackPane card = new StackPane(iv);
                    card.getStyleClass().add("story-gallery-tile");
                    card.setOnMouseClicked(e -> openImageViewer(f));
                    grid.add(card, i, 0);
                } catch (Exception ignored) {}
            }
            return new VBox(6, grid);
        }

        int i = 0;
        try {
            File f0 = new File(paths.get(0));
            if (f0.exists()) {
                double W = baseW * 2 + gap;
                double H = baseH * 2 + gap;
                ImageView iv0 = new ImageView(new Image(f0.toURI().toString(), W, H, true, true, true));
                iv0.getStyleClass().add("story-gallery-thumb-img");
                iv0.setFitWidth(W);
                iv0.setFitHeight(H);
                iv0.setPreserveRatio(true);
                iv0.setSmooth(true);
                StackPane card0 = new StackPane(iv0);
                card0.getStyleClass().add("story-gallery-tile");
                card0.setOnMouseClicked(e -> openImageViewer(f0));
                grid.add(card0, 0, 0);
                GridPane.setColumnSpan(card0, 2);
                GridPane.setRowSpan(card0, 2);
            }
        } catch (Exception ignored) {}
        i = 1;
        if (n >= 2) {
            try {
                File f1 = new File(paths.get(1));
                if (f1.exists()) {
                    ImageView iv1 = new ImageView(new Image(f1.toURI().toString(), baseW, baseH, true, true, true));
                    iv1.getStyleClass().add("story-gallery-thumb-img");
                    iv1.setFitWidth(baseW);
                    iv1.setFitHeight(baseH);
                    iv1.setPreserveRatio(true);
                    iv1.setSmooth(true);
                    StackPane card1 = new StackPane(iv1);
                    card1.getStyleClass().add("story-gallery-tile");
                    card1.setOnMouseClicked(e -> openImageViewer(f1));
                    grid.add(card1, 2, 0);
                }
            } catch (Exception ignored) {}
            i = 2;
        }
        if (n >= 3) {
            try {
                File f2 = new File(paths.get(2));
                if (f2.exists()) {
                    ImageView iv2 = new ImageView(new Image(f2.toURI().toString(), baseW, baseH, true, true, true));
                    iv2.getStyleClass().add("story-gallery-thumb-img");
                    iv2.setFitWidth(baseW);
                    iv2.setFitHeight(baseH);
                    iv2.setPreserveRatio(true);
                    iv2.setSmooth(true);
                    StackPane card2 = new StackPane(iv2);
                    card2.getStyleClass().add("story-gallery-tile");
                    card2.setOnMouseClicked(e -> openImageViewer(f2));
                    grid.add(card2, 2, 1);
                }
            } catch (Exception ignored) {}
            i = 3;
        }

        for (int k = i; k < n; k++) {
            try {
                File f = new File(paths.get(k));
                if (!f.exists()) continue;
                int idx = k - i;
                int row = 2 + idx / cols;
                int col = idx % cols;
                ImageView iv = new ImageView(new Image(f.toURI().toString(), baseW, baseH, true, true, true));
                iv.getStyleClass().add("story-gallery-thumb-img");
                iv.setFitWidth(baseW);
                iv.setFitHeight(baseH);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                StackPane card = new StackPane(iv);
                card.getStyleClass().add("story-gallery-tile");
                card.setOnMouseClicked(e -> openImageViewer(f));
                grid.add(card, col, row);
            } catch (Exception ignored) {}
        }

        if (grid.getChildren().isEmpty()) return null;
        return new VBox(6, grid);
    }

    // =========================================================
    // Actions rows (React + Comment + Share)
    // =========================================================
    private HBox buildActionsRowPost(int postId, CommentsSection cs) {
        Button reactBtn = new Button(buildReactLabelForPost(postId));
        reactBtn.getStyleClass().addAll("action-row-btn", "react-btn");
        reactBtn.setStyle(btnStyleNeutral());

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("react-context-menu");
        menu.getItems().addAll(
                postReactItem("👍 Like", ReactionsService.ReactType.LIKE, postId),
                postReactItem("❤️ Love", ReactionsService.ReactType.LOVE, postId),
                postReactItem("😂 Haha", ReactionsService.ReactType.HAHA, postId),
                postReactItem("😮 Wow",  ReactionsService.ReactType.WOW,  postId),
                postReactItem("😢 Sad",  ReactionsService.ReactType.SAD,  postId),
                postReactItem("😡 Angry",ReactionsService.ReactType.ANGRY,postId)
        );
        reactBtn.setOnAction(e -> menu.show(reactBtn, Side.BOTTOM, 0, 0));

        Button commentBtn = new Button("💬 Comment");
        commentBtn.getStyleClass().add("action-row-btn");
        commentBtn.setStyle(btnStyleNeutral());
        commentBtn.setOnAction(e -> cs.openAndFocus());

        Button shareBtn = new Button("↗ Share");
        shareBtn.getStyleClass().add("action-row-btn");
        shareBtn.setStyle(btnStyleNeutral());
        shareBtn.setOnAction(e -> doSharePost(postId));

        boolean saved = currentUser != null && savedPostsService.isSaved(currentUser.getUserId(), postId);
        Button saveBtn = new Button(saved ? "🔖 Saved" : "🔖 Save");
        saveBtn.getStyleClass().add("action-row-btn");
        saveBtn.setStyle(btnStyleNeutral() + (saved ? "-fx-text-fill: #6D83F2; -fx-font-weight: 900;" : ""));
        saveBtn.setOnAction(e -> {
            if (currentUser == null) { showAlert("Please log in to save posts."); return; }
            savedPostsService.toggleSave(currentUser.getUserId(), postId);
            boolean nowSaved = savedPostsService.isSaved(currentUser.getUserId(), postId);
            if (nowSaved) {
                saveBtn.setText("🔖 Saved");
                saveBtn.setStyle(btnStyleNeutral() + "-fx-text-fill: #6D83F2; -fx-font-weight: 900;");
            } else {
                saveBtn.setText("🔖 Save");
                saveBtn.setStyle(btnStyleNeutral());
            }
            if (savedContainer != null) refreshSaved();
            // Keep sidebar stats and activity page in sync with saves
            updateSidebar();
            if (statsScroll != null && statsScroll.isVisible()) {
                refreshStatsPage();
            }
        });

        int reacts = reactionsService.countReactionsForPost(postId);
        int comments = safeList(commentService.findByPostId(postId)).size();
        int shares = sharesService.countSharesForPost(postId);

        Label reactsLbl   = reacts   > 0 ? countPill(reacts)   : invisiblePill();
        Label commentsLbl = comments > 0 ? countPill(comments) : invisiblePill();
        Label sharesLbl   = shares   > 0 ? countPill(shares)   : invisiblePill();

        HBox row = new HBox(14, reactBtn, reactsLbl, commentBtn, commentsLbl, shareBtn, sharesLbl, saveBtn);
        row.getStyleClass().add("action-row");
        return row;
    }

    private HBox buildActionsRowStory(int storyId, CommentsSection cs) {
        Button reactBtn = new Button(buildReactLabelForStory(storyId));
        reactBtn.getStyleClass().addAll("action-row-btn", "react-btn");
        reactBtn.setStyle(btnStyleNeutral());

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("react-context-menu");
        menu.getItems().addAll(
                storyReactItem("👍 Like", ReactionsService.ReactType.LIKE, storyId),
                storyReactItem("❤️ Love", ReactionsService.ReactType.LOVE, storyId),
                storyReactItem("😂 Haha", ReactionsService.ReactType.HAHA, storyId),
                storyReactItem("😮 Wow",  ReactionsService.ReactType.WOW,  storyId),
                storyReactItem("😢 Sad",  ReactionsService.ReactType.SAD,  storyId),
                storyReactItem("😡 Angry",ReactionsService.ReactType.ANGRY,storyId)
        );
        reactBtn.setOnAction(e -> menu.show(reactBtn, Side.BOTTOM, 0, 0));

        Button commentBtn = new Button("💬 Comment");
        commentBtn.getStyleClass().add("action-row-btn");
        commentBtn.setStyle(btnStyleNeutral());
        commentBtn.setOnAction(e -> cs.openAndFocus());

        Button shareBtn = new Button("↗ Share");
        shareBtn.getStyleClass().add("action-row-btn");
        shareBtn.setStyle(btnStyleNeutral());
        shareBtn.setOnAction(e -> doShareStory(storyId));

        int reacts = reactionsService.countReactionsForStory(storyId);
        int comments = safeList(commentService.findByTravelStoryId(storyId)).size();
        int shares = sharesService.countSharesForStory(storyId);

        Label reactsLbl = reacts > 0 ? countPill(reacts) : invisiblePill();
        Label commentsLbl = comments > 0 ? countPill(comments) : invisiblePill();
        Label sharesLbl = shares > 0 ? countPill(shares) : invisiblePill();

        HBox row = new HBox(14, reactBtn, reactsLbl, commentBtn, commentsLbl, shareBtn, sharesLbl);
        row.getStyleClass().add("action-row");
        return row;
    }

    private String buildReactLabelForPost(int postId) {
        if (currentUser == null) return "React";
        ReactionsService.ReactType t = reactionsService.getUserReactionForPost(currentUser.getUserId(), postId);
        return emojiLabel(t);
    }

    private String buildReactLabelForStory(int storyId) {
        if (currentUser == null) return "React";
        ReactionsService.ReactType t = reactionsService.getUserReactionForStory(currentUser.getUserId(), storyId);
        return emojiLabel(t);
    }

    private String buildReactLabelForComment(int commentId) {
        if (currentUser == null) return "React";
        ReactionsService.ReactType t = reactionsService.getUserReactionForComment(currentUser.getUserId(), commentId);
        return emojiLabel(t);
    }

    private String emojiLabel(ReactionsService.ReactType t) {
        if (t == null) return "React";
        return switch (t) {
            case LIKE -> "👍 Like";
            case LOVE -> "❤️ Love";
            case HAHA -> "😂 Haha";
            case WOW  -> "😮 Wow";
            case SAD  -> "😢 Sad";
            case ANGRY-> "😡 Angry";
            default   -> "React";
        };
    }

    private MenuItem postReactItem(String text, ReactionsService.ReactType type, int postId) {
        MenuItem it = new MenuItem(text);
        it.getStyleClass().add("react-menu-item");
        it.setStyle("-fx-font-size: 15px;");
        it.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            reactionsService.toggleReactionForPost(currentUser.getUserId(), postId, type);
            refreshAll();
        });
        return it;
    }

    private MenuItem storyReactItem(String text, ReactionsService.ReactType type, int storyId) {
        MenuItem it = new MenuItem(text);
        it.getStyleClass().add("react-menu-item");
        it.setStyle("-fx-font-size: 15px;");
        it.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            reactionsService.toggleReactionForStory(currentUser.getUserId(), storyId, type);
            refreshAll();
        });
        return it;
    }

    private MenuItem commentReactItem(String text, ReactionsService.ReactType type, int commentId) {
        MenuItem it = new MenuItem(text);
        it.getStyleClass().add("react-menu-item");
        it.setStyle("-fx-font-size: 15px;");
        it.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            reactionsService.toggleReactionForComment(currentUser.getUserId(), commentId, type);
            refreshAll();
        });
        return it;
    }

    // =========================================================
    // Comments section (with replies + react)
    // =========================================================
    private static class CommentsSection {
        VBox root;
        VBox box;
        VBox list;
        TextField input;
        void openAndFocus() {
            box.setVisible(true);
            box.setManaged(true);
            input.requestFocus();
        }
    }

    private CommentsSection buildPostCommentsSection(int postId) {
        CommentsSection cs = new CommentsSection();
        VBox root = new VBox(8);

        Hyperlink toggle = new Hyperlink("View comments (" + safeList(commentService.findByPostId(postId)).size() + ")");
        toggle.getStyleClass().add("comment-toggle");

        VBox box = new VBox(8);
        box.setVisible(false);
        box.setManaged(false);

        VBox list = new VBox(8);

        Runnable render = () -> {
            list.getChildren().clear();
            List<Comments> top = safeList(commentService.findTopLevelByPostId(postId));
            for (Comments c : top) list.getChildren().add(buildCommentThread(c));
            toggle.setText("View comments (" + safeList(commentService.findByPostId(postId)).size() + ")");
        };

        TextField input = new TextField();
        input.setPromptText("Write a comment...");
        HBox.setHgrow(input, Priority.ALWAYS);

        Button send = new Button("Send");
        send.getStyleClass().add("comment-send-btn");

        send.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            String text = input.getText();
            if (text == null || text.trim().isEmpty()) return;

            Comments c = new Comments(postId, null, currentUser.getUserId(), null, text.trim());
            if (commentService.create(c)) {
                input.clear();
                box.setVisible(true);
                box.setManaged(true);
                render.run();
                toggle.setText("Hide comments");
            } else showAlert("Failed to add comment.");
        });

        input.setOnAction(e -> send.fire());

        HBox addRow = new HBox(8, input, send);
        box.getChildren().addAll(list, addRow);

        toggle.setOnAction(e -> {
            boolean open = !box.isVisible();
            box.setVisible(open);
            box.setManaged(open);
            if (open) { render.run(); toggle.setText("Hide comments"); }
            else toggle.setText("View comments (" + safeList(commentService.findByPostId(postId)).size() + ")");
        });

        root.getChildren().addAll(toggle, box);

        cs.root = root;
        cs.box = box;
        cs.list = list;
        cs.input = input;
        return cs;
    }

    private CommentsSection buildStoryCommentsSection(int storyId) {
        CommentsSection cs = new CommentsSection();
        VBox root = new VBox(8);

        Hyperlink toggle = new Hyperlink("View comments (" + safeList(commentService.findByTravelStoryId(storyId)).size() + ")");
        toggle.getStyleClass().add("comment-toggle");

        VBox box = new VBox(8);
        box.setVisible(false);
        box.setManaged(false);

        VBox list = new VBox(8);

        Runnable render = () -> {
            list.getChildren().clear();
            List<Comments> top = safeList(commentService.findTopLevelByTravelStoryId(storyId));
            for (Comments c : top) list.getChildren().add(buildCommentThread(c));
            toggle.setText("View comments (" + safeList(commentService.findByTravelStoryId(storyId)).size() + ")");
        };

        TextField input = new TextField();
        input.setPromptText("Write a comment...");
        HBox.setHgrow(input, Priority.ALWAYS);

        Button send = new Button("Send");
        send.getStyleClass().add("comment-send-btn");

        send.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            String text = input.getText();
            if (text == null || text.trim().isEmpty()) return;

            Comments c = new Comments(null, storyId, currentUser.getUserId(), null, text.trim());
            if (commentService.create(c)) {
                input.clear();
                box.setVisible(true);
                box.setManaged(true);
                render.run();
                toggle.setText("Hide comments");
            } else showAlert("Failed to add comment.");
        });

        input.setOnAction(e -> send.fire());

        HBox addRow = new HBox(8, input, send);
        box.getChildren().addAll(list, addRow);

        toggle.setOnAction(e -> {
            boolean open = !box.isVisible();
            box.setVisible(open);
            box.setManaged(open);
            if (open) { render.run(); toggle.setText("Hide comments"); }
            else toggle.setText("View comments (" + safeList(commentService.findByTravelStoryId(storyId)).size() + ")");
        });

        root.getChildren().addAll(toggle, box);

        cs.root = root;
        cs.box = box;
        cs.list = list;
        cs.input = input;
        return cs;
    }

    private VBox buildCommentThread(Comments parent) {
        VBox thread = new VBox(6);
        thread.getChildren().add(buildCommentBubble(parent));

        List<Comments> replies = safeList(commentService.findReplies(parent.getId()));
        if (!replies.isEmpty()) {
            VBox repliesBox = new VBox(6);
            repliesBox.setPadding(new Insets(0, 0, 0, 20));
            for (Comments r : replies) repliesBox.getChildren().add(buildCommentBubble(r));
            thread.getChildren().add(repliesBox);
        }

        thread.getChildren().add(buildReplyBox(parent));
        return thread;
    }

    private HBox buildReplyBox(Comments parent) {
        TextField replyField = new TextField();
        replyField.setPromptText("Reply...");
        HBox.setHgrow(replyField, Priority.ALWAYS);

        Button replyBtn = new Button("Reply");
        replyBtn.getStyleClass().add("comment-reply-btn");

        replyBtn.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            String text = replyField.getText();
            if (text == null || text.trim().isEmpty()) return;

            Comments reply = new Comments(parent.getPostId(), parent.getTravelStoryId(),
                    currentUser.getUserId(), parent.getId(), text.trim());

            if (commentService.create(reply)) {
                replyField.clear();
                refreshAll();
            } else {
                showAlert("Failed to add reply.");
            }
        });

        replyField.setOnAction(e -> replyBtn.fire());

        HBox row = new HBox(8, replyField, replyBtn);
        row.setPadding(new Insets(0, 0, 0, 10));
        return row;
    }

    private VBox buildCommentBubble(Comments c) {
        VBox bubble = new VBox(6);
        bubble.getStyleClass().add("comment-bubble");

        User u = userService.findById(c.getUserId());
        String uname = u != null ? (safe(u.getFirstName()) + " " + safe(u.getLastName())).trim() : "Unknown";

        Label name = new Label(uname);
        name.getStyleClass().add("comment-author");

        Label bodyLbl = new Label(safe(c.getBody()));
        bodyLbl.setWrapText(true);
        bodyLbl.getStyleClass().add("comment-body");

        Button reactBtn = new Button(buildReactLabelForComment(c.getId()));
        reactBtn.getStyleClass().addAll("action-row-btn", "react-btn");
        reactBtn.setStyle(btnStyleNeutral());

        ContextMenu reactMenu = new ContextMenu();
        reactMenu.getStyleClass().add("react-context-menu");
        reactMenu.getItems().addAll(
                commentReactItem("👍 Like", ReactionsService.ReactType.LIKE, c.getId()),
                commentReactItem("❤️ Love", ReactionsService.ReactType.LOVE, c.getId()),
                commentReactItem("😂 Haha", ReactionsService.ReactType.HAHA, c.getId()),
                commentReactItem("😮 Wow",  ReactionsService.ReactType.WOW,  c.getId()),
                commentReactItem("😢 Sad",  ReactionsService.ReactType.SAD,  c.getId()),
                commentReactItem("😡 Angry",ReactionsService.ReactType.ANGRY,c.getId())
        );
        reactBtn.setOnAction(e -> reactMenu.show(reactBtn, Side.BOTTOM, 0, 0));

        bubble.getChildren().addAll(name, bodyLbl, reactBtn);
        return bubble;
    }

    // =========================================================
    // Header / card helpers
    // =========================================================
    private VBox baseCard() {
        VBox card = new VBox(10);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private HBox buildHeader(User author, String authorName, String timeText,
                             Runnable onEdit, Runnable onDelete,
                             boolean showActions) {

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Node avatarNode = buildAuthorAvatar(author, 42);

        VBox meta = new VBox(2);
        Label nameLbl = new Label(authorName);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label timeLbl = new Label(timeText);
        timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        meta.getChildren().addAll(nameLbl, timeLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (showActions) {
            Button editBtn = new Button("✏");
            editBtn.setFocusTraversable(false);
            editBtn.setStyle(
                    "-fx-background-color: #3b82f6;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 8 14;" +
                            "-fx-cursor: hand;"
            );
            editBtn.setOnAction(e -> onEdit.run());

            Button deleteBtn = new Button("🗑");
            deleteBtn.setFocusTraversable(false);
            deleteBtn.setStyle(
                    "-fx-background-color: #ef4444;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 8 14;" +
                            "-fx-cursor: hand;"
            );
            deleteBtn.setOnAction(e -> onDelete.run());

            actions.getChildren().addAll(editBtn, deleteBtn);
        } else {
            actions.setVisible(false);
            actions.setManaged(false);
        }

        header.getChildren().addAll(avatarNode, meta, spacer, actions);
        return header;
    }

    private Node buildAuthorAvatar(User author, int size) {
        StackPane wrap = new StackPane();
        wrap.setMinSize(size, size);
        wrap.setMaxSize(size, size);
        wrap.setStyle("-fx-background-radius: 999; -fx-background-color: linear-gradient(135deg, #6D83F2, #4CCCAD);");

        Label initials = new Label();
        initials.setStyle("-fx-font-size: " + (size / 2.2) + "px; -fx-font-weight: bold; -fx-text-fill: white;");
        if (author != null) {
            String first = author.getFirstName() != null && !author.getFirstName().isBlank()
                    ? author.getFirstName().substring(0, 1).toUpperCase() : "";
            String last = author.getLastName() != null && !author.getLastName().isBlank()
                    ? author.getLastName().substring(0, 1).toUpperCase() : "";
            initials.setText((first + last).isBlank() ? "?" : (first + last));
        } else {
            initials.setText("?");
        }

        ImageView iv = new ImageView();
        iv.setFitWidth(size - 4);
        iv.setFitHeight(size - 4);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setVisible(false);
        iv.setManaged(false);
        double r = (size - 4) / 2.0;
        iv.setClip(new Circle(r, r, r));

        if (author != null && author.getAvatarId() != null && author.getAvatarId().contains(":")) {
            String[] parts = author.getAvatarId().split(":", 2);
            if (parts.length == 2 && "emoji".equals(parts[0])) {
                initials.setText(parts[1]);
            } else if (parts.length == 2 && "url".equals(parts[0])) {
                try {
                    Image img = new Image(parts[1], true);
                    iv.setImage(img);
                    img.progressProperty().addListener((o, oldP, progress) -> {
                        if (progress != null && progress.doubleValue() >= 1.0 && !img.isError()) {
                            Platform.runLater(() -> {
                                iv.setVisible(true);
                                iv.setManaged(true);
                                initials.setVisible(false);
                                initials.setManaged(false);
                            });
                        }
                    });
                    img.errorProperty().addListener((o, wasErr, isErr) -> {
                        if (Boolean.TRUE.equals(isErr)) {
                            Platform.runLater(() -> {
                                iv.setVisible(false);
                                iv.setManaged(false);
                                initials.setVisible(true);
                                initials.setManaged(true);
                            });
                        }
                    });
                } catch (Exception ignored) {}
            } else if (parts.length == 2) {
                // DiceBear-style avatar (e.g. avataaars:seed) — load async and show in card
                String style = parts[0];
                String seed = parts[1];
                String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed + "&size=" + size + "&backgroundColor=4cccad";
                ImageView finalIv = iv;
                Label finalInitials = initials;
                new Thread(() -> {
                    try {
                        Image img = new Image(avatarUrl, size, size, true, true, true);
                        Platform.runLater(() -> {
                            if (finalIv != null && !img.isError()) {
                                finalIv.setImage(img);
                                finalIv.setVisible(true);
                                finalIv.setManaged(true);
                                if (finalInitials != null) {
                                    finalInitials.setVisible(false);
                                    finalInitials.setManaged(false);
                                }
                            }
                        });
                    } catch (Exception ignored) {}
                }).start();
            }
        }

        wrap.getChildren().addAll(iv, initials);
        return wrap;
    }

    private void addPostImages(VBox card, String imagePathsValue) {
        List<String> paths = splitPaths(safe(imagePathsValue));
        if (paths.isEmpty()) return;
        List<File> files = new ArrayList<>();
        for (String p : paths) {
            File f = new File(p.trim());
            if (f.exists()) files.add(f);
        }
        if (files.isEmpty()) return;

        double W = 520;
        double H = 420;

        if (files.size() == 1) {
            ImageView iv = new ImageView(new Image(files.get(0).toURI().toString(), true));
            iv.setFitWidth(W);
            iv.setPreserveRatio(true);
            card.getChildren().add(iv);
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(6); grid.setVgap(6);

        List<File> show = files.size() > 4 ? files.subList(0, 4) : files;
        for (int i = 0; i < show.size(); i++) {
            int r = i / 2, c = i % 2;
            ImageView iv = new ImageView(new Image(show.get(i).toURI().toString(), true));
            iv.setFitWidth(W/2 - 6); iv.setFitHeight(H/2 - 6);
            iv.setPreserveRatio(true); iv.setSmooth(true);
            StackPane cell = new StackPane(iv);
            cell.setMinSize(W/2 - 6, H/2 - 6);
            grid.add(cell, c, r);
            if (i == 3 && files.size() > 4) {
                Label more = new Label("+" + (files.size() - 4));
                more.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 22px; -fx-padding: 6 10; -fx-background-radius: 8;");
                StackPane.setAlignment(more, Pos.CENTER);
                cell.getChildren().add(more);
            }
        }
        card.getChildren().add(grid);
    }

    private String joinPaths(List<File> files) {
        List<String> ps = new ArrayList<>();
        for (File f : files) if (f != null) ps.add(f.getAbsolutePath());
        return String.join("||", ps);
    }
    private List<String> splitPaths(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();
        if (s.startsWith("[") && s.endsWith("]")) return parseJsonArray(s); // tolerance
        return Arrays.asList(s.split("\\|\\|"));
    }

    private boolean isOwner(int ownerUserId) {
        return currentUser != null && ownerUserId == currentUser.getUserId();
    }

    private String btnStyleNeutral() {
        return "-fx-background-color: transparent; -fx-text-fill: #0A153A; -fx-cursor: hand; -fx-font-weight: 700; -fx-font-size: 12px; -fx-padding: 8 14; -fx-background-radius: 14;";
    }

    private Label countPill(int n) {
        Label l = new Label(String.valueOf(n));
        l.getStyleClass().add("count-pill");
        return l;
    }
    private Label invisiblePill() {
        Label l = new Label();
        l.setManaged(false);
        l.setVisible(false);
        return l;
    }

    private void shareText(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        showAlert("Copied to clipboard ✔");
    }

    private void doSharePost(int postId) {
        if (currentUser != null) sharesService.addShareForPost(currentUser.getUserId(), postId);
        shareText("Post #" + postId + " on TripX — check it out!");
        refreshAll();
    }

    private void doShareStory(int storyId) {
        if (currentUser != null) sharesService.addShareForStory(currentUser.getUserId(), storyId);
        shareText("Travel Story #" + storyId + " on TripX — check it out!");
        refreshAll();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // =========================================================
    // Modal helpers
    // =========================================================
    private void hideAllForms() {
        if (modalDimmer != null) {
            modalDimmer.setVisible(false);
            modalDimmer.setManaged(false);
            modalDimmer.toBack();
        }
        hideBox(createPostForm);
        hideBox(createStoryForm);
        hideBox(editPostForm);
        hideBox(editStoryForm);
        hideBox(createInstaStoryForm);
    }

    private void hideBox(VBox box) {
        if (box == null) return;
        box.setVisible(false);
        box.setManaged(false);
    }

    /**
     * ✅ FIX: your previous code had nested method definitions (illegal start of expression)
     * ✅ FIX: ensure the modal card is ALWAYS above the dimmer
     * ✅ FIX: cap modal height so Create/Edit Travel Story footer stays visible on small windows
     */
    private void showModal(VBox form) {
        if (modalDimmer != null) {
            modalDimmer.setVisible(true);
            modalDimmer.setManaged(true);
            modalDimmer.setMouseTransparent(false);
            modalDimmer.toFront();
        }

        if (form != null) {
            form.setVisible(true);
            form.setManaged(true);
            form.setMouseTransparent(false);
            form.toFront();

            // Dynamically cap height to 90% of the window height after layout
            javafx.application.Platform.runLater(() -> {
                javafx.scene.Scene sc = form.getScene();
                if (sc == null && modalDimmer != null) sc = modalDimmer.getScene();
                if (sc != null && sc.getWindow() != null) {
                    double windowHeight = sc.getWindow().getHeight();
                    if (windowHeight > 0) {
                        // cap at 85% of window height, leaving room for top margin + taskbar
                        form.setMaxHeight(Math.min(form.getMaxHeight(), windowHeight * 0.85));
                    }
                }
                form.toFront();
            });
        }
    }

    private void resetCreatePostForm() {
        if (postTitleField != null) postTitleField.clear();
        if (postContentField != null) postContentField.clear();
        selectedPostImages.clear();
        refreshImagesUI(postImagesPreview, postImagesInfoLabel, selectedPostImages);
    }

    private void resetCreateStoryForm() {
        if (storyTitleField != null) storyTitleField.clear();
        if (storyCountryCombo != null) {
            storyCountryCombo.getSelectionModel().clearSelection();
            if (storyCountryCombo.getEditor() != null) storyCountryCombo.getEditor().clear();
        }
        if (storyDestinationField != null) {
            storyDestinationField.clear();
            storyDestinationField.setVisible(false);
            storyDestinationField.setManaged(false);
        }
        if (storySummaryField != null) storySummaryField.clear();
        if (storyTipsField != null) storyTipsField.clear();

        if (storyStartDatePicker != null) storyStartDatePicker.setValue(null);
        if (storyEndDatePicker != null) storyEndDatePicker.setValue(null);

        if (storyTravelTypeChoice != null && !storyTravelTypeChoice.getItems().isEmpty())
            storyTravelTypeChoice.setValue(storyTravelTypeChoice.getItems().get(0));
        if (storyTravelStyleChoice != null && !storyTravelStyleChoice.getItems().isEmpty())
            storyTravelStyleChoice.setValue(storyTravelStyleChoice.getItems().get(0));

        createStoryRating = 5;
        setStarRating(storyRatingStars, 5);

        if (storyWouldRecommendCheck != null) storyWouldRecommendCheck.setSelected(true);
        if (storyWouldGoAgainCheck != null) storyWouldGoAgainCheck.setSelected(false);

        selectedStoryImages.clear();
        refreshImagesUI(storyImagesPreview, storyImagesInfoLabel, selectedStoryImages);
    }

    private Stage getStageSafe() {
        if (feedContainer != null && feedContainer.getScene() != null) {
            return (Stage) feedContainer.getScene().getWindow();
        }
        if (createPostForm != null && createPostForm.getScene() != null) {
            return (Stage) createPostForm.getScene().getWindow();
        }
        return new Stage();
    }

    // =========================================================
    // Reflection helpers
    // =========================================================
    private Object getByAnyGetter(Object target, String... getterNames) {
        if (target == null) return null;
        for (String name : getterNames) {
            try {
                Method m = target.getClass().getMethod(name);
                return m.invoke(target);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void setByAnySetter(Object target, Object value, String... setterNames) {
        if (target == null) return;
        for (String name : setterNames) {
            if (tryInvokeSetter(target, name, value)) return;
        }
    }

    private boolean tryInvokeSetter(Object target, String setterName, Object value) {
        try {
            if (value == null) {
                for (Method m : target.getClass().getMethods()) {
                    if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                        m.invoke(target, new Object[]{null});
                        return true;
                    }
                }
                return false;
            }

            Class<?> vType = value.getClass();

            try {
                Method m = target.getClass().getMethod(setterName, vType);
                m.invoke(target, value);
                return true;
            } catch (NoSuchMethodException ignored) {}

            if (value instanceof Integer) {
                try {
                    Method m = target.getClass().getMethod(setterName, int.class);
                    m.invoke(target, (Integer) value);
                    return true;
                } catch (NoSuchMethodException ignored) {}
            }
            if (value instanceof Boolean) {
                try {
                    Method m = target.getClass().getMethod(setterName, boolean.class);
                    m.invoke(target, (Boolean) value);
                    return true;
                } catch (NoSuchMethodException ignored) {}
            }

            for (Method m : target.getClass().getMethods()) {
                if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                    Class<?> p = m.getParameterTypes()[0];
                    if (p.isAssignableFrom(vType)) {
                        m.invoke(target, value);
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String safeTextArea(TextArea ta) {
        return ta == null ? "" : safe(ta.getText());
    }

    private String safeChoice(ChoiceBox<String> cb) {
        return cb == null || cb.getValue() == null ? "" : safe(cb.getValue());
    }

    private Date toDate(DatePicker dp) {
        if (dp == null) return null;
        LocalDate ld = dp.getValue();
        if (ld == null) return null;
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date sd) {
            return sd.toLocalDate();
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void markInvalid(Control c, boolean invalid) {
        if (c == null) return;
        c.setStyle(invalid ? "-fx-border-color: #ef4444; -fx-border-width: 2; -fx-border-radius: 8;" : "");
    }

    // =========================================================
    // TravelStory UI helpers
    // =========================================================
    private void openImageViewer(File f) {
        try {
            Stage st = new Stage();
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: white; -fx-padding: 12;");

            ImageView iv = new ImageView(new Image(f.toURI().toString(), true));
            iv.setPreserveRatio(true);
            iv.setFitWidth(900);

            ScrollPane sp = new ScrollPane(iv);
            sp.setFitToWidth(true);
            sp.setStyle("-fx-background-color: transparent;");

            Button close = new Button("Close");
            close.setOnAction(e -> st.close());

            HBox top = new HBox(close);
            top.setAlignment(Pos.CENTER_RIGHT);
            top.setPadding(new Insets(0, 0, 10, 0));

            root.setTop(top);
            root.setCenter(sp);

            st.setScene(new Scene(root, 950, 700));
            st.setTitle("Photo");
            st.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private VBox buildStoryCover(TravelStory story) {
        String cover = safe((String) getByAnyGetter(story, "getCoverImageUrl"));
        VBox box = new VBox();

        if (!cover.isEmpty()) {
            box.getStyleClass().add("story-cover");
            try {
                File f = new File(cover);
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString(), true));
                    iv.setFitWidth(520);
                    iv.setFitHeight(220);
                    iv.setPreserveRatio(false);
                    iv.getStyleClass().add("story-cover-img");
                    box.getChildren().add(iv);
                    return box;
                }
            } catch (Exception ignored) {}
        }

        // No image -> collapse this area completely
        box.setManaged(false);
        box.setVisible(false);
        return box;
    }

    private Label pill(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().addAll("pill", styleClass);
        return l;
    }

    private String starsLabel(Integer rating) {
        if (rating == null) return "";
        int r = Math.max(0, Math.min(5, rating));
        return "★".repeat(r) + "☆".repeat(5 - r) + "  (" + r + "/5)";
    }

    private String composeDateLine(Date start, Date end) {
        if (start == null && end == null) return "";
        if (start != null && end != null) return "🗓 " + DATE_ONLY.format(start) + " → " + DATE_ONLY.format(end);
        if (start != null) return "🗓 From " + DATE_ONLY.format(start);
        return "🗓 Until " + DATE_ONLY.format(end);
    }

    private TitledPane buildTipsPane(String tips) {
        if (tips == null || tips.trim().isEmpty()) return null;

        Label tipsLbl = new Label(tips.trim());
        tipsLbl.setWrapText(true);
        tipsLbl.getStyleClass().add("story-tips-text");

        VBox content = new VBox(tipsLbl);
        content.setPadding(new Insets(8, 0, 0, 0));

        TitledPane tp = new TitledPane("💡 Tips", content);
        tp.setExpanded(false);
        tp.getStyleClass().add("story-tips");
        return tp;
    }

    // =========================================================
    // JSON helpers for image_urls_json
    // =========================================================
    private String toJsonArray(List<String> items) {
        if (items == null) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String it : items) {
            if (it == null) continue;
            String v = it.replace("\\", "\\\\").replace("\"", "\\\"");
            if (!first) sb.append(",");
            sb.append("\"").append(v).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private List<String> parseJsonArray(String json) {
        if (json == null) return Collections.emptyList();
        String s = json.trim();
        if (s.isEmpty() || s.equals("[]")) return Collections.emptyList();

        List<String> out = new ArrayList<>();
        Matcher m = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(s);
        while (m.find()) {
            String v = m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
            if (!v.trim().isEmpty()) out.add(v);
        }
        return out;
    }

    private String timeLeftText(Date expiresAt) {
        if (expiresAt == null) return "";
        long now = System.currentTimeMillis();
        long diff = Math.max(0, expiresAt.getTime() - now);
        long hours = diff / (1000 * 60 * 60);
        long minutes = (diff / (1000 * 60)) % 60;
        if (hours >= 1) return hours + "h left";
        return minutes + "m left";
    }
}