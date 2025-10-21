package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Tests to verify that cards properly add their stat points (CP, SP, FP, PP, KP)
 * when played.
 */
public class CardStatsTest {

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
        
        // Initialize player1 with a basic starting principality
        // Place a Settlement in the center row
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);
    }

    @Test
    public void testBrickShipAddsCP() {
        // Create a Brick Ship card with CP=1
        Card brickShip = new Card();
        brickShip.name = "Brick Ship";
        brickShip.type = "Unit – Trade Ship";
        brickShip.placement = "Settlement/city";
        brickShip.CP = "1";
        brickShip.cost = "LW";
        
        // Give player some resources to pay the cost
        player1.setResourceCount("Lumber", 1);
        player1.setResourceCount("Wool", 1);
        
        int initialCP = player1.commercePoints;
        
        // Play the card at position (1, 2) - above the settlement
        boolean success = brickShip.applyEffect(player1, player2, 1, 2);
        
        assertTrue(success, "Brick Ship should be placed successfully");
        assertEquals(initialCP + 1, player1.commercePoints, "Brick Ship should add 1 CP");
    }

    @Test
    public void testLargeTradeShipAddsCP() {
        // Create a Large Trade Ship card with CP=1
        Card largeTradeShip = new Card();
        largeTradeShip.name = "Large Trade Ship";
        largeTradeShip.type = "Unit – Trade Ship";
        largeTradeShip.placement = "Settlement/city";
        largeTradeShip.CP = "1";
        largeTradeShip.cost = "LOW";
        
        // Give player some resources to pay the cost
        player1.setResourceCount("Lumber", 1);
        player1.setResourceCount("Ore", 1);
        player1.setResourceCount("Wool", 1);
        
        int initialCP = player1.commercePoints;
        
        // Play the card at position (1, 2) - above the settlement
        boolean success = largeTradeShip.applyEffect(player1, player2, 1, 2);
        
        assertTrue(success, "Large Trade Ship should be placed successfully");
        assertEquals(initialCP + 1, player1.commercePoints, "Large Trade Ship should add 1 CP");
    }

    @Test
    public void testHeroAddsSPAndFP() {
        // Create a Hero card with SP and FP
        Card hero = new Card();
        hero.name = "Test Hero";
        hero.type = "Unit – Hero";
        hero.placement = "Settlement/city";
        hero.SP = "2";
        hero.FP = "3";
        hero.cost = "";
        
        int initialSP = player1.skillPoints;
        int initialFP = player1.strengthPoints;
        
        // Play the card at position (1, 2) - above the settlement
        boolean success = hero.applyEffect(player1, player2, 1, 2);
        
        assertTrue(success, "Hero should be placed successfully");
        assertEquals(initialSP + 2, player1.skillPoints, "Hero should add 2 SP");
        assertEquals(initialFP + 3, player1.strengthPoints, "Hero should add 3 FP");
    }

    @Test
    public void testAbbeyAddsPP() {
        // Create an Abbey card with PP=1
        Card abbey = new Card();
        abbey.name = "Abbey";
        abbey.type = "Building";
        abbey.placement = "Settlement/city";
        abbey.PP = "1";
        abbey.cost = "BGO";
        
        // Give player some resources to pay the cost
        player1.setResourceCount("Brick", 1);
        player1.setResourceCount("Grain", 1);
        player1.setResourceCount("Ore", 1);
        
        int initialPP = player1.progressPoints;
        
        // Play the card at position (1, 2) - above the settlement
        boolean success = abbey.applyEffect(player1, player2, 1, 2);
        
        assertTrue(success, "Abbey should be placed successfully");
        assertEquals(initialPP + 1, player1.progressPoints, "Abbey should add 1 PP");
    }

    @Test
    public void testMarketplaceAddsCP() {
        // Create a Marketplace card with CP=1
        Card marketplace = new Card();
        marketplace.name = "Marketplace";
        marketplace.type = "Building";
        marketplace.placement = "Settlement/city";
        marketplace.CP = "1";
        marketplace.cost = "BGO";
        
        // Give player some resources to pay the cost
        player1.setResourceCount("Brick", 1);
        player1.setResourceCount("Grain", 1);
        player1.setResourceCount("Ore", 1);
        
        int initialCP = player1.commercePoints;
        
        // Play the card at position (1, 2) - above the settlement
        boolean success = marketplace.applyEffect(player1, player2, 1, 2);
        
        assertTrue(success, "Marketplace should be placed successfully");
        assertEquals(initialCP + 1, player1.commercePoints, "Marketplace should add 1 CP");
    }

    @Test
    public void testMultipleShipsAddMultipleCP() {
        // Test that playing multiple ships correctly accumulates CP
        int initialCP = player1.commercePoints;
        
        // Play Brick Ship
        Card brickShip = new Card();
        brickShip.name = "Brick Ship";
        brickShip.type = "Unit – Trade Ship";
        brickShip.placement = "Settlement/city";
        brickShip.CP = "1";
        brickShip.cost = "";
        brickShip.applyEffect(player1, player2, 1, 2);
        
        assertEquals(initialCP + 1, player1.commercePoints, "First ship should add 1 CP");
        
        // Play Grain Ship
        Card grainShip = new Card();
        grainShip.name = "Grain Ship";
        grainShip.type = "Unit – Trade Ship";
        grainShip.placement = "Settlement/city";
        grainShip.CP = "1";
        grainShip.cost = "";
        grainShip.applyEffect(player1, player2, 3, 2);
        
        assertEquals(initialCP + 2, player1.commercePoints, "Second ship should add another 1 CP");
    }
}
