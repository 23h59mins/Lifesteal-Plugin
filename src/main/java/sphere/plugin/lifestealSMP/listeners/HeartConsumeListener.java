package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.ColorUtils;
import sphere.plugin.lifestealSMP.utils.MessageUtils;

import java.sql.SQLException;

/**
 * Listens for players consuming heart items and adds the corresponding number of hearts.
 */
public class HeartConsumeListener implements Listener {

    private final LifestealSMP plugin;

    public HeartConsumeListener(LifestealSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHeartUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        ItemStack item = event.getItem();
        Material expectedMaterial = plugin.getConfigManager().getHeartItemMaterial();
        if (item == null || item.getType() != expectedMaterial || item.getAmount() <= 0) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = ColorUtils.strip(meta.getDisplayName()).toLowerCase();
        int heartsToAdd = parseTier(displayName);
        if (heartsToAdd <= 0) return;

        try {
            plugin.getHeartManager().addHearts(player, heartsToAdd);
            item.setAmount(item.getAmount() - 1);
            event.setCancelled(true);

            MessageUtils.sendWithPrefix(player, plugin,
                    "&aYou gained +" + heartsToAdd + " heart" + (heartsToAdd > 1 ? "s" : "") + "!");
        } catch (SQLException e) {
            MessageUtils.sendWithPrefix(player, plugin,
                    "&cFailed to add hearts due to a database error. Please contact staff.");
            e.printStackTrace();
        }
    }

    /**
     * Parses heart tier based on the display name.
     * Flexible to support naming patterns like "Tier 3 ❤ Heart", "❤ Heart Tier 5", etc.
     */
    private int parseTier(String name) {
        if (name.contains("tier 5")) return 5;
        if (name.contains("tier 4")) return 4;
        if (name.contains("tier 3")) return 3;
        if (name.contains("tier 2")) return 2;
        if (name.contains("heart")) return 1;
        return 0;
    }
}
