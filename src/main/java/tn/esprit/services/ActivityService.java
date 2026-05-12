package tn.esprit.services;

import tn.esprit.entities.Activity;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService {
    private Connection conx;

    public ActivityService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean addActivity(Activity activity) {
        String sql = "INSERT INTO activities (destination_id, name, description, price, capacity, category, duration_minutes, currency, age_min, available_from, available_to, meeting_point, average_rating, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, activity.getDestinationId());
            ps.setString(2, activity.getName());
            ps.setString(3, activity.getDescription());
            ps.setDouble(4, activity.getPrice());
            ps.setInt(5, activity.getCapacity());
            ps.setString(6, activity.getCategory().name().toLowerCase());
            
            if (activity.getDurationMinutes() != null) ps.setInt(7, activity.getDurationMinutes()); else ps.setNull(7, java.sql.Types.INTEGER);
            ps.setString(8, activity.getCurrency());
            if (activity.getAgeMin() != null) ps.setInt(9, activity.getAgeMin()); else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setDate(10, activity.getAvailableFrom());
            ps.setDate(11, activity.getAvailableTo());
            ps.setString(12, activity.getMeetingPoint());
            if (activity.getAverageRating() != null) ps.setDouble(13, activity.getAverageRating()); else ps.setNull(13, java.sql.Types.DOUBLE);
            ps.setBoolean(14, activity.isActive());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) activity.setActivityId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateActivity(Activity activity) {
        String sql = "UPDATE activities SET destination_id=?, name=?, description=?, price=?, capacity=?, category=?, duration_minutes=?, currency=?, age_min=?, available_from=?, available_to=?, meeting_point=?, average_rating=?, is_active=? WHERE activity_id=?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setLong(1, activity.getDestinationId());
            ps.setString(2, activity.getName());
            ps.setString(3, activity.getDescription());
            ps.setDouble(4, activity.getPrice());
            ps.setInt(5, activity.getCapacity());
            ps.setString(6, activity.getCategory().name().toLowerCase());
            
            if (activity.getDurationMinutes() != null) ps.setInt(7, activity.getDurationMinutes()); else ps.setNull(7, java.sql.Types.INTEGER);
            ps.setString(8, activity.getCurrency());
            if (activity.getAgeMin() != null) ps.setInt(9, activity.getAgeMin()); else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setDate(10, activity.getAvailableFrom());
            ps.setDate(11, activity.getAvailableTo());
            ps.setString(12, activity.getMeetingPoint());
            if (activity.getAverageRating() != null) ps.setDouble(13, activity.getAverageRating()); else ps.setNull(13, java.sql.Types.DOUBLE);
            ps.setBoolean(14, activity.isActive());
            ps.setLong(15, activity.getActivityId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteActivity(Long id) {
        String sql = "DELETE FROM activities WHERE activity_id=?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Activity> getAllActivities() {
        List<Activity> list = new ArrayList<>();
        // Join with destinations to get destination name
        String sql = "SELECT a.*, d.name as dest_name FROM activities a " +
                     "LEFT JOIN destinations d ON a.destination_id = d.destination_id " +
                     "ORDER BY a.name";
        try (Statement stmt = conx.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Activity> getActivitiesByDestination(Long destinationId) {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name as dest_name FROM activities a " +
                     "LEFT JOIN destinations d ON a.destination_id = d.destination_id " +
                     "WHERE a.destination_id = ? ORDER BY a.name";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setLong(1, destinationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Activity> searchActivities(String keyword) {
        List<Activity> list = new ArrayList<>();
        String sql = "SELECT a.*, d.name as dest_name FROM activities a " +
                     "LEFT JOIN destinations d ON a.destination_id = d.destination_id " +
                     "WHERE a.name LIKE ? OR a.description LIKE ? OR a.category LIKE ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Activity getActivityById(Long id) {
        String sql = "SELECT a.*, d.name as dest_name FROM activities a " +
                     "LEFT JOIN destinations d ON a.destination_id = d.destination_id " +
                     "WHERE a.activity_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToActivity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity a = new Activity();
        a.setActivityId(rs.getLong("activity_id"));
        a.setDestinationId(rs.getLong("destination_id"));
        a.setDestinationName(rs.getString("dest_name"));
        a.setName(rs.getString("name"));
        a.setDescription(rs.getString("description"));
        a.setPrice(rs.getDouble("price"));
        a.setCapacity(rs.getInt("capacity"));
        
        a.setDurationMinutes(rs.getObject("duration_minutes") != null ? rs.getInt("duration_minutes") : null);
        a.setCurrency(rs.getString("currency"));
        a.setAgeMin(rs.getObject("age_min") != null ? rs.getInt("age_min") : null);
        a.setAvailableFrom(rs.getDate("available_from"));
        a.setAvailableTo(rs.getDate("available_to"));
        a.setMeetingPoint(rs.getString("meeting_point"));
        a.setAverageRating(rs.getObject("average_rating") != null ? rs.getDouble("average_rating") : null);
        a.setActive(rs.getBoolean("is_active"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        
        String cat = rs.getString("category");
        if (cat != null) {
            boolean found = false;
            for (Activity.ActivityCategory c : Activity.ActivityCategory.values()) {
                if (c.name().equalsIgnoreCase(cat)) {
                    a.setCategory(c);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Try to handle spaces or other variations if needed, or default to Other
                a.setCategory(Activity.ActivityCategory.Other);
            }
        }
        
        return a;
    }
}
