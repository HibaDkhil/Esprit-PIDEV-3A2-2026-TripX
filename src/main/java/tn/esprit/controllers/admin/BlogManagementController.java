package tn.esprit.controllers.admin;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Comments;
import tn.esprit.entities.Posts;
import tn.esprit.entities.TravelStory;
import tn.esprit.entities.User;
import tn.esprit.services.CommentService;
import tn.esprit.services.PostService;
import tn.esprit.services.TravelStoryService;
import tn.esprit.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlogManagementController {

    @FXML private TabPane mainTabPane;

    @FXML private TextField postSearchField, storySearchField, commentSearchField;
    @FXML private Button postSearchBtn, addPostBtn, storySearchBtn, addStoryBtn, commentSearchBtn;

    @FXML private TableView<Posts> postsTable;
    @FXML private TableColumn<Posts, Number> postIdCol, postUserIdCol;
    @FXML private TableColumn<Posts, String> postTitleCol, postBodyCol, postTypeCol;
    @FXML private TableColumn<Posts, Boolean> postConfirmedCol;
    @FXML private TableColumn<Posts, Void> postActionsCol;

    @FXML private TableView<TravelStory> storiesTable;
    @FXML private TableColumn<TravelStory, Number> storyIdCol, storyUserIdCol;
    @FXML private TableColumn<TravelStory, String> storyTitleCol, storyDestinationCol;

    // ✅ UPDATED: content column replaced by summary column
    @FXML private TableColumn<TravelStory, String> storySummaryCol;

    @FXML private TableColumn<TravelStory, Void> storyActionsCol;

    @FXML private TableView<Comments> commentsTable;
    @FXML private TableColumn<Comments, Number> commentIdCol, commentUserIdCol, commentPostIdCol, commentStoryIdCol;
    @FXML private TableColumn<Comments, String> commentBodyCol;
    @FXML private TableColumn<Comments, Void> commentActionsCol;

    @FXML private Label postsCountLabel, storiesCountLabel, commentsCountLabel;

    private PostService postService;
    private TravelStoryService travelStoryService;
    private CommentService commentService;
    private UserService userService;

    private final ObservableList<Posts> postsList = FXCollections.observableArrayList();
    private final ObservableList<TravelStory> storiesList = FXCollections.observableArrayList();
    private final ObservableList<Comments> commentsList = FXCollections.observableArrayList();

    public void setUserData(User user, String role) {
        // Optional: restrict actions by role
    }

    @FXML
    public void initialize() {
        postService = new PostService();
        travelStoryService = new TravelStoryService();
        commentService = new CommentService();
        userService = new UserService();

        setupPostsTable();
        setupStoriesTable();
        setupCommentsTable();

        setupSearch();
        setupAddButtons();

        loadAllData();
    }

    // =========================================================
    // TABLE SETUP
    // =========================================================
    private void setupPostsTable() {
        postIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        postUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        postTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        postBodyCol.setCellValueFactory(cell ->
                Bindings.createStringBinding(() -> abbreviate(cell.getValue().getBody(), 60)));

        postTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        postConfirmedCol.setCellValueFactory(new PropertyValueFactory<>("confirmed"));

        postActionsCol.setCellFactory(col ->
                actionsCell(postsTable, "Moderate", "Delete", this::moderatePost, this::deletePost));
    }

    private void setupStoriesTable() {
        storyIdCol.setCellValueFactory(new PropertyValueFactory<>("travelStoryId"));
        storyUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        storyTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        // ✅ destination -> destinationText
        storyDestinationCol.setCellValueFactory(new PropertyValueFactory<>("destinationText"));

        // ✅ content -> summary
        storySummaryCol.setCellValueFactory(cell ->
                Bindings.createStringBinding(() -> abbreviate(cell.getValue().getSummary(), 50)));

        storyActionsCol.setCellFactory(col ->
                actionsCell(storiesTable, "Moderate", "Delete", this::moderateStory, this::deleteStory));
    }

    private void setupCommentsTable() {
        commentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        commentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        commentPostIdCol.setCellValueFactory(new PropertyValueFactory<>("postId"));
        commentStoryIdCol.setCellValueFactory(new PropertyValueFactory<>("travelStoryId"));

        commentBodyCol.setCellValueFactory(cell ->
                Bindings.createStringBinding(() -> abbreviate(cell.getValue().getBody(), 50)));

        commentActionsCol.setCellFactory(col ->
                actionsCell(commentsTable, "Moderate", "Delete", this::moderateComment, this::deleteComment));
    }

    // =========================================================
    // GENERIC ACTIONS CELL
    // =========================================================
    private <T> TableCell<T, Void> actionsCell(
            TableView<T> table,
            String primaryText,
            String deleteText,
            RunnableWithItem<T> onPrimary,
            RunnableWithItem<T> onDelete
    ) {
        return new TableCell<>() {
            private final Button primaryBtn = new Button(primaryText);
            private final Button deleteBtn = new Button(deleteText);
            private final HBox pane = new HBox(8, primaryBtn, deleteBtn);

            {
                primaryBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 5 10;");

                pane.setAlignment(Pos.CENTER);
                pane.setPadding(new Insets(5));

                primaryBtn.setOnAction(e -> {
                    T item = getTableRow().getItem();
                    if (item != null) onPrimary.run(item);
                });

                deleteBtn.setOnAction(e -> {
                    T item = getTableRow().getItem();
                    if (item != null) onDelete.run(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private interface RunnableWithItem<T> { void run(T item); }

    // =========================================================
    // LOAD + COUNTS
    // =========================================================
    private void loadAllData() {
        postsList.setAll(safeList(postService.findAll()));
        storiesList.setAll(safeList(travelStoryService.findAll()));
        commentsList.setAll(safeList(commentService.findAll()));

        postsTable.setItems(postsList);
        storiesTable.setItems(storiesList);
        commentsTable.setItems(commentsList);

        if (postsCountLabel != null) postsCountLabel.setText(String.valueOf(postsList.size()));
        if (storiesCountLabel != null) storiesCountLabel.setText(String.valueOf(storiesList.size()));
        if (commentsCountLabel != null) commentsCountLabel.setText(String.valueOf(commentsList.size()));
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? java.util.Collections.emptyList() : list;
    }

    // =========================================================
    // SEARCH
    // =========================================================
    private void setupSearch() {
        postSearchBtn.setOnAction(e -> filterPosts(postSearchField.getText()));
        postSearchField.textProperty().addListener((o, a, b) -> filterPosts(b));

        storySearchBtn.setOnAction(e -> filterStories(storySearchField.getText()));
        storySearchField.textProperty().addListener((o, a, b) -> filterStories(b));

        commentSearchBtn.setOnAction(e -> filterComments(commentSearchField.getText()));
        commentSearchField.textProperty().addListener((o, a, b) -> filterComments(b));
    }

    private void filterPosts(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            postsTable.setItems(postsList);
            return;
        }
        String k = kw.toLowerCase().trim();
        ObservableList<Posts> filtered = FXCollections.observableArrayList(
                postsList.stream().filter(p ->
                        (p.getTitle() != null && p.getTitle().toLowerCase().contains(k)) ||
                                (p.getBody() != null && p.getBody().toLowerCase().contains(k)) ||
                                (p.getType() != null && p.getType().toLowerCase().contains(k))
                ).collect(Collectors.toList())
        );
        postsTable.setItems(filtered);
    }

    private void filterStories(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            storiesTable.setItems(storiesList);
            return;
        }
        String k = kw.toLowerCase().trim();
        ObservableList<TravelStory> filtered = FXCollections.observableArrayList(
                storiesList.stream().filter(s ->
                        (s.getTitle() != null && s.getTitle().toLowerCase().contains(k)) ||
                                (s.getDestinationText() != null && s.getDestinationText().toLowerCase().contains(k)) ||
                                (s.getSummary() != null && s.getSummary().toLowerCase().contains(k))
                ).collect(Collectors.toList())
        );
        storiesTable.setItems(filtered);
    }

    private void filterComments(String kw) {
        if (kw == null || kw.trim().isEmpty()) {
            commentsTable.setItems(commentsList);
            return;
        }
        String k = kw.toLowerCase().trim();
        ObservableList<Comments> filtered = FXCollections.observableArrayList(
                commentsList.stream().filter(c ->
                        c.getBody() != null && c.getBody().toLowerCase().contains(k)
                ).collect(Collectors.toList())
        );
        commentsTable.setItems(filtered);
    }

    // =========================================================
    // ADD BUTTONS
    // =========================================================
    private void setupAddButtons() {
        addPostBtn.setOnAction(e -> showAddPostDialog());
        addStoryBtn.setOnAction(e -> showAddStoryDialog());
    }

    // =========================================================
    // MODERATE DIALOGS
    // =========================================================
    private void moderatePost(Posts p) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Moderate Post");
        d.setHeaderText("Review / Approve / Reject");

        ButtonType approve = new ButtonType("Approve", ButtonBar.ButtonData.OK_DONE);
        ButtonType reject  = new ButtonType("Reject", ButtonBar.ButtonData.OTHER);
        ButtonType save    = new ButtonType("Save Changes", ButtonBar.ButtonData.APPLY);
        ButtonType cancel  = ButtonType.CANCEL;

        d.getDialogPane().getButtonTypes().setAll(approve, reject, save, cancel);

        VBox content = buildModerationLayout(
                "Post",
                "Post ID: " + p.getId() + " | User ID: " + p.getUserId(),
                p.getTitle(),
                p.getBody(),
                p.getType(),
                p.isConfirmed()
        );

        TextField titleField = (TextField) content.lookup("#modTitle");
        TextArea bodyField   = (TextArea) content.lookup("#modBody");
        TextField typeField  = (TextField) content.lookup("#modType");
        CheckBox confirmedCb = (CheckBox) content.lookup("#modConfirmed");

        d.getDialogPane().setContent(content);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty()) return;

        ButtonType bt = res.get();

        if (bt == approve) {
            confirmedCb.setSelected(true);
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), true);
            showSuccess("Post approved.");
            loadAllData();
            return;
        }

        if (bt == reject) {
            confirmedCb.setSelected(false);
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), false);
            showSuccess("Post rejected.");
            loadAllData();
            return;
        }

        if (bt == save) {
            applyPostChanges(p, titleField.getText(), bodyField.getText(), typeField.getText(), confirmedCb.isSelected());
            showSuccess("Post updated.");
            loadAllData();
        }
    }

    private void applyPostChanges(Posts p, String title, String body, String type, boolean confirmed) {
        p.setTitle(safe(title));
        p.setBody(safe(body));
        p.setType(safe(type).isEmpty() ? "BLOG" : safe(type));
        p.setConfirmed(confirmed);

        if (!postService.update(p)) {
            showError("Failed to update post.");
        }
    }

    private void moderateStory(TravelStory s) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Moderate Travel Story");
        d.setHeaderText("Review / Save Changes");

        ButtonType save   = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = ButtonType.CANCEL;
        d.getDialogPane().getButtonTypes().setAll(save, cancel);

        VBox box = new VBox(12);
        box.setPadding(new Insets(12));

        Label meta = new Label("Story ID: " + s.getTravelStoryId() + " | User ID: " + s.getUserId());
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        TextField titleField = new TextField(safe(s.getTitle()));
        titleField.setPromptText("Title");

        TextField destField = new TextField(safe(s.getDestinationText()));
        destField.setPromptText("Destination");

        // ✅ UPDATED: Summary instead of Content
        TextArea summaryField = new TextArea(safe(s.getSummary()));
        summaryField.setPromptText("Summary");
        summaryField.setPrefRowCount(6);

        box.getChildren().addAll(
                meta,
                new Label("Title:"), titleField,
                new Label("Destination:"), destField,
                new Label("Summary:"), summaryField
        );

        d.getDialogPane().setContent(box);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty() || res.get() != save) return;

        s.setTitle(safe(titleField.getText()));
        s.setDestinationText(safe(destField.getText()));
        s.setSummary(safe(summaryField.getText()));

        if (travelStoryService.update(s)) {
            showSuccess("Travel story updated.");
            loadAllData();
        } else {
            showError("Failed to update travel story.");
        }
    }

    private void moderateComment(Comments c) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Moderate Comment");
        d.setHeaderText("Edit or remove inappropriate content");

        ButtonType save   = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = ButtonType.CANCEL;
        d.getDialogPane().getButtonTypes().setAll(save, cancel);

        VBox box = new VBox(10);
        box.setPadding(new Insets(12));

        Label meta = new Label(
                "Comment ID: " + c.getId() +
                        " | User ID: " + c.getUserId() +
                        " | Post ID: " + (c.getPostId() == null ? "-" : c.getPostId()) +
                        " | Story ID: " + (c.getTravelStoryId() == null ? "-" : c.getTravelStoryId())
        );
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        TextArea bodyField = new TextArea(safe(c.getBody()));
        bodyField.setPromptText("Comment content");
        bodyField.setPrefRowCount(5);

        box.getChildren().addAll(meta, new Label("Body:"), bodyField);

        d.getDialogPane().setContent(box);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty() || res.get() != save) return;

        c.setBody(safe(bodyField.getText()));

        if (commentService.update(c)) {
            showSuccess("Comment updated.");
            loadAllData();
        } else {
            showError("Failed to update comment.");
        }
    }

    private VBox buildModerationLayout(
            String kind,
            String metaText,
            String title,
            String body,
            String type,
            boolean confirmed
    ) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        Label meta = new Label(metaText);
        meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        TextField titleField = new TextField(safe(title));
        titleField.setId("modTitle");
        titleField.setPromptText(kind + " title");

        TextArea bodyField = new TextArea(safe(body));
        bodyField.setId("modBody");
        bodyField.setPromptText(kind + " content");
        bodyField.setPrefRowCount(6);

        TextField typeField = new TextField(safe(type));
        typeField.setId("modType");
        typeField.setPromptText("Type (BLOG, NEWS, ...)");

        CheckBox confirmedCb = new CheckBox("Confirmed (Approved)");
        confirmedCb.setId("modConfirmed");
        confirmedCb.setSelected(confirmed);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(100);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeField, 1, 1);

        grid.add(new Label("Body:"), 0, 2);
        grid.add(bodyField, 1, 2);

        grid.add(confirmedCb, 1, 3);

        root.getChildren().addAll(meta, grid);
        return root;
    }

    // =========================================================
    // DELETE
    // =========================================================
    private void deletePost(Posts p) {
        if (!confirm("Delete post \"" + safe(p.getTitle()) + "\"?")) return;
        if (postService.delete(p.getId())) {
            showSuccess("Post deleted.");
            loadAllData();
        } else showError("Failed to delete post.");
    }

    private void deleteStory(TravelStory s) {
        if (!confirm("Delete travel story \"" + safe(s.getTitle()) + "\"?")) return;
        if (travelStoryService.delete(s.getTravelStoryId())) {
            showSuccess("Travel story deleted.");
            loadAllData();
        } else showError("Failed to delete travel story.");
    }

    private void deleteComment(Comments c) {
        if (!confirm("Delete this comment?")) return;
        if (commentService.delete(c.getId())) {
            showSuccess("Comment deleted.");
            loadAllData();
        } else showError("Failed to delete comment.");
    }

    // =========================================================
    // ADD DIALOGS
    // =========================================================
    private void showAddPostDialog() {
        List<User> users;
        try { users = userService.getAllUsers(); }
        catch (Exception e) { users = new java.util.ArrayList<>(); }

        if (users == null || users.isEmpty()) {
            showError("No users found. Create a user first in the database.");
            return;
        }

        Dialog<Boolean> d = new Dialog<>();
        d.setTitle("Add Post");
        d.setHeaderText("Create a new post");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<User> userCombo = new ComboBox<>(FXCollections.observableArrayList(users));
        userCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(User u) { return u == null ? "" : u.getEmail() + " (ID:" + u.getUserId() + ")"; }
            @Override public User fromString(String s) { return null; }
        });
        userCombo.getSelectionModel().selectFirst();

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Content");
        bodyField.setPrefRowCount(4);

        TextField typeField = new TextField("BLOG");
        typeField.setPromptText("Type (e.g. BLOG)");

        CheckBox confirmedCheck = new CheckBox("Confirmed");

        grid.add(new Label("User:"), 0, 0); grid.add(userCombo, 1, 0);
        grid.add(new Label("Title:"), 0, 1); grid.add(titleField, 1, 1);
        grid.add(new Label("Content:"), 0, 2); grid.add(bodyField, 1, 2);
        grid.add(new Label("Type:"), 0, 3); grid.add(typeField, 1, 3);
        grid.add(confirmedCheck, 1, 4);

        d.getDialogPane().setContent(grid);
        d.setResultConverter(btn -> btn == ButtonType.OK);

        Optional<Boolean> ok = d.showAndWait();
        if (ok.isEmpty() || !ok.get()) return;

        User u = userCombo.getSelectionModel().getSelectedItem();
        if (u == null) return;

        String title = safe(titleField.getText());
        if (title.isEmpty()) { showError("Please enter a title."); return; }

        Posts p = new Posts(u.getUserId(), 0, title, safe(bodyField.getText()),
                safe(typeField.getText()).isEmpty() ? "BLOG" : safe(typeField.getText()));
        p.setConfirmed(confirmedCheck.isSelected());

        if (postService.create(p)) {
            showSuccess("Post created.");
            loadAllData();
        } else {
            showError("Failed to create post.");
        }
    }

    private void showAddStoryDialog() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
            showError("No users found. Create a user first.");
            return;
        }

        Dialog<Boolean> d = new Dialog<>();
        d.setTitle("Add Travel Story");
        d.setHeaderText("Create a new travel story");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<User> userCombo = new ComboBox<>(FXCollections.observableArrayList(users));
        userCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(User u) { return u == null ? "" : u.getEmail() + " (ID:" + u.getUserId() + ")"; }
            @Override public User fromString(String s) { return null; }
        });
        userCombo.getSelectionModel().selectFirst();

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField destinationField = new TextField();
        destinationField.setPromptText("Destination");

        // ✅ UPDATED: Summary instead of Content
        TextArea summaryField = new TextArea();
        summaryField.setPromptText("Summary");
        summaryField.setPrefRowCount(4);

        grid.add(new Label("User:"), 0, 0); grid.add(userCombo, 1, 0);
        grid.add(new Label("Title:"), 0, 1); grid.add(titleField, 1, 1);
        grid.add(new Label("Destination:"), 0, 2); grid.add(destinationField, 1, 2);
        grid.add(new Label("Summary:"), 0, 3); grid.add(summaryField, 1, 3);

        d.getDialogPane().setContent(grid);
        d.setResultConverter(btn -> btn == ButtonType.OK);

        Optional<Boolean> ok = d.showAndWait();
        if (ok.isEmpty() || !ok.get()) return;

        User u = userCombo.getSelectionModel().getSelectedItem();
        if (u == null) return;

        String title = safe(titleField.getText());
        if (title.isEmpty()) { showError("Please enter a title."); return; }

        // ✅ constructor uses summary now (because content is removed)
        TravelStory s = new TravelStory(
                u.getUserId(),
                title,
                safe(summaryField.getText()),
                safe(destinationField.getText())
        );

        if (travelStoryService.create(s)) {
            showSuccess("Travel story created.");
            loadAllData();
        } else {
            showError("Failed to create travel story.");
        }
    }

    // =========================================================
    // UTIL
    // =========================================================
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String abbreviate(String s, int max) {
        if (s == null) return "";
        String t = s.trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max)) + "...";
    }

    private boolean confirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm");
        a.setHeaderText(null);
        a.setContentText(message);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Success");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}