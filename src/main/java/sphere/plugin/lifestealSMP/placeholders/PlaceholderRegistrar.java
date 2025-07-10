package sphere.plugin.lifestealSMP.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public final class PlaceholderRegistrar {

    private final LifestealSMP plugin;
    private final Logger logger;

    public PlaceholderRegistrar(LifestealSMP plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void register() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.warning("PlaceholderAPI is not enabled. Placeholders will not function.");
            return;
        }

        try {
            Class<?> expansionClass = Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");

            Object expansion = Proxy.newProxyInstance(
                    PlaceholderRegistrar.class.getClassLoader(),
                    new Class[]{expansionClass},
                    new ExpansionHandler()
            );

            Method registerMethod = expansionClass.getMethod("register");
            registerMethod.invoke(expansion);

            logger.info("Lifesteal placeholders registered successfully.");
        } catch (Exception e) {
            logger.severe("Failed to register Lifesteal placeholders: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class ExpansionHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "getIdentifier": return "lifesteal";
                case "getAuthor": return "SphereDev";
                case "getVersion": return plugin.getDescription().getVersion();
                case "persist":
                case "canRegister": return true;
                case "onRequest":
                    if (args != null && args.length == 2) {
                        return handlePlaceholder(args[0], (String) args[1]);
                    }
                    return "";
                default:
                    return null;
            }
        }
    }

    private String handlePlaceholder(Object playerObj, String id) {
        if (playerObj == null || id == null || id.isBlank()) return "";

        try {
            UUID uuid = (UUID) playerObj.getClass().getMethod("getUniqueId").invoke(playerObj);
            if (uuid == null) return "";

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            Player player = offlinePlayer.getPlayer();
            String key = id.toLowerCase();

            return switch (key) {
                case "hearts" -> (player != null && player.isOnline())
                        ? String.valueOf(plugin.getHeartManager().getHearts(player))
                        : "Offline";

                case "maxhearts" -> String.valueOf(plugin.getConfigManager().getMaxHearts());

                case "is_banned" -> String.valueOf(plugin.getBanManager().isPlayerBanned(uuid));

                case "database_type" -> plugin.getConfig().getString("database.type", "sqlite").toLowerCase();

                case "lang" -> plugin.getConfigManager().getLanguageFile();

                case "startinghearts" -> String.valueOf(plugin.getConfigManager().getStartingHearts());

                case "prefix" -> plugin.getConfig().getString("messages.prefix", "&c&lLifesteal &7»");

                case "online" -> (player != null && player.isOnline()) ? "true" : "false";

                case "ban_reason" -> plugin.getBanManager().isPlayerBanned(uuid)
                        ? "Out of hearts" // replace with actual reason if available
                        : "";

                default -> "";
            };
        } catch (SQLException e) {
            logger.warning("SQL error [" + id + "]: " + e.getMessage());
            return "ERR";
        } catch (Exception e) {
            logger.warning("Placeholder error [" + id + "]: " + e.getMessage());
            return "ERR";
        }
    }
}
