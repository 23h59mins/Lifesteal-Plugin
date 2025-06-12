package sphere.plugin.lifestealSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.managers.HeartManager;

public class JoinQuitListener implements Listener {

    private final LifestealSMP plugin;
    private final HeartManager heartManager;

    public JoinQuitListener(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        heartManager.loadPlayer(event.getPlayer());
    }
}
