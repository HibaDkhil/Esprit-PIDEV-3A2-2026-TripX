package tn.esprit.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.entities.LoyaltyPoints;
import tn.esprit.services.LoyaltyPointsService;

import java.sql.SQLException;
import java.util.Optional;

public class AdminLoyaltyController {

    @FXML private TableView<LoyaltyPoints> loyaltyTable;
    @FXML private TableColumn<LoyaltyPoints, String> colUserId, colUsername, colPoints, colLevel, colDiscount, colLastUpdated, colActions;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> levelFilter;
    
    @FXML private Label bronzeCount, silverCount, goldCount;

    private LoyaltyPointsService loyaltyService;
    
    private ObservableList<LoyaltyPoints> loyaltyList;

    public void initialize() {
        loyaltyService = new LoyaltyPointsService();
        
        loyaltyList = FXCollections.observableArrayList();
        
        setupTable();
        setupFilters();
        loadLoyaltyData();
        updateStats();
        
        searchField.textProperty().addListener((obs, old, newVal) -> filterLoyalty());
    }

    private void setupTable() {
        colUserId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getUserId())));
        
        // TODO: Need to join with users table to get username - for now just show user_id
        colUsername.setCellValueFactory(data -> new SimpleStringProperty("User " + data.getValue().getUserId()));
        
        colPoints.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalPoints())));
        
        colLevel.setCellValueFactory(data -> {
            LoyaltyPoints.Level level = data.getValue().computeLevel();
            String emoji = level == LoyaltyPoints.Level.GOLD ? "🥇" :
                          level == LoyaltyPoints.Level.SILVER ? "🥈" : "🥉";
            return new SimpleStringProperty(emoji + " " + level.name());
        });
        
        colDiscount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoyaltyDiscountPercent() + "%"));
        
        colLastUpdated.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getUpdatedAt() != null 
                ? data.getValue().getUpdatedAt().toString() 
                : "N/A"));
        
        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button adjustBtn = new Button("➕ Adjust Points");
            private final HBox box = new HBox(adjustBtn);
            
            {
                box.setAlignment(Pos.CENTER);
                adjustBtn.setStyle("-fx-background-color: #4E8EA2; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                adjustBtn.setOnAction(e -> handleAdjustPoints(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        
        loyaltyTable.setItems(loyaltyList);
    }

    private void setupFilters() {
        levelFilter.setItems(FXCollections.observableArrayList("All", "BRONZE", "SILVER", "GOLD"));
        levelFilter.setValue("All");
        levelFilter.setOnAction(e -> filterLoyalty());
    }

    private void loadLoyaltyData() {
        try {
            loyaltyList.clear();
            loyaltyList.addAll(loyaltyService.afficherList());
        } catch (SQLException e) {
            showError("Failed to load loyalty data: " + e.getMessage());
        }
    }

    private void updateStats() {
        int bronze = 0, silver = 0, gold = 0;
        
        for (LoyaltyPoints lp : loyaltyList) {
            switch (lp.computeLevel()) {
                case BRONZE: bronze++; break;
                case SILVER: silver++; break;
                case GOLD:   gold++;   break;
            }
        }
        
        bronzeCount.setText(String.valueOf(bronze));
        silverCount.setText(String.valueOf(silver));
        goldCount.setText(String.valueOf(gold));
    }

    private void filterLoyalty() {
        // TODO: Implement filtering
        loadLoyaltyData();
        updateStats();
    }

    private void handleAdjustPoints(LoyaltyPoints lp) {
        if (lp == null) return;
        
        TextInputDialog dialog = new TextInputDialog(String.valueOf(lp.getTotalPoints()));
        dialog.setTitle("Adjust Points");
        dialog.setHeaderText("Adjust Points for User " + lp.getUserId());
        dialog.setContentText("New Total Points:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(pointsStr -> {
            try {
                int newPoints = Integer.parseInt(pointsStr);
                if (newPoints >= 0) {
                    lp.setTotalPoints(newPoints);
                    loyaltyService.modifier(lp);
                    loadLoyaltyData();
                    updateStats();
                    showInfo("Points adjusted successfully!");
                } else {
                    showError("Points must be >= 0");
                }
            } catch (NumberFormatException e) {
                showError("Invalid number format");
            } catch (SQLException e) {
                showError("Failed to adjust points: " + e.getMessage());
            }
        });
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
