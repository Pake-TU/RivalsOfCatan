package controller;

import controller.interfaces.IGameManager;
import model.*;
import java.util.*;

/**
 * Manages resource production logic for regions.
 * Handles production phase including marketplace and booster effects.
 */
public class ProductionManager implements IGameManager {
    
    @Override
    public String getPhaseName() {
        return "Production";
    }

    /**
     * Apply production based on the dice roll.
     * @param face The production die face (1-6)
     * @param players All players in the game
     * @param opponentOf Function to get opponent of a player
     */
    public void applyProduction(int face, List<Player> players, java.util.function.Function<Player, Player> opponentOf) {
        for (Player p : players) {
            // Marketplace extra check: if opponent has more regions matching face, p gets
            // +1 of matching type
            boolean hasMarketplace = p.flags.contains("MARKETPLACE");
            int pMatches = countFaceRegions(p, face);
            int oppMatches = countFaceRegions(opponentOf.apply(p), face);

            for (int r = 0; r < p.principality.size(); r++) {
                List<Card> row = p.principality.get(r);
                for (int c = 0; c < row.size(); c++) {
                    Card card = row.get(c);
                    if (card == null || !"Region".equalsIgnoreCase(card.type))
                        continue;
                    if (card.diceRoll != face)
                        continue;

                    // Base increase = 1
                    int inc = 1;
                    // Booster buildings adjacent (same row, at c-1 or c+1) add +1
                    if (hasAdjacentBoosterForRegion(p, r, c))
                        inc += 1;

                    card.regionProduction = Math.min(3, card.regionProduction + inc);
                }
            }

            // Marketplace: if opponent has strictly more face-regions than p, p may gain +1
            // of one of those face resources
            if (hasMarketplace && oppMatches > pMatches) {
                p.sendMessage("PROMPT: Marketplace - choose one resource produced on face " + face
                        + " to gain (e.g., Grain/Gold/Lumber):");
                String res = p.receiveMessage();
                p.gainResource(res);
            }
        }
    }

    private boolean hasAdjacentBoosterForRegion(Player p, int rr, int cc) {
        Card region = p.getCard(rr, cc);
        if (region == null)
            return false;
        
        // Check for boosters in same row
        Card left = p.getCard(rr, cc - 1);
        Card right = p.getCard(rr, cc + 1);
        if (isBoosting(left, region) || isBoosting(right, region)) {
            return true;
        }
        
        // Also check the corresponding outer/inner row for boosters
        // If we're on row 1, also check row 0 (outer top)
        // If we're on row 3, also check row 4 (outer bottom)
        int altRow = -1;
        if (rr == 1) {
            altRow = 0;
        } else if (rr == 3) {
            altRow = 4;
        }
        
        if (altRow >= 0) {
            Card altLeft = p.getCard(altRow, cc - 1);
            Card altRight = p.getCard(altRow, cc + 1);
            if (isBoosting(altLeft, region) || isBoosting(altRight, region)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isBoosting(Card maybeBuilding, Card region) {
        if (maybeBuilding == null)
            return false;
        if (!"Building".equalsIgnoreCase(maybeBuilding.type))
            return false;
        return Card.buildingBoostsRegion(maybeBuilding.name, region.name);
    }

    private int countFaceRegions(Player p, int face) {
        int n = 0;
        for (List<Card> row : p.principality)
            for (Card c : row)
                if (c != null && "Region".equalsIgnoreCase(c.type) && c.diceRoll == face)
                    n++;
        return n;
    }
}
