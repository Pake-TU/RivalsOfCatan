/**
 * Interface for player input/output operations.
 * This abstraction allows for different implementations (console, network, mock for testing).
 */
public interface IPlayerIO {
    /**
     * Send a message to the player.
     * @param message The message to send
     */
    void sendMessage(Object message);

    /**
     * Receive a message from the player.
     * @return The received message as a string
     */
    String receiveMessage();

    /**
     * Validate and receive a resource input from the player.
     * Keeps prompting until a valid resource type is entered.
     * @param prompt The prompt message to display
     * @return A valid resource type
     */
    String validateResourceInput(String prompt);
}
