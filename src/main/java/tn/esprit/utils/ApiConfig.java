package tn.esprit.utils;

/**
 * Centralized API configuration.
 * Replace the placeholder keys with your real API keys.
 */
public class ApiConfig {

    // Stripe Test Mode Secret Key
    // Get yours at: https://dashboard.stripe.com/test/apikeys
    public static final String STRIPE_SECRET_KEY = "sk_test_51T2vavPVlBCtS0sumOFSUdbV1AWUmoDquoC1d2dqCcZUrJhaqGO37haphQsvTNGdMNem4W9aXiS2EDNUx7XI8z0I006JCJxufs";

    // Stripe Test Mode Publishable Key
    public static final String STRIPE_PUBLISHABLE_KEY = "pk_test_51T2vavPVlBCtS0su1Ezrmk51b72n3wZGybpcEEf4CTwpulvs2QhvqjFhK3mS0wh8IB6xHReIDLFgpaFwvLH8uJnU00Mu3orRvE";

    // App name shown on Stripe Checkout
    public static final String APP_NAME = "TripX Travel";

    // Success/Cancel URL for Stripe Checkout (local redirect markers)
    public static final String PAYMENT_SUCCESS_URL = "https://tripx.local/payment/success";
    public static final String PAYMENT_CANCEL_URL = "https://tripx.local/payment/cancel";

    // OpenWeatherMap API
    // Get yours at: https://openweathermap.org/api
    public static final String OPENWEATHER_API_KEY = "c58259a074febdf474dd8c4a1b58ad0e";
    public static final String OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    // REST Countries API (no key needed)
    public static final String REST_COUNTRIES_BASE_URL = "https://restcountries.com/v3.1";
}
