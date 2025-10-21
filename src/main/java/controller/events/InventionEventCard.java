package controller.events;

import model.Player;
import java.util.List;

/**
 * Handles the Invention event card.
 * Players gain resources based on their progress points (max 2).
 */
public class InventionEventCard {
    
    public void resolve(List<Player> players, Player active, Player other) {
        for (Player p : players) {
            int times = Math.min(2, Math.max(0, p.progressPoints));
            if (times == 0) {
                p.sendMessage("Invention: you have no progress point buildings (max 2).");
                continue;
            }
            for (int i = 0; i < times; i++) {
                String res = p.validateAndPromptResource("Invention - gain resource #" + (i + 1) + " of your choice");
                p.gainResource(res);
            }
        }
    }
}
