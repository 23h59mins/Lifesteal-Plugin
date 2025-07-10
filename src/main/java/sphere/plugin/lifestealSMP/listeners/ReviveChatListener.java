package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Handles player input after activating a Revive Beacon.
 * Processes target selection via chat and unbans them if valid.
 */
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
        if (!event.getPlayer().getUniqueId().equals(reviver.getUniqueId())) return;

        event.setCancelled(true);
        String targetName = event.getMessage().trim();

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                processRevive(targetName);
            } finally {
                activeRevives.remove(reviver.getUniqueId());
                AsyncPlayerChatEvent.getHandlerList().unregister(this);
            }
        });
    }

    private void processRevive(String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getUniqueId() == null || target.getName() == null) {
            MessageUtils.sendWithPrefix(reviver, plugin,
                    plugin.getLangManager().getOrDefault("admin.player-not-found", "&cPlayer not found."));
            return;
        }

        UUID targetUUID = target.getUniqueId();

        if (!banManager.isPlayerBanned(targetUUID)) {
            MessageUtils.sendWithPrefix(reviver, plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-not-banned", "&eThat player is not currently banned."));
            return;
        }

        banManager.unbanPlayer(targetUUID);

        String msg = plugin.getLangManager().getOrDefault("admin.revive-success", "&a{player} has been revived!")
                .replace("{player}", target.getName());
        MessageUtils.sendWithPrefix(reviver, plugin, msg);

        consumeBeacon();
    }

    private void consumeBeacon() {
        ItemStack item = reviver.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.BEACON) {
            MessageUtils.sendWithPrefix(reviver, plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-no-beacon", "&cNo Revive Beacon found in hand."));
            return;
        }

        int newAmount = item.getAmount() - 1;
        if (newAmount > 0) {
            item.setAmount(newAmount);
        } else {
            reviver.getInventory().setItemInMainHand(null);
        }
    }
}
