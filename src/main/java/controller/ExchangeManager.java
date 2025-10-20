package controller;

import controller.interfaces.IGameManager;
import model.*;
import util.CostParser;
import java.util.*;

/**
 * Manages card exchange phase.
 */
public class ExchangeManager implements IGameManager {
    
    @Override
    public String getPhaseName() {
        return "Exchange";
    }

    /**
     * Execute the exchange phase for a player.
     * @param p The player
     * @param broadcast Function to broadcast messages to all players
     */
    public void exchangePhase(Player p, java.util.function.Consumer<String> broadcast) {
        int limit = 3 + p.progressPoints;
        if (p.handSize() < limit) {
            broadcast.accept("Exchange: hand below limit; skipping.");
            return;
        }

        p.sendMessage("PROMPT: Exchange a card? (Y/N)");
        String ans = p.receiveMessage();
        if (ans == null || !ans.trim().toUpperCase().startsWith("Y"))
            return;

        p.sendMessage("PROMPT: Enter card name to put under a stack:");
        String nm = p.receiveMessage();
        Card chosen = p.removeFromHandByName(nm);
        if (chosen == null) {
            p.sendMessage("Not in hand.");
            return;
        }

        p.sendMessage("PROMPT: Choose stack [1-4] to put it under:");
        int st = CostParser.parseInt(p.receiveMessage(), 1);
        Vector<Card> stack = stackBy(st);
        stack.add(chosen);

        boolean hasParish = p.flags.contains("PARISH");
        int searchCost = hasParish ? 1 : 2;

        p.sendMessage("PROMPT: Choose Random draw (R) or Search (S, costs " + searchCost + " any)?");
        String mode = p.receiveMessage();
        if (mode != null && mode.trim().toUpperCase().startsWith("S")) {
            // Pay 1 (with Parish) or 2 (normal) resources of the player's choice
            if (p.totalAllResources() < searchCost) {
                p.sendMessage("Not enough resources to search.");
                return;
            }
            for (int i = 0; i < searchCost; i++) {
                p.sendMessage("PROMPT: Discard resource #" + (i + 1) + " [Brick|Grain|Lumber|Wool|Ore|Gold]:");
                p.removeResource(p.receiveMessage(), 1);
            }

            if (stack.isEmpty()) {
                p.sendMessage("That stack is empty.");
                return;
            }
            p.sendMessage("Stack contains (top..bottom):");
            for (Card c : stack)
                p.sendMessage(" - " + c.name);
            p.sendMessage("PROMPT: Type exact name to take:");
            String take = p.receiveMessage();
            for (int i = 0; i < stack.size(); i++) {
                if (stack.get(i).name.equalsIgnoreCase(take)) {
                    p.addToHand(stack.remove(i));
                    return;
                }
            }
            p.sendMessage("Not found; no card taken.");
        } else {
            // Random draw (top of chosen stack)
            if (stack.isEmpty()) {
                p.sendMessage("That stack is empty.");
                return;
            }
            p.addToHand(stack.remove(0));
        }
    }

    private Vector<Card> stackBy(int n) {
        switch (n) {
            case 1:
                return Card.drawStack1;
            case 2:
                return Card.drawStack2;
            case 3:
                return Card.drawStack3;
            case 4:
                return Card.drawStack4;
        }
        return Card.drawStack1;
    }
}
