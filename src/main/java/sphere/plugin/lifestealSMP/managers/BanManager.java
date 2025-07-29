package sphere.plugin.lifestealSMP.managers;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Manages banning and unbanning players based on heart count.
 * Includes safety checks, main-thread enforcement, and advanced features.
 */
public class BanManager {

    private final LifestealSMP plugin;
    private final HeartManager heartManager;
    private final BanList banList;

    public BanManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
        this.banList = Bukkit.getBanList(BanList.Type.NAME);
    }

    /**
     * Checks and applies a ban if the player is out of hearts.
     * If hearts > 0, ensures correct max health.
     *
     * @param player Player to check.
     * @throws SQLException If database interaction fails.
     */
    public void checkBan(Player player) throws SQLException {
        if (player == null) return;

        int hearts = heartManager.getHearts(player);
        if (hearts <= 0) {
            banAsync(player, null);
        } else {
            heartManager.applyMaxHealth(player, hearts);
        }
    }

    /**
     * Asynchronously bans the player, kicking them from the server.
     *
     * @param player Player to ban.
     * @param until  Optional ban expiration (null = permanent).
     */
    public void banAsync(Player player, Date until) {
        if (player == null || !player.isOnline()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                ban(player, until);
            }
        }.runTask(plugin);
    }

    /**
     * Bans the player with a custom message and duration.
     *
     * @param player Player to ban.
     * @param until  Expiration date or null for permanent.
     */
    private void ban(Player player, Date until) {
        if (isPlayerBanned(player.getUniqueId())) return; // Avoid double bans

        String banMessage = buildBanMessage();

        try {
            banList.addBan(player.getName(), banMessage, until, "LifestealSMP");
            player.kickPlayer(banMessage);
            plugin.getLogger().info("[BanManager] Banned player " + player.getName() + " (hearts=0).");
        } catch (Exception e) {
            plugin.getLogger().severe("[BanManager] Failed to ban " + player.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Checks if the player with the given UUID is currently banned.
     *
     * @param uuid Player UUID.
     * @return true if banned, false otherwise.
     */
    public boolean isPlayerBanned(UUID uuid) {
        if (uuid == null) return false;

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        if (name == null) return false;

        return banList.isBanned(name);
    }

    /**
     * Unbans a player by UUID if they are currently banned.
     *
     * @param uuid Player UUID.
     */
    public void unbanPlayer(UUID uuid) {
        if (uuid == null) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        if (name == null || !banList.isBanned(name)) return;

        try {
            banList.pardon(name);
            plugin.getLogger().info("[BanManager] Unbanned player " + name + ".");
        } catch (Exception e) {
            plugin.getLogger().severe("[BanManager] Failed to unban " + name + ": " + e.getMessage());
        }
    }

    /**
     * Builds the ban message from config with prefix.
     *
     * @return The ban message.
     */
    private String buildBanMessage() {
        return plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getBanMessage();
    }
}
