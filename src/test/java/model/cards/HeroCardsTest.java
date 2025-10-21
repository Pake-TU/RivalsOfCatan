package model.cards;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for hero/unit cards in the basic set.
 * Tests: Austin, Harald, Inga, Osmund, Candamir, Siglind
 */
public class HeroCardsTest {

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
    public void testAustinGives1SPAnd2FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Austin
        Card austin = new Card();
        austin.name = "Austin";
        austin.type = "Unit – Hero";
        austin.placement = "Settlement/city";
        austin.SP = "1";
        austin.FP = "2";
        boolean placed = austin.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Austin should be placed successfully");

        // Verify skill points increased by 1
        assertEquals(spBefore + 1, player1.skillPoints, 
            "Austin should give 1 Skill Point");

        // Verify strength points increased by 2
        assertEquals(fpBefore + 2, player1.strengthPoints, 
            "Austin should give 2 Strength Points");
    }

    @Test
    public void testHaraldGives2SPAnd1FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Harald
        Card harald = new Card();
        harald.name = "Harald";
        harald.type = "Unit – Hero";
        harald.placement = "Settlement/city";
        harald.SP = "2";
        harald.FP = "1";
        boolean placed = harald.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Harald should be placed successfully");

        // Verify skill points increased by 2
        assertEquals(spBefore + 2, player1.skillPoints, 
            "Harald should give 2 Skill Points");

        // Verify strength points increased by 1
        assertEquals(fpBefore + 1, player1.strengthPoints, 
            "Harald should give 1 Strength Point");
    }

    @Test
    public void testIngaGives1SPAnd3FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Inga
        Card inga = new Card();
        inga.name = "Inga";
        inga.type = "Unit – Hero";
        inga.placement = "Settlement/city";
        inga.SP = "1";
        inga.FP = "3";
        boolean placed = inga.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Inga should be placed successfully");

        // Verify skill points increased by 1
        assertEquals(spBefore + 1, player1.skillPoints, 
            "Inga should give 1 Skill Point");

        // Verify strength points increased by 3
        assertEquals(fpBefore + 3, player1.strengthPoints, 
            "Inga should give 3 Strength Points");
    }

    @Test
    public void testOsmundGives2SPAnd2FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Osmund
        Card osmund = new Card();
        osmund.name = "Osmund";
        osmund.type = "Unit – Hero";
        osmund.placement = "Settlement/city";
        osmund.SP = "2";
        osmund.FP = "2";
        boolean placed = osmund.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Osmund should be placed successfully");

        // Verify skill points increased by 2
        assertEquals(spBefore + 2, player1.skillPoints, 
            "Osmund should give 2 Skill Points");

        // Verify strength points increased by 2
        assertEquals(fpBefore + 2, player1.strengthPoints, 
            "Osmund should give 2 Strength Points");
    }

    @Test
    public void testCandamirGives4SPAnd1FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Candamir
        Card candamir = new Card();
        candamir.name = "Candamir";
        candamir.type = "Unit – Hero";
        candamir.placement = "Settlement/city";
        candamir.SP = "4";
        candamir.FP = "1";
        boolean placed = candamir.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Candamir should be placed successfully");

        // Verify skill points increased by 4
        assertEquals(spBefore + 4, player1.skillPoints, 
            "Candamir should give 4 Skill Points");

        // Verify strength points increased by 1
        assertEquals(fpBefore + 1, player1.strengthPoints, 
            "Candamir should give 1 Strength Point");
    }

    @Test
    public void testSiglindGives2SPAnd3FP() {
        // Place settlement
        Card settlement = new Card();
        settlement.name = "Settlement";
        settlement.type = "Settlement";
        player1.placeCard(2, 2, settlement);

        int spBefore = player1.skillPoints;
        int fpBefore = player1.strengthPoints;

        // Place Siglind
        Card siglind = new Card();
        siglind.name = "Siglind";
        siglind.type = "Unit – Hero";
        siglind.placement = "Settlement/city";
        siglind.SP = "2";
        siglind.FP = "3";
        boolean placed = siglind.applyEffect(player1, player2, 1, 2);
        assertTrue(placed, "Siglind should be placed successfully");

        // Verify skill points increased by 2
        assertEquals(spBefore + 2, player1.skillPoints, 
            "Siglind should give 2 Skill Points");

        // Verify strength points increased by 3
        assertEquals(fpBefore + 3, player1.strengthPoints, 
            "Siglind should give 3 Strength Points");
    }

    @Test
    public void testHeroesProvideStrengthAdvantage() {
        // Place settlements for both players
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);

        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);

        // Player1 places Inga (1 SP, 3 FP)
        Card inga = new Card();
        inga.name = "Inga";
        inga.type = "Unit – Hero";
        inga.placement = "Settlement/city";
        inga.SP = "1";
        inga.FP = "3";
        inga.applyEffect(player1, player2, 1, 2);

        // Verify player1 has strength advantage
        assertTrue(player1.hasStrengthTokenAgainst(player2), 
            "Player1 should have strength advantage after placing hero");
        assertFalse(player2.hasStrengthTokenAgainst(player1), 
            "Player2 should not have strength advantage");
    }
}
