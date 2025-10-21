package model;

import util.CardLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

/**
 * Manages the global card decks and piles for the game.
 * Follows Single Responsibility Principle - handles only deck management logic.
 */
public class CardDeckManager {
    
    // Global piles for the Basic set
    private Vector<Card> regions = new Vector<>();
    private Vector<Card> roads = new Vector<>();
    private Vector<Card> settlements = new Vector<>();
    private Vector<Card> cities = new Vector<>();
    private Vector<Card> events = new Vector<>();
    private Vector<Card> drawStack1 = new Vector<>();
    private Vector<Card> drawStack2 = new Vector<>();
    private Vector<Card> drawStack3 = new Vector<>();
    private Vector<Card> drawStack4 = new Vector<>();
    
    /**
     * Load the Basic set cards from the JSON file and organize them into piles.
     * 
     * @param jsonPath Path to the cards JSON file
     * @throws IOException If the file cannot be read or parsed
     */
    public void loadBasicCards(String jsonPath) throws IOException {
        Vector<Card> allBasic = CardLoader.loadCards(jsonPath, "basic");
        
        // Split into piles we care about
        // Center cards
        roads = extractCardsByAttribute(allBasic, "name", "Road");
        settlements = extractCardsByAttribute(allBasic, "name", "Settlement");
        cities = extractCardsByAttribute(allBasic, "name", "City");
        
        // Regions: "type" == "Region"
        regions = extractCardsByAttribute(allBasic, "type", "Region");
        
        // Events
        events = extractCardsByAttribute(allBasic, "placement", "Event");
        // Place Yule 4th from bottom per cheat sheet
        Card yule = popCardByName(events, "Yule");
        Collections.shuffle(events);
        if (yule != null && events.size() >= 3) {
            events.add(Math.max(0, events.size() - 3), yule);
        }
        
        // Remaining "draw stack" cards (action/expansion/units)
        Collections.shuffle(allBasic);
        int stackSize = 9; // Intro game
        drawStack1 = new Vector<>(allBasic.subList(0, Math.min(stackSize, allBasic.size())));
        drawStack2 = new Vector<>(allBasic.subList(Math.min(stackSize, allBasic.size()),
                Math.min(2 * stackSize, allBasic.size())));
        drawStack3 = new Vector<>(allBasic.subList(Math.min(2 * stackSize, allBasic.size()),
                Math.min(3 * stackSize, allBasic.size())));
        drawStack4 = new Vector<>(allBasic.subList(Math.min(3 * stackSize, allBasic.size()),
                Math.min(4 * stackSize, allBasic.size())));
    }
    
    /**
     * Extract all cards whose public String field `attribute` equals `value`.
     */
    private Vector<Card> extractCardsByAttribute(Vector<Card> cards, String attribute, String value) {
        Vector<Card> out = new Vector<>();
        try {
            java.lang.reflect.Field f = Card.class.getField(attribute);
            for (int i = cards.size() - 1; i >= 0; i--) {
                Card c = cards.get(i);
                Object v = f.get(c);
                if (v != null && String.valueOf(v).equalsIgnoreCase(value)) {
                    out.add(0, cards.remove(i));
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }
    
    /**
     * Pop first card by name (case-insensitive) from a vector.
     */
    private Card popCardByName(Vector<Card> cards, String name) {
        if (cards == null || name == null) {
            return null;
        }
        String target = name.trim();
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (c != null && c.name != null && c.name.trim().equalsIgnoreCase(target)) {
                return cards.remove(i);
            }
        }
        return null;
    }
    
    // Getters for the various piles
    public Vector<Card> getRegions() {
        return regions;
    }
    
    public Vector<Card> getRoads() {
        return roads;
    }
    
    public Vector<Card> getSettlements() {
        return settlements;
    }
    
    public Vector<Card> getCities() {
        return cities;
    }
    
    public Vector<Card> getEvents() {
        return events;
    }
    
    public Vector<Card> getDrawStack1() {
        return drawStack1;
    }
    
    public Vector<Card> getDrawStack2() {
        return drawStack2;
    }
    
    public Vector<Card> getDrawStack3() {
        return drawStack3;
    }
    
    public Vector<Card> getDrawStack4() {
        return drawStack4;
    }
}
