package model.effects;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CenterCardEffectHandler - focusing on Road, Settlement, and City placement.
 */
public class CenterCardEffectHandlerTest {

    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() throws IOException {
        // Load cards from the JSON file
        Card.loadBasicCards("cards.json");
        
        // Create two players
        player1 = new Player();
        player1.isBot = true; // Set as bot to avoid console input
        player2 = new Player();
        player2.isBot = true;
    }

    @Test
    public void testCityReplacesSettlementAndReturnsToSettlementsPile() {
        // First, place a Road
        Card road = new Card();
        road.name = "Road";
        road.type = "Road";
        player1.placeCard(2, 2, road);
        
        // Then, place a Settlement next to the road
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        
        int settlementsPileSizeBefore = Card.settlements.size();
        int initialVP = player1.victoryPoints;
        
        boolean settlementPlaced = settlement.applyEffect(player1, player2, 2, 3);
        assertTrue(settlementPlaced, "Settlement should be placed successfully");
        assertEquals(initialVP + 1, player1.victoryPoints, "Settlement should add 1 VP");
        
        // Verify settlement is on the board
        Card cardAtPosition = player1.getCard(2, 3);
        assertNotNull(cardAtPosition, "Card should exist at position (2,3)");
        assertEquals("Settlement", cardAtPosition.name, "Card should be Settlement");
        
        // Now place a City on the Settlement
        Card city = new Card();
        city.name = "City";
        city.type = "City";
        
        boolean cityPlaced = city.applyEffect(player1, player2, 2, 3);
        assertTrue(cityPlaced, "City should be placed successfully");
        assertEquals(initialVP + 2, player1.victoryPoints, "City should add another 1 VP (total 2 VP)");
        
        // Verify city is on the board
        cardAtPosition = player1.getCard(2, 3);
        assertNotNull(cardAtPosition, "Card should exist at position (2,3)");
        assertEquals("City", cardAtPosition.name, "Card should now be City, not Settlement");
        
        // Verify settlement was returned to the settlements pile
        int settlementsPileSizeAfter = Card.settlements.size();
        assertEquals(settlementsPileSizeBefore + 1, settlementsPileSizeAfter, 
            "Settlement should be returned to settlements pile when replaced by City");
    }

    @Test
    public void testCityCannotBePlacedWithoutSettlement() {
        // Try to place a City without a Settlement first
        Card city = new Card();
        city.name = "City";
        city.type = "City";
        
        boolean cityPlaced = city.applyEffect(player1, player2, 2, 2);
        assertFalse(cityPlaced, "City should not be placed without a Settlement");
        
        // Verify no city is on the board
        Card cardAtPosition = player1.getCard(2, 2);
        assertNull(cardAtPosition, "No card should be at position (2,2)");
    }

    @Test
    public void testCityCannotBePlacedOnRoad() {
        // Place a Road
        Card road = new Card();
        road.name = "Road";
        road.type = "Road";
        player1.placeCard(2, 2, road);
        
        // Try to place a City on the Road
        Card city = new Card();
        city.name = "City";
        city.type = "City";
        
        boolean cityPlaced = city.applyEffect(player1, player2, 2, 2);
        assertFalse(cityPlaced, "City should not be placed on a Road");
        
        // Verify road is still on the board
        Card cardAtPosition = player1.getCard(2, 2);
        assertNotNull(cardAtPosition, "Card should still exist at position (2,2)");
        assertEquals("Road", cardAtPosition.name, "Card should still be Road");
    }
}
