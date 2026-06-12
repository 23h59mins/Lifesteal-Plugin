package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.utils.ColorUtils;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.Map;
import java.util.UUID;

public class ReviveChatListener implements Listener {

    private final Player reviver;
    private final BanManager banManager;
    private final LifestealSMP plugin;
    private final Map<UUID, BukkitRunnable> activeRevives;

    public ReviveChatListener(Player reviver, BanManager banManager, Plugin plugin, Map<UUID, BukkitRunnable> activeRevives) {
        this.reviver = reviver;
        this.banManager = banManager;
        this.plugin = (LifestealSMP) plugin;
        this.activeRevives = activeRevives;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(reviver.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        String targetName = event.getMessage();
        if (targetName == null) {
            cleanupSession();
            return;
        }

        targetName = targetName.trim();
        if (targetName.isEmpty()) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    MessageUtils.sendWithPrefix(
                            reviver,
                            plugin,
                            plugin.getLangManager().getOrDefault("admin.player-not-found", "&cPlayer not found.")
                    )
            );
            cleanupSession();
            return;
        }

        final String finalTargetName = targetName;
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                processRevive(finalTargetName);
            } finally {
                cleanupSession();
            }
        });
    }

    private void processRevive(String targetName) {
        if (!reviver.isOnline()) {
            return;
        }

        OfflinePlayer target = findTarget(targetName);
        if (target == null || target.getUniqueId() == null || target.getName() == null) {
            MessageUtils.sendWithPrefix(
                    reviver,
                    plugin,
                    plugin.getLangManager().getOrDefault("admin.player-not-found", "&cPlayer not found.")
            );
            return;
        }

        UUID targetUUID = target.getUniqueId();

        if (!banManager.isPlayerBanned(targetUUID)) {
            MessageUtils.sendWithPrefix(
                    reviver,
                    plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-not-banned", "&eThat player is not currently banned.")
            );
            return;
        }

        if (!hasReviveBeaconInMainHand()) {
            MessageUtils.sendWithPrefix(
                    reviver,
                    plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-no-beacon", "&cNo Revive Beacon found in hand.")
            );
            return;
        }

        banManager.unbanPlayer(targetUUID);

        String message = plugin.getLangManager()
                .getOrDefault("admin.revive-success", "&a{player} has been revived!")
                .replace("{player}", target.getName());

        MessageUtils.sendWithPrefix(reviver, plugin, message);
        consumeBeacon();
    }

    private OfflinePlayer findTarget(String targetName) {
        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        if (onlineTarget != null) {
            return onlineTarget;
        }

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
        if (offlineTarget.getName() == null && !offlineTarget.hasPlayedBefore()) {
            return null;
        }

        return offlineTarget;
    }

    private boolean hasReviveBeaconInMainHand() {
        ItemStack item = reviver.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.BEACON) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        String actualName = normalize(meta.getDisplayName());
        String expectedName = normalize(plugin.getConfig().getString(
                "revive-beacon-crafting.display-name",
                "&b&lRevive Beacon"
        ));

        if (!actualName.equalsIgnoreCase(expectedName)) {
            return false;
        }

        var expectedLore = plugin.getConfig().getStringList("revive-beacon-crafting.lore");
        if (expectedLore == null || expectedLore.isEmpty()) {
            return true;
        }

        var actualLore = meta.getLore();
        if (actualLore == null || actualLore.size() != expectedLore.size()) {
            return false;
        }

        for (int i = 0; i < expectedLore.size(); i++) {
            if (!normalize(actualLore.get(i)).equalsIgnoreCase(normalize(expectedLore.get(i)))) {
                return false;
            }
        }

        return true;
    }

    private void consumeBeacon() {
        ItemStack item = reviver.getInventory().getItemInMainHand();
        if (!hasReviveBeaconInMainHand()) {
            MessageUtils.sendWithPrefix(
                    reviver,
                    plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-no-beacon", "&cNo Revive Beacon found in hand.")
            );
            return;
        }

        int newAmount = item.getAmount() - 1;
        if (newAmount > 0) {
            item.setAmount(newAmount);
        } else {
            reviver.getInventory().setItemInMainHand(null);
        }
    }

    private void cleanupSession() {
        BukkitRunnable timeoutTask = activeRevives.remove(reviver.getUniqueId());
        if (timeoutTask != null) {
            timeoutTask.cancel();
        }
        HandlerList.unregisterAll(this);
    }

    private String normalize(String input) {
        if (input == null) {
            return "";
        }

        String stripped = ColorUtils.strip(ColorUtils.color(input));
        return stripped == null ? "" : stripped.trim();
    }
}