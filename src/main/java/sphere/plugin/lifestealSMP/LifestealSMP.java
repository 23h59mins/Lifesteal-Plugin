package sphere.plugin.lifestealSMP;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
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

import java.lang.reflect.Field;

public final class LifestealSMP extends JavaPlugin {

    private static LifestealSMP instance;

    private ConfigManager configManager;
    private LangManager langManager;
    private DatabaseManager databaseManager;
    private HeartManager heartManager;
    private BanManager banManager;
    private RecipeManager recipeManager;

    private CommandMap commandMap;

    @Override
    public void onEnable() {
        instance = this;

        try {
            initializeCoreSystems();
            registerListeners();
            setupCommandMap();
            registerCommands();
            log("&a[LifestealSMP] Plugin successfully enabled.");
        } catch (Exception e) {
            log("&c[LifestealSMP] Critical error during enable: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        safelyShutdown();
        log("&c[LifestealSMP] Plugin disabled.");
    }

    private void initializeCoreSystems() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.heartManager = new HeartManager(this);
        this.banManager = new BanManager(this);
        this.recipeManager = new RecipeManager(this);

        databaseManager.setup();
        registerRecipes();
    }

    private void registerRecipes() {
        recipeManager.registerHeartRecipe();
        recipeManager.registerReviveBeaconRecipe();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new JoinQuitListener(this), this);
        pm.registerEvents(new HeartConsumeListener(this), this);
        pm.registerEvents(new BeaconReviveListener(banManager, this), this);
    }

    private void setupCommandMap() throws Exception {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        this.commandMap = (SimpleCommandMap) field.get(Bukkit.getServer());
    }

    private void registerCommands() {
        registerDynamicCommand("withdrawheart", new WithdrawHeartCommand(this), "lifesteal.withdraw");
        registerDynamicCommand("reloadlifesteal", new ReloadCommand(this), "lifesteal.reload");
        registerDynamicCommand("lifestealadmin", new AdminCommands(this), "lifesteal.admin");

        // Tab completer only for admin command
        PluginCommand adminCommand = this.getCommand("lifestealadmin");
        if (adminCommand != null) {
            adminCommand.setTabCompleter(new AdminTabCompleter());
        }
    }

    private void registerDynamicCommand(String name, Object executor, String permission) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor((org.bukkit.command.CommandExecutor) executor);
            command.setPermission(permission);
        } else {
            log("&c[Warning] Command not found in plugin.yml: " + name);
        }
    }

    private void safelyShutdown() {
        if (databaseManager != null) databaseManager.close();
        if (recipeManager != null) recipeManager.unregisterRecipe();
    }

    public static LifestealSMP getInstance() {
        return instance;
    }

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

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(message.replace("&", "§"));
    }
}
