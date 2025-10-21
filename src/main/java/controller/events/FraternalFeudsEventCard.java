package controller.events;

import model.Card;
import model.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the Fraternal Feuds event card.
 * Player with strength advantage removes up to 2 cards from opponent's hand,
 * and opponent cannot replenish next turn.
 */
public class FraternalFeudsEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        Player adv = hasStrengthAdvantage(players.get(0), players.get(1)) ? players.get(0)
                : hasStrengthAdvantage(players.get(1), players.get(0)) ? players.get(1)
                        : null;

        if (adv == null) {
            broadcast(players, "Fraternal Feuds: no strength advantage; nothing happens.");
            return;
        }
        Player opp = (adv == players.get(0)) ? players.get(1) : players.get(0);

        if (opp.hand.isEmpty()) {
            broadcast(players, "Fraternal Feuds: opponent hand empty.");
            return;
        }

        adv.sendMessage("PROMPT: Opponent hand:\n" + opp.printHand() + "Choose up to two indices (e.g., '2 5'):");
        String sel = adv.receiveMessage();
        Set<Integer> idxs = new HashSet<>();
        try {
            for (String tok : sel.trim().split("\\s+")) {
                int i = Integer.parseInt(tok);
                if (i >= 0 && i < opp.hand.size())
                    idxs.add(i);
                if (idxs.size() == 2)
                    break;
            }
        } catch (Exception ignored) {
        }

        // If insufficient/invalid, take first one or two
        if (idxs.isEmpty()) {
            idxs.add(0);
            if (opp.hand.size() > 1)
                idxs.add(1);
        }

        // Remove in descending order
        List<Integer> order = new ArrayList<>(idxs);
        Collections.sort(order, Collections.reverseOrder());
        for (int i : order) {
            Card rem = opp.hand.remove(i);
            returnBuildingToBottom(rem);
            broadcast(players, "Fraternal Feuds: returned '" + rem.name + "' to bottom of a draw stack.");
        }

        markSkipReplenishOnce(opp);
        broadcast(players, "Fraternal Feuds: opponent cannot replenish hand at the end of the next turn.");
    }
    
    private boolean hasStrengthAdvantage(Player a, Player b) {
        return a.strengthPoints >= 3 && a.strengthPoints > b.strengthPoints;
    }
    
    private void returnBuildingToBottom(Card bld) {
        if (bld == null)
            return;
        Card.drawStack1.add(bld);
    }
    
    private void markSkipReplenishOnce(Player p) {
        if (p.flags == null)
            p.flags = new HashSet<>();
        p.flags.add("NO_REPLENISH_ONCE");
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
