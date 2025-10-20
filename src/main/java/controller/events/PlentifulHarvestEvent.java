package controller.events;

import model.Card;
import model.Player;
import java.util.List;

/**
 * Handles the Plentiful Harvest event.
 * Each player gains 1 resource of choice, with Toll Bridge bonus.
 */
public class PlentifulHarvestEvent implements IEventHandler {
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Plentiful Harvest: each player gains 1 of choice.");
        
        for (Player p : players) {
            String res = p.validateAndPromptResource("Plentiful Harvest - choose a resource");
            p.gainResource(res);
            
            // Toll Bridge: +2 Gold if you can store it (any gold field with <3)
            if (p.flags.contains("TOLLB")) {
                int add = grantGoldIfSpace(p, 2);
                if (add > 0)
                    p.sendMessage("Toll Bridge: +" + add + " Gold");
            }
        }
    }
    
    @Override
    public String getEventName() {
        return "Plentiful Harvest";
    }
    
    private int grantGoldIfSpace(Player p, int want) {
        int given = 0;
        for (int r = 0; r < p.principality.size(); r++) {
            List<Card> row = p.principality.get(r);
            if (row == null)
                continue;
            for (int c = 0; c < row.size(); c++) {
                if (given >= want)
                    break;
                Card card = row.get(c);
                if (card != null && "Gold Field".equalsIgnoreCase(card.name)) {
                    int can = Math.max(0, 3 - card.regionProduction);
                    int add = Math.min(can, want - given);
                    if (add > 0) {
                        card.regionProduction += add;
                        given += add;
                    }
                }
            }
        }
        return given;
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
