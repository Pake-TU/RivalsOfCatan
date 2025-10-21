package model;

import model.effects.ActionCardEffectHandler;
import model.effects.CenterCardEffectHandler;
import model.effects.ExpansionCardEffectHandler;
import util.CardLoader;
import util.PlacementValidator;

import java.io.IOException;
import java.util.*;

/**
 * Represents a game card with its attributes and basic operations.
 * Refactored to follow SOLID principles - this class is now primarily a data model.
 * Game logic has been extracted to dedicated handler classes.
 */
public class Card implements Comparable<Card> {

    // ---------- Public fields (keep simple for the take-home) ----------
    public String name, theme, type, placement, cost, oneOf;
    public String victoryPoints, CP, SP, FP, PP, LP, KP, cardText;
    public String germanName, Requires, protectionOrRemoval;

    // Regions track "stored" resources by rotating; here we model it as an int (0..3)
    public int regionProduction = 0;
    // Regions use production die faces (1..6). 0 means "not a region" / unassigned.
    public int diceRoll = 0;

    // ---------- Global piles for the Basic set ----------
    public static Vector<Card> regions = new Vector<>();
    public static Vector<Card> roads = new Vector<>();
    public static Vector<Card> settlements = new Vector<>();
    public static Vector<Card> cities = new Vector<>();
    public static Vector<Card> events = new Vector<>();
    public static Vector<Card> drawStack1 = new Vector<>();
    public static Vector<Card> drawStack2 = new Vector<>();
    public static Vector<Card> drawStack3 = new Vector<>();
    public static Vector<Card> drawStack4 = new Vector<>();

    // ---------- Construction ----------
    public Card() {
    }

    public Card(String name, String theme, String type,
            String germanName, String placement,
            String oneOf, String cost,
            String victoryPoints, String CP, String SP, String FP,
            String PP, String LP, String KP, String Requires,
            String cardText, String protectionOrRemoval) {
        this.name = name;
        this.theme = theme;
        this.type = type;
        this.germanName = germanName;
        this.placement = placement;
        this.oneOf = oneOf;
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.CP = CP;
        this.SP = SP;
        this.FP = FP;
        this.PP = PP;
        this.LP = LP;
        this.KP = KP;
        this.Requires = Requires;
        this.cardText = cardText;
        this.protectionOrRemoval = protectionOrRemoval;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Card o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    // ---------- Helper methods ----------
    static boolean nmEquals(String a, String b) {
        return a != null && a.equalsIgnoreCase(b);
    }

    // Pop first card by name (case-insensitive) from a vector
    public static Card popCardByName(Vector<Card> cards, String name) {
        if (cards == null || name == null)
            return null;
        String target = name.trim();
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (c != null && c.name != null && c.name.trim().equalsIgnoreCase(target)) {
                return cards.remove(i);
            }
        }
        return null;
    }

    // Extract all cards whose public String field `attribute` equals `value`
    public static Vector<Card> extractCardsByAttribute(Vector<Card> cards, String attribute, String value) {
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

    // ---------- Loading ONLY the Basic set into piles ----------
    public static void loadBasicCards(String jsonPath) throws IOException {
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

    // Determine if region name matches what a booster affects
    // Delegated to PlacementValidator but kept here for backward compatibility
    public static boolean buildingBoostsRegion(String buildingName, String regionName) {
        return PlacementValidator.buildingBoostsRegion(buildingName, regionName);
    }

    // ---------- Main effect / placement entry ----------
    // Returns true if placed/applied; false if illegal placement
    // This method now delegates to specialized handler classes following SRP
    public boolean applyEffect(Player active, Player other, int row, int col) {
        String nm = (name == null ? "" : name);
        System.out.println("ApplyEffect: " + nm + " at (" + row + "," + col + ")");
        
        // 0) Early validation for occupied slot
        // Exception: Cities can be placed on Settlements to replace them
        if (active.getCard(row, col) != null && !nmEquals(nm, "City")) {
            active.sendMessage("That space is occupied.");
            return false;
        }

        // 1) Center cards: Road / Settlement / City
        if (nmEquals(nm, "Road") || nmEquals(nm, "Settlement") || nmEquals(nm, "City")) {
            return CenterCardEffectHandler.applyCenterCardEffect(this, active, other, row, col);
        }

        // 2) Regions: allow only in region rows (not center); we set default production=1
        if ("Region".equalsIgnoreCase(type)) {
            if (PlacementValidator.isCenterSlot(row)) {
                active.sendMessage("Regions must be placed above/below the center row.");
                return false;
            }
            if (regionProduction <= 0)
                regionProduction = 1;
            active.placeCard(row, col, this);
            return true;
        }

        // 3) Settlement/City Expansions (Buildings & Units)
        if (placement != null && placement.equalsIgnoreCase("Settlement/city")) {
            return ExpansionCardEffectHandler.applyExpansionEffect(this, active, other, row, col);
        }

        // 4) Pure action cards (Basic intro handful):
        if ("Action".equalsIgnoreCase(placement) || "Action".equalsIgnoreCase(type)) {
            return ActionCardEffectHandler.applyActionEffect(this, active, other, row, col);
        }

        // Fallback: accept placement (ugly default)
        active.placeCard(row, col, this);
        return true;
    }
}
