package model.cards;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for trade ship cards in the basic set.
 * Tests: Large Trade Ship, Gold Ship, Ore Ship, Grain Ship, Lumber Ship, Brick Ship, Wool Ship
 */
public class TradeShipCardsTest {

    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() throws IOException {
        Card.loadBasicCards("cards.json");
        player1 = new Player();
        player1.isBot = true;
        player2 = new Player();
        player2.isBot = true;
    }

    @Test
    public void testLargeTradeShipGives1CPAndSets2For1TradeWithNeighbors() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Large Trade Ship
        Card largeTradeShip = new Card();
        largeTradeShip.name = "Large Trade Ship";
        largeTradeShip.type = "Unit – Trade Ship";
        largeTradeShip.placement = "Settlement/city";
        largeTradeShip.CP = "1";
        boolean placed = largeTradeShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Large Trade Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Large Trade Ship should give 1 Commerce Point");

        // Verify Large Trade Ship flag is set with position
        assertTrue(player1.flags.stream().anyMatch(f -> f.startsWith("LTS@")), 
            "Large Trade Ship flag should be set with position for 2:1 trade with neighbors");
    }

    @Test
    public void testGoldShipGives1CPAndAllows2GoldFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Gold Ship
        Card goldShip = new Card();
        goldShip.name = "Gold Ship";
        goldShip.type = "Unit – Trade Ship";
        goldShip.placement = "Settlement/city";
        goldShip.CP = "1";
        boolean placed = goldShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Gold Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Gold Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Gold
        assertTrue(player1.flags.contains("2FOR1_GOLD"), 
            "Gold Ship should set flag for 2 Gold for 1 resource trade");
    }

    @Test
    public void testOreShipGives1CPAndAllows2OreFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Ore Ship
        Card oreShip = new Card();
        oreShip.name = "Ore Ship";
        oreShip.type = "Unit – Trade Ship";
        oreShip.placement = "Settlement/city";
        oreShip.CP = "1";
        boolean placed = oreShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Ore Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Ore Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Ore
        assertTrue(player1.flags.contains("2FOR1_ORE"), 
            "Ore Ship should set flag for 2 Ore for 1 resource trade");
    }

    @Test
    public void testGrainShipGives1CPAndAllows2GrainFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Grain Ship
        Card grainShip = new Card();
        grainShip.name = "Grain Ship";
        grainShip.type = "Unit – Trade Ship";
        grainShip.placement = "Settlement/city";
        grainShip.CP = "1";
        boolean placed = grainShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Grain Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Grain Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Grain
        assertTrue(player1.flags.contains("2FOR1_GRAIN"), 
            "Grain Ship should set flag for 2 Grain for 1 resource trade");
    }

    @Test
    public void testLumberShipGives1CPAndAllows2LumberFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Lumber Ship
        Card lumberShip = new Card();
        lumberShip.name = "Lumber Ship";
        lumberShip.type = "Unit – Trade Ship";
        lumberShip.placement = "Settlement/city";
        lumberShip.CP = "1";
        boolean placed = lumberShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Lumber Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Lumber Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Lumber
        assertTrue(player1.flags.contains("2FOR1_LUMBER"), 
            "Lumber Ship should set flag for 2 Lumber for 1 resource trade");
    }

    @Test
    public void testBrickShipGives1CPAndAllows2BrickFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Brick Ship
        Card brickShip = new Card();
        brickShip.name = "Brick Ship";
        brickShip.type = "Unit – Trade Ship";
        brickShip.placement = "Settlement/city";
        brickShip.CP = "1";
        boolean placed = brickShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Brick Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Brick Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Brick
        assertTrue(player1.flags.contains("2FOR1_BRICK"), 
            "Brick Ship should set flag for 2 Brick for 1 resource trade");
    }

    @Test
    public void testWoolShipGives1CPAndAllows2WoolFor1ResourceTrade() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Wool Ship
        Card woolShip = new Card();
        woolShip.name = "Wool Ship";
        woolShip.type = "Unit – Trade Ship";
        woolShip.placement = "Settlement/city";
        woolShip.CP = "1";
        boolean placed = woolShip.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Wool Ship should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Wool Ship should give 1 Commerce Point");

        // Verify 2:1 trade flag is set for Wool
        assertTrue(player1.flags.contains("2FOR1_WOOL"), 
            "Wool Ship should set flag for 2 Wool for 1 resource trade");
    }
}
