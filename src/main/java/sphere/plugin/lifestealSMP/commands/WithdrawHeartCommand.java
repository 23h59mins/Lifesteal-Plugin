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
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class WithdrawHeartCommand implements CommandExecutor {

    private final LifestealSMP plugin;

    public WithdrawHeartCommand(LifestealSMP plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin instance cannot be null");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning("Withdraw command can only be executed by a player.");
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("lifesteal.withdraw")) {
            if (plugin.getConfigManager() != null) {
                MessageUtils.sendWithPrefix(player, plugin, plugin.getConfigManager().getNoPermission());
            } else {
                player.sendMessage("§cYou don't have permission.");
            }
            return true;
        }

        HeartManager heartManager = plugin.getHeartManager();
        if (heartManager == null) {
            MessageUtils.sendWithPrefix(player, plugin, "&cHeart system not available.");
            return true;
        }

        try {
            int currentHearts = heartManager.getHearts(player);
            if (currentHearts <= 1) {
                MessageUtils.sendWithPrefix(player, plugin, "&cYou cannot withdraw when you only have 1 heart left.");
                return true;
            }

            // Attempt to remove heart
            heartManager.removeHearts(player, 1);

            Material heartMaterial = plugin.getConfigManager() != null
                    ? plugin.getConfigManager().getHeartItemMaterial()
                    : Material.NETHER_STAR;

            if (heartMaterial == null || heartMaterial == Material.AIR) {
                heartMaterial = Material.NETHER_STAR;
                plugin.getLogger().warning("Invalid or missing heart item material. Defaulted to NETHER_STAR.");
                MessageUtils.sendWithPrefix(player, plugin, "&eUsing fallback material for heart item.");
            }

            ItemStack heartItem = new ItemBuilder(heartMaterial)
                    .name("&c❤ Heart")
                    .lore("&7Use this item to restore one heart.")
                    .amount(1)
                    .build();

            player.getInventory().addItem(heartItem);

            if (plugin.getConfigManager() != null) {
                MessageUtils.sendWithPrefix(player, plugin, plugin.getConfigManager().getWithdrawSuccess());
            } else {
                MessageUtils.sendWithPrefix(player, plugin, "&aHeart withdrawn successfully.");
            }

        } catch (SQLException e) {
            MessageUtils.sendWithPrefix(player, plugin, "&cAn error occurred while processing your heart withdrawal.");
            plugin.getLogger().log(Level.SEVERE, "SQL error during heart withdrawal for " + player.getName(), e);
        } catch (Exception e) {
            MessageUtils.sendWithPrefix(player, plugin, "&cUnexpected error during heart withdrawal.");
            plugin.getLogger().log(Level.SEVERE, "Unexpected error during heart withdrawal for " + player.getName(), e);
        }

        return true;
    }
}
