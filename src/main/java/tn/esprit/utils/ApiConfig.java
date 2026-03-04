package tn.esprit.utils;

public class ApiConfig {

    // Stripe — load from .env file first (Config), then system environment
    public static final String STRIPE_SECRET_KEY =
            sanitizeKey(firstNonEmpty(Config.getEnv("STRIPE_SECRET_KEY"), System.getenv("STRIPE_SECRET_KEY")));

    public static final String STRIPE_PUBLISHABLE_KEY =
            sanitizeKey(firstNonEmpty(Config.getEnv("STRIPE_PUBLISHABLE_KEY"), System.getenv("STRIPE_PUBLISHABLE_KEY")));

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                String cleaned = v.replace("\r", "").replace("\n", "").trim();
                if (!cleaned.isEmpty()) return cleaned;
            }
        }
        return null;
    }

    /** Remove BOM, extra quotes, and surrounding whitespace so keys from .env work reliably. */
    private static String sanitizeKey(String key) {
        if (key == null) return null;
        key = key.replace("\uFEFF", "").replace("\r", "").replace("\n", "").trim();
        if (key.length() >= 2 && (key.startsWith("\"") && key.endsWith("\"") || key.startsWith("'") && key.endsWith("'")))
            key = key.substring(1, key.length() - 1).trim();
        return key.isEmpty() ? null : key;
    }

    public static final String APP_NAME = "TripX Travel";

    public static final String PAYMENT_SUCCESS_URL =
            "https://tripx.local/payment/success";

    public static final String PAYMENT_CANCEL_URL =
            "https://tripx.local/payment/cancel";

    /** Currency for Stripe (e.g. "usd"). If not set, defaults to "usd". */
    public static final String STRIPE_CURRENCY =
            firstNonEmpty(Config.getEnv("STRIPE_CURRENCY"), System.getenv("STRIPE_CURRENCY"), "usd");

    /** TND to USD rate for converting TND bookings to USD in Stripe (e.g. 0.32). Optional. */
    public static double getTndToUsdRate() {
        String s = firstNonEmpty(Config.getEnv("TND_TO_USD"), System.getenv("TND_TO_USD"));
        if (s == null || s.isBlank()) return 0.32;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return 0.32; }
    }

    // OpenWeather
    public static final String OPENWEATHER_API_KEY =
            System.getenv("OPENWEATHER_API_KEY");

    public static final String OPENWEATHER_BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather";

    // REST Countries
    public static final String REST_COUNTRIES_BASE_URL =
            "https://restcountries.com/v3.1";

    // Groq (for AI-generated story summary)
    public static final String GROQ_API_KEY =
            sanitizeKey(firstNonEmpty(Config.getEnv("GROQ_API_KEY"), System.getenv("GROQ_API_KEY")));

    public static final String GROQ_CHAT_URL =
            "https://api.groq.com/openai/v1/chat/completions";
}