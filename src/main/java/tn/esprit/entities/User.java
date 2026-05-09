package tn.esprit.entities;

import java.util.Date;

/**
 * User entity — mirrors the Symfony `user` table exactly.
 * Table: user
 * Columns: user_id, first_name, last_name, email, password,
 *          phone_number, role, status, email_verified,
 *          gender, birth_year, bio, avatar_id,
 *          created_at, updated_at,
 *          google_authenticator_secret, dynamic_theme_enabled, face_descriptor
 *
 * Status values (match Symfony): pending_verification, active, suspended, banned
 * Role values (match Symfony):   user, admin, adminDestination, adminAccomodation,
 *                                adminOffers, adminBlog, adminTransport
 */
public class User {

    private int    userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;

    // Symfony default: 'user'
    private String role   = "user";

    // Symfony default: 'pending_verification'
    private String status = "pending_verification";

    // Symfony: email_verified TINYINT(1) default 0
    private boolean emailVerified = false;

    private String gender;
    private String birthYear;
    private String bio;

    // Symfony: avatar_id INT nullable
    private Integer avatarId;

    private Date createdAt;
    private Date updatedAt;

    // Symfony 2FA columns (nullable)
    private String googleAuthenticatorSecret;

    // Symfony: dynamic_theme_enabled TINYINT(1) default 1
    private boolean dynamicThemeEnabled = true;

    // Symfony: face_descriptor JSON nullable — stored as raw JSON String in Java
    private String faceDescriptor;

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {
    }

    /** Minimal constructor for signup (Java-side). */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.password  = password;
        this.role      = "user";
        this.status    = "pending_verification";
    }

    /** Full constructor used when reading rows from the DB. */
    public User(int userId, String firstName, String lastName, String email,
                String password, String gender, String birthYear, Date createdAt) {
        this.userId    = userId;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.password  = password;
        this.gender    = gender;
        this.birthYear = birthYear;
        this.createdAt = createdAt;
        this.role      = "user";
        this.status    = "pending_verification";
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getAvatarId() { return avatarId; }
    public void setAvatarId(Integer avatarId) { this.avatarId = avatarId; }

    /** Legacy setter — accepts String so old call-sites don't break. */
    public void setAvatarId(String avatarId) {
        if (avatarId == null || avatarId.isEmpty()) {
            this.avatarId = null;
        } else {
            try {
                this.avatarId = Integer.parseInt(avatarId);
            } catch (NumberFormatException e) {
                // If it's not a number (old format like "big-smile:Adrian"), ignore it
                this.avatarId = null;
            }
        }
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getGoogleAuthenticatorSecret() { return googleAuthenticatorSecret; }
    public void setGoogleAuthenticatorSecret(String googleAuthenticatorSecret) {
        this.googleAuthenticatorSecret = googleAuthenticatorSecret;
    }

    public boolean isDynamicThemeEnabled() { return dynamicThemeEnabled; }
    public void setDynamicThemeEnabled(boolean dynamicThemeEnabled) {
        this.dynamicThemeEnabled = dynamicThemeEnabled;
    }

    public String getFaceDescriptor() { return faceDescriptor; }
    public void setFaceDescriptor(String faceDescriptor) { this.faceDescriptor = faceDescriptor; }

    // ── Convenience helpers ───────────────────────────────────────────────────

    /** Returns true if this user has any admin role. */
    public boolean isAdmin() {
        return role != null && role.toLowerCase().startsWith("admin");
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", gender='" + gender + '\'' +
                ", birthYear='" + birthYear + '\'' +
                ", avatarId=" + avatarId +
                ", createdAt=" + createdAt +
                '}';
    }
}