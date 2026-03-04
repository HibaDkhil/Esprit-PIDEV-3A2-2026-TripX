package tn.esprit.entities;

public class Country {
    private final String name;
    private final String alpha2;

    public Country(String name, String alpha2) {
        this.name = name;
        this.alpha2 = alpha2;
    }

    public String getName() { return name; }
    public String getAlpha2() { return alpha2; }
    public String getFlagPngUrl(int w) {
        String code = alpha2 == null ? "" : alpha2.toLowerCase();
        int size = (w <= 20) ? 20 : (w <= 40 ? 40 : 80);
        return "https://flagcdn.com/w" + size + "/" + code + ".png";
    }
}
