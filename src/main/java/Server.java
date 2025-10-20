import model.*;
import network.OnlinePlayer;
import controller.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    public final List<Player> players = new ArrayList<>();
    public ServerSocket serverSocket;
    private final Random rng = new Random();
    
    // Managers for different game phases
    private final InitializationManager initManager = new InitializationManager();
    private final ProductionManager productionManager = new ProductionManager();
    private final ReplenishManager replenishManager = new ReplenishManager();
    private final ExchangeManager exchangeManager = new ExchangeManager();
    private final EventResolver eventResolver = new EventResolver();
    private final ActionManager actionManager = new ActionManager();

    // Event die faces
    private static final int EV_BRIGAND = EventType.BRIGAND;
    private static final int EV_TRADE = EventType.TRADE;
    private static final int EV_CELEB = EventType.CELEBRATION;
    private static final int EV_PLENTY = EventType.PLENTIFUL_HARVEST;
    private static final int EV_EVENT_A = EventType.EVENT_A;
    private static final int EV_EVENT_B = EventType.EVENT_B;



    public void start(boolean withBot) throws Exception {
        // 1) local console player
        players.add(new Player());
        // 2) bot player
        if (withBot) {
            Player bot = new Player();
            bot.isBot = true;
            players.add(bot);
        }
        // 3) networked players
        else {
            serverSocket = new ServerSocket(2048);
            Socket sock = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            // Use your existing OnlinePlayer class for remote players:
            OnlinePlayer op = new OnlinePlayer();
            // Then wire up its socket streams directly:
            op.setConnection(sock, in, out);
            players.add(op);
            System.out.println("Connected Online Player ");
            op.sendMessage("WELCOME Online Player ");
        }
        initManager.initPrincipality(players);
        // Initial replenish (3 cards each)
        for (int i = 0; i < players.size(); i++) {
            replenishManager.replenish(players.get(i));
        }
    }


    public void run() {
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
            int eventFace = rollEventDie(active);
            int prodFace = rollProductionDie(active);

            if (eventFace == EV_BRIGAND) { // Brigand first, then production
                eventResolver.resolveEvent(eventFace, players, active, other);
                productionManager.applyProduction(prodFace, players, this::opponentOf);
            } else { // production first, then event
                productionManager.applyProduction(prodFace, players, this::opponentOf);
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
            actionManager.actionPhase(active, other, this::broadcast);

            // -------- Part 3: Replenish Hand --------
            replenishManager.replenish(active);

            // -------- Part 4: Exchange (simplified) --------
            exchangeManager.exchangePhase(active, this::broadcast);

            // -------- Part 5: Scoring & Win Check --------
            if (checkWinEndOfTurn(active, other))
                break;

            current = (current + 1) % players.size();
        }
    }

    private boolean checkWinEndOfTurn(Player active, Player other) {
        int score = active.currentScoreAgainst(other);
        if (score >= 7) {
            broadcast("winner: Player " + players.indexOf(active)
                    + " wins with " + score + " VP (incl. advantage tokens)!");
            return true;
        }
        return false;
    }

    // ---------- Dice ----------
    private int rollEventDie(Player active) {
        // Brigitta lets the player fix production die, not event die — but we keep the
        // hook simple
        int face = 1 + rng.nextInt(6);
        broadcast("[EventDie] -> " + face);
        return face;
    }

    private int rollProductionDie(Player active) {
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
        broadcast("[ProductionDie] -> " + face);
        return face;
    }

    // ---------- Production ----------

    private Player opponentOf(Player p) {
        return (p == players.get(0)) ? players.get(1) : players.get(0);
    }

    // ---------- Misc ----------
    private void broadcast(String s) {
        // send to each player
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}