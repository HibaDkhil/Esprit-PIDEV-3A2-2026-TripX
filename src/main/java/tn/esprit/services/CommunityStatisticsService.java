package tn.esprit.services;

import tn.esprit.entities.CommunityStatistics;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommunityStatisticsService {

    private final Connection conx;

    public CommunityStatisticsService() {
        conx = MyDB.getInstance().getConx();
    }

    public CommunityStatistics getByUserId(int userId) {
        String sql = "SELECT * FROM community_statistics WHERE user_id = ? LIMIT 1";
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("getByUserId stats error: " + e.getMessage());
        }
        return null;
    }

    public List<CommunityStatistics> findAll() {
        List<CommunityStatistics> list = new ArrayList<>();
        String sql = "SELECT * FROM community_statistics ORDER BY followers_count DESC";
        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("findAll stats error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Recalculate and upsert statistics for a specific user from the raw tables.
     */
    public boolean refreshForUser(int userId) {
        String sql = """
            INSERT INTO community_statistics (user_id, posts_count, comments_count, reactions_count, followers_count, updated_at)
            VALUES (?,
                (SELECT COUNT(*) FROM posts WHERE user_id = ?),
                (SELECT COUNT(*) FROM comments WHERE user_id = ?),
                (SELECT COUNT(*) FROM reactions WHERE user_id = ?),
                (SELECT COUNT(*) FROM followings WHERE followed_id = ?),
                NOW()
            )
            ON DUPLICATE KEY UPDATE
                posts_count     = VALUES(posts_count),
                comments_count  = VALUES(comments_count),
                reactions_count = VALUES(reactions_count),
                followers_count = VALUES(followers_count),
                updated_at      = VALUES(updated_at)
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("refreshForUser stats error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recalculate stats for all users.
     */
    public void refreshAll() {
        String sql = """
            INSERT INTO community_statistics (user_id, posts_count, comments_count, reactions_count, followers_count, updated_at)
            SELECT
                u.user_id,
                (SELECT COUNT(*) FROM posts      WHERE user_id = u.user_id),
                (SELECT COUNT(*) FROM comments   WHERE user_id = u.user_id),
                (SELECT COUNT(*) FROM reactions  WHERE user_id = u.user_id),
                (SELECT COUNT(*) FROM followings WHERE followed_id = u.user_id),
                NOW()
            FROM user u
            ON DUPLICATE KEY UPDATE
                posts_count     = VALUES(posts_count),
                comments_count  = VALUES(comments_count),
                reactions_count = VALUES(reactions_count),
                followers_count = VALUES(followers_count),
                updated_at      = VALUES(updated_at)
        """;
        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("refreshAll stats error: " + e.getMessage());
        }
    }

    private CommunityStatistics mapRow(ResultSet rs) throws SQLException {
        CommunityStatistics s = new CommunityStatistics();
        s.setId(rs.getInt("id"));
        s.setUserId(rs.getInt("user_id"));
        s.setPostsCount(rs.getInt("posts_count"));
        s.setCommentsCount(rs.getInt("comments_count"));
        s.setReactionsCount(rs.getInt("reactions_count"));
        s.setBadgesCount(rs.getInt("badges_count"));
        s.setFollowersCount(rs.getInt("followers_count"));
        s.setOtherStats(rs.getString("other_stats"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
