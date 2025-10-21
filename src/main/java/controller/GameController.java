package controller;

import model.*;
import java.util.*;

/**
 * GameController manages the main game loop and turn progression.
 * Follows MVC pattern by separating game logic from application entry point.
 * Implements Single Responsibility Principle - handles only game flow control.
 */
public class GameController {
    
    private final ProductionManager productionManager = new ProductionManager();
    private final ReplenishManager replenishManager = new ReplenishManager();
    private final ExchangeManager exchangeManager = new ExchangeManager();
    private final EventResolver eventResolver = new EventResolver();
    private final ActionManager actionManager = new ActionManager();
    private final Random rng = new Random();

    // Event die faces
    private static final int EV_BRIGAND = EventType.BRIGAND;
    private static final int EV_TRADE = EventType.TRADE;
    private static final int EV_CELEB = EventType.CELEBRATION;
    private static final int EV_PLENTY = EventType.PLENTIFUL_HARVEST;
    private static final int EV_EVENT_A = EventType.EVENT_A;
    private static final int EV_EVENT_B = EventType.EVENT_B;

    /**
     * Main gameplay loop.
     * Handles turn progression, dice rolling, events, actions, and win conditions.
     *
     * @param players List of players in the game
     */
    public void runGameLoop(List<Player> players) {
        int current = Math.random() < 0.5 ? 0 : 1; // random start
        // print the players principality and hand
        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            Player opponentPlayer = players.get((i + 1) % players.size());
            currentPlayer.sendMessage("Opponent's starting board:");
            currentPlayer.sendMessage(
                    "\t\t" + opponentPlayer.printPrincipality(currentPlayer).replace("\n", "\n\t\t"));
            currentPlayer.sendMessage("Your starting board:");
            currentPlayer.sendMessage(currentPlayer.printPrincipality(opponentPlayer));
            currentPlayer.sendMessage("Your starting hand:");
            currentPlayer.sendMessage(currentPlayer.printHand());
        }
        while (true) {
            Player active = players.get(current);
            Player other = players.get((current + 1) % players.size());

            // -------- Part 1: Roll Dice --------
            int eventFace = rollEventDie(active, players);
            int prodFace = rollProductionDie(active, players);

            if (eventFace == EV_BRIGAND) { // Brigand first, then production
                eventResolver.resolveEvent(eventFace, players, active, other);
                productionManager.applyProduction(prodFace, players, p -> opponentOf(p, players));
            } else { // production first, then event
                productionManager.applyProduction(prodFace, players, p -> opponentOf(p, players));
                eventResolver.resolveEvent(eventFace, players, active, other);
            }

            // print the players principality and hand
            for (int i = 0; i < players.size(); i++) {
                Player currentPlayer = players.get(i);
                Player opponentPlayer = players.get((i + 1) % players.size());
                currentPlayer.sendMessage("Opponent's board:");
                currentPlayer.sendMessage(
                        "\t\t" + opponentPlayer.printPrincipality(currentPlayer).replace("\n", "\n\t\t"));
                currentPlayer.sendMessage("Your board:");
                currentPlayer.sendMessage(currentPlayer.printPrincipality(opponentPlayer));
                currentPlayer.sendMessage("Your hand:");
                currentPlayer.sendMessage(currentPlayer.printHand());
            }

            // -------- Part 2: Action Phase (very small) --------
            actionManager.actionPhase(active, other, s -> broadcast(s, players));

            // -------- Part 3: Replenish Hand --------
            replenishManager.replenish(active);

            // -------- Part 4: Exchange (simplified) --------
            exchangeManager.exchangePhase(active, s -> broadcast(s, players));

            // -------- Part 5: Scoring & Win Check --------
            if (checkWinEndOfTurn(active, other, players))
                break;

            current = (current + 1) % players.size();
        }
    }

    private boolean checkWinEndOfTurn(Player active, Player other, List<Player> players) {
        int score = active.currentScoreAgainst(other);
        if (score >= 7) {
            broadcast("winner: Player " + players.indexOf(active)
                    + " wins with " + score + " VP (incl. advantage tokens)!", players);
            return true;
        }
        return false;
    }

    // ---------- Dice ----------
    private int rollEventDie(Player active, List<Player> players) {
        // Brigitta lets the player fix production die, not event die — but we keep the
        // hook simple
        int face = 1 + rng.nextInt(6);
        broadcast("[EventDie] -> " + face, players);
        return face;
    }

    private int rollProductionDie(Player active, List<Player> players) {
        int face = 1 + rng.nextInt(6);
        if (active.flags.contains("BRIGITTA")) {
            active.sendMessage("PROMPT: Brigitta active -  choose production die [1-6]:");
            try {
                int forced = Integer.parseInt(active.receiveMessage().trim());
                if (forced >= 1 && forced <= 6)
                    face = forced;
            } catch (Exception ignored) {
            }
            active.flags.remove("BRIGITTA");
        }
        broadcast("[ProductionDie] -> " + face, players);
        return face;
    }

    // ---------- Helper Methods ----------
    private Player opponentOf(Player p, List<Player> players) {
        return (p == players.get(0)) ? players.get(1) : players.get(0);
    }

    private void broadcast(String s, List<Player> players) {
        // send to each player
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
