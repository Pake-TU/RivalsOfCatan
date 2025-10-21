package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class VerboseAdvantageTest {
    @Test
    public void testVerbose() throws IOException {
        Card.loadBasicCards("cards.json");
        
        Player player1 = new Player();
        player1.isBot = false; // NOT a bot so we can see messages
        Player player2 = new Player();
        player2.isBot = false; // NOT a bot
        
        // Setup
        Card settlement1 = new Card();
        settlement1.name = "Settlement";
        settlement1.type = "Settlement";
        player1.placeCard(2, 2, settlement1);
        
        Card settlement2 = new Card();
        settlement2.name = "Settlement";
        settlement2.type = "Settlement";
        player2.placeCard(2, 2, settlement2);
        
        player1.commercePoints = 3;
        player2.commercePoints = 2;
        
        System.out.println("BEFORE: Player1 CP=" + player1.commercePoints + ", Player2 CP=" + player2.commercePoints);
        System.out.println("BEFORE: Player1 has advantage? " + player1.hasTradeTokenAgainst(player2));
        System.out.println("BEFORE: Player2 has advantage? " + player2.hasTradeTokenAgainst(player1));
        
        // Player 2 places a card that gives +1 CP
        Card ship = new Card();
        ship.name = "Test Trade Ship";
        ship.type = "Unit – Trade Ship";
        ship.placement = "Settlement/city";
        ship.CP = "1";
        ship.cost = "";
        
        System.out.println("\n>>> Player 2 placing card with +1 CP...\n");
        ship.applyEffect(player2, player1, 1, 2);
        
        System.out.println("\nAFTER: Player1 CP=" + player1.commercePoints + ", Player2 CP=" + player2.commercePoints);
        System.out.println("AFTER: Player1 has advantage? " + player1.hasTradeTokenAgainst(player2));
        System.out.println("AFTER: Player2 has advantage? " + player2.hasTradeTokenAgainst(player1));
    }
}
