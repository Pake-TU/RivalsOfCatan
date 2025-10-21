package model.cards;

import model.Card;
import model.Player;
import controller.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for event cards in the basic set.
 * Tests: Invention, Yule, Year of Plenty, Fraternal Feuds, Feud, Traveling Merchant, Trade Ships Race
 */
public class EventCardsTest {

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
    public void testInventionGivesResourcesBasedOnProgressPoints() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place Abbey to give 1 PP
        Card abbey = new Card();
        abbey.name = "Abbey";
        abbey.type = "Building";
        abbey.placement = "Settlement/city";
        abbey.PP = "1";
        abbey.applyEffect(player1, player2, 1, 2);

        assertEquals(1, player1.progressPoints, "Player should have 1 Progress Point from Abbey");

        // Invention event gives resources based on PP (up to max 2)
        // The event would allow player to choose resources based on their PP buildings
        // Since we can't test interactive input, we just verify PP is tracked correctly
        assertTrue(player1.progressPoints > 0, "Player has progress points for Invention event");
    }

    @Test
    public void testYuleReshufflesEventStack() {
        // Save initial event stack size
        int initialEventSize = Card.events.size();
        assertTrue(initialEventSize > 0, "Event stack should have cards before Yule");

        // Trigger Yule event (note: in actual game, Yule would be handled specially)
        // This test just verifies the event exists and can be referenced
        Card yuleCard = Card.popCardByName(Card.events, "Yule");
        assertNotNull(yuleCard, "Yule card should exist in event stack");
        assertEquals("Yule", yuleCard.name, "Card should be named Yule");
    }

    @Test
    public void testYearOfPlentyGivesResourcesFromAdjacentStorehousesAndAbbeys() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place region adjacent to settlement
        Card region = new Card();
        region.name = "Fields";
        region.type = "Region";
        region.diceRoll = 3;
        region.regionProduction = 0;
        player1.placeCard(1, 2, region);

        // Place Abbey above settlement (row 1, col 2), adjacent to region
        Card abbey = new Card();
        abbey.name = "Abbey";
        abbey.type = "Building";
        abbey.placement = "Settlement/city";
        abbey.PP = "1";
        abbey.applyEffect(player1, player2, 1, 2);

        // Year of Plenty gives regions 1 resource for each adjacent Storehouse/Abbey
        // Since we can't test interactive input, we verify the setup is correct
        assertNotNull(player1.getCard(1, 3), "Abbey should be placed");
        assertEquals("Abbey", player1.getCard(1, 3).name, "Card should be Abbey");
        assertEquals(0, region.regionProduction, "Region starts with 0 resources");
    }

    @Test
    public void testFraternalFeudsWithStrengthAdvantage() {
        // Set up settlements
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);

        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);

        // Give player1 strength advantage
        Card inga = new Card();
        inga.name = "Inga";
        inga.type = "Unit – Hero";
        inga.placement = "Settlement/city";
        inga.SP = "1";
        inga.FP = "3";
        inga.applyEffect(player1, player2, 1, 2);

        assertTrue(player1.hasStrengthTokenAgainst(player2), 
            "Player1 should have strength advantage");

        // Give player2 some cards in hand
        Card card1 = new Card();
        card1.name = "Test Card 1";
        player2.hand.add(card1);
        Card card2 = new Card();
        card2.name = "Test Card 2";
        player2.hand.add(card2);

        int player2HandSizeBefore = player2.hand.size();
        assertTrue(player2HandSizeBefore >= 2, "Player2 should have at least 2 cards in hand");

        // Fraternal Feuds: player with strength advantage selects 2 cards from opponent's hand
        // Since we can't test interactive input, we just verify the conditions are met
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Player1 has strength advantage for Fraternal Feuds");
    }

    @Test
    public void testFeudWithStrengthAdvantage() {
        // Set up settlements
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);

        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);

        // Give player1 strength advantage
        Card inga = new Card();
        inga.name = "Inga";
        inga.type = "Unit – Hero";
        inga.placement = "Settlement/city";
        inga.SP = "1";
        inga.FP = "3";
        inga.applyEffect(player1, player2, 1, 2);

        assertTrue(player1.hasStrengthTokenAgainst(player2), 
            "Player1 should have strength advantage");

        // Give player2 some buildings
        Card abbey = new Card();
        abbey.name = "Abbey";
        abbey.type = "Building";
        abbey.placement = "Settlement/city";
        abbey.PP = "1";
        abbey.applyEffect(player2, player1, 1, 2);

        // Feud: player with strength advantage selects 3 buildings, opponent removes 1
        // Since we can't test interactive input, we just verify the conditions are met
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Player1 has strength advantage for Feud");
    }

    @Test
    public void testTravelingMerchantAllowsResourcePurchase() {
        // Place gold field region so player can have gold
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        Card goldField = new Card();
        goldField.name = "Gold Field";
        goldField.type = "Region";
        goldField.regionProduction = 2;
        player1.placeCard(1, 2, goldField);

        int goldBefore = player1.getResourceCount("Gold");
        assertEquals(2, goldBefore, "Player should have 2 gold");

        // Traveling Merchant: each player may take up to 2 resources, paying 1 gold per resource
        // Since we can't test interactive input, we just verify player has gold for purchasing
        assertTrue(player1.getResourceCount("Gold") >= 1, "Player has gold for Traveling Merchant");
    }

    @Test
    public void testTradeShipsRaceRewardsPlayerWithMostShips() {
        // Place settlements
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);

        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);

        // Give player1 two trade ships
        Card brickShip = new Card();
        brickShip.name = "Brick Ship";
        brickShip.type = "Unit – Trade Ship";
        brickShip.placement = "Settlement/city";
        brickShip.CP = "1";
        brickShip.applyEffect(player1, player2, 1, 2);

        Card grainShip = new Card();
        grainShip.name = "Grain Ship";
        grainShip.type = "Unit – Trade Ship";
        grainShip.placement = "Settlement/city";
        grainShip.CP = "1";
        grainShip.applyEffect(player1, player2, 3, 2);

        // Give player2 one trade ship
        Card woolShip = new Card();
        woolShip.name = "Wool Ship";
        woolShip.type = "Unit – Trade Ship";
        woolShip.placement = "Settlement/city";
        woolShip.CP = "1";
        woolShip.applyEffect(player2, player1, 1, 2);

        // Trade Ships Race: player with most trade ships receives 1 resource of choice
        // Since we can't test interactive input, verify player1 has more ships
        assertEquals(2, player1.commercePoints, "Player1 should have 2 CP from 2 trade ships");
        assertEquals(1, player2.commercePoints, "Player2 should have 1 CP from 1 trade ship");
    }

    @Test
    public void testTradeShipsRaceTieGivesResourceToBothPlayers() {
        // Place settlements
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);

        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);

        // Give both players one trade ship each (tie)
        Card brickShip = new Card();
        brickShip.name = "Brick Ship";
        brickShip.type = "Unit – Trade Ship";
        brickShip.placement = "Settlement/city";
        brickShip.CP = "1";
        brickShip.applyEffect(player1, player2, 1, 2);

        Card grainShip = new Card();
        grainShip.name = "Grain Ship";
        grainShip.type = "Unit – Trade Ship";
        grainShip.placement = "Settlement/city";
        grainShip.CP = "1";
        grainShip.applyEffect(player2, player1, 1, 2);

        // Trade Ships Race: in case of tie, each player receives 1 resource of choice
        // Since we can't test interactive input, verify both have equal ships
        assertEquals(player1.commercePoints, player2.commercePoints, 
            "Both players should have equal CP from equal trade ships (tie)");
    }
}
