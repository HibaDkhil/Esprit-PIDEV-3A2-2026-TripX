package tn.esprit.services;

import tn.esprit.entities.Report;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsService {

    private final Connection conx;

    public ReportsService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean create(Report r) {
        String sql = """
            INSERT INTO reports (reported_by_id, reported_user_id, reported_post_id,
                reported_comment_id, report_type, reason, created_at, status)
            VALUES (?, ?, ?, ?, ?, ?, NOW(), 'pending')
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, r.getReportedById());
            setNullableInt(ps, 2, r.getReportedUserId());
            setNullableInt(ps, 3, r.getReportedPostId());
            setNullableInt(ps, 4, r.getReportedCommentId());
            ps.setString(5, r.getReportType());
            ps.setString(6, r.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("create report error: " + e.getMessage());
            return false;
        }
    }

    public List<Report> findAll() {
        return query("SELECT * FROM reports ORDER BY created_at DESC", null);
    }

    public List<Report> findPending() {
        return query("SELECT * FROM reports WHERE status = 'pending' ORDER BY created_at DESC", null);
    }

    public boolean updateStatus(int reportId, String status) {
        String sql = "UPDATE reports SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateStatus report error: " + e.getMessage());
            return false;
        }
    }

    public boolean resolve(int reportId) { return updateStatus(reportId, "resolved"); }
    public boolean reject(int reportId)  { return updateStatus(reportId, "rejected"); }

    public boolean delete(int reportId) {
        String sql = "DELETE FROM reports WHERE id = ?";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("delete report error: " + e.getMessage());
            return false;
        }
    }

    public int countPending() {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = 'pending'";
        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("countPending reports error: " + e.getMessage());
            return 0;
        }
    }

    private List<Report> query(String sql, Object param) {
        List<Report> list = new ArrayList<>();
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            if (param != null) ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("query reports error: " + e.getMessage());
        }
        return list;
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, val);
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        return new Report(
                rs.getInt("id"),
                rs.getInt("reported_by_id"),
                (Integer) rs.getObject("reported_user_id"),
                (Integer) rs.getObject("reported_post_id"),
                (Integer) rs.getObject("reported_comment_id"),
                rs.getString("report_type"),
                rs.getString("reason"),
                rs.getTimestamp("created_at"),
                rs.getString("status")
        );
    }
}
