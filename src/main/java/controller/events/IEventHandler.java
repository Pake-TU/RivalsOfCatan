package controller.events;

import model.Player;
import java.util.List;

/**
 * Interface for handling specific event types.
 * Each event handler implements its own resolution logic.
 */
public interface IEventHandler {
    
    /**
     * Handle the event for the given players.
     * @param players All players in the game
     * @param active The active player
     * @param other The opponent player
     */
    void handleEvent(List<Player> players, Player active, Player other);
    
    /**
     * Get the name of this event.
     * @return The event name
     */
    String getEventName();
}
