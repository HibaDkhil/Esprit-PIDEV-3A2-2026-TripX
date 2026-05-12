package tn.esprit.services;

import tn.esprit.entities.DestinationTrans;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinationTransService {

    private Connection connection() {
        return MyDB.getInstance().getConx();
    }

    public List<DestinationTrans> getAllDestinations() {
        List<DestinationTrans> destinations = new ArrayList<>();
        String sql = "SELECT * FROM destination_trans ORDER BY destination_id DESC";
        try (Statement st = connection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                destinations.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[getAllDestinationTrans ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return destinations;
    }

    public boolean addDestination(DestinationTrans d) {
        String sql = "INSERT INTO destination_trans (name, type, country, city, best_season, description, timezone, average_rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            bindEditableFields(ps, d);
            ps.executeUpdate();
            System.out.println("Destination added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[addDestinationTrans ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDestination(DestinationTrans d) {
        String sql = "UPDATE destination_trans SET name=?, type=?, country=?, city=?, best_season=?, description=?, timezone=?, average_rating=? WHERE destination_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            bindEditableFields(ps, d);
            ps.setInt(9, d.getDestinationId());
            ps.executeUpdate();
            System.out.println("Destination updated successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[updateDestinationTrans ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDestination(int id) {
        String sql = "DELETE FROM destination_trans WHERE destination_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Destination deleted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[deleteDestinationTrans ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void bindEditableFields(PreparedStatement ps, DestinationTrans d) throws SQLException {
        ps.setString(1, d.getName());
        ps.setString(2, d.getType());
        ps.setString(3, d.getCountry());
        setNullableString(ps, 4, d.getCity());
        ps.setString(5, d.getBestSeason());
        setNullableString(ps, 6, d.getDescription());
        ps.setString(7, d.getTimezone() == null || d.getTimezone().isBlank() ? "UTC" : d.getTimezone());
        if (d.getAverageRating() == null) ps.setDouble(8, 0.0);
        else ps.setDouble(8, d.getAverageRating());
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) ps.setNull(index, Types.VARCHAR);
        else ps.setString(index, value);
    }

    private DestinationTrans mapRow(ResultSet rs) throws SQLException {
        DestinationTrans d = new DestinationTrans();
        d.setDestinationId(rs.getInt("destination_id"));
        d.setName(rs.getString("name"));
        d.setType(rs.getString("type"));
        d.setCountry(rs.getString("country"));
        d.setCity(rs.getString("city"));
        d.setBestSeason(rs.getString("best_season"));
        d.setDescription(rs.getString("description"));
        d.setTimezone(rs.getString("timezone"));
        double rating = rs.getDouble("average_rating");
        d.setAverageRating(rs.wasNull() ? null : rating);
        Timestamp created = rs.getTimestamp("created_at");
        d.setCreatedAt(created == null ? null : created.toLocalDateTime());
        return d;
    }
}
