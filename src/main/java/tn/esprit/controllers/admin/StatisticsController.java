package tn.esprit.controllers.admin;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import tn.esprit.entities.*;
import tn.esprit.services.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Statistics page: app-wide summary stats, charts and graphs.
 * Pastel styling aligned with Overview page.
 */
public class StatisticsController {

    @FXML private VBox contentBox;

    private static final String STAT_LBL =
            "-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-font-family: 'Poppins','Segoe UI';";
    private static final String SEC_TITLE =
            "-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #0A4174; " +
            "-fx-font-family: 'Poppins','Segoe UI';";
    private static final String SECTION_CARD =
            "-fx-background-color: white; -fx-background-radius: 18; " +
            "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.10), 18, 0, 0, 5); -fx-padding: 26;";
    private static final String GRADIENT_LINE =
            "-fx-background-color: linear-gradient(to right, #6D83F2, #4CCCAD); " +
            "-fx-pref-height: 3; -fx-background-radius: 2; -fx-min-height: 3; -fx-max-height: 3;";

    @FXML
    public void initialize() {
        if (contentBox == null) return;
        contentBox.getChildren().clear();
        try {
            contentBox.getStylesheets().add(getClass().getResource("/css/statistics-charts.css").toExternalForm());
        } catch (Exception ignored) {}

        StatsData data = loadAllStats();

        contentBox.getChildren().addAll(
                buildPageHeader(),
                buildSummaryCards(data),
                buildSection("📊   Module comparison", buildModuleBarChart(data), false),
                buildSection("📦   Booking distribution", buildBookingPieChart(data), false),
                buildSection("💰   Revenue by source", buildRevenuePieChart(data), false),
                buildSection("💬   Community engagement", buildCommunityBarChart(data), false)
        );
    }

    private VBox buildPageHeader() {
        Label title = new Label("App Statistics");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #0A4174; " +
                       "-fx-font-family: 'Poppins','Segoe UI';");
        Label sub = new Label("Summary and performance across all TripX modules");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: #94A3B8; -fx-font-family: 'Poppins','Segoe UI';");
        VBox box = new VBox(5, title, sub);
        box.setPadding(new Insets(0, 0, 10, 0));
        return box;
    }

    private VBox buildSummaryCards(StatsData d) {
        HBox row1 = new HBox(14,
                statCard("👥", String.valueOf(d.users), "Users", "#6D83F2", "#EEF2FF"),
                statCard("🏨", String.valueOf(d.accommodations), "Properties", "#0D9488", "#E0F7F4"),
                statCard("🛏️", String.valueOf(d.rooms), "Rooms", "#6D83F2", "#EEF2FF"),
                statCard("🌍", String.valueOf(d.destinations), "Destinations", "#F59E0B", "#FEF3C7"),
                statCard("🎭", String.valueOf(d.activities), "Activities", "#10B981", "#D1FAE5"),
                statCard("🚗", String.valueOf(d.transports), "Vehicles", "#8B5CF6", "#EDE9FE")
        );
        row1.setPadding(new Insets(14, 0, 8, 0));

        HBox row2 = new HBox(14,
                statCard("🎒", String.valueOf(d.packs), "Packs", "#6D83F2", "#EEF2FF"),
                statCard("🎉", String.valueOf(d.offers), "Offers", "#F59E0B", "#FEF3C7"),
                statCard("📄", String.valueOf(d.posts), "Posts", "#0D9488", "#E0F7F4"),
                statCard("✈️", String.valueOf(d.travelStories), "Travel Stories", "#10B981", "#D1FAE5"),
                statCard("💬", String.valueOf(d.comments), "Comments", "#8B5CF6", "#EDE9FE"),
                statCard("👥", String.valueOf(d.follows), "Follows", "#6D83F2", "#EEF2FF")
        );
        row2.setPadding(new Insets(0, 0, 8, 0));

        long totalBookings = d.accBookings + d.transBookings + d.packBookings;
        HBox row3 = new HBox(14,
                statCard("📋", String.valueOf(totalBookings), "Total Bookings", "#0D9488", "#E0F7F4"),
                statCard("💰", String.format("%.0f TND", d.totalRevenue), "Total Revenue", "#8B5CF6", "#EDE9FE")
        );
        row3.setPadding(new Insets(0, 0, 0, 0));

        VBox all = new VBox(0, row1, row2, row3);
        return buildSection("📈   Key metrics", all, false);
    }

    private VBox buildSection(String title, javafx.scene.Node content, boolean withTeal) {
        Label lbl = new Label(title);
        lbl.setStyle(SEC_TITLE + (withTeal ? " -fx-font-size: 20px;" : ""));

        Region gradLine = new Region();
        gradLine.setStyle(withTeal
                ? "-fx-background-color: linear-gradient(to right, #0D9488, #6D83F2, #4CCCAD); " +
                  "-fx-pref-height: 3; -fx-background-radius: 2; -fx-min-height: 3; -fx-max-height: 3;"
                : GRADIENT_LINE);
        VBox.setMargin(gradLine, new Insets(5, 0, 0, 0));

        VBox section = new VBox(0, lbl, gradLine, content);
        section.setStyle(withTeal
                ? "-fx-background-color: white; -fx-background-radius: 18; " +
                  "-fx-effect: dropshadow(gaussian, rgba(13,148,136,0.14), 22, 0, 0, 6); -fx-padding: 26; " +
                  "-fx-border-color: rgba(13,148,136,0.12); -fx-border-radius: 18; -fx-border-width: 1;"
                : SECTION_CARD);
        return section;
    }

    private VBox statCard(String icon, String value, String label, String accentColor, String bgColor) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 24px;");

        StackPane iconCircle = new StackPane(iconLbl);
        iconCircle.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 14; " +
                "-fx-min-width: 50; -fx-min-height: 50; -fx-max-width: 50; -fx-max-height: 50;");
        iconCircle.setAlignment(Pos.CENTER);

        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + "; " +
                        "-fx-font-family: 'Poppins','Segoe UI';");

        Label nameLbl = new Label(label);
        nameLbl.setStyle(STAT_LBL);
        nameLbl.setWrapText(true);

        VBox card = new VBox(8, iconCircle, valLbl, nameLbl);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.09), 10, 0, 0, 3); " +
                "-fx-padding: 18; -fx-cursor: default;");
        card.setPrefWidth(155);
        card.setMinWidth(140);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private javafx.scene.Node buildModuleBarChart(StatsData d) {
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("");
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");
        chart.getXAxis().setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 12px;");
        chart.getYAxis().setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 12px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Users", d.users));
        series.getData().add(new XYChart.Data<>("Properties", d.accommodations));
        series.getData().add(new XYChart.Data<>("Destinations", d.destinations));
        series.getData().add(new XYChart.Data<>("Activities", d.activities));
        series.getData().add(new XYChart.Data<>("Vehicles", d.transports));
        series.getData().add(new XYChart.Data<>("Packs", d.packs));
        series.getData().add(new XYChart.Data<>("Posts", d.posts));
        series.getData().add(new XYChart.Data<>("Stories", d.travelStories));
        chart.getData().add(series);

        chart.setPrefHeight(320);
        chart.setCategoryGap(12);

        VBox wrap = new VBox(chart);
        wrap.setPadding(new Insets(20, 0, 0, 0));
        return wrap;
    }

    private javafx.scene.Node buildBookingPieChart(StatsData d) {
        long total = d.accBookings + d.transBookings + d.packBookings;
        if (total == 0) total = 1;
        PieChart chart = new PieChart(javafx.collections.FXCollections.observableArrayList(
                new PieChart.Data("Accommodation", d.accBookings),
                new PieChart.Data("Transport", d.transBookings),
                new PieChart.Data("Packs", d.packBookings)
        ));
        chart.setTitle("");
        chart.setLegendVisible(true);
        chart.setPrefHeight(280);
        chart.setStyle("-fx-background-color: transparent;");
        VBox wrap = new VBox(chart);
        wrap.setPadding(new Insets(20, 0, 0, 0));
        return wrap;
    }

    private javafx.scene.Node buildRevenuePieChart(StatsData d) {
        double total = d.accRevenue + d.transRevenue + d.packRevenue;
        if (total <= 0) total = 1;
        PieChart chart = new PieChart(javafx.collections.FXCollections.observableArrayList(
                new PieChart.Data("Accommodation", d.accRevenue),
                new PieChart.Data("Transport", d.transRevenue),
                new PieChart.Data("Packs", d.packRevenue)
        ));
        chart.setTitle("");
        chart.setLegendVisible(true);
        chart.setPrefHeight(280);
        chart.setStyle("-fx-background-color: transparent;");
        VBox wrap = new VBox(chart);
        wrap.setPadding(new Insets(20, 0, 0, 0));
        return wrap;
    }

    private javafx.scene.Node buildCommunityBarChart(StatsData d) {
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("");
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: transparent;");
        chart.getXAxis().setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 12px;");
        chart.getYAxis().setStyle("-fx-tick-label-fill: #475569; -fx-font-size: 12px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Posts", d.posts));
        series.getData().add(new XYChart.Data<>("Travel Stories", d.travelStories));
        series.getData().add(new XYChart.Data<>("Comments", d.comments));
        series.getData().add(new XYChart.Data<>("Follows", d.follows));
        chart.getData().add(series);

        chart.setPrefHeight(280);
        chart.setCategoryGap(16);

        VBox wrap = new VBox(chart);
        wrap.setPadding(new Insets(20, 0, 0, 0));
        return wrap;
    }

    private StatsData loadAllStats() {
        StatsData d = new StatsData();

        try { d.users = new UserService().getAllUsers().size(); } catch (Exception ignored) {}
        try { d.accommodations = new AccommodationService().getAllAccommodations().size(); } catch (Exception ignored) {}
        try { d.rooms = new RoomService().getAll().size(); } catch (Exception ignored) {}
        try { d.destinations = new DestinationService().getAllDestinations().size(); } catch (Exception ignored) {}
        try { d.activities = new ActivityService().getAllActivities().size(); } catch (Exception ignored) {}
        try { d.transports = new TransportService().getAllTransports().size(); } catch (Exception ignored) {}

        try {
            List<Pack> packs = new PackService().getActivePacks();
            d.packs = packs != null ? packs.size() : 0;
        } catch (SQLException ignored) {}
        try {
            List<Offer> offers = new OfferService().getActiveOffers();
            d.offers = offers != null ? offers.size() : 0;
        } catch (SQLException ignored) {}

        try { d.posts = new PostService().findAll().size(); } catch (Exception ignored) {}
        try { d.travelStories = new TravelStoryService().findAll().size(); } catch (Exception ignored) {}
        try { d.comments = new CommentService().findAll().size(); } catch (Exception ignored) {}
        try { d.follows = new FollowingsService().findAll().size(); } catch (Exception ignored) {}

        try {
            List<AccommodationBooking> accBk = new AccommodationBookingService().getAllAccommodationBookings();
            d.accBookings = accBk.size();
            d.accRevenue = accBk.stream()
                    .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                    .mapToDouble(AccommodationBooking::getTotalPrice).sum();
        } catch (Exception ignored) {}

        try {
            List<Bookingtrans> transBk = new BookingtransService().getAllBookings();
            d.transBookings = transBk.size();
            d.transRevenue = transBk.stream()
                    .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()))
                    .mapToDouble(Bookingtrans::getTotalPrice).sum();
        } catch (Exception ignored) {}

        try {
            List<PacksBooking> packBk = new PackBookingService().afficherList();
            d.packBookings = packBk.size();
            d.packRevenue = packBk.stream()
                    .filter(b -> b.getStatus() == PacksBooking.Status.CONFIRMED)
                    .mapToDouble(b -> b.getFinalPrice() != null ? b.getFinalPrice().doubleValue() : 0)
                    .sum();
        } catch (SQLException ignored) {}

        d.totalRevenue = d.accRevenue + d.transRevenue + d.packRevenue;
        return d;
    }

    private static class StatsData {
        int users, accommodations, rooms, destinations, activities, transports;
        int packs, offers, posts, travelStories, comments, follows;
        int accBookings, transBookings, packBookings;
        double accRevenue, transRevenue, packRevenue, totalRevenue;
    }
}
