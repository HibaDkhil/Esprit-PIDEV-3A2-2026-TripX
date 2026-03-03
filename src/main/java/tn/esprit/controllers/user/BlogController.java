package tn.esprit.controllers.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Comments;
import tn.esprit.entities.Posts;
import tn.esprit.entities.Story;
import tn.esprit.entities.TravelStory;
import tn.esprit.entities.User;
import tn.esprit.entities.Country;
import tn.esprit.services.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlogController implements Initializable {

    // Top/search
    @FXML private TextField searchField;

    // Sidebar
    @FXML private ImageView profileImageView;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label postsCountLabel;
    @FXML private Label followersCountLabel;
    @FXML private Label followingCountLabel;

    // Instagram stories row (bubbles)
    @FXML private HBox storiesRow;
    @FXML private VBox storiesSection;
    @FXML private ScrollPane storiesScroll;

    // Main content containers
    @FXML private VBox feedContainer;
    @FXML private VBox myPostsContainer;
    @FXML private VBox myStoriesContainer;
    @FXML private VBox savedContainer;

    // View switch scroll panes
    @FXML private ScrollPane feedScroll;
    @FXML private ScrollPane myPostsScroll;
    @FXML private ScrollPane myStoriesScroll;
    @FXML private ScrollPane savedScroll;

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
    @FXML private TextArea storySummaryField;
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

        setupTravelStoryFormControls();
        setupCountryCombos();

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
            storyCountryCombo.setEditable(false);
            Platform.runLater(() -> bindCountryCombo(storyCountryCombo, items));
        }
        if (editStoryCountryCombo != null) {
            editStoryCountryCombo.setEditable(false);
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
        refreshAll();
    }

    // =========================================================
    // Sidebar Navigation
    // =========================================================
    @FXML public void goFeed() { showOnly(feedScroll); refreshFeed(); if (feedScroll != null) updateStoriesFade(feedScroll.getVvalue()); }
    @FXML public void goExplore() { showOnly(feedScroll); refreshFeed(); if (feedScroll != null) updateStoriesFade(feedScroll.getVvalue()); }
    @FXML public void goStories() { showOnly(myStoriesScroll); refreshMyStories(); } // TravelStory list
    @FXML public void goSaved() { showOnly(savedScroll); refreshSaved(); }

    private void showOnly(ScrollPane target) {
        if (feedScroll != null) { feedScroll.setVisible(false); feedScroll.setManaged(false); }
        if (myPostsScroll != null) { myPostsScroll.setVisible(false); myPostsScroll.setManaged(false); }
        if (myStoriesScroll != null) { myStoriesScroll.setVisible(false); myStoriesScroll.setManaged(false); }
        if (savedScroll != null) { savedScroll.setVisible(false); savedScroll.setManaged(false); }

        if (target != null) { target.setVisible(true); target.setManaged(true); }
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
    }

    private void refreshFeed() {
        if (feedContainer == null) return;
        feedContainer.getChildren().clear();

        List<Object> items = new ArrayList<>();
        items.addAll(safeList(postService.findAll()));
        items.addAll(safeList(travelStoryService.findAll()));

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

        Label empty = new Label("No saved posts yet.");
        empty.getStyleClass().add("muted");
        empty.setStyle("-fx-padding: 10;");
        savedContainer.getChildren().add(empty);
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

        followersCountLabel.setText("0");
        followingCountLabel.setText("0");
    }

    // =========================================================
    // Instagram Stories bubbles (NOT TravelStory)
    // =========================================================
    private void loadStoriesBubbles() {
        if (storiesRow == null) return;

        storiesRow.getChildren().clear();
        storiesRow.getChildren().add(createAddStoryBubble());

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
        if (myLatest != null) {
            storiesRow.getChildren().add(createStoryBubble(myLatest));
        }
        for (Story s : latestByUser.values()) {
            storiesRow.getChildren().add(createStoryBubble(s));
        }
    }

    private Node createAddStoryBubble() {
        StackPane outer = new StackPane();
        outer.getStyleClass().add("story-bubble");

        StackPane inner = new StackPane();
        inner.getStyleClass().add("story-image-wrapper");

        Label plus = new Label("+");
        plus.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #4F46E5;");
        inner.getChildren().add(plus);
        outer.getChildren().add(inner);

        outer.setOnMouseClicked(e -> handleShowCreateInstaStory());

        Label name = new Label("Add");
        name.getStyleClass().add("story-bubble-username");

        VBox tile = new VBox(4, outer, name);
        tile.setAlignment(Pos.CENTER);
        return tile;
    }

    private Node createStoryBubble(Story story) {
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
        String summary = safeTextArea(editStorySummaryField);

        boolean invalidDest = dest.isEmpty();
        markInvalid(editStoryCountryCombo, invalidDest);
        if (title.isEmpty() || summary.isEmpty() || invalidDest) {
            showAlert(invalidDest ? "Please select a destination country." : "Please fill title and summary.");
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
        String timeText = post.getCreatedAt() != null ? SDF.format(post.getCreatedAt()) : "";

        HBox header = buildHeader(
                authorName, timeText,
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
        Date createdAt = (Date) getByAnyGetter(story, "getCreatedAt");
        String timeText = createdAt != null ? SDF.format(createdAt) : "";

        HBox header = buildHeader(
                authorName, timeText,
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
        reactBtn.setStyle(btnStyleNeutral());

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(
                postReactItem("👍 Like", ReactionsService.ReactType.LIKE, postId),
                postReactItem("❤️ Love", ReactionsService.ReactType.LOVE, postId),
                postReactItem("😂 Haha", ReactionsService.ReactType.HAHA, postId),
                postReactItem("😮 Wow",  ReactionsService.ReactType.WOW,  postId),
                postReactItem("😢 Sad",  ReactionsService.ReactType.SAD,  postId),
                postReactItem("😡 Angry",ReactionsService.ReactType.ANGRY,postId)
        );
        reactBtn.setOnAction(e -> menu.show(reactBtn, Side.BOTTOM, 0, 0));

        Button commentBtn = new Button("Comment");
        commentBtn.setStyle(btnStyleNeutral());
        commentBtn.setOnAction(e -> cs.openAndFocus());

        Button shareBtn = new Button("Share");
        shareBtn.setStyle(btnStyleNeutral());
        shareBtn.setOnAction(e -> shareText("Post #" + postId));

        int reacts = reactionsService.countReactionsForPost(postId);
        int comments = safeList(commentService.findByPostId(postId)).size();
        int shares = sharesService.countSharesForPost(postId);

        Label reactsLbl = reacts > 0 ? countPill(reacts) : invisiblePill();
        Label commentsLbl = comments > 0 ? countPill(comments) : invisiblePill();
        Label sharesLbl = shares > 0 ? countPill(shares) : invisiblePill();

        HBox row = new HBox(12, reactBtn, reactsLbl, commentBtn, commentsLbl, shareBtn, sharesLbl);
        row.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: #eef2f7; -fx-border-width: 1 0 0 0;");
        return row;
    }

    private HBox buildActionsRowStory(int storyId, CommentsSection cs) {
        Button reactBtn = new Button(buildReactLabelForStory(storyId));
        reactBtn.setStyle(btnStyleNeutral());

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(
                storyReactItem("👍 Like", ReactionsService.ReactType.LIKE, storyId),
                storyReactItem("❤️ Love", ReactionsService.ReactType.LOVE, storyId),
                storyReactItem("😂 Haha", ReactionsService.ReactType.HAHA, storyId),
                storyReactItem("😮 Wow",  ReactionsService.ReactType.WOW,  storyId),
                storyReactItem("😢 Sad",  ReactionsService.ReactType.SAD,  storyId),
                storyReactItem("😡 Angry",ReactionsService.ReactType.ANGRY,storyId)
        );
        reactBtn.setOnAction(e -> menu.show(reactBtn, Side.BOTTOM, 0, 0));

        Button commentBtn = new Button("Comment");
        commentBtn.setStyle(btnStyleNeutral());
        commentBtn.setOnAction(e -> cs.openAndFocus());

        Button shareBtn = new Button("Share");
        shareBtn.setStyle(btnStyleNeutral());
        shareBtn.setOnAction(e -> shareText("Travel Story #" + storyId));

        int reacts = reactionsService.countReactionsForStory(storyId);
        int comments = safeList(commentService.findByTravelStoryId(storyId)).size();
        int shares = sharesService.countSharesForStory(storyId);

        Label reactsLbl = reacts > 0 ? countPill(reacts) : invisiblePill();
        Label commentsLbl = comments > 0 ? countPill(comments) : invisiblePill();
        Label sharesLbl = shares > 0 ? countPill(shares) : invisiblePill();

        HBox row = new HBox(12, reactBtn, reactsLbl, commentBtn, commentsLbl, shareBtn, sharesLbl);
        row.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: #eef2f7; -fx-border-width: 1 0 0 0;");
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
        it.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            reactionsService.toggleReactionForPost(currentUser.getUserId(), postId, type);
            refreshAll();
        });
        return it;
    }

    private MenuItem storyReactItem(String text, ReactionsService.ReactType type, int storyId) {
        MenuItem it = new MenuItem(text);
        it.setOnAction(e -> {
            if (currentUser == null) { showAlert("No user session found."); return; }
            reactionsService.toggleReactionForStory(currentUser.getUserId(), storyId, type);
            refreshAll();
        });
        return it;
    }

    private MenuItem commentReactItem(String text, ReactionsService.ReactType type, int commentId) {
        MenuItem it = new MenuItem(text);
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
        toggle.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 12px;");

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
        send.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 10;");

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
        toggle.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 12px;");

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
        send.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 10;");

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
        replyBtn.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-background-radius: 10;");

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
        bubble.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 8 10; -fx-background-radius: 12;");

        User u = userService.findById(c.getUserId());
        String uname = u != null ? (safe(u.getFirstName()) + " " + safe(u.getLastName())).trim() : "Unknown";

        Label name = new Label(uname);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #111827;");

        Label bodyLbl = new Label(safe(c.getBody()));
        bodyLbl.setWrapText(true);
        bodyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #111827;");

        Button reactBtn = new Button(buildReactLabelForComment(c.getId()));
        reactBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: 700; -fx-padding: 0;");

        ContextMenu reactMenu = new ContextMenu();
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

    private HBox buildHeader(String authorName, String timeText,
                             Runnable onEdit, Runnable onDelete,
                             boolean showActions) {

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Region avatar = new Region();
        avatar.setStyle("-fx-min-width: 42; -fx-min-height: 42; -fx-background-radius: 999; -fx-background-color: #dbeafe;");

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

        header.getChildren().addAll(avatar, meta, spacer, actions);
        return header;
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
        return "-fx-background-color: transparent; -fx-cursor: hand; -fx-font-weight: 700; -fx-padding: 8 12; -fx-background-radius: 10;";
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
