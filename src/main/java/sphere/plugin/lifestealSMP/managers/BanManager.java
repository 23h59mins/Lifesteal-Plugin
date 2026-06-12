package sphere.plugin.lifestealSMP.managers;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class BanManager {

    private final LifestealSMP plugin;
    private final HeartManager heartManager;
    private final BanList banList;

    public BanManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
        this.banList = Bukkit.getBanList(BanList.Type.NAME);
    }

    public void checkBan(Player player) throws SQLException {
        if (player == null) {
            return;
        }

        int hearts = heartManager.getHearts(player);
        if (hearts <= 0) {
            banSync(player, null);
            return;
        }

        heartManager.applyMaxHealth(player, hearts);
    }

    public void banAsync(Player player, Date until) {
        if (player == null || !player.isOnline()) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                banSync(player, until);
            }
        }.runTask(plugin);
    }

    public void unbanPlayer(UUID uuid) {
        if (uuid == null) {
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        if (name == null || !banList.isBanned(name)) {
            return;
        }

        try {
            banList.pardon(name);
            plugin.getLogger().info("[BanManager] Unbanned player " + name + ".");
        } catch (Exception e) {
            plugin.getLogger().severe("[BanManager] Failed to unban " + name + ": " + e.getMessage());
        }
    }

    public boolean isPlayerBanned(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        return name != null && banList.isBanned(name);
    }

    private void banSync(Player player, Date until) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String playerName = player.getName();
        if (playerName == null || playerName.isBlank()) {
            return;
        }

        if (banList.isBanned(playerName)) {
            return;
        }

        String banMessage = buildBanMessage();

        try {
            banList.addBan(playerName, banMessage, until, "LifestealSMP");
            player.kickPlayer(banMessage);
            plugin.getLogger().info("[BanManager] Banned player " + playerName + " (hearts=0).");
        } catch (Exception e) {
            plugin.getLogger().severe("[BanManager] Failed to ban " + playerName + ": " + e.getMessage());
        }
    }

    private String buildBanMessage() {
        return plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getBanMessage();
    }
}