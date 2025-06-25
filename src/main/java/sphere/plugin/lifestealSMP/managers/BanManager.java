package sphere.plugin.lifestealSMP.managers;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.time.Instant;
import java.util.UUID;

public class BanManager {

    private final LifestealSMP plugin;
    private final HeartManager heartManager;
    private final BanList banList;

    public BanManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartManager = plugin.getHeartManager();
        this.banList = Bukkit.getBanList(BanList.Type.NAME);
    }

    public void checkBan(Player player) {
        int hearts = heartManager.getHearts(player);
        if (hearts <= 0) {
            ban(player);
        } else {
            heartManager.applyMaxHealth(player, hearts);
        }
    }

    private void ban(Player player) {
        String message = plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getBanMessage();
        player.kickPlayer(message);

        // Explicitly specify String version of addBan
        banList.addBan(String.valueOf((Object) player.getName()), message, (Instant) null, "LifestealSMP");
    }

    public boolean isPlayerBanned(UUID uuid) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        if (target.getName() == null) return false;

        BanEntry entry = banList.getBanEntry(target.getName());
        return entry != null;
    }

    public void unbanPlayer(UUID uuid) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        if (target == null || target.getName() == null) return;

        banList.pardon(target.getName());
    }
}
