package controller.events;

import model.Player;
import java.util.List;

/**
 * Handles the Traveling Merchant event card.
 * Players can trade gold for resources (1 gold per resource, up to 2).
 */
public class TravelingMerchantEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        for (Player p : players) {
            int max = Math.min(2, p.getResourceCount("Gold"));
            if (max <= 0) {
                p.sendMessage("Traveling Merchant: not enough Gold to trade (need 1 per resource).");
                continue;
            }
            p.sendMessage(
                    "PROMPT: Traveling Merchant - you may take up to " + max + " resources (1 Gold each). How many (0.."
                            + max + ")?");
            int k = 0;
            try {
                k = Integer.parseInt(p.receiveMessage().trim());
            } catch (Exception ignored) {
            }
            if (k < 0)
                k = 0;
            if (k > max)
                k = max;

            for (int i = 0; i < k; i++) {
                if (!p.removeResource("Gold", 1)) {
                    p.sendMessage("No more Gold; stopping.");
                    break;
                }
                String res = p.validateAndPromptResource("Pick resource #" + (i + 1));
                p.gainResource(res);
            }
        }
    }
}
