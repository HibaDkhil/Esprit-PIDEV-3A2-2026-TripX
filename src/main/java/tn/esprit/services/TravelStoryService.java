package tn.esprit.services;

import tn.esprit.entities.TravelStory;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TravelStoryService {

    private final Connection conx;

    public TravelStoryService() {
        conx = MyDB.getInstance().getConx();
    }

    public boolean create(TravelStory story) {
        String sql = "INSERT INTO travel_story (" +
                "user_id, destination_id, title, summary, start_date, end_date, travel_type, travel_style, " +
                "overall_rating, would_recommend, would_go_again, tips, currency, total_budget, " +
                "budget_json, tags_json, must_visit_json, must_do_json, must_try_json, favorite_places_json, " +
                "destination, cover_image_url, image_urls_json, created_at" +
                ") VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()" +
                ")";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            int i = 1;

            ps.setInt(i++, story.getUserId());

            if (story.getDestinationId() == null) ps.setNull(i++, Types.BIGINT);
            else ps.setLong(i++, story.getDestinationId());

            ps.setString(i++, story.getTitle());
            ps.setString(i++, story.getSummary());

            if (story.getStartDate() == null) ps.setNull(i++, Types.DATE);
            else ps.setDate(i++, new java.sql.Date(story.getStartDate().getTime()));

            if (story.getEndDate() == null) ps.setNull(i++, Types.DATE);
            else ps.setDate(i++, new java.sql.Date(story.getEndDate().getTime()));

            ps.setString(i++, story.getTravelType());
            ps.setString(i++, story.getTravelStyle());

            if (story.getOverallRating() == null) ps.setNull(i++, Types.TINYINT);
            else ps.setInt(i++, story.getOverallRating());

            ps.setBoolean(i++, story.isWouldRecommend());
            ps.setBoolean(i++, story.isWouldGoAgain());

            ps.setString(i++, story.getTips());

            String currency = story.getCurrency();
            if (currency == null || currency.trim().isEmpty()) currency = "TND";
            ps.setString(i++, currency);

            if (story.getTotalBudget() == null) ps.setNull(i++, Types.DECIMAL);
            else ps.setDouble(i++, story.getTotalBudget());

            ps.setString(i++, story.getBudgetJson());
            ps.setString(i++, story.getTagsJson());
            ps.setString(i++, story.getMustVisitJson());
            ps.setString(i++, story.getMustDoJson());
            ps.setString(i++, story.getMustTryJson());
            ps.setString(i++, story.getFavoritePlacesJson());

            // destination text fallback (DB column is "destination")
            if (story.getDestinationText() == null || story.getDestinationText().trim().isEmpty())
                ps.setNull(i++, Types.VARCHAR);
            else
                ps.setString(i++, story.getDestinationText().trim());

            ps.setString(i++, story.getCoverImageUrl());
            ps.setString(i++, story.getImageUrlsJson());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating travel story: " + e.getMessage());
            return false;
        }
    }

    public List<TravelStory> findAll() {
        List<TravelStory> list = new ArrayList<>();
        String sql = "SELECT * FROM travel_story ORDER BY created_at DESC";

        try (PreparedStatement ps = conx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("Error finding travel stories: " + e.getMessage());
        }
        return list;
    }

    public List<TravelStory> findByUserId(int userId) {
        List<TravelStory> list = new ArrayList<>();
        String sql = "SELECT * FROM travel_story WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding travel stories by user: " + e.getMessage());
        }
        return list;
    }

    public List<TravelStory> search(String keyword) {
        List<TravelStory> list = new ArrayList<>();
        String sql = "SELECT * FROM travel_story " +
                "WHERE title LIKE ? OR destination LIKE ? OR summary LIKE ? OR tips LIKE ? " +
                "ORDER BY created_at DESC";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching travel stories: " + e.getMessage());
        }
        return list;
    }

    public boolean update(TravelStory story) {
        String sql = "UPDATE travel_story SET " +
                "destination_id=?, title=?, summary=?, start_date=?, end_date=?, travel_type=?, travel_style=?, " +
                "overall_rating=?, would_recommend=?, would_go_again=?, tips=?, currency=?, total_budget=?, " +
                "budget_json=?, tags_json=?, must_visit_json=?, must_do_json=?, must_try_json=?, favorite_places_json=?, " +
                "destination=?, cover_image_url=?, image_urls_json=?, updated_at=NOW() " +
                "WHERE travel_story_id=?";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            int i = 1;

            if (story.getDestinationId() == null) ps.setNull(i++, Types.BIGINT);
            else ps.setLong(i++, story.getDestinationId());

            ps.setString(i++, story.getTitle());
            ps.setString(i++, story.getSummary());

            if (story.getStartDate() == null) ps.setNull(i++, Types.DATE);
            else ps.setDate(i++, new java.sql.Date(story.getStartDate().getTime()));

            if (story.getEndDate() == null) ps.setNull(i++, Types.DATE);
            else ps.setDate(i++, new java.sql.Date(story.getEndDate().getTime()));

            ps.setString(i++, story.getTravelType());
            ps.setString(i++, story.getTravelStyle());

            if (story.getOverallRating() == null) ps.setNull(i++, Types.TINYINT);
            else ps.setInt(i++, story.getOverallRating());

            ps.setBoolean(i++, story.isWouldRecommend());
            ps.setBoolean(i++, story.isWouldGoAgain());

            ps.setString(i++, story.getTips());

            String currency = story.getCurrency();
            if (currency == null || currency.trim().isEmpty()) currency = "TND";
            ps.setString(i++, currency);

            if (story.getTotalBudget() == null) ps.setNull(i++, Types.DECIMAL);
            else ps.setDouble(i++, story.getTotalBudget());

            ps.setString(i++, story.getBudgetJson());
            ps.setString(i++, story.getTagsJson());
            ps.setString(i++, story.getMustVisitJson());
            ps.setString(i++, story.getMustDoJson());
            ps.setString(i++, story.getMustTryJson());
            ps.setString(i++, story.getFavoritePlacesJson());

            if (story.getDestinationText() == null || story.getDestinationText().trim().isEmpty())
                ps.setNull(i++, Types.VARCHAR);
            else
                ps.setString(i++, story.getDestinationText().trim());

            ps.setString(i++, story.getCoverImageUrl());
            ps.setString(i++, story.getImageUrlsJson());

            ps.setInt(i++, story.getTravelStoryId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating travel story: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int travelStoryId) {
        String sql = "DELETE FROM travel_story WHERE travel_story_id = ?";

        try (PreparedStatement ps = conx.prepareStatement(sql)) {
            ps.setInt(1, travelStoryId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting travel story: " + e.getMessage());
            return false;
        }
    }

    private TravelStory mapRow(ResultSet rs) throws SQLException {
        TravelStory s = new TravelStory();

        s.setTravelStoryId(rs.getInt("travel_story_id"));
        s.setUserId(rs.getInt("user_id"));

        Object destObj = rs.getObject("destination_id");
        s.setDestinationId(destObj == null ? null : rs.getLong("destination_id"));

        s.setTitle(rs.getString("title"));
        s.setSummary(rs.getString("summary"));

        s.setStartDate(rs.getDate("start_date"));
        s.setEndDate(rs.getDate("end_date"));

        s.setTravelType(rs.getString("travel_type"));
        s.setTravelStyle(rs.getString("travel_style"));

        Object ratingObj = rs.getObject("overall_rating");
        s.setOverallRating(ratingObj == null ? null : rs.getInt("overall_rating"));

        s.setWouldRecommend(rs.getBoolean("would_recommend"));
        s.setWouldGoAgain(rs.getBoolean("would_go_again"));

        s.setTips(rs.getString("tips"));

        s.setCurrency(rs.getString("currency"));

        Object totalBudgetObj = rs.getObject("total_budget");
        s.setTotalBudget(totalBudgetObj == null ? null : rs.getDouble("total_budget"));

        s.setBudgetJson(rs.getString("budget_json"));
        s.setTagsJson(rs.getString("tags_json"));
        s.setMustVisitJson(rs.getString("must_visit_json"));
        s.setMustDoJson(rs.getString("must_do_json"));
        s.setMustTryJson(rs.getString("must_try_json"));
        s.setFavoritePlacesJson(rs.getString("favorite_places_json"));

        s.setDestinationText(rs.getString("destination"));

        s.setCoverImageUrl(rs.getString("cover_image_url"));
        s.setImageUrlsJson(rs.getString("image_urls_json"));

        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));

        return s;
    }
}