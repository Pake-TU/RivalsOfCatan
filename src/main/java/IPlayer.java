import java.util.List;

/**
 * Interface for player operations in the game.
 * This abstraction allows for different player types (local, bot, online)
 * and makes the system more testable and extensible.
 */
public interface IPlayer extends IPlayerIO, IResourceManager {
    /**
     * Get a card at a specific position in the principality.
     * @param row The row index
     * @param col The column index
     * @return The card at that position, or null if empty
     */
    Card getCard(int row, int col);

    /**
     * Place a card at a specific position.
     * @param row The row index
     * @param col The column index
     * @param card The card to place
     */
    void placeCard(int row, int col, Card card);

    /**
     * Check if a card with a specific name exists in the principality.
     * @param name The card name to search for
     * @return true if found, false otherwise
     */
    boolean hasInPrincipality(String name);

    /**
     * Get the size of the player's hand.
     * @return The number of cards in hand
     */
    int handSize();

    /**
     * Add a card to the player's hand.
     * @param card The card to add
     */
    void addToHand(Card card);

    /**
     * Remove a card from hand by name.
     * @param name The name of the card to remove
     * @return The removed card, or null if not found
     */
    Card removeFromHandByName(String name);

    /**
     * Print the player's principality (board).
     * @return A string representation of the principality
     */
    String printPrincipality();

    /**
     * Print the player's hand.
     * @return A string representation of the hand
     */
    String printHand();

    /**
     * Calculate the current score against an opponent.
     * @param opponent The opponent player
     * @return The score including advantage tokens
     */
    int currentScoreAgainst(IPlayer opponent);

    /**
     * Check if this player has trade advantage against opponent.
     * @param opponent The opponent player
     * @return true if has trade advantage
     */
    boolean hasTradeTokenAgainst(IPlayer opponent);

    /**
     * Check if this player has strength advantage against opponent.
     * @param opponent The opponent player
     * @return true if has strength advantage
     */
    boolean hasStrengthTokenAgainst(IPlayer opponent);

    // Point getters/setters
    int getVictoryPoints();
    void setVictoryPoints(int points);
    int getProgressPoints();
    void setProgressPoints(int points);
    int getSkillPoints();
    void setSkillPoints(int points);
    int getCommercePoints();
    void setCommercePoints(int points);
    int getStrengthPoints();
    void setStrengthPoints(int points);
}
