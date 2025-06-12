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

        Bukkit.getScheduler().runTask(plugin, () -> processRevive(targetName));
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }

    private void processRevive(String targetName) {
        activeRevives.remove(reviver.getUniqueId());

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getUniqueId() == null) {
            MessageUtils.sendWithPrefix(reviver, plugin, plugin.getLangManager().get("admin.player-not-found"));
            return;
        }

        UUID targetUUID = target.getUniqueId();
        if (!banManager.isPlayerBanned(targetUUID)) {
            MessageUtils.sendWithPrefix(reviver, plugin, plugin.getLangManager().get("admin.revive-not-banned"));
            return;
        }

        banManager.unbanPlayer(targetUUID);

        String successMsg = plugin.getLangManager().get("admin.revive-success")
                .replace("{player}", target.getName());
        MessageUtils.sendWithPrefix(reviver, plugin, successMsg);

        consumeBeacon();
    }

    private void consumeBeacon() {
        ItemStack hand = reviver.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() != Material.BEACON) {
            MessageUtils.sendWithPrefix(reviver, plugin, plugin.getLangManager().get("admin.revive-no-beacon"));
            return;
        }

        if (hand.getAmount() > 1) {
            hand.setAmount(hand.getAmount() - 1);
        } else {
            reviver.getInventory().setItemInMainHand(null);
        }
    }
}
