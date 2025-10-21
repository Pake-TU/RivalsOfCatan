package model.interfaces;

import model.Card;
import java.util.List;

/**
 * Interface representing a player in the game.
 * Provides abstraction for different player types (local, online, bot).
 */
public interface IPlayer {
    
    /**
     * Send a message to the player.
     * @param message The message to send
     */
    void sendMessage(Object message);
    
    /**
     * Receive input from the player.
     * @return The player's input as a string
     */
    String receiveMessage();
    
    /**
     * Get the player's principality (game board).
     * @return 2D list of cards representing the principality
     */
    List<List<Card>> getPrincipality();
    
    /**
     * Get a specific card from the principality.
     * @param row Row index
     * @param col Column index
     * @return The card at the specified position, or null if empty
     */
    Card getCard(int row, int col);
    
    /**
     * Place a card at a specific position in the principality.
     * @param row Row index
     * @param col Column index
     * @param card The card to place
     */
    void placeCard(int row, int col, Card card);
    
    /**
     * Get the player's hand of cards.
     * @return List of cards in hand
     */
    List<Card> getHand();
    
    /**
     * Add a card to the player's hand.
     * @param card The card to add
     */
    void addToHand(Card card);
    
    /**
     * Get the count of a specific resource type.
     * @param resourceType The resource type to count
     * @return The number of that resource available
     */
    int getResourceCount(String resourceType);
    
    /**
     * Gain a resource of a specific type.
     * @param resourceType The type of resource to gain
     */
    void gainResource(String resourceType);
    
    /**
     * Remove resources of a specific type.
     * @param resourceType The type of resource to remove
     * @param amount The amount to remove
     * @return true if the operation succeeded, false otherwise
     */
    boolean removeResource(String resourceType, int amount);
    
    /**
     * Prompt the player for a valid resource with retry on invalid input.
     * @param promptMessage The message to display
     * @return A validated resource type
     */
    String validateAndPromptResource(String promptMessage);
    
    /**
     * Prompt the player to discard a resource with validation.
     * @param promptMessage The message to display
     * @return The validated resource type that was discarded
     */
    String promptAndRemoveResource(String promptMessage);
    
    /**
     * Check if the player is a bot.
     * @return true if bot, false otherwise
     */
    boolean isBot();
    
    /**
     * Get victory points.
     * @return Current victory points
     */
    int getVictoryPoints();
    
    /**
     * Get commerce points.
     * @return Current commerce points
     */
    int getCommercePoints();
    
    /**
     * Get strength points.
     * @return Current strength points
     */
    int getStrengthPoints();
    
    /**
     * Get skill points.
     * @return Current skill points
     */
    int getSkillPoints();
    
    /**
     * Get progress points.
     * @return Current progress points
     */
    int getProgressPoints();
    
    /**
     * Check if this player has the trade advantage token against opponent.
     * @param opponent The opponent player
     * @return true if this player has trade advantage
     */
    boolean hasTradeTokenAgainst(IPlayer opponent);
    
    /**
     * Check if this player has the strength advantage token against opponent.
     * @param opponent The opponent player
     * @return true if this player has strength advantage
     */
    boolean hasStrengthTokenAgainst(IPlayer opponent);
    
    /**
     * Get the current score including advantage tokens.
     * @param opponent The opponent player
     * @return Total score with advantage bonuses
     */
    int currentScoreAgainst(IPlayer opponent);
}
