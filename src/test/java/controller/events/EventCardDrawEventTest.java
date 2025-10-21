package controller.events;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EventCardDrawEvent to ensure event cards are properly returned
 * to the event pile after being resolved.
 */
public class EventCardDrawEventTest {

    private Player player1;
    private Player player2;
    private List<Player> players;
    private EventCardDrawEvent eventHandler;

    @BeforeEach
    public void setUp() throws IOException {
        // Load cards from the JSON file
        Card.loadBasicCards("cards.json");
        
        // Create two players
        player1 = new Player();
        player1.isBot = true; // Set as bot to avoid console input
        player2 = new Player();
        player2.isBot = true;
        
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        eventHandler = new EventCardDrawEvent();
    }

    @Test
    public void testEventCardReturnedToPileAfterResolution() {
        // Record the initial size of the event deck
        int initialSize = Card.events.size();
        assertTrue(initialSize > 0, "Event deck should not be empty initially");
        
        // Get the top card (but don't remove it yet)
        Card topCard = Card.events.get(0);
        
        // Handle the event (which should draw and then return the card)
        eventHandler.handleEvent(players, player1, player2);
        
        // The event deck should still have the same size after resolution
        assertEquals(initialSize, Card.events.size(), 
            "Event deck should maintain the same size after event resolution");
        
        // The top card should now be at the bottom of the deck
        Card bottomCard = Card.events.get(Card.events.size() - 1);
        assertEquals(topCard.name, bottomCard.name, 
            "The drawn event card should be at the bottom of the pile");
    }

    @Test
    public void testMultipleEventCardsReturned() {
        int initialSize = Card.events.size();
        assertTrue(initialSize > 1, "Event deck should have at least 2 cards for this test");
        
        // Draw multiple events
        for (int i = 0; i < 3; i++) {
            eventHandler.handleEvent(players, player1, player2);
        }
        
        // The event deck should still have the same size
        assertEquals(initialSize, Card.events.size(), 
            "Event deck should maintain the same size after multiple event resolutions");
    }

    @Test
    public void testEventDeckNeverEmpty() {
        int initialSize = Card.events.size();
        assertTrue(initialSize > 0, "Event deck should not be empty initially");
        
        // Draw more events than the initial deck size to ensure recycling works
        int drawCount = initialSize + 5;
        for (int i = 0; i < drawCount; i++) {
            int sizeBefore = Card.events.size();
            assertTrue(sizeBefore > 0, "Event deck should never be empty during gameplay");
            
            eventHandler.handleEvent(players, player1, player2);
            
            int sizeAfter = Card.events.size();
            assertEquals(sizeBefore, sizeAfter, 
                "Event deck size should remain constant after each draw");
        }
        
        // Final check
        assertEquals(initialSize, Card.events.size(), 
            "Event deck should have the same number of cards at the end");
    }

    @Test
    public void testYuleCardReturnedBeforeShuffle() {
        // Find if Yule card exists in the deck
        Card yuleCard = null;
        for (Card card : Card.events) {
            if (card.name != null && card.name.equalsIgnoreCase("Yule")) {
                yuleCard = card;
                break;
            }
        }
        
        if (yuleCard != null) {
            int initialSize = Card.events.size();
            
            // Move Yule to the top of the deck
            Card.events.remove(yuleCard);
            Card.events.add(0, yuleCard);
            
            // Handle the Yule event (should add it back, shuffle, then draw another)
            eventHandler.handleEvent(players, player1, player2);
            
            // The deck should still have the same size
            assertEquals(initialSize, Card.events.size(), 
                "Event deck should maintain size even after Yule shuffle and redraw");
        }
    }
}
