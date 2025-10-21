package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing game-related strings and costs.
 */
public class CostParser {
    /**
     * Maps single-letter cost codes to canonical resource names.
     * B=Brick, G=Grain, L=Lumber, W=Wool, O=Ore, A=Gold
     */
    public static String letterToResource(char ch) {
        switch (Character.toUpperCase(ch)) {
            case 'B':
                return "Brick";
            case 'G':
                return "Grain";
            case 'L':
                return "Lumber";
            case 'W':
                return "Wool";
            case 'O':
                return "Ore";
            case 'A':
                return "Gold";
            default:
                return null;
        }
    }

    /**
     * Parse a cost string (e.g., "LW", "AA") into a map of resource counts.
     * Accept strings with optional spaces or separators ("L,W", "A A")
     */
    public static Map<String, Integer> parseCost(String cost) {
        Map<String, Integer> m = new HashMap<>();
        if (cost == null)
            return m;

        for (int i = 0; i < cost.length(); i++) {
            char ch = cost.charAt(i);
            if (Character.isWhitespace(ch) || ch == ',' || ch == ';' || ch == '+')
                continue;
            String res = letterToResource(ch);
            if (res != null) {
                m.put(res, m.getOrDefault(res, 0) + 1);
            }
        }
        return m;
    }

    /**
     * Safely parse an integer from a string, returning default if parsing fails.
     */
    public static int parseInt(String s, int def) {
        try {
            if (s == null)
                return def;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
