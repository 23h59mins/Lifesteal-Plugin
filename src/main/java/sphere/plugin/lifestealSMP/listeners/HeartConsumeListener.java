package sphere.plugin.lifestealSMP.listeners;

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

public class HeartConsumeListener implements Listener {

    private final LifestealSMP plugin;

    public HeartConsumeListener(LifestealSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHeartUse(PlayerInteractEvent event) throws SQLException {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != plugin.getConfigManager().getHeartItemMaterial()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = ColorUtils.color(meta.getDisplayName());
        int heartsToAdd = parseTier(displayName);
        if (heartsToAdd <= 0) return;

        plugin.getHeartManager().addHearts(event.getPlayer(), heartsToAdd);
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        MessageUtils.sendWithPrefix(event.getPlayer(), plugin,
                "&aYou gained +" + heartsToAdd + " heart" + (heartsToAdd > 1 ? "s" : "") + "!");
    }

    private int parseTier(String displayName) {
        if (displayName.contains("Tier 5")) return 5;
        if (displayName.contains("Tier 4")) return 4;
        if (displayName.contains("Tier 3")) return 3;
        if (displayName.contains("Tier 2")) return 2;
        if (displayName.contains("❤ Heart")) return 1;
        return 0;
    }
}
