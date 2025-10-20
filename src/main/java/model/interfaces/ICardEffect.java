package model.interfaces;

import model.Player;

/**
 * Interface for card effects.
 * Allows for extensible card behavior and better testability.
 */
public interface ICardEffect {
    
    /**
     * Apply the effect of this card.
     * 
     * @param active The active player playing the card
     * @param opponent The opponent player
     * @param row The row where the card is being placed (or -1 for action cards)
     * @param col The column where the card is being placed (or -1 for action cards)
     * @return true if the effect was successfully applied, false otherwise
     */
    boolean applyEffect(Player active, Player opponent, int row, int col);
    
    /**
     * Check if this card can be played given the current game state.
     * 
     * @param active The active player
     * @param opponent The opponent player
     * @return true if the card can be played, false otherwise
     */
    default boolean canPlay(Player active, Player opponent) {
        return true;
    }
    
    /**
     * Get a description of what this effect does.
     * 
     * @return A human-readable description of the effect
     */
    String getEffectDescription();
}
