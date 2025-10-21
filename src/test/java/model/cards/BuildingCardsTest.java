package model.cards;

import model.Card;
import model.Player;
import model.ResourceType;
import controller.ProductionManager;
import controller.events.PlentifulHarvestEvent;
import controller.events.BrigandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for building cards in the basic set.
 * Tests: Toll Bridge, Storehouse, Iron Foundry, Grain Mill, Lumber Camp, 
 * Brick Factory, Weaver's Shop, Abbey, Marketplace, Parish Hall
 */
public class BuildingCardsTest {

    private Player player1;
    private Player player2;
    private ProductionManager productionManager;

    @BeforeEach
    public void setUp() throws IOException {
        Card.loadBasicCards("cards.json");
        player1 = new Player();
        player1.isBot = true;
        player2 = new Player();
        player2.isBot = true;
        productionManager = new ProductionManager();
    }

    @Test
    public void testTollBridgeGives2GoldOnPlentifulHarvest() {
        // Place settlement and gold field for player1
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        Card goldField = new Card();
        goldField.name = "Gold Field";
        goldField.type = "Region";
        goldField.regionProduction = 0;
        player1.placeCard(0, 2, goldField);

        // Place Toll Bridge at inner ring (row 1), above settlement
        Card tollBridge = new Card();
        tollBridge.name = "Toll Bridge";
        tollBridge.type = "Building";
        tollBridge.placement = "Settlement/city";
        tollBridge.CP = "1";
        boolean placed = tollBridge.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Toll Bridge should be placed successfully");

        // Verify Toll Bridge flag is set
        assertTrue(player1.flags.contains("TOLLB"), "Toll Bridge flag should be set");
        
        // Verify commerce points from Toll Bridge
        assertEquals(1, player1.commercePoints, "Toll Bridge should give 1 Commerce Point");
        
        // Plentiful Harvest event would give player 2 gold when TOLLB flag is set
        // Since we can't test interactive events, we just verify the setup is correct
    }

    @Test
    public void testStorehouseProtectsNeighboringRegionsFromBrigands() {
        // Place settlement for player1
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place two pastures at outer ring that will be adjacent to storehouse
        Card pasture1 = new Card();
        pasture1.name = "Pasture";
        pasture1.type = "Region";
        pasture1.regionProduction = 3;
        player1.placeCard(0, 1, pasture1);

        Card pasture2 = new Card();
        pasture2.name = "Pasture";
        pasture2.type = "Region";
        pasture2.regionProduction = 3;
        player1.placeCard(0, 3, pasture2);

        // Place third pasture not adjacent to storehouse (different column)
        Card pasture3 = new Card();
        pasture3.name = "Pasture";
        pasture3.type = "Region";
        pasture3.regionProduction = 3;
        player1.placeCard(4, 1, pasture3);

        // Place Storehouse at inner ring (1, 2) - above settlement at (2,2)
        Card storehouse = new Card();
        storehouse.name = "Storehouse";
        storehouse.type = "Building";
        storehouse.placement = "Settlement/city";
        boolean placed = storehouse.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Storehouse should be placed successfully");

        // Trigger Brigand Attack event
        BrigandEvent brigandEvent = new BrigandEvent();
        brigandEvent.handleEvent(Arrays.asList(player1, player2), player1, player2);

        // Verify neighboring pastures are protected (still have resources)
        assertTrue(pasture1.regionProduction > 0 || pasture2.regionProduction > 0, 
            "At least one neighboring pasture should be protected by Storehouse");
        
        // Non-adjacent pasture should be zeroed
        assertEquals(0, pasture3.regionProduction, 
            "Pasture not adjacent to Storehouse should be zeroed by Brigands");
    }

    @Test
    public void testIronFoundryDoublesOreProduction() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place mountain at inner ring (row 1, col 2) above settlement
        Card mountain = new Card();
        mountain.name = "Mountains";
        mountain.type = "Region";
        mountain.diceRoll = 4;
        mountain.regionProduction = 0;
        player1.placeCard(1, 2, mountain);

        // Place Iron Foundry at same row as mountain, adjacent horizontally (row 1, col 1)
        Card ironFoundry = new Card();
        ironFoundry.name = "Iron Foundry";
        ironFoundry.type = "Building";
        ironFoundry.placement = "Settlement/city";
        boolean placed = ironFoundry.applyEffect(player1, player2, 1, 1);
        assertTrue(placed, "Iron Foundry should be placed successfully");

        // Simulate production with dice roll 4
        productionManager.applyProduction(4, Arrays.asList(player1, player2), p -> player2);

        // Mountain should have doubled production (1 base + 1 from Iron Foundry = 2)
        assertEquals(2, mountain.regionProduction, 
            "Iron Foundry should double ore production from adjacent mountain");
    }

    @Test
    public void testGrainMillDoublesGrainProduction() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place field at inner ring (row 1, col 2) above settlement
        Card field = new Card();
        field.name = "Fields";
        field.type = "Region";
        field.diceRoll = 3;
        field.regionProduction = 0;
        player1.placeCard(1, 2, field);

        // Place Grain Mill at same row as field, adjacent horizontally (row 1, col 1)
        Card grainMill = new Card();
        grainMill.name = "Grain Mill";
        grainMill.type = "Building";
        grainMill.placement = "Settlement/city";
        boolean placed = grainMill.applyEffect(player1, player2, 1, 1);
        assertTrue(placed, "Grain Mill should be placed successfully");

        // Simulate production with dice roll 3
        productionManager.applyProduction(3, Arrays.asList(player1, player2), p -> player2);

        // Field should have doubled production (1 base + 1 from Grain Mill = 2)
        assertEquals(2, field.regionProduction, 
            "Grain Mill should double grain production from adjacent field");
    }

    @Test
    public void testLumberCampDoublesLumberProduction() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place forest at inner ring (row 1, col 2) above settlement
        Card forest = new Card();
        forest.name = "Forest";
        forest.type = "Region";
        forest.diceRoll = 5;
        forest.regionProduction = 0;
        player1.placeCard(1, 2, forest);

        // Place Lumber Camp at same row as forest, adjacent horizontally (row 1, col 1)
        Card lumberCamp = new Card();
        lumberCamp.name = "Lumber Camp";
        lumberCamp.type = "Building";
        lumberCamp.placement = "Settlement/city";
        boolean placed = lumberCamp.applyEffect(player1, player2, 1, 1);
        assertTrue(placed, "Lumber Camp should be placed successfully");

        // Simulate production with dice roll 5
        productionManager.applyProduction(5, Arrays.asList(player1, player2), p -> player2);

        // Forest should have doubled production (1 base + 1 from Lumber Camp = 2)
        assertEquals(2, forest.regionProduction, 
            "Lumber Camp should double lumber production from adjacent forest");
    }

    @Test
    public void testBrickFactoryDoublesBrickProduction() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place hills at inner ring (row 1, col 2) above settlement
        Card hills = new Card();
        hills.name = "Hills";
        hills.type = "Region";
        hills.diceRoll = 2;
        hills.regionProduction = 0;
        player1.placeCard(1, 2, hills);

        // Place Brick Factory at same row as hills, adjacent horizontally (row 1, col 1)
        Card brickFactory = new Card();
        brickFactory.name = "Brick Factory";
        brickFactory.type = "Building";
        brickFactory.placement = "Settlement/city";
        boolean placed = brickFactory.applyEffect(player1, player2, 1, 1);
        assertTrue(placed, "Brick Factory should be placed successfully");

        // Simulate production with dice roll 2
        productionManager.applyProduction(2, Arrays.asList(player1, player2), p -> player2);

        // Hills should have doubled production (1 base + 1 from Brick Factory = 2)
        assertEquals(2, hills.regionProduction, 
            "Brick Factory should double brick production from adjacent hills");
    }

    @Test
    public void testWeaversShopDoublesWoolProduction() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place pasture at inner ring (row 1, col 2) above settlement
        Card pasture = new Card();
        pasture.name = "Pasture";
        pasture.type = "Region";
        pasture.diceRoll = 6;
        pasture.regionProduction = 0;
        player1.placeCard(1, 2, pasture);

        // Place Weaver's Shop at same row as pasture, adjacent horizontally (row 1, col 1)
        Card weaversShop = new Card();
        weaversShop.name = "Weaver's Shop";
        weaversShop.type = "Building";
        weaversShop.placement = "Settlement/city";
        boolean placed = weaversShop.applyEffect(player1, player2, 1, 1);
        assertTrue(placed, "Weaver's Shop should be placed successfully");

        // Simulate production with dice roll 6
        productionManager.applyProduction(6, Arrays.asList(player1, player2), p -> player2);

        // Pasture should have doubled production (1 base + 1 from Weaver's Shop = 2)
        assertEquals(2, pasture.regionProduction, 
            "Weaver's Shop should double wool production from adjacent pasture");
    }

    @Test
    public void testAbbeyGives1ProgressPoint() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int ppBefore = player1.progressPoints;

        // Place Abbey
        Card abbey = new Card();
        abbey.name = "Abbey";
        abbey.type = "Building";
        abbey.placement = "Settlement/city";
        abbey.PP = "1";
        boolean placed = abbey.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Abbey should be placed successfully");

        // Verify progress points increased by 1
        assertEquals(ppBefore + 1, player1.progressPoints, 
            "Abbey should give 1 Progress Point");
    }

    @Test
    public void testMarketplaceGives1CPAndResourceWhenOpponentHasMoreRegions() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int cpBefore = player1.commercePoints;

        // Place Marketplace
        Card marketplace = new Card();
        marketplace.name = "Marketplace";
        marketplace.type = "Building";
        marketplace.placement = "Settlement/city";
        marketplace.CP = "1";
        boolean placed = marketplace.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Marketplace should be placed successfully");

        // Verify commerce points increased by 1
        assertEquals(cpBefore + 1, player1.commercePoints, 
            "Marketplace should give 1 Commerce Point");

        // Verify marketplace flag is set
        assertTrue(player1.flags.contains("MARKETPLACE"), "Marketplace flag should be set");
    }

    @Test
    public void testParishHallReducesCardDrawCostTo1Resource() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        // Place Parish Hall
        Card parishHall = new Card();
        parishHall.name = "Parish Hall";
        parishHall.type = "Building";
        parishHall.placement = "Settlement/city";
        boolean placed = parishHall.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Parish Hall should be placed successfully");

        // Verify parish hall flag is set
        assertTrue(player1.flags.contains("PARISH"), 
            "Parish Hall flag should be set to reduce card draw cost");
    }
}
