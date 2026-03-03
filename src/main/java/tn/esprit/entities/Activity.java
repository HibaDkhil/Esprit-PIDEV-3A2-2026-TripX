package tn.esprit.entities;

public class Activity {
    
    private long activityId;  // Changed from int id_activity
    private String name;
    // Note: Final DB has many more fields (category, price, duration, etc.)

    // Constructor with ID
    public Activity(long activityId, String name) {
        this.activityId = activityId;
        this.name = name;
    }

    // Getters and Setters
    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", name='" + name + '\'' +
                '}';
    }
}
