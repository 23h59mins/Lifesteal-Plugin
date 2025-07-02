package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.managers.HeartManager;

import java.sql.SQLException;

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
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && killer != victim) {
            heartManager.addHearts(killer, 1);
            heartManager.removeHearts(victim, 1);
            banManager.checkBan(victim);
        } else {
            if (plugin.getConfigManager().isDeathPenaltyEnabled()) {
                heartManager.removeHearts(victim, 1);
                banManager.checkBan(victim);
            }
        }
    }
}
