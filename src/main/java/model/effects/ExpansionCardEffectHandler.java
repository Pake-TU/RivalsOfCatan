package model.effects;

import model.Card;
import model.Player;
import util.CostParser;
import util.PlacementValidator;

/**
 * Handles card effects for expansion cards (Buildings and Units).
 */
public class ExpansionCardEffectHandler {
    
    /**
     * Apply effect for an expansion card (Building or Unit).
     * 
     * @return true if successfully placed, false otherwise
     */
    public static boolean applyExpansionEffect(Card card, Player active, Player other, int row, int col) {
        String name = card.name == null ? "" : card.name;
        
        // Validate placement above or below settlement/city
        if (!PlacementValidator.isAboveOrBelowSettlementOrCity(active, row, col)) {
            active.sendMessage("Expansion must be above/below a Settlement or City (fill inner ring first).");
            return false;
        }
        
        // one-of check (simple)
        if (card.oneOf != null && card.oneOf.trim().equalsIgnoreCase("1x")) {
            if (active.hasInPrincipality(name)) {
                active.sendMessage("You may only have one '" + name + "' in your principality.");
                return false;
            }
        }
        
        System.out.println("Passed placement checks for " + name);
        
        // Buildings that "double" adjacent regions when the number hits (enforced during production)
        if ("Building".equalsIgnoreCase(card.type)) {
            return applyBuildingEffect(card, active, other, row, col);
        }
        
        // Units
        if (card.type != null && card.type.contains("Unit")) {
            return applyUnitEffect(card, active, other, row, col);
        }
        
        return false;
    }
    
    private static boolean applyBuildingEffect(Card card, Player active, Player opponent, int row, int col) {
        String name = card.name == null ? "" : card.name;
        
        // Just place it. Production phase will check adjacency and apply +1 increment (cap 3).
        active.placeCard(row, col, card);
        System.out.println("Contained Building");
        
        // Handle special buildings
        if (name.equalsIgnoreCase("Marketplace")) {
            active.flags.add("MARKETPLACE");
        } else if (name.equalsIgnoreCase("Parish Hall")) {
            active.flags.add("PARISH");
        } else if (name.equalsIgnoreCase("Storehouse")) {
            active.flags.add("STOREHOUSE@" + row + "," + col);
        } else if (name.equalsIgnoreCase("Toll Bridge")) {
            active.flags.add("TOLLB");
        }
        
        // Add stats from the card (CP, SP, FP, PP, KP)
        addCardStats(card, active, opponent);
        return true;
    }
    
    private static boolean applyUnitEffect(Card card, Player active, Player opponent, int row, int col) {
        String name = card.name == null ? "" : card.name;
        
        System.out.println("Contained UNIT!!!!!");
        
        // Large Trade Ship: adjacency 2-for-1 between L/R regions (handled in Server)
        if (name.equalsIgnoreCase("Large Trade Ship")) {
            active.placeCard(row, col, card);
            active.flags.add("LTS@" + row + "," + col);
            addCardStats(card, active, opponent);
            return true;
        }
        
        // "Common" trade ships: 2:1 bank for specific resource (handled in Server)
        if (name.toLowerCase().endsWith(" ship")) {
            active.placeCard(row, col, card);
            String res = name.split("\\s+")[0]; // Brick/Gold/Grain/Lumber/Ore/Wool
            active.flags.add("2FOR1_" + res.toUpperCase());
            addCardStats(card, active, opponent);
            return true;
        }
        
        // Heroes: just add SP/FP/CP/etc.
        active.placeCard(row, col, card);
        addCardStats(card, active, opponent);
        return true;
    }
    
    /**
     * Helper to add card stats to player when card is played/placed.
     * Checks for advantage token changes and notifies players.
     */
    private static void addCardStats(Card card, Player player, Player opponent) {
        int sp = CostParser.parseInt(card.SP, 0);
        int fp = CostParser.parseInt(card.FP, 0);
        int cp = CostParser.parseInt(card.CP, 0);
        int pp = CostParser.parseInt(card.PP, 0);
        int kp = CostParser.parseInt(card.KP, 0);
        
        // Check advantage status BEFORE adding stats
        boolean playerHadTradeAdvantage = player.hasTradeTokenAgainst(opponent);
        boolean playerHadStrengthAdvantage = player.hasStrengthTokenAgainst(opponent);
        boolean opponentHadTradeAdvantage = opponent.hasTradeTokenAgainst(player);
        boolean opponentHadStrengthAdvantage = opponent.hasStrengthTokenAgainst(player);
        
        if (sp != 0) {
            player.skillPoints += sp;
        }
        if (fp != 0) {
            player.strengthPoints += fp;
        }
        if (cp != 0) {
            player.commercePoints += cp;
        }
        if (pp != 0) {
            player.progressPoints += pp;
        }
        if (kp != 0) {
            player.victoryPoints += kp;
        }
        
        // Check advantage status AFTER adding stats and notify of changes
        boolean playerHasTradeAdvantage = player.hasTradeTokenAgainst(opponent);
        boolean playerHasStrengthAdvantage = player.hasStrengthTokenAgainst(opponent);
        boolean opponentHasTradeAdvantage = opponent.hasTradeTokenAgainst(player);
        boolean opponentHasStrengthAdvantage = opponent.hasStrengthTokenAgainst(player);
        
        // Trade advantage changes
        if (!playerHadTradeAdvantage && playerHasTradeAdvantage) {
            player.sendMessage(">>> You gained the Trade Advantage! (CP: " + player.commercePoints + " vs " + opponent.commercePoints + ")");
            opponent.sendMessage(">>> Opponent gained the Trade Advantage! (Their CP: " + player.commercePoints + " vs yours: " + opponent.commercePoints + ")");
            if (opponentHadTradeAdvantage) {
                opponent.sendMessage(">>> You lost the Trade Advantage!");
            }
        } else if (playerHadTradeAdvantage && !playerHasTradeAdvantage) {
            player.sendMessage(">>> You lost the Trade Advantage!");
            if (opponentHasTradeAdvantage) {
                opponent.sendMessage(">>> You gained the Trade Advantage!");
            }
        } else if (opponentHadTradeAdvantage && !opponentHasTradeAdvantage) {
            // Opponent lost advantage without player gaining it (e.g., player caught up to tie)
            opponent.sendMessage(">>> You lost the Trade Advantage!");
        }
        
        // Strength advantage changes
        if (!playerHadStrengthAdvantage && playerHasStrengthAdvantage) {
            player.sendMessage(">>> You gained the Strength Advantage! (FP: " + player.strengthPoints + " vs " + opponent.strengthPoints + ")");
            opponent.sendMessage(">>> Opponent gained the Strength Advantage! (Their FP: " + player.strengthPoints + " vs yours: " + opponent.strengthPoints + ")");
            if (opponentHadStrengthAdvantage) {
                opponent.sendMessage(">>> You lost the Strength Advantage!");
            }
        } else if (playerHadStrengthAdvantage && !playerHasStrengthAdvantage) {
            player.sendMessage(">>> You lost the Strength Advantage!");
            if (opponentHasStrengthAdvantage) {
                opponent.sendMessage(">>> You gained the Strength Advantage!");
            }
        } else if (opponentHadStrengthAdvantage && !opponentHasStrengthAdvantage) {
            // Opponent lost advantage without player gaining it (e.g., player caught up to tie)
            opponent.sendMessage(">>> You lost the Strength Advantage!");
        }
    }
}
