package tn.esprit.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.entities.PackCategory;
import tn.esprit.services.PackCategoryService;
import tn.esprit.services.PackService;

import java.sql.SQLException;
import java.util.Optional;

public class AdminCategoriesController {

    @FXML private TableView<PackCategory> categoriesTable;
    @FXML private TableColumn<PackCategory, String> colId, colName, colPackCount, colActions;
    
    @FXML private TextField searchField;
    @FXML private Button btnAddCategory;

    private PackCategoryService categoryService;
    private PackService packService;
    
    private ObservableList<PackCategory> categoriesList;

    public void initialize() {
        categoryService = new PackCategoryService();
        packService = new PackService();
        
        categoriesList = FXCollections.observableArrayList();
        
        setupTable();
        loadCategories();
        
        btnAddCategory.setOnAction(e -> handleAddCategory());
        searchField.textProperty().addListener((obs, old, newVal) -> filterCategories());
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdCategory())));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        colPackCount.setCellValueFactory(data -> {
            try {
                int count = packService.getByCategory(data.getValue().getIdCategory()).size();
                return new SimpleStringProperty(String.valueOf(count));
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });
        
        // Actions column
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox box = new HBox(8, editBtn, deleteBtn);
            
            {
                box.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #4E8EA2; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #EF5350; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                
                editBtn.setOnAction(e -> handleEditCategory(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDeleteCategory(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        
        categoriesTable.setItems(categoriesList);
    }

    private void loadCategories() {
        try {
            categoriesList.clear();
            categoriesList.addAll(categoryService.afficherList());
        } catch (SQLException e) {
            showError("Failed to load categories: " + e.getMessage());
        }
    }

    private void filterCategories() {
        // TODO: Implement search filtering
        loadCategories();
    }

    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Create New Pack Category");
        dialog.setContentText("Category Name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                try {
                    PackCategory newCat = new PackCategory(name.trim());
                    categoryService.add(newCat);
                    loadCategories();
                    showInfo("Category added successfully!");
                } catch (SQLException e) {
                    showError("Failed to add category: " + e.getMessage());
                }
            }
        });
    }

    private void handleEditCategory(PackCategory category) {
        if (category == null) return;
        
        TextInputDialog dialog = new TextInputDialog(category.getName());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit Pack Category");
        dialog.setContentText("Category Name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                try {
                    category.setName(name.trim());
                    categoryService.modifier(category);
                    loadCategories();
                    showInfo("Category updated successfully!");
                } catch (SQLException e) {
                    showError("Failed to update category: " + e.getMessage());
                }
            }
        });
    }

    private void handleDeleteCategory(PackCategory category) {
        if (category == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Category");
        confirm.setHeaderText("Delete " + category.getName() + "?");
        confirm.setContentText("This action cannot be undone. Packs in this category will have their category set to NULL.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.delete(category);
                loadCategories();
                showInfo("Category deleted successfully!");
            } catch (SQLException e) {
                showError("Failed to delete category: " + e.getMessage());
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
