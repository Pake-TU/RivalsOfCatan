package controller.events;

import model.Card;
import model.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the Feud event card.
 * Player with strength advantage selects up to 3 opponent buildings, 
 * opponent chooses which one to remove.
 */
public class FeudEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        Player adv = hasStrengthAdvantage(players.get(0), players.get(1)) ? players.get(0)
                : hasStrengthAdvantage(players.get(1), players.get(0)) ? players.get(1)
                        : null;

        if (adv == null) {
            broadcast(players, "Feud: no strength advantage; nothing happens.");
            return;
        }

        Player opp = (adv == players.get(0)) ? players.get(1) : players.get(0);

        // Collect opponent buildings
        List<int[]> buildings = new ArrayList<>();
        for (int r = 0; r < opp.principality.size(); r++) {
            var row = opp.principality.get(r);
            for (int c = 0; c < row.size(); c++) {
                Card x = row.get(c);
                if (x != null && x.type != null && x.type.equalsIgnoreCase("Building")) {
                    buildings.add(new int[] { r, c });
                }
            }
        }
        if (buildings.isEmpty()) {
            broadcast(players, "Feud: opponent has no buildings.");
            return;
        }

        // Ask advantage player to pick up to 3 targets
        adv.sendMessage(
                "PROMPT: Feud - select up to 3 opponent building coordinates as 'r c;r c;r c'. Opponent board:\n"
                        + opp.printPrincipality());
        String line = adv.receiveMessage();
        List<int[]> picked = new ArrayList<>();
        try {
            for (String pair : line.split(";")) {
                String s = pair.trim();
                if (s.isEmpty())
                    continue;
                String[] rc = s.split("\\s+");
                int r = Integer.parseInt(rc[0]);
                int c = Integer.parseInt(rc[1]);
                Card x = getSafe(opp, r, c);
                if (x != null && x.type != null && x.type.equalsIgnoreCase("Building")) {
                    picked.add(new int[] { r, c });
                    if (picked.size() == 3)
                        break;
                }
            }
        } catch (Exception ignored) {
        }

        // Auto-fill from discovered buildings
        int k = 0;
        while (picked.size() < 3 && k < buildings.size()) {
            int[] bc = buildings.get(k++);
            boolean dup = false;
            for (int[] pc : picked)
                if (pc[0] == bc[0] && pc[1] == bc[1]) {
                    dup = true;
                    break;
                }
            if (!dup)
                picked.add(bc);
        }
        if (picked.isEmpty()) {
            broadcast(players, "Feud: no valid targets selected/found.");
            return;
        }

        // Opponent chooses which ONE to remove
        StringBuilder opts = new StringBuilder("PROMPT: Feud - choose which to remove (index 0..")
                .append(picked.size() - 1)
                .append("):\n");
        for (int i = 0; i < picked.size(); i++) {
            int r = picked.get(i)[0], c = picked.get(i)[1];
            Card x = getSafe(opp, r, c);
            opts.append("  [").append(i).append("] (").append(r).append(",").append(c).append(") ").append(x)
                    .append("\n");
        }
        opp.sendMessage(opts.toString());
        int choice = 0;
        try {
            choice = Integer.parseInt(opp.receiveMessage().trim());
        } catch (Exception ignored) {
        }
        if (choice < 0 || choice >= picked.size())
            choice = 0;
        int rr = picked.get(choice)[0], cc = picked.get(choice)[1];
        Card removed = opp.principality.get(rr).set(cc, null);
        broadcast(players, "Feud: removed " + (removed == null ? "unknown" : removed.name) + " from opponent at (" + rr + ","
                + cc + ").");
        returnBuildingToBottom(removed);
    }
    
    private boolean hasStrengthAdvantage(Player a, Player b) {
        return a.strengthPoints >= 3 && a.strengthPoints > b.strengthPoints;
    }
    
    private Card getSafe(Player p, int r, int c) {
        return p.getCard(r, c);
    }
    
    private void returnBuildingToBottom(Card bld) {
        if (bld == null)
            return;
        Card.drawStack1.add(bld);
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
