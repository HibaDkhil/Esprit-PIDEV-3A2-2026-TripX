package tn.esprit.controllers.admin;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Bookingtrans;
import tn.esprit.entities.Schedule;
import tn.esprit.entities.Transport;
import tn.esprit.services.BookingtransService;
import tn.esprit.services.ScheduleService;
import tn.esprit.services.TransportService;
import tn.esprit.utils.MyDB;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransportAdminDashboardController {

    // ── ONLY keep these FXML nodes ──
    @FXML private StackPane contentArea;

    private final TransportService transportService = new TransportService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final BookingtransService bookingService = new BookingtransService();

    private static final String BTN_TEAL = "-fx-background-color: #4FB3B5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-background-radius: 5;";
    private static final String BTN_ORANGE = "-fx-background-color: #F06E32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-background-radius: 5;";
    private static final String BTN_DARK = "-fx-background-color: #1F294C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-background-radius: 5;";
    private static final String BTN_GREEN = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-background-radius: 5;";
    private static final String BTN_RED = "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand; -fx-background-radius: 5;";
    private static final String FORM_FIELD = "-fx-padding: 8 12 8 12; -fx-background-radius: 7; -fx-border-color: #D0C8C3; -fx-border-radius: 7; -fx-font-size: 12px; -fx-background-color: white;";

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // Reference to parent dashboard controller
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    public void initialize() {
        // Initialize with empty content - will be populated when tab is selected
    }

    // Public methods that the dashboard can call
    public void showTransportTab() {
        buildTransportView();
    }

    public void showScheduleTab() {
        buildScheduleView();
    }

    public void showBookingTab() {
        buildBookingView();
    }

    // ════════════════════════════════════════
    // DB HELPERS
    // ════════════════════════════════════════

    private Map<Long, String> loadDestinations() {
        Map<Long, String> map = new LinkedHashMap<>();
        String sql = "SELECT destination_id, name FROM destination_trans ORDER BY name";
        try (Connection con = MyDB.getInstance().getConx();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("destination_id");
                String name = rs.getString("name");
                if (name != null) map.put(id, name);
            }
            System.out.println("[Destinations loaded] " + map.size() + " rows");
        } catch (Exception e) {
            System.err.println("[loadDestinations ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    private Map<Integer, String> loadTransportsByType(String type) {
        Map<Integer, String> map = new LinkedHashMap<>();
        String sql = "SELECT transport_id, provider_name, is_active FROM transport WHERE transport_type = ? ORDER BY provider_name";
        try (Connection con = MyDB.getInstance().getConx();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("transport_id");
                    String provider = rs.getString("provider_name");
                    boolean active = rs.getBoolean("is_active");
                    String label = provider + "  (ID: " + id + ")" + (active ? "" : "  [inactive]");
                    map.put(id, label);
                }
            }
            System.out.println("[Transports " + type + "] " + map.size() + " rows");
        } catch (Exception e) {
            System.err.println("[loadTransportsByType ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    private long parseDestId(String val, Map<Long, String> dests) {
        if (val == null) return -1;
        for (Map.Entry<Long, String> e : dests.entrySet())
            if (val.startsWith(e.getValue())) return e.getKey();
        try {
            int idx = val.lastIndexOf("ID: ");
            if (idx >= 0) return Long.parseLong(val.substring(idx + 4).replace(")", "").trim());
        } catch (NumberFormatException ignored) {}
        return -1;
    }

    private int parseTransportId(String val, Map<Integer, String> transMap) {
        if (val == null) return -1;
        for (Map.Entry<Integer, String> e : transMap.entrySet())
            if (e.getValue().equals(val)) return e.getKey();
        return -1;
    }

    // ════════════════════════════════════════
    // TRANSPORT TAB
    // ════════════════════════════════════════

    private void buildTransportView() {
        TableView<Transport> t = new TableView<>();
        styleTable(t);
        t.getColumns().addAll(
                col("ID", "transportId", 60),
                col("Type", "transportType", 90),
                col("Provider", "providerName", 140),
                col("Model", "vehicleModel", 140),
                col("Base Price", "basePrice", 100),
                col("Capacity", "capacity", 90),
                col("Units", "availableUnits", 80),
                col("Eco", "sustainabilityRating", 80),
                col("Active", "isActive", 70));
        t.setItems(FXCollections.observableArrayList(transportService.getAllTransports()));

        Button addBtn = btn("Add Transport", BTN_TEAL);
        Button updBtn = btn("Edit", BTN_ORANGE);
        Button delBtn = btn("Delete", BTN_DARK);
        Button togBtn = btn("Toggle Active", BTN_GREEN);

        addBtn.setOnAction(e -> {
            openTransportForm(null);
            t.setItems(FXCollections.observableArrayList(transportService.getAllTransports()));
        });

        updBtn.setOnAction(e -> {
            Transport sel = t.getSelectionModel().getSelectedItem();
            if (sel != null) {
                openTransportForm(sel);
                t.setItems(FXCollections.observableArrayList(transportService.getAllTransports()));
            } else showAlert("Select a transport row first.");
        });

        delBtn.setOnAction(e -> {
            Transport sel = t.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a transport row first."); return; }
            if (confirm("Delete transport: " + sel.getProviderName() + "?")) {
                transportService.deleteTransport(sel.getTransportId());
                t.setItems(FXCollections.observableArrayList(transportService.getAllTransports()));
                showSuccess("Transport deleted.");
            }
        });

        togBtn.setOnAction(e -> {
            Transport sel = t.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a transport row first."); return; }
            sel.setActive(!sel.isActive());
            transportService.updateTransport(sel);
            t.setItems(FXCollections.observableArrayList(transportService.getAllTransports()));
            showSuccess("Transport status toggled.");
        });

        VBox layout = new VBox(14, sectionTitle("Transport Management"),
                toolbar(addBtn, updBtn, delBtn, togBtn), t);
        VBox.setVgrow(t, Priority.ALWAYS);
        contentArea.getChildren().setAll(layout);
    }

    private void openTransportForm(Transport existing) {
        boolean isEdit = existing != null;
        Stage popup = popupStage(isEdit ? "Update Transport" : "Add Transport");

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("FLIGHT", "VEHICLE"));
        TextField prov = tf("e.g. Air France"), model = tf("e.g. Boeing 737"),
                price = tf("e.g. 250.00"), cap = tf("e.g. 180"),
                units = tf("e.g. 1"), eco = tf("e.g. 4.5");
        TextArea amen = new TextArea();
        amen.setPrefRowCount(2);

        // Image file picker
        TextField imgPath = tf("No file selected");
        imgPath.setEditable(false);
        imgPath.setStyle(FORM_FIELD + " -fx-background-color: #f5f5f5;");
        Button browseBtn = new Button("Browse...");
        browseBtn.setStyle(BTN_DARK + " -fx-font-size: 11px; -fx-padding: 6 12 6 12;");
        HBox imgRow = new HBox(8, imgPath, browseBtn);
        imgRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(imgPath, Priority.ALWAYS);

        final String[] selectedImagePath = { isEdit && existing.getImageUrl() != null ? existing.getImageUrl() : "" };
        if (isEdit && existing.getImageUrl() != null) imgPath.setText(existing.getImageUrl());

        browseBtn.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Transport Image");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fc.showOpenDialog(popup);
            if (file != null) {
                selectedImagePath[0] = file.getAbsolutePath();
                imgPath.setText(file.getName());
            }
        });

        if (isEdit) {
            typeBox.setValue(existing.getTransportType());
            prov.setText(existing.getProviderName());
            model.setText(existing.getVehicleModel());
            price.setText(String.valueOf(existing.getBasePrice()));
            cap.setText(String.valueOf(existing.getCapacity()));
            units.setText(String.valueOf(existing.getAvailableUnits()));
            eco.setText(String.valueOf(existing.getSustainabilityRating()));
            amen.setText(existing.getAmenities());
        } else typeBox.setValue("FLIGHT");

        GridPane g = formGrid();
        int r = 0;
        g.addRow(r++, fl("Type"), typeBox);
        g.addRow(r++, fl("Provider Name"), prov);
        g.addRow(r++, fl("Model"), model);
        g.addRow(r++, fl("Base Price (EUR)"), price);
        g.addRow(r++, fl("Capacity"), cap);
        g.addRow(r++, fl("Available Units"), units);
        g.addRow(r++, fl("Eco Rating (0-5)"), eco);
        g.addRow(r++, fl("Amenities"), amen);
        g.addRow(r++, fl("Transport Image"), imgRow);

        Button save = btn(isEdit ? "Save Changes" : "Add Transport", BTN_TEAL);
        Button canc = btn("Cancel", BTN_DARK);
        canc.setOnAction(e -> popup.close());

        save.setOnAction(e -> {
            try {
                Transport t = isEdit ? existing : new Transport();
                t.setTransportType(typeBox.getValue());
                t.setProviderName(prov.getText().trim());
                t.setVehicleModel(model.getText().trim());
                t.setBasePrice(Double.parseDouble(price.getText().trim()));
                t.setCapacity(Integer.parseInt(cap.getText().trim()));
                t.setAvailableUnits(Integer.parseInt(units.getText().trim()));
                t.setSustainabilityRating(Double.parseDouble(eco.getText().trim()));
                t.setAmenities(amen.getText().trim());
                t.setImageUrl(selectedImagePath[0]);
                if (isEdit) transportService.updateTransport(t);
                else transportService.addTransport(t);
                popup.close();
                showSuccess(isEdit ? "Transport updated!" : "Transport added!");
            } catch (NumberFormatException ex) {
                showAlert("Enter valid numbers for price, capacity, units and eco rating.");
            }
        });

        popup.setScene(new Scene(popupRoot(isEdit ? "Update Transport" : "Add Transport", g, save, canc), 540, 580));
        popup.showAndWait();
    }

    // ════════════════════════════════════════
    // SCHEDULE TAB
    // ════════════════════════════════════════

    private void buildScheduleView() {
        Map<Integer, Transport> tMap = new HashMap<>();
        transportService.getAllTransports().forEach(t -> tMap.put(t.getTransportId(), t));

        Map<Integer, Bookingtrans> bookingBySchedule = new LinkedHashMap<>();
        bookingService.getAllBookings().stream()
                .filter(b -> b.getScheduleId() > 0)
                .forEach(b -> bookingBySchedule.putIfAbsent(b.getScheduleId(), b));

        TableView<Schedule> table = new TableView<>();
        styleTable(table);

        TableColumn<Schedule, String> typeDispCol = new TableColumn<>("Type");
        typeDispCol.setPrefWidth(75);
        typeDispCol.setCellValueFactory(d -> {
            Transport t = tMap.get(d.getValue().getTransportId());
            return new javafx.beans.property.SimpleStringProperty(t != null ? t.getTransportType() : "-");
        });

        TableColumn<Schedule, String> provDispCol = new TableColumn<>("Provider");
        provDispCol.setPrefWidth(120);
        provDispCol.setCellValueFactory(d -> {
            Transport t = tMap.get(d.getValue().getTransportId());
            return new javafx.beans.property.SimpleStringProperty(t != null ? t.getProviderName() : "-");
        });

        TableColumn<Schedule, String> pickupAddrCol = new TableColumn<>("Pickup Address");
        pickupAddrCol.setPrefWidth(190);
        pickupAddrCol.setCellValueFactory(d -> {
            Schedule s = d.getValue();
            Transport t = tMap.get(s.getTransportId());
            if (t == null || !"VEHICLE".equals(t.getTransportType())) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
            Bookingtrans b = bookingBySchedule.get(s.getScheduleId());
            String value = (b != null && b.getPickupAddress() != null && !b.getPickupAddress().isBlank())
                    ? b.getPickupAddress() : "-";
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        TableColumn<Schedule, String> dropoffAddrCol = new TableColumn<>("Drop-off Address");
        dropoffAddrCol.setPrefWidth(190);
        dropoffAddrCol.setCellValueFactory(d -> {
            Schedule s = d.getValue();
            Transport t = tMap.get(s.getTransportId());
            if (t == null || !"VEHICLE".equals(t.getTransportType())) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
            Bookingtrans b = bookingBySchedule.get(s.getScheduleId());
            String value = (b != null && b.getDropoffAddress() != null && !b.getDropoffAddress().isBlank())
                    ? b.getDropoffAddress() : "-";
            return new javafx.beans.property.SimpleStringProperty(value);
        });

        TableColumn<Schedule, LocalDateTime> depCol = new TableColumn<>("Departure");
        depCol.setCellValueFactory(new PropertyValueFactory<>("departureDatetime"));
        depCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DT_FMT));
            }
        });
        depCol.setPrefWidth(120);

        table.getColumns().addAll(
                col("ID", "scheduleId", 55),
                col("Trans.ID", "transportId", 70),
                typeDispCol, provDispCol, depCol,
                col("Class", "travelClass", 90),
                col("Status", "status", 100),
                pickupAddrCol, dropoffAddrCol,
                col("Delay(min)", "delayMinutes", 85),
                col("Price*", "priceMultiplier", 70),
                col("AI Score", "aiDemandScore", 80));
        table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));

        Button addBtn = btn("Add Schedule", BTN_TEAL);
        Button updBtn = btn("Edit", BTN_ORANGE);
        Button delBtn = btn("Delete", BTN_DARK);
        Button dlyBtn = btn("Mark Delayed", BTN_ORANGE);
        Button canBtn = btn("Cancel Schedule", BTN_RED);

        addBtn.setOnAction(e -> {
            openAddScheduleTypePopup();
            table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
        });

        updBtn.setOnAction(e -> {
            Schedule sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a schedule row first."); return; }
            Transport t = tMap.get(sel.getTransportId());
            if (t == null) { showAlert("Transport for this schedule not found."); return; }
            if ("FLIGHT".equals(t.getTransportType())) openFlightScheduleForm(sel);
            else openVehicleScheduleForm(sel);
            table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
        });

        delBtn.setOnAction(e -> {
            Schedule sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a schedule row first."); return; }
            if (confirm("Delete schedule #" + sel.getScheduleId() + "?")) {
                scheduleService.deleteSchedule(sel.getScheduleId());
                table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
                showSuccess("Schedule deleted.");
            }
        });

        dlyBtn.setOnAction(e -> {
            Schedule sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a schedule row first."); return; }
            TextInputDialog dlg = new TextInputDialog("30");
            dlg.setTitle("Mark Delayed");
            dlg.setHeaderText("Enter delay in minutes:");
            dlg.setContentText("Minutes:");
            dlg.showAndWait().ifPresent(val -> {
                try {
                    sel.setDelayMinutes(Integer.parseInt(val.trim()));
                    sel.setStatus("DELAYED");
                    scheduleService.updateSchedule(sel);
                    table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
                    showSuccess("Schedule marked DELAYED.");
                } catch (NumberFormatException ex) { showAlert("Invalid number."); }
            });
        });

        canBtn.setOnAction(e -> {
            Schedule sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a schedule row first."); return; }
            if (confirm("Cancel schedule #" + sel.getScheduleId() + "?")) {
                sel.setStatus("CANCELLED");
                scheduleService.updateSchedule(sel);
                table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
                showSuccess("Schedule cancelled.");
            }
        });

        VBox layout = new VBox(14, sectionTitle("Schedule Management"),
                toolbar(addBtn, updBtn, delBtn, dlyBtn, canBtn), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().setAll(layout);
    }

    // -- Step 1: choose FLIGHT or VEHICLE --
    private void openAddScheduleTypePopup() {
        Stage popup = popupStage("Add Schedule - Choose Type");

        Label hdr = new Label("What type of schedule are you adding?");
        hdr.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1F294C;");

        Button flightBtn = new Button("✈  Flight Schedule");
        flightBtn.setStyle(BTN_DARK + " -fx-font-size:14px; -fx-padding:16 28 16 28;");
        Button vehicleBtn = new Button("🚗  Vehicle Schedule");
        vehicleBtn.setStyle(BTN_TEAL + " -fx-font-size:14px; -fx-padding:16 28 16 28;");
        Button cancBtn = btn("Cancel", BTN_DARK);
        cancBtn.setOnAction(e -> popup.close());

        flightBtn.setOnAction(e -> {
            popup.close();
            Platform.runLater(() -> openFlightScheduleForm(null));
        });
        vehicleBtn.setOnAction(e -> {
            popup.close();
            Platform.runLater(() -> openVehicleScheduleForm(null));
        });

        HBox choices = new HBox(24, flightBtn, vehicleBtn);
        choices.setAlignment(Pos.CENTER);
        choices.setPadding(new Insets(20, 0, 10, 0));

        HBox cancRow = new HBox(cancBtn);
        cancRow.setAlignment(Pos.CENTER_RIGHT);

        Label hdrLbl = new Label("Add Schedule");
        hdrLbl.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        HBox headerBar = new HBox(hdrLbl);
        headerBar.setStyle("-fx-background-color: #1F294C; -fx-padding: 14 22 14 22;");
        headerBar.setAlignment(Pos.CENTER_LEFT);

        VBox body = new VBox(16, hdr, choices, cancRow);
        body.setPadding(new Insets(28, 32, 24, 32));
        body.setStyle("-fx-background-color: #F1EAE7;");

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        popup.setScene(new Scene(new VBox(headerBar, scroll), 480, 260));
        popup.showAndWait();
    }

    // ── FLIGHT schedule form ──
    private void openFlightScheduleForm(Schedule existing) {
        boolean isEdit = existing != null;
        Stage popup = popupStage(isEdit ? "Update Flight Schedule" : "Add Flight Schedule");

        // Transport dropdown — FLIGHT only
        Map<Integer, String> flightMap = loadTransportsByType("FLIGHT");
        javafx.collections.ObservableList<String> flightItems = FXCollections.observableArrayList(flightMap.values());
        ComboBox<String> transBox = new ComboBox<>(flightItems);
        transBox.setPromptText("Select flight provider...");
        applyFieldStyle(transBox);

        if (isEdit) {
            String cur = flightMap.get(existing.getTransportId());
            if (cur != null) transBox.setValue(cur);
        }

        // Departure: free text
        TextField depField = tf("e.g. Tunis-Carthage Airport");

        // Arrival destination: dropdown
        Map<Long, String> dests = loadDestinations();
        ComboBox<String> arrBox = buildDestCombo(dests, "Select arrival destination...");
        if (isEdit && existing.getArrivalDestinationId() > 0) {
            String name = dests.get(existing.getArrivalDestinationId());
            if (name != null)
                arrBox.setValue(name + "  (ID: " + existing.getArrivalDestinationId() + ")");
        }

        // Departure date/time row
        int depHour = isEdit && existing.getDepartureDatetime() != null ? existing.getDepartureDatetime().getHour() : 8;
        int depMinute = isEdit && existing.getDepartureDatetime() != null ? existing.getDepartureDatetime().getMinute() : 0;
        LocalDate depDate = isEdit && existing.getDepartureDatetime() != null ? existing.getDepartureDatetime().toLocalDate() : null;
        HBox depDtRow = buildDateTimeRow(depDate, depHour, depMinute);

        // Arrival date/time row
        int arrHour = isEdit && existing.getArrivalDatetime() != null ? existing.getArrivalDatetime().getHour() : 10;
        int arrMinute = isEdit && existing.getArrivalDatetime() != null ? existing.getArrivalDatetime().getMinute() : 0;
        LocalDate arrDate = isEdit && existing.getArrivalDatetime() != null ? existing.getArrivalDatetime().toLocalDate() : null;
        HBox arrDtRow = buildDateTimeRow(arrDate, arrHour, arrMinute);

        ComboBox<String> clsBox = classCombo(isEdit ? existing.getTravelClass() : "ECONOMY");
        ComboBox<String> statBox = statusCombo(isEdit ? existing.getStatus() : "ON_TIME");
        TextField multF = tf2(isEdit ? String.valueOf(existing.getPriceMultiplier()) : "1.0");
        TextField demandF = tf2(isEdit ? String.valueOf(existing.getAiDemandScore()) : "0.0");

        GridPane g = formGrid();
        int r = 0;
        g.addRow(r++, fl("Transport (Flight)"), transBox);
        g.addRow(r++, fl("From (city/airport)"), depField);
        g.addRow(r++, fl("To (arrival dest.)"), arrBox);
        g.addRow(r++, fl("Departure Date + Time"), withNote(depDtRow, "Pick date → set HH and MM with spinners"));
        g.addRow(r++, fl("Arrival Date + Time"), withNote(arrDtRow, "Must be after departure"));
        g.addRow(r++, fl("Travel Class"), clsBox);
        g.addRow(r++, fl("Status"), statBox);
        g.addRow(r++, fl("Price Multiplier"), multF);

        Button save = btn(isEdit ? "Save Changes" : "Add Flight Schedule", BTN_TEAL);
        Button canc = btn("Cancel", BTN_DARK);
        canc.setOnAction(e -> popup.close());

        save.setOnAction(e -> {
            int transId = parseTransportId(transBox.getValue(), flightMap);
            if (transId < 0) { showAlert("Please select a flight transport."); return; }
            long arrId = parseDestId(arrBox.getValue(), dests);
            if (arrId < 0) { showAlert("Please select an arrival destination."); return; }
            LocalDateTime dep = parseDateTimeRow(depDtRow);
            LocalDateTime arr = parseDateTimeRow(arrDtRow);
            if (dep == null) { showAlert("Please select the departure date and time."); return; }
            if (arr == null) { showAlert("Please select the arrival date and time."); return; }
            LocalDateTime now = LocalDateTime.now();
            if (!dep.isAfter(now)) { showAlert("Departure date/time must be in the future."); return; }
            if (!arr.isAfter(now)) { showAlert("Arrival date/time must be in the future."); return; }
            if (!dep.isBefore(arr)) { showAlert("Departure must be before arrival."); return; }

            try {
                double mult = Double.parseDouble(multF.getText().trim());
                double ai = Double.parseDouble(demandF.getText().trim());
                Schedule s = isEdit ? existing : new Schedule();
                s.setTransportId(transId);
                s.setDepartureDestinationId(arrId);
                s.setArrivalDestinationId(arrId);
                s.setDepartureDatetime(dep);
                s.setArrivalDatetime(arr);
                s.setRentalStart(null);
                s.setRentalEnd(null);
                s.setTravelClass(clsBox.getValue());
                s.setStatus(statBox.getValue());
                s.setPriceMultiplier(mult);
                s.setAiDemandScore(ai);
                s.setDelayMinutes(isEdit ? existing.getDelayMinutes() : 0);
                if (isEdit) scheduleService.updateSchedule(s);
                else scheduleService.addSchedule(s);
                popup.close();
                showSuccess(isEdit ? "Flight schedule updated!" : "Flight schedule added!");
            } catch (NumberFormatException ex) {
                showAlert("Multiplier and AI score must be numbers.");
            }
        });

        ScrollPane scroll = scrollOf(g);
        scroll.setPrefHeight(420);
        Scene flightScene = new Scene(popupRoot("Flight Schedule", scroll, save, canc), 620, 600);
        popup.setScene(flightScene);
        popup.showAndWait();
    }

    // ── VEHICLE schedule form ──
    private void openVehicleScheduleForm(Schedule existing) {
        boolean isEdit = existing != null;
        Stage popup = popupStage(isEdit ? "Update Vehicle Schedule" : "Add Vehicle Schedule");

        // Transport dropdown — VEHICLE only
        Map<Integer, String> vehicleMap = loadTransportsByType("VEHICLE");
        javafx.collections.ObservableList<String> vehicleItems = FXCollections.observableArrayList(vehicleMap.values());
        ComboBox<String> transBox = new ComboBox<>(vehicleItems);
        transBox.setPromptText("Select vehicle provider...");
        applyFieldStyle(transBox);

        if (isEdit) {
            String cur = vehicleMap.get(existing.getTransportId());
            if (cur != null) transBox.setValue(cur);
        }

        // Location dropdown
        Map<Long, String> dests = loadDestinations();
        ComboBox<String> locationBox = buildDestCombo(dests, "Select location...");
        if (isEdit && existing.getArrivalDestinationId() > 0) {
            String name = dests.get(existing.getArrivalDestinationId());
            if (name != null)
                locationBox.setValue(name + "  (ID: " + existing.getArrivalDestinationId() + ")");
        }

        DatePicker startPicker = new DatePicker();
        startPicker.setPromptText("Rental start date");
        applyFieldStyle(startPicker);
        if (isEdit && existing.getRentalStart() != null)
            startPicker.setValue(existing.getRentalStart().toLocalDate());

        DatePicker endPicker = new DatePicker();
        endPicker.setPromptText("Rental end date");
        applyFieldStyle(endPicker);
        if (isEdit && existing.getRentalEnd() != null)
            endPicker.setValue(existing.getRentalEnd().toLocalDate());

        Label hint = new Label("Both dates must be today or in the future. Start must be before end.");
        hint.setStyle("-fx-text-fill: #888; -fx-font-size: 11px; -fx-font-style: italic;");
        hint.setWrapText(true);

        ComboBox<String> clsBox = classCombo(isEdit ? existing.getTravelClass() : "ECONOMY");
        ComboBox<String> statBox = statusCombo(isEdit ? existing.getStatus() : "ON_TIME");
        TextField multF = tf2(isEdit ? String.valueOf(existing.getPriceMultiplier()) : "1.0");
        TextField demandF = tf2(isEdit ? String.valueOf(existing.getAiDemandScore()) : "0.0");

        GridPane g = formGrid();
        int r = 0;
        g.addRow(r++, fl("Transport (Vehicle)"), transBox);
        g.addRow(r++, fl("Location"), locationBox);
        g.addRow(r++, fl("Rental Start"), startPicker);
        g.addRow(r++, fl("Rental End"), endPicker);
        g.addRow(r++, new Label(""), hint);
        g.addRow(r++, fl("Travel Class"), clsBox);
        g.addRow(r++, fl("Status"), statBox);
        g.addRow(r++, fl("Price Multiplier"), multF);

        Button save = btn(isEdit ? "Save Changes" : "Add Vehicle Schedule", BTN_TEAL);
        Button canc = btn("Cancel", BTN_DARK);
        canc.setOnAction(e -> popup.close());

        save.setOnAction(e -> {
            int transId;
            if (transBox.getValue() != null) {
                transId = parseTransportId(transBox.getValue(), vehicleMap);
            } else if (isEdit) {
                transId = existing.getTransportId();
            } else {
                transId = -1;
            }
            if (transId < 0) { showAlert("Please select a vehicle transport."); return; }

            long locId;
            if (locationBox.getValue() != null) {
                locId = parseDestId(locationBox.getValue(), dests);
            } else if (isEdit && existing.getArrivalDestinationId() > 0) {
                locId = existing.getArrivalDestinationId();
            } else {
                locId = -1;
            }
            if (locId < 0) { showAlert("Please select a location."); return; }

            LocalDate rs = startPicker.getValue();
            LocalDate re = endPicker.getValue();

            if (rs == null && isEdit && existing.getRentalStart() != null)
                rs = existing.getRentalStart().toLocalDate();
            if (re == null && isEdit && existing.getRentalEnd() != null)
                re = existing.getRentalEnd().toLocalDate();

            if (rs == null) { showAlert("Please select a rental start date."); return; }
            if (re == null) { showAlert("Please select a rental end date."); return; }

            LocalDate today = LocalDate.now();
            if (rs.isBefore(today)) { showAlert("Rental start must be today or in the future."); return; }
            if (re.isBefore(today)) { showAlert("Rental end must be today or in the future."); return; }
            if (!rs.isBefore(re)) { showAlert("Rental start must be before rental end."); return; }

            try {
                double mult = Double.parseDouble(multF.getText().trim());
                double ai = Double.parseDouble(demandF.getText().trim());
                Schedule s = isEdit ? existing : new Schedule();
                s.setTransportId(transId);
                s.setDepartureDestinationId(locId);
                s.setArrivalDestinationId(locId);
                s.setDepartureDatetime(null);
                s.setArrivalDatetime(null);
                s.setRentalStart(rs.atStartOfDay());
                s.setRentalEnd(re.atTime(23, 59));
                s.setTravelClass(clsBox.getValue());
                s.setStatus(statBox.getValue());
                s.setPriceMultiplier(mult);
                s.setAiDemandScore(ai);
                s.setDelayMinutes(isEdit ? existing.getDelayMinutes() : 0);
                if (isEdit) scheduleService.updateSchedule(s);
                else scheduleService.addSchedule(s);
                popup.close();
                showSuccess(isEdit ? "Vehicle schedule updated!" : "Vehicle schedule added!");
            } catch (NumberFormatException ex) {
                showAlert("Multiplier and AI score must be numbers.");
            }
        });

        ScrollPane scroll = scrollOf(g);
        scroll.setPrefHeight(400);
        Scene vehicleScene = new Scene(popupRoot("Vehicle Schedule", scroll, save, canc), 560, 580);
        popup.setScene(vehicleScene);
        popup.showAndWait();
    }

    // ════════════════════════════════════════
    // BOOKING TAB
    // ════════════════════════════════════════

    private void buildBookingView() {
        TableView<Bookingtrans> table = new TableView<>();
        styleTable(table);

        TableColumn<Bookingtrans, String> statusCol = col("Status", "bookingStatus", 110);
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "CONFIRMED" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "CANCELLED" -> setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    default -> setStyle("-fx-text-fill: #F06E32; -fx-font-weight: bold;");
                }
            }
        });

        table.getColumns().addAll(
                col("ID", "bookingId", 55),
                col("User", "userId", 65),
                col("Transport", "transportId", 90),
                col("Schedule", "scheduleId", 80),
                col("Seats", "totalSeats", 65),
                col("Total (EUR)", "totalPrice", 90),
                statusCol,
                col("Payment", "paymentStatus", 100),
                col("Insurance", "insuranceIncluded", 90),
                col("Pickup", "pickupAddress", 180),
                col("Drop-off", "dropoffAddress", 180));
        table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));

        Button confBtn = btn("Confirm", BTN_GREEN);
        Button canBtn = btn("Cancel", BTN_RED);
        Button detBtn = btn("View Details", BTN_TEAL);
        Button refBtn = btn("Mark Refunded", BTN_DARK);
        Button delBtn = btn("Delete", BTN_DARK);

        confBtn.setOnAction(e -> {
            Bookingtrans sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a booking row first."); return; }
            if (confirm("Confirm booking #" + sel.getBookingId() + "?")) {
                sel.setBookingStatus("CONFIRMED");
                sel.setPaymentStatus("PAID");
                bookingService.updateBookingtrans(sel);
                table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
                showSuccess("Booking confirmed.");
            }
        });

        canBtn.setOnAction(e -> {
            Bookingtrans sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a booking row first."); return; }
            if (confirm("Cancel booking #" + sel.getBookingId() + "?")) {
                sel.setBookingStatus("CANCELLED");
                TextInputDialog dlg = new TextInputDialog();
                dlg.setTitle("Cancellation Reason");
                dlg.setHeaderText("Enter reason (optional):");
                dlg.showAndWait().ifPresent(sel::setCancellationReason);
                bookingService.updateBookingtrans(sel);
                table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
                showSuccess("Booking cancelled.");
            }
        });

        refBtn.setOnAction(e -> {
            Bookingtrans sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a booking row first."); return; }
            if (confirm("Mark booking #" + sel.getBookingId() + " as refunded?")) {
                sel.setPaymentStatus("REFUNDED");
                bookingService.updateBookingtrans(sel);
                table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
                showSuccess("Payment marked REFUNDED.");
            }
        });

        detBtn.setOnAction(e -> {
            Bookingtrans sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) viewBookingDetails(sel);
            else showAlert("Select a booking row first.");
        });

        delBtn.setOnAction(e -> {
            Bookingtrans sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a booking row first."); return; }
            if (confirm("Delete booking #" + sel.getBookingId() + "?")) {
                bookingService.deleteBookingtrans(sel.getBookingId());
                table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
                showSuccess("Booking deleted.");
            }
        });

        VBox layout = new VBox(14, sectionTitle("Booking Management"),
                toolbar(confBtn, canBtn, refBtn, detBtn, delBtn), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        contentArea.getChildren().setAll(layout);
    }

    private void viewBookingDetails(Bookingtrans sel) {
        Stage popup = popupStage("Booking Details - #" + sel.getBookingId());

        Map<Integer, Transport> tMap = new HashMap<>();
        transportService.getAllTransports().forEach(t -> tMap.put(t.getTransportId(), t));
        Transport transport = tMap.get(sel.getTransportId());
        String provName = transport != null ? transport.getProviderName() : "Transport #" + sel.getTransportId();
        String transType = transport != null ? transport.getTransportType() : "N/A";
        String transModel = transport != null ? transport.getVehicleModel() : "N/A";
        boolean isVehicle = transport != null && "VEHICLE".equals(transport.getTransportType());

        GridPane grid = formGrid();
        int r = 0;

        Label transHdr = new Label("Transport");
        transHdr.setStyle("-fx-font-weight: bold; -fx-text-fill: #4FB3B5; -fx-font-size: 13px;");
        grid.add(transHdr, 0, r++, 2, 1);

        grid.addRow(r++, fl("Provider"), lbl(provName));
        grid.addRow(r++, fl("Type"), lbl(transType));
        grid.addRow(r++, fl("Model"), lbl(transModel));
        grid.addRow(r++, new Label(""), new Separator());

        Label bookHdr = new Label("Booking");
        bookHdr.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F294C; -fx-font-size: 13px;");
        grid.add(bookHdr, 0, r++, 2, 1);

        grid.addRow(r++, fl("Booking ID"), lbl(String.valueOf(sel.getBookingId())));
        grid.addRow(r++, fl("User ID"), lbl(String.valueOf(sel.getUserId())));
        grid.addRow(r++, fl("Transport ID"), lbl(String.valueOf(sel.getTransportId())));
        grid.addRow(r++, fl("Schedule ID"), lbl(sel.getScheduleId() > 0 ? "Schedule #" + sel.getScheduleId() : "Direct booking"));
        grid.addRow(r++, fl("Booking Date"), lbl(sel.getBookingDate() == null ? "N/A" : sel.getBookingDate().format(DT_FMT)));
        grid.addRow(r++, fl("Adults"), lbl(String.valueOf(sel.getAdultsCount())));
        grid.addRow(r++, fl("Children"), lbl(String.valueOf(sel.getChildrenCount())));
        grid.addRow(r++, fl("Total Seats"), lbl(String.valueOf(sel.getTotalSeats())));
        grid.addRow(r++, fl("Total Price"), lbl(String.format("EUR %.2f", sel.getTotalPrice())));
        grid.addRow(r++, fl("Status"), lbl(sel.getBookingStatus()));
        grid.addRow(r++, fl("Payment"), lbl(sel.getPaymentStatus()));
        grid.addRow(r++, fl("Insurance"), lbl(sel.isInsuranceIncluded() ? "Yes" : "No"));

        if (isVehicle) {
            grid.addRow(r++, new Label(""), new Separator());
            Label mapHdr = new Label("Pickup / Drop-off Coordinates");
            mapHdr.setStyle("-fx-font-weight: bold; -fx-text-fill: #4FB3B5; -fx-font-size: 13px;");
            grid.add(mapHdr, 0, r++, 2, 1);

            String pickupCoords = sel.getPickupLatitude() != null && sel.getPickupLongitude() != null
                    ? String.format("%.6f, %.6f", sel.getPickupLatitude(), sel.getPickupLongitude())
                    : "-";
            String dropoffCoords = sel.getDropoffLatitude() != null && sel.getDropoffLongitude() != null
                    ? String.format("%.6f, %.6f", sel.getDropoffLatitude(), sel.getDropoffLongitude())
                    : "-";

            grid.addRow(r++, fl("Pickup coords"), lbl(pickupCoords));
            grid.addRow(r++, fl("Pickup address"), lbl(sel.getPickupAddress() == null ? "-" : sel.getPickupAddress()));
            grid.addRow(r++, fl("Drop-off coords"), lbl(dropoffCoords));
            grid.addRow(r++, fl("Drop-off address"), lbl(sel.getDropoffAddress() == null ? "-" : sel.getDropoffAddress()));
        }

        Button closeBtn = btn("Close", BTN_DARK);
        closeBtn.setOnAction(e -> popup.close());

        popup.setScene(new Scene(popupRoot("Booking Details", grid, closeBtn), 500, 680));
        popup.showAndWait();
    }

    // ════════════════════════════════════════
    // SHARED UI HELPERS
    // ════════════════════════════════════════

    private <T> void styleTable(TableView<T> tv) {
        tv.setStyle("-fx-background-color: white; -fx-background-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private <T, V> TableColumn<T, V> col(String title, String prop, double width) {
        TableColumn<T, V> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private Button btn(String text, String style) {
        Button b = new Button(text);
        b.setStyle(style);
        return b;
    }

    private HBox toolbar(Button... buttons) {
        HBox box = new HBox(10, buttons);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1F294C;");
        return l;
    }

    private Stage popupStage(String title) {
        Stage s = new Stage();
        s.setTitle(title);
        s.initModality(Modality.APPLICATION_MODAL);
        if (contentArea != null && contentArea.getScene() != null)
            s.initOwner(contentArea.getScene().getWindow());
        s.setResizable(true);
        return s;
    }

    private GridPane formGrid() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(10);
        g.setPadding(new Insets(4, 8, 4, 8));
        ColumnConstraints c1 = new ColumnConstraints(165);
        ColumnConstraints c2 = new ColumnConstraints(310);
        c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2);
        g.setMinWidth(480);
        return g;
    }

    private Label fl(String text) {
        Label l = new Label(text + ":");
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F294C; -fx-font-size: 12px;");
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #333; -fx-font-size: 12px;");
        l.setWrapText(true);
        return l;
    }

    private TextField tf(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(FORM_FIELD);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private TextField tf2(String val) {
        TextField tf = new TextField(val);
        tf.setStyle(FORM_FIELD);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private <T> void applyFieldStyle(ComboBox<T> cb) {
        cb.setStyle(FORM_FIELD);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setPrefWidth(300);
        GridPane.setFillWidth(cb, true);
    }

    private void applyFieldStyle(DatePicker dp) {
        dp.setStyle(FORM_FIELD);
        dp.setMaxWidth(Double.MAX_VALUE);
    }

    private ComboBox<String> buildDestCombo(Map<Long, String> dests, String prompt) {
        javafx.collections.ObservableList<String> items = FXCollections.observableArrayList();
        dests.forEach((id, name) -> items.add(name + "  (ID: " + id + ")"));
        ComboBox<String> cb = new ComboBox<>(items);
        cb.setPromptText(prompt);
        applyFieldStyle(cb);
        return cb;
    }

    private ComboBox<String> classCombo(String val) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList("ECONOMY","PREMIUM","BUSINESS","FIRST"));
        cb.setValue(val != null ? val : "ECONOMY");
        applyFieldStyle(cb);
        return cb;
    }

    private ComboBox<String> statusCombo(String val) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList("ON_TIME","DELAYED","CANCELLED","COMPLETED","BOARDING"));
        cb.setValue(val != null ? val : "ON_TIME");
        applyFieldStyle(cb);
        return cb;
    }

    /**
     * Date picker + hour spinner + minute spinner
     */
    private HBox buildDateTimeRow(LocalDate date, int hour, int minute) {
        DatePicker dp = new DatePicker(date);
        dp.setStyle(FORM_FIELD);
        dp.setPrefWidth(148);

        // Hour spinner
        Spinner<Integer> hSpin = new Spinner<>(0, 23, hour);
        hSpin.setEditable(true);
        hSpin.setPrefWidth(72);
        hSpin.setStyle("-fx-padding: 4 6 4 6; -fx-background-radius: 7; -fx-border-color: #D0C8C3; " +
                "-fx-border-radius: 7; -fx-font-size: 13px; -fx-font-weight: bold;");
        hSpin.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) hSpin.increment(0);
        });

        // Minute spinner
        Spinner<Integer> mSpin = new Spinner<>(0, 59, minute);
        mSpin.setEditable(true);
        mSpin.setPrefWidth(72);
        mSpin.setStyle("-fx-padding: 4 6 4 6; -fx-background-radius: 7; -fx-border-color: #D0C8C3; " +
                "-fx-border-radius: 7; -fx-font-size: 13px; -fx-font-weight: bold;");
        mSpin.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) mSpin.increment(0);
        });

        Label colLabel = new Label(":");
        colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1F294C;");
        Label hLabel = new Label("HH");
        hLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        Label mLabel = new Label("MM");
        mLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        VBox hBox = new VBox(2, hLabel, hSpin);
        hBox.setAlignment(Pos.CENTER);
        VBox mBox = new VBox(2, mLabel, mSpin);
        mBox.setAlignment(Pos.CENTER);
        VBox colBox = new VBox(colLabel);
        colBox.setAlignment(Pos.BOTTOM_CENTER);
        colBox.setPadding(new Insets(0, 0, 4, 0));

        HBox row = new HBox(8, dp, hBox, colBox, mBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    @SuppressWarnings("unchecked")
    private LocalDateTime parseDateTimeRow(HBox row) {
        try {
            DatePicker dp = (DatePicker) row.getChildren().get(0);
            VBox hBox = (VBox) row.getChildren().get(1);
            VBox mBox = (VBox) row.getChildren().get(3);
            Spinner<Integer> hSpin = (Spinner<Integer>) hBox.getChildren().get(1);
            Spinner<Integer> mSpin = (Spinner<Integer>) mBox.getChildren().get(1);
            if (dp.getValue() == null) return null;
            hSpin.increment(0);
            mSpin.increment(0);
            return dp.getValue().atTime(hSpin.getValue(), mSpin.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    private VBox withNote(javafx.scene.Node node, String note) {
        Label n = new Label(note);
        n.setStyle("-fx-text-fill: #999; -fx-font-size: 10px; -fx-font-style: italic;");
        return new VBox(3, node, n);
    }

    private ScrollPane scrollOf(javafx.scene.Node content) {
        ScrollPane sc = new ScrollPane(content);
        sc.setFitToWidth(true);
        sc.setMinViewportWidth(460);
        sc.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return sc;
    }

    private VBox popupRoot(String title, javafx.scene.Node content, Button... buttons) {
        Label hdr = new Label(title);
        hdr.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox headerBar = new HBox(hdr);
        headerBar.setStyle("-fx-background-color: #1F294C; -fx-padding: 14 20 14 20;");
        headerBar.setAlignment(Pos.CENTER_LEFT);

        ScrollPane scroll = content instanceof ScrollPane ? (ScrollPane) content : scrollOf(content);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        HBox btnRow = new HBox(10, buttons);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10, 0, 0, 0));

        VBox body = new VBox(12, scroll, btnRow);
        body.setPadding(new Insets(18));
        body.setStyle("-fx-background-color: #F1EAE7;");
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox root = new VBox(headerBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);
        return root;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}