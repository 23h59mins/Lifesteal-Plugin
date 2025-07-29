package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.database.DatabaseManager;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Manages player hearts and synchronizes with the database.
 */
public class HeartManager {

    private final LifestealSMP plugin;
    private final DatabaseManager database;

    public HeartManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabaseManager();
    }

    /**
     * Loads and applies a player's heart data from the database.
     */
    public void loadPlayer(Player player) throws SQLException {
        if (player == null) return;
        PlayerData data = database.loadPlayer(player);
        if (data != null) applyMaxHealth(player, data.getHearts());
    }

    /**
     * Sets a player's hearts to a specific amount.
     */
    public void setHearts(Player player, int amount) throws SQLException {
        if (player == null) return;
        PlayerData data = database.loadPlayer(player);
        if (data == null) return;

        int sanitized = sanitize(player, amount);
        if (data.getHearts() == sanitized) return; // no update needed

        data.setHearts(sanitized);
        savePlayerDataAsync(data);
        applyMaxHealth(player, sanitized);
    }

    /**
     * Adds hearts to the player's current heart count.
     */
    public void addHearts(Player player, int amount) throws SQLException {
        if (player == null || amount <= 0) return;
        adjustHearts(player, amount);
    }

    /**
     * Removes hearts from the player's current heart count.
     */
    public void removeHearts(Player player, int amount) throws SQLException {
        if (player == null || amount <= 0) return;
        adjustHearts(player, -amount);
    }

    /**
     * Retrieves the player's heart count from the database.
     */
    public int getHearts(Player player) throws SQLException {
        if (player == null) return 0;
        UUID uuid = player.getUniqueId();
        return (uuid != null) ? database.getHearts(uuid) : 0;
    }

    /**
     * Applies max health scaling based on the heart count.
     */
    public void applyMaxHealth(Player player, int hearts) {
        if (player == null) return;

        double targetHealth = heartsToHealth(hearts);
        player.setMaxHealth(targetHealth);

        // Ensure current health does not exceed max health
        if (!player.isDead() && player.isOnline()) {
            if (player.getHealth() > targetHealth) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && !player.isDead()) {
                        player.setHealth(targetHealth);
                    }
                });
            }
        }
    }

    /**
     * Adjusts player's hearts by a delta (can be positive or negative).
     */
    private void adjustHearts(Player player, int delta) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        if (data == null) return;

        int updated = sanitize(player, data.getHearts() + delta);
        if (updated != data.getHearts()) {
            data.setHearts(updated);
            savePlayerDataAsync(data);
            applyMaxHealth(player, updated);
        }
    }

    /**
     * Converts hearts to in-game health (2 HP per heart).
     */
    private double heartsToHealth(int hearts) {
        return Math.max(2.0, hearts * 2.0);
    }

    /**
     * Sanitizes a heart amount based on max limits.
     */
    private int sanitize(Player player, int amount) {
        int max = plugin.getConfigManager().getMaxHearts();
        if (amount < 0) return 0;
        return player.hasPermission("lifesteal.bypasslimit") ? amount : Math.min(amount, max);
    }

    /**
     * Saves player data asynchronously to avoid lag spikes.
     */
    private void savePlayerDataAsync(PlayerData data) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    database.savePlayer(data);
                } catch (SQLException e) {
                    plugin.getLogger().severe("[HeartManager] Failed to save hearts for " +
                            data.getName() + ": " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
