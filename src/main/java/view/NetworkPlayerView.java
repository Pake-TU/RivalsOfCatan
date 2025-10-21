package view;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Network-based implementation of IPlayerView.
 * Handles input/output through network socket streams using Java serialization.
 * 
 * SECURITY NOTE: This class uses Java serialization (ObjectInputStream/ObjectOutputStream)
 * which has known security vulnerabilities. This implementation includes basic validation
 * to only accept String objects, but for production use, consider:
 * - Using a safer serialization format (JSON, Protocol Buffers, etc.)
 * - Implementing proper authentication and authorization
 * - Using encrypted connections (TLS/SSL)
 * - Restricting which classes can be deserialized
 */
public class NetworkPlayerView implements IPlayerView {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    /**
     * Constructor with network streams.
     * @param in Input stream for receiving messages
     * @param out Output stream for sending messages
     */
    public NetworkPlayerView(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }
    
    @Override
    public void sendMessage(String message) {
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
                out.reset(); // prevent memory leak
            } catch (Exception e) {
                System.err.println("[NetworkPlayerView] send error: " + e.getMessage());
            }
        }
    }
    
    @Override
    public String receiveMessage() {
        if (in != null) {
            try {
                Object o = in.readObject();
                // Security: Only accept String objects to prevent deserialization attacks
                if (o != null && !(o instanceof String)) {
                    System.err.println("[NetworkPlayerView] Security: Rejected non-String object: " + 
                        o.getClass().getName());
                    return null;
                }
                return (o == null) ? null : (String) o;
            } catch (Exception e) {
                System.err.println("[NetworkPlayerView] receive error: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
}
