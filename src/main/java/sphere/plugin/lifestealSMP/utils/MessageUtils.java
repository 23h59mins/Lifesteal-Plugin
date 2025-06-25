package sphere.plugin.lifestealSMP.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.util.List;

/**
 * Utility class for sending formatted messages to command senders.
 * Supports prefixed and unprefixed messages, with optional batch support.
 */
public final class MessageUtils {

    private MessageUtils() {
        // Prevent instantiation
    }

    /**
     * Sends a single message to the sender without prefix.
     *
     * @param sender  the command sender
     * @param message the message to send (ignored if null/blank)
     */
    public static void send(CommandSender sender, String message) {
        if (isBlank(message)) return;
        sender.sendMessage(ColorUtils.color(message));
    }

    /**
     * Sends a single message to the sender with prefix from the plugin config.
     *
     * @param sender  the command sender
     * @param plugin  the plugin instance
     * @param message the message to send (ignored if null/blank)
     */
    public static void sendWithPrefix(CommandSender sender, LifestealSMP plugin, String message) {
        if (isBlank(message)) return;
        String prefix = getPrefix(plugin);
        sender.sendMessage(ColorUtils.color(prefix + message));
    }

    /**
     * Sends a list of messages to the sender without prefix.
     *
     * @param sender   the command sender
     * @param messages the list of messages (ignored if null/empty)
     */
    public static void sendList(CommandSender sender, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String line : messages) {
            if (!isBlank(line)) {
                sender.sendMessage(ColorUtils.color(line));
            }
        }
    }

    /**
     * Sends a list of messages to the sender, each with the plugin prefix.
     *
     * @param sender   the command sender
     * @param plugin   the plugin instance
     * @param messages the list of messages (ignored if null/empty)
     */
    public static void sendListWithPrefix(CommandSender sender, LifestealSMP plugin, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        String prefix = getPrefix(plugin);
        for (String line : messages) {
            if (!isBlank(line)) {
                sender.sendMessage(ColorUtils.color(prefix + line));
            }
        }
    }

    /**
     * Sends a prefixed notification to the player.
     *
     * @param sender  the command sender
     * @param plugin  the plugin instance
     * @param message the notification message
     */
    public static void notifyPlayer(CommandSender sender, LifestealSMP plugin, String message) {
        sendWithPrefix(sender, plugin, message);
    }

    /**
     * Returns true if a string is null, empty, or whitespace only.
     *
     * @param str input string
     * @return boolean indicating if string is blank
     */
    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Retrieves prefix from plugin config manager.
     *
     * @param plugin plugin instance
     * @return formatted prefix or empty string if null
     */
    private static String getPrefix(LifestealSMP plugin) {
        try {
            String prefix = plugin.getConfigManager().getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception e) {
            Bukkit.getLogger().warning("[LifestealSMP] Failed to get prefix from config: " + e.getMessage());
            return "";
        }
    }
}
