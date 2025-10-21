package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Tests to verify that advantage tokens work correctly:
 * - Only one player can have advantage at a time
 * - Advantage requires 3+ points AND being ahead of opponent
 * - VP is calculated dynamically (not permanently added)
 * - Advantage changes are properly detected and notified
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
    public void testTradeAdvantageRequires3CPAhead() {
        // Player1 has 2 CP, Player2 has 0 CP - player1 has <3, not enough for advantage
        player1.commercePoints = 2;
        player2.commercePoints = 0;
        
        assertFalse(player1.hasTradeTokenAgainst(player2), "Less than 3 CP is not enough");
        assertEquals(0, player1.currentScoreAgainst(player2), "Should have 0 total VP (no base VP, no advantage)");
        
        // Add 1 more CP to reach 3 total (and still ahead by 3)
        player1.commercePoints = 3;
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "3 CP and 1+ ahead should give advantage");
        assertEquals(1, player1.currentScoreAgainst(player2), "Should have 1 total VP (0 base + 1 advantage)");
    }

    @Test
    public void testStrengthAdvantageRequires3FPAhead() {
        // Player1 has 2 FP, Player2 has 0 FP - player1 has <3, not enough for advantage
        player1.strengthPoints = 2;
        player2.strengthPoints = 0;
        
        assertFalse(player1.hasStrengthTokenAgainst(player2), "Less than 3 FP is not enough");
        assertEquals(0, player1.currentScoreAgainst(player2), "Should have 0 total VP");
        
        // Add 1 more FP to reach 3 total (and still ahead)
        player1.strengthPoints = 3;
        
        assertTrue(player1.hasStrengthTokenAgainst(player2), "3 FP and 1+ ahead should give advantage");
        assertEquals(1, player1.currentScoreAgainst(player2), "Should have 1 total VP (0 base + 1 advantage)");
    }

    @Test
    public void testOnlyOnePlayerCanHaveTradeAdvantage() {
        // Player1 has 5 CP, Player2 has 2 CP (both >= 3, player1 ahead by 3)
        player1.commercePoints = 5;
        player2.commercePoints = 2;
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "Player1 should have advantage (5 > 2)");
        assertFalse(player2.hasTradeTokenAgainst(player1), "Player2 should NOT have advantage (2 < 5)");
        
        // Now player2 catches up and goes ahead by 1
        player2.commercePoints = 6;
        
        assertFalse(player1.hasTradeTokenAgainst(player2), "Player1 should lose advantage (5 < 6)");
        assertTrue(player2.hasTradeTokenAgainst(player1), "Player2 should gain advantage (6 > 5)");
    }

    @Test
    public void testOnlyOnePlayerCanHaveStrengthAdvantage() {
        // Player1 has 5 FP, Player2 has 2 FP (both >= 3 but player2 < 3)
        player1.strengthPoints = 5;
        player2.strengthPoints = 2;
        
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Player1 should have advantage (5 > 2)");
        assertFalse(player2.hasStrengthTokenAgainst(player1), "Player2 should NOT have advantage (2 < 3)");
        
        // Now player2 catches up to 3+ and goes ahead by 1
        player2.strengthPoints = 6;
        
        assertFalse(player1.hasStrengthTokenAgainst(player2), "Player1 should lose advantage (5 < 6)");
        assertTrue(player2.hasStrengthTokenAgainst(player1), "Player2 should gain advantage (6 > 5)");
    }

    @Test
    public void testAdvantageVPIsDynamic() {
        // Start with player1 having advantage (3 CP, ahead of opponent)
        player1.commercePoints = 3;
        player1.victoryPoints = 2; // 2 base VP
        player2.commercePoints = 0;
        
        assertEquals(3, player1.currentScoreAgainst(player2), "Should have 3 total VP (2 base + 1 advantage)");
        
        // Player2 catches up to tie, player1 loses advantage (needs to be ahead)
        player2.commercePoints = 3; // now tied, loses advantage
        
        assertEquals(2, player1.currentScoreAgainst(player2), "Should have 2 total VP (2 base, no advantage)");
        assertEquals(2, player1.victoryPoints, "Base VP should still be 2 (unchanged)");
    }

    @Test
    public void testBothAdvantagesCanBeHeldSimultaneously() {
        // Player1 gets both CP and FP advantages
        player1.commercePoints = 5;
        player1.strengthPoints = 4;
        player1.victoryPoints = 1; // 1 base VP
        player2.commercePoints = 0;
        player2.strengthPoints = 0;
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "Should have trade advantage");
        assertTrue(player1.hasStrengthTokenAgainst(player2), "Should have strength advantage");
        assertEquals(3, player1.currentScoreAgainst(player2), "Should have 3 total VP (1 base + 2 advantages)");
    }

    @Test
    public void testAdvantageRequiresBeingAhead() {
        // Both players have 3 CP - no one should have advantage (need to be ahead)
        player1.commercePoints = 3;
        player2.commercePoints = 3;
        
        assertFalse(player1.hasTradeTokenAgainst(player2), "Equal CP means no advantage");
        assertFalse(player2.hasTradeTokenAgainst(player1), "Equal CP means no advantage");
        
        // Player1 has 3, Player2 has 2 - player1 is 1 ahead (enough with >= 3 CP!)
        player2.commercePoints = 2;
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "3 CP and 1 ahead should give advantage");
        assertFalse(player2.hasTradeTokenAgainst(player1), "Player2 has only 2 CP (< 3)");
        
        // Player1 has 3, Player2 has 0 - player1 is 3 ahead (also enough!)
        player2.commercePoints = 0;
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "3 CP and ahead should give advantage");
    }

    @Test
    public void testPlacingCardThatGivesAdvantage() {
        // Player1 starts with 2 CP
        player1.commercePoints = 2;
        player2.commercePoints = 0;
        
        assertFalse(player1.hasTradeTokenAgainst(player2), "Should not have advantage yet");
        
        // Place a ship that gives +1 CP, reaching 3 CP
        Card ship = new Card();
        ship.name = "Brick Ship";
        ship.type = "Unit – Trade Ship";
        ship.placement = "Settlement/city";
        ship.CP = "1";
        ship.cost = "";
        ship.applyEffect(player1, player2, 1, 2);
        
        assertEquals(3, player1.commercePoints, "Should have 3 CP now");
        assertTrue(player1.hasTradeTokenAgainst(player2), "Should have trade advantage now");
        assertEquals(1, player1.currentScoreAgainst(player2), "Should have 1 total VP (0 base + 1 advantage)");
    }

    @Test
    public void testGetPointsSummaryShowsAdvantages() {
        // Set up player with both advantages
        player1.commercePoints = 4;
        player1.strengthPoints = 5;
        player1.victoryPoints = 2;
        player2.commercePoints = 0;
        player2.strengthPoints = 0;
        
        String summary = player1.getPointsSummary(player2);
        
        assertTrue(summary.contains("VP=2"), "Should show base VP");
        assertTrue(summary.contains("Trade+1"), "Should show trade advantage");
        assertTrue(summary.contains("Strength+1"), "Should show strength advantage");
        assertTrue(summary.contains("Total: 4"), "Should show total including advantages");
        assertTrue(summary.contains("CP=4"), "Should show CP");
        assertTrue(summary.contains("FP=5"), "Should show FP");
    }

    @Test
    public void testGetPointsSummaryWithoutAdvantages() {
        player1.commercePoints = 1;
        player1.strengthPoints = 1;
        player1.victoryPoints = 3;
        player2.commercePoints = 0;
        player2.strengthPoints = 0;
        
        String summary = player1.getPointsSummary(player2);
        
        assertTrue(summary.contains("VP=3"), "Should show base VP");
        assertFalse(summary.contains("Trade+1"), "Should not show trade advantage");
        assertFalse(summary.contains("Strength+1"), "Should not show strength advantage");
        assertFalse(summary.contains("Total:"), "Should not show total when same as base");
    }

    @Test
    public void testPrintPrincipalityShowsAdvantages() {
        // Set up player with both advantages
        player1.commercePoints = 4;
        player1.strengthPoints = 5;
        player1.victoryPoints = 2;
        player2.commercePoints = 0;
        player2.strengthPoints = 0;
        
        String principality = player1.printPrincipality(player2);
        
        assertTrue(principality.contains("VP=2"), "Should show base VP");
        assertTrue(principality.contains("Trade+1"), "Should show trade advantage");
        assertTrue(principality.contains("Strength+1"), "Should show strength advantage");
        assertTrue(principality.contains("Total: 4"), "Should show total including advantages");
        assertTrue(principality.contains("CP=4"), "Should show CP");
        assertTrue(principality.contains("FP=5"), "Should show FP");
    }

    @Test
    public void testPrintPrincipalityWithoutAdvantages() {
        player1.commercePoints = 1;
        player1.strengthPoints = 1;
        player1.victoryPoints = 3;
        player2.commercePoints = 0;
        player2.strengthPoints = 0;
        
        String principality = player1.printPrincipality(player2);
        
        assertTrue(principality.contains("VP=3"), "Should show base VP");
        assertFalse(principality.contains("Trade+1"), "Should not show trade advantage");
        assertFalse(principality.contains("Strength+1"), "Should not show strength advantage");
        assertFalse(principality.contains("Total:"), "Should not show total when same as base");
    }

    @Test
    public void testOpponentLosesAdvantageWhenPlayerCatchesUpByPlacingCard() {
        // Setup: Player 1 has 3 CP (has advantage), Player 2 has 2 CP
        player1.commercePoints = 3;
        player2.commercePoints = 2;
        
        // Setup player2 with a settlement so they can place expansion cards
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player2.placeCard(2, 2, settlement);
        
        assertTrue(player1.hasTradeTokenAgainst(player2), "Player1 should have trade advantage initially");
        assertFalse(player2.hasTradeTokenAgainst(player1), "Player2 should not have advantage");
        
        // Player 2 places a card that gives +1 CP, bringing them to 3 CP (tied)
        Card ship = new Card();
        ship.name = "Test Trade Ship";
        ship.type = "Unit – Trade Ship";
        ship.placement = "Settlement/city";
        ship.CP = "1";
        ship.cost = "";
        
        // Apply the effect - this should cause Player 1 to lose advantage due to tie
        ship.applyEffect(player2, player1, 1, 2);
        
        // Both players should now have 3 CP and be tied
        assertEquals(3, player2.commercePoints, "Player2 should have 3 CP now");
        assertEquals(3, player1.commercePoints, "Player1 should still have 3 CP");
        
        // CRITICAL: Both players should NOT have advantage when tied
        assertFalse(player1.hasTradeTokenAgainst(player2), "Player1 should lose advantage when tied");
        assertFalse(player2.hasTradeTokenAgainst(player1), "Player2 should not have advantage when tied");
    }
}
