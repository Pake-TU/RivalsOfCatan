package network;

import model.Player;
import view.NetworkPlayerView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Online-capable player using network I/O through a view abstraction.
 * The server calls setConnection(...) after accepting a socket.
 * 
 * SECURITY NOTE: This class uses Java serialization (ObjectInputStream/ObjectOutputStream)
 * which has known security vulnerabilities. See NetworkPlayerView for details.
 */
public class OnlinePlayer extends Player {

    // Network socket (null when offline)
    private Socket socket;

    public OnlinePlayer() {
        super();
    }

    /**
     * Used by Server to wire up a freshly accepted socket and streams.
     * Sets the view to use network I/O.
     */
    public void setConnection(Socket sock, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = sock;
        setView(new NetworkPlayerView(in, out));
    }

    /**
     * Optional: close the connection cleanly (server may never need this).
     */
    public void closeConnection() {
        try {
            if (socket != null)
                socket.close();
        } catch (Exception ignored) {
        }
        socket = null;
    }
}