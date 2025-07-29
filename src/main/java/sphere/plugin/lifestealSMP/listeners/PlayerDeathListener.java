package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.managers.HeartManager;

import java.sql.SQLException;

/**
 * Handles logic when a player dies: heart removal, gain, and potential ban checks.
 */
public class PlayerDeathListener implements Listener {

    private final LifestealSMP plugin;
    private final HeartManager heartManager;
    private final BanManager banManager;

    public PlayerDeathListener(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
        this.banManager = plugin.getBanManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (victim == null || !victim.isOnline()) return;

        Player killer = victim.getKiller();
        boolean isPvP = killer != null && killer != victim;

        try {
            if (isPvP) {
                heartManager.addHearts(killer, 1);
                heartManager.removeHearts(victim, 1);
                banManager.checkBan(victim);
            } else if (plugin.getConfigManager().isDeathPenaltyEnabled()) {
                heartManager.removeHearts(victim, 1);
                banManager.checkBan(victim);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[PlayerDeathListener] SQL error processing death of " + victim.getName());
            e.printStackTrace();
        } catch (Exception e) {
            plugin.getLogger().severe("[PlayerDeathListener] Unexpected error during player death event.");
            e.printStackTrace();
        }
    }
}
