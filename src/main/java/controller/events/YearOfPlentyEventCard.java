package controller.events;

import model.Card;
import model.Player;
import java.util.List;

/**
 * Handles the Year of Plenty event card.
 * Adds resources to regions adjacent to Storehouse/Abbey.
 */
public class YearOfPlentyEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        for (Player p : players) {
            int added = 0;
            for (int r = 0; r < p.principality.size(); r++) {
                var row = p.principality.get(r);
                for (int c = 0; c < row.size(); c++) {
                    Card reg = row.get(c);
                    if (reg == null || !"Region".equalsIgnoreCase(reg.type))
                        continue;

                    int adj = countAdjStorehouseAbbey(p, r, c);
                    while (adj-- > 0) {
                        if (reg.regionProduction < 3) {
                            reg.regionProduction++;
                            added++;
                        }
                    }
                }
            }
            p.sendMessage("Year of Plenty: resources were added to your regions where adjacent to Storehouse/Abbey.");
        }
    }
    
    private int countAdjStorehouseAbbey(Player p, int rr, int cc) {
        int cnt = 0;
        Card up = getSafe(p, rr - 1, cc);
        Card down = getSafe(p, rr + 1, cc);
        if (up != null && up.name != null) {
            String n = up.name.toLowerCase();
            if (n.equals("storehouse") || n.equals("abbey"))
                cnt++;
        }
        if (down != null && down.name != null) {
            String n = down.name.toLowerCase();
            if (n.equals("storehouse") || n.equals("abbey"))
                cnt++;
        }
        return cnt;
    }
    
    private Card getSafe(Player p, int r, int c) {
        return p.getCard(r, c);
    }
}
