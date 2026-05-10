package tn.esprit.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Map<String, String> envVars = new HashMap<>();

    static {
        loadEnvFile();
    }

    private static void loadEnvFile() {
        Path path = Paths.get(".env");
        if (!Files.exists(path)) {
            Path inUserDir = Paths.get(System.getProperty("user.dir", "."), ".env");
            if (Files.exists(inUserDir)) path = inUserDir;
        }
        if (!Files.exists(path)) {
            System.out.println("⚠️ No .env found in current dir or " + System.getProperty("user.dir", ".") + ". Using system env.");
            return;
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String keyPart = parts[0].trim().replace("\uFEFF", "");
                        String value = parts[1].trim().replace("\uFEFF", "");
                        if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))))
                            value = value.substring(1, value.length() - 1);
                        value = value.replace("\r", "").replace("\n", "").trim();
                        envVars.put(keyPart, value);
                    System.out.println("✅ Loaded: " + keyPart);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        String value = getOptional(key);
        if (!value.isEmpty()) {
            return value;
        }
        System.err.println("⚠️ Warning: " + key + " not found in any source!");
        return "";
    }

    private static String getOptional(String key) {
        // 1. Try .env file first
        String value = envVars.get(key);
        if (value != null && !value.isEmpty()) return value;

        // 2. Try system environment variables
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) return value;

        // 3. Try system properties (for IDE configurations)
        value = System.getProperty(key);
        if (value != null && !value.isEmpty()) return value;
        return "";
    }

    /** Returns value from .env, then system env, then system property; empty string if not found. */
    public static String getEnv(String key) {
        return getOptional(key);
    }

    // Convenience methods
    public static String getGeminiKey() {
        return get("GEMINI_API_KEY");
    }

    public static String getWeatherKey() {
        String weather = getOptional("WEATHER_API_KEY");
        if (!weather.isEmpty()) {
            return weather;
        }
        // Backward-compatible alias used by WeatherService.
        return get("OPENWEATHER_API_KEY");
    }

    public static String getEmailKey() {
        return get("EMAIL_API_KEY");
    }

    public static String getGoogleClientId() {
        return get("GOOGLE_CLIENT_ID");
    }

    public static String getGoogleClientSecret() {
        return get("GOOGLE_CLIENT_SECRET");
    }

    public static String getLinkedInClientId() {
        return get("LINKEDIN_CLIENT_ID");
    }

    public static String getLinkedInClientSecret() {
        return get("LINKEDIN_CLIENT_SECRET");
    }
}