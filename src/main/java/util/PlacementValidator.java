package util;

import model.Card;
import model.Player;

/**
 * Validates card placement rules.
 * Follows Single Responsibility Principle - handles only placement validation logic.
 */
public class PlacementValidator {
    
    /**
     * Check if a row is the center slot.
     * By convention: inner/outer rows hold expansions & regions; middle row is center (roads/settlements/cities)
     * Only row 2 is allowed for center cards.
     */
    public static boolean isCenterSlot(int row) {
        return row == 2;
    }
    
    /**
     * Check if a position is above or below a Settlement or City.
     * This validates expansion card placement.
     */
    public static boolean isAboveOrBelowSettlementOrCity(Player player, int row, int col) {
        // Inner ring: ±1 from center settlement/city
        Card up1 = player.getCard(row - 1, col);
        Card down1 = player.getCard(row + 1, col);
        if (isSettlementOrCity(up1) || isSettlementOrCity(down1)) {
            return true;
        }
        
        // Outer ring allowed *only* if the inner slot is already filled (fill inner first)
        Card up2 = player.getCard(row - 2, col);
        Card down2 = player.getCard(row + 2, col);
        boolean outerOK = ((isSettlementOrCity(up2) && up1 != null) ||
                           (isSettlementOrCity(down2) && down1 != null));
        
        return outerOK;
    }
    
    /**
     * Check if a card is a Settlement or City.
     */
    public static boolean isSettlementOrCity(Card card) {
        if (card == null || card.name == null) {
            return false;
        }
        String name = card.name;
        return name.equalsIgnoreCase("Settlement") || name.equalsIgnoreCase("City");
    }
    
    /**
     * Check if a card name matches one of the given options (case-insensitive).
     */
    public static boolean nameMatches(Card card, String... options) {
        if (card == null || card.name == null) {
            return false;
        }
        for (String option : options) {
            if (card.name.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determine if a building boosts a specific region type.
     */
    public static boolean buildingBoostsRegion(String buildingName, String regionName) {
        if (buildingName == null || regionName == null) {
            return false;
        }
        if (buildingName.equalsIgnoreCase("Iron Foundry") && regionName.equalsIgnoreCase("Mountain")) {
            return true;
        }
        if (buildingName.equalsIgnoreCase("Grain Mill") && regionName.equalsIgnoreCase("Field")) {
            return true;
        }
        if (buildingName.equalsIgnoreCase("Lumber Camp") && regionName.equalsIgnoreCase("Forest")) {
            return true;
        }
        if (buildingName.equalsIgnoreCase("Brick Factory") && regionName.equalsIgnoreCase("Hill")) {
            return true;
        }
        if (buildingName.equalsIgnoreCase("Weaver's Shop") && regionName.equalsIgnoreCase("Pasture")) {
            return true;
        }
        return false;
    }
    
    /**
     * Check if a card is a Region card.
     */
    public static boolean isRegionCard(Card card) {
        return card != null && card.type != null && card.type.equalsIgnoreCase("Region");
    }
    
    /**
     * Check if a card is an Expansion card.
     */
    public static boolean isExpansionCard(Card card) {
        if (card == null) {
            return false;
        }
        String placement = (card.placement == null ? "" : card.placement.toLowerCase());
        return placement.contains("expansion");
    }
    
    /**
     * Normalize a row for adjacency checks.
     * Cards on outer rows (0 and 4) behave as if they're on inner rows (1 and 3).
     * 
     * @param row The row to normalize
     * @return The normalized row for adjacency purposes
     */
    public static int normalizeRowForAdjacency(int row) {
        if (row == 0) {
            return 1; // Outer top row behaves like inner top row
        } else if (row == 4) {
            return 3; // Outer bottom row behaves like inner bottom row
        }
        return row; // All other rows remain unchanged
    }
}
