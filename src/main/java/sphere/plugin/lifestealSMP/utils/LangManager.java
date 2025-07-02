package sphere.plugin.lifestealSMP.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Manages the plugin's language configuration system.
 * Supports dynamic loading, defaults, and color formatting.
 */
public class LangManager {

    private final LifestealSMP plugin;
    private final Logger logger;
    private FileConfiguration lang;

    public LangManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        loadLanguage();
    }

    /**
     * Loads the language YAML configuration file from disk,
     * merges with internal default (packaged) resource.
     */
    private void loadLanguage() {
        String langCode = plugin.getConfigManager().getLanguageFile(); // e.g., "en"
        String fileName = "lang_" + langCode + ".yml";
        File langFile = new File(plugin.getDataFolder(), fileName);

        if (!langFile.exists()) {
            plugin.saveResource(fileName, false);
            logger.info("Language file '" + fileName + "' created.");
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
            );
            lang.setDefaults(defaults);
            lang.options().copyDefaults(true);
        }
    }

    /**
     * Retrieves a language string by key. Applies color formatting.
     * Returns default-style error if missing.
     *
     * @param key the language key
     * @return formatted message or fallback error
     */
    public String get(String key) {
        return get(key, "&cMissing lang: " + key);
    }

    /**
     * Retrieves a language string by key, returns a custom fallback if not found.
     * Automatically applies color formatting.
     *
     * @param key the language key
     * @param def fallback message if not found
     * @return formatted message
     */
    public String get(String key, String def) {
        if (key == null || key.trim().isEmpty()) {
            logger.warning("Attempted to fetch null/empty language key.");
            return ColorUtils.color(def);
        }

        String value = lang.getString(key);
        if (value == null) {
            logger.warning("Missing language key: '" + key + "'");
            value = def;
        }

        return ColorUtils.color(value);
    }

    /**
     * Retrieves a formatted language string (e.g., with placeholders).
     *
     * @param key  the language key
     * @param args placeholder values
     * @return formatted and colored message
     */
    public String getFormatted(String key, Object... args) {
        String raw = get(key);
        return ColorUtils.color(String.format(raw, args));
    }

    /**
     * Reloads the language configuration from disk.
     */
    public void reload() {
        loadLanguage();
        logger.info("Language file reloaded.");
    }

    /**
     * Returns the raw configuration section (for advanced access).
     *
     * @return configuration section root
     */
    public ConfigurationSection getLang() {
        return lang;
    }
}
