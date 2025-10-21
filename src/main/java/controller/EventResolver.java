package controller;

import controller.events.*;
import controller.interfaces.IGameManager;
import model.EventType;
import model.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventResolver handles all game events based on event die rolls.
 * Follows Single Responsibility Principle by delegating to specific event handlers.
 * Supports extensibility through the IEventHandler interface.
 */
public class EventResolver implements IGameManager {
    
    private final Map<Integer, IEventHandler> eventHandlers;
    
    public EventResolver() {
        eventHandlers = new HashMap<>();
        eventHandlers.put(EventType.BRIGAND, new BrigandEvent());
        eventHandlers.put(EventType.TRADE, new TradeEvent());
        eventHandlers.put(EventType.CELEBRATION, new CelebrationEvent());
        eventHandlers.put(EventType.PLENTIFUL_HARVEST, new PlentifulHarvestEvent());
        
        // Both EVENT_A and EVENT_B use the same handler
        EventCardDrawEvent eventCardHandler = new EventCardDrawEvent();
        eventHandlers.put(EventType.EVENT_A, eventCardHandler);
        eventHandlers.put(EventType.EVENT_B, eventCardHandler);
    }
    
    @Override
    public String getPhaseName() {
        return "Event Resolution";
    }
    
    /**
     * Resolve an event based on the event die face.
     * @param eventFace The event die face value (1-6)
     * @param players All players in the game
     * @param active The active player
     * @param other The opponent player
     */
    public void resolveEvent(int eventFace, List<Player> players, Player active, Player other) {
        IEventHandler handler = eventHandlers.get(eventFace);
        
        if (handler != null) {
            handler.handleEvent(players, active, other);
        } else {
            broadcast(players, "[Event] Unknown face " + eventFace);
        }
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
