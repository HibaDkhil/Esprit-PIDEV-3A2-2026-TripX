package tn.esprit.controllers.admin;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlogManagementController {

    // ── Tab pane
    @FXML private TabPane mainTabPane;

    // ── Posts
    @FXML private TextField postSearchField;
    @FXML private Button postSearchBtn, addPostBtn, refreshPostsBtn;
    @FXML private TableView<Posts> postsTable;
    @FXML private TableColumn<Posts, Number>  postIdCol, postUserIdCol;
    @FXML private TableColumn<Posts, String>  postTitleCol, postBodyCol, postTypeCol;
    @FXML private TableColumn<Posts, Boolean> postConfirmedCol;
    @FXML private TableColumn<Posts, Void>    postActionsCol;

    // ── Travel Stories
    @FXML private TextField storySearchField;
    @FXML private Button storySearchBtn, addStoryBtn, refreshStoriesBtn;
    @FXML private TableView<TravelStory> storiesTable;
    @FXML private TableColumn<TravelStory, Number>  storyIdCol, storyUserIdCol;
    @FXML private TableColumn<TravelStory, String>  storyTitleCol, storyDestinationCol, storySummaryCol;
    @FXML private TableColumn<TravelStory, Number>  storyRatingCol;
    @FXML private TableColumn<TravelStory, Void>    storyActionsCol;

    // ── Comments
    @FXML private TextField commentSearchField;
    @FXML private Button commentSearchBtn, refreshCommentsBtn;
    @FXML private TableView<Comments> commentsTable;
    @FXML private TableColumn<Comments, Number> commentIdCol, commentUserIdCol, commentPostIdCol, commentStoryIdCol;
    @FXML private TableColumn<Comments, String> commentBodyCol;
    @FXML private TableColumn<Comments, Void>   commentActionsCol;

    // ── Instagram Stories
    @FXML private TextField igStorySearchField;
    @FXML private Button cleanExpiredStoriesBtn, refreshIgStoriesBtn;
    @FXML private TableView<Story> igStoriesTable;
    @FXML private TableColumn<Story, Number>  igStoryIdCol, igStoryUserIdCol;
    @FXML private TableColumn<Story, String>  igStoryCaptionCol, igStoryCreatedCol, igStoryExpiresCol;
    @FXML private TableColumn<Story, Void>    igStoryActionsCol;

    // ── Followings
    @FXML private TextField followSearchField;
    @FXML private Button refreshFollowsBtn, refreshStatsBtn;
    @FXML private TableView<Followings> followingsTable;
    @FXML private TableColumn<Followings, Number> followIdCol, followerIdCol, followedIdCol;
    @FXML private TableColumn<Followings, String> followerNameCol, followedNameCol, followCreatedCol;
    @FXML private TableColumn<Followings, Void>   followActionsCol;

    // ── Community Stats
    @FXML private Button rebuildAllStatsBtn, refreshStatsTableBtn;
    @FXML private TableView<CommunityStatistics> statsTable;
    @FXML private TableColumn<CommunityStatistics, Number> statsUserIdCol, statsPostsCol,
            statsCommentsCol, statsReactionsCol, statsFollowersCol;

    // ── Count Labels
    @FXML private Label postsCountLabel, storiesCountLabel, commentsCountLabel,
                        igStoriesCountLabel, followsCountLabel;

    // ── Services
    private PostService             postService;
    private TravelStoryService      travelStoryService;
    private CommentService          commentService;
    private StoryService            storyService;
    private FollowingsService       followingsService;
    private CommunityStatisticsService statsService;
    private UserService             userService;

    // ── Observable lists
    private final ObservableList<Posts>               postsList        = FXCollections.observableArrayList();
    private final ObservableList<TravelStory>         storiesList      = FXCollections.observableArrayList();
    private final ObservableList<Comments>            commentsList     = FXCollections.observableArrayList();
    private final ObservableList<Story>               igStoriesList    = FXCollections.observableArrayList();
    private final ObservableList<Followings>          followingsList   = FXCollections.observableArrayList();
    private final ObservableList<CommunityStatistics> statsList        = FXCollections.observableArrayList();

    private static final SimpleDateFormat DT = new SimpleDateFormat("dd/MM/yy HH:mm");

    public void setUserData(User user, String role) { /* optional role-based restriction */ }

    @FXML
    public void initialize() {
        postService    = new PostService();
        travelStoryService = new TravelStoryService();
        commentService = new CommentService();
        storyService   = new StoryService();
        followingsService = new FollowingsService();
        statsService   = new CommunityStatisticsService();
        userService    = new UserService();

        setupPostsTable();
        setupStoriesTable();
        setupCommentsTable();
        setupIgStoriesTable();
        setupFollowingsTable();
        setupStatsTable();

        setupSearch();
        setupButtons();

        loadAllData();
    }

    // =========================================================
    // TABLE SETUP
    // =========================================================
    private void setupPostsTable() {
        postIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        postUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        postTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        postBodyCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> abbreviate(c.getValue().getBody(), 55)));
        postTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        postConfirmedCol.setCellValueFactory(new PropertyValueFactory<>("confirmed"));
        postActionsCol.setCellFactory(col -> actionsCell(postsTable, "Moderate", "Delete",
                this::moderatePost, this::deletePost));
    }

    private void setupStoriesTable() {
        storyIdCol.setCellValueFactory(new PropertyValueFactory<>("travelStoryId"));
        storyUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        storyTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        storyDestinationCol.setCellValueFactory(new PropertyValueFactory<>("destinationText"));
        storySummaryCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> abbreviate(c.getValue().getSummary(), 50)));
        storyRatingCol.setCellValueFactory(new PropertyValueFactory<>("overallRating"));
        storyActionsCol.setCellFactory(col -> actionsCell(storiesTable, "Edit", "Delete",
                this::moderateStory, this::deleteStory));
    }

    private void setupCommentsTable() {
        commentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        commentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        commentPostIdCol.setCellValueFactory(new PropertyValueFactory<>("postId"));
        commentStoryIdCol.setCellValueFactory(new PropertyValueFactory<>("travelStoryId"));
        commentBodyCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> abbreviate(c.getValue().getBody(), 50)));
        commentActionsCol.setCellFactory(col -> actionsCell(commentsTable, "Edit", "Delete",
                this::moderateComment, this::deleteComment));
    }

    private void setupIgStoriesTable() {
        igStoryIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        igStoryUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        igStoryCaptionCol.setCellValueFactory(new PropertyValueFactory<>("caption"));
        igStoryCreatedCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> formatDate(c.getValue().getCreatedAt())));
        igStoryExpiresCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> formatDate(c.getValue().getExpiresAt())));
        igStoryActionsCol.setCellFactory(col -> singleActionCell(igStoriesTable, "Delete",
                "-fx-background-color: #dc2626;", this::deleteIgStory));
    }

    private void setupFollowingsTable() {
        followIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        followerIdCol.setCellValueFactory(new PropertyValueFactory<>("followerId"));
        followerNameCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> getUserName(c.getValue().getFollowerId())));
        followedIdCol.setCellValueFactory(new PropertyValueFactory<>("followedId"));
        followedNameCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> getUserName(c.getValue().getFollowedId())));
        followCreatedCol.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> formatDate(c.getValue().getCreatedAt())));
        followActionsCol.setCellFactory(col -> singleActionCell(followingsTable, "Remove",
                "-fx-background-color: #dc2626;", f -> {
                    if (!confirm("Remove this follow relationship?")) return;
                    if (followingsService.delete(f.getId())) {
                        showSuccess("Follow removed.");
                        loadFollowings();
                    } else showError("Failed to remove follow.");
                }));
    }

    private void setupStatsTable() {
        statsUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        statsPostsCol.setCellValueFactory(new PropertyValueFactory<>("postsCount"));
        statsCommentsCol.setCellValueFactory(new PropertyValueFactory<>("commentsCount"));
        statsReactionsCol.setCellValueFactory(new PropertyValueFactory<>("reactionsCount"));
        statsFollowersCol.setCellValueFactory(new PropertyValueFactory<>("followersCount"));
    }

    // =========================================================
    // GENERIC CELL FACTORIES
    // =========================================================
    private <T> TableCell<T, Void> actionsCell(
            TableView<T> table, String primaryText, String deleteText,
            RunnableWithItem<T> onPrimary, RunnableWithItem<T> onDelete) {
        return new TableCell<>() {
            private final Button primaryBtn = styledPastel(primaryText, true);
            private final Button deleteBtn  = styledPastel(deleteText, false);
            private final HBox pane = new HBox(8, primaryBtn, deleteBtn);
            { pane.setAlignment(Pos.CENTER); pane.setPadding(new Insets(4));
              primaryBtn.setOnAction(e -> run(onPrimary));
              deleteBtn .setOnAction(e -> run(onDelete)); }
            private void run(RunnableWithItem<T> action) {
                T item = getTableRow().getItem();
                if (item != null) action.run(item);
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private <T> TableCell<T, Void> singleActionCell(
            TableView<T> table, String text, String colorStyle, RunnableWithItem<T> action) {
        return new TableCell<>() {
            private final Button btn = styledPastel(text, false);
            private final HBox pane = new HBox(btn);
            { pane.setAlignment(Pos.CENTER); pane.setPadding(new Insets(4));
              btn.setOnAction(e -> { T item = getTableRow().getItem(); if (item != null) action.run(item); }); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private Button styled(String text, String bgHex) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bgHex +
                "; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
                " -fx-background-radius: 6; -fx-padding: 5 10;");
        return b;
    }

    /** Pastel table action buttons (styled via blog-admin.css). */
    private Button styledPastel(String text, boolean primary) {
        Button b = new Button(text);
        b.getStyleClass().addAll("table-action-btn", primary ? "pastel-primary" : "pastel-delete");
        b.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 12;");
        return b;
    }

    private interface RunnableWithItem<T> { void run(T item); }

    // =========================================================
    // LOAD / RELOAD
    // =========================================================
    private void loadAllData() {
        loadPosts();
        loadStories();
        loadComments();
        loadIgStories();
        loadFollowings();
        loadStats();
        updateCountLabels();
    }

    private void loadPosts() {
        postsList.setAll(safe(postService.findAll()));
        postsTable.setItems(postsList);
    }

    private void loadStories() {
        storiesList.setAll(safe(travelStoryService.findAll()));
        storiesTable.setItems(storiesList);
    }

    private void loadComments() {
        commentsList.setAll(safe(commentService.findAll()));
        commentsTable.setItems(commentsList);
    }

    private void loadIgStories() {
        igStoriesList.setAll(safe(storyService.findActiveStories()));
        igStoriesTable.setItems(igStoriesList);
    }

    private void loadFollowings() {
        followingsList.setAll(safe(followingsService.findAll()));
        followingsTable.setItems(followingsList);
    }

    private void loadStats() {
        List<CommunityStatistics> list = safe(statsService.findAll());
        if (list.isEmpty()) {
            statsService.refreshAll();
            list = safe(statsService.findAll());
        }
        statsList.setAll(list);
        statsTable.setItems(statsList);
    }

    private void updateCountLabels() {
        if (postsCountLabel    != null) postsCountLabel.setText(String.valueOf(postsList.size()));
        if (storiesCountLabel  != null) storiesCountLabel.setText(String.valueOf(storiesList.size()));
        if (commentsCountLabel != null) commentsCountLabel.setText(String.valueOf(commentsList.size()));
        if (igStoriesCountLabel!= null) igStoriesCountLabel.setText(String.valueOf(igStoriesList.size()));
        if (followsCountLabel  != null) followsCountLabel.setText(String.valueOf(followingsList.size()));
    }

    // =========================================================
    // SEARCH
    // =========================================================
    private void setupSearch() {
        if (postSearchBtn != null) postSearchBtn.setOnAction(e -> filterPosts(postSearchField.getText()));
        if (postSearchField != null) postSearchField.textProperty().addListener((o, a, b) -> filterPosts(b));

        if (storySearchBtn != null) storySearchBtn.setOnAction(e -> filterStories(storySearchField.getText()));
        if (storySearchField != null) storySearchField.textProperty().addListener((o, a, b) -> filterStories(b));

        if (commentSearchBtn != null) commentSearchBtn.setOnAction(e -> filterComments(commentSearchField.getText()));
        if (commentSearchField != null) commentSearchField.textProperty().addListener((o, a, b) -> filterComments(b));

        if (igStorySearchField != null) igStorySearchField.textProperty().addListener((o, a, b) -> filterIgStories(b));

        if (followSearchField != null) followSearchField.textProperty().addListener((o, a, b) -> filterFollowings(b));
    }

    private void filterPosts(String kw) {
        if (kw == null || kw.isBlank()) { postsTable.setItems(postsList); return; }
        String k = kw.toLowerCase();
        postsTable.setItems(FXCollections.observableArrayList(
                postsList.stream().filter(p ->
                        has(p.getTitle(), k) || has(p.getBody(), k) || has(p.getType(), k)
                ).collect(Collectors.toList())));
    }

    private void filterStories(String kw) {
        if (kw == null || kw.isBlank()) { storiesTable.setItems(storiesList); return; }
        String k = kw.toLowerCase();
        storiesTable.setItems(FXCollections.observableArrayList(
                storiesList.stream().filter(s ->
                        has(s.getTitle(), k) || has(s.getDestinationText(), k) || has(s.getSummary(), k)
                ).collect(Collectors.toList())));
    }

    private void filterComments(String kw) {
        if (kw == null || kw.isBlank()) { commentsTable.setItems(commentsList); return; }
        String k = kw.toLowerCase();
        commentsTable.setItems(FXCollections.observableArrayList(
                commentsList.stream().filter(c -> has(c.getBody(), k)).collect(Collectors.toList())));
    }

    private void filterIgStories(String kw) {
        if (kw == null || kw.isBlank()) { igStoriesTable.setItems(igStoriesList); return; }
        String k = kw.toLowerCase();
        igStoriesTable.setItems(FXCollections.observableArrayList(
                igStoriesList.stream().filter(s ->
                        has(s.getCaption(), k) || String.valueOf(s.getUserId()).contains(k)
                ).collect(Collectors.toList())));
    }

    private void filterFollowings(String kw) {
        if (kw == null || kw.isBlank()) { followingsTable.setItems(followingsList); return; }
        String k = kw.toLowerCase();
        followingsTable.setItems(FXCollections.observableArrayList(
                followingsList.stream().filter(f ->
                        String.valueOf(f.getFollowerId()).contains(k) ||
                        String.valueOf(f.getFollowedId()).contains(k)
                ).collect(Collectors.toList())));
    }

    private boolean has(String s, String k) { return s != null && s.toLowerCase().contains(k); }

    // =========================================================
    // BUTTONS
    // =========================================================
    private void setupButtons() {
        if (addPostBtn    != null) addPostBtn.setOnAction(e -> showAddPostDialog());
        if (addStoryBtn   != null) addStoryBtn.setOnAction(e -> showAddStoryDialog());

        if (refreshPostsBtn     != null) refreshPostsBtn.setOnAction(e -> { loadPosts();    updateCountLabels(); });
        if (refreshStoriesBtn   != null) refreshStoriesBtn.setOnAction(e -> { loadStories(); updateCountLabels(); });
        if (refreshCommentsBtn  != null) refreshCommentsBtn.setOnAction(e -> { loadComments(); updateCountLabels(); });
        if (refreshIgStoriesBtn != null) refreshIgStoriesBtn.setOnAction(e -> { loadIgStories(); updateCountLabels(); });
        if (refreshFollowsBtn   != null) refreshFollowsBtn.setOnAction(e -> { loadFollowings(); updateCountLabels(); });
        if (refreshStatsBtn     != null) refreshStatsBtn.setOnAction(e -> { loadStats(); });
        if (refreshStatsTableBtn!= null) refreshStatsTableBtn.setOnAction(e -> loadStats());

        if (cleanExpiredStoriesBtn != null) cleanExpiredStoriesBtn.setOnAction(e -> {
            int deleted = storyService.cleanupExpiredStories();
            showSuccess("Removed " + deleted + " expired story/stories.");
            loadIgStories();
            updateCountLabels();
        });

        if (rebuildAllStatsBtn != null) rebuildAllStatsBtn.setOnAction(e -> {
            statsService.refreshAll();
            loadStats();
            showSuccess("Community statistics rebuilt for all users.");
        });
    }

    // =========================================================
    // MODERATE / EDIT DIALOGS
    // =========================================================
    private void moderatePost(Posts p) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Moderate Post");
        d.setHeaderText("Review, edit, approve or reject this post");

        ButtonType approve = new ButtonType("✔ Approve", ButtonBar.ButtonData.OK_DONE);
        ButtonType reject  = new ButtonType("✘ Reject",  ButtonBar.ButtonData.OTHER);
        ButtonType save    = new ButtonType("💾 Save",    ButtonBar.ButtonData.APPLY);
        d.getDialogPane().getButtonTypes().setAll(approve, reject, save, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(560);

        GridPane grid = buildGrid();
        TextField titleField = field(safe(p.getTitle()), "Title");
        TextArea  bodyField  = area(safe(p.getBody()),   "Content", 6);
        TextField typeField  = field(safe(p.getType()),  "Type (inquiry/story/review/advice/other)");
        CheckBox  confirmedCb = new CheckBox("Approved");
        confirmedCb.setSelected(p.isConfirmed());

        grid.add(new Label("Title:"),   0, 0); grid.add(titleField,  1, 0);
        grid.add(new Label("Type:"),    0, 1); grid.add(typeField,   1, 1);
        grid.add(new Label("Content:"), 0, 2); grid.add(bodyField,   1, 2);
        grid.add(confirmedCb,           1, 3);
        d.getDialogPane().setContent(grid);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty()) return;
        ButtonType bt = res.get();

        if (bt == approve) {
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), true);
            showSuccess("Post approved.");
        } else if (bt == reject) {
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), false);
            showSuccess("Post rejected.");
        } else if (bt == save) {
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), confirmedCb.isSelected());
            showSuccess("Post updated.");
        }
        loadPosts();
        updateCountLabels();
    }

    private void applyPostChanges(Posts p, String title, String body, String type, boolean confirmed) {
        p.setTitle(safe(title));
        p.setBody(safe(body));
        p.setType(safe(type).isEmpty() ? "other" : safe(type));
        p.setConfirmed(confirmed);
        if (!postService.update(p)) showError("Failed to update post.");
    }

    private void moderateStory(TravelStory s) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Edit Travel Story");
        d.setHeaderText("Story ID: " + s.getTravelStoryId() + " | User: " + s.getUserId());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(520);

        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        TextField titleField = field(safe(s.getTitle()), "Title");
        TextField destField  = field(safe(s.getDestinationText()), "Destination");
        TextArea  summField  = area(safe(s.getSummary()), "Summary", 5);
        TextArea  tipsField  = area(safe(s.getTips()), "Tips", 3);

        box.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Destination:"), destField,
                new Label("Summary:"), summField,
                new Label("Tips:"), tipsField
        );
        d.getDialogPane().setContent(box);

        if (d.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        s.setTitle(safe(titleField.getText()));
        s.setDestinationText(safe(destField.getText()));
        s.setSummary(safe(summField.getText()));
        s.setTips(safe(tipsField.getText()));
        if (travelStoryService.update(s)) { showSuccess("Travel story updated."); loadStories(); }
        else showError("Failed to update travel story.");
    }

    private void moderateComment(Comments c) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Edit Comment");
        d.setHeaderText("Comment #" + c.getId() + " — User: " + c.getUserId());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea bodyField = area(safe(c.getBody()), "Comment content", 5);
        VBox box = new VBox(8, new Label("Body:"), bodyField);
        box.setPadding(new Insets(12));
        d.getDialogPane().setContent(box);

        if (d.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        c.setBody(safe(bodyField.getText()));
        if (commentService.update(c)) { showSuccess("Comment updated."); loadComments(); }
        else showError("Failed to update comment.");
    }

    private void deleteIgStory(Story s) {
        if (!confirm("Delete this story (user " + s.getUserId() + ")?")) return;
        if (storyService.delete(s.getId())) { showSuccess("Story deleted."); loadIgStories(); updateCountLabels(); }
        else showError("Failed to delete story.");
    }

    // =========================================================
    // DELETE
    // =========================================================
    private void deletePost(Posts p) {
        if (!confirm("Delete post \"" + safe(p.getTitle()) + "\"?")) return;
        if (postService.delete(p.getId())) { showSuccess("Post deleted."); loadPosts(); updateCountLabels(); }
        else showError("Failed to delete post.");
    }

    private void deleteStory(TravelStory s) {
        if (!confirm("Delete travel story \"" + safe(s.getTitle()) + "\"?")) return;
        if (travelStoryService.delete(s.getTravelStoryId())) { showSuccess("Travel story deleted."); loadStories(); updateCountLabels(); }
        else showError("Failed to delete travel story.");
    }

    private void deleteComment(Comments c) {
        if (!confirm("Delete this comment?")) return;
        if (commentService.delete(c.getId())) { showSuccess("Comment deleted."); loadComments(); updateCountLabels(); }
        else showError("Failed to delete comment.");
    }

    // =========================================================
    // ADD DIALOGS
    // =========================================================
    private void showAddPostDialog() {
        List<User> users = getUsers();
        if (users == null || users.isEmpty()) { showError("No users found."); return; }

        Dialog<Boolean> d = new Dialog<>();
        d.setTitle("Add Post");
        d.setHeaderText("Create a new community post");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(520);

        GridPane grid = buildGrid();
        ComboBox<User> userCombo = userCombo(users);
        TextField titleField = field("", "Title");
        TextArea  bodyField  = area("", "Content", 5);
        ChoiceBox<String> typeBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "inquiry","story","review","advice","other"));
        typeBox.setValue("other");
        CheckBox confirmedCb = new CheckBox("Approve immediately");

        grid.add(new Label("User:"),    0, 0); grid.add(userCombo,  1, 0);
        grid.add(new Label("Title:"),   0, 1); grid.add(titleField, 1, 1);
        grid.add(new Label("Type:"),    0, 2); grid.add(typeBox,    1, 2);
        grid.add(new Label("Content:"), 0, 3); grid.add(bodyField,  1, 3);
        grid.add(confirmedCb,           1, 4);
        d.getDialogPane().setContent(grid);
        d.setResultConverter(btn -> btn == ButtonType.OK);

        if (!d.showAndWait().orElse(false)) return;
        User u = userCombo.getSelectionModel().getSelectedItem();
        if (u == null) return;
        String title = safe(titleField.getText());
        if (title.isEmpty()) { showError("Title is required."); return; }

        Posts p = new Posts(u.getUserId(), 0, title, safe(bodyField.getText()),
                typeBox.getValue() == null ? "other" : typeBox.getValue());
        p.setConfirmed(confirmedCb.isSelected());
        if (postService.create(p)) { showSuccess("Post created."); loadPosts(); updateCountLabels(); }
        else showError("Failed to create post.");
    }

    private void showAddStoryDialog() {
        List<User> users = getUsers();
        if (users == null || users.isEmpty()) { showError("No users found."); return; }

        Dialog<Boolean> d = new Dialog<>();
        d.setTitle("Add Travel Story");
        d.setHeaderText("Create a new travel story");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(520);

        GridPane grid = buildGrid();
        ComboBox<User> userCombo = userCombo(users);
        TextField titleField = field("", "Title");
        TextField destField  = field("", "Destination");
        TextArea  summField  = area("", "Summary", 4);

        grid.add(new Label("User:"),        0, 0); grid.add(userCombo,  1, 0);
        grid.add(new Label("Title:"),       0, 1); grid.add(titleField, 1, 1);
        grid.add(new Label("Destination:"), 0, 2); grid.add(destField,  1, 2);
        grid.add(new Label("Summary:"),     0, 3); grid.add(summField,  1, 3);
        d.getDialogPane().setContent(grid);
        d.setResultConverter(btn -> btn == ButtonType.OK);

        if (!d.showAndWait().orElse(false)) return;
        User u = userCombo.getSelectionModel().getSelectedItem();
        if (u == null) return;
        String title = safe(titleField.getText());
        if (title.isEmpty()) { showError("Title is required."); return; }

        TravelStory s = new TravelStory(u.getUserId(), title, safe(summField.getText()), safe(destField.getText()));
        if (travelStoryService.create(s)) { showSuccess("Travel story created."); loadStories(); updateCountLabels(); }
        else showError("Failed to create travel story.");
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private List<User> getUsers() {
        try { return userService.getAllUsers(); }
        catch (Exception e) { return java.util.Collections.emptyList(); }
    }

    private String getUserName(int userId) {
        try {
            User u = userService.findById(userId);
            if (u != null) return u.getFirstName() + " " + u.getLastName();
        } catch (Exception ignored) {}
        return "User #" + userId;
    }

    private String formatDate(Date d) {
        if (d == null) return "-";
        return DT.format(d);
    }

    private ComboBox<User> userCombo(List<User> users) {
        ComboBox<User> cb = new ComboBox<>(FXCollections.observableArrayList(users));
        cb.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(User u) { return u == null ? "" : u.getEmail() + " (ID:" + u.getUserId() + ")"; }
            @Override public User fromString(String s) { return null; }
        });
        cb.getSelectionModel().selectFirst();
        return cb;
    }

    private GridPane buildGrid() {
        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setMinWidth(100);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2);
        g.setPadding(new Insets(12));
        return g;
    }

    private TextField field(String text, String prompt) {
        TextField tf = new TextField(text);
        tf.setPromptText(prompt);
        return tf;
    }

    private TextArea area(String text, String prompt, int rows) {
        TextArea ta = new TextArea(text);
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setWrapText(true);
        return ta;
    }

    private <T> List<T> safe(List<T> list) {
        return list == null ? java.util.Collections.emptyList() : list;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static String abbreviate(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max) + "…";
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm"); a.setHeaderText(null); a.setContentText(msg);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
