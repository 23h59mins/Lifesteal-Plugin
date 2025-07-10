package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
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
        if (player == null) return;
        PlayerData data = database.loadPlayer(player);
        if (data != null) applyMaxHealth(player, data.getHearts());
    }

    public void setHearts(Player player, int amount) throws SQLException {
        if (player == null) return;
        PlayerData data = database.loadPlayer(player);
        if (data == null) return;

        int sanitized = sanitize(player, amount);
        data.setHearts(sanitized);
        database.savePlayer(data);
        applyMaxHealth(player, sanitized);
    }

    public void addHearts(Player player, int amount) throws SQLException {
        if (player == null || amount == 0) return;
        PlayerData data = database.loadPlayer(player);
        if (data == null) return;

        data.addHearts(amount);
        data.setHearts(sanitize(player, data.getHearts()));
        database.savePlayer(data);
        applyMaxHealth(player, data.getHearts());
    }

    public void removeHearts(Player player, int amount) throws SQLException {
        if (player == null || amount <= 0) return;
        PlayerData data = database.loadPlayer(player);
        if (data == null) return;

        data.removeHearts(amount);
        data.setHearts(Math.max(0, data.getHearts())); // prevent negatives
        database.savePlayer(data);
        applyMaxHealth(player, data.getHearts());
    }

    public int getHearts(Player player) throws SQLException {
        if (player == null) return 0;
        UUID uuid = player.getUniqueId();
        return (uuid != null) ? database.getHearts(uuid) : 0;
    }

    public void applyMaxHealth(Player player, int hearts) {
        if (player == null) return;

        double target = Math.max(2.0, hearts * 2.0);
        player.setMaxHealth(target);

        if (!player.isDead() && player.isOnline()) {
            double current = player.getHealth();
            if (current > target) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && !player.isDead()) {
                        player.setHealth(target);
                    }
                });
            }
        }
    }

    private int sanitize(Player player, int amount) {
        int max = plugin.getConfigManager().getMaxHearts();
        if (amount < 0) return 0;
        return player.hasPermission("lifesteal.bypasslimit") ? amount : Math.min(amount, max);
    }
}
