package tn.esprit.services;

import tn.esprit.entities.Followings;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowingsService {

    private final Connection conx;

    public FollowingsService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean follow(int followerId, int followedId) {
        if (followerId == followedId) return false;
        if (isFollowing(followerId, followedId)) return true;
        String sql = "INSERT INTO followings (follower_id, followed_id, created_at) VALUES (?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE id=id";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followedId);
            int n = ps.executeUpdate();
            return n >= 1;
        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("duplicate") || e.getErrorCode() == 1062) return true;
            System.err.println("follow error [" + e.getErrorCode() + "]: " + e.getMessage());
            return false;
        }
    }

    public boolean unfollow(int followerId, int followedId) {
        if (followerId == followedId) return true;
        String sql = "DELETE FROM followings WHERE follower_id = ? AND followed_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followedId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("unfollow error: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleFollow(int followerId, int followedId) {
        if (followerId == followedId) return true;
        try {
            if (isFollowing(followerId, followedId)) return unfollow(followerId, followedId);
            return follow(followerId, followedId);
        } catch (Exception e) {
            System.err.println("toggleFollow: " + e.getMessage());
            return false;
        }
    }

    public boolean isFollowing(int followerId, int followedId) {
        String sql = "SELECT COUNT(*) FROM followings WHERE follower_id = ? AND followed_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followedId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("isFollowing error: " + e.getMessage());
            return false;
        }
    }

    public int countFollowers(int userId) {
        String sql = "SELECT COUNT(*) FROM followings WHERE followed_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("countFollowers error: " + e.getMessage());
            return 0;
        }
    }

    public int countFollowing(int userId) {
        String sql = "SELECT COUNT(*) FROM followings WHERE follower_id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("countFollowing error: " + e.getMessage());
            return 0;
        }
    }

    /** Users that follow the given user */
    public List<Integer> getFollowerIds(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT follower_id FROM followings WHERE followed_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("follower_id"));
            }
        } catch (SQLException e) {
            System.err.println("getFollowerIds error: " + e.getMessage());
        }
        return ids;
    }

    /** Users that the given user follows */
    public List<Integer> getFollowingIds(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT followed_id FROM followings WHERE follower_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("followed_id"));
            }
        } catch (SQLException e) {
            System.err.println("getFollowingIds error: " + e.getMessage());
        }
        return ids;
    }

    public List<Followings> findAll() {
        List<Followings> list = new ArrayList<>();
        String sql = "SELECT * FROM followings ORDER BY created_at DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("findAll followings error: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM followings WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("delete following error: " + e.getMessage());
            return false;
        }
    }

    private Followings mapRow(ResultSet rs) throws SQLException {
        return new Followings(
                rs.getInt("id"),
                rs.getInt("follower_id"),
                rs.getInt("followed_id"),
                rs.getTimestamp("created_at")
        );
    }
}
