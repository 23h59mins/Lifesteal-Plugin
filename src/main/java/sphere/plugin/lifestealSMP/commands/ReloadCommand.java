package sphere.plugin.lifestealSMP.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sphere.plugin.lifestealSMP.LifestealSMP;

public class ReloadCommand implements CommandExecutor {

    private final LifestealSMP plugin;

    public ReloadCommand(LifestealSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getNoPermission());
            return true;
        }

        plugin.getConfigManager().reload();
        plugin.getLangManager().reload();
        sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getReloadSuccess());
        return true;
    }
}
