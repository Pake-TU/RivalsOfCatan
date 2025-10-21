import model.*;
import network.OnlinePlayer;
import controller.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Server class responsible for managing network connections and player initialization.
 * Handles server-side responsibilities such as accepting connections, setting up players,
 * and initializing the game state. The gameplay loop is handled by Main.java.
 */
public class Server {

    private final List<Player> players = new ArrayList<>();
    public ServerSocket serverSocket;
    
    // Managers for game initialization
    private final InitializationManager initManager = new InitializationManager();
    private final ReplenishManager replenishManager = new ReplenishManager();

    /**
     * Initializes the server and sets up players.
     * Handles three scenarios:
     * - Bot mode: Creates local player and bot player
     * - Network mode: Creates local player and waits for remote player connection
     *
     * @param withBot true to create a bot opponent, false to wait for network connection
     * @throws Exception if server setup or connection fails
     */
    public void start(boolean withBot) throws Exception {
        // 1) local console player
        players.add(new Player());
        // 2) bot player
        if (withBot) {
            Player bot = new Player(new view.BotPlayerView());
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

    /**
     * Returns the list of players for the game.
     *
     * @return List of players
     */
    public List<Player> getPlayers() {
        return players;
    }
}