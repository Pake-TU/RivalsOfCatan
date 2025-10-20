/**
 * Interface for card effects.
 * This abstraction allows for different card behaviors to be implemented
 * and makes it easier to add new cards and expansions.
 */
public interface ICardEffect {
    /**
     * Apply the card's effect.
     * @param active The active player
     * @param other The opponent player
     * @param row The target row (if applicable)
     * @param col The target column (if applicable)
     * @return true if the effect was successfully applied, false otherwise
     */
    boolean applyEffect(Player active, Player other, int row, int col);

    /**
     * Get the card's name.
     * @return The card name
     */
    String getName();

    /**
     * Get the card's type.
     * @return The card type
     */
    String getType();

    /**
     * Get the card's cost.
     * @return The cost string
     */
    String getCost();
}
