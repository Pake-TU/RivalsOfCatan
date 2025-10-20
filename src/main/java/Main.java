import model.Card;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Main entry point for the Rivals of Catan application.
 * Handles program initialization and client-side connection logic.
 * Follows SOLID principles by separating application bootstrap from game logic.
 */
public class Main {

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
                server.run();
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
     * Runs the client-side connection to a remote game server.
     * Handles bidirectional communication with the server through object streams.
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
                if (!(obj instanceof String)) {
                    // ignore unexpected payloads
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
