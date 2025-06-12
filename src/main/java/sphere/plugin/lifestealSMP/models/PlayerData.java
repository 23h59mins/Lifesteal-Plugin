package sphere.plugin.lifestealSMP.models;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private String name;
    private int hearts;

    public PlayerData(UUID uuid, String name, int hearts) {
        this.uuid = uuid;
        this.name = name;
        this.hearts = hearts;
    }

    public PlayerData(Player player, int hearts) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.hearts = hearts;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public void addHearts(int amount) {
        this.hearts += amount;
    }

    public void removeHearts(int amount) {
        this.hearts = Math.max(0, this.hearts - amount);
    }
}
