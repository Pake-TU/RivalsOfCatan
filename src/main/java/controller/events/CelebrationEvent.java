package controller.events;

import model.Player;
import java.util.List;

/**
 * Handles the Celebration event.
 * Player with most skill points gains 1 resource of choice (or both if tied).
 */
public class CelebrationEvent implements IEventHandler {
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Celebration");
        
        if (players.size() < 2) {
            return;
        }
        
        int aSP = players.get(0).skillPoints;
        int bSP = players.get(1).skillPoints;
        
        if (aSP == bSP) {
            for (Player p : players) {
                String res = p.validateAndPromptResource("Celebration - gain 1 resource of your choice");
                p.gainResource(res);
            }
        } else {
            Player winner = aSP > bSP ? players.get(0) : players.get(1);
            String res = winner.validateAndPromptResource("Celebration (you have most skill) - gain 1 resource of your choice");
            winner.gainResource(res);
        }
    }
    
    @Override
    public String getEventName() {
        return "Celebration";
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
