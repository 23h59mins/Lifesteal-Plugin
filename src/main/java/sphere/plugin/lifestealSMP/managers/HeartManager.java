package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.database.DatabaseManager;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.SQLException;
import java.util.UUID;

public class HeartManager {

    private final LifestealSMP plugin;
    private final DatabaseManager database;

    public HeartManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabaseManager();
    }

    public void loadPlayer(Player player) throws SQLException {
        if (player == null) {
            return;
        }

        PlayerData data = database.loadPlayer(player);
        if (data != null) {
            applyMaxHealth(player, data.getHearts());
        }
    }

    public void setHearts(Player player, int amount) throws SQLException {
        if (player == null) {
            return;
        }

        PlayerData data = database.loadPlayer(player);
        if (data == null) {
            return;
        }

        int sanitized = sanitize(player, amount);
        if (data.getHearts() == sanitized) {
            return;
        }

        data.setHearts(sanitized);
        database.savePlayer(data);
        applyMaxHealth(player, sanitized);
    }

    public void addHearts(Player player, int amount) throws SQLException {
        if (player == null || amount <= 0) {
            return;
        }
        adjustHearts(player, amount);
    }

    public void removeHearts(Player player, int amount) throws SQLException {
        if (player == null || amount <= 0) {
            return;
        }
        adjustHearts(player, -amount);
    }

    public int getHearts(Player player) throws SQLException {
        if (player == null) {
            return 0;
        }

        UUID uuid = player.getUniqueId();
        return database.getHearts(uuid);
    }

    public void applyMaxHealth(Player player, int hearts) {
        if (player == null) {
            return;
        }

        double targetHealth = heartsToHealth(hearts);
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) {
            plugin.getLogger().warning("[HeartManager] MAX_HEALTH attribute missing for " + player.getName());
            return;
        }

        attribute.setBaseValue(targetHealth);

        if (player.isOnline() && !player.isDead() && player.getHealth() > targetHealth) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && !player.isDead()) {
                    player.setHealth(targetHealth);
                }
            });
        }
    }

    private void adjustHearts(Player player, int delta) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        if (data == null) {
            return;
        }

        int updated = sanitize(player, data.getHearts() + delta);
        if (updated == data.getHearts()) {
            return;
        }

        data.setHearts(updated);
        database.savePlayer(data);
        applyMaxHealth(player, updated);
    }

    private double heartsToHealth(int hearts) {
        return Math.max(2.0, hearts * 2.0);
    }

    private int sanitize(Player player, int amount) {
        int max = plugin.getConfigManager().getMaxHearts();
        if (amount < 0) {
            return 0;
        }
        return player.hasPermission("lifesteal.bypasslimit") ? amount : Math.min(amount, max);
    }
}