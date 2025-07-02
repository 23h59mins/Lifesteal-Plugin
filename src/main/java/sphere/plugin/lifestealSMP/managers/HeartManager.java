package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.database.DatabaseManager;
import sphere.plugin.lifestealSMP.models.PlayerData;

import java.sql.SQLException;


public class HeartManager {

    private final LifestealSMP plugin;
    private final DatabaseManager database;

    public HeartManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabaseManager();
    }

    public void loadPlayer(Player player) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        applyMaxHealth(player, data.getHearts());
    }

    public void setHearts(Player player, int amount) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        amount = sanitize(player, amount);
        data.setHearts(amount);
        database.savePlayer(data);
        applyMaxHealth(player, amount);
    }

    public void addHearts(Player player, int amount) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        data.addHearts(amount);
        data.setHearts(sanitize(player, data.getHearts()));
        database.savePlayer(data);
        applyMaxHealth(player, data.getHearts());
    }

    public void removeHearts(Player player, int amount) throws SQLException {
        PlayerData data = database.loadPlayer(player);
        data.removeHearts(amount);
        database.savePlayer(data);
        applyMaxHealth(player, data.getHearts());
    }

    public int getHearts(Player player) throws SQLException {
        return database.getHearts(player.getUniqueId());
    }

    private int sanitize(Player player, int amount) {
        int max = plugin.getConfigManager().getMaxHearts();
        if (amount < 0) return 0;
        if (!player.hasPermission("lifesteal.bypasslimit")) {
            return Math.min(amount, max);
        }
        return amount;
    }

    public void applyMaxHealth(Player player, int hearts) {
        double targetHealth = Math.max(2.0, hearts * 2.0);

        // Simple direct approach
        player.setMaxHealth(targetHealth);

        if (player.isOnline() && !player.isDead()) {
            double currentHealth = player.getHealth();
            if (currentHealth > targetHealth) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && !player.isDead()) {
                        player.setHealth(targetHealth);
                    }
                });
            }
        }
    }
}
