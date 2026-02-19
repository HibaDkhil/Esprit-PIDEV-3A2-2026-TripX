package tn.esprit.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.controllers.admin.PackDialogController;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminPacksController {

    @FXML private TableView<Pack> packsTable;
    @FXML private TableColumn<Pack, String> colId, colTitle, colDestination, colCategory, colDuration, colPrice, colStatus, colActions;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter, statusFilter;
    @FXML private Button btnAddPack, btnClearFilters;

    private PackService packService;
    private LookupService lookupService;
    private PackCategoryService categoryService;
    
    private ObservableList<Pack> packsList;
    private List<Pack> allPacks;

    public void initialize() {
        packService = new PackService();
        lookupService = new LookupService();
        categoryService = new PackCategoryService();
        
        packsList = FXCollections.observableArrayList();
        
        setupTable();
        setupFilters();
        loadPacks();
        
        // Button handlers
        btnAddPack.setOnAction(e -> handleAddPack());
        btnClearFilters.setOnAction(e -> clearFilters());
        searchField.textProperty().addListener((obs, old, newVal) -> filterPacks());
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdPack())));
        colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        colDestination.setCellValueFactory(data -> {
            try {
                Destination dest = lookupService.getDestinationById(data.getValue().getDestinationId());
                return new SimpleStringProperty(dest != null ? dest.getName() : "N/A");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });
        
        colCategory.setCellValueFactory(data -> {
            try {
                PackCategory cat = categoryService.getById(data.getValue().getCategoryId());
                return new SimpleStringProperty(cat != null ? cat.getName() : "N/A");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });
        
        colDuration.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDurationDays() + " days"));
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBasePrice().toString()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));
        
        // Actions column with Edit/Delete buttons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox box = new HBox(8, editBtn, deleteBtn);
            
            {
                box.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #4E8EA2; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                
                editBtn.setOnAction(e -> handleEditPack(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDeletePack(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        
        packsTable.setItems(packsList);
    }

    private void setupFilters() {
        try {
            // Category filter
            ObservableList<String> categories = FXCollections.observableArrayList("All");
            categoryService.afficherList().forEach(cat -> categories.add(cat.getName()));
            categoryFilter.setItems(categories);
            categoryFilter.setValue("All");
            categoryFilter.setOnAction(e -> filterPacks());
            
            // Status filter
            statusFilter.setItems(FXCollections.observableArrayList("All", "ACTIVE", "INACTIVE"));
            statusFilter.setValue("All");
            statusFilter.setOnAction(e -> filterPacks());
            
        } catch (SQLException e) {
            showError("Failed to load filters: " + e.getMessage());
        }
    }

    private void loadPacks() {
        try {
            allPacks = packService.afficherList();
            packsList.clear();
            packsList.addAll(allPacks);
        } catch (SQLException e) {
            showError("Failed to load packs: " + e.getMessage());
        }
    }

    private void filterPacks() {
        if (allPacks == null) return;

        List<Pack> filtered = allPacks.stream().filter(pack -> {
            // Search filter
            String searchText = searchField.getText().toLowerCase().trim();
            if (!searchText.isEmpty()) {
                boolean matchesTitle = pack.getTitle().toLowerCase().contains(searchText);
                boolean matchesDesc = pack.getDescription() != null && 
                                     pack.getDescription().toLowerCase().contains(searchText);
                if (!matchesTitle && !matchesDesc) {
                    return false;
                }
            }

            // Category filter
            if (!categoryFilter.getValue().equals("All")) {
                try {
                    PackCategory cat = categoryService.getById(pack.getCategoryId());
                    if (cat == null || !cat.getName().equals(categoryFilter.getValue())) {
                        return false;
                    }
                } catch (SQLException e) {
                    return false;
                }
            }

            // Status filter
            if (!statusFilter.getValue().equals("All")) {
                if (!pack.getStatus().name().equals(statusFilter.getValue())) {
                    return false;
                }
            }

            return true;
        }).toList();

        packsList.clear();
        packsList.addAll(filtered);
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.setValue("All");
        statusFilter.setValue("All");
        loadPacks();
    }

    private void handleAddPack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/admin/dialogs/PackDialog.fxml")
            );
            javafx.scene.layout.VBox dialogContent = loader.load();
            PackDialogController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Add New Pack");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.setScene(new javafx.scene.Scene(dialogContent));
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadPacks();
                showInfo("Pack added successfully!");
            }
        } catch (Exception e) {
            showError("Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditPack(Pack pack) {
        if (pack == null) return;

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/admin/dialogs/PackDialog.fxml")
            );
            javafx.scene.layout.VBox dialogContent = loader.load();
            PackDialogController controller = loader.getController();
            controller.setPackToEdit(pack);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Edit Pack");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.setScene(new javafx.scene.Scene(dialogContent));
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadPacks();
                showInfo("Pack updated successfully!");
            }
        } catch (Exception e) {
            showError("Failed to open dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeletePack(Pack pack) {
        if (pack == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Pack");
        confirm.setHeaderText("Delete " + pack.getTitle() + "?");
        confirm.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                packService.delete(pack);
                loadPacks();
                showInfo("Pack deleted successfully!");
            } catch (SQLException e) {
                showError("Failed to delete pack: " + e.getMessage());
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
