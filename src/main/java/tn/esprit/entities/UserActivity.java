package tn.esprit.entities;

import java.time.LocalDateTime;

/**
 * UserActivity entity — mirrors the Symfony UserActivityLog entity exactly.
 * Table: user_activity_log
 * Columns: id (PK), user_id, activity_type, target_id, target_type, timestamp
 *
 * NOTE: Symfony uses `id` as the PK column name (auto-generated).
 *       The `timestamp` column is DATETIME (maps to LocalDateTime in Java).
 */
public class UserActivity {

    /** PK — column name: `id` (matches Symfony @ORM\Column) */
    private int id;

    /** column: user_id */
    private int userId;

    /** column: activity_type  e.g. 'CLICK', 'VISIT', 'SEARCH', 'STAY' */
    private String activityType;

    /** column: target_id (VARCHAR 255 in Symfony, nullable) */
    private String targetId;

    /** column: target_type  e.g. 'DESTINATION', 'ACTIVITY', 'PAGE', 'FEATURE' */
    private String targetType;

    /** column: timestamp  DATETIME (Symfony uses DateTimeImmutable) */
    private LocalDateTime timestamp;

    // ── Constructors ──────────────────────────────────────────────────────────

    public UserActivity() {
        this.timestamp = LocalDateTime.now();
    }

    public UserActivity(int userId, String activityType) {
        this.userId       = userId;
        this.activityType = activityType;
        this.timestamp    = LocalDateTime.now();
    }

    public UserActivity(int userId, String activityType, String targetId, String targetType) {
        this.userId       = userId;
        this.activityType = activityType;
        this.targetId     = targetId;
        this.targetType   = targetType;
        this.timestamp    = LocalDateTime.now();
    }

    /** Legacy convenience constructor that accepts Long targetId. */
    public UserActivity(int userId, String activityType, Long targetId, String targetType) {
        this(userId, activityType, targetId != null ? String.valueOf(targetId) : null, targetType);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    /** PK — maps to column `id` */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Legacy alias for getId() so old code using getLogId() still works. */
    public int getLogId() { return id; }
    public void setLogId(int logId) { this.id = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    /** Returns targetId as String (matches Symfony VARCHAR 255). */
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    /** Legacy setter that accepts Long — converts to String automatically. */
    public void setTargetId(Long targetId) {
        this.targetId = targetId != null ? String.valueOf(targetId) : null;
    }

    /** Returns targetId as Long for services that still expect Long. Returns null if not numeric. */
    public Long getTargetIdAsLong() {
        if (targetId == null) return null;
        try { return Long.parseLong(targetId); } catch (NumberFormatException e) { return null; }
    }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", userId=" + userId +
                ", activityType='" + activityType + '\'' +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
