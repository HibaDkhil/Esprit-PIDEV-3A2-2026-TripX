package tn.esprit.controllers.user;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.User;
import tn.esprit.entities.UserPreferences;
import tn.esprit.services.EmailReputationService;
import tn.esprit.services.UserPreferencesService;
import tn.esprit.services.UserService;
import tn.esprit.utils.ValidationUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class ProfileController {

    @FXML private VBox personalInfoView;
    @FXML private VBox searchPreferencesView;
    @FXML private VBox securityView;
    @FXML private StackPane contentArea;

    @FXML private Button btnPersonalInfo;
    @FXML private Button btnPreferences;
    @FXML private Button btnSecurity;
    @FXML private Button btnNotifications; // Renamed display to "Your Activity" in FXML

    // Personal Info Fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML private Label passwordStatusLabel;

    // Error Labels
    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label emailError;
    @FXML private Label phoneError;
    @FXML private Label genderError;
    @FXML private Label currentPasswordError;
    @FXML private Label newPasswordError;
    @FXML private Label confirmPasswordError;

    // Preferences Labels
    @FXML private Label budgetLabel;
    @FXML private Label prioritiesLabel;
    @FXML private Label locationLabel;
    @FXML private Label accommodationLabel;
    @FXML private Label styleLabel;
    @FXML private Label dietaryLabel;
    @FXML private Label climateLabel;
    @FXML private Label paceLabel;
    @FXML private Label groupLabel;
    @FXML private Label currentEmailLabel;

    @FXML private Button checkEmailBtn;

    // ========== AVATAR FIELDS ==========
    @FXML private HBox avatarContainer;
    @FXML private Label currentAvatarLabel;
    @FXML private StackPane currentAvatarCircle;
    private String selectedAvatarStyle = "big-smile";
    private final String[] AVATAR_SEEDS = {
            "Adrian", "Bella", "Charlie", "Daisy", "Ethan", "Fiona",
            "George", "Hannah", "Ian", "Julia", "Kevin", "Luna",
            "Mason", "Nina", "Oliver", "Piper", "Quinn", "Rose",
            "Sam", "Tina", "Uma", "Victor", "Wendy", "Xander",
            "Yara", "Zack", "Amy", "Brian", "Chloe", "David"
    };

    private User currentUser;
    private UserPreferences currentPreferences;
    private final UserService userService = new UserService();
    private final UserPreferencesService userPreferencesService = new UserPreferencesService();
    private final EmailReputationService emailReputationService = new EmailReputationService();

    @FXML
    public void initialize() {
        // Initialize ComboBox
        if (genderComboBox != null) {
            genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female"));
        }

        // Default visibility check
        if (personalInfoView != null) {
            personalInfoView.setVisible(true);
            personalInfoView.setManaged(true);
        }
        if (searchPreferencesView != null) {
            searchPreferencesView.setVisible(false);
            searchPreferencesView.setManaged(false);
        }
        if (securityView != null) {
            securityView.setVisible(false);
            securityView.setManaged(false);
        }
        if (btnPersonalInfo != null) setActiveButton(btnPersonalInfo);
    }

    public void setUser(User user) {
        System.out.println("DEBUG: ProfileController.setUser called with user: " + user);
        this.currentUser = user;
        if (currentUser != null) {
            loadUserData();
            loadUserPreferences();
            showPersonalInfo(null);

            // Track profile visit
            tn.esprit.utils.ActivityLogger.logVisit(currentUser, "PROFILE");
        }
    }

    private void loadUserData() {
        System.out.println("DEBUG: loadUserData called. currentUser: " + currentUser);

        if (firstNameField != null) firstNameField.setText(currentUser.getFirstName());
        if (lastNameField != null) lastNameField.setText(currentUser.getLastName());
        if (emailField != null) emailField.setText(currentUser.getEmail());
        if (currentEmailLabel != null) currentEmailLabel.setText(currentUser.getEmail());

        if (currentAvatarLabel != null && currentUser.getFirstName() != null && !currentUser.getFirstName().isEmpty()) {
            currentAvatarLabel.setText(currentUser.getFirstName().substring(0, 1).toUpperCase());
        }
        if (phoneField != null) phoneField.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        if (genderComboBox != null) genderComboBox.setValue(currentUser.getGender());

        // FIXED: Handle birth year parsing
        if (currentUser.getBirthYear() != null && !currentUser.getBirthYear().isEmpty() && birthDatePicker != null) {
            try {
                birthDatePicker.setValue(LocalDate.parse(currentUser.getBirthYear()));
            } catch (Exception e) {
                System.out.println("DEBUG: Birth year is range: " + currentUser.getBirthYear());
            }
        }

        // Initialize avatar selection
        initializeAvatarSelection();
    }

    private void loadUserPreferences() {
        currentPreferences = userPreferencesService.getPreferencesByUserId(currentUser.getUserId());
        if (currentPreferences == null) {
            currentPreferences = new UserPreferences();
            currentPreferences.setUserId(currentUser.getUserId());
            userPreferencesService.addPreferences(currentPreferences);
            currentPreferences = userPreferencesService.getPreferencesByUserId(currentUser.getUserId());
        }
        updatePreferencesUI();
    }

    private void updatePreferencesUI() {
        if (currentPreferences != null) {
            if (budgetLabel != null) {
                if (currentPreferences.getBudgetMinPerNight() != null && currentPreferences.getBudgetMaxPerNight() != null) {
                    budgetLabel.setText(currentPreferences.getBudgetMinPerNight() + " - " + currentPreferences.getBudgetMaxPerNight());
                } else {
                    budgetLabel.setText("Not set");
                }
            }
            if (prioritiesLabel != null) {
                prioritiesLabel.setText(currentPreferences.getPriorities() != null ? currentPreferences.getPriorities() : "Not set");
            }
            if (locationLabel != null) {
                locationLabel.setText(currentPreferences.getLocationPreferences() != null ? currentPreferences.getLocationPreferences() : "Not set");
            }
            if (accommodationLabel != null) {
                accommodationLabel.setText(currentPreferences.getAccommodationTypes() != null ? currentPreferences.getAccommodationTypes() : "Not set");
            }
            if (styleLabel != null) {
                styleLabel.setText(currentPreferences.getStylePreferences() != null ? currentPreferences.getStylePreferences() : "Not set");
            }
            if (dietaryLabel != null) {
                dietaryLabel.setText(currentPreferences.getDietaryRestrictions() != null ? currentPreferences.getDietaryRestrictions() : "Not set");
            }
            if (climateLabel != null) {
                climateLabel.setText(currentPreferences.getPreferredClimate() != null ? currentPreferences.getPreferredClimate() : "Not set");
            }
            if (paceLabel != null) {
                paceLabel.setText(currentPreferences.getTravelPace() != null ? currentPreferences.getTravelPace() : "Not set");
            }
            if (groupLabel != null) {
                groupLabel.setText(currentPreferences.getGroupType() != null ? currentPreferences.getGroupType() : "Not set");
            }
        }
    }

    @FXML
    void showPersonalInfo(ActionEvent event) {
        if (personalInfoView != null) {
            personalInfoView.setVisible(true);
            personalInfoView.setManaged(true);
        }
        if (searchPreferencesView != null) {
            searchPreferencesView.setVisible(false);
            searchPreferencesView.setManaged(false);
        }
        if (securityView != null) {
            securityView.setVisible(false);
            securityView.setManaged(false);
        }
        setActiveButton(btnPersonalInfo);
        System.out.println("DEBUG: Switched to Personal Info View");
    }

    @FXML
    void showAccountSecurity(ActionEvent event) {
        if (personalInfoView != null) {
            personalInfoView.setVisible(false);
            personalInfoView.setManaged(false);
        }
        if (searchPreferencesView != null) {
            searchPreferencesView.setVisible(false);
            searchPreferencesView.setManaged(false);
        }
        if (securityView != null) {
            securityView.setVisible(true);
            securityView.setManaged(true);
        }
        setActiveButton(btnSecurity);
        System.out.println("DEBUG: Switched to Account Security View");
    }

    @FXML
    void showSearchPreferences(ActionEvent event) {
        if (personalInfoView != null) {
            personalInfoView.setVisible(false);
            personalInfoView.setManaged(false);
        }
        if (searchPreferencesView != null) {
            searchPreferencesView.setVisible(true);
            searchPreferencesView.setManaged(true);
        }
        if (securityView != null) {
            securityView.setVisible(false);
            securityView.setManaged(false);
        }
        setActiveButton(btnPreferences);
        System.out.println("DEBUG: Switched to Search Preferences View");
    }

    @FXML
    void showUserAnalytics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/user_analytics.fxml"));
            VBox analyticsView = loader.load();
            
            UserAnalyticsController controller = loader.getController();
            controller.setUser(currentUser);
            
            contentArea.getChildren().setAll(analyticsView);
            setActiveButton(btnNotifications);
            System.out.println("DEBUG: Switched to User Analytics View");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load activity dashboard: " + e.getMessage());
        }
    }

    private void setActiveButton(Button activeButton) {
        Button[] railButtons = {btnPersonalInfo, btnPreferences, btnSecurity, btnNotifications};
        for (Button btn : railButtons) {
            if (btn != null) {
                btn.getStyleClass().removeAll("rail-item-active");
                btn.getStyleClass().add("rail-item");
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().removeAll("rail-item");
            activeButton.getStyleClass().add("rail-item-active");
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/home.fxml"));
            Parent root = loader.load();
            
            HomeController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            Scene scene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        if (statusLabel != null) statusLabel.setVisible(false);

        String validationError = validateInput();
        if (validationError != null) {
            if (statusLabel != null) {
                statusLabel.setText(validationError);
                statusLabel.setVisible(true);
                statusLabel.setStyle("-fx-text-fill: red;");
            }
            return;
        }

        if (firstNameField != null) currentUser.setFirstName(firstNameField.getText());
        if (lastNameField != null) currentUser.setLastName(lastNameField.getText());
        if (emailField != null) currentUser.setEmail(emailField.getText());
        if (phoneField != null) currentUser.setPhoneNumber(phoneField.getText());
        if (genderComboBox != null) currentUser.setGender(genderComboBox.getValue());

        if (birthDatePicker != null && birthDatePicker.getValue() != null) {
            currentUser.setBirthYear(birthDatePicker.getValue().toString());
        }

        boolean updateSuccess = userService.updateUser(currentUser);

        if (newPasswordField != null && !newPasswordField.getText().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(newPasswordField.getText(), BCrypt.gensalt());
            currentUser.setPassword(hashedPassword);
            boolean passUpdate = userService.updateUserPassword(currentUser);
            updateSuccess = updateSuccess && passUpdate;
        }

        if (statusLabel != null) {
            if (updateSuccess) {
                statusLabel.setText("Profile updated successfully!");
                statusLabel.setVisible(true);
                statusLabel.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                loadUserData();
            } else {
                statusLabel.setText("Failed to update profile in database.");
                statusLabel.setVisible(true);
                statusLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            }
        }
    }

    private String validateInput() {
        boolean isValid = true;
        clearAllErrors();

        if (!ValidationUtils.isValidName(firstNameField.getText())) {
            showFieldError(firstNameField, firstNameError,
                    ValidationUtils.isNotEmpty(firstNameField.getText()) ?
                            ValidationUtils.getNameError("First name") :
                            ValidationUtils.getRequiredFieldError("First name"));
            isValid = false;
        }

        if (!ValidationUtils.isValidName(lastNameField.getText())) {
            showFieldError(lastNameField, lastNameError,
                    ValidationUtils.isNotEmpty(lastNameField.getText()) ?
                            ValidationUtils.getNameError("Last name") :
                            ValidationUtils.getRequiredFieldError("Last name"));
            isValid = false;
        }

        if (!ValidationUtils.isValidEmail(emailField.getText())) {
            showFieldError(emailField, emailError,
                    ValidationUtils.isNotEmpty(emailField.getText()) ?
                            ValidationUtils.getEmailError() :
                            ValidationUtils.getRequiredFieldError("Email"));
            isValid = false;
        }

        if (!ValidationUtils.isValidPhone(phoneField.getText())) {
            showFieldError(phoneField, phoneError, ValidationUtils.getPhoneError());
            isValid = false;
        }

        if (!isValid) {
            return "Please fix the red errors below.";
        }
        return null;
    }

    private void clearAllErrors() {
        clearFieldError(firstNameField, firstNameError);
        clearFieldError(lastNameField, lastNameError);
        clearFieldError(emailField, emailError);
        clearFieldError(phoneField, phoneError);
        clearFieldError(genderComboBox, genderError);
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        if (field != null && errorLabel != null) {
            field.getStyleClass().add("input-error");
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void clearFieldError(Control field, Label errorLabel) {
        if (field != null && errorLabel != null) {
            field.getStyleClass().remove("input-error");
            errorLabel.setVisible(false);
        }
    }

    @FXML
    void handleEditBudget(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(budgetLabel.getText().equals("Not set") ? "50-200" : budgetLabel.getText());
        dialog.setTitle("Edit Budget");
        dialog.setHeaderText("Enter Budget Range (Min-Max)");
        dialog.setContentText("Range:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(range -> {
            try {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    BigDecimal min = new BigDecimal(parts[0].trim());
                    BigDecimal max = new BigDecimal(parts[1].trim());
                    currentPreferences.setBudgetMinPerNight(min);
                    currentPreferences.setBudgetMaxPerNight(max);
                    savePreferences();
                } else {
                    showAlert("Invalid Format", "Please use format: Min-Max (e.g., 50-200)");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Number", "Please enter valid numeric values.");
            }
        });
    }

    @FXML
    void handleEditStyle(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getStylePreferences());
        dialog.setTitle("Edit Travel Style");
        dialog.setHeaderText("Enter your travel style");
        dialog.setContentText("Style (e.g., Modern, Rustic, Luxury):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(style -> {
            currentPreferences.setStylePreferences(style);
            savePreferences();
        });
    }

    @FXML
    void handleEditPace(ActionEvent event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Moderate", "Relaxed", "Moderate", "Fast-paced");
        dialog.setTitle("Edit Travel Pace");
        dialog.setHeaderText("Select your preferred travel pace");
        dialog.setContentText("Pace:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(pace -> {
            currentPreferences.setTravelPace(pace);
            savePreferences();
        });
    }

    @FXML
    void handleEditPreferences(ActionEvent event) {
        showSearchPreferences(event);
    }

    @FXML
    void handleEditPriorities(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getPriorities());
        dialog.setTitle("Edit Travel Priorities");
        dialog.setHeaderText("Enter your travel priorities");
        dialog.setContentText("Priorities (e.g., Culture, Adventure, Relaxation):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(priorities -> {
            currentPreferences.setPriorities(priorities);
            savePreferences();
        });
    }

    @FXML
    void handleEditLocation(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getLocationPreferences());
        dialog.setTitle("Edit Location Preferences");
        dialog.setHeaderText("Enter your location preferences");
        dialog.setContentText("Locations (e.g., Beach, Mountains, City):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(location -> {
            currentPreferences.setLocationPreferences(location);
            savePreferences();
        });
    }

    @FXML
    void handleEditAccommodation(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getAccommodationTypes());
        dialog.setTitle("Edit Accommodation Types");
        dialog.setHeaderText("Enter your preferred accommodation types");
        dialog.setContentText("Types (e.g., Hotel, Hostel, Resort, Airbnb):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(accommodation -> {
            currentPreferences.setAccommodationTypes(accommodation);
            savePreferences();
        });
    }

    @FXML
    void handleEditDietary(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getDietaryRestrictions());
        dialog.setTitle("Edit Dietary Restrictions");
        dialog.setHeaderText("Enter your dietary restrictions");
        dialog.setContentText("Restrictions (e.g., Vegetarian, Vegan, Gluten-free):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(dietary -> {
            currentPreferences.setDietaryRestrictions(dietary);
            savePreferences();
        });
    }

    @FXML
    void handleEditClimate(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(currentPreferences.getPreferredClimate());
        dialog.setTitle("Edit Preferred Climate");
        dialog.setHeaderText("Enter your preferred climate");
        dialog.setContentText("Climate (e.g., Tropical, Temperate, Cold):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(climate -> {
            currentPreferences.setPreferredClimate(climate);
            savePreferences();
        });
    }

    @FXML
    void handleEditGroup(ActionEvent event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Solo", "Solo", "Couple", "Family", "Friends", "Business");
        dialog.setTitle("Edit Group Type");
        dialog.setHeaderText("Select your group type");
        dialog.setContentText("Group:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(group -> {
            currentPreferences.setGroupType(group);
            savePreferences();
        });
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        clearPasswordErrors();

        String currentPwd = currentPasswordField.getText();
        String newPwd = newPasswordField.getText();
        String confirmPwd = confirmPasswordField.getText();

        boolean isValid = true;

        if (!ValidationUtils.isNotEmpty(currentPwd)) {
            showFieldError(currentPasswordField, currentPasswordError,
                    ValidationUtils.getRequiredFieldError("Current password"));
            isValid = false;
        } else {
            boolean passwordMatch = false;
            try {
                passwordMatch = BCrypt.checkpw(currentPwd, currentUser.getPassword());
            } catch (IllegalArgumentException e) {
                passwordMatch = currentPwd.equals(currentUser.getPassword());
            }

            if (!passwordMatch) {
                showFieldError(currentPasswordField, currentPasswordError, "Incorrect current password");
                isValid = false;
            }
        }

        if (!ValidationUtils.isValidPassword(newPwd)) {
            showFieldError(newPasswordField, newPasswordError,
                    ValidationUtils.isNotEmpty(newPwd) ?
                            ValidationUtils.getPasswordError() :
                            ValidationUtils.getRequiredFieldError("New password"));
            isValid = false;
        }

        if (!ValidationUtils.passwordsMatch(newPwd, confirmPwd)) {
            showFieldError(confirmPasswordField, confirmPasswordError,
                    ValidationUtils.getPasswordMismatchError());
            isValid = false;
        }

        if (!isValid) {
            if (passwordStatusLabel != null) {
                passwordStatusLabel.setText("Please fix the errors above.");
                passwordStatusLabel.setStyle("-fx-text-fill: red;");
                passwordStatusLabel.setVisible(true);
            }
            return;
        }

        try {
            String hashedPassword = BCrypt.hashpw(newPasswordField.getText(), BCrypt.gensalt());
            currentUser.setPassword(hashedPassword);
            boolean success = userService.updateUserPassword(currentUser);

            if (success) {
                if (passwordStatusLabel != null) {
                    passwordStatusLabel.setText("Password changed successfully!");
                    passwordStatusLabel.setStyle("-fx-text-fill: green;");
                    passwordStatusLabel.setVisible(true);
                }
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                if (passwordStatusLabel != null) {
                    passwordStatusLabel.setText("Failed to change password.");
                    passwordStatusLabel.setStyle("-fx-text-fill: red;");
                    passwordStatusLabel.setVisible(true);
                }
            }
        } catch (Exception e) {
            if (passwordStatusLabel != null) {
                passwordStatusLabel.setText("Error: " + e.getMessage());
                passwordStatusLabel.setStyle("-fx-text-fill: red;");
                passwordStatusLabel.setVisible(true);
            }
        }
    }

    private void clearPasswordErrors() {
        clearFieldError(currentPasswordField, currentPasswordError);
        clearFieldError(newPasswordField, newPasswordError);
        clearFieldError(confirmPasswordField, confirmPasswordError);
        if (passwordStatusLabel != null) {
            passwordStatusLabel.setVisible(false);
        }
    }

    private void savePreferences() {
        boolean success = userPreferencesService.updatePreferences(currentPreferences);
        if (success) {
            updatePreferencesUI();
        } else {
            showAlert("Error", "Failed to save preferences.");
        }
    }

    @FXML
    void handleCheckEmailReputation(ActionEvent event) {
        String email = currentUser != null ? currentUser.getEmail() : null;
        if (email == null || email.isBlank()) {
            showAlert("No Email", "No email address is associated with your account.");
            return;
        }

        if (checkEmailBtn != null) {
            checkEmailBtn.setDisable(true);
            checkEmailBtn.setText("⏳ Checking...");
        }

        Thread bgThread = new Thread(() -> {
            EmailReputationService.EmailReputation result = emailReputationService.checkEmailReputation(email);
            Platform.runLater(() -> {
                if (checkEmailBtn != null) {
                    checkEmailBtn.setDisable(false);
                    checkEmailBtn.setText("\uD83D\uDD0D Check Email Reputation");
                }
                if (result == null) {
                    showAlert("Connection Error", "Could not reach the email reputation service.");
                    return;
                }
                showEmailReputationDialog(email, result);
            });
        });
        bgThread.setDaemon(true);
        bgThread.start();
    }

    private void showEmailReputationDialog(String email, EmailReputationService.EmailReputation r) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Email Reputation Report");
        dialog.setHeaderText("Results for: " + email);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setMinWidth(380);

        boolean isValid = r.isFormatValid() && r.isSmtpValid();
        Label validRow = buildRow(
                isValid ? "\u2705" : "\u274C",
                isValid ? "Email is valid and deliverable" : "Email is invalid: " + r.getStatusDetail(),
                isValid ? "#166534" : "#991B1B",
                isValid ? "#DCFCE7" : "#FEE2E2"
        );

        int breaches = r.getTotalBreaches();
        Label breachRow = buildRow(
                breaches > 0 ? "\u26A0\uFE0F" : "\u2705",
                breaches > 0 ? "Found in " + breaches + " data breach(es)" : "No known data breaches",
                breaches > 0 ? "#92400E" : "#166534",
                breaches > 0 ? "#FEF3C7" : "#DCFCE7"
        );

        int scorePercent = (int) Math.round(r.getScore() * 100);
        String scoreColor = scorePercent >= 70 ? "#166534" : (scorePercent >= 40 ? "#92400E" : "#991B1B");
        String scoreBg = scorePercent >= 70 ? "#DCFCE7" : (scorePercent >= 40 ? "#FEF3C7" : "#FEE2E2");
        Label scoreRow = buildRow("\uD83D\uDCCA", "Quality score: " + scorePercent + "%", scoreColor, scoreBg);

        Label disposableRow = buildRow(
                "\u2139\uFE0F",
                r.isDisposable() ? "Disposable/temporary email detected" : "Not a disposable email",
                r.isDisposable() ? "#92400E" : "#166534",
                r.isDisposable() ? "#FEF3C7" : "#DCFCE7"
        );

        Label freeRow = buildRow(
                "\u2139\uFE0F",
                r.isFreeEmail() ? "Free email provider" : "Not a free email provider",
                "#1E40AF", "#EFF6FF"
        );

        content.getChildren().addAll(validRow, breachRow, scoreRow, disposableRow, freeRow);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private Label buildRow(String icon, String text, String textColor, String bgColor) {
        Label lbl = new Label(icon + "  " + text);
        lbl.setStyle(
                "-fx-background-color: " + bgColor + "; " +
                        "-fx-text-fill: " + textColor + "; " +
                        "-fx-font-size: 13px; " +
                        "-fx-padding: 10 14; " +
                        "-fx-background-radius: 8;"
        );
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setWrapText(true);
        return lbl;
    }

    @FXML
    void handleDeleteAccount(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action is permanent and cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userPreferencesService.deletePreferencesByUserId(currentUser.getUserId());
                boolean success = userService.deleteUser(currentUser.getUserId());
                if (success) {
                    handleLogout(event);
                } else {
                    showAlert("Error", "Failed to delete account.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred: " + e.getMessage());
            }
        }
    }

    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ========== AVATAR METHODS ==========

    /**
     * Initialize avatar selection with DiceBear API - BIG SMILE STYLE ONLY
     */
    private void initializeAvatarSelection() {
        if (avatarContainer == null) return;

        avatarContainer.getChildren().clear();

        // Add current user's avatar first
        VBox currentAvatarBox = createCurrentAvatarBox();
        avatarContainer.getChildren().add(currentAvatarBox);

        // Add separator
        Separator separator = new Separator();
        separator.setPrefWidth(avatarContainer.getWidth());
        avatarContainer.getChildren().add(separator);

        Label instructionLabel = new Label("Choose your avatar from the Big Smile collection:");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 10 0;");
        avatarContainer.getChildren().add(instructionLabel);

        // Create a GridPane for 2 rows layout
        GridPane avatarGrid = new GridPane();
        avatarGrid.setHgap(15);
        avatarGrid.setVgap(15);
        avatarGrid.setAlignment(Pos.CENTER_LEFT);

        // Add avatar options from seeds (BIG SMILE STYLE ONLY) - Exactly 2 rows
        int col = 0, row = 0;
        for (String seed : AVATAR_SEEDS) {
            VBox avatarOption = createAvatarOption("big-smile", seed);
            avatarGrid.add(avatarOption, col, row);

            row++;
            if (row > 1) { // Only 2 rows: 0 and 1
                row = 0;
                col++;
            }
        }

        avatarContainer.getChildren().add(avatarGrid);
    }

    /**
     * Create the current user's avatar display
     */
    private VBox createCurrentAvatarBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10 0 20 0;");

        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER);

        StackPane avatarCircle = new StackPane();
        avatarCircle.setPrefSize(100, 100);
        avatarCircle.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #4cccad, #2c7a6b);" +
                        "-fx-background-radius: 50;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
        );

        if (currentUser != null && currentUser.getAvatarId() != null && !currentUser.getAvatarId().isEmpty()) {
            // User has saved avatar - format: "big-smile:Adrian"
            String[] parts = currentUser.getAvatarId().split(":");
            String style = parts.length > 0 ? parts[0] : "big-smile";
            String seed = parts.length > 1 ? parts[1] : currentUser.getFirstName();

            String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;
            loadAvatarImage(avatarUrl, avatarCircle, 100);
        } else {
            // Show initials
            String initials = currentUser != null ?
                    (currentUser.getFirstName().substring(0, 1) +
                            (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty() ?
                                    currentUser.getLastName().substring(0, 1) : "")).toUpperCase() : "U";

            Label initialLabel = new Label(initials);
            initialLabel.setStyle("-fx-text-fill: white; -fx-font-size: 40px; -fx-font-weight: bold;");
            avatarCircle.getChildren().add(initialLabel);
        }

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label emailLabel = new Label(currentUser.getEmail());
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        infoBox.getChildren().addAll(nameLabel, emailLabel);
        content.getChildren().addAll(avatarCircle, infoBox);

        box.getChildren().add(content);
        return box;
    }

    /**
     * Load avatar image asynchronously with retry
     */
    private void loadAvatarImage(String url, StackPane container, double size) {
        Thread loadThread = new Thread(() -> {
            int retries = 3;
            while (retries > 0) {
                try {
                    // Add a timestamp to avoid cache issues
                    String urlWithTimestamp = url + "&t=" + System.currentTimeMillis();
                    Image img = new Image(urlWithTimestamp, size, size, true, true, true);

                    Platform.runLater(() -> {
                        container.getChildren().clear();
                        ImageView avatarView = new ImageView(img);
                        avatarView.setFitWidth(size);
                        avatarView.setFitHeight(size);
                        avatarView.setPreserveRatio(true);

                        // Make circular
                        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size/2, size/2, size/2);
                        avatarView.setClip(clip);

                        container.getChildren().add(avatarView);
                    });
                    return; // Success - exit method

                } catch (Exception e) {
                    System.err.println("Error loading avatar (attempt " + (4-retries) + "): " + e.getMessage());
                    retries--;
                    try {
                        Thread.sleep(500); // Wait before retry
                    } catch (InterruptedException ie) {}
                }
            }

            // If all retries failed, show colored circle
            Platform.runLater(() -> {
                container.setStyle(
                        "-fx-background-color: " + getRandomColor() + ";" +
                                "-fx-background-radius: " + (size/2) + ";"
                );

                String initials = currentUser != null ?
                        (currentUser.getFirstName().substring(0, 1) +
                                (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty() ?
                                        currentUser.getLastName().substring(0, 1) : "")).toUpperCase() : "U";

                Label initialLabel = new Label(initials);
                initialLabel.setStyle("-fx-text-fill: white; -fx-font-size: " + (size/2.5) + "px; -fx-font-weight: bold;");
                container.getChildren().add(initialLabel);
            });
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Create an avatar option with specific style and seed
     */
    private VBox createAvatarOption(String style, String seed) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 10;");
        box.setPrefWidth(100);

        String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;

        StackPane previewCircle = new StackPane();
        previewCircle.setPrefSize(70, 70);
        previewCircle.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-background-radius: 35;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        // Load preview image
        Thread loadThread = new Thread(() -> {
            try {
                Image img = new Image(avatarUrl, 70, 70, true, true, true);
                Platform.runLater(() -> {
                    ImageView avatarView = new ImageView(img);
                    avatarView.setFitWidth(60);
                    avatarView.setFitHeight(60);
                    avatarView.setPreserveRatio(true);

                    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(30, 30, 30);
                    avatarView.setClip(clip);

                    previewCircle.getChildren().clear();
                    previewCircle.getChildren().add(avatarView);
                });
            } catch (Exception e) {
                // Fallback to colored circle with initial
                Platform.runLater(() -> {
                    previewCircle.setStyle(
                            "-fx-background-color: " + getRandomColor() + ";" +
                                    "-fx-background-radius: 35;"
                    );

                    Label initialLabel = new Label(seed.substring(0, 1).toUpperCase());
                    initialLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
                    previewCircle.getChildren().add(initialLabel);
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();

        // Seed name label
        Label seedLabel = new Label(seed);
        seedLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        box.getChildren().addAll(previewCircle, seedLabel);

        // Store full avatar ID as "style:seed"
        String avatarId = style + ":" + seed;
        box.setOnMouseClicked(e -> selectAvatar(avatarId, style, seed));

        // Hover effect
        box.setOnMouseEntered(e ->
                box.setStyle("-fx-padding: 10; -fx-cursor: hand; -fx-background-color: #e8f4f8; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(76,204,173,0.3), 10, 0, 0, 2);"));
        box.setOnMouseExited(e ->
                box.setStyle("-fx-padding: 10; -fx-cursor: hand; -fx-background-radius: 10;"));

        return box;
    }

    /**
     * Get random color for fallback
     */
    private String getRandomColor() {
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEEAD",
                "#D4A5A5", "#9B59B6", "#3498DB", "#E67E22", "#2ECC71",
                "#E74C3C", "#1ABC9C", "#F1C40F", "#E67E22", "#95A5A6"};
        int random = (int)(Math.random() * colors.length);
        return colors[random];
    }

    /**
     * Handle avatar selection
     */
    private void selectAvatar(String avatarId, String style, String seed) {
        selectedAvatarStyle = style;

        // Update current user's avatar_id (store as "style:seed")
        currentUser.setAvatarId(avatarId);

        // Save to database
        boolean updated = userService.updateUserAvatar(currentUser.getUserId(), avatarId);

        if (updated) {
            // Update the current avatar display
            updateCurrentAvatar(style, seed);

            // Show success message
            showToast("✓ Avatar updated to " + seed);

            System.out.println("✅ Avatar updated: " + avatarId);
        } else {
            showAlert("Error", "Failed to update avatar. Please try again.");
        }
    }

    /**
     * Update the current avatar display
     */
    private void updateCurrentAvatar(String style, String seed) {
        if (avatarContainer != null && !avatarContainer.getChildren().isEmpty()) {
            VBox currentBox = (VBox) avatarContainer.getChildren().get(0);
            HBox content = (HBox) currentBox.getChildren().get(0);
            StackPane avatarCircle = (StackPane) content.getChildren().get(0);

            String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;
            loadAvatarImage(avatarUrl, avatarCircle, 100);
        }
    }

    private void showToast(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(() -> alert.close());
            } catch (InterruptedException e) {}
        }).start();
    }

    private static class Toast {
        private final Stage ownerStage;
        private final String message;

        public Toast(Window owner, String msg) {
            this.ownerStage = (Stage) owner;
            this.message = msg;
        }

        public void show() {
            Stage stage = new Stage();
            stage.initOwner(ownerStage);
            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.UNDECORATED);

            Label label = new Label(message);
            label.setStyle(
                    "-fx-background-color: #4cccad;" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 15 25;" +
                            "-fx-background-radius: 30;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
            );

            Scene scene = new Scene(new StackPane(label));
            stage.setScene(scene);

            stage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - 150);
            stage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);

            stage.show();

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> stage.close());
            delay.play();
        }
    }
}