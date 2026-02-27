package tn.esprit.entities;

import java.time.LocalDateTime;

public class UserActivity {
    private int logId;
    private int userId;
    private String activityType; // 'CLICK', 'VISIT', 'SEARCH', 'STAY'
    private Long targetId;
    private String targetType; // 'DESTINATION', 'ACTIVITY', 'PAGE', 'FEATURE'
    private LocalDateTime timestamp;

    public UserActivity() {}

    public UserActivity(int userId, String activityType) {
        this.userId = userId;
        this.activityType = activityType;
    }

    public UserActivity(int userId, String activityType, Long targetId, String targetType) {
        this.userId = userId;
        this.activityType = activityType;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
