package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles revive beacon activation and manages per-player revive states with timeouts.
 */
public class BeaconReviveListener implements Listener {

    private final BanManager banManager;
    private final LifestealSMP plugin;
    private final Map<UUID, BukkitRunnable> activeRevives = new HashMap<>();

    public BeaconReviveListener(BanManager banManager, LifestealSMP plugin) {
        this.banManager = banManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBeaconUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BEACON) return;

        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("lifesteal.revive")) {
            MessageUtils.sendWithPrefix(player, plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-no-permission", "&cYou don't have permission."));
            return;
        }

        if (activeRevives.containsKey(uuid)) {
            MessageUtils.sendWithPrefix(player, plugin, "&cYou are already reviving someone.");
            return;
        }

        event.setCancelled(true);
        MessageUtils.sendWithPrefix(player, plugin,
                plugin.getLangManager().getOrDefault("admin.revive-type-player", "&7Type the name of the player to revive in chat."));

        BukkitRunnable timeout = new BukkitRunnable() {
            @Override
            public void run() {
                activeRevives.remove(uuid);
                MessageUtils.sendWithPrefix(player, plugin,
                        plugin.getLangManager().getOrDefault("admin.revive-expired", "&cRevive session expired."));
            }
        };

        activeRevives.put(uuid, timeout);
        timeout.runTaskLater(plugin, 20 * 30); // 30 seconds timeout

        Bukkit.getPluginManager().registerEvents(
                new ReviveChatListener(player, banManager, plugin, activeRevives), plugin);
    }
}
