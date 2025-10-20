package controller;

import controller.interfaces.IGameManager;
import model.*;
import util.CostParser;
import java.util.*;

/**
 * Manages hand replenishment for players.
 */
public class ReplenishManager implements IGameManager {
    
    @Override
    public String getPhaseName() {
        return "Replenish";
    }

    /**
     * Replenish a player's hand to the target size.
     * @param p The player to replenish
     */
    public void replenish(Player p) {
        if (p.flags != null && p.flags.remove("NO_REPLENISH_ONCE")) {
            p.sendMessage("You cannot replenish your hand this turn (Fraternal Feuds).");
            return;
        } else {
            int handTarget = 3 + p.progressPoints;
            while (p.handSize() < handTarget) {
                p.sendMessage("PROMPT: Replenish - choose draw stack [1-4]:");
                int which = CostParser.parseInt(p.receiveMessage(), 1);
                Vector<Card> stack = stackBy(which);
                if (stack.isEmpty()) {
                    // advance circularly until any non-empty
                    int tries = 0;
                    do {
                        which = 1 + (which % 4);
                        stack = stackBy(which);
                        tries++;
                    } while (stack.isEmpty() && tries <= 4);
                    if (stack.isEmpty()) {
                        p.sendMessage("All stacks empty.");
                        break;
                    }
                }
                p.addToHand(stack.remove(0));
            }
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
