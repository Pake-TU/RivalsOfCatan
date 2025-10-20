/**
 * Interface for managing player resources.
 * This abstraction allows for different resource management strategies.
 */
public interface IResourceManager {
    /**
     * Get the count of a specific resource type.
     * @param type The resource type (e.g., "Brick", "Grain", etc.)
     * @return The count of that resource
     */
    int getResourceCount(String type);

    /**
     * Gain one unit of a resource.
     * @param type The resource type to gain
     */
    void gainResource(String type);

    /**
     * Remove resources of a specific type.
     * @param type The resource type to remove
     * @param amount The amount to remove
     * @return true if successfully removed, false otherwise
     */
    boolean removeResource(String type, int amount);

    /**
     * Set the total count of a resource type.
     * @param type The resource type
     * @param count The new count
     */
    void setResourceCount(String type, int count);

    /**
     * Get the total count of all resources.
     * @return The total resource count
     */
    int totalAllResources();
}
