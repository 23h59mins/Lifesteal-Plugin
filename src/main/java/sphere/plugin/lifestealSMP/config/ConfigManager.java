package sphere.plugin.lifestealSMP.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import sphere.plugin.lifestealSMP.utils.ColorUtils;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    // ============= Reload config safely =============

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // ============= Core settings =============

    public int getMaxHearts() {
        return config.getInt("max-hearts", 40);
    }

    public int getStartingHearts() {
        return config.getInt("starting-hearts", 10);
    }

    public boolean isDeathPenaltyEnabled() {
        return config.getBoolean("death-penalty-enabled", true);
    }

    public Material getHeartItemMaterial() {
        return parseMaterial(config.getString("heart-item-material", "NETHER_STAR"), Material.NETHER_STAR);
    }

    // ============= Database =============

    public boolean isMysqlEnabled() {
        return config.getBoolean("mysql.enabled", false);
    }

    public String getMysqlHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int getMysqlPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String getMysqlDatabase() {
        return config.getString("mysql.database", "lifestealsmp");
    }

    public String getMysqlUsername() {
        return config.getString("mysql.username", "root");
    }

    public String getMysqlPassword() {
        return config.getString("mysql.password", "");
    }

    // ============= Language & Prefix =============

    public String getLanguageFile() {
        return config.getString("languages", "en");
    }

    public String getPrefix() {
        return color("messages.prefix", "&c&lLifesteal &7» ");
    }

    // ============= Messages =============

    public String getBanMessage() {
        return color("messages.ban-message", "&cYou have run out of hearts!");
    }

    public String getWithdrawSuccess() {
        return color("messages.withdraw-success", "&aYou withdrew 1 heart!");
    }

    public String getReloadSuccess() {
        return color("messages.reload-success", "&aConfiguration reloaded!");
    }

    public String getNoPermission() {
        return color("messages.no-permission", "&cYou do not have permission!");
    }

    // ============= Recipe Toggles =============

    public boolean isHeartCraftingEnabled() {
        return config.getBoolean("heart-crafting.enabled", true);
    }

    public boolean isReviveBeaconCraftingEnabled() {
        return config.getBoolean("revive-beacon-crafting.enabled", true);
    }

    // ============= Internal Helpers =============

    private Material parseMaterial(String input, Material fallback) {
        if (input == null || input.isBlank()) {
            plugin.getLogger().warning("[Config] Material path is null or empty. Using fallback: " + fallback);
            return fallback;
        }

        try {
            return Material.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[Config] Invalid material: '" + input + "'. Using fallback: " + fallback);
            return fallback;
        }
    }

    private String color(String path, String def) {
        return ColorUtils.color(config.getString(path, def));
    }
}
