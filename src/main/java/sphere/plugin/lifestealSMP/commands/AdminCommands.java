package sphere.plugin.lifestealSMP.commands;

import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.ItemBuilder;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class AdminCommands implements CommandExecutor {

    private final LifestealSMP plugin;

    public AdminCommands(LifestealSMP plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin instance cannot be null");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("lifesteal.admin")) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getConfigManager().getNoPermission());
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        try {
            switch (args[0].toLowerCase()) {
                case "set" -> handleSet(sender, args);
                case "add" -> handleAdd(sender, args);
                case "remove" -> handleRemove(sender, args);
                case "unban" -> handleUnban(sender, args);
                case "purgebans" -> handlePurgeBans(sender);
                case "setmax" -> handleSetMax(sender, args);
                case "giveheart" -> handleGiveHeart(sender, args);
                case "revivebeacon" -> handleReviveBeacon(sender, args);
                case "banlist" -> handleBanList(sender);
                case "resetall" -> handleResetAll(sender);
                case "reload" -> handleReload(sender);
                default -> sendHelp(sender);
            }
        } catch (Exception e) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cAn unexpected error occurred: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "AdminCommands error", e);
        }
        return true;
    }

    private void handleGiveHeart(CommandSender sender, String[] args) {
        Player target = parseTarget(sender, args, 1);
        if (target == null) return;

        int tier = parseTier(sender, args, 2);
        int amount = parseAmount(sender, args, 3, 1);

        Material material = plugin.getConfigManager().getHeartItemMaterial();
        if (material == null || material == Material.AIR) {
            material = Material.NETHER_STAR; // fallback
            MessageUtils.sendWithPrefix(sender, plugin, "&eDefault material used for heart item.");
        }

        ItemStack heartItem = ItemBuilder.buildHeart(material, tier);
        if (heartItem == null) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cFailed to create heart item.");
            return;
        }

        heartItem.setAmount(amount);
        target.getInventory().addItem(heartItem);

        MessageUtils.sendWithPrefix(sender, plugin,
                plugin.getLangManager().get("admin.giveheart-success")
                        .replace("{player}", target.getName())
                        .replace("{tier}", String.valueOf(tier))
                        .replace("{amount}", String.valueOf(amount)));
    }

    private void handleReviveBeacon(CommandSender sender, String[] args) {
        Player target = parseTarget(sender, args, 1);
        if (target == null) return;

        ItemStack reviveBeacon = new ItemBuilder(Material.BEACON)
                .name("&b&lRevive Beacon")
                .lore("&7Use this item to revive a banned player.")
                .amount(1)
                .build();

        target.getInventory().addItem(reviveBeacon);

        MessageUtils.sendWithPrefix(sender, plugin,
                plugin.getLangManager().get("admin.revivebeacon-given")
                        .replace("{player}", target.getName()));
    }

    private void handleSet(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-set")) return;
        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) return;

        plugin.getHeartManager().setHearts(target, amount);
        sendSuccess(sender, "admin.set-success", target.getName(), amount);
    }

    private void handleAdd(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-add")) return;
        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) return;

        plugin.getHeartManager().addHearts(target, amount);
        sendSuccess(sender, "admin.add-success", target.getName(), amount);
    }

    private void handleRemove(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-remove")) return;
        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) return;

        plugin.getHeartManager().removeHearts(target, amount);
        sendSuccess(sender, "admin.remove-success", target.getName(), amount);
    }

    private void handleUnban(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cUsage: /lifestealadmin unban <player>");
            return;
        }

        String targetName = args[1];
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        if (banList.isBanned(targetName)) {
            banList.pardon(targetName);
            MessageUtils.sendWithPrefix(sender, plugin, "&aUnbanned " + targetName + ".");
        } else {
            MessageUtils.sendWithPrefix(sender, plugin, "&ePlayer " + targetName + " is not banned.");
        }
    }

    private void handleReload(CommandSender sender) {
        plugin.getConfigManager().reload();
        plugin.getLangManager().reload();
        MessageUtils.sendWithPrefix(sender, plugin, plugin.getConfigManager().getReloadSuccess());
    }

    private void handlePurgeBans(CommandSender sender) {
        Bukkit.getBanList(BanList.Type.NAME).getBanEntries().forEach(entry ->
                Bukkit.getBanList(BanList.Type.NAME).pardon(entry.getTarget()));
        MessageUtils.sendWithPrefix(sender, plugin, "&aAll bans have been purged.");
    }

    private void handleSetMax(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.send(sender, "&cUsage: /lifestealadmin setmax <amount>");
            return;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            plugin.getConfig().set("max-hearts", amount);
            plugin.saveConfig();
            plugin.getConfigManager().reload();
            MessageUtils.sendWithPrefix(sender, plugin, "&aMax hearts updated to " + amount + ".");
        } catch (NumberFormatException e) {
            MessageUtils.send(sender, "&cInvalid number.");
        }
    }

    private void handleBanList(CommandSender sender) {
        var bans = Bukkit.getBanList(BanList.Type.NAME).getBanEntries();
        if (bans.isEmpty()) {
            MessageUtils.sendWithPrefix(sender, plugin, "&aThere are no currently banned players.");
            return;
        }
        MessageUtils.sendWithPrefix(sender, plugin, "&cBanned Players:");
        bans.forEach(entry -> MessageUtils.send(sender, "&7- &c" + entry.getTarget()));
    }

    private void handleResetAll(CommandSender sender) {
        int startingHearts = plugin.getConfigManager().getStartingHearts();
        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                plugin.getHeartManager().setHearts(p, startingHearts);
            } catch (SQLException e) {
                MessageUtils.send(sender, "&cFailed to reset hearts for " + p.getName() + ".");
            }
        });
        MessageUtils.sendWithPrefix(sender, plugin, "&aAll online players have been reset to " + startingHearts + " hearts.");
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("lifesteal.admin")) {
            MessageUtils.send(sender, "&cYou do not have permission to view admin help.");
            return;
        }
        List<String> help = plugin.getLangManager().getLang().getStringList("admin.help");
        if (help == null || help.isEmpty()) {
            MessageUtils.send(sender, "&cHelp content is not available.");
            return;
        }
        MessageUtils.sendList(sender, help);
    }

    // ================= UTILITY =================

    private Player parseTarget(CommandSender sender, String[] args, int argIndex) {
        if (args.length > argIndex) {
            Player target = Bukkit.getPlayer(args[argIndex]);
            if (target == null) {
                MessageUtils.send(sender, plugin.getLangManager().get("admin.player-not-found"));
            }
            return target;
        } else if (sender instanceof Player p) {
            return p;
        } else {
            MessageUtils.send(sender, plugin.getLangManager().get("admin.revivebeacon-console-require-player"));
            return null;
        }
    }

    private int parseTier(CommandSender sender, String[] args, int argIndex) {
        try {
            if (args.length > argIndex) {
                int tier = Integer.parseInt(args[argIndex]);
                return Math.max(1, Math.min(tier, 5));
            }
        } catch (NumberFormatException ignored) {
        }
        MessageUtils.send(sender, plugin.getLangManager().get("admin.invalid-tier"));
        return 1;
    }

    private int parseAmount(CommandSender sender, String[] args, int argIndex, int defaultValue) {
        try {
            if (args.length > argIndex) {
                return Math.max(1, Integer.parseInt(args[argIndex]));
            }
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    private Integer parseAmountOrNull(CommandSender sender, String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            MessageUtils.send(sender, plugin.getLangManager().get("admin.invalid-amount"));
            return null;
        }
    }

    private boolean validateArgs(CommandSender sender, String[] args, String usageKey) {
        if (args.length < 3) {
            MessageUtils.send(sender, plugin.getLangManager().get(usageKey));
            return false;
        }
        return true;
    }

    private void sendSuccess(CommandSender sender, String key, String player, int amount) {
        MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get(key)
                .replace("{player}", player)
                .replace("{amount}", String.valueOf(amount)));
    }
}
