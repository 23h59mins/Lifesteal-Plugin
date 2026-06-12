package sphere.plugin.lifestealSMP.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.Objects;
import java.util.logging.Level;

public class ReloadCommand implements CommandExecutor {

    private final LifestealSMP plugin;

    public ReloadCommand(LifestealSMP plugin) {
        this.plugin = Objects.requireNonNull(plugin, "LifestealSMP plugin instance cannot be null");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender == null) return false;

        if (!sender.hasPermission("lifesteal.admin")) {
            if (plugin.getConfigManager() != null) {
                MessageUtils.sendWithPrefix(sender, plugin, plugin.getConfigManager().getNoPermission());
            } else {
                sender.sendMessage("§cMissing config manager or no permission.");
            }
            return true;
        }

        try {
            if (plugin.getConfigManager() != null) {
                plugin.getConfigManager().reload();
            }

            if (plugin.getLangManager() != null) {
                plugin.getLangManager().reload();
            }

            if (plugin.getConfigManager() != null) {
                MessageUtils.sendWithPrefix(sender, plugin, plugin.getConfigManager().getReloadSuccess());
            } else {
                sender.sendMessage("§aReload completed, but no success message was configured.");
            }

        } catch (Exception e) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cAn error occurred while reloading configs.");
            plugin.getLogger().log(Level.SEVERE, "ReloadCommand failed", e);
        }

        return true;
    }
}
