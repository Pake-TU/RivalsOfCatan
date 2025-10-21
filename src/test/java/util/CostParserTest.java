package util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CostParser utility class.
 */
public class CostParserTest {

    @Test
    public void testLetterToResource() {
        assertEquals("Brick", CostParser.letterToResource('B'));
        assertEquals("Brick", CostParser.letterToResource('b')); // Case-insensitive
        assertEquals("Grain", CostParser.letterToResource('G'));
        assertEquals("Lumber", CostParser.letterToResource('L'));
        assertEquals("Wool", CostParser.letterToResource('W'));
        assertEquals("Ore", CostParser.letterToResource('O'));
        assertEquals("Gold", CostParser.letterToResource('A'));
        assertNull(CostParser.letterToResource('X'));
        assertNull(CostParser.letterToResource('Z'));
    }

    @Test
    public void testParseCostSimple() {
        Map<String, Integer> cost = CostParser.parseCost("LW");
        assertEquals(1, cost.get("Lumber"));
        assertEquals(1, cost.get("Wool"));
        assertEquals(2, cost.size());
    }

    @Test
    public void testParseCostMultiple() {
        Map<String, Integer> cost = CostParser.parseCost("BGO");
        assertEquals(1, cost.get("Brick"));
        assertEquals(1, cost.get("Grain"));
        assertEquals(1, cost.get("Ore"));
        assertEquals(3, cost.size());
    }

    @Test
    public void testParseCostDuplicates() {
        Map<String, Integer> cost = CostParser.parseCost("AABBB");
        assertEquals(2, cost.get("Gold"));
        assertEquals(3, cost.get("Brick"));
        assertEquals(2, cost.size());
    }

    @Test
    public void testParseCostWithSpaces() {
        Map<String, Integer> cost = CostParser.parseCost("L W");
        assertEquals(1, cost.get("Lumber"));
        assertEquals(1, cost.get("Wool"));
    }

    @Test
    public void testParseCostWithSeparators() {
        Map<String, Integer> cost = CostParser.parseCost("L,W");
        assertEquals(1, cost.get("Lumber"));
        assertEquals(1, cost.get("Wool"));

        cost = CostParser.parseCost("B;G");
        assertEquals(1, cost.get("Brick"));
        assertEquals(1, cost.get("Grain"));

        cost = CostParser.parseCost("O+W");
        assertEquals(1, cost.get("Ore"));
        assertEquals(1, cost.get("Wool"));
    }

    @Test
    public void testParseCostEmpty() {
        Map<String, Integer> cost = CostParser.parseCost("");
        assertTrue(cost.isEmpty());
    }

    @Test
    public void testParseCostNull() {
        Map<String, Integer> cost = CostParser.parseCost(null);
        assertTrue(cost.isEmpty());
    }

    @Test
    public void testParseCostInvalidCharacters() {
        Map<String, Integer> cost = CostParser.parseCost("BXG");
        assertEquals(1, cost.get("Brick"));
        assertEquals(1, cost.get("Grain"));
        assertEquals(2, cost.size()); // X is ignored
    }

    @Test
    public void testParseIntValid() {
        assertEquals(5, CostParser.parseInt("5", 0));
        assertEquals(123, CostParser.parseInt("123", 0));
        assertEquals(-10, CostParser.parseInt("-10", 0));
    }

    @Test
    public void testParseIntWithWhitespace() {
        assertEquals(5, CostParser.parseInt("  5  ", 0));
        assertEquals(123, CostParser.parseInt("\t123\n", 0));
    }

    @Test
    public void testParseIntInvalid() {
        assertEquals(99, CostParser.parseInt("abc", 99));
        assertEquals(0, CostParser.parseInt("", 0));
        assertEquals(-1, CostParser.parseInt("not a number", -1));
    }

    @Test
    public void testParseIntNull() {
        assertEquals(42, CostParser.parseInt(null, 42));
    }
}
