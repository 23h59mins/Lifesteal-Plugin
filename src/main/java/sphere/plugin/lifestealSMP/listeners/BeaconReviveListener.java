package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.utils.ColorUtils;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BeaconReviveListener implements Listener {

    private static final long REVIVE_TIMEOUT_TICKS = 20L * 30L;

    private final BanManager banManager;
    private final LifestealSMP plugin;
    private final Map<UUID, BukkitRunnable> activeRevives = new HashMap<>();

    public BeaconReviveListener(BanManager banManager, LifestealSMP plugin) {
        this.banManager = banManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBeaconUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (!isReviveBeacon(item)) {
            return;
        }

        event.setCancelled(true);
        startReviveSession(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBeaconPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!isReviveBeacon(item)) {
            return;
        }

        event.setCancelled(true);
        startReviveSession(event.getPlayer());
    }

    private void startReviveSession(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("lifesteal.revive")) {
            MessageUtils.sendWithPrefix(
                    player,
                    plugin,
                    plugin.getLangManager().getOrDefault("admin.revive-no-permission", "&cYou don't have permission.")
            );
            return;
        }

        if (activeRevives.containsKey(uuid)) {
            MessageUtils.sendWithPrefix(player, plugin, "&cYou are already reviving someone.");
            return;
        }

        MessageUtils.sendWithPrefix(
                player,
                plugin,
                plugin.getLangManager().getOrDefault("admin.revive-type-player", "&7Type the name of the player to revive in chat.")
        );

        BukkitRunnable timeout = new BukkitRunnable() {
            @Override
            public void run() {
                activeRevives.remove(uuid);
                MessageUtils.sendWithPrefix(
                        player,
                        plugin,
                        plugin.getLangManager().getOrDefault("admin.revive-expired", "&cRevive session expired.")
                );
            }
        };

        activeRevives.put(uuid, timeout);
        timeout.runTaskLater(plugin, REVIVE_TIMEOUT_TICKS);

        Bukkit.getPluginManager().registerEvents(
                new ReviveChatListener(player, banManager, plugin, activeRevives),
                plugin
        );
    }

    private boolean isReviveBeacon(ItemStack item) {
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

        List<String> expectedLore = plugin.getConfig().getStringList("revive-beacon-crafting.lore");
        if (expectedLore == null || expectedLore.isEmpty()) {
            return true;
        }

        List<String> actualLore = meta.getLore();
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

    private String normalize(String input) {
        if (input == null) {
            return "";
        }
        String stripped = ColorUtils.strip(ColorUtils.color(input));
        return stripped == null ? "" : stripped.trim();
    }
}