package sphere.plugin.lifestealSMP.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.HeartManager;
import sphere.plugin.lifestealSMP.utils.ItemBuilder;

import java.sql.SQLException;

public class WithdrawHeartCommand implements CommandExecutor {

    private final LifestealSMP plugin;

    public WithdrawHeartCommand(LifestealSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.log("&cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("lifesteal.withdraw")) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getNoPermission());
            return true;
        }

        HeartManager heartManager = plugin.getHeartManager();

        try {
            if (heartManager.getHearts(player) <= 1) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "&cYou cannot withdraw when you only have 1 heart left.");
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            heartManager.removeHearts(player, 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ItemStack heartItem = new ItemBuilder(plugin.getConfigManager().getHeartItemMaterial())
                .name("&c❤ Heart")
                .lore("&7Use this item to restore one heart.")
                .build();

        player.getInventory().addItem(heartItem);
        player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getWithdrawSuccess());

        return true;
    }
}
