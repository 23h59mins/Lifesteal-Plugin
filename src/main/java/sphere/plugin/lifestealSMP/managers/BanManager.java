package sphere.plugin.lifestealSMP.managers;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;
import java.util.Date;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Manages banning and unbanning players based on heart count.
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
     * Checks if a player should be banned based on heart count.
     * Applies ban if hearts <= 0, else reapplies max health.
     *
     * @param player The player to check.
     * @throws SQLException if heartManager fails.
     */
    public void checkBan(Player player) throws SQLException {
        int hearts = heartManager.getHearts(player);
        if (hearts <= 0) {
            ban(player);
        } else {
            heartManager.applyMaxHealth(player, hearts);
        }
    }

    /**
     * Bans a player and kicks them with a custom message.
     *
     * @param player The player to ban.
     */
    private void ban(Player player) {
        String message = plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getBanMessage();

        try {
            banList.addBan(player.getName(), message, (Date) null, "LifestealSMP"); // resolved ambiguity
            player.kickPlayer(message);
            plugin.getLogger().info("Banned player " + player.getName() + " due to 0 hearts.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to ban player " + player.getName() + ": " + e.getMessage());
        }
    }


    /**
     * Checks if the player with the given UUID is banned.
     *
     * @param uuid The UUID of the player.
     * @return True if banned, false otherwise.
     */
    public boolean isPlayerBanned(UUID uuid) {
        if (uuid == null) return false;

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        if (name == null) return false;

        BanEntry entry = banList.getBanEntry(name);
        return entry != null;
    }

    /**
     * Unbans a player by UUID.
     *
     * @param uuid UUID of the player to unban.
     */
    public void unbanPlayer(UUID uuid) {
        if (uuid == null) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName();
        if (name == null) return;

        banList.pardon(name);
        plugin.getLogger().info("Unbanned player " + name + ".");
    }
}
