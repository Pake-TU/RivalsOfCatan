package controller.events;

import model.Card;
import model.Player;
import java.util.List;

/**
 * Handles drawing event cards (EVENT_A and EVENT_B).
 * Delegates to specific event card handlers based on card name.
 */
public class EventCardDrawEvent implements IEventHandler {
    
    private final FeudEventCard feudEvent = new FeudEventCard();
    private final FraternalFeudsEventCard fraternalFeudsEvent = new FraternalFeudsEventCard();
    private final InventionEventCard inventionEvent = new InventionEventCard();
    private final TradeShipsRaceEventCard tradeShipsRaceEvent = new TradeShipsRaceEventCard();
    private final TravelingMerchantEventCard travelingMerchantEvent = new TravelingMerchantEventCard();
    private final YearOfPlentyEventCard yearOfPlentyEvent = new YearOfPlentyEventCard();
    
    @Override
    public void handleEvent(List<Player> players, Player active, Player other) {
        broadcast(players, "[Event] Draw Event Card");
        
        if (Card.events.isEmpty()) {
            broadcast(players, "Event deck empty.");
            return;
        }
        
        Card top = Card.events.remove(0);
        broadcast(players, "EVENT: " + (top.cardText != null ? top.cardText : top.name));
        
        String nm = (top.name == null ? "" : top.name).toLowerCase();
        
        if (nm.equalsIgnoreCase("feud")) {
            feudEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("fraternal feuds")) {
            fraternalFeudsEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("invention")) {
            inventionEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("trade ships race")) {
            tradeShipsRaceEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("traveling merchant")) {
            travelingMerchantEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("year of plenty")) {
            yearOfPlentyEvent.resolve(players, active, other);
        } else if (nm.equalsIgnoreCase("yule")) {
            // Shuffle the event deck and immediately draw again
            java.util.Collections.shuffle(Card.events);
            handleEvent(players, active, other); // recurse one more draw
        }
    }
    
    @Override
    public String getEventName() {
        return "Event Card Draw";
    }
    
    private void broadcast(List<Player> players, String s) {
        for (Player p : players) {
            if (p != null) {
                p.sendMessage(s);
            }
        }
    }
}
