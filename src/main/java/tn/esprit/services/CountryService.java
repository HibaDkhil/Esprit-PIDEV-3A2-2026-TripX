package tn.esprit.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tn.esprit.entities.Country;
import tn.esprit.utils.ApiConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for fetching country information from the REST Countries API.
 * No API key required. Includes caching.
 */
public class CountryService {

    // Cache: key = country name -> value = CountryData
    private static final Map<String, CountryData> cache = new HashMap<>();
    private static final Map<String, Long> failureCache = new HashMap<>();
    private static final long RETRY_AFTER_MS = 300000; // 5 minutes

    /** Static list for dropdowns (e.g. travel story destination). */
    private static final List<Country> ALL_COUNTRIES = new ArrayList<>(Arrays.asList(
            new Country("Tunisia", "TN"), new Country("France", "FR"), new Country("Italy", "IT"),
            new Country("Spain", "ES"), new Country("Germany", "DE"), new Country("United Kingdom", "GB"),
            new Country("United States", "US"), new Country("Japan", "JP"), new Country("Thailand", "TH"),
            new Country("Indonesia", "ID"), new Country("Morocco", "MA"), new Country("Egypt", "EG"),
            new Country("Turkey", "TR"), new Country("Greece", "GR"), new Country("Portugal", "PT"),
            new Country("Netherlands", "NL"), new Country("Belgium", "BE"), new Country("Switzerland", "CH"),
            new Country("Austria", "AT"), new Country("Canada", "CA"), new Country("Australia", "AU"),
            new Country("India", "IN"), new Country("Malaysia", "MY"), new Country("Vietnam", "VN"),
            new Country("South Korea", "KR"), new Country("China", "CN"), new Country("Brazil", "BR"),
            new Country("Mexico", "MX"), new Country("Argentina", "AR"), new Country("South Africa", "ZA"),
            new Country("United Arab Emirates", "AE"), new Country("Saudi Arabia", "SA"), new Country("Lebanon", "LB"),
            new Country("Jordan", "JO"), new Country("Algeria", "DZ"), new Country("Libya", "LY")
    ));

    /**
     * Returns a list of countries for dropdowns (e.g. destination in blog/travel story).
     */
    public List<Country> getAll() {
        return new ArrayList<>(ALL_COUNTRIES);
    }

    /**
     * Country data container.
     */
    public static class CountryData {
        public final String commonName;      // e.g. "France"
        public final String officialName;    // e.g. "French Republic"
        public final String capital;         // e.g. "Paris"
        public final String region;          // e.g. "Europe"
        public final String subregion;       // e.g. "Western Europe"
        public final long population;
        public final String flagEmoji;       // e.g. "🇫🇷"
        public final String flagUrl;         // URL to SVG flag
        public final String[] currencies;    // e.g. ["EUR - Euro"]
        public final String[] languages;     // e.g. ["French"]
        public final String[] timezones;     // e.g. ["UTC+01:00"]
        public final double latitude;
        public final double longitude;

        public CountryData(String commonName, String officialName, String capital,
                          String region, String subregion, long population,
                          String flagEmoji, String flagUrl,
                          String[] currencies, String[] languages, String[] timezones,
                          double latitude, double longitude) {
            this.commonName = commonName;
            this.officialName = officialName;
            this.capital = capital;
            this.region = region;
            this.subregion = subregion;
            this.population = population;
            this.flagEmoji = flagEmoji;
            this.flagUrl = flagUrl;
            this.currencies = currencies;
            this.languages = languages;
            this.timezones = timezones;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        /** Returns formatted population string */
        public String getPopulationString() {
            if (population >= 1_000_000_000) return String.format("%.1fB", population / 1_000_000_000.0);
            if (population >= 1_000_000) return String.format("%.1fM", population / 1_000_000.0);
            if (population >= 1_000) return String.format("%.0fK", population / 1_000.0);
            return String.valueOf(population);
        }

        /** Returns the first timezone, or empty string */
        public String getPrimaryTimezone() {
            return (timezones != null && timezones.length > 0) ? timezones[0] : "";
        }

        /** Returns all languages as comma-separated string */
        public String getLanguagesString() {
            if (languages == null || languages.length == 0) return "";
            return String.join(", ", languages);
        }

        /** Returns all currencies as comma-separated string */
        public String getCurrenciesString() {
            if (currencies == null || currencies.length == 0) return "";
            return String.join(", ", currencies);
        }

        /** Returns a compact info summary */
        public String getSummary() {
            return flagEmoji + " " + commonName + " | " + region +
                   " | Pop: " + getPopulationString() +
                   " | " + getLanguagesString();
        }
    }

    /**
     * Fetches country info by name.
     *
     * @param countryName Country name (e.g. "France", "USA", "Tunisia")
     * @return CountryData or null if not found
     */
    public CountryData getCountryByName(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) return null;

        String cacheKey = countryName.trim().toLowerCase();

        // Check cache
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        // Check failure cache
        if (failureCache.containsKey(cacheKey)) {
            if (System.currentTimeMillis() - failureCache.get(cacheKey) < RETRY_AFTER_MS) {
                return null;
            }
            failureCache.remove(cacheKey); // Retry period passed, remove from failure cache
        }

        try {
            String encoded = URLEncoder.encode(countryName.trim(), StandardCharsets.UTF_8);
            String urlStr = ApiConfig.REST_COUNTRIES_BASE_URL + "/name/" + encoded + "?fullText=false";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status != 200) {
                System.err.println("REST Countries API returned status " + status + " for " + countryName);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonArray results = JsonParser.parseString(response.toString()).getAsJsonArray();
            if (results.size() == 0) return null;

            // Take the first (best) match
            JsonObject country = results.get(0).getAsJsonObject();

            // Parse names
            JsonObject nameObj = country.getAsJsonObject("name");
            String commonName = nameObj.get("common").getAsString();
            String officialName = nameObj.has("official") ? nameObj.get("official").getAsString() : commonName;

            // Capital
            String capital = "";
            if (country.has("capital") && country.get("capital").isJsonArray()) {
                JsonArray caps = country.getAsJsonArray("capital");
                if (caps.size() > 0) capital = caps.get(0).getAsString();
            }

            // Region
            String region = country.has("region") ? country.get("region").getAsString() : "";
            String subregion = country.has("subregion") ? country.get("subregion").getAsString() : "";

            // Population
            long population = country.has("population") ? country.get("population").getAsLong() : 0;

            // Flag emoji
            String flagEmoji = "";
            if (country.has("flag")) {
                flagEmoji = country.get("flag").getAsString();
            }

            // Flag URL (PNG)
            String flagUrl = "";
            if (country.has("flags")) {
                JsonObject flags = country.getAsJsonObject("flags");
                if (flags.has("png")) flagUrl = flags.get("png").getAsString();
            }

            // Currencies
            String[] currencies = new String[0];
            if (country.has("currencies") && country.get("currencies").isJsonObject()) {
                JsonObject currObj = country.getAsJsonObject("currencies");
                currencies = new String[currObj.size()];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : currObj.entrySet()) {
                    String code = entry.getKey();
                    String currName = entry.getValue().getAsJsonObject().get("name").getAsString();
                    currencies[i++] = code + " - " + currName;
                }
            }

            // Languages
            String[] languages = new String[0];
            if (country.has("languages") && country.get("languages").isJsonObject()) {
                JsonObject langObj = country.getAsJsonObject("languages");
                languages = new String[langObj.size()];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : langObj.entrySet()) {
                    languages[i++] = entry.getValue().getAsString();
                }
            }

            // Timezones
            String[] timezones = new String[0];
            if (country.has("timezones") && country.get("timezones").isJsonArray()) {
                JsonArray tzArr = country.getAsJsonArray("timezones");
                timezones = new String[tzArr.size()];
                for (int i = 0; i < tzArr.size(); i++) {
                    timezones[i] = tzArr.get(i).getAsString();
                }
            }

            // Coordinates
            double lat = 0, lng = 0;
            if (country.has("latlng") && country.get("latlng").isJsonArray()) {
                JsonArray latlng = country.getAsJsonArray("latlng");
                if (latlng.size() >= 2) {
                    lat = latlng.get(0).getAsDouble();
                    lng = latlng.get(1).getAsDouble();
                }
            }

            CountryData data = new CountryData(commonName, officialName, capital,
                    region, subregion, population, flagEmoji, flagUrl,
                    currencies, languages, timezones, lat, lng);

            // Cache
            cache.put(cacheKey, data);

            return data;

        } catch (Exception e) {
            System.err.println("REST Countries API error for " + countryName + ": " + e.getMessage());
            failureCache.put(cacheKey, System.currentTimeMillis());
            return null;
        }
    }
}
