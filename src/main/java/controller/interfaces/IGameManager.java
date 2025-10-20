package controller.interfaces;

/**
 * Base interface for all game phase managers.
 * Promotes consistency across different game phase implementations.
 */
public interface IGameManager {
    
    /**
     * Get the name of this game phase manager.
     * @return The name of the phase (e.g., "Production", "Replenish", "Exchange")
     */
    String getPhaseName();
    
    /**
     * Initialize or reset the manager state if needed.
     * This can be called at the start of a game or when resetting.
     */
    default void initialize() {
        // Default implementation does nothing
    }
}
