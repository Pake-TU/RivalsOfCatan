package controller;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductionManager.
 * Tests production logic including booster adjacency for outer rows.
 */
public class ProductionManagerTest {

    private ProductionManager productionManager;
    private Player player;
    private Player opponent;

    @BeforeEach
    public void setUp() {
        productionManager = new ProductionManager();
        player = new Player();
        player.isBot = true; // Avoid console input
        opponent = new Player();
        opponent.isBot = true;
    }

    /**
     * Test that cards on outer row 0 behave like they're on row 1 for adjacency.
     * Example from issue: Iron foundry on row 0 col 3, mountain on row 1 col 4
     * should behave like iron foundry is on row 1 col 3.
     */
    @Test
    public void testOuterRow0BehavesLikeRow1ForBooster() {
        // Place a mountain (region) on row 1, col 4
        Card mountain = new Card();
        mountain.name = "Mountain";
        mountain.type = "Region";
        mountain.diceRoll = 3;
        mountain.regionProduction = 1;
        player.placeCard(1, 4, mountain);

        // Place an iron foundry (booster building) on row 0, col 3
        // This should boost the mountain because row 0 behaves like row 1
        Card ironFoundry = new Card();
        ironFoundry.name = "Iron Foundry";
        ironFoundry.type = "Building";
        player.placeCard(0, 3, ironFoundry);

        // Apply production for dice face 3 (mountain's face)
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(3, players, p -> opponent);

        // Mountain should have gained 2 resources (1 base + 1 from booster) = 3 total
        assertEquals(3, mountain.regionProduction,
                "Mountain should have 3 resources (1 initial + 2 from production with booster)");
    }

    /**
     * Test that cards on outer row 4 behave like they're on row 3 for adjacency.
     */
    @Test
    public void testOuterRow4BehavesLikeRow3ForBooster() {
        // Place a field (region) on row 3, col 2
        Card field = new Card();
        field.name = "Field";
        field.type = "Region";
        field.diceRoll = 4;
        field.regionProduction = 1;
        player.placeCard(3, 2, field);

        // Place a grain mill (booster building) on row 4, col 1
        // This should boost the field because row 4 behaves like row 3
        Card grainMill = new Card();
        grainMill.name = "Grain Mill";
        grainMill.type = "Building";
        player.placeCard(4, 1, grainMill);

        // Apply production for dice face 4 (field's face)
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(4, players, p -> opponent);

        // Field should have gained 2 resources (1 base + 1 from booster) = 3 total
        assertEquals(3, field.regionProduction,
                "Field should have 3 resources (1 initial + 2 from production with booster)");
    }

    /**
     * Test that a booster on outer row 0 doesn't affect regions on row 2 or row 3.
     */
    @Test
    public void testOuterRow0DoesNotBoostRow2() {
        // Place a mountain on row 2 (center), col 3
        Card mountain = new Card();
        mountain.name = "Mountain";
        mountain.type = "Region";
        mountain.diceRoll = 3;
        mountain.regionProduction = 1;
        player.placeCard(2, 3, mountain);

        // Place an iron foundry on row 0, col 2 (left of mountain if on same row)
        // This should NOT boost because row 0 normalizes to row 1, not row 2
        Card ironFoundry = new Card();
        ironFoundry.name = "Iron Foundry";
        ironFoundry.type = "Building";
        player.placeCard(0, 2, ironFoundry);

        // Apply production
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(3, players, p -> opponent);

        // Mountain should only have base production (no boost)
        assertEquals(2, mountain.regionProduction,
                "Mountain should have 2 resources (1 initial + 1 base production, no booster)");
    }

    /**
     * Test that boosters on inner rows (1, 3) still work normally.
     */
    @Test
    public void testInnerRowBoostersWorkNormally() {
        // Place a forest on row 1, col 3
        Card forest = new Card();
        forest.name = "Forest";
        forest.type = "Region";
        forest.diceRoll = 5;
        forest.regionProduction = 1;
        player.placeCard(1, 3, forest);

        // Place a lumber camp on row 1, col 2 (left of forest)
        Card lumberCamp = new Card();
        lumberCamp.name = "Lumber Camp";
        lumberCamp.type = "Building";
        player.placeCard(1, 2, lumberCamp);

        // Apply production
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(5, players, p -> opponent);

        // Forest should have gained 2 resources (1 base + 1 from booster)
        assertEquals(3, forest.regionProduction,
                "Forest should have 3 resources (1 initial + 2 from production with booster)");
    }

    /**
     * Test that boosters need to be adjacent in columns (left or right).
     * A booster on row 0 col 1 should not affect a region on row 1 col 3.
     */
    @Test
    public void testBoosterMustBeAdjacentInColumns() {
        // Place a mountain on row 1, col 3
        Card mountain = new Card();
        mountain.name = "Mountain";
        mountain.type = "Region";
        mountain.diceRoll = 3;
        mountain.regionProduction = 1;
        player.placeCard(1, 3, mountain);

        // Place an iron foundry on row 0, col 1 (not adjacent to col 3)
        Card ironFoundry = new Card();
        ironFoundry.name = "Iron Foundry";
        ironFoundry.type = "Building";
        player.placeCard(0, 1, ironFoundry);

        // Apply production
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(3, players, p -> opponent);

        // Mountain should only have base production (no boost)
        assertEquals(2, mountain.regionProduction,
                "Mountain should have 2 resources (1 initial + 1 base production, no booster - not adjacent)");
    }

    /**
     * Test production without any boosters works normally.
     */
    @Test
    public void testProductionWithoutBooster() {
        // Place a pasture on row 3, col 2
        Card pasture = new Card();
        pasture.name = "Pasture";
        pasture.type = "Region";
        pasture.diceRoll = 2;
        pasture.regionProduction = 1;
        player.placeCard(3, 2, pasture);

        // Apply production
        List<Player> players = new ArrayList<>();
        players.add(player);
        productionManager.applyProduction(2, players, p -> opponent);

        // Pasture should have gained 1 resource (base production only)
        assertEquals(2, pasture.regionProduction,
                "Pasture should have 2 resources (1 initial + 1 base production)");
    }
}
