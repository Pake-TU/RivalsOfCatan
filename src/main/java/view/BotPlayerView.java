package view;

/**
 * Bot player view that provides default automated responses.
 * Suppresses output and returns default choices automatically.
 * Follows Strategy Pattern for different player types.
 */
public class BotPlayerView implements IPlayerView {
    
    @Override
    public void sendMessage(String message) {
        // Bots don't need to see messages
    }
    
    @Override
    public String receiveMessage() {
        // Bot auto-response: simple default choice
        // Returns "1" as a default choice for most prompts
        return "1";
    }
}
