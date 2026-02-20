package tn.esprit.services;

import tn.esprit.entities.Destination;
import tn.esprit.entities.UserPreferences;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherPreferenceMatcher {

    private WeatherService weatherService;
    private DestinationService destinationService;

    public WeatherPreferenceMatcher() {
        this.weatherService = new WeatherService();
        this.destinationService = new DestinationService();
    }

    /**
     * Get destinations that match user's climate preference
     */
    // Update the getDestinationsMatchingClimate method
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
            int matchScore = calculateClimateMatch(dest, preferredClimates);
            System.out.println("   Checking " + dest.getName() + " - Best season: " + dest.getBestSeason() +
                    " - Match score: " + matchScore + "%");

            if (matchScore > 0) {
                // Try to fetch real weather for this destination
                WeatherService.WeatherInfo weather = null;
                try {
                    weather = weatherService.getWeatherForCity(dest.getCity());
                    if (weather == null) {
                        // Try with just the city name without country
                        weather = weatherService.getWeatherForCity(dest.getCity().split(",")[0].trim());
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Could not fetch weather for " + dest.getCity());
                }

                matches.add(new DestinationMatch(dest, matchScore, weather));
            }
        }

        // Sort by match score (highest first)
        matches.sort((a, b) -> b.getMatchScore() - a.getMatchScore());

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
        WeatherService.WeatherInfo weather = weatherService.getWeatherForCity(dest.getCity());

        return new DestinationMatch(dest, 100, weather);
    }

    private String mapSeasonToClimate(Destination.Season season) {
        if (season == null) return "Temperate";

        switch (season) {
            case spring:
                return "Temperate"; // Spring is mild/temperate
            case summer:
                return "Tropical,Mediterranean"; // Summer can be tropical or Mediterranean
            case autumn:
                return "Mediterranean,Temperate"; // Autumn is often mild
            case winter:
                return "Cold/Arctic"; // Winter is cold
            case all_year:
                return "Temperate,Mediterranean"; // All year could be multiple
            default:
                return "Temperate";
        }
    }

    private int calculateClimateMatch(Destination dest, List<String> preferredClimates) {
        String destClimates = mapSeasonToClimate(dest.getBestSeason());
        String[] possibleClimates = destClimates.split(",");

        for (String pref : preferredClimates) {
            pref = pref.trim();
            for (String destClimate : possibleClimates) {
                destClimate = destClimate.trim();
                if (destClimate.equalsIgnoreCase(pref)) {
                    return 100; // Perfect match
                }
                // Partial matches
                if (destClimate.contains(pref) || pref.contains(destClimate)) {
                    return 75;
                }
            }
        }
        return 0;
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

    // Inner class for match results
    public static class DestinationMatch {
        private Destination destination;
        private int matchScore;
        private WeatherService.WeatherInfo weather;

        public DestinationMatch(Destination destination, int matchScore, WeatherService.WeatherInfo weather) {
            this.destination = destination;
            this.matchScore = matchScore;
            this.weather = weather;
        }

        public Destination getDestination() { return destination; }
        public int getMatchScore() { return matchScore; }
        public WeatherService.WeatherInfo getWeather() { return weather; }

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