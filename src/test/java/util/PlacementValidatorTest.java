package util;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlacementValidator.
 * These tests demonstrate improved testability after refactoring.
 */
public class PlacementValidatorTest {

    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player();
        player.isBot = true; // Avoid console input
    }

    @Test
    public void testIsCenterSlot() {
        assertFalse(PlacementValidator.isCenterSlot(0), "Row 0 should not be center");
        assertFalse(PlacementValidator.isCenterSlot(1), "Row 1 should not be center");
        assertTrue(PlacementValidator.isCenterSlot(2), "Row 2 should be center");
        assertFalse(PlacementValidator.isCenterSlot(3), "Row 3 should not be center");
        assertFalse(PlacementValidator.isCenterSlot(4), "Row 4 should not be center");
    }

    @Test
    public void testIsSettlementOrCity() {
        Card settlement = new Card();
        settlement.name = "Settlement";
        assertTrue(PlacementValidator.isSettlementOrCity(settlement));

        Card city = new Card();
        city.name = "City";
        assertTrue(PlacementValidator.isSettlementOrCity(city));

        Card road = new Card();
        road.name = "Road";
        assertFalse(PlacementValidator.isSettlementOrCity(road));

        assertFalse(PlacementValidator.isSettlementOrCity(null));
    }

    @Test
    public void testNameMatches() {
        Card card = new Card();
        card.name = "Settlement";

        assertTrue(PlacementValidator.nameMatches(card, "Settlement"));
        assertTrue(PlacementValidator.nameMatches(card, "settlement")); // Case-insensitive
        assertTrue(PlacementValidator.nameMatches(card, "Road", "Settlement", "City"));
        assertFalse(PlacementValidator.nameMatches(card, "Road"));
        assertFalse(PlacementValidator.nameMatches(null, "Settlement"));
    }

    @Test
    public void testBuildingBoostsRegion() {
        assertTrue(PlacementValidator.buildingBoostsRegion("Iron Foundry", "Mountain"));
        assertTrue(PlacementValidator.buildingBoostsRegion("Grain Mill", "Field"));
        assertTrue(PlacementValidator.buildingBoostsRegion("Lumber Camp", "Forest"));
        assertTrue(PlacementValidator.buildingBoostsRegion("Brick Factory", "Hill"));
        assertTrue(PlacementValidator.buildingBoostsRegion("Weaver's Shop", "Pasture"));

        // Case-insensitive
        assertTrue(PlacementValidator.buildingBoostsRegion("iron foundry", "mountain"));

        // Non-matching combinations
        assertFalse(PlacementValidator.buildingBoostsRegion("Iron Foundry", "Field"));
        assertFalse(PlacementValidator.buildingBoostsRegion("Grain Mill", "Mountain"));

        // Null checks
        assertFalse(PlacementValidator.buildingBoostsRegion(null, "Mountain"));
        assertFalse(PlacementValidator.buildingBoostsRegion("Iron Foundry", null));
    }

    @Test
    public void testIsRegionCard() {
        Card region = new Card();
        region.type = "Region";
        assertTrue(PlacementValidator.isRegionCard(region));

        Card building = new Card();
        building.type = "Building";
        assertFalse(PlacementValidator.isRegionCard(building));

        assertFalse(PlacementValidator.isRegionCard(null));
    }

    @Test
    public void testIsExpansionCard() {
        Card expansion = new Card();
        expansion.placement = "Settlement/City Expansions";
        assertTrue(PlacementValidator.isExpansionCard(expansion));

        Card action = new Card();
        action.placement = "Action";
        assertFalse(PlacementValidator.isExpansionCard(action));

        assertFalse(PlacementValidator.isExpansionCard(null));
    }

    @Test
    public void testIsAboveOrBelowSettlementOrCity() {
        // Place a settlement in the center
        Card settlement = new Card();
        settlement.name = "Settlement";
        player.placeCard(2, 2, settlement);

        // Inner ring: row 1 and row 3 should be valid (above and below)
        assertTrue(PlacementValidator.isAboveOrBelowSettlementOrCity(player, 1, 2),
                "Row 1 (above settlement) should be valid");
        assertTrue(PlacementValidator.isAboveOrBelowSettlementOrCity(player, 3, 2),
                "Row 3 (below settlement) should be valid");

        // Not adjacent to settlement
        assertFalse(PlacementValidator.isAboveOrBelowSettlementOrCity(player, 1, 1),
                "Row 1, Col 1 (not above settlement) should be invalid");
    }

    @Test
    public void testOuterRingRequiresInnerRing() {
        // Place a settlement in the center
        Card settlement = new Card();
        settlement.name = "Settlement";
        player.placeCard(2, 2, settlement);

        // Outer ring (row 0) should require inner ring (row 1) to be filled first
        assertFalse(PlacementValidator.isAboveOrBelowSettlementOrCity(player, 0, 2),
                "Outer ring should not be valid without inner ring");

        // Fill inner ring
        Card building = new Card();
        building.name = "Test Building";
        player.placeCard(1, 2, building);

        // Now outer ring should be valid
        assertTrue(PlacementValidator.isAboveOrBelowSettlementOrCity(player, 0, 2),
                "Outer ring should be valid after inner ring is filled");
    }
}
