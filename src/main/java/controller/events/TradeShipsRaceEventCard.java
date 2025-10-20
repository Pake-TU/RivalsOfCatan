package controller.events;

import model.Card;
import model.Player;
import java.util.List;

/**
 * Handles the Trade Ships Race event card.
 * Player with most trade ships gains 1 resource of choice.
 */
public class TradeShipsRaceEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        if (players.size() < 2) {
            return;
        }
        
        int c0 = countTradeShips(players.get(0));
        int c1 = countTradeShips(players.get(1));

        if (c0 == 0 && c1 == 0) {
            broadcast(players, "Trade Ships Race: no one owns trade ships.");
            return;
        }
        if (c0 > c1) {
            Player p = players.get(0);
            String res = p.validateAndPromptResource("Trade Ships Race - you have the most trade ships. Choose 1 resource");
            p.gainResource(res);
        } else if (c1 > c0) {
            Player p = players.get(1);
            String res = p.validateAndPromptResource("Trade Ships Race - you have the most trade ships. Choose 1 resource");
            p.gainResource(res);
        } else {
            if (c0 >= 1 && c1 >= 1) {
                for (Player p : players) {
                    String res = p.validateAndPromptResource("Trade Ships Race (tie) - choose 1 resource");
                    p.gainResource(res);
                }
            } else {
                broadcast(players, "Trade Ships Race: tie without both having ≥1 ship; no one receives a resource.");
            }
        }
    }
    
    private int countTradeShips(Player p) {
        int count = 0;
        for (int r = 0; r < p.principality.size(); r++) {
            var row = p.principality.get(r);
            for (int c = 0; c < row.size(); c++) {
                Card x = row.get(c);
                if (x == null)
                    continue;
                String t = x.type == null ? "" : x.type;
                String pl = x.placement == null ? "" : x.placement;
                if (t.toLowerCase().contains("trade ship") ||
                        (pl.toLowerCase().contains("settlement/city") && x.name != null
                                && x.name.toLowerCase().endsWith("ship"))) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
