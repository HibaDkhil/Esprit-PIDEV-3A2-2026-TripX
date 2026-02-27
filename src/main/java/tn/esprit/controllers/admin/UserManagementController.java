package tn.esprit.controllers.admin;

import tn.esprit.entities.User;
import tn.esprit.entities.Destination;
import tn.esprit.services.UserService;
import tn.esprit.services.UserActivityService;
import tn.esprit.services.DestinationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.*;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private Button sortBtn;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Button slideArrowBtn;
    @FXML private VBox statsPanel;
    @FXML private HBox slidingOverlay;
    @FXML private AnchorPane mainContainer;
    @FXML private PieChart genderChart;
    @FXML private BarChart<String, Integer> ageChart;
    @FXML private Label totalUsersStat;
    @FXML private Label adminCountStat;
    @FXML private Label userCountStat;
    @FXML private ListView<String> mostPopularList;

    // Pagination
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageInfoLabel;

    private UserService userService;
    private UserActivityService activityService;
    private DestinationService destinationService;
    private ObservableList<User> userList;
    private ObservableList<User> paginatedList;
    private boolean statsVisible = false;

    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages = 1;

    // Ajoute cette méthode pour recevoir les données de l'utilisateur connecté
    public void setUserData(User user, String role) {
        // Tu peux stocker ces données si nécessaire pour plus tard
        System.out.println("UserManagement opened by: " + user.getEmail() + " with role: " + role);

        // Optionnel : si tu veux restreindre l'accès basé sur le rôle
        if (!"SUPER_ADMIN".equals(role) && !"USER_ADMIN".equals(role)) {
            // Masquer certaines fonctionnalités pour les admins non autorisés
            // Par exemple, désactiver le bouton delete pour les non-admins
        }
    }

    @FXML
    public void initialize() {
        userService = new UserService();
        activityService = new UserActivityService();
        destinationService = new DestinationService();

        // Setup table columns
        setupTableColumns();

        // Load REAL users from database
        loadUsersFromDatabase();

        // Setup search functionality
        setupSearch();

        // Setup pagination
        setupPagination();

        // Setup slide arrow
        setupSlideArrow();

        // Update statistics
        updateStatistics();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String status = user.getStatus() != null ? user.getStatus() : "ACTIVE";
            return javafx.beans.binding.Bindings.createStringBinding(() -> status);
        });
        
        // Add status color logic
        statusColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if (item.equals("BANNED")) setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                    else if (item.equals("SUSPENDED")) setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                }
            }
        });

        // Actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button suspendBtn = new Button();
            private final Button banBtn = new Button();
            private final HBox pane = new HBox(5, editBtn, deleteBtn, suspendBtn, banBtn);

            {
                // Style buttons
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                
                // Common style for status buttons
                String commonStyle = "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;";
                suspendBtn.setStyle("-fx-background-color: #ff9800; " + commonStyle);
                banBtn.setStyle("-fx-background-color: #333333; " + commonStyle);

                // Add hover effects
                addHoverEffect(editBtn, "#2196F3", "#1976D2");
                addHoverEffect(deleteBtn, "#f44336", "#d32f2f");
                addHoverEffect(suspendBtn, "#ff9800", "#f57c00");
                addHoverEffect(banBtn, "#333333", "#000000");

                // Set actions
                editBtn.setOnAction(e -> handleEdit(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
                
                suspendBtn.setOnAction(e -> {
                    User u = getTableRow().getItem();
                    if ("SUSPENDED".equals(u.getStatus())) handleUnsuspend(u);
                    else handleSuspend(u);
                });
                
                banBtn.setOnAction(e -> {
                    User u = getTableRow().getItem();
                    if ("BANNED".equals(u.getStatus())) handleUnban(u);
                    else handleBan(u);
                });

                pane.setAlignment(Pos.CENTER);
                pane.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        suspendBtn.setText("SUSPENDED".equals(user.getStatus()) ? "Unsuspend" : "Suspend");
                        banBtn.setText("BANNED".equals(user.getStatus()) ? "Unban" : "Ban");
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void addHoverEffect(Button btn, String normalColor, String hoverColor) {
        btn.setOnMouseEntered(e -> {
            btn.setStyle(btn.getStyle().replace(normalColor, hoverColor) + "; -fx-cursor: hand;");
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(btn.getStyle().replace(hoverColor, normalColor) + "; -fx-cursor: default;");
        });
    }

    private void loadUsersFromDatabase() {
        List<User> users = userService.getAllUsers();
        userList = FXCollections.observableArrayList(users);
        updatePagination();
        System.out.println("Loaded " + users.size() + " users from database");
    }

    private void setupPagination() {
        prevPageBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updatePagination();
            }
        });

        nextPageBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePagination();
            }
        });
    }

    private void updatePagination() {
        if (userList == null) return;
        
        int total = userList.size();
        totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        
        if (currentPage > totalPages) currentPage = totalPages;
        
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        paginatedList = FXCollections.observableArrayList(userList.subList(fromIndex, toIndex));
        userTable.setItems(paginatedList);
        
        pageInfoLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
        prevPageBtn.setDisable(currentPage == 1);
        nextPageBtn.setDisable(currentPage == totalPages);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue);
        });

        searchBtn.setOnAction(e -> filterUsers(searchField.getText()));
        sortBtn.setOnAction(e -> sortUsers());
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            updatePagination();
            return;
        }

        String lowerSearch = searchText.toLowerCase().trim();
        List<User> filtered = userList.stream()
                .filter(u -> (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(lowerSearch)) ||
                            (u.getLastName() != null && u.getLastName().toLowerCase().contains(lowerSearch)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerSearch)) ||
                            String.valueOf(u.getUserId()).contains(lowerSearch) ||
                            (u.getRole() != null && u.getRole().toLowerCase().contains(lowerSearch)))
                .collect(Collectors.toList());

        userTable.setItems(FXCollections.observableArrayList(filtered));
        // Reset pagination info for search results
        pageInfoLabel.setText("Search Results");
        prevPageBtn.setDisable(true);
        nextPageBtn.setDisable(true);
    }

    private void sortUsers() {
        FXCollections.sort(userTable.getItems(),
                (u1, u2) -> Integer.compare(u1.getUserId(), u2.getUserId()));
    }

    private void setupSlideArrow() {
        // Initial state: entire overlay (arrow + panel) is slid to the right
        // The panel is 600px wide. We keep the arrow visible at the right edge.
        slidingOverlay.setTranslateX(0); // Position is defined by AnchorPane.rightAnchor in FXML (-600)
        
        mainContainer.setPickOnBounds(false); 

        slideArrowBtn.setOnAction(e -> toggleStatsPanel());
    }

    private void toggleStatsPanel() {
        statsVisible = !statsVisible;
        
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), slidingOverlay);
        
        if (statsVisible) {
            transition.setToX(-600); // Slide everything 600px to the left
            slideArrowBtn.setText("▶");
            updateStatistics();
        } else {
            transition.setToX(0); // Slide back to original position (only arrow visible)
            slideArrowBtn.setText("◀");
        }
        
        transition.play();
    }

    @FXML
    private void updateStatistics() {
        if (userList == null) return;

        int total = userList.size();
        int admins = 0;
        int regularUsers = 0;

        Map<String, Integer> genderMap = new HashMap<>();
        Map<String, Integer> ageGroups = new HashMap<>();
        ageGroups.put("Under 18", 0);
        ageGroups.put("18-24", 0);
        ageGroups.put("25-34", 0);
        ageGroups.put("35-44", 0);
        ageGroups.put("45-54", 0);
        ageGroups.put("55-64", 0);
        ageGroups.put("65+", 0);

        int currentYear = LocalDate.now().getYear();

        for (User user : userList) {
            // Count roles
            String userRole = user.getRole();
            if (userRole != null && userRole.toLowerCase().contains("admin")) {
                admins++;
            } else {
                regularUsers++;
            }

            // Gender data
            String gender = user.getGender() != null ? user.getGender() : "Unknown";
            genderMap.put(gender, genderMap.getOrDefault(gender, 0) + 1);

            // Detailed Age data
            if (user.getBirthYear() != null && !user.getBirthYear().isEmpty()) {
                try {
                    int age = currentYear - Integer.parseInt(user.getBirthYear());
                    if (age < 18) ageGroups.put("Under 18", ageGroups.get("Under 18") + 1);
                    else if (age <= 24) ageGroups.put("18-24", ageGroups.get("18-24") + 1);
                    else if (age <= 34) ageGroups.put("25-34", ageGroups.get("25-34") + 1);
                    else if (age <= 44) ageGroups.put("35-44", ageGroups.get("35-44") + 1);
                    else if (age <= 54) ageGroups.put("45-54", ageGroups.get("45-54") + 1);
                    else if (age <= 64) ageGroups.put("55-64", ageGroups.get("55-64") + 1);
                    else ageGroups.put("65+", ageGroups.get("65+") + 1);
                } catch (Exception e) {}
            }
        }

        totalUsersStat.setText(String.valueOf(total));
        adminCountStat.setText(String.valueOf(admins));
        userCountStat.setText(String.valueOf(regularUsers));

        // Update Charts
        updateCharts(genderMap, ageGroups);

        // Update Behavior Analytics
        updateBehaviorAnalytics();
    }

    private void updateBehaviorAnalytics() {
        if (activityService == null || mostPopularList == null) return;

        List<Long> popularIds = activityService.getGlobalMostVisitedDestinations(5);
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Long id : popularIds) {
            Destination d = destinationService.getDestinationById(id);
            if (d != null) {
                items.add("📍 " + d.getName() + " (" + d.getCountry() + ")");
            }
        }

        if (items.isEmpty()) {
            items.add("No activity recorded yet.");
        }

        mostPopularList.setItems(items);
    }

    private void updateCharts(Map<String, Integer> genderMap, Map<String, Integer> ageGroups) {
        // Gender PieChart
        genderChart.getData().clear();
        genderMap.forEach((gender, count) -> {
            genderChart.getData().add(new PieChart.Data(gender + " (" + count + ")", count));
        });

        // Age BarChart
        ageChart.getData().clear();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Users by Age Group");
        
        // Ensure sorted order in BarChart
        String[] labels = {"Under 18", "18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        for (String label : labels) {
            series.getData().add(new XYChart.Data<>(label, ageGroups.get(label)));
        }
        
        ageChart.getData().add(series);
    }

    // ==================== DELETE OPERATION ====================
    private void handleDelete(User user) {
        if (user == null) return;

        // Create confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");

        // Style the dialog buttons
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getButtonTypes().stream()
                .map(dialogPane::lookupButton)
                .forEach(button -> {
                    button.setStyle("-fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");

                    if (button instanceof Button) {
                        Button btn = (Button) button;
                        if (btn.getText().equals("OK")) {
                            btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");
                            // Add hover for YES button
                            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-cursor: hand;"));
                            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-cursor: default;"));
                        } else if (btn.getText().equals("Cancel")) {
                            btn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");
                            // Add hover for NO button
                            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-cursor: hand;"));
                            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold; -fx-cursor: default;"));
                        }
                    }
                });

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Perform delete
            boolean deleted = userService.deleteUser(user.getUserId());

            if (deleted) {
                // Show success message
                showSuccessMessage("User deleted successfully!");

                // Refresh the table
                loadUsersFromDatabase();
                updateStatistics();
            } else {
                // Show error message
                showErrorMessage("Failed to delete user!");
            }
        }
    }

    // ==================== EDIT OPERATION ====================
    private void handleEdit(User user) {
        if (user == null) return;

        try {
            // Create a new stage (popup window) for editing
            Stage editStage = new Stage();
            editStage.setTitle("Edit User - " + user.getFirstName() + " " + user.getLastName());

            // Create the edit form
            VBox editForm = new VBox(20);
            editForm.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 15;");
            editForm.setAlignment(Pos.TOP_CENTER);
            editForm.setPrefWidth(500);
            editForm.setPrefHeight(500);

            // Title
            Label titleLabel = new Label("✏️ Edit User");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-text-fill: #2c3e50;");

            // Form fields
            GridPane formGrid = new GridPane();
            formGrid.setHgap(15);
            formGrid.setVgap(15);
            formGrid.setAlignment(Pos.CENTER);

            // First Name
            Label firstNameLabel = new Label("First Name:");
            firstNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
            TextField firstNameField = new TextField(user.getFirstName());
            firstNameField.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
            firstNameField.setPrefWidth(250);

            // Last Name
            Label lastNameLabel = new Label("Last Name:");
            lastNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
            TextField lastNameField = new TextField(user.getLastName());
            lastNameField.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
            lastNameField.setPrefWidth(250);

            // Email
            Label emailLabel = new Label("Email:");
            emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
            TextField emailField = new TextField(user.getEmail());
            emailField.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
            emailField.setPrefWidth(250);

            // Role
            Label roleLabel = new Label("Role:");
            roleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
            ComboBox<String> roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll(
                "user",
                "admin",
                "adminDestination",
                "adminAccomodation",
                "adminTransport",
                "adminBlog",
                "adminOffers"
            );
            roleCombo.setValue(user.getRole() != null ? user.getRole() : "USER");
            roleCombo.setStyle("-fx-background-radius: 8; -fx-padding: 5; -fx-font-size: 14px; -fx-font-family: 'Poppins';");
            roleCombo.setPrefWidth(250);

            // Add to grid
            formGrid.add(firstNameLabel, 0, 0);
            formGrid.add(firstNameField, 1, 0);
            formGrid.add(lastNameLabel, 0, 1);
            formGrid.add(lastNameField, 1, 1);
            formGrid.add(emailLabel, 0, 2);
            formGrid.add(emailField, 1, 2);
            formGrid.add(roleLabel, 0, 3);
            formGrid.add(roleCombo, 1, 3);

            // Buttons
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER);

            Button saveBtn = new Button("Save Changes");
            saveBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30;");

            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30;");

            // Hover effects
            saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30; -fx-cursor: hand;"));
            saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30; -fx-cursor: default;"));

            cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30; -fx-cursor: hand;"));
            cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30; -fx-cursor: default;"));

            buttonBox.getChildren().addAll(saveBtn, cancelBtn);

            // Add everything to the form
            editForm.getChildren().addAll(titleLabel, formGrid, buttonBox);

            // Save action
            saveBtn.setOnAction(e -> {
                // Update user object
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setRole(roleCombo.getValue());

                // Call update service
                boolean updated = userService.updateUser(user);

                if (updated) {
                    showSuccessMessage("User updated successfully!");
                    editStage.close();
                    loadUsersFromDatabase();
                    updateStatistics();
                } else {
                    showErrorMessage("Failed to update user!");
                }
            });

            // Cancel action
            cancelBtn.setOnAction(e -> editStage.close());

            // Create scene and show
            Scene scene = new Scene(editForm);
            editStage.setScene(scene);
            editStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            editStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error opening edit window!");
        }
    }

    // ==================== HELPER METHODS ====================
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #4cccad; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");

        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-family: 'Poppins'; -fx-font-weight: bold;");

        alert.showAndWait();
    }

    // ==================== STATUS OPERATIONS ====================
    private void handleSuspend(User user) {
        if (user == null) return;
        updateStatus(user, "SUSPENDED", "User suspended successfully!");
    }

    private void handleUnsuspend(User user) {
        if (user == null) return;
        updateStatus(user, "ACTIVE", "User unsuspended successfully!");
    }

    private void handleBan(User user) {
        if (user == null) return;
        updateStatus(user, "BANNED", "User banned successfully!");
    }

    private void handleUnban(User user) {
        if (user == null) return;
        updateStatus(user, "ACTIVE", "User unbanned successfully!");
    }

    private void updateStatus(User user, String newStatus, String successMsg) {
        boolean updated = userService.updateUserStatus(user.getUserId(), newStatus);
        if (updated) {
            user.setStatus(newStatus);
            userTable.refresh();
            updateStatistics();
            showSuccessMessage(successMsg);
        } else {
            showErrorMessage("Failed to update user status!");
        }
    }
}