package tn.esprit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.math.BigDecimal;
import java.sql.SQLException;

public class PackDialogController {

    @FXML private Label dialogTitle;
    @FXML private TextField txtTitle, txtPrice;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<PackCategory> cmbCategory;
    @FXML private ComboBox<Destination> cmbDestination;
    @FXML private ComboBox<Accommodation> cmbAccommodation;
    @FXML private ComboBox<Activity> cmbActivity;
    @FXML private ComboBox<Transport> cmbTransport;
    @FXML private ComboBox<Pack.Status> cmbStatus;
    @FXML private Spinner<Integer> spinDuration;
    @FXML private Button btnSave, btnCancel;

    private PackService packService;
    private PackCategoryService categoryService;
    private LookupService lookupService;

    private Pack packToEdit; // null = Add mode, not null = Edit mode
    private boolean saveClicked = false;

    public void initialize() {
        packService = new PackService();
        categoryService = new PackCategoryService();
        lookupService = new LookupService();

        loadComboBoxes();
        setupButtons();
    }

    private void loadComboBoxes() {
        try {
            // Categories
            cmbCategory.setItems(FXCollections.observableArrayList(categoryService.afficherList()));
            cmbCategory.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(PackCategory item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            cmbCategory.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(PackCategory item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            // Destinations
            cmbDestination.setItems(FXCollections.observableArrayList(lookupService.getAllDestinations()));
            cmbDestination.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Destination item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            cmbDestination.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Destination item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            // Accommodations
            cmbAccommodation.setItems(FXCollections.observableArrayList(lookupService.getAllAccommodations()));
            cmbAccommodation.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Accommodation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            cmbAccommodation.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Accommodation item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            // Activities
            cmbActivity.setItems(FXCollections.observableArrayList(lookupService.getAllActivities()));
            cmbActivity.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Activity item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });
            cmbActivity.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Activity item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName());
                }
            });

            // Transport
            cmbTransport.setItems(FXCollections.observableArrayList(lookupService.getAllTransport()));
            cmbTransport.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Transport item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getType());
                }
            });
            cmbTransport.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Transport item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getType());
                }
            });

            // Status
            cmbStatus.setItems(FXCollections.observableArrayList(Pack.Status.values()));

        } catch (SQLException e) {
            showError("Failed to load dropdown data: " + e.getMessage());
        }
    }

    private void setupButtons() {
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
    }

    public void setPackToEdit(Pack pack) {
        this.packToEdit = pack;
        dialogTitle.setText("Edit Pack");

        if (pack != null) {
            txtTitle.setText(pack.getTitle());
            txtDescription.setText(pack.getDescription());
            txtPrice.setText(pack.getBasePrice().toString());
            spinDuration.getValueFactory().setValue(pack.getDurationDays());
            cmbStatus.setValue(pack.getStatus());

            try {
                // Set selected items in ComboBoxes
                cmbCategory.getItems().stream()
                    .filter(c -> c.getIdCategory() == pack.getCategoryId())
                    .findFirst()
                    .ifPresent(cmbCategory::setValue);

                cmbDestination.getItems().stream()
                    .filter(d -> d.getIdDestination() == pack.getDestinationId())
                    .findFirst()
                    .ifPresent(cmbDestination::setValue);

                cmbAccommodation.getItems().stream()
                    .filter(a -> a.getIdAccommodation() == pack.getAccommodationId())
                    .findFirst()
                    .ifPresent(cmbAccommodation::setValue);

                cmbActivity.getItems().stream()
                    .filter(a -> a.getIdActivity() == pack.getActivityId())
                    .findFirst()
                    .ifPresent(cmbActivity::setValue);

                cmbTransport.getItems().stream()
                    .filter(t -> t.getIdTransport() == pack.getTransportId())
                    .findFirst()
                    .ifPresent(cmbTransport::setValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSave() {
        // Validation
        if (txtTitle.getText().trim().isEmpty()) {
            showError("Please enter a title");
            return;
        }
        if (cmbCategory.getValue() == null) {
            showError("Please select a category");
            return;
        }
        if (cmbDestination.getValue() == null) {
            showError("Please select a destination");
            return;
        }
        if (cmbAccommodation.getValue() == null) {
            showError("Please select an accommodation");
            return;
        }
        if (cmbActivity.getValue() == null) {
            showError("Please select an activity");
            return;
        }
        if (cmbTransport.getValue() == null) {
            showError("Please select a transport");
            return;
        }
        if (txtPrice.getText().trim().isEmpty()) {
            showError("Please enter a price");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than 0");
                return;
            }

            if (packToEdit == null) {
                // Add new pack
                Pack newPack = new Pack(
                    txtTitle.getText().trim(),
                    txtDescription.getText().trim(),
                    cmbDestination.getValue().getIdDestination(),
                    cmbAccommodation.getValue().getIdAccommodation(),
                    cmbActivity.getValue().getIdActivity(),
                    cmbTransport.getValue().getIdTransport(),
                    cmbCategory.getValue().getIdCategory(),
                    spinDuration.getValue(),
                    price
                );
                packService.add(newPack);
            } else {
                // Update existing pack
                packToEdit.setTitle(txtTitle.getText().trim());
                packToEdit.setDescription(txtDescription.getText().trim());
                packToEdit.setCategoryId(cmbCategory.getValue().getIdCategory());
                packToEdit.setDestinationId(cmbDestination.getValue().getIdDestination());
                packToEdit.setAccommodationId(cmbAccommodation.getValue().getIdAccommodation());
                packToEdit.setActivityId(cmbActivity.getValue().getIdActivity());
                packToEdit.setTransportId(cmbTransport.getValue().getIdTransport());
                packToEdit.setDurationDays(spinDuration.getValue());
                packToEdit.setBasePrice(price);
                packToEdit.setStatus(cmbStatus.getValue());
                packService.modifier(packToEdit);
            }

            saveClicked = true;
            closeDialog();

        } catch (NumberFormatException e) {
            showError("Invalid price format");
        } catch (SQLException e) {
            showError("Failed to save pack: " + e.getMessage());
        }
    }

    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
