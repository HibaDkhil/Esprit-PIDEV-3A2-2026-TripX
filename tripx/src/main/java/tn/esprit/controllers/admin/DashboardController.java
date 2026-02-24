package tn.esprit.controllers.admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.util.prefs.Preferences;

public class DashboardController {

    // Sidebar components
    @FXML private VBox sidebar;
    @FXML private Button sidebarToggle;
    @FXML private Button sidebarOpenButton;

    // Menu headers (for click events)
    @FXML private HBox dashboardHeader;
    @FXML private HBox usersHeader;
    @FXML private HBox accommodationsHeader;
    @FXML private HBox destinationsHeader;
    @FXML private HBox transportHeader;
    @FXML private HBox offersHeader;
    @FXML private HBox blogHeader;
    @FXML private HBox profileHeaderSection;
    // Menu toggles (triangles)
    @FXML private Button dashboardToggle;
    @FXML private Button usersToggle;
    @FXML private Button accommodationsToggle;
    @FXML private Button destinationsToggle;
    @FXML private Button transportToggle;
    @FXML private Button offersToggle;
    @FXML private Button blogToggle;

    // Menu containers
    @FXML private VBox dashboardMenu;
    @FXML private VBox usersMenu;
    @FXML private VBox accommodationsMenu;
    @FXML private VBox destinationsMenu;
    @FXML private VBox transportMenu;
    @FXML private VBox offersMenu;
    @FXML private VBox blogMenu;

    // Menu buttons (for navigation)
    @FXML private Button overviewBtn;
    @FXML private Button usersBtn;
    @FXML private Button destinationsBtn;
    @FXML private Button activitiesBtn;
    @FXML private Button accommodationsBtn;
    @FXML private Button transportBtn;
    @FXML private Button offersBtn;
    @FXML private Button blogBtn;
    @FXML private Button sidebarBookingBtn;

    // Header components
    @FXML private Button headerSettingsBtn;
    @FXML private MenuButton profileMenuBtn;
    @FXML private Label userNameHeaderLabel;
    @FXML private Label avatarText;

    // Theme and language
    @FXML private Label themeIcon;
    @FXML private ToggleButton darkModeToggle;
    @FXML private ComboBox<String> languageSelector;

    // Breadcrumb
    @FXML private Label breadcrumb1;
    @FXML private Label breadcrumb2;

    // Main content area
    @FXML private StackPane mainContent;

    // Logout button in sidebar
    @FXML private Button logoutBtn;

    private String role;
    private User currentUser;
    private boolean isDarkMode = false;
    private boolean isSidebarCollapsed = false;

    private static DashboardController instance;

    public static DashboardController getInstance() {
        return instance;
    }

    public void setRole(String role) {
        this.role = role;
        configureSidebar();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateProfileInfo();
    }

    private void updateProfileInfo() {
        if (currentUser != null && avatarText != null) {
            String initials = currentUser.getFirstName().substring(0, 1) +
                    currentUser.getLastName().substring(0, 1);
            avatarText.setText(initials.toUpperCase());
            if (userNameHeaderLabel != null) {
                userNameHeaderLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            }
        }
    }

    private void configureSidebar() {
        boolean superAdmin = isSuper();

        // Users section — super-admin only
        setMenuLocked(usersHeader, usersMenu, !superAdmin);

        // Module sections
        setMenuLocked(destinationsHeader,    destinationsMenu,    !superAdmin && !hasAccess("destinations"));
        setMenuLocked(accommodationsHeader,  accommodationsMenu,  !superAdmin && !hasAccess("accommodations"));
        setMenuLocked(transportHeader,       transportMenu,       !superAdmin && !hasAccess("transport"));
        setMenuLocked(offersHeader,          offersMenu,          !superAdmin && !hasAccess("offers"));
        setMenuLocked(blogHeader,            blogMenu,            !superAdmin && !hasAccess("blog"));
    }

    /** Dim and block interaction on a menu section if locked. */
    private void setMenuLocked(HBox header, VBox menu, boolean locked) {
        if (header != null) {
            header.setOpacity(locked ? 0.35 : 1.0);
            header.setDisable(locked);
        }
        if (menu != null) {
            // Always collapse submenus; they'll be re-opened by the user
            menu.setVisible(false);
            menu.setManaged(false);
        }
    }

    /** Returns true if the current role is the full super-admin. */
    private boolean isSuper() {
        return role != null && role.equalsIgnoreCase("admin");
    }

    /** Returns true if the current role has access to the given module key. */
    private boolean hasAccess(String module) {
        if (isSuper()) return true;
        if (role == null) return false;
        return switch (role.toLowerCase()) {
            case "admindestination"  -> module.equals("destinations");
            case "adminaccomodation" -> module.equals("accommodations");
            case "admintransport"   -> module.equals("transport");
            case "adminblog"        -> module.equals("blog");
            case "adminoffers"      -> module.equals("offers");
            default -> false;
        };
    }

    /** Displays a full-page access-denied panel in the content area. */
    private void showAccessDenied(String moduleName) {
        mainContent.getChildren().clear();
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(500);
        box.setStyle(
            "-fx-background-color: #FEF2F2;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 60 50;"
        );

        Label icon = new Label("\uD83D\uDD12");
        icon.setStyle("-fx-font-size: 64px;");

        Label title = new Label("Access Denied");
        title.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #991B1B;" +
            "-fx-font-family: 'Poppins';"
        );

        Label sub = new Label(
            "You don't have permission to access the " + moduleName + " module.\n" +
            "Contact your super administrator to request access."
        );
        sub.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #B91C1C;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-text-alignment: center;"
        );
        sub.setWrapText(true);
        sub.setMaxWidth(400);

        box.getChildren().addAll(icon, title, sub);

        // Centre the box inside the StackPane
        StackPane.setAlignment(box, Pos.CENTER);
        mainContent.getChildren().add(box);
    }

    @FXML
    public void initialize() {
        instance = this;
        setupSidebarToggle();
        setupNavigation();
        setupHeaderActions();
        setupTheme();
        setupLanguageSelector();

        // Load overview by default
        showOverview();
    }

    private void setupSidebarToggle() {
        if (sidebarToggle != null) {
            sidebarToggle.setOnAction(event -> collapseSidebar());
        }
        if (sidebarOpenButton != null) {
            sidebarOpenButton.setOnAction(event -> expandSidebar());
            sidebarOpenButton.setVisible(false);
            sidebarOpenButton.setManaged(false);
        }
    }

    private void collapseSidebar() {
        isSidebarCollapsed = true;
        if (sidebar != null) {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        }
        if (sidebarOpenButton != null) {
            sidebarOpenButton.setVisible(true);
            sidebarOpenButton.setManaged(true);
        }
    }

    private void expandSidebar() {
        isSidebarCollapsed = false;
        if (sidebar != null) {
            sidebar.setVisible(true);
            sidebar.setManaged(true);
        }
        if (sidebarOpenButton != null) {
            sidebarOpenButton.setVisible(false);
            sidebarOpenButton.setManaged(false);
        }
    }

    // ============ MENU TOGGLE METHODS - CLICK ON WHOLE HEADER ============
    @FXML
    private void toggleDashboardMenu(MouseEvent event) {
        toggleMenu(dashboardMenu, dashboardToggle);
    }

    @FXML
    private void toggleUsersMenu(MouseEvent event) {
        toggleMenu(usersMenu, usersToggle);
    }

    @FXML
    private void toggleAccommodationsMenu(MouseEvent event) {
        toggleMenu(accommodationsMenu, accommodationsToggle);
    }

    @FXML
    private void toggleDestinationsMenu(MouseEvent event) {
        toggleMenu(destinationsMenu, destinationsToggle);
    }

    @FXML
    private void toggleTransportMenu(MouseEvent event) {
        toggleMenu(transportMenu, transportToggle);
    }

    @FXML
    private void toggleOffersMenu(MouseEvent event) {
        toggleMenu(offersMenu, offersToggle);
    }

    @FXML
    private void toggleBlogMenu(MouseEvent event) {
        toggleMenu(blogMenu, blogToggle);
    }

    private void toggleMenu(VBox menu, Button toggleButton) {
        if (menu == null || toggleButton == null) return;

        boolean isVisible = menu.isVisible();
        menu.setVisible(!isVisible);
        menu.setManaged(!isVisible);
        toggleButton.setText(!isVisible ? "▼" : "▶");
    }

    private void setupNavigation() {
        overviewBtn.setOnAction(e -> showOverview());
        usersBtn.setOnAction(e -> showUsers());
        destinationsBtn.setOnAction(e -> showDestinations());
        if (activitiesBtn != null) activitiesBtn.setOnAction(e -> showActivities());
        accommodationsBtn.setOnAction(e -> showAccommodations());
        transportBtn.setOnAction(e -> showTransport());
        offersBtn.setOnAction(e -> showOffers());
        blogBtn.setOnAction(e -> showBlog());
        if (sidebarBookingBtn != null) sidebarBookingBtn.setOnAction(e -> showBookings());
    }

    private void setupHeaderActions() {
        // Settings button click (opens Settings with My Claims tab - index 0)
        if (headerSettingsBtn != null) {
            headerSettingsBtn.setOnAction(e -> showSettings(0));
        }

        // Profile section click (directly opens profile)
        if (profileHeaderSection != null) {
            profileHeaderSection.setCursor(javafx.scene.Cursor.HAND);
            // Action is handled by onMouseClicked in FXML (#showProfile)
        }

        // Logout from sidebar
        if (logoutBtn != null) {
            logoutBtn.setOnAction(e -> handleLogout());
        }
    }

    private void setActiveButton(Button button) {
        // Reset all menu items
        resetAllMenuItems();

        // Set active class on the clicked button
        if (button != null) {
            button.getStyleClass().add("active");
        }

        // Update breadcrumb
        updateBreadcrumb(button);
    }

    private void resetAllMenuItems() {
        Button[] allButtons = {overviewBtn, usersBtn, destinationsBtn, activitiesBtn,
                accommodationsBtn, transportBtn, offersBtn, blogBtn, sidebarBookingBtn};
        for (Button btn : allButtons) {
            if (btn != null) btn.getStyleClass().remove("active");
        }
    }

    private void updateBreadcrumb(Button button) {
        if (button == overviewBtn) {
            breadcrumb1.setText("Dashboard");
            breadcrumb2.setText("Overview");
        } else if (button == usersBtn) {
            breadcrumb1.setText("Users");
            breadcrumb2.setText("User Management");
        } else if (button == destinationsBtn) {
            breadcrumb1.setText("Destinations");
            breadcrumb2.setText("All Destinations");
        } else if (button == accommodationsBtn) {
            breadcrumb1.setText("Accommodations");
            breadcrumb2.setText("Manage Accommodations");
        } else if (button == transportBtn) {
            breadcrumb1.setText("Transport");
            breadcrumb2.setText("Manage Transport");
        } else if (button == offersBtn) {
            breadcrumb1.setText("Offers");
            breadcrumb2.setText("Manage Offers");
        } else if (button == blogBtn) {
            breadcrumb1.setText("Blog");
            breadcrumb2.setText("Blog & Community");
        } else if (button == sidebarBookingBtn) {
            breadcrumb1.setText("Destinations");
            breadcrumb2.setText("Bookings");
        }
    }

    private void showOverview() {
        setActiveButton(overviewBtn);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/overview.fxml"));
            Node overviewView = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(overviewView);
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to empty content
            mainContent.getChildren().clear();
            Label label = new Label("Overview Module");
            label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            mainContent.getChildren().add(label);
        }
    }

    private void showUsers() {
        if (!isSuper()) { showAccessDenied("Users"); return; }
        setActiveButton(usersBtn);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/user_management.fxml"));
            Node view = loader.load();

            UserManagementController controller = loader.getController();
            if (currentUser != null) {
                controller.setUserData(currentUser, role);
            }

            if (breadcrumb1 != null) breadcrumb1.setText("Users");
            if (breadcrumb2 != null) breadcrumb2.setText("Manage Users");

            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading User Management");
        }
    }

    private void showDestinations() {
        if (!hasAccess("destinations")) { showAccessDenied("Destinations"); return; }
        setActiveButton(destinationsBtn);
        breadcrumb1.setText("Destinations");
        breadcrumb2.setText("All Destinations");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/destination_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Destination Management: " + e.getMessage());
        }
    }

    private void showActivities() {
        if (!hasAccess("destinations")) { showAccessDenied("Activities"); return; }
        if (activitiesBtn != null) setActiveButton(activitiesBtn);
        breadcrumb1.setText("Destinations");
        breadcrumb2.setText("Activities");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/activity_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Activity Management: " + e.getMessage());
        }
    }

    private void showBookings() {
        if (!hasAccess("destinations")) { showAccessDenied("Bookings"); return; }
        if (sidebarBookingBtn != null) setActiveButton(sidebarBookingBtn);
        breadcrumb1.setText("Destinations");
        breadcrumb2.setText("Bookings");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/booking_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Booking Management: " + e.getMessage());
        }
    }

    //------------------------------------el beki mta modules----------------------------

    private void showAccommodations() {
        if (!hasAccess("accommodations")) { showAccessDenied("Accommodations"); return; }
        setActiveButton(accommodationsBtn);
        
        /* INTEGRATION: Future Accommodation Module
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/accommodation_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Accommodation Management: " + e.getMessage());
        }
        */

        mainContent.getChildren().clear();
        Label label = new Label("Accommodation Management Module");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
        mainContent.getChildren().add(label);
    }

    private void showTransport() {
        if (!hasAccess("transport")) { showAccessDenied("Transport"); return; }
        setActiveButton(transportBtn);

        /* INTEGRATION: Future Transport Module
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/transport_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Transport Management: " + e.getMessage());
        }
        */

        mainContent.getChildren().clear();
        Label label = new Label("Transport Management Module");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
        mainContent.getChildren().add(label);
    }

    private void showOffers() {
        if (!hasAccess("offers")) { showAccessDenied("Offers"); return; }
        setActiveButton(offersBtn);

        /* INTEGRATION: Future Offers Module
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/offers_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Offers Management: " + e.getMessage());
        }
        */

        mainContent.getChildren().clear();
        Label label = new Label("Offers Management Module");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
        mainContent.getChildren().add(label);
    }

    private void showBlog() {
        if (!hasAccess("blog")) { showAccessDenied("Blog"); return; }
        setActiveButton(blogBtn);

        /* INTEGRATION: Future Blog Module
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/blog_management.fxml"));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Blog Management: " + e.getMessage());
        }
        */

        mainContent.getChildren().clear();
        Label label = new Label("Blog & Community Module");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Poppins';");
        mainContent.getChildren().add(label);
    }

    // NEW METHOD: Show Profile
    @FXML
    private void showProfile(javafx.scene.input.MouseEvent event) {
        try {
            // Clear active button from sidebar
            resetAllMenuItems();

            // DEBUG: Check if currentUser exists
            System.out.println("=== DEBUG SHOW PROFILE ===");
            System.out.println("currentUser: " + currentUser);

            if (currentUser != null) {
                System.out.println("User ID: " + currentUser.getUserId());
                System.out.println("User Email: " + currentUser.getEmail());
                System.out.println("User Name: " + currentUser.getFirstName() + " " + currentUser.getLastName());
                System.out.println("User Role: " + role);
            } else {
                System.out.println("ERROR: currentUser is NULL!");
                showError("No user data available!");
                return;
            }

            String fxmlPath = "/fxml/admin/profileAdmin.fxml";
            System.out.println("Loading profile from: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                System.err.println("ERROR: Cannot find profileAdmin.fxml at " + fxmlPath);
                showError("Profile FXML file not found!");
                return;
            }

            Node profileView = loader.load();

            ProfileAdminController controller = loader.getController();

            // PASS THE USER DATA
            controller.setUserData(currentUser, role);
            System.out.println("User data passed to ProfileAdminController");

            // Update breadcrumb
            breadcrumb1.setText("Admin");
            breadcrumb2.setText("My Profile");

            mainContent.getChildren().clear();
            mainContent.getChildren().add(profileView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading profile: " + e.getMessage());
        }
    }

    private void showSettings(int selectedTab) {
        try {
            // Clear active button from sidebar (settings not in sidebar)
            resetAllMenuItems();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/settings.fxml"));
            Node settingsView = loader.load();

            SettingsController controller = loader.getController();
            if (currentUser != null) {
                controller.setUserData(currentUser, role, selectedTab); // 3 arguments
            }

            // Update breadcrumb - CHANGED: removed "My Profile" option
            breadcrumb1.setText("Settings");
            breadcrumb2.setText("My Claims");

            mainContent.getChildren().clear();
            mainContent.getChildren().add(settingsView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading settings");
        }
    }

    private void setupTheme() {
        if (darkModeToggle == null) return;

        // Initial state
        updateThemeDisplay(isDarkMode);

        darkModeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isDarkMode = newVal;
            applyTheme(isDarkMode);
            saveThemePreference(isDarkMode ? "dark" : "light");
        });

        // Load saved preference
        loadThemePreference();
    }

    private void applyTheme(boolean dark) {
        if (sidebar != null && sidebar.getScene() != null) {
            StackPane root = (StackPane) sidebar.getScene().getRoot();
            if (dark) {
                root.getStyleClass().add("dark-mode");
            } else {
                root.getStyleClass().remove("dark-mode");
            }
            updateThemeDisplay(dark);
        }
    }

    private void updateThemeDisplay(boolean dark) {
        if (themeIcon != null) {
            themeIcon.setText(dark ? "🌙" : "☀️");
        }
        if (darkModeToggle != null) {
            darkModeToggle.setSelected(dark);
        }
    }

    private void saveThemePreference(String theme) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(DashboardController.class);
            prefs.put("theme", theme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadThemePreference() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(DashboardController.class);
            String theme = prefs.get("theme", "light");
            isDarkMode = theme.equals("dark");

            // Apply theme after UI is loaded
            Platform.runLater(() -> {
                applyTheme(isDarkMode);
            });
        } catch (Exception e) {
            isDarkMode = false;
        }
    }


    private void setupLanguageSelector() {
        if (languageSelector == null) return;
        if (languageSelector.getValue() == null) {
            languageSelector.setValue("English");
        }
    }

    @FXML
    private void handleMenuProfile() {
        showProfile(null);
    }

    @FXML
    private void handleMenuSettings() {
        showSettings(0);
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) sidebar.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadView(String fxmlPath, String b1, String b2) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
            
            if (breadcrumb1 != null) breadcrumb1.setText(b1);
            if (breadcrumb2 != null) breadcrumb2.setText(b2);
            
            // Try to find the button and set it active
            resetAllMenuItems();
            // This is a bit tricky since we don't know which button corresponds to which FXML
            // But we can manually set it in callers or handle it here if we map them.
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading view: " + fxmlPath);
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSidebarCollapsed() {
        return isSidebarCollapsed;
    }
}