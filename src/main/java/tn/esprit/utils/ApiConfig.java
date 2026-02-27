package tn.esprit.utils;

public class ApiConfig {

    // Stripe
    public static final String STRIPE_SECRET_KEY =
            System.getenv("STRIPE_SECRET_KEY");

    public static final String STRIPE_PUBLISHABLE_KEY =
            System.getenv("STRIPE_PUBLISHABLE_KEY");

    public static final String APP_NAME = "TripX Travel";

    public static final String PAYMENT_SUCCESS_URL =
            "https://tripx.local/payment/success";

    public static final String PAYMENT_CANCEL_URL =
            "https://tripx.local/payment/cancel";

    // OpenWeather
    public static final String OPENWEATHER_API_KEY =
            System.getenv("OPENWEATHER_API_KEY");

    public static final String OPENWEATHER_BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather";

    // REST Countries
    public static final String REST_COUNTRIES_BASE_URL =
            "https://restcountries.com/v3.1";
}