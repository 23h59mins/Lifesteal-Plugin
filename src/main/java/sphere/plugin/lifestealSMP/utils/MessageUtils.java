package sphere.plugin.lifestealSMP.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.util.List;

/**
 * Utility class for sending formatted and prefixed messages to players or console.
 */
public final class MessageUtils {

    private MessageUtils() {
        // Static utility class
    }

    /**
     * Sends a single color-formatted message to a sender (no prefix).
     *
     * @param sender  the command sender
     * @param message message to send (ignored if null or blank)
     */
    public static void send(CommandSender sender, String message) {
        if (sender == null || isBlank(message)) return;
        sender.sendMessage(ColorUtils.color(message));
    }

    /**
     * Sends a single color-formatted message with prefix.
     *
     * @param sender  the command sender
     * @param plugin  plugin instance
     * @param message message to send (ignored if null or blank)
     */
    public static void sendWithPrefix(CommandSender sender, LifestealSMP plugin, String message) {
        if (sender == null || plugin == null || isBlank(message)) return;
        sender.sendMessage(ColorUtils.color(getPrefix(plugin) + message));
    }

    /**
     * Sends multiple color-formatted messages without prefix.
     *
     * @param sender   the command sender
     * @param messages list of messages to send (skips null/blank)
     */
    public static void sendList(CommandSender sender, List<String> messages) {
        if (sender == null || messages == null || messages.isEmpty()) return;
        for (String line : messages) {
            if (!isBlank(line)) {
                sender.sendMessage(ColorUtils.color(line));
            }
        }
    }

    /**
     * Sends multiple color-formatted messages with prefix.
     *
     * @param sender   the command sender
     * @param plugin   plugin instance
     * @param messages list of messages to send (skips null/blank)
     */
    public static void sendListWithPrefix(CommandSender sender, LifestealSMP plugin, List<String> messages) {
        if (sender == null || plugin == null || messages == null || messages.isEmpty()) return;
        String prefix = getPrefix(plugin);
        for (String line : messages) {
            if (!isBlank(line)) {
                sender.sendMessage(ColorUtils.color(prefix + line));
            }
        }
    }

    /**
     * Sends a prefixed message to the player as notification.
     *
     * @param sender  command sender
     * @param plugin  plugin instance
     * @param message message to send
     */
    public static void notifyPlayer(CommandSender sender, LifestealSMP plugin, String message) {
        sendWithPrefix(sender, plugin, message);
    }

    /**
     * Checks whether a string is null or empty after trimming.
     *
     * @param str input string
     * @return true if null, empty, or whitespace
     */
    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Safely retrieves the plugin message prefix.
     *
     * @param plugin plugin instance
     * @return prefix or empty string
     */
    private static String getPrefix(LifestealSMP plugin) {
        try {
            String prefix = plugin.getConfigManager().getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception e) {
            Bukkit.getLogger().warning("[LifestealSMP] Error fetching message prefix: " + e.getMessage());
            return "";
        }
    }
}
