package tn.esprit.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * UserPreferences entity — mirrors the Symfony Preference entity exactly.
 * Table: userpreferences
 * Columns: preference_id, user_id, budget_min_per_night, budget_max_per_night,
 *          priorities, location_preferences, accommodation_types,
 *          style_preferences, dietary_restrictions, preferred_climate,
 *          travel_pace, group_type, accessibility_needs,
 *          created_at, updated_at
 *
 * travel_pace values: 'Relaxed', 'Moderate', 'Fast-paced'  (Symfony default: 'Moderate')
 * group_type values:  'Solo', 'Couple', 'Family', 'Friends', 'Business'
 */
public class UserPreferences {

    /** column: preference_id  (PK, auto-increment) */
    private int preferenceId;

    /** column: user_id */
    private int userId;

    /** column: budget_min_per_night  DECIMAL(10,2) nullable */
    private BigDecimal budgetMinPerNight;

    /** column: budget_max_per_night  DECIMAL(10,2) nullable */
    private BigDecimal budgetMaxPerNight;

    /** column: priorities  TEXT nullable — stored as JSON or CSV */
    private String priorities;

    /** column: location_preferences  TEXT nullable */
    private String locationPreferences;

    /** column: accommodation_types  TEXT nullable */
    private String accommodationTypes;

    /** column: style_preferences  TEXT nullable */
    private String stylePreferences;

    /** column: dietary_restrictions  TEXT nullable */
    private String dietaryRestrictions;

    /** column: preferred_climate  TEXT nullable */
    private String preferredClimate;

    /** column: travel_pace  VARCHAR(30) nullable, default 'Moderate' */
    private String travelPace;

    /** column: group_type  VARCHAR(30) nullable */
    private String groupType;

    /** column: accessibility_needs  TINYINT(1) nullable, default 0 */
    private boolean accessibilityNeeds;

    /** column: created_at  DATETIME */
    private Date createdAt;

    /** column: updated_at  DATETIME nullable */
    private Date updatedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public UserPreferences() {
    }

    public UserPreferences(int userId, BigDecimal budgetMinPerNight, BigDecimal budgetMaxPerNight,
                           String priorities, String locationPreferences, String accommodationTypes,
                           String stylePreferences, String dietaryRestrictions, String preferredClimate,
                           String travelPace, String groupType, boolean accessibilityNeeds) {
        this.userId              = userId;
        this.budgetMinPerNight   = budgetMinPerNight;
        this.budgetMaxPerNight   = budgetMaxPerNight;
        this.priorities          = priorities;
        this.locationPreferences = locationPreferences;
        this.accommodationTypes  = accommodationTypes;
        this.stylePreferences    = stylePreferences;
        this.dietaryRestrictions = dietaryRestrictions;
        this.preferredClimate    = preferredClimate;
        this.travelPace          = travelPace;
        this.groupType           = groupType;
        this.accessibilityNeeds  = accessibilityNeeds;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int getPreferenceId() { return preferenceId; }
    public void setPreferenceId(int preferenceId) { this.preferenceId = preferenceId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBudgetMinPerNight() { return budgetMinPerNight; }
    public void setBudgetMinPerNight(BigDecimal budgetMinPerNight) {
        this.budgetMinPerNight = budgetMinPerNight;
    }

    public BigDecimal getBudgetMaxPerNight() { return budgetMaxPerNight; }
    public void setBudgetMaxPerNight(BigDecimal budgetMaxPerNight) {
        this.budgetMaxPerNight = budgetMaxPerNight;
    }

    public String getPriorities() { return priorities; }
    public void setPriorities(String priorities) { this.priorities = priorities; }

    public String getLocationPreferences() { return locationPreferences; }
    public void setLocationPreferences(String locationPreferences) {
        this.locationPreferences = locationPreferences;
    }

    public String getAccommodationTypes() { return accommodationTypes; }
    public void setAccommodationTypes(String accommodationTypes) {
        this.accommodationTypes = accommodationTypes;
    }

    public String getStylePreferences() { return stylePreferences; }
    public void setStylePreferences(String stylePreferences) {
        this.stylePreferences = stylePreferences;
    }

    public String getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public String getPreferredClimate() { return preferredClimate; }
    public void setPreferredClimate(String preferredClimate) {
        this.preferredClimate = preferredClimate;
    }

    public String getTravelPace() { return travelPace; }
    public void setTravelPace(String travelPace) { this.travelPace = travelPace; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

    public boolean isAccessibilityNeeds() { return accessibilityNeeds; }
    public void setAccessibilityNeeds(boolean accessibilityNeeds) {
        this.accessibilityNeeds = accessibilityNeeds;
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "preferenceId=" + preferenceId +
                ", userId=" + userId +
                ", priorities='" + priorities + '\'' +
                ", preferredClimate='" + preferredClimate + '\'' +
                ", travelPace='" + travelPace + '\'' +
                ", groupType='" + groupType + '\'' +
                '}';
    }
}
