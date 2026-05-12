package tn.esprit.entities;

public class Activity {
    private Long activityId;
    private Long destinationId;
    private String destinationName; // For display purposes
    private String name;
    private String description;
    private Double price;
    private int capacity;
    private ActivityCategory category;

    // Added 8 columns (plus createdAt)
    private Integer durationMinutes = 60;
    private String currency = "USD";
    private Integer ageMin;
    private java.sql.Date availableFrom;
    private java.sql.Date availableTo;
    private String meetingPoint;
    private Double averageRating = 0.0;
    private boolean isActive = true;
    private java.sql.Timestamp createdAt;

    public enum ActivityCategory {
        Tour, Adventure, Cultural, Food, Relaxation, Nightlife, Sports, Wellness, Other
    }

    public Activity() {}

    public Activity(Long destinationId, String name, String description, Double price, int capacity, ActivityCategory category) {
        this.destinationId = destinationId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.category = category;
    }

    // Getters and Setters
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public ActivityCategory getCategory() { return category; }
    public void setCategory(ActivityCategory category) { this.category = category; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public java.sql.Date getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(java.sql.Date availableFrom) { this.availableFrom = availableFrom; }

    public java.sql.Date getAvailableTo() { return availableTo; }
    public void setAvailableTo(java.sql.Date availableTo) { this.availableTo = availableTo; }

    public String getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}