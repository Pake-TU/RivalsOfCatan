package controller.events;

import model.Player;
import java.util.List;

/**
 * Handles the Trade event.
 * Player with highest commerce points (and >=3 commerce) gains 1 resource of choice from bank.
 */
public class TradeEvent implements IEventHandler {
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Trade");
        
        // Find player with highest commerce points
        Player highestCommercePlayer = null;
        int highestCommerce = 0;
        
        for (Player p : players) {
            if (p.commercePoints > highestCommerce) {
                highestCommerce = p.commercePoints;
                highestCommercePlayer = p;
            }
        }
        
        // Only grant resource if highest commerce is at least 3
        if (highestCommercePlayer != null && highestCommerce >= 3) {
            String res = highestCommercePlayer.validateAndPromptResource("Trade Advantage - gain 1 resource of your choice");
            highestCommercePlayer.gainResource(res);
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
