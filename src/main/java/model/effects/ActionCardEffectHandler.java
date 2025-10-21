package model.effects;

import model.Card;
import model.Player;
import util.PlacementValidator;

/**
 * Handles card effects for action cards.
 */
public class ActionCardEffectHandler {
    
    /**
     * Apply effect for an action card.
     * 
     * @return true if successfully applied, false otherwise
     */
    public static boolean applyActionEffect(Card card, Player active, Player other, int row, int col) {
        String name = card.name == null ? "" : card.name;
        
        // Merchant Caravan: "gain 2 of your choice by discarding any 2 resources"
        if (name.equalsIgnoreCase("Merchant Caravan")) {
            return applyMerchantCaravan(active);
        }
        
        if (name.equalsIgnoreCase("Scout")) {
            // Scout requires the player to be able to afford a settlement
            // Check if player has enough resources (BGLW = Brick, Grain, Lumber, Wool)
            if (active.getResourceCount("Brick") < 1 || 
                active.getResourceCount("Grain") < 1 ||
                active.getResourceCount("Lumber") < 1 ||
                active.getResourceCount("Wool") < 1) {
                active.sendMessage("Scout can only be played if you can afford a Settlement (Brick, Grain, Lumber, Wool).");
                return false;
            }
            // Only meaningful when used with a new settlement (Server stores lastSettlementRow/Col)
            active.flags.add("SCOUT_NEXT_SETTLEMENT");
            return true;
        }
        
        if (name.equalsIgnoreCase("Brigitta the Wise Woman")) {
            // Choose production die result before rolling; we store forced value in Server
            active.flags.add("BRIGITTA");
            return true;
        }
        
        // Discard 3 gold and take any 2 resources of your choice in return.
        if (name.equalsIgnoreCase("Goldsmith")) {
            return applyGoldsmith(active);
        }
        
        // Swap 2 of your own Regions OR 2 of your own Expansion cards.
        if (name.equalsIgnoreCase("Relocation")) {
            return applyRelocation(active);
        }
        
        // Default: treat as "+1 VP"
        active.victoryPoints += 1;
        active.sendMessage("Played " + name + ": +1 VP (default).");
        return true;
    }
    
    private static boolean applyMerchantCaravan(Player active) {
        if (active.totalAllResources() < 2) {
            active.sendMessage("You need at least 2 resources to play Merchant Caravan.");
            return false;
        }
        // Let player pick 2 to discard with validation, then 2 to gain with validation
        for (int i = 0; i < 2; i++) {
            active.promptAndRemoveResource("Type Discard resource #" + (i + 1));
        }
        for (int i = 0; i < 2; i++) {
            String g = active.validateAndPromptResource("Type Gain resource #" + (i + 1));
            active.gainResource(g);
        }
        return true;
    }
    
    private static boolean applyGoldsmith(Player active) {
        if (!active.removeResource("Gold", 3)) {
            active.sendMessage("Goldsmith: you need 3 Gold to play this.");
            return false;
        }
        active.sendMessage("Goldsmith: choose two resources to gain:");
        for (int i = 1; i <= 2; i++) {
            String g = active.validateAndPromptResource("Pick resource #" + i);
            active.gainResource(g);
        }
        return true;
    }
    
    private static boolean applyRelocation(Player active) {
        active.sendMessage(
                "PROMPT: Relocation - Type 'REGION' to swap two regions or 'EXP' to swap two expansions:");
        String pick = active.receiveMessage();
        boolean swapRegions = (pick != null && pick.trim().toUpperCase().startsWith("R"));
        boolean swapExp = (pick != null && pick.trim().toUpperCase().startsWith("E"));
        if (!swapRegions && !swapExp) {
            active.sendMessage("Relocation canceled (need REGION or EXP).");
            return false;
        }
        
        // Read two coordinates
        active.sendMessage("PROMPT: Enter first coordinate (row col):");
        int r1 = 0, c1 = 0;
        try {
            String[] t = active.receiveMessage().trim().split("\\s+");
            r1 = Integer.parseInt(t[0]);
            c1 = Integer.parseInt(t[1]);
        } catch (Exception e) {
            active.sendMessage("Invalid coordinate.");
            return false;
        }
        active.sendMessage("PROMPT: Enter second coordinate (row col):");
        int r2 = 0, c2 = 0;
        try {
            String[] t = active.receiveMessage().trim().split("\\s+");
            r2 = Integer.parseInt(t[0]);
            c2 = Integer.parseInt(t[1]);
        } catch (Exception e) {
            active.sendMessage("Invalid coordinate.");
            return false;
        }
        
        Card a = active.getCard(r1, c1);
        Card b = active.getCard(r2, c2);
        if (a == null || b == null) {
            active.sendMessage("Relocation: both positions must contain cards.");
            return false;
        }
        
        if (swapRegions) {
            if (!PlacementValidator.isRegionCard(a) || !PlacementValidator.isRegionCard(b)) {
                active.sendMessage("Relocation (Region): both cards must be Regions.");
                return false;
            }
            // target slots must be valid region slots (i.e., not center)
            if (PlacementValidator.isCenterSlot(r1) || PlacementValidator.isCenterSlot(r2)) {
                active.sendMessage("Relocation: regions must be outside center row.");
                return false;
            }
            // Swap without re-applying effects
            active.placeCard(r1, c1, b);
            active.placeCard(r2, c2, a);
            active.sendMessage("Relocation done (Regions swapped).");
            return true;
        } else { // swapExp
            if (!PlacementValidator.isExpansionCard(a) || !PlacementValidator.isExpansionCard(b)) {
                active.sendMessage("Relocation (Expansion): both cards must be expansions.");
                return false;
            }
            // Must still obey expansion placement for each target slot
            if (!PlacementValidator.isAboveOrBelowSettlementOrCity(active, r2, c2)
                    || !PlacementValidator.isAboveOrBelowSettlementOrCity(active, r1, c1)) {
                active.sendMessage("Relocation: target slot is not valid for an expansion.");
                return false;
            }
            active.placeCard(r1, c1, b);
            active.placeCard(r2, c2, a);
            active.sendMessage("Relocation done (Expansions swapped).");
            return true;
        }
    }
}
