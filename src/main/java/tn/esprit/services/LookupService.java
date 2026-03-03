package tn.esprit.services;

import tn.esprit.entities.Accommodation;
import tn.esprit.entities.Activity;
import tn.esprit.entities.Destination;
import tn.esprit.entities.Transport;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for read-only lookup tables (managed by other modules)
 */
public class LookupService {

    private Connection conx;

    public LookupService() {
        conx = MyDatabase.getInstance().getConx();
    }

    // ============ DESTINATIONS ============
    
    public List<Destination> getAllDestinations() throws SQLException {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT destination_id, name FROM destinations";  // Updated column name
        
        try (Statement st = conx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                destinations.add(new Destination(
                    rs.getLong("destination_id"),  // Updated from id_destination
                    rs.getString("name")
                ));
            }
        }
        
        return destinations;
    }

    public Destination getDestinationById(long id) throws SQLException {
        String query = "SELECT destination_id, name FROM destinations WHERE destination_id = ?";
        
        try (PreparedStatement pst = conx.prepareStatement(query)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Destination(
                        rs.getLong("destination_id"),
                        rs.getString("name")
                    );
                }
            }
        }
        
        return null;
    }

    // ============ ACTIVITIES ============
    
    public List<Activity> getAllActivities() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT activity_id, name FROM activities";  // Updated column name
        
        try (Statement st = conx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                activities.add(new Activity(
                    rs.getLong("activity_id"),  // Updated from id_activity
                    rs.getString("name")
                ));
            }
        }
        
        return activities;
    }

    public Activity getActivityById(long id) throws SQLException {
        String query = "SELECT activity_id, name FROM activities WHERE activity_id = ?";
        
        try (PreparedStatement pst = conx.prepareStatement(query)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Activity(
                        rs.getLong("activity_id"),
                        rs.getString("name")
                    );
                }
            }
        }
        
        return null;
    }

    // ============ ACCOMMODATIONS ============
    
    public List<Accommodation> getAllAccommodations() throws SQLException {
        List<Accommodation> accommodations = new ArrayList<>();
        String query = "SELECT id, name FROM accommodation";  // Updated table and column names
        
        try (Statement st = conx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                accommodations.add(new Accommodation(
                    rs.getInt("id"),  // Updated from id_accommodation
                    rs.getString("name")
                ));
            }
        }
        
        return accommodations;
    }

    public Accommodation getAccommodationById(int id) throws SQLException {
        String query = "SELECT id, name FROM accommodation WHERE id = ?";
        
        try (PreparedStatement pst = conx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Accommodation(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                }
            }
        }
        
        return null;
    }

    // ============ TRANSPORT ============
    
    public List<Transport> getAllTransport() throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String query = "SELECT transport_id, transport_type FROM transport";  // Updated column name
        
        try (Statement st = conx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                transports.add(new Transport(
                    rs.getInt("transport_id"),  // Updated from id_transport
                    rs.getString("transport_type")
                ));
            }
        }
        
        return transports;
    }

    public Transport getTransportById(int id) throws SQLException {
        String query = "SELECT transport_id, transport_type FROM transport WHERE transport_id = ?";
        
        try (PreparedStatement pst = conx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Transport(
                        rs.getInt("transport_id"),
                        rs.getString("transport_type")
                    );
                }
            }
        }
        
        return null;
    }
}
