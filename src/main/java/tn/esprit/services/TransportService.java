package tn.esprit.services;

import tn.esprit.entities.Transport;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportService {

    public TransportService() {
    }

    private Connection connection() {
        return MyDB.getInstance().getConx();
    }

    private boolean hasValidCapacity(Transport t) {
        return t.getCapacity() > t.getAvailableUnits();
    }

    // Create
    public boolean addTransport(Transport t) {
        if (!hasValidCapacity(t)) {
            System.err.println("[addTransport ERROR] Capacity must be greater than available units.");
            return false;
        }
        String sql = "INSERT INTO transport (transport_type, provider_name, vehicle_model, base_price, capacity, available_units, sustainability_rating, amenities, image_url, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, t.getTransportType());
            ps.setString(2, t.getProviderName());
            ps.setString(3, t.getVehicleModel());
            ps.setDouble(4, t.getBasePrice());
            ps.setInt(5, t.getCapacity());
            ps.setInt(6, t.getAvailableUnits());
            ps.setDouble(7, t.getSustainabilityRating());
            ps.setString(8, t.getAmenities());
            ps.setString(9, t.getImageUrl());
            ps.setBoolean(10, t.isActive());
            ps.executeUpdate();
            System.out.println("Transport added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[addTransport ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Read all
    public List<Transport> getAllTransports() {
        List<Transport> list = new ArrayList<>();
        String sql = "SELECT * FROM transport";
        try (Statement st = connection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Transport t = new Transport();
                t.setTransportId(rs.getInt("transport_id"));
                t.setTransportType(rs.getString("transport_type"));
                t.setProviderName(rs.getString("provider_name"));
                t.setVehicleModel(rs.getString("vehicle_model"));
                t.setBasePrice(rs.getDouble("base_price"));
                t.setCapacity(rs.getInt("capacity"));
                t.setAvailableUnits(rs.getInt("available_units"));
                t.setSustainabilityRating(rs.getDouble("sustainability_rating"));
                t.setAmenities(rs.getString("amenities"));
                t.setImageUrl(rs.getString("image_url"));
                t.setActive(rs.getBoolean("is_active"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("[getAllTransports ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Transport> getActiveTransports() {
        List<Transport> list = new ArrayList<>();
        String sql = "SELECT * FROM transport WHERE is_active = TRUE";
        try (Statement st = connection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Transport t = new Transport();
                t.setTransportId(rs.getInt("transport_id"));
                t.setTransportType(rs.getString("transport_type"));
                t.setProviderName(rs.getString("provider_name"));
                t.setVehicleModel(rs.getString("vehicle_model"));
                t.setBasePrice(rs.getDouble("base_price"));
                t.setCapacity(rs.getInt("capacity"));
                t.setAvailableUnits(rs.getInt("available_units"));
                t.setSustainabilityRating(rs.getDouble("sustainability_rating"));
                t.setAmenities(rs.getString("amenities"));
                t.setImageUrl(rs.getString("image_url"));
                t.setActive(rs.getBoolean("is_active"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("[getActiveTransports ERROR] " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Update
    public boolean updateTransport(Transport t) {
        if (!hasValidCapacity(t)) {
            System.err.println("[updateTransport ERROR] Capacity must be greater than available units.");
            return false;
        }
        String sql = "UPDATE transport SET transport_type=?, provider_name=?, vehicle_model=?, base_price=?, capacity=?, available_units=?, sustainability_rating=?, amenities=?, image_url=?, is_active=?, updated_at=NOW() WHERE transport_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setString(1, t.getTransportType());
            ps.setString(2, t.getProviderName());
            ps.setString(3, t.getVehicleModel());
            ps.setDouble(4, t.getBasePrice());
            ps.setInt(5, t.getCapacity());
            ps.setInt(6, t.getAvailableUnits());
            ps.setDouble(7, t.getSustainabilityRating());
            ps.setString(8, t.getAmenities());
            ps.setString(9, t.getImageUrl());
            ps.setBoolean(10, t.isActive());
            ps.setInt(11, t.getTransportId());
            ps.executeUpdate();
            System.out.println("Transport updated successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[updateTransport ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete
    public boolean deleteTransport(int id) {
        String sql = "DELETE FROM transport WHERE transport_id=?";
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Transport deleted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("[deleteTransport ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
