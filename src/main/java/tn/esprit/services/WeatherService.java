package tn.esprit.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tn.esprit.utils.ApiConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for fetching current weather data from OpenWeatherMap API.
 * Includes an in-memory cache to reduce API calls.
 */
public class WeatherService {

    // Cache: key = "city,country" -> value = WeatherData
    private static final Map<String, WeatherData> cache = new HashMap<>();
    private static final Map<String, Long> cacheTimestamps = new HashMap<>();
    private static final long CACHE_DURATION_MS = 10 * 60 * 1000; // 10 minutes

    /**
     * Weather data container.
     */
    public static class WeatherData {
        public final double temperature;     // Celsius
        public final double feelsLike;       // Celsius
        public final String description;     // e.g. "clear sky"
        public final String icon;            // icon code e.g. "01d"
        public final int humidity;           // percentage
        public final double windSpeed;       // m/s
        public final String cityName;

        public WeatherData(double temperature, double feelsLike, String description,
                          String icon, int humidity, double windSpeed, String cityName) {
            this.temperature = temperature;
            this.feelsLike = feelsLike;
            this.description = description;
            this.icon = icon;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.cityName = cityName;
        }

        /** Returns a weather emoji based on the icon code */
        public String getEmoji() {
            if (icon == null) return "🌍";
            switch (icon.substring(0, 2)) {
                case "01": return "☀️";   // clear
                case "02": return "⛅";    // few clouds
                case "03": return "☁️";   // scattered clouds
                case "04": return "☁️";   // broken clouds
                case "09": return "🌧️";  // shower rain
                case "10": return "🌦️";  // rain
                case "11": return "⛈️";   // thunderstorm
                case "13": return "❄️";   // snow
                case "50": return "🌫️";  // mist
                default:   return "🌤️";
            }
        }

        /** Returns formatted temperature string */
        public String getTempString() {
            return String.format("%.0f°C", temperature);
        }

        /** Returns a compact one-line summary */
        public String getSummary() {
            return getEmoji() + " " + getTempString() + " — " + capitalize(description);
        }

        /** Returns a full multi-line description */
        public String getFullDescription() {
            return getEmoji() + " " + capitalize(description) + "\n" +
                   "🌡️ Temperature: " + getTempString() + " (feels like " + String.format("%.0f°C", feelsLike) + ")\n" +
                   "💧 Humidity: " + humidity + "%\n" +
                   "💨 Wind: " + String.format("%.1f m/s", windSpeed);
        }

        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }

    /**
     * Fetches weather for a city/country combination.
     * Returns cached data if available and fresh.
     *
     * @param city    City name (e.g. "Paris")
     * @param country Country name or code (e.g. "France" or "FR")
     * @return WeatherData or null if the API call fails
     */
    public WeatherData getWeather(String city, String country) {
        if (city == null || city.isEmpty()) return null;

        String cacheKey = (city + "," + (country != null ? country : "")).toLowerCase();

        // Check cache
        if (cache.containsKey(cacheKey)) {
            long age = System.currentTimeMillis() - cacheTimestamps.getOrDefault(cacheKey, 0L);
            if (age < CACHE_DURATION_MS) {
                return cache.get(cacheKey);
            }
        }

        try {
            String query = URLEncoder.encode(city, StandardCharsets.UTF_8);
            if (country != null && !country.isEmpty()) {
                query += "," + URLEncoder.encode(country, StandardCharsets.UTF_8);
            }

            String urlStr = ApiConfig.OPENWEATHER_BASE_URL +
                    "?q=" + query +
                    "&appid=" + ApiConfig.OPENWEATHER_API_KEY +
                    "&units=metric";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status != 200) {
                System.err.println("Weather API returned status " + status + " for " + city);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

            // Parse main weather data
            JsonObject main = json.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            int humidity = main.get("humidity").getAsInt();

            JsonObject wind = json.getAsJsonObject("wind");
            double windSpeed = wind.get("speed").getAsDouble();

            JsonArray weatherArr = json.getAsJsonArray("weather");
            JsonObject weather = weatherArr.get(0).getAsJsonObject();
            String description = weather.get("description").getAsString();
            String icon = weather.get("icon").getAsString();

            String resolvedCity = json.get("name").getAsString();

            WeatherData data = new WeatherData(temp, feelsLike, description, icon, humidity, windSpeed, resolvedCity);

            // Cache the result
            cache.put(cacheKey, data);
            cacheTimestamps.put(cacheKey, System.currentTimeMillis());

            return data;

        } catch (Exception e) {
            System.err.println("Weather API error for " + city + ": " + e.getMessage());
            return null;
        }
    }
}
