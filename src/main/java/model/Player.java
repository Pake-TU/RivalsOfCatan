package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.interfaces.IPlayer;
import view.IPlayerView;
import view.ConsolePlayerView;
import view.BotPlayerView;
import view.PlayerFormatter;

public class Player implements IPlayer {
    // --- “Public on purpose” for the exam ---
    public int victoryPoints = 0;
    public int progressPoints = 0;
    public int skillPoints = 0;
    public int commercePoints = 0;
    public int strengthPoints = 0;

    public int tradeRate = 3; // default 3:1 with bank
    public boolean isBot = false;

    // “Flags” for quick state (MARKETPLACE, PARISH, TOLLB, LTS@r,c, 2FOR1_WOOL,
    // STOREHOUSE@r,c, BRIGITTA, SCOUT_NEXT_SETTLEMENT)
    public Set<String> flags = new HashSet<>();

    // Resource pools (coarse, not per region)
    public Map<String, Integer> resources = new HashMap<>();

    // Hand (now real cards)
    public List<Card> hand = new ArrayList<>();

    // Principality: dynamic 2D grid of cards (null = empty)
    public List<List<Card>> principality = new ArrayList<>();

    // Last settlement (for Scout)
    public int lastSettlementRow = -1, lastSettlementCol = -1;

    // View abstraction for I/O (follows Dependency Inversion Principle)
    private IPlayerView view;

    public Player() {
        this(new ConsolePlayerView());
    }
    
    /**
     * Constructor with view injection (Dependency Inversion Principle).
     * Allows different view implementations for console, bot, network, or testing.
     * @param view The view implementation to use for I/O
     */
    public Player(IPlayerView view) {
        this.view = view;
        String[] all = { "Brick", "Grain", "Lumber", "Wool", "Ore", "Gold", "Any" };
        principality = new java.util.ArrayList<>();
        // Start with a 5×5 empty grid (grows as needed)
        for (int r = 0; r < 5; r++) {
            java.util.List<Card> row = new java.util.ArrayList<>();
            for (int c = 0; c < 5; c++)
                row.add(null);
            principality.add(row);
        }
        // (init other maps as new HashMap each; no static/shared refs)
        resources = new java.util.HashMap<>();
        for (String r : all)
            resources.put(r, 0);
    }

    // ------------- I/O (delegated to view) -------------
    public void sendMessage(Object m) {
        // Check if bot flag is set and view needs updating
        if (isBot && !(view instanceof BotPlayerView)) {
            view = new BotPlayerView();
        }
        view.sendMessage(String.valueOf(m));
    }

    public String receiveMessage() {
        // Check if bot flag is set and view needs updating
        if (isBot && !(view instanceof BotPlayerView)) {
            view = new BotPlayerView();
        }
        return view.receiveMessage();
    }
    
    /**
     * Set the view for this player (supports changing I/O mode).
     * @param view The new view implementation
     */
    public void setView(IPlayerView view) {
        this.view = view;
    }

    // ------------- Grid helpers -------------
    public Card getCard(int r, int c) {
        if (r < 0 || c < 0)
            return null;
        if (r >= principality.size())
            return null;
        List<Card> row = principality.get(r);
        if (row == null || c >= row.size())
            return null;
        return row.get(c);
    }

    public void placeCard(int r, int c, Card card) {
        ensureSize(r, c);
        principality.get(r).set(c, card);
    }

    // Returns the (possibly updated) column where the just-built center card now
    // sits
    public int expandAfterEdgeBuild(int col) {
        int cols = principality.get(0).size();
        // if placed in first column, insert a new column at the far left
        if (col == 0) {
            for (java.util.List<Card> row : principality) {
                row.add(0, null);
            }
            // all existing cards (including the one we just placed) shifted +1
            col += 1;
            if (lastSettlementCol >= 0)
                lastSettlementCol += 1;
        } else if (col == cols - 1) {
            // placed in last column, so append a new rightmost column
            for (java.util.List<Card> row : principality) {
                row.add(null);
            }
            // col stays the same
        }
        return col;
    }

    private void ensureSize(int r, int c) {
        while (principality.size() <= r) {
            ArrayList<Card> row = new ArrayList<>();
            // match width
            int cols = principality.isEmpty() ? 5 : principality.get(0).size();
            for (int i = 0; i < cols; i++)
                row.add(null);
            principality.add(row);
        }
        for (List<Card> row : principality) {
            while (row.size() <= c)
                row.add(null);
        }
    }

    public boolean hasInPrincipality(String name) {
        for (List<Card> row : principality)
            for (Card c : row)
                if (c != null && c.name != null && c.name.equalsIgnoreCase(name))
                    return true;
        return false;
    }

    // Nicely prints the principality with coordinates, plus hand & point summary.
    public String printPrincipality(Player opponent) {
        return PlayerFormatter.printPrincipality(this, opponent);
    }

    /**
     * Pretty-print this player's hand with index, cost, and any point values.
     */
    public String printHand() {
        return PlayerFormatter.printHand(this);
    }
    /**
     * Get a formatted string showing points including advantage tokens.
     * 
     * @param opp The opponent player (needed to check advantage status)
     * @return Formatted string showing all points and advantage tokens
     */
    public String getPointsSummary(Player opp) {
        return PlayerFormatter.getPointsSummary(this, opp);
    }

    // Advantage tokens require having 3+ points AND at least 1 more than the opponent.
    public boolean hasTradeTokenAgainst(IPlayer opp) {
        int oppCP = (opp == null ? 0 : opp.getCommercePoints());
        return this.commercePoints >= 3 && this.commercePoints > oppCP;
    }

    public boolean hasStrengthTokenAgainst(IPlayer opp) {
        int oppFP = (opp == null ? 0 : opp.getStrengthPoints());
        return this.strengthPoints >= 3 && this.strengthPoints > oppFP;
    }

    // Final score used for win check: base VP + 1 per advantage token against opponent
    public int currentScoreAgainst(IPlayer opp) {
        int score = this.victoryPoints;
        if (hasTradeTokenAgainst(opp))
            score += 1;
        if (hasStrengthTokenAgainst(opp))
            score += 1;
        return score;
    }

    // ------------- Resources (per-region, not pooled) -------------

    // Map a resource name to its Region card name
    private String resourceToRegion(String type) {
        return ResourceType.resourceToRegion(type);
    }

    // Collect all Region cards of a given region-name (e.g., "Forest")
    private java.util.List<Card> findRegions(String regionName) {
        java.util.List<Card> list = new java.util.ArrayList<>();
        if (regionName == null)
            return list;
        for (int r = 0; r < principality.size(); r++) {
            java.util.List<Card> row = principality.get(r);
            if (row == null)
                continue;
            for (int c = 0; c < row.size(); c++) {
                Card x = row.get(c);
                if (x != null &&
                        "Region".equalsIgnoreCase(x.type) &&
                        x.name != null &&
                        x.name.equalsIgnoreCase(regionName)) {
                    list.add(x);
                }
            }
        }
        return list;
    }

    // Sum stored resources on all regions (of ANY type)
    public int totalAllResources() {
        int sum = 0;
        for (int r = 0; r < principality.size(); r++) {
            java.util.List<Card> row = principality.get(r);
            if (row == null)
                continue;
            for (int c = 0; c < row.size(); c++) {
                Card x = row.get(c);
                if (x != null && "Region".equalsIgnoreCase(x.type)) {
                    sum += Math.max(0, Math.min(3, x.regionProduction));
                }
            }
        }
        return sum;
    }

    // Count stored resources of a specific resource type across the board
    public int getResourceCount(String type) {
        String regionName = resourceToRegion(type);
        if (regionName == null)
            return 0;
        if ("Any".equals(regionName))
            return totalAllResources();
        int sum = 0;
        for (Card r : findRegions(regionName)) {
            sum += Math.max(0, Math.min(3, r.regionProduction));
        }
        return sum;
    }

    // Gain 1 resource of a type: add to the matching region with the LOWEST stock
    // (<3)
    // If "Any", ask the player which resource to take.
    public void gainResource(String type) {
        String t = type;
        if (t == null || t.equalsIgnoreCase("Any")) {
            t = util.PlayerInputHelper.validateAndPromptResource(this, "Choose resource to gain");
        } else {
            // Validate the provided resource type
            String regionName = resourceToRegion(t);
            if (regionName == null || "Any".equals(regionName)) {
                // Invalid resource, prompt for correct input
                sendMessage("Unknown resource '" + t + "'.");
                t = util.PlayerInputHelper.validateAndPromptResource(this, "Please enter a valid resource");
            }
        }

        String regionName = resourceToRegion(t);
        java.util.List<Card> regs = findRegions(regionName);
        if (regs.isEmpty()) {
            sendMessage("No region for resource " + t + " is present.");
            return;
        }

        // pick lowest stored (<3); tie -> first in board order
        Card best = null;
        int bestVal = Integer.MAX_VALUE;
        for (Card r : regs) {
            int v = Math.max(0, Math.min(3, r.regionProduction));
            if (v < bestVal) {
                bestVal = v;
                best = r;
            }
        }
        if (best != null && best.regionProduction < 3) {
            best.regionProduction += 1;
            // Optional: feedback
            // sendMessage("Gained 1 " + t + " on " + regionName + " (" +
            // (best.regionProduction) + "/3)");
        } else {
            sendMessage("No storage space on any " + regionName + " (already 3/3).");
        }
    }

    /**
     * Prompts the player for a valid resource until they provide a correct one.
     * 
     * @param promptMessage The message to display when asking for input
     * @return A validated resource type name (Brick, Grain, Lumber, Wool, Ore, or
     *         Gold)
     */
    public String validateAndPromptResource(String promptMessage) {
        return util.PlayerInputHelper.validateAndPromptResource(this, promptMessage);
    }

    // Remove N resources of a type: repeatedly remove from the region with the
    // HIGHEST stock (>0)
    // Returns true if all could be removed, false otherwise (removes as many as
    // possible).
    public boolean removeResource(String type, int n) {
        if (n <= 0)
            return true;

        // Validate resource type
        String regionName = resourceToRegion(type);
        if (regionName == null || "Any".equals(regionName)) {
            sendMessage("Invalid resource type '" + type + "' for removal.");
            return false;
        }

        java.util.List<Card> regs = findRegions(regionName);
        if (regs.isEmpty())
            return false;

        int removed = 0;
        while (removed < n) {
            // find highest stocked region (>0)
            Card best = null;
            int bestVal = -1;
            for (Card r : regs) {
                int v = Math.max(0, Math.min(3, r.regionProduction));
                if (v > bestVal) {
                    bestVal = v;
                    best = r;
                }
            }
            if (best == null || bestVal <= 0)
                break; // no more to remove
            best.regionProduction -= 1;
            removed++;
        }
        return removed == n;
    }

    /**
     * Prompts the player to discard a resource with validation and retry on invalid
     * input.
     * 
     * @param promptMessage The message to display when asking for input
     * @return The validated resource type that was discarded
     */
    public String promptAndRemoveResource(String promptMessage) {
        return util.PlayerInputHelper.promptAndRemoveResource(this, promptMessage);
    }

    // Set total stored resources for a type by redistributing across its regions.
    // If n <= 0, zero all matching regions.
    // If increasing, fill lowest first; if decreasing, remove from highest first.
    public void setResourceCount(String type, int n) {
        String regionName = resourceToRegion(type);
        if (regionName == null || "Any".equals(regionName))
            return;

        java.util.List<Card> regs = findRegions(regionName);
        if (regs.isEmpty())
            return;

        // clamp desired total between 0 and regions*3
        int maxTotal = regs.size() * 3;
        int want = Math.max(0, Math.min(maxTotal, n));

        // current total
        int cur = 0;
        for (Card r : regs) {
            r.regionProduction = Math.max(0, Math.min(3, r.regionProduction)); // sanitize
            cur += r.regionProduction;
        }
        if (cur == want)
            return;

        if (cur < want) {
            // add (want - cur) by filling lowest first
            int need = want - cur;
            while (need > 0) {
                Card best = null;
                int bestVal = Integer.MAX_VALUE;
                for (Card r : regs) {
                    int v = r.regionProduction;
                    if (v < 3 && v < bestVal) {
                        bestVal = v;
                        best = r;
                    }
                }
                if (best == null || best.regionProduction >= 3)
                    break;
                best.regionProduction += 1;
                need--;
            }
        } else {
            // remove (cur - want) by draining highest first
            int drop = cur - want;
            while (drop > 0) {
                Card best = null;
                int bestVal = -1;
                for (Card r : regs) {
                    int v = r.regionProduction;
                    if (v > bestVal) {
                        bestVal = v;
                        best = r;
                    }
                }
                if (best == null || best.regionProduction <= 0)
                    break;
                best.regionProduction -= 1;
                drop--;
            }
        }
    }

    // ------------- Hand -------------
    public int handSize() {
        return hand.size();
    }

    public void addToHand(Card c) {
        hand.add(c);
    }

    public Card removeFromHandByName(String nm) {
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            if (c != null && c.name != null && c.name.equalsIgnoreCase(nm)) {
                return hand.remove(i);
            }
        }
        return null;
    }

    // ------------- Cheap prompts used by Server -------------
    public String chooseResource() {
        return util.PlayerInputHelper.chooseResource(this);
    }

    // ------------- IPlayer interface implementations (getters) -------------
    @Override
    public List<List<Card>> getPrincipality() {
        return principality;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public boolean isBot() {
        return isBot;
    }

    @Override
    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public int getCommercePoints() {
        return commercePoints;
    }

    @Override
    public int getStrengthPoints() {
        return strengthPoints;
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public int getProgressPoints() {
        return progressPoints;
    }
}