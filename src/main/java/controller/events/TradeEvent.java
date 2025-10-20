package controller.events;

import model.Player;
import java.util.List;

/**
 * Handles the Trade event.
 * Players with Trade Advantage (>=3 commerce) gain 1 resource of choice from bank.
 */
public class TradeEvent implements IEventHandler {
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Trade");
        
        for (Player p : players) {
            if (p.commercePoints >= 3) {
                String res = p.validateAndPromptResource("Trade Advantage - gain 1 resource of your choice");
                p.gainResource(res);
            }
        }
    }
    
    @Override
    public String getEventName() {
        return "Trade";
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
