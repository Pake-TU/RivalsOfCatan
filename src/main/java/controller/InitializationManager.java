package controller;

import controller.interfaces.IGameManager;
import model.*;
import java.util.*;

/**
 * Manages game initialization and principality setup.
 */
public class InitializationManager implements IGameManager {
    
    @Override
    public String getPhaseName() {
        return "Initialization";
    }

    /**
     * Initialize both players' principalities with starting regions and buildings.
     * @param players The list of players
     */
    public void initPrincipality(List<Player> players) {
        // Center row index = 2 in a 5x5
        int center = 2;

        // Two players' starting dice sets (Forest, Gold Field, Field, Hill, Pasture, Mountain)
        int[][] regionDice = { { 2, 1, 6, 3, 4, 5 }, { 3, 4, 5, 2, 1, 6 } };

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            // center basics
            p.placeCard(center, 1, Card.popCardByName(Card.settlements, "Settlement"));
            p.placeCard(center, 2, Card.popCardByName(Card.roads, "Road"));
            p.placeCard(center, 3, Card.popCardByName(Card.settlements, "Settlement"));

            // Regions in rows 1 and 3 (above/below)
            Card forest = Card.popCardByName(Card.regions, "Forest");
            forest.diceRoll = regionDice[i][0];
            forest.regionProduction = 1;
            Card gold = Card.popCardByName(Card.regions, "Gold Field");
            gold.diceRoll = regionDice[i][1];
            gold.regionProduction = 0;
            Card field = Card.popCardByName(Card.regions, "Field");
            field.diceRoll = regionDice[i][2];
            field.regionProduction = 1;
            Card hill = Card.popCardByName(Card.regions, "Hill");
            hill.diceRoll = regionDice[i][3];
            hill.regionProduction = 1;
            Card past = Card.popCardByName(Card.regions, "Pasture");
            past.diceRoll = regionDice[i][4];
            past.regionProduction = 1;
            Card mount = Card.popCardByName(Card.regions, "Mountain");
            mount.diceRoll = regionDice[i][5];
            mount.regionProduction = 1;

            p.placeCard(center - 1, 0, forest);
            p.placeCard(center - 1, 2, gold);
            p.placeCard(center - 1, 4, field);
            p.placeCard(center + 1, 0, hill);
            p.placeCard(center + 1, 2, past);
            p.placeCard(center + 1, 4, mount);
        }

        // Put remaining "fixed dice" regions back in region stack
        addBackExtraFixedRegions();
        Collections.shuffle(Card.regions);
    }

    private void addBackExtraFixedRegions() {
        // There are two of each of these cards, each with a fixed diceRoll:
        setTwoUndiced("Field", 3, 1);
        setTwoUndiced("Mountain", 4, 2);
        setTwoUndiced("Hill", 5, 1);
        setTwoUndiced("Forest", 6, 4);
        setTwoUndiced("Pasture", 6, 5);
        setTwoUndiced("Gold Field", 3, 2);

        // After assigning dice to remaining cards, shuffle the deck
        Collections.shuffle(Card.regions);
    }

    private void setTwoUndiced(String name, int d1, int d2) {
        Card c1 = findUndicedByName(Card.regions, name);
        if (c1 != null)
            c1.diceRoll = d1;
        Card c2 = findUndicedByName(Card.regions, name);
        if (c2 != null)
            c2.diceRoll = d2;
    }

    // Returns a card with diceRoll == 0, matching name, but DOES NOT remove it.
    private Card findUndicedByName(Vector<Card> deck, String name) {
        for (int i = 0; i < deck.size(); i++) {
            Card c = deck.get(i);
            if (c != null && name.equalsIgnoreCase(c.name) && c.diceRoll == 0) {
                return c;
            }
        }
        return null;
    }
}
