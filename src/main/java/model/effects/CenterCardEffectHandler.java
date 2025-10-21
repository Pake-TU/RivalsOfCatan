package model.effects;

import model.Card;
import model.Player;
import util.PlacementValidator;

/**
 * Handles card effects for center cards (Roads, Settlements, Cities).
 */
public class CenterCardEffectHandler {
    
    /**
     * Apply effect for a center card (Road, Settlement, or City).
     * 
     * @return true if successfully placed, false otherwise
     */
    public static boolean applyCenterCardEffect(Card card, Player active, Player other, int row, int col) {
        String name = card.name == null ? "" : card.name;
        
        // Validate center slot
        if (!PlacementValidator.isCenterSlot(row)) {
            active.sendMessage("Roads/Settlements/Cities must go in the center row(s).");
            return false;
        }
        
        if (name.equalsIgnoreCase("Road")) {
            return applyRoadEffect(card, active, row, col);
        }
        
        if (name.equalsIgnoreCase("Settlement")) {
            return applySettlementEffect(card, active, row, col);
        }
        
        if (name.equalsIgnoreCase("City")) {
            return applyCityEffect(card, active, row, col);
        }
        
        return false;
    }
    
    private static boolean applyRoadEffect(Card card, Player active, int row, int col) {
        // Roads cannot be placed next to other roads (left or right)
        Card left = active.getCard(row, col - 1);
        Card right = active.getCard(row, col + 1);
        boolean hasLeftRoad = (left != null && left.name != null && left.name.equalsIgnoreCase("Road"));
        boolean hasRightRoad = (right != null && right.name != null && right.name.equalsIgnoreCase("Road"));
        
        if (hasLeftRoad || hasRightRoad) {
            active.sendMessage("Road cannot be placed next to another Road. Roads must be separated by Settlements or Cities.");
            return false;
        }
        
        active.placeCard(row, col, card);
        active.sendMessage("Built a Road.");
        // Expand board if we built at an edge
        active.expandAfterEdgeBuild(col);
        return true;
    }
    
    private static boolean applySettlementEffect(Card card, Player active, int row, int col) {
        // Settlement must be next to a Road (left or right)
        // AND there cannot be another Settlement or City directly adjacent (left or right)
        Card left = active.getCard(row, col - 1);
        Card right = active.getCard(row, col + 1);
        
        // Check if there's a settlement or city directly adjacent
        boolean hasAdjacentSettlementOrCity = PlacementValidator.isSettlementOrCity(left)
                || PlacementValidator.isSettlementOrCity(right);
        
        if (hasAdjacentSettlementOrCity) {
            active.sendMessage("Settlement cannot be placed directly next to another Settlement or City. A Road must be between them.");
            return false;
        }
        
        // Check if there's a road adjacent
        boolean hasRoad = (left != null && left.name != null && left.name.equalsIgnoreCase("Road"))
                || (right != null && right.name != null && right.name.equalsIgnoreCase("Road"));
        if (!hasRoad) {
            active.sendMessage("Settlement must be placed next to a Road.");
            return false;
        }
        
        active.placeCard(row, col, card);
        active.victoryPoints += 1;
        
        // Expand and capture the updated column
        col = active.expandAfterEdgeBuild(col);
        
        // Now place diagonals using the correct, updated col
        RegionPlacementHelper.placeTwoDiagonalRegions(active, row, col);
        
        active.lastSettlementRow = row;
        active.lastSettlementCol = col;
        return true;
    }
    
    private static boolean applyCityEffect(Card card, Player active, int row, int col) {
        // Must be on top of an existing settlement in the same slot (same row,col)
        Card under = active.getCard(row, col);
        if (!PlacementValidator.isSettlementOrCity(under) || !under.name.equalsIgnoreCase("Settlement")) {
            active.sendMessage("City must be placed on top of an existing Settlement (same slot).");
            return false;
        }
        
        // No need to check adjacency - the settlement already satisfied those rules
        // when it was placed, and we're just upgrading it
        active.placeCard(row, col, card);
        active.victoryPoints += 1; // city is 2VP total; settlement vp ignored here, we just add +1
        return true;
    }
}
