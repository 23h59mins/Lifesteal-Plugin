package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.BEACON) return;

        if (!player.hasPermission("lifesteal.revive")) {
            MessageUtils.sendWithPrefix(player, plugin, plugin.getLangManager().get("admin.revive-no-permission"));
            return;
        }

        if (activeRevives.containsKey(player.getUniqueId())) {
            MessageUtils.sendWithPrefix(player, plugin, "&cYou are already reviving someone.");
            return;
        }

        event.setCancelled(true);
        MessageUtils.sendWithPrefix(player, plugin, plugin.getLangManager().get("admin.revive-type-player"));

        BukkitRunnable timeout = new BukkitRunnable() {
            @Override
            public void run() {
                activeRevives.remove(player.getUniqueId());
                MessageUtils.sendWithPrefix(player, plugin, plugin.getLangManager().get("admin.revive-expired"));
            }
        };

        activeRevives.put(player.getUniqueId(), timeout);
        timeout.runTaskLater(plugin, 20 * 30); // 30 seconds timeout

        Bukkit.getPluginManager().registerEvents(
                new ReviveChatListener(player, banManager, plugin, activeRevives), plugin);
    }
}
