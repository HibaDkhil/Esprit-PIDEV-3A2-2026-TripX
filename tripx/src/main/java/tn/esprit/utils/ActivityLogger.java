package tn.esprit.utils;

import tn.esprit.entities.User;
import tn.esprit.entities.UserActivity;
import tn.esprit.services.UserActivityService;

public class ActivityLogger {
    private static final UserActivityService activityService = new UserActivityService();

    /**
     * Log a simple page visit.
     */
    public static void logVisit(User user, String pageName) {
        if (user == null) return;
        UserActivity activity = new UserActivity(user.getUserId(), "VISIT", null, "PAGE:" + pageName);
        activityService.logActivity(activity);
    }

    /**
     * Log a search action.
     */
    public static void logSearch(User user, String query) {
        if (user == null || query == null || query.trim().isEmpty()) return;
        UserActivity activity = new UserActivity(user.getUserId(), "SEARCH", null, "QUERY:" + query);
        activityService.logActivity(activity);
    }

    /**
     * Log interaction with a specific entity (Destination, Activity, etc.)
     */
    public static void logInteraction(User user, Long targetId, String targetType, String action) {
        if (user == null) return;
        UserActivity activity = new UserActivity(user.getUserId(), action, targetId, targetType);
        activityService.logActivity(activity);
    }

    /**
     * Log use of a specific feature (e.g., AI Chat, Trip Planner).
     */
    public static void logFeatureUse(User user, String featureName) {
        if (user == null) return;
        UserActivity activity = new UserActivity(user.getUserId(), "USE", null, "FEATURE:" + featureName);
        activityService.logActivity(activity);
    }
}
