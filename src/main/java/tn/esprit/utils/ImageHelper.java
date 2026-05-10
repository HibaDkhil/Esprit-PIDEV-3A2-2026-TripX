package tn.esprit.utils;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class ImageHelper {
    private static final Logger LOGGER = Logger.getLogger(ImageHelper.class.getName());
    
    // Extensions to try in order of preference
    private static final String[] EXTENSIONS = {".jpg", ".jfif", ".png", ".jpeg", ".webp"};

    /**
     * Attempts to load an image from the classpath based on a category and name.
     * Searches in /images/{category}/
     * 
     * @param category The subfolder in /images/ (e.g., "destinations" or "activities")
     * @param name The name of the item (e.g., "Paris" or "Hiking")
     * @return An Image object if found, null otherwise
     */
    public static Image loadImage(String category, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String baseName = name.trim().toLowerCase();
        // Also try original case just in case
        String originalName = name.trim();

        // Search patterns:
        // 1. /images/{category}/{lowercase-name}.{ext}
        // 2. /images/{category}/{original-name}.{ext}
        
        for (String ext : EXTENSIONS) {
            // Try lowercase
            Image img = tryLoad(String.format("/images/%s/%s%s", category, baseName, ext));
            if (img != null) return img;
            
            // Try original name
            if (!originalName.equals(baseName)) {
                img = tryLoad(String.format("/images/%s/%s%s", category, originalName, ext));
                if (img != null) return img;
            }
        }

        LOGGER.info("No image found for " + category + ": " + name);
        return null;
    }

    /**
     * Attempts to find the image URL for an item.
     * Useful for JavaFX background loading: new Image(url, true)
     */
    public static String getImageUrl(String category, String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String baseName = name.trim().toLowerCase();
        String originalName = name.trim();

        for (String ext : EXTENSIONS) {
            String path = String.format("/images/%s/%s%s", category, baseName, ext);
            URL url = ImageHelper.class.getResource(path);
            if (url != null) return url.toExternalForm();

            if (!originalName.equals(baseName)) {
                path = String.format("/images/%s/%s%s", category, originalName, ext);
                url = ImageHelper.class.getResource(path);
                if (url != null) return url.toExternalForm();
            }
        }
        return null;
    }

    private static Image tryLoad(String path) {
        try {
            URL url = ImageHelper.class.getResource(path);
            if (url != null) {
                return new Image(url.toExternalForm(), true); // Load in background
            }
        } catch (Exception e) {
            // Silently ignore failures for specific paths
        }
        return null;
    }
}
