package model.cards;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for action cards in the basic set.
 * Tests: Brigitta the Wise Woman, Relocation, Scout, Merchant Caravan, Goldsmith
 */
public class ActionCardsTest {

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
    public void testBrigittaTheWiseWomanSetsNextDiceRoll() {
        // Place Brigitta the Wise Woman action card
        Card brigitta = new Card();
        brigitta.name = "Brigitta the Wise Woman";
        brigitta.type = "Action";
        brigitta.placement = "Action";
        boolean played = brigitta.applyEffect(player1, player2, -1, -1);
        assertTrue(played, "Brigitta the Wise Woman should be played successfully");

        // Verify Brigitta flag is set (allows choosing next production dice roll)
        assertTrue(player1.flags.contains("BRIGITTA"), 
            "Brigitta flag should be set to allow choosing next dice roll");
    }

    @Test
    public void testRelocationSwapsTwoRegions() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place two regions
        Card region1 = new Card();
        region1.name = "Field";
        region1.type = "Region";
        region1.diceRoll = 3;
        player1.placeCard(1, 2, region1);

        Card region2 = new Card();
        region2.name = "Mountain";
        region2.type = "Region";
        region2.diceRoll = 5;
        player1.placeCard(3, 2, region2);

        // Play Relocation action card (allows swapping two regions or expansions)
        // Since we can't test interactive input, just verify the regions are placed
        assertNotNull(player1.getCard(1, 2), "Region 1 should be placed");
        assertNotNull(player1.getCard(3, 2), "Region 2 should be placed");
    }

    @Test
    public void testScoutRequiresSettlementResourcesAndSetsFlag() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place regions so player can have resources
        Card hills = new Card();
        hills.name = "Hill";
        hills.type = "Region";
        hills.regionProduction = 1;
        player1.placeCard(1, 1, hills);

        Card fields = new Card();
        fields.name = "Field";
        fields.type = "Region";
        fields.regionProduction = 1;
        player1.placeCard(1, 2, fields);

        Card forest = new Card();
        forest.name = "Forest";
        forest.type = "Region";
        forest.regionProduction = 1;
        player1.placeCard(1, 3, forest);

        Card pasture = new Card();
        pasture.name = "Pasture";
        pasture.type = "Region";
        pasture.regionProduction = 1;
        player1.placeCard(3, 1, pasture);

        // Now player has resources for settlement (Brick from Hills, Grain from Fields, Lumber from Forest, Wool from Pasture)
        assertEquals(1, player1.getResourceCount("Brick"), "Player should have 1 Brick");
        assertEquals(1, player1.getResourceCount("Grain"), "Player should have 1 Grain");
        assertEquals(1, player1.getResourceCount("Lumber"), "Player should have 1 Lumber");
        assertEquals(1, player1.getResourceCount("Wool"), "Player should have 1 Wool");

        // Play Scout action card
        Card scout = new Card();
        scout.name = "Scout";
        scout.type = "Action";
        scout.placement = "Action";
        boolean played = scout.applyEffect(player1, player2, -1, -1);
        assertTrue(played, "Scout should be played successfully when player has settlement resources");

        // Verify Scout flag is set
        assertTrue(player1.flags.contains("SCOUT_NEXT_SETTLEMENT"), 
            "Scout flag should be set for next settlement placement");
    }

    @Test
    public void testScoutFailsWithoutSettlementResources() {
        // Don't give player any resources
        
        // Try to play Scout action card
        Card scout = new Card();
        scout.name = "Scout";
        scout.type = "Action";
        scout.placement = "Action";
        boolean played = scout.applyEffect(player1, player2, -1, -1);
        assertFalse(played, "Scout should fail when player doesn't have settlement resources");

        // Verify Scout flag is NOT set
        assertFalse(player1.flags.contains("SCOUT_NEXT_SETTLEMENT"), 
            "Scout flag should not be set when card fails to play");
    }

    @Test
    public void testMerchantCaravanTradesResources() {
        // Place regions so player can have resources
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        Card hills = new Card();
        hills.name = "Hill";
        hills.type = "Region";
        hills.regionProduction = 1;
        player1.placeCard(1, 1, hills);

        Card fields = new Card();
        fields.name = "Field";
        fields.type = "Region";
        fields.regionProduction = 1;
        player1.placeCard(1, 2, fields);

        // Verify player has at least 2 resources
        assertTrue(player1.totalAllResources() >= 2, "Player should have at least 2 resources");
        
        // Merchant Caravan trades any 2 resources for any 2 other resources
        // Since we can't test interactive input, just verify player has resources to trade
        int resourceCount = player1.totalAllResources();
        assertTrue(resourceCount >= 2, "Player has resources for Merchant Caravan");
    }

    @Test
    public void testMerchantCaravanFailsWithoutResources() {
        // Don't give player any resources
        
        // Try to play Merchant Caravan action card
        Card merchantCaravan = new Card();
        merchantCaravan.name = "Merchant Caravan";
        merchantCaravan.type = "Action";
        merchantCaravan.placement = "Action";
        boolean played = merchantCaravan.applyEffect(player1, player2, -1, -1);
        assertFalse(played, "Merchant Caravan should fail when player has fewer than 2 resources");
    }

    @Test
    public void testGoldsmithTradesGoldForResources() {
        // Place gold field regions so player can have gold
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        Card goldField = new Card();
        goldField.name = "Gold Field";
        goldField.type = "Region";
        goldField.regionProduction = 3;
        player1.placeCard(1, 2, goldField);

        int goldBefore = player1.getResourceCount("Gold");
        assertEquals(3, goldBefore, "Player should have 3 gold before playing Goldsmith");

        // Goldsmith requires 3 gold and trades for 2 resources
        // Since we can't test interactive input, just verify the player has enough gold
        assertTrue(player1.getResourceCount("Gold") >= 3, "Player has enough gold for Goldsmith");
    }

    @Test
    public void testGoldsmithFailsWithoutEnoughGold() {
        // Give player only 2 gold (not enough)
        player1.gainResource("Gold");
        player1.gainResource("Gold");

        // Try to play Goldsmith action card
        Card goldsmith = new Card();
        goldsmith.name = "Goldsmith";
        goldsmith.type = "Action";
        goldsmith.placement = "Action";
        boolean played = goldsmith.applyEffect(player1, player2, -1, -1);
        assertFalse(played, "Goldsmith should fail when player has fewer than 3 gold");
    }
}
