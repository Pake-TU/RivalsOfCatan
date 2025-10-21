package util;

import model.ResourceType;
import model.interfaces.IPlayer;

/**
 * Utility class for handling player input and prompts.
 * Separates I/O logic from the Player model following the Single Responsibility Principle.
 */
public class PlayerInputHelper {

    /**
     * Prompts the player for a valid resource until they provide a correct one.
     * 
     * @param player The player to prompt
     * @param promptMessage The message to display when asking for input
     * @return A validated resource type name (Brick, Grain, Lumber, Wool, Ore, or Gold)
     */
    public static String validateAndPromptResource(IPlayer player, String promptMessage) {
        while (true) {
            player.sendMessage("PROMPT: " + promptMessage + " [Brick|Grain|Lumber|Wool|Ore|Gold]:");
            String input = player.receiveMessage();
            if (input == null) {
                input = "";
            }
            String regionName = ResourceType.resourceToRegion(input.trim());
            if (regionName != null && !"Any".equals(regionName)) {
                // Valid resource type
                return input.trim();
            }
            player.sendMessage(
                    "Invalid resource '" + input + "'. Please enter one of: Brick, Grain, Lumber, Wool, Ore, or Gold.");
        }
    }

    /**
     * Prompts the player to discard a resource with validation and retry on invalid input.
     * 
     * @param player The player to prompt
     * @param promptMessage The message to display when asking for input
     * @return The validated resource type that was discarded
     */
    public static String promptAndRemoveResource(IPlayer player, String promptMessage) {
        while (true) {
            player.sendMessage("PROMPT: " + promptMessage + " [Brick|Grain|Lumber|Wool|Ore|Gold]:");
            String input = player.receiveMessage();
            if (input == null) {
                input = "";
            }
            String trimmedInput = input.trim();
            String regionName = ResourceType.resourceToRegion(trimmedInput);

            if (regionName == null || "Any".equals(regionName)) {
                player.sendMessage("Invalid resource '" + input
                        + "'. Please enter one of: Brick, Grain, Lumber, Wool, Ore, or Gold.");
                continue;
            }

            // Try to remove the resource
            if (player.removeResource(trimmedInput, 1)) {
                return trimmedInput;
            } else {
                player.sendMessage("You don't have any " + trimmedInput + " to discard. Please choose another resource.");
            }
        }
    }

    /**
     * Simple prompt for choosing a resource.
     * @param player The player to prompt
     * @return The chosen resource
     */
    public static String chooseResource(IPlayer player) {
        player.sendMessage("PROMPT: Choose resource:");
        return player.receiveMessage();
    }
}
