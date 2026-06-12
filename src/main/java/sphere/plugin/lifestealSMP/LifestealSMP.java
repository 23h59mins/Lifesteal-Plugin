package sphere.plugin.lifestealSMP;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import sphere.plugin.lifestealSMP.commands.AdminCommands;
import sphere.plugin.lifestealSMP.commands.AdminTabCompleter;
import sphere.plugin.lifestealSMP.commands.ReloadCommand;
import sphere.plugin.lifestealSMP.commands.WithdrawHeartCommand;
import sphere.plugin.lifestealSMP.config.ConfigManager;
import sphere.plugin.lifestealSMP.database.DatabaseManager;
import sphere.plugin.lifestealSMP.listeners.*;
import sphere.plugin.lifestealSMP.managers.BanManager;
import sphere.plugin.lifestealSMP.managers.HeartManager;
import sphere.plugin.lifestealSMP.managers.RecipeManager;
import sphere.plugin.lifestealSMP.placeholders.LifestealExpansion;
import sphere.plugin.lifestealSMP.utils.LangManager;

import java.sql.SQLException;

/**
 * Main class for the LifestealSMP plugin.
 * Handles lifecycle, dependency injection, and service registration.
 */
public final class LifestealSMP extends JavaPlugin {

    private static volatile LifestealSMP instance;

    private ConfigManager configManager;
    private LangManager langManager;
    private DatabaseManager databaseManager;
    private HeartManager heartManager;
    private BanManager banManager;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;
        try {
            log("&aEnabling LifestealSMP...");
            init();
            log("&aLifestealSMP enabled successfully.");
        } catch (Exception e) {
            getLogger().severe("Critical startup error: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            safelyShutdown();
        } catch (SQLException e) {
            getLogger().severe("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        log("&cLifestealSMP disabled.");
    }

    /**
     * Initializes core components and registers services.
     * @throws SQLException if database setup fails.
     */
    private void init() throws SQLException {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        log("&e[Lang] &aCreated default language file: lang_en.yml");

        this.databaseManager = new DatabaseManager(this);
        log("&e[DatabaseManager] &aUsing " + (getConfig().getBoolean("mysql.enabled") ? "MySQL" : "SQLite") + " backend.");

        this.heartManager = new HeartManager(this);
        this.banManager = new BanManager(this);
        this.recipeManager = new RecipeManager(this);

        databaseManager.setup();
        log("&aSQLite connection established.");
        log("&aSQLite table '&fplayer_hearts&a' ensured.");

        recipeManager.registerHeartRecipe();
        recipeManager.registerReviveBeaconRecipe();
        log("&bHeart & revive beacon recipes registered.");

        registerListeners();
        registerCommands();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LifestealExpansion(this).register();
            log("&bLifestealExpansion placeholders registered.");
        } else {
            log("&cPlaceholderAPI not found. Placeholders will not be available.");
        }
    }

    /**
     * Registers all plugin event listeners.
     */
    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new JoinQuitListener(this), this);
        pm.registerEvents(new HeartConsumeListener(this), this);
        pm.registerEvents(new BeaconReviveListener(banManager, this), this);
    }

    /**
     * Registers all commands and tab completers.
     */
    private void registerCommands() {
        registerDynamicCommand("withdrawheart", new WithdrawHeartCommand(this), "lifesteal.withdraw");
        registerDynamicCommand("reloadlifesteal", new ReloadCommand(this), "lifesteal.reload");
        registerDynamicCommand("lifestealadmin", new AdminCommands(this), "lifesteal.admin");

        PluginCommand adminCommand = getCommand("lifestealadmin");
        if (adminCommand != null) {
            adminCommand.setTabCompleter(new AdminTabCompleter());
        } else {
            getLogger().warning("Admin command (lifestealadmin) not found in plugin.yml.");
        }
    }

    /**
     * Registers a single command dynamically.
     *
     * @param name       Command name
     * @param executor   Command executor
     * @param permission Required permission
     */
    private void registerDynamicCommand(String name, Object executor, String permission) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor((org.bukkit.command.CommandExecutor) executor);
            command.setPermission(permission);
        } else {
            getLogger().warning("Missing command in plugin.yml: " + name);
        }
    }

    /**
     * Handles plugin shutdown and resource cleanup.
     * @throws SQLException if database shutdown fails.
     */
    private void safelyShutdown() throws SQLException {
        if (databaseManager != null) {
            databaseManager.close();
            log("&cDatabase connections closed.");
        }

        if (recipeManager != null) {
            recipeManager.unregisterRecipe();
            log("&cRecipes unregistered.");
        }
    }

    /**
     * Logs messages to the console with color support.
     *
     * @param message The message to log
     */
    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(message.replace("&", "§"));
    }

    /**
     * Returns singleton plugin instance.
     *
     * @return LifestealSMP plugin instance
     */
    public static LifestealSMP getInstance() {
        return instance;
    }

    // -------------------- Accessors --------------------

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
