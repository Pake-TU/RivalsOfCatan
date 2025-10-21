package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Tests to verify that advantage tokens are properly awarded when placing cards.
 * When a player reaches >= 3 points advantage in CP or FP over their opponent,
 * they should immediately receive +1 VP for gaining the advantage token.
 */
public class AdvantageTokenTest {

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
    public void testTradeAdvantageGainedWhenPlacingShip() {
        // Player1 starts with 0 CP, Player2 starts with 0 CP
        assertEquals(0, player1.commercePoints);
        assertEquals(0, player2.commercePoints);
        assertEquals(0, player1.victoryPoints);
        
        // Place a ship that gives +1 CP - not enough for advantage yet
        Card ship1 = new Card();
        ship1.name = "Brick Ship";
        ship1.type = "Unit – Trade Ship";
        ship1.placement = "Settlement/city";
        ship1.CP = "1";
        ship1.cost = "";
        ship1.applyEffect(player1, player2, 1, 2);
        
        assertEquals(1, player1.commercePoints);
        assertEquals(0, player1.victoryPoints, "Should not get VP yet - only 1 CP ahead");
        
        // Place another ship - now 2 CP ahead, still not enough
        Card ship2 = new Card();
        ship2.name = "Grain Ship";
        ship2.type = "Unit – Trade Ship";
        ship2.placement = "Settlement/city";
        ship2.CP = "1";
        ship2.cost = "";
        ship2.applyEffect(player1, player2, 3, 2);
        
        assertEquals(2, player1.commercePoints);
        assertEquals(0, player1.victoryPoints, "Should not get VP yet - only 2 CP ahead");
        
        // Place a building that gives +1 CP - now 3 CP ahead, should get advantage token!
        Card marketplace = new Card();
        marketplace.name = "Marketplace";
        marketplace.type = "Building";
        marketplace.placement = "Settlement/city";
        marketplace.CP = "1";
        marketplace.cost = "";
        
        // Place at row 1, col 1 (above the settlement which is at 2,2)
        // Need to add another settlement first at row 2, col 1
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 1, settlement1);
        
        marketplace.applyEffect(player1, player2, 1, 1);
        
        assertEquals(3, player1.commercePoints);
        assertEquals(1, player1.victoryPoints, "Should get +1 VP for trade advantage token");
        assertTrue(player1.hasTradeTokenAgainst(player2), "Should have trade advantage");
    }

    @Test
    public void testStrengthAdvantageGainedWhenPlacingHero() {
        // Player1 starts with 0 FP, Player2 starts with 0 FP
        assertEquals(0, player1.strengthPoints);
        assertEquals(0, player2.strengthPoints);
        assertEquals(0, player1.victoryPoints);
        
        // Place a hero that gives +3 FP - should immediately get advantage token
        Card hero = new Card();
        hero.name = "Test Hero";
        hero.type = "Unit – Hero";
        hero.placement = "Settlement/city";
        hero.FP = "3";
        hero.cost = "";
        hero.applyEffect(player1, player2, 1, 2);
        
        assertEquals(3, player1.strengthPoints);
        assertEquals(1, player1.victoryPoints, "Should get +1 VP for strength advantage token");
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Should have strength advantage");
    }

    @Test
    public void testNoAdvantageWhenOpponentHasMorePoints() {
        // Give player2 some CP first
        player2.commercePoints = 5;
        
        // Player1 places a ship giving +1 CP
        Card ship = new Card();
        ship.name = "Brick Ship";
        ship.type = "Unit – Trade Ship";
        ship.placement = "Settlement/city";
        ship.CP = "1";
        ship.cost = "";
        ship.applyEffect(player1, player2, 1, 2);
        
        assertEquals(1, player1.commercePoints);
        assertEquals(0, player1.victoryPoints, "Should not get VP - opponent has more CP");
        assertFalse(player1.hasTradeTokenAgainst(player2), "Should not have trade advantage");
    }

    @Test
    public void testAdvantageNotGrantedTwice() {
        // Player1 gets 3 CP first (gains advantage)
        Card ship1 = new Card();
        ship1.name = "Brick Ship";
        ship1.type = "Unit – Trade Ship";
        ship1.placement = "Settlement/city";
        ship1.CP = "3";
        ship1.cost = "";
        ship1.applyEffect(player1, player2, 1, 2);
        
        assertEquals(3, player1.commercePoints);
        assertEquals(1, player1.victoryPoints, "Should get +1 VP for first advantage");
        
        // Player1 places another ship (still has advantage, shouldn't get another VP)
        Card ship2 = new Card();
        ship2.name = "Grain Ship";
        ship2.type = "Unit – Trade Ship";
        ship2.placement = "Settlement/city";
        ship2.CP = "1";
        ship2.cost = "";
        ship2.applyEffect(player1, player2, 3, 2);
        
        assertEquals(4, player1.commercePoints);
        assertEquals(1, player1.victoryPoints, "Should still have only 1 VP - advantage already held");
    }

    @Test
    public void testBothAdvantagesCanBeGainedSimultaneously() {
        // Place a card that gives both CP and FP to gain both advantages at once
        Card powerCard = new Card();
        powerCard.name = "Power Card";
        powerCard.type = "Unit – Hero";
        powerCard.placement = "Settlement/city";
        powerCard.CP = "3";
        powerCard.FP = "3";
        powerCard.cost = "";
        powerCard.applyEffect(player1, player2, 1, 2);
        
        assertEquals(3, player1.commercePoints);
        assertEquals(3, player1.strengthPoints);
        assertEquals(2, player1.victoryPoints, "Should get +2 VP (one for each advantage)");
        assertTrue(player1.hasTradeTokenAgainst(player2), "Should have trade advantage");
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Should have strength advantage");
    }

    @Test
    public void testAdvantageRequiresAtLeast3PointDifference() {
        // Give both players some points - player1 only 2 ahead
        player1.commercePoints = 2;
        player2.commercePoints = 0;
        
        assertFalse(player1.hasTradeTokenAgainst(player2), "2 point difference is not enough");
        
        // Add 1 more to reach 3 point difference
        Card ship = new Card();
        ship.name = "Brick Ship";
        ship.type = "Unit – Trade Ship";
        ship.placement = "Settlement/city";
        ship.CP = "1";
        ship.cost = "";
        ship.applyEffect(player1, player2, 1, 2);
        
        assertEquals(3, player1.commercePoints);
        assertEquals(1, player1.victoryPoints, "Should get +1 VP when reaching 3 point advantage");
        assertTrue(player1.hasTradeTokenAgainst(player2), "Should have trade advantage at exactly 3 points");
    }
}
