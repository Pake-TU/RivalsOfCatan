package view;

/**
 * View interface for player I/O operations.
 * Abstracts input/output from the model layer, supporting MVC architecture.
 * This allows for different view implementations (console, GUI, network, bot).
 */
public interface IPlayerView {
    /**
     * Send a message to the player.
     * @param message The message to display
     */
    void sendMessage(String message);
    
    /**
     * Receive a message from the player.
     * @return The player's input
     */
    String receiveMessage();
}
