package controller;

import model.Card;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InitializationManager - focusing on initial game setup.
 */
public class InitializationManagerTest {

    private InitializationManager initManager;

    @BeforeEach
    public void setUp() throws IOException {
        // Load cards from the JSON file
        Card.loadBasicCards("cards.json");
        
        initManager = new InitializationManager();
    }

    @Test
    public void testPlayersStartWithTwoVictoryPoints() {
        // Create two players
        Player player1 = new Player();
        player1.isBot = true;
        Player player2 = new Player();
        player2.isBot = true;
        
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        // Initialize principalities
        initManager.initPrincipality(players);
        
        // Each player should start with 2 VP (one for each settlement)
        assertEquals(2, player1.victoryPoints, 
            "Player 1 should start with 2 victory points (2 settlements)");
        assertEquals(2, player2.victoryPoints, 
            "Player 2 should start with 2 victory points (2 settlements)");
    }

    @Test
    public void testPlayersStartWithTwoSettlements() {
        // Create two players
        Player player1 = new Player();
        player1.isBot = true;
        Player player2 = new Player();
        player2.isBot = true;
        
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        // Initialize principalities
        initManager.initPrincipality(players);
        
        // Verify player 1 has 2 settlements
        int settlements1 = 0;
        for (int r = 0; r < player1.getPrincipality().size(); r++) {
            for (int c = 0; c < player1.getPrincipality().get(r).size(); c++) {
                Card card = player1.getCard(r, c);
                if (card != null && "Settlement".equals(card.name)) {
                    settlements1++;
                }
            }
        }
        assertEquals(2, settlements1, "Player 1 should have 2 settlements");
        
        // Verify player 2 has 2 settlements
        int settlements2 = 0;
        for (int r = 0; r < player2.getPrincipality().size(); r++) {
            for (int c = 0; c < player2.getPrincipality().get(r).size(); c++) {
                Card card = player2.getCard(r, c);
                if (card != null && "Settlement".equals(card.name)) {
                    settlements2++;
                }
            }
        }
        assertEquals(2, settlements2, "Player 2 should have 2 settlements");
    }

    @Test
    public void testPlayersStartWithOneRoad() {
        // Create two players
        Player player1 = new Player();
        player1.isBot = true;
        Player player2 = new Player();
        player2.isBot = true;
        
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        // Initialize principalities
        initManager.initPrincipality(players);
        
        // Verify player 1 has 1 road
        int roads1 = 0;
        for (int r = 0; r < player1.getPrincipality().size(); r++) {
            for (int c = 0; c < player1.getPrincipality().get(r).size(); c++) {
                Card card = player1.getCard(r, c);
                if (card != null && "Road".equals(card.name)) {
                    roads1++;
                }
            }
        }
        assertEquals(1, roads1, "Player 1 should have 1 road");
        
        // Verify player 2 has 1 road
        int roads2 = 0;
        for (int r = 0; r < player2.getPrincipality().size(); r++) {
            for (int c = 0; c < player2.getPrincipality().get(r).size(); c++) {
                Card card = player2.getCard(r, c);
                if (card != null && "Road".equals(card.name)) {
                    roads2++;
                }
            }
        }
        assertEquals(1, roads2, "Player 2 should have 1 road");
    }

    @Test
    public void testPlayersStartWithSixRegions() {
        // Create two players
        Player player1 = new Player();
        player1.isBot = true;
        Player player2 = new Player();
        player2.isBot = true;
        
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        
        // Initialize principalities
        initManager.initPrincipality(players);
        
        // Verify player 1 has 6 regions
        int regions1 = 0;
        for (int r = 0; r < player1.getPrincipality().size(); r++) {
            for (int c = 0; c < player1.getPrincipality().get(r).size(); c++) {
                Card card = player1.getCard(r, c);
                if (card != null && "Region".equals(card.type)) {
                    regions1++;
                }
            }
        }
        assertEquals(6, regions1, "Player 1 should have 6 regions");
        
        // Verify player 2 has 6 regions
        int regions2 = 0;
        for (int r = 0; r < player2.getPrincipality().size(); r++) {
            for (int c = 0; c < player2.getPrincipality().get(r).size(); c++) {
                Card card = player2.getCard(r, c);
                if (card != null && "Region".equals(card.type)) {
                    regions2++;
                }
            }
        }
        assertEquals(6, regions2, "Player 2 should have 6 regions");
    }
}
