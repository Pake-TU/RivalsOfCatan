package view;

import model.Card;
import model.interfaces.IPlayer;
import java.util.List;

/**
 * Handles formatting and presentation of player data.
 * Separates presentation logic from the Player model following the Single Responsibility Principle.
 */
public class PlayerFormatter {

    /**
     * Nicely prints the principality with coordinates, plus hand & point summary.
     * @param player The player whose principality to print
     * @param opponent The opponent player (needed for advantage calculations)
     * @return Formatted string representation of the principality
     */
    public static String printPrincipality(IPlayer player, IPlayer opponent) {
        StringBuilder sb = new StringBuilder();
        List<List<Card>> principality = player.getPrincipality();
        int rows = principality.size();
        int cols = principality.isEmpty() ? 0 : principality.get(0).size();

        // Compute column widths based on both title and info lines
        int[] w = new int[cols];
        int minW = 10; // a reasonable minimum so headers fit
        for (int c = 0; c < cols; c++) {
            int m = minW;
            for (int r = 0; r < rows; r++) {
                Card card = player.getCard(r, c);
                String title = cellTitle(card);
                String info = cellInfo(card);
                m = Math.max(m, title.length());
                m = Math.max(m, info.length());
            }
            w[c] = m;
        }

        // Top header for columns (outside the grid for clarity)
        sb.append("      "); // space for row index column
        for (int c = 0; c < cols; c++) {
            String hdr = "Col " + c;
            sb.append(padRight(hdr, w[c] + 3)); // +3 because inside grid we put spaces and | around content
        }
        sb.append("\n");

        // Top border
        sb.append("    ").append(buildSep(w)).append("\n");

        // Each board row => 2 text lines inside the grid
        for (int r = 0; r < rows; r++) {
            // Title line
            sb.append(String.format("%2d  ", r)); // row index + two spaces
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                String title = cellTitle(player.getCard(r, c));
                sb.append(" ").append(padRight(title, w[c])).append(" ").append("|");
            }
            sb.append("\n");

            // Info line
            sb.append("    "); // aligns with the top border (no row index on the 2nd line)
            sb.append("|");
            for (int c = 0; c < cols; c++) {
                String info = cellInfo(player.getCard(r, c));
                sb.append(" ").append(padRight(info, w[c])).append(" ").append("|");
            }
            sb.append("\n");

            // Row separator
            sb.append("    ").append(buildSep(w)).append("\n");
        }

        // Points line - use getPointsSummary to include advantages
        sb.append(getPointsSummary(player, opponent));

        // Resources banner - show current available resources
        sb.append("\nResources: ");
        sb.append("Brick=").append(player.getResourceCount("Brick")).append("  ");
        sb.append("Grain=").append(player.getResourceCount("Grain")).append("  ");
        sb.append("Lumber=").append(player.getResourceCount("Lumber")).append("  ");
        sb.append("Wool=").append(player.getResourceCount("Wool")).append("  ");
        sb.append("Ore=").append(player.getResourceCount("Ore")).append("  ");
        sb.append("Gold=").append(player.getResourceCount("Gold"));
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Pretty-print the player's hand with index, cost, and any point values.
     * @param player The player whose hand to print
     * @return Formatted string representation of the hand
     */
    public static String printHand(IPlayer player) {
        StringBuilder sb = new StringBuilder();
        List<Card> hand = player.getHand();
        sb.append("Hand (").append(hand.size()).append("):\n");
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            if (c == null)
                continue;
            String cost = (c.cost == null || c.cost.isBlank()) ? "-" : c.cost;
            String pts = summarizePoints(c); // same helper you already use in printPrincipality
            sb.append("  [").append(i).append("] ")
                    .append(c.name == null ? "Unknown" : c.name)
                    .append("   {cost: ").append(cost).append("} ")
                    .append(pts.isEmpty() ? "" : pts)
                    .append("\n").append(c.cardText == null ? "" : "\t" + c.cardText + "\n");
        }
        return sb.toString();
    }

    /**
     * Get a formatted string showing points including advantage tokens.
     * @param player The player whose points to show
     * @param opp The opponent player (needed to check advantage status)
     * @return Formatted string showing all points and advantage tokens
     */
    public static String getPointsSummary(IPlayer player, IPlayer opp) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPoints: ");
        sb.append("VP=").append(player.getVictoryPoints());

        // Show advantage tokens
        boolean hasTradeAdv = player.hasTradeTokenAgainst(opp);
        boolean hasStrengthAdv = player.hasStrengthTokenAgainst(opp);
        if (hasTradeAdv || hasStrengthAdv) {
            sb.append(" [");
            if (hasTradeAdv) {
                sb.append("Trade+1");
            }
            if (hasTradeAdv && hasStrengthAdv) {
                sb.append(", ");
            }
            if (hasStrengthAdv) {
                sb.append("Strength+1");
            }
            sb.append("]");
        }

        int totalScore = player.currentScoreAgainst(opp);
        if (totalScore != player.getVictoryPoints()) {
            sb.append(" → Total: ").append(totalScore);
        }

        sb.append("  CP=").append(player.getCommercePoints());
        sb.append("  SP=").append(player.getSkillPoints());
        sb.append("  FP=").append(player.getStrengthPoints());
        sb.append("  PP=").append(player.getProgressPoints());
        sb.append("\n");

        return sb.toString();
    }

    // ---------- Helper methods for formatting ----------

    private static String buildSep(int[] w) {
        StringBuilder sep = new StringBuilder();
        sep.append("+");
        for (int c = 0; c < w.length; c++) {
            sep.append("-".repeat(w[c] + 2)); // +2 for side spaces inside cells
            sep.append("+");
        }
        return sep.toString();
    }

    private static String padRight(String s, int w) {
        if (s == null)
            s = "";
        if (s.length() >= w)
            return s;
        return s + " ".repeat(w - s.length());
    }

    private static String cellTitle(Card c) {
        if (c == null)
            return "";
        String title = c.name;
        if (title.equals("Forest"))
            title += " (L):Lumber";
        else if (title.equals("Hill"))
            title += " (B):Brick";
        else if (title.equals("Field"))
            title += " (G):Grain";
        else if (title.equals("Pasture"))
            title += " (W):Wool";
        else if (title.equals("Mountain"))
            title += " (O):Ore";
        else if (title.equals("Gold Field"))
            title += " (A):Gold";
        return title == null ? "Unknown" : title;
    }

    private static String cellInfo(Card c) {
        if (c == null)
            return ""; // EMPTY
        // Regions: show dice + stored (0..3)
        if ("Region".equalsIgnoreCase(c.type)) {
            String die = (c.diceRoll <= 0 ? "-" : String.valueOf(c.diceRoll));
            int stored = Math.max(0, Math.min(3, c.regionProduction));
            return "d" + die + "  " + stored + "/3";
        }

        // Common trade ships: "2:1 <Res>"
        String nm = c.name == null ? "" : c.name;
        if (c.type != null && c.type.toLowerCase().contains("trade ship")) {
            if (!nm.equalsIgnoreCase("Large Trade Ship") && nm.endsWith("Ship")) {
                String res = firstWord(nm); // Brick / Grain / etc.
                return "2:1 " + res;
            } else if (nm.equalsIgnoreCase("Large Trade Ship")) {
                return "LTS (left/right swap 2→1)";
            }
        }

        // Boosters: Foundry/Mill/Camp/Factory/Shop (hint text)
        if ("Building".equalsIgnoreCase(c.type) &&
                "Settlement/City Expansions".equalsIgnoreCase(c.placement)) {
            if (nm.endsWith("Foundry"))
                return "Boosts Ore x2 on match";
            if (nm.endsWith("Mill"))
                return "Boosts Grain x2 on match";
            if (nm.endsWith("Camp"))
                return "Boosts Lumber x2 on match";
            if (nm.endsWith("Factory"))
                return "Boosts Brick x2 on match";
            if (nm.endsWith("Shop"))
                return "Boosts Wool x2 on match";
        }

        // Center cards quick hints
        if ("Road".equalsIgnoreCase(nm))
            return "Center";
        if ("Settlement".equalsIgnoreCase(nm))
            return "Center";
        if ("City".equalsIgnoreCase(nm))
            return "Center";

        // Heroes / others: summarize points if any
        String pts = summarizePoints(c);
        if (!pts.isEmpty())
            return pts;

        // Default: show placement/type short
        String pl = c.placement == null ? "" : c.placement;
        String tp = c.type == null ? "" : c.type;
        if (!pl.isEmpty() || !tp.isEmpty())
            return (pl + " " + tp).trim();
        return "";
    }

    private static String summarizePoints(Card c) {
        // Build a compact points summary like: "[VP1 CP2 SP1 FP0 PP0]"
        int vp = parseIntSafe(c.victoryPoints);
        int cp = parseIntSafe(c.CP);
        int sp = parseIntSafe(c.SP);
        int fp = parseIntSafe(c.FP);
        int pp = parseIntSafe(c.PP);

        StringBuilder t = new StringBuilder();
        if (vp > 0 || cp > 0 || sp > 0 || fp > 0 || pp > 0) {
            t.append("[");
            if (vp > 0)
                t.append("VP").append(vp).append(" ");
            if (cp > 0)
                t.append("CP").append(cp).append(" ");
            if (sp > 0)
                t.append("SP").append(sp).append(" ");
            if (fp > 0)
                t.append("FP").append(fp).append(" ");
            if (pp > 0)
                t.append("PP").append(pp).append(" ");
            if (t.charAt(t.length() - 1) == ' ')
                t.deleteCharAt(t.length() - 1);
            t.append("]");
        }
        return t.toString();
    }

    private static String firstWord(String s) {
        if (s == null)
            return "";
        String[] toks = s.trim().split("\\s+");
        return toks.length == 0 ? "" : toks[0];
    }

    private static int parseIntSafe(String s) {
        if (s == null || s.isBlank())
            return 0;
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
