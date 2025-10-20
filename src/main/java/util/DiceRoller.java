package util;

import java.util.Random;

/**
 * Handles dice rolling for the game.
 */
public class DiceRoller {
    private final Random rng;

    public DiceRoller() {
        this.rng = new Random();
    }

    public DiceRoller(Random rng) {
        this.rng = rng;
    }

    /**
     * Roll a standard 6-sided die.
     * @return A value between 1 and 6 (inclusive)
     */
    public int rollDie() {
        return 1 + rng.nextInt(6);
    }
}
