package sphere.plugin.lifestealSMP.models;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's heart-related data for the LifestealSMP plugin.
 */
public class PlayerData {

    private final UUID uuid;
    private String name;
    private int hearts;

    /**
     * Constructs PlayerData with explicit UUID, name, and heart count.
     *
     * @param uuid   Unique player identifier.
     * @param name   Player's name.
     * @param hearts Number of hearts the player has.
     */
    public PlayerData(UUID uuid, String name, int hearts) {
        this.uuid = Objects.requireNonNull(uuid, "UUID cannot be null.");
        this.name = name != null ? name : "Unknown";
        this.hearts = Math.max(0, hearts);
    }

    /**
     * Constructs PlayerData from a Bukkit Player object.
     *
     * @param player Bukkit Player.
     * @param hearts Initial number of hearts.
     */
    public PlayerData(Player player, int hearts) {
        this(
                Objects.requireNonNull(player, "Player cannot be null.").getUniqueId(),
                player.getName(),
                hearts
        );
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name : this.name;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = Math.max(0, hearts);
    }

    public void addHearts(int amount) {
        this.hearts = Math.max(0, this.hearts + amount);
    }

    public void removeHearts(int amount) {
        this.hearts = Math.max(0, this.hearts - amount);
    }

    @Override
    public String toString() {
        return "PlayerData{uuid=" + uuid + ", name='" + name + "', hearts=" + hearts + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerData other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
