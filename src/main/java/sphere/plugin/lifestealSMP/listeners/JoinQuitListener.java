package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.HeartManager;

import java.sql.SQLException;

/**
 * Loads player's heart data when they join the server.
 */
public class JoinQuitListener implements Listener {

    private final HeartManager heartManager;
    private final LifestealSMP plugin;

    public JoinQuitListener(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;

        try {
            heartManager.loadPlayer(player);
        } catch (SQLException e) {
            plugin.getLogger().severe("[JoinQuitListener] Failed to load player data for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
