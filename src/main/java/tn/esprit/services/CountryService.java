package tn.esprit.services;

import tn.esprit.entities.Country;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountryService {
    private List<Country> cache;

    public List<Country> getAll() {
        if (cache != null) return cache;
        List<Country> list = fetchFromRestCountries();
        if (list.isEmpty()) list = loadBundledFallback();
        cache = list;
        return cache;
    }

    private List<Country> fetchFromRestCountries() {
        try {
            HttpClient http = HttpClient.newHttpClient();
            String url = "https://restcountries.com/v3.1/all?fields=name,cca2";
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = res.body();
            if (res.statusCode() >= 200 && res.statusCode() < 300 && body != null && !body.isBlank()) {
                // naive regex extraction: "common":"...","cca2":"XX"
                Pattern p = Pattern.compile("\"common\"\\s*:\\s*\"([^\"]+)\"[\\s\\S]*?\"cca2\"\\s*:\\s*\"([A-Za-z]{2})\"");
                Matcher m = p.matcher(body);
                List<Country> out = new ArrayList<>();
                while (m.find()) {
                    String name = m.group(1);
                    String code = m.group(2).toUpperCase();
                    out.add(new Country(name, code));
                }
                out.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
                return out;
            }
        } catch (Exception ignored) {}
        return Collections.emptyList();
    }

    private List<Country> loadBundledFallback() {
        try {
            InputStream in = getClass().getResourceAsStream("/countries-fallback.csv");
            if (in == null) return minimalSet();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            List<Country> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) list.add(new Country(parts[1].trim(), parts[0].trim()));
            }
            list.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
            return list;
        } catch (Exception ignored) {}
        return minimalSet();
    }

    private List<Country> minimalSet() {
        List<Country> list = new ArrayList<>();
        // small safety net; real data will come from API
        list.add(new Country("France", "FR"));
        list.add(new Country("United States", "US"));
        list.add(new Country("United Kingdom", "GB"));
        list.add(new Country("Germany", "DE"));
        list.add(new Country("Canada", "CA"));
        list.add(new Country("Japan", "JP"));
        list.add(new Country("Tunisia", "TN"));
        Collections.sort(list, (a,b) -> a.getName().compareToIgnoreCase(b.getName()));
        return list;
    }
}
