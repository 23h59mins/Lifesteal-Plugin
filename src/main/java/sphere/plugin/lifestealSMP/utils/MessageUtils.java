package sphere.plugin.lifestealSMP.utils;

import org.bukkit.command.CommandSender;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.util.List;

public class MessageUtils {

    /**
     * Send message without prefix
     */
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(ColorUtils.color(message));
    }

    /**
     * Send message with plugin prefix
     */
    public static void sendWithPrefix(CommandSender sender, LifestealSMP plugin, String message) {
        if (message == null || message.isEmpty()) return;
        String fullMessage = plugin.getConfigManager().getPrefix() + message;
        sender.sendMessage(ColorUtils.color(fullMessage));
    }

    /**
     * Send multiple messages (no prefix)
     */
    public static void sendList(CommandSender sender, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String line : messages) {
            sender.sendMessage(ColorUtils.color(line));
        }
    }

    /**
     * Send multiple messages with prefix applied to each line
     */
    public static void sendListWithPrefix(CommandSender sender, LifestealSMP plugin, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        String prefix = plugin.getConfigManager().getPrefix();
        for (String line : messages) {
            sender.sendMessage(ColorUtils.color(prefix + line));
        }
    }

    /**
     * Utility shortcut: send player notification (quick clean)
     */
    public static void notifyPlayer(CommandSender sender, LifestealSMP plugin, String message) {
        sendWithPrefix(sender, plugin, message);
    }
}
