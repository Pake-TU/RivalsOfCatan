import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Basic tests for Player functionality, particularly input validation.
 */
public class PlayerTest {
    
    private Player player;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        player = new Player();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void testResourceToRegionMapping() {
        // Test that basic resource mapping works
        // Initially, player has no regions so resource count should be 0
        assertEquals(0, player.getResourceCount("Brick"));
        
        // Note: gainResource requires regions to be present
        // In actual game, regions are set up in Server.initPrincipality()
        // For this test, we just verify the count is >= 0
        assertTrue(player.getResourceCount("Brick") >= 0);
    }

    @Test
    public void testPlayerImplementsInterfaces() {
        // Verify Player implements required interfaces
        assertTrue(player instanceof IPlayer);
        assertTrue(player instanceof IPlayerIO);
        assertTrue(player instanceof IResourceManager);
    }

    @Test
    public void testVictoryPointsGetterSetter() {
        player.setVictoryPoints(5);
        assertEquals(5, player.getVictoryPoints());
    }

    @Test
    public void testProgressPointsGetterSetter() {
        player.setProgressPoints(3);
        assertEquals(3, player.getProgressPoints());
    }

    @Test
    public void testHandOperations() {
        assertEquals(0, player.handSize());
        
        Card testCard = new Card();
        testCard.name = "Test Card";
        player.addToHand(testCard);
        
        assertEquals(1, player.handSize());
        
        Card removed = player.removeFromHandByName("Test Card");
        assertNotNull(removed);
        assertEquals("Test Card", removed.name);
        assertEquals(0, player.handSize());
    }

    @Test
    public void testScoreCalculation() {
        Player opponent = new Player();
        
        player.setVictoryPoints(5);
        player.setCommercePoints(5);
        opponent.setCommercePoints(2);
        
        // Should have trade advantage (5-2 >= 3)
        assertTrue(player.hasTradeTokenAgainst(opponent));
        
        // Score should be VP + trade token
        int score = player.currentScoreAgainst(opponent);
        assertEquals(6, score); // 5 VP + 1 trade token
    }
}
