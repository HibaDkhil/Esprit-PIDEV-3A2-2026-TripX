package tn.esprit.utils;

public class SessionManager {
    // -1 means no authenticated user in session.
    private static int currentUserId = -1;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }
}
