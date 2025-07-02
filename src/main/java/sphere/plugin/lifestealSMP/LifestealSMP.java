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
import sphere.plugin.lifestealSMP.utils.LangManager;

import java.sql.SQLException;

public final class LifestealSMP extends JavaPlugin {

    private static volatile LifestealSMP instance;  // Ensuring thread safety with volatile

    private ConfigManager configManager;
    private LangManager langManager;
    private DatabaseManager databaseManager;
    private HeartManager heartManager;
    private BanManager banManager;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;  // Thread-safe singleton initialization

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
        }
        log("&cLifestealSMP disabled.");
    }

    /**
     * Initializes the plugin components.
     * @throws SQLException If database setup fails.
     */
    private void init() throws SQLException {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.heartManager = new HeartManager(this);
        this.banManager = new BanManager(this);
        this.recipeManager = new RecipeManager(this);

        databaseManager.setup();  // Setup database connection
        recipeManager.registerHeartRecipe();  // Register Heart Recipe
        recipeManager.registerReviveBeaconRecipe();  // Register Revive Beacon Recipe

        registerListeners();  // Register listeners for events
        registerCommands();  // Register commands and their tab completers
    }

    /**
     * Registers the event listeners.
     */
    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new JoinQuitListener(this), this);
        pm.registerEvents(new HeartConsumeListener(this), this);
        pm.registerEvents(new BeaconReviveListener(banManager, this), this);
    }

    /**
     * Registers commands and their corresponding executors and permissions.
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
     * Registers a command dynamically by setting its executor and permission.
     * @param name The command name.
     * @param executor The command executor object.
     * @param permission The permission node for the command.
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
     * Shuts down the plugin safely, closing all resources.
     * @throws SQLException If there is an error shutting down the database connection.
     */
    private void safelyShutdown() throws SQLException {
        if (databaseManager != null) {
            databaseManager.close();  // Close the database connection
            log("&cDatabase connections closed.");
        }

        if (recipeManager != null) {
            recipeManager.unregisterRecipe();  // Unregister recipes
            log("&cRecipes unregistered.");
        }
    }

    /**
     * Logs a message to the console with color support.
     * @param message The message to log.
     */
    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(message.replace("&", "§"));
    }

    /**
     * Retrieves the plugin instance (singleton).
     * @return The LifestealSMP plugin instance.
     */
    public static LifestealSMP getInstance() {
        return instance;
    }

    // Getters for managers
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
