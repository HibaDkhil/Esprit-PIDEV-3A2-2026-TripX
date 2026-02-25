package tn.esprit.services;

import tn.esprit.entities.Destination;
import tn.esprit.entities.Destination.DestinationType;
import tn.esprit.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * AI-powered Destination Recommendation Service.
 * Uses a content-based filtering approach with weighted scoring across four factors:
 *   - Category match (40%)
 *   - Budget proximity (25%)
 *   - Average rating (20%)
 *   - Popularity / booking count (15%)
 */
public class RecommendationService {

    // --- Scoring Weights ---
    private static final double W_CATEGORY = 0.40;
    private static final double W_BUDGET   = 0.25;
    private static final double W_RATING   = 0.20;
    private static final double W_POPULARITY = 0.15;

    private static final double MAX_RATING = 5.0;

    private final Connection conx;
    private final DestinationService destinationService;

    public RecommendationService() {
        this.conx = MyDB.getInstance().getConx();
        this.destinationService = new DestinationService();
    }

    // ========================================================================
    // PUBLIC API
    // ========================================================================

    /**
     * Returns the top N recommended destinations based on user preferences.
     *
     * @param preferredType the category the user is interested in (nullable — if null, category weight is spread to others)
     * @param maxBudget     the user's maximum budget per person (0 = no budget constraint)
     * @param topN          number of results to return
     * @return sorted list of ScoredDestination, highest score first
     */
    public List<ScoredDestination> getRecommendations(DestinationType preferredType, double maxBudget, int topN) {
        // Step 1: Refresh popularity counts from bookings table
        refreshPopularity();

        // Step 2: Load all destinations
        List<Destination> allDestinations = destinationService.getAllDestinations();

        if (allDestinations.isEmpty()) {
            return new ArrayList<>();
        }

        // Step 3: Find max popularity for normalization
        int maxPopularity = allDestinations.stream()
                .mapToInt(d -> d.getPopularity() != null ? d.getPopularity() : 0)
                .max()
                .orElse(1);
        if (maxPopularity == 0) maxPopularity = 1; // avoid division by zero

        // Step 4: Score each destination
        List<ScoredDestination> scored = new ArrayList<>();
        for (Destination d : allDestinations) {
            double score = calculateScore(d, preferredType, maxBudget, maxPopularity);
            scored.add(new ScoredDestination(d, score));
        }

        // Step 5: Sort descending by score and return top N
        scored.sort(Comparator.comparingDouble(ScoredDestination::getScore).reversed());

        return scored.subList(0, Math.min(topN, scored.size()));
    }

    /**
     * Convenience overload — returns top 5 recommendations.
     */
    public List<ScoredDestination> getRecommendations(DestinationType preferredType, double maxBudget) {
        return getRecommendations(preferredType, maxBudget, 5);
    }

    // ========================================================================
    // SCORING ENGINE
    // ========================================================================

    /**
     * Calculates the weighted recommendation score for a single destination.
     *
     * @param d              the destination to score
     * @param preferredType  the user's preferred category
     * @param maxBudget      user's maximum budget (0 = ignored)
     * @param maxPopularity  the highest popularity value across all destinations (for normalization)
     * @return a score between 0.0 and 1.0
     */
    double calculateScore(Destination d, DestinationType preferredType, double maxBudget, int maxPopularity) {

        // --- Factor 1: Category match (0.0 or 1.0) ---
        double categoryScore = 0.0;
        if (preferredType == null) {
            categoryScore = 0.5; // neutral when no preference given
        } else if (d.getType() == preferredType) {
            categoryScore = 1.0;
        }

        // --- Factor 2: Budget proximity (0.0 to 1.0) ---
        double budgetScore = 1.0; // default: perfect match if no budget constraint
        if (maxBudget > 0 && d.getEstimatedBudget() != null && d.getEstimatedBudget() > 0) {
            double diff = Math.abs(d.getEstimatedBudget() - maxBudget);
            budgetScore = Math.max(0.0, 1.0 - (diff / maxBudget));
        }

        // --- Factor 3: Average rating normalized to 0–1 ---
        double ratingScore = 0.0;
        if (d.getAverageRating() != null && d.getAverageRating() > 0) {
            ratingScore = Math.min(1.0, d.getAverageRating() / MAX_RATING);
        }

        // --- Factor 4: Popularity normalized to 0–1 ---
        double popularityScore = 0.0;
        if (d.getPopularity() != null && d.getPopularity() > 0) {
            popularityScore = (double) d.getPopularity() / maxPopularity;
        }

        // --- Weighted sum ---
        return (W_CATEGORY * categoryScore)
             + (W_BUDGET * budgetScore)
             + (W_RATING * ratingScore)
             + (W_POPULARITY * popularityScore);
    }

    // ========================================================================
    // POPULARITY SYNC
    // ========================================================================

    /**
     * Refreshes the popularity column in the destinations table
     * by counting bookings from the bookingdes table.
     */
    public void refreshPopularity() {
        String sql = "UPDATE destinations d " +
                     "LEFT JOIN (SELECT destination_id, COUNT(*) AS cnt FROM bookingdes GROUP BY destination_id) b " +
                     "ON d.destination_id = b.destination_id " +
                     "SET d.popularity = COALESCE(b.cnt, 0)";

        try (Statement stmt = conx.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("📊 Popularity scores refreshed from booking data");
        } catch (SQLException e) {
            System.err.println("❌ Error refreshing popularity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // INNER CLASS — Scored Destination
    // ========================================================================

    /**
     * Wraps a Destination with its computed recommendation score.
     */
    public static class ScoredDestination {
        private final Destination destination;
        private final double score;

        public ScoredDestination(Destination destination, double score) {
            this.destination = destination;
            this.score = score;
        }

        public Destination getDestination() { return destination; }
        public double getScore() { return score; }

        /** Returns score as a percentage string, e.g. "87.5%" */
        public String getScorePercent() {
            return String.format("%.1f%%", score * 100);
        }

        /** Returns a human-readable match label. */
        public String getMatchLabel() {
            if (score >= 0.80) return "Excellent Match";
            if (score >= 0.60) return "Great Match";
            if (score >= 0.40) return "Good Match";
            if (score >= 0.20) return "Fair Match";
            return "Low Match";
        }

        @Override
        public String toString() {
            return String.format("%s — Score: %s (%s)", destination.getName(), getScorePercent(), getMatchLabel());
        }
    }
}
