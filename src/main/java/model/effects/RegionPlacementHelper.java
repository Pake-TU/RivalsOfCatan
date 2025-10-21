package model.effects;

import model.Card;
import model.Player;
import model.ResourceType;

/**
 * Helper class for placing regions when a new settlement is built.
 */
public class RegionPlacementHelper {
    
    /**
     * Place two diagonal regions after a new settlement.
     * This method handles both normal and SCOUT-enhanced placements.
     */
    public static void placeTwoDiagonalRegions(Player active, int row, int col) {
        // Access the global regions vector through Card class
        // Note: This still depends on Card.regions static field
        // For full decoupling, would need to pass CardDeckManager
        if (Card.regions == null || Card.regions.size() < 2) {
            active.sendMessage("Region stack does not have enough cards.");
            return;
        }
        
        // Decide which side is the "open side" (the side without a road)
        int colMod = (active.getCard(row, col - 1) == null) ? -1 : 1;
        int sideCol = col + colMod;
        
        // Draw or choose 2 regions
        Card first, second;
        
        if (active.flags.contains("SCOUT_NEXT_SETTLEMENT")) {
            // SCOUT: let player pick two specific regions from the region stack by name or index
            active.sendMessage("PROMPT: SCOUT - Choose first region (name or index):");
            String s1 = active.receiveMessage();
            first = pickRegionFromStackByNameOrIndex(s1);
            if (first == null) {
                // fallback to top
                first = Card.regions.isEmpty() ? null : Card.regions.remove(0);
            }
            
            active.sendMessage("PROMPT: SCOUT - Choose second region (name or index):");
            String s2 = active.receiveMessage();
            second = pickRegionFromStackByNameOrIndex(s2);
            if (second == null) {
                second = Card.regions.isEmpty() ? null : Card.regions.remove(0);
            }
            
            if (first == null || second == null) {
                active.sendMessage("SCOUT: Region stack exhausted.");
                // still clear the flag to avoid leaking it
                active.flags.remove("SCOUT_NEXT_SETTLEMENT");
                return;
            }
        } else {
            // normal: take top two
            if (Card.regions.size() < 2) {
                active.sendMessage("Region stack does not have two cards.");
                return;
            }
            first = Card.regions.remove(0);
            second = Card.regions.remove(0);
        }
        
        // Tell the player which two we drew/selected
        active.sendMessage("New settlement regions drawn/selected:");
        active.sendMessage("  1) " + first.name + "   2) " + second.name);
        
        // Ask where to put the first one (top/bottom), second goes to the other
        active.sendMessage("PROMPT: Place FIRST region on " + (colMod == -1 ? "LEFT" : "RIGHT")
                + " side: TOP or BOTTOM? (T/B)");
        String choice = active.receiveMessage();
        boolean top = choice != null && choice.trim().toUpperCase().startsWith("T");
        row = 2; // center row
        int topRow = row - 1;
        int bottomRow = row + 1;
        
        if (top) {
            active.placeCard(topRow, sideCol, first);
            active.placeCard(bottomRow, sideCol, second);
        } else {
            active.placeCard(topRow, sideCol, second);
            active.placeCard(bottomRow, sideCol, first);
        }
        
        // SCOUT benefit is consumed now; clear the flag
        active.flags.remove("SCOUT_NEXT_SETTLEMENT");
    }
    
    /**
     * Helper: choose region by name or index from Card.regions
     * Accepts region name (e.g., "Forest"), resource type (e.g., "Lumber"), or index
     */
    private static Card pickRegionFromStackByNameOrIndex(String spec) {
        if (spec == null || spec.isBlank()) {
            return null;
        }
        spec = spec.trim();
        // try index
        try {
            int idx = Integer.parseInt(spec);
            if (idx >= 0 && idx < Card.regions.size()) {
                return Card.regions.remove(idx);
            }
        } catch (Exception ignored) {
        }
        // try by name (first match)
        for (int i = 0; i < Card.regions.size(); i++) {
            Card c = Card.regions.get(i);
            if (c != null && c.name != null && c.name.equalsIgnoreCase(spec)) {
                return Card.regions.remove(i);
            }
        }
        // try by resource type (e.g., "Lumber" for "Forest", "Brick" for "Hill")
        String regionName = ResourceType.resourceToRegion(spec);
        if (regionName != null && !"Any".equals(regionName)) {
            for (int i = 0; i < Card.regions.size(); i++) {
                Card c = Card.regions.get(i);
                if (c != null && c.name != null && c.name.equalsIgnoreCase(regionName)) {
                    return Card.regions.remove(i);
                }
            }
        }
        return null;
    }
}
