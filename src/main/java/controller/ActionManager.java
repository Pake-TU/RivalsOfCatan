package controller;

import controller.interfaces.IGameManager;
import model.*;
import util.CostParser;
import java.util.*;

/**
 * Manages the action phase where players can trade resources, play cards, and build.
 * Handles all action commands including TRADE3, TRADE2, LTS (Large Trade Ship), and PLAY.
 */
public class ActionManager implements IGameManager {
    
    @Override
    public String getPhaseName() {
        return "Action";
    }

    /**
     * Execute the action phase for the active player.
     * @param active The active player
     * @param other The opponent player
     * @param broadcast Function to broadcast messages to all players
     */
    public void actionPhase(Player active, Player other, java.util.function.Consumer<String> broadcast) {
        boolean done = false;
        active.sendMessage("Opponent's board:");
        active.sendMessage("\t\t" + other.printPrincipality(active).replace("\n", "\n\t\t"));
        while (!done) {
            active.sendMessage("Your board:");
            active.sendMessage(active.printPrincipality(other));
            active.sendMessage("Your hand:");
            active.sendMessage(active.printHand());
            active.sendMessage("Action Phase:");
            active.sendMessage("  TRADE3 <get> <give>     — bank 3:1 ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            active.sendMessage(
                    "  TRADE2 <get> <Res>      — if you have a 2:1 ship for <Res> ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            active.sendMessage(
                    "  LTS <L|R> <2from> <1to> — Large Trade Ship adjacent trade (left/right side) ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            // Allow PLAY to play cards from hand or center cards
            String play = "  PLAY <cardName> | <id>  — play a card from hand / play center card: ";

            // Add Center card options that are actually available
            ArrayList<String> buildBits = new ArrayList<>();
            if (!Card.roads.isEmpty()) {
                String cost = Card.roads.get(0).cost == null ? "-" : Card.roads.get(0).cost;
                buildBits.add("ROAD(" + cost + ")");
            }
            if (!Card.settlements.isEmpty()) {
                String cost = Card.settlements.get(0).cost == null ? "-" : Card.settlements.get(0).cost;
                buildBits.add("SETTLEMENT(" + cost + ")");
            }
            if (!Card.cities.isEmpty()) {
                String cost = Card.cities.get(0).cost == null ? "-" : Card.cities.get(0).cost;
                buildBits.add("CITY(" + cost + ")");
            }
            play += String.join(", ", buildBits);
            active.sendMessage(play);
            active.sendMessage("  END                     — finish action phase");
            active.sendMessage("PROMPT: make your choice: ");
            String cmd = active.receiveMessage();
            if (cmd == null)
                cmd = "END";
            String up = cmd.trim().toUpperCase(Locale.ROOT);

            if (up.startsWith("TRADE3")) {
                String[] parts = cmd.trim().split("\\s+");
                if (parts.length >= 3) {
                    String get = parts[1];
                    String give = parts[2];
                    if (active.getResourceCount(give) >= 3) {
                        active.removeResource(give, 3);
                        active.gainResource(get);
                        broadcast.accept("Trade 3:1 -> +1 " + get);
                    } else
                        active.sendMessage("Not enough " + give + " to trade 3:1.");
                } else
                    active.sendMessage("Usage: TRADE3 <get> <give> ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            } else if (up.startsWith("TRADE2")) {
                // Requires a flag 2FOR1_<RES>
                String[] parts = cmd.trim().split("\\s+");
                if (parts.length >= 3) {
                    String get = parts[1];
                    String from = parts[2].toUpperCase();
                    if (active.flags.contains("2FOR1_" + from)) {
                        if (active.getResourceCount(from) >= 2) {
                            active.removeResource(from, 2);
                            active.gainResource(get);
                            broadcast.accept("Trade 2:1 (" + from + " ship) -> +1 " + get);
                        } else
                            active.sendMessage("Not enough " + from + " to trade 2:1.");
                    } else
                        active.sendMessage("You don't have a 2:1 ship for " + from + ".");
                } else
                    active.sendMessage("Usage: TRADE2 <get> <give> ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            } else if (up.startsWith("LTS")) {
                // LTS <L|R> <two-from> <one-to>
                String[] parts = cmd.trim().split("\\s+");
                if (parts.length >= 4) {
                    String side = parts[1].toUpperCase(); // L or R
                    String twoFrom = parts[2];
                    String oneTo = parts[3];
                    if (applyLTS(active, side, twoFrom, oneTo))
                        broadcast.accept("LTS: traded 2 " + twoFrom + " for 1 " + oneTo + " on the "
                                + (side.startsWith("L") ? "LEFT" : "RIGHT"));
                    else
                        active.sendMessage("LTS trade invalid here.");
                } else
                    active.sendMessage("Usage: LTS <L|R> <2from> <1to> ([Brick|Grain|Lumber|Wool|Ore|Gold])");
            } else if (up.startsWith("PLAY")) {
                // PLAY <cardName>|<id>
                String[] parts = cmd.trim().split("\\s+", 2);
                if (parts.length < 2) {
                    active.sendMessage("Usage: PLAY <cardName> | <id>");
                    continue;
                }
                String spec = parts[1].trim();

                // ---------- 1) Center cards from piles: Road / Settlement / City ----------
                if (spec.equalsIgnoreCase("Road") || spec.equalsIgnoreCase("Settlement")
                        || spec.equalsIgnoreCase("City")) {
                    Vector<Card> pile = null;
                    if (spec.equalsIgnoreCase("Road"))
                        pile = Card.roads;
                    else if (spec.equalsIgnoreCase("Settlement"))
                        pile = Card.settlements;
                    else if (spec.equalsIgnoreCase("City"))
                        pile = Card.cities;

                    if (pile == null || pile.isEmpty()) {
                        active.sendMessage("No " + spec + " cards left in the pile.");
                        continue;
                    }

                    // Peek (do not remove yet)
                    Card proto = pile.firstElement();

                    // Check & pay cost first (do NOT mutate piles yet)
                    if (!payCost(active, proto.cost)) {
                        active.sendMessage("Can't afford cost: " + (proto.cost == null ? "-" : proto.cost));
                        continue;
                    }

                    // Ask coordinates and attempt placement
                    active.sendMessage("PROMPT: Enter placement coordinates as: ROW COL");
                    int row = -1, col = -1;
                    try {
                        String[] rc = active.receiveMessage().trim().split("\\s+");
                        row = Integer.parseInt(rc[0]);
                        col = Integer.parseInt(rc[1]);
                    } catch (Exception e) {
                        active.sendMessage("Invalid coordinates. Use: ROW COL (e.g., 2 3)");
                        refundCost(active, proto.cost);
                        continue;
                    }

                    boolean ok = proto.applyEffect(active, other, row, col);
                    if (!ok) {
                        active.sendMessage("Illegal placement/effect; refunding cost.");
                        refundCost(active, proto.cost);
                        continue;
                    }

                    // Success → remove from pile now
                    pile.remove(0);
                    broadcast.accept("Built " + spec + " at (" + row + "," + col + ")");
                    continue;
                }

                // ---------- 2) Cards from the player's HAND ----------
                // Resolve by index or name
                Card c = findCardInHand(active, spec);
                if (c == null) {
                    active.sendMessage("No such card in hand: " + spec);
                    continue;
                }

                // Check & pay cost (only now)
                if (!payCost(active, c.cost)) {
                    active.sendMessage("Can't afford cost: " + (c.cost == null ? "-" : c.cost));
                    continue;
                }

                boolean isAction = (c.type != null && c.type.toLowerCase().contains("action"));
                boolean ok;

                if (isAction) {
                    // Action cards: no placement
                    ok = c.applyEffect(active, other, -1, -1);
                    if (!ok) {
                        active.sendMessage("Action could not be resolved; refunding cost.");
                        refundCost(active, c.cost);
                        continue;
                    }
                    // Success → remove the specific instance from hand
                    active.hand.remove(c);
                    broadcast.accept("Current player played action " + c.name);
                } else {
                    // Non-action: needs placement
                    active.sendMessage("PROMPT: Enter placement coordinates as: ROW COL");
                    int row = -1, col = -1;
                    try {
                        String[] rc = active.receiveMessage().trim().split("\\s+");
                        row = Integer.parseInt(rc[0]);
                        col = Integer.parseInt(rc[1]);
                    } catch (Exception e) {
                        active.sendMessage("Invalid coordinates. Use: ROW COL (e.g., 2 3)");
                        refundCost(active, c.cost);
                        continue;
                    }

                    ok = c.applyEffect(active, other, row, col);
                    if (!ok) {
                        active.sendMessage("Illegal placement/effect; refunding cost.");
                        refundCost(active, c.cost);
                        continue;
                    }

                    // Success → remove the specific instance from hand
                    active.hand.remove(c);
                    broadcast.accept("Current player played " + c.name + " at (" + row + "," + col + ")");
                }
            } else if (up.startsWith("END")) {
                done = true;
            } else {
                active.sendMessage("Unknown command.");
            }
        }
    }

    private Card findCardInHand(Player p, String spec) {
        if (spec == null)
            return null;
        spec = spec.trim();

        // Numeric index?
        try {
            int idx = Integer.parseInt(spec);
            if (idx >= 0 && idx < p.hand.size())
                return p.hand.get(idx);
        } catch (NumberFormatException ignored) {
        }

        // Exact name match
        for (Card c : p.hand) {
            if (c != null && c.name != null && c.name.equalsIgnoreCase(spec))
                return c;
        }
        // Prefix fallback
        String lower = spec.toLowerCase();
        for (Card c : p.hand) {
            if (c != null && c.name != null && c.name.toLowerCase().startsWith(lower))
                return c;
        }
        return null;
    }

    private boolean payCost(Player p, String cost) {
        if (cost == null || cost.isBlank())
            return true;
        Map<String, Integer> need = CostParser.parseCost(cost);
        for (var e : need.entrySet()) {
            if (p.getResourceCount(e.getKey()) < e.getValue())
                return false;
        }
        for (var e : need.entrySet()) {
            p.removeResource(e.getKey(), e.getValue());
        }
        return true;
    }

    private void refundCost(Player p, String cost) {
        if (cost == null || cost.isBlank())
            return;
        Map<String, Integer> need = CostParser.parseCost(cost);
        for (var e : need.entrySet())
            p.setResourceCount(e.getKey(), p.getResourceCount(e.getKey()) + e.getValue());
    }

    // Large Trade Ship trade: side L/R relative to a placed LTS@row,col
    private boolean applyLTS(Player p, String side, String twoFrom, String oneTo) {
        // Find any LTS flag; for simplicity use the first one
        int ltsRow = -1, ltsCol = -1;
        for (String f : p.flags) {
            if (f.startsWith("LTS@")) {
                String[] rc = f.substring(4).split(",");
                try {
                    ltsRow = Integer.parseInt(rc[0]);
                    ltsCol = Integer.parseInt(rc[1]);
                } catch (Exception ignored) {
                }
                break;
            }
        }
        if (ltsRow < 0)
            return false;

        // Regions on that side are at (ltsRow, ltsCol-1) and (ltsRow, ltsCol+1)
        int takeCol = side.startsWith("L") ? ltsCol - 1 : ltsCol + 1;
        Card fromRegion = getSafe(p, ltsRow, takeCol);
        Card toRegion = getSafe(p, ltsRow, (side.startsWith("L") ? ltsCol + 1 : ltsCol - 1));

        if (fromRegion == null || toRegion == null)
            return false;

        // We don't track per-resource piles, but we *do* track regionProduction; allow
        // trade if fromRegion's
        // produced resource type matches `twoFrom` and has at least 2; grant +1 to
        // `oneTo` by increasing toRegion
        String fromType = ResourceType.REGION_TO_RESOURCE.getOrDefault(fromRegion.name, "");
        if (!fromType.equalsIgnoreCase(twoFrom))
            return false;
        if (fromRegion.regionProduction < 2)
            return false;

        fromRegion.regionProduction -= 2;
        // Grant the "oneTo": if it matches toRegion's type, store there; else bank
        String toType = ResourceType.REGION_TO_RESOURCE.getOrDefault(toRegion.name, "");
        if (toType.equalsIgnoreCase(oneTo)) {
            toRegion.regionProduction = Math.min(3, toRegion.regionProduction + 1);
        } else {
            p.gainResource(oneTo);
        }
        return true;
    }

    private Card getSafe(Player p, int r, int c) {
        return p.getCard(r, c);
    }
}
