import model.*;
import controller.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Main entry point for the Rivals of Catan application.
 * Handles program initialization, gameplay loop, and client-side connection logic.
 * Follows SOLID principles by separating application logic from server responsibilities.
 */
public class Main {

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
     * Application entry point.
     * Supports three modes:
     * - bot: Start local game with bot opponent
     * - online: Connect to remote game as client
     * - default: Start local game waiting for network opponent
     *
     * @param args Command line arguments [bot|online]
     */
    public static void main(String[] args) {
        Main main = new Main();
        try {
            if ((args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("bot")))) {
                Card.loadBasicCards("cards.json");
                Server server = new Server();
                server.start(args.length == 0 ? false : true); // with bot
                main.runGameLoop(server.getPlayers());
                return;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("online")) {
                main.runClient();
                return; // run client mode
            } else {
                System.out.println("Usage: java Main [optional: bot|online]");
                return;
            }
        } catch (Exception e) {
            System.err.println("Failed to start: " + e.getMessage());
            return;
        }
    }

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
            players.get(i).sendMessage("Opponent's starting board:");
            players.get(i).sendMessage(
                    "\t\t" + players.get((i + 1) % players.size()).printPrincipality().replace("\n", "\n\t\t"));
            players.get(i).sendMessage("Your starting board:");
            players.get(i).sendMessage(players.get(i).printPrincipality());
            players.get(i).sendMessage("Your starting hand:");
            players.get(i).sendMessage(players.get(i).printHand());
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
                players.get(i).sendMessage("Opponent's board:");
                players.get(i).sendMessage(
                        "\t\t" + players.get((i + 1) % players.size()).printPrincipality().replace("\n", "\n\t\t"));
                players.get(i).sendMessage("Your board:");
                players.get(i).sendMessage(players.get(i).printPrincipality());
                players.get(i).sendMessage("Your hand:");
                players.get(i).sendMessage(players.get(i).printHand());
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

    /**
     * Runs the client-side connection to a remote game server.
     * Handles bidirectional communication with the server through object streams.
     * 
     * SECURITY NOTE: This method uses Java serialization (ObjectInputStream/ObjectOutputStream)
     * which has known security vulnerabilities. This implementation includes basic validation
     * to only accept String objects, but for production use, consider:
     * - Using a safer serialization format (JSON, Protocol Buffers, etc.)
     * - Implementing proper authentication and authorization
     * - Using encrypted connections (TLS/SSL)
     *
     * @throws Exception if connection fails or communication errors occur
     */
    public void runClient() throws Exception {
        Socket socket = new Socket("127.0.0.1", 2048);

        // IMPORTANT: create ObjectOutputStream first, then flush, then
        // ObjectInputStream
        ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
        outToServer.flush(); // send stream header immediately
        ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());

        Scanner console = new Scanner(System.in);
        try {
            while (true) {
                Object obj = inFromServer.readObject();
                // Security: Only accept String objects to prevent deserialization attacks
                if (!(obj instanceof String)) {
                    System.err.println("[Client] Security: Rejected non-String object: " + 
                        (obj == null ? "null" : obj.getClass().getName()));
                    continue;
                }
                String msg = (String) obj;

                // Always print what the server sent
                System.out.println(msg);

                // If it's a prompt, read one line from console and send it back
                if (msg.startsWith("PROMPT:")) {
                    System.out.print("> ");
                    System.out.flush();
                    String answer = console.nextLine();
                    outToServer.writeObject(answer);
                    outToServer.flush(); // push it now
                    outToServer.reset(); // avoid OOS caching of repeated String instances
                }

                // Allow server to end the session with a keyword
                if (msg.toLowerCase().contains("winner") || msg.equalsIgnoreCase("CLOSE"))
                    break;
            }
        } finally {
            try {
                console.close();
                inFromServer.close();
                outToServer.close();
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}
