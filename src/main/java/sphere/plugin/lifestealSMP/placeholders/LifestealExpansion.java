package sphere.plugin.lifestealSMP.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.sql.SQLException;
import java.util.UUID;

/**
 * PlaceholderAPI expansion for LifestealSMP.
 * Supported placeholders:
 *  - %lifesteal_hearts%
 *  - %lifesteal_maxhearts%
 *  - %lifesteal_is_banned%
 *  - %lifesteal_database_type%
 *  - %lifesteal_lang%
 *  - %lifesteal_startinghearts%
 *  - %lifesteal_prefix%
 *  - %lifesteal_online%
 *  - %lifesteal_ban_reason%
 */
public class LifestealExpansion extends PlaceholderExpansion {

    private final LifestealSMP plugin;

    public LifestealExpansion(LifestealSMP plugin) {
        this.plugin = plugin != null ? plugin : throwIllegal("Plugin cannot be null");
    }

    @Override
    public String getIdentifier() {
        return "lifesteal";
    }

    @Override
    public String getAuthor() {
        return "SphereDev";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer == null || identifier == null || identifier.isBlank()) return "";

        UUID uuid = offlinePlayer.getUniqueId();
        if (uuid == null) return "";

        try {
            String id = identifier.toLowerCase();
            Player player = Bukkit.getPlayer(uuid);

            return switch (id) {
                case "hearts" -> (player != null && player.isOnline())
                        ? String.valueOf(plugin.getHeartManager().getHearts(player))
                        : "Offline";

                case "maxhearts" -> String.valueOf(plugin.getConfigManager().getMaxHearts());

                case "is_banned" -> String.valueOf(plugin.getBanManager().isPlayerBanned(uuid));

                case "playername" -> (player != null) ? player.getName() : offlinePlayer.getName();

                case "uuid" -> uuid.toString();

                case "health" -> (player != null) ? String.valueOf((int) player.getHealth()) : "0";

                case "maxhealth" -> (player != null) ? String.valueOf((int) player.getMaxHealth()) : "0";

                case "hearts_left" -> (player != null)
                        ? String.valueOf(plugin.getConfigManager().getMaxHearts() - plugin.getHeartManager().getHearts(player))
                        : "0";

                case "online_count" -> String.valueOf(Bukkit.getOnlinePlayers().size());

                case "database_connected" -> (plugin.getDatabaseManager() != null ? "true" : "false");

                case "database_type" -> safeString(plugin.getConfig().getString("database.type", "sqlite").toLowerCase());

                case "lang" -> safeString(plugin.getConfigManager().getLanguageFile());

                case "startinghearts" -> String.valueOf(plugin.getConfigManager().getStartingHearts());

                case "prefix" -> safeString(plugin.getConfig().getString("messages.prefix", "&c&lLifesteal &7»"));

                case "online" -> (player != null && player.isOnline()) ? "true" : "false";

                case "ban_reason" -> plugin.getBanManager().isPlayerBanned(uuid)
                        ? "Out of hearts"
                        : "";

                default -> "";
            };
        } catch (SQLException sql) {
            plugin.getLogger().warning("[PlaceholderAPI] SQL error on '" + identifier + "': " + sql.getMessage());
            return "ERR";
        } catch (Exception ex) {
            plugin.getLogger().warning("[PlaceholderAPI] Failed to resolve '" + identifier + "': " + ex.getMessage());
            return "ERR";
        }
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }

    private static LifestealSMP throwIllegal(String msg) {
        throw new IllegalArgumentException(msg);
    }
}