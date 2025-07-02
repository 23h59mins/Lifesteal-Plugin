package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.HeartManager;

import java.sql.SQLException;

public class JoinQuitListener implements Listener {

    private final HeartManager heartManager;

    public JoinQuitListener(LifestealSMP plugin) {
        this.heartManager = plugin.getHeartManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        heartManager.loadPlayer(event.getPlayer());
    }
}
