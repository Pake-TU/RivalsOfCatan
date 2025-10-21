package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Card;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Responsible for loading cards from JSON files.
 * Follows Single Responsibility Principle - handles only card loading logic.
 */
public class CardLoader {
    
    /**
     * Load cards from a JSON file, filtering by theme.
     * 
     * @param jsonPath Path to the JSON file (classpath or filesystem)
     * @param themeFilter Filter string to match in the theme field (case-insensitive)
     * @return Vector of loaded cards matching the theme filter
     * @throws IOException If the file cannot be read or parsed
     */
    public static Vector<Card> loadCards(String jsonPath, String themeFilter) throws IOException {
        Vector<Card> cards = new Vector<>();
        
        // Try to load from classpath first (for Maven), then from filesystem
        InputStream is = CardLoader.class.getClassLoader().getResourceAsStream(jsonPath);
        if (is == null) {
            // Fall back to filesystem (for backward compatibility)
            is = new java.io.FileInputStream(jsonPath);
        }
        
        try (InputStreamReader isr = new InputStreamReader(is)) {
            JsonElement root = JsonParser.parseReader(isr);
            if (!root.isJsonArray()) {
                throw new IOException("cards.json: expected top-level array");
            }
            JsonArray arr = root.getAsJsonArray();
            
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) {
                    continue;
                }
                JsonObject o = el.getAsJsonObject();
                String theme = getString(o, "theme");
                
                // Filter by theme if specified
                if (themeFilter != null && (theme == null || !theme.toLowerCase().contains(themeFilter.toLowerCase()))) {
                    continue;
                }
                
                int number = getInt(o, "number", 1);
                for (int i = 0; i < number; i++) {
                    Card card = new Card(
                        getString(o, "name"), theme, getString(o, "type"),
                        getString(o, "germanName"), getString(o, "placement"),
                        getString(o, "oneOf"), getString(o, "cost"),
                        getString(o, "victoryPoints"), getString(o, "CP"), getString(o, "SP"), getString(o, "FP"),
                        getString(o, "PP"), getString(o, "LP"), getString(o, "KP"), getString(o, "Requires"),
                        getString(o, "cardText"), getString(o, "protectionOrRemoval")
                    );
                    cards.add(card);
                }
            }
        }
        
        return cards;
    }
    
    /**
     * Helper to safely extract string from JSON object.
     */
    private static String getString(JsonObject o, String key) {
        if (!o.has(key)) {
            return null;
        }
        JsonElement e = o.get(key);
        return (e == null || e.isJsonNull()) ? null : e.getAsString();
    }
    
    /**
     * Helper to safely extract integer from JSON object with a default value.
     */
    private static int getInt(JsonObject o, String key, int defaultValue) {
        if (!o.has(key)) {
            return defaultValue;
        }
        try {
            return o.get(key).getAsInt();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
