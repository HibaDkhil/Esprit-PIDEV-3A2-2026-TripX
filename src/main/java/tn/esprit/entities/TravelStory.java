package tn.esprit.entities;

import java.util.Date;

public class TravelStory {

    private int travelStoryId;
    private int userId;

    // Integration-ready link (nullable)
    private Long destinationId;              // destination_id
    private String destinationText;          // destination (text fallback)

    // Core
    private String title;
    private String summary;
    private String tips;

    // Dates
    private Date startDate;                  // start_date
    private Date endDate;                    // end_date

    // Trip meta
    private String travelType;               // travel_type
    private String travelStyle;              // travel_style

    // Ratings / flags
    private Integer overallRating;           // overall_rating (nullable)
    private boolean wouldRecommend = true;   // would_recommend
    private boolean wouldGoAgain = false;    // would_go_again

    // Budget
    private String currency = "TND";         // currency
    private Double totalBudget;              // total_budget (nullable)
    private String budgetJson;               // budget_json

    // Lists (JSON strings)
    private String tagsJson;                 // tags_json
    private String mustVisitJson;            // must_visit_json
    private String mustDoJson;               // must_do_json
    private String mustTryJson;              // must_try_json
    private String favoritePlacesJson;       // favorite_places_json

    // Images
    private String coverImageUrl;            // cover_image_url
    private String imageUrlsJson;            // image_urls_json

    private Date createdAt;                  // created_at
    private Date updatedAt;                  // updated_at

    public TravelStory() {}

    // ✅ Minimal constructor updated: removed content, keep summary
    public TravelStory(int userId, String title, String summary, String destinationText) {
        this.userId = userId;
        this.title = title;
        this.summary = summary;
        this.destinationText = destinationText;
    }

    public int getTravelStoryId() { return travelStoryId; }
    public void setTravelStoryId(int travelStoryId) { this.travelStoryId = travelStoryId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }

    public String getDestinationText() { return destinationText; }
    public void setDestinationText(String destinationText) { this.destinationText = destinationText; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getTravelType() { return travelType; }
    public void setTravelType(String travelType) { this.travelType = travelType; }

    public String getTravelStyle() { return travelStyle; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }

    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }

    public boolean isWouldRecommend() { return wouldRecommend; }
    public void setWouldRecommend(boolean wouldRecommend) { this.wouldRecommend = wouldRecommend; }

    public boolean isWouldGoAgain() { return wouldGoAgain; }
    public void setWouldGoAgain(boolean wouldGoAgain) { this.wouldGoAgain = wouldGoAgain; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public String getBudgetJson() { return budgetJson; }
    public void setBudgetJson(String budgetJson) { this.budgetJson = budgetJson; }

    public String getTagsJson() { return tagsJson; }
    public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }

    public String getMustVisitJson() { return mustVisitJson; }
    public void setMustVisitJson(String mustVisitJson) { this.mustVisitJson = mustVisitJson; }

    public String getMustDoJson() { return mustDoJson; }
    public void setMustDoJson(String mustDoJson) { this.mustDoJson = mustDoJson; }

    public String getMustTryJson() { return mustTryJson; }
    public void setMustTryJson(String mustTryJson) { this.mustTryJson = mustTryJson; }

    public String getFavoritePlacesJson() { return favoritePlacesJson; }
    public void setFavoritePlacesJson(String favoritePlacesJson) { this.favoritePlacesJson = favoritePlacesJson; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getImageUrlsJson() { return imageUrlsJson; }
    public void setImageUrlsJson(String imageUrlsJson) { this.imageUrlsJson = imageUrlsJson; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}