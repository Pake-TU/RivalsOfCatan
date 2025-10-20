package controller.events;

import model.Card;
import model.Player;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the Brigand event.
 * Brigands attack players with more than 7 total resources, zeroing out Gold and Wool in affected regions.
 */
public class BrigandEvent implements IEventHandler {
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Brigand Attack");
        
        for (Player p : players) {
            int total = countAllResources(p, true);
            if (total > 7) {
                zeroGoldAndWool(p, true);
                p.sendMessage("Brigands! You lose all Gold & Wool in affected regions.");
            }
        }
    }
    
    @Override
    public String getEventName() {
        return "Brigand";
    }
    
    private int countAllResources(Player p, boolean excludeStorehouseAdj) {
        int total = 0;
        Set<String> excluded = excludeStorehouseAdj ? storehouseExcludedKeys(p) : Set.of();
        for (int r = 0; r < p.principality.size(); r++) {
            List<Card> row = p.principality.get(r);
            if (row == null)
                continue;
            for (int c = 0; c < row.size(); c++) {
                Card card = row.get(c);
                if (card == null)
                    continue;
                String key = r + ":" + c;
                if (excluded.contains(key))
                    continue;
                // Count resources from all region types
                if ("Region".equalsIgnoreCase(card.type)) {
                    total += Math.max(0, Math.min(3, card.regionProduction));
                }
            }
        }
        return total;
    }
    
    private void zeroGoldAndWool(Player p, boolean excludeStorehouseAdj) {
        Set<String> excluded = excludeStorehouseAdj ? storehouseExcludedKeys(p) : Set.of();
        for (int r = 0; r < p.principality.size(); r++) {
            List<Card> row = p.principality.get(r);
            if (row == null)
                continue;
            for (int c = 0; c < row.size(); c++) {
                Card card = row.get(c);
                if (card == null)
                    continue;
                String key = r + ":" + c;
                if (excluded.contains(key))
                    continue;
                // Zero out only Gold Field (produces Gold) and Pasture (produces Wool)
                if ("Gold Field".equalsIgnoreCase(card.name) || "Pasture".equalsIgnoreCase(card.name)) {
                    card.regionProduction = 0;
                }
            }
        }
    }
    
    private Set<String> storehouseExcludedKeys(Player p) {
        Set<String> out = new HashSet<>();
        for (int r = 0; r < p.principality.size(); r++) {
            List<Card> row = p.principality.get(r);
            for (int c = 0; c < row.size(); c++) {
                Card x = row.get(c);
                if (x != null && x.name != null && x.name.equalsIgnoreCase("Storehouse")) {
                    boolean belowCenter = nmAt(p.getCard(r + 1, c), "Settlement", "City")
                            || nmAt(p.getCard(r + 2, c), "City", "City");
                    int regionRow = belowCenter ? r - 1 : r + 1;
                    out.add(regionRow + ":" + (c - 1));
                    out.add(regionRow + ":" + (c + 1));
                }
            }
        }
        return out;
    }
    
    private static boolean nmAt(Card c, String a, String b) {
        if (c == null || c.name == null)
            return false;
        return c.name.equalsIgnoreCase(a) || c.name.equalsIgnoreCase(b);
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
