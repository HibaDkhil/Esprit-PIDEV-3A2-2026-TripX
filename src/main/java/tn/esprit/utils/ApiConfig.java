package tn.esprit.utils;

/**
 * Centralized API configuration.
 * Replace the placeholder keys with your real API keys.
 */
public class ApiConfig {

    // Stripe Test Mode Secret Key
    // Get yours at: https://dashboard.stripe.com/test/apikeys
    public static final String STRIPE_SECRET_KEY = "";

    // Stripe Test Mode Publishable Key
    public static final String STRIPE_PUBLISHABLE_KEY = "";

    // App name shown on Stripe Checkout
    public static final String APP_NAME = "TripX Travel";

    // Success/Cancel URL for Stripe Checkout (local redirect markers)
    public static final String PAYMENT_SUCCESS_URL = "https://tripx.local/payment/success";
    public static final String PAYMENT_CANCEL_URL = "https://tripx.local/payment/cancel";

    // OpenWeatherMap API
    // Get yours at: https://openweathermap.org/api
    public static final String OPENWEATHER_API_KEY = "";
    public static final String OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    // REST Countries API (no key needed)
    public static final String REST_COUNTRIES_BASE_URL = "https://restcountries.com/v3.1";
}
