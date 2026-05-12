package tn.esprit.services;

import tn.esprit.entities.Destination;
import tn.esprit.entities.UserPreferences;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherPreferenceMatcher {

    private WeatherServiceUser weatherServiceUser;  // Fixed variable name
    private DestinationService destinationService;

    public WeatherPreferenceMatcher() {
        this.weatherServiceUser = new WeatherServiceUser();  // Fixed class name
        this.destinationService = new DestinationService();
    }

    /**
     * Get destinations that match user's climate preference
     */
    public List<DestinationMatch> getDestinationsMatchingClimate(UserPreferences prefs) {
        List<Destination> allDestinations = destinationService.getAllDestinations();
        List<DestinationMatch> matches = new ArrayList<>();

        String climatePref = prefs.getPreferredClimate();
        if (climatePref == null || climatePref.isEmpty()) {
            return Collections.emptyList();
        }

        // Parse climate preferences
        List<String> preferredClimates = parseClimatePreferences(climatePref);
        System.out.println("🔍 User climate preferences: " + preferredClimates);

        for (Destination dest : allDestinations) {
            // Initial score based on destination attributes
            double baseScore = calculateBaseClimateMatch(dest, preferredClimates);

            if (baseScore > 0) {
                // Try to fetch real weather for this destination
                WeatherServiceUser.WeatherInfo weather = null;  // Fixed type
                try {
                    String city = dest.getCity();
                    if (city != null && !city.isEmpty()) {
                        weather = weatherServiceUser.getWeatherForCity(city);  // Fixed variable name
                        if (weather == null) {
                            weather = weatherServiceUser.getWeatherForCity(city.split(",")[0].trim());  // Fixed variable name
                        }
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Could not fetch weather for " + dest.getCity());
                }

                // Refine score based on real weather if available
                int finalScore = refineScoreWithRealWeather(baseScore, weather, preferredClimates);

                matches.add(new DestinationMatch(dest, finalScore, weather));
            }
        }

        // Sort by match score (highest first), then by name for stability
        matches.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getMatchScore(), a.getMatchScore());
            if (scoreCompare != 0) return scoreCompare;
            return a.getDestination().getName().compareTo(b.getDestination().getName());
        });

        System.out.println("✅ Found " + matches.size() + " matching destinations");
        return matches;
    }

    /**
     * Search destinations by city name and show current weather
     */
    public DestinationMatch searchDestinationWithWeather(String cityName) {
        List<Destination> destinations = destinationService.searchDestinations(cityName);

        if (destinations.isEmpty()) {
            return null;
        }

        // Take first matching destination
        Destination dest = destinations.get(0);
        WeatherServiceUser.WeatherInfo weather = weatherServiceUser.getWeatherForCity(dest.getCity());  // Fixed

        return new DestinationMatch(dest, 100, weather);
    }

    private String mapSeasonToClimate(Destination.Season season) {
        if (season == null) return "Temperate";

        if (season == Destination.Season.spring) {
            return "Temperate";
        } else if (season == Destination.Season.summer) {
            return "Tropical,Mediterranean";
        } else if (season == Destination.Season.autumn) {
            return "Mediterranean,Temperate";
        } else if (season == Destination.Season.winter) {
            return "Cold/Arctic";
        } else if (season == Destination.Season.all_year) {
            return "Temperate,Mediterranean";
        } else {
            return "Temperate";
        }
    }

    private double calculateBaseClimateMatch(Destination dest, List<String> preferredClimates) {
        String destClimates = mapSeasonToClimate(dest.getBestSeason());

        if (dest.getType() != null) {
            switch (dest.getType()) {
                case desert: destClimates += ",Hot,Dry,Sunny"; break;
                case beach: destClimates += ",Tropical,Sunny,Humid"; break;
                case mountain: destClimates += ",Cold,Fresh,Snowy"; break;
                case forest: destClimates += ",Humid,Temperate,Rainy"; break;
            }
        }

        String[] destTags = destClimates.split(",");
        double maxMatch = 0;

        for (String pref : preferredClimates) {
            pref = pref.trim().toLowerCase();
            for (String tag : destTags) {
                tag = tag.trim().toLowerCase();
                if (tag.equals(pref)) {
                    maxMatch = Math.max(maxMatch, 1.0);
                } else if (tag.contains(pref) || pref.contains(tag)) {
                    maxMatch = Math.max(maxMatch, 0.7);
                }
            }
        }
        return maxMatch * 100;
    }

    private int refineScoreWithRealWeather(double baseScore, WeatherServiceUser.WeatherInfo weather, List<String> preferredClimates) {
        if (weather == null) return (int) baseScore;

        double weatherBonus = 0;
        int activePrefs = 0;

        for (String pref : preferredClimates) {
            pref = pref.trim().toLowerCase();
            activePrefs++;

            // Temperature matches
            if (pref.contains("hot") || pref.contains("tropical")) {
                if (weather.getTemperature() > 28) weatherBonus += 40;
                else if (weather.getTemperature() > 22) weatherBonus += 25;
            } else if (pref.contains("cold") || pref.contains("arctic") || pref.contains("winter")) {
                if (weather.getTemperature() < 5) weatherBonus += 40;
                else if (weather.getTemperature() < 15) weatherBonus += 20;
            } else if (pref.contains("temperate") || pref.contains("mild")) {
                if (weather.getTemperature() >= 15 && weather.getTemperature() <= 25) weatherBonus += 40;
            }

            // Condition matches
            String condition = weather.getMainCondition().toLowerCase();
            if ((pref.contains("sun") || pref.contains("clear")) && condition.contains("clear")) {
                weatherBonus += 40;
            } else if (pref.contains("rain") && (condition.contains("rain") || condition.contains("drizzle"))) {
                weatherBonus += 40;
            } else if (pref.contains("snow") && condition.contains("snow")) {
                weatherBonus += 40;
            } else if (pref.contains("cloud") && condition.contains("cloud")) {
                weatherBonus += 40;
            }
        }

        // Average bonus points and combine with base score
        double finalScore = (baseScore * 0.4) + (weatherBonus / Math.max(1, activePrefs) * 1.5);

        // Ensure some variability and caps
        finalScore = Math.min(100, Math.max(10, finalScore));

        // Add a tiny bit of random variability to avoid identical 75% scores if they are very similar
        // but still logically grounded
        finalScore += (weather != null ? (weather.getHumidity() % 5) : (baseScore % 3));

        return (int) Math.min(99, finalScore); // 100% is rare, keeps it feeling "calculated"
    }

    private List<String> parseClimatePreferences(String climatePref) {
        // If it's JSON array, parse it
        if (climatePref.startsWith("[")) {
            // Simple parsing - you might want to use Gson here
            return Arrays.stream(climatePref.replace("[", "")
                            .replace("]", "")
                            .replace("\"", "")
                            .split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else {
            // Single value
            return Collections.singletonList(climatePref);
        }
    }

    // Inner class for match results - FIXED to use WeatherServiceUser.WeatherInfo
    public static class DestinationMatch {
        private Destination destination;
        private int matchScore;
        private WeatherServiceUser.WeatherInfo weather;  // Fixed type

        public DestinationMatch(Destination destination, int matchScore, WeatherServiceUser.WeatherInfo weather) {  // Fixed constructor
            this.destination = destination;
            this.matchScore = matchScore;
            this.weather = weather;
        }

        public Destination getDestination() { return destination; }
        public int getMatchScore() { return matchScore; }
        public WeatherServiceUser.WeatherInfo getWeather() { return weather; }  // Fixed return type

        public String getDisplayText() {
            String weatherText = (weather != null) ? weather.toString() : "Weather unavailable";
            return String.format("%s, %s - %d%% match %s",
                    destination.getName(),
                    destination.getCountry(),
                    matchScore,
                    weatherText);
        }
    }
}