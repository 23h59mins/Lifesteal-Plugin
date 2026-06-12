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
import java.util.Map;
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
        if (target == null) {
            return;
        }

        int tier = parseTier(sender, args, 2);
        int amount = parseAmount(sender, args, 3, 1);

        Material material = plugin.getConfigManager().getHeartItemMaterial();
        if (material == null || material == Material.AIR) {
            material = Material.NETHER_STAR;
            MessageUtils.sendWithPrefix(sender, plugin, "&eDefault material used for heart item.");
        }

        ItemStack heartItem = ItemBuilder.buildHeart(material, tier);
        heartItem.setAmount(amount);

        Map<Integer, ItemStack> leftovers = target.getInventory().addItem(heartItem);
        if (!leftovers.isEmpty()) {
            MessageUtils.sendWithPrefix(sender, plugin, "&c" + target.getName() + "'s inventory is full.");
            return;
        }

        MessageUtils.sendWithPrefix(
                sender,
                plugin,
                plugin.getLangManager().get("admin.giveheart-success")
                        .replace("{player}", target.getName())
                        .replace("{tier}", String.valueOf(tier))
                        .replace("{amount}", String.valueOf(amount))
        );
    }

    private void handleReviveBeacon(CommandSender sender, String[] args) {
        Player target = parseTarget(sender, args, 1);
        if (target == null) {
            return;
        }

        ItemStack reviveBeacon = buildReviveBeacon();
        Map<Integer, ItemStack> leftovers = target.getInventory().addItem(reviveBeacon);

        if (!leftovers.isEmpty()) {
            MessageUtils.sendWithPrefix(sender, plugin, "&c" + target.getName() + "'s inventory is full.");
            return;
        }

        MessageUtils.sendWithPrefix(
                sender,
                plugin,
                plugin.getLangManager().get("admin.revivebeacon-given")
                        .replace("{player}", target.getName())
        );
    }

    private ItemStack buildReviveBeacon() {
        String displayName = plugin.getConfig().getString(
                "revive-beacon-crafting.display-name",
                "&b&lRevive Beacon"
        );
        List<String> lore = plugin.getConfig().getStringList("revive-beacon-crafting.lore");

        ItemBuilder builder = new ItemBuilder(Material.BEACON).name(displayName).amount(1);
        if (lore != null && !lore.isEmpty()) {
            builder.lore(lore);
        }

        return builder.build();
    }

    private void handleSet(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-set")) {
            return;
        }

        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) {
            return;
        }

        plugin.getHeartManager().setHearts(target, amount);
        sendSuccess(sender, "admin.set-success", target.getName(), amount);
    }

    private void handleAdd(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-add")) {
            return;
        }

        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) {
            return;
        }

        plugin.getHeartManager().addHearts(target, amount);
        sendSuccess(sender, "admin.add-success", target.getName(), amount);
    }

    private void handleRemove(CommandSender sender, String[] args) throws SQLException {
        if (!validateArgs(sender, args, "admin.usage-remove")) {
            return;
        }

        Player target = parseTarget(sender, args, 1);
        Integer amount = parseAmountOrNull(sender, args[2]);
        if (target == null || amount == null) {
            return;
        }

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
            return;
        }

        MessageUtils.sendWithPrefix(sender, plugin, "&ePlayer " + targetName + " is not banned.");
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
            MessageUtils.sendWithPrefix(sender, plugin, "&cUsage: /lifestealadmin setmax <amount>");
            return;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            plugin.getConfig().set("max-hearts", amount);
            plugin.saveConfig();
            plugin.getConfigManager().reload();
            MessageUtils.sendWithPrefix(sender, plugin, "&aMax hearts updated to " + amount + ".");
        } catch (NumberFormatException e) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cInvalid number.");
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

        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                plugin.getHeartManager().setHearts(player, startingHearts);
            } catch (SQLException e) {
                MessageUtils.sendWithPrefix(sender, plugin, "&cFailed to reset hearts for " + player.getName() + ".");
            }
        });

        MessageUtils.sendWithPrefix(
                sender,
                plugin,
                "&aAll online players have been reset to " + startingHearts + " hearts."
        );
    }

    private void sendHelp(CommandSender sender) {
        List<String> help = plugin.getLangManager().getLang().getStringList("admin.help");
        if (help == null || help.isEmpty()) {
            MessageUtils.sendWithPrefix(sender, plugin, "&cHelp content is not available.");
            return;
        }
        MessageUtils.sendList(sender, help);
    }

    private Player parseTarget(CommandSender sender, String[] args, int argIndex) {
        if (args.length > argIndex) {
            Player target = Bukkit.getPlayerExact(args[argIndex]);
            if (target == null) {
                MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.player-not-found"));
            }
            return target;
        }

        if (sender instanceof Player player) {
            return player;
        }

        MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.revivebeacon-console-require-player"));
        return null;
    }

    private int parseTier(CommandSender sender, String[] args, int argIndex) {
        if (args.length <= argIndex) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.invalid-tier"));
            return 1;
        }

        try {
            int tier = Integer.parseInt(args[argIndex]);
            return Math.max(1, Math.min(tier, 5));
        } catch (NumberFormatException e) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.invalid-tier"));
            return 1;
        }
    }

    private int parseAmount(CommandSender sender, String[] args, int argIndex, int defaultValue) {
        if (args.length <= argIndex) {
            return defaultValue;
        }

        try {
            return Math.max(1, Integer.parseInt(args[argIndex]));
        } catch (NumberFormatException e) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.invalid-amount"));
            return defaultValue;
        }
    }

    private Integer parseAmountOrNull(CommandSender sender, String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get("admin.invalid-amount"));
            return null;
        }
    }

    private boolean validateArgs(CommandSender sender, String[] args, String usageKey) {
        if (args.length < 3) {
            MessageUtils.sendWithPrefix(sender, plugin, plugin.getLangManager().get(usageKey));
            return false;
        }
        return true;
    }

    private void sendSuccess(CommandSender sender, String key, String player, int amount) {
        MessageUtils.sendWithPrefix(
                sender,
                plugin,
                plugin.getLangManager().get(key)
                        .replace("{player}", player)
                        .replace("{amount}", String.valueOf(amount))
        );
    }
}