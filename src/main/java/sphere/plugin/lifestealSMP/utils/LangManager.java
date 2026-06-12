package sphere.plugin.lifestealSMP.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Handles loading, parsing, and retrieving of plugin language files.
 * Supports default fallbacks and color formatting.
 */
public class LangManager {

    private final LifestealSMP plugin;
    private final Logger logger;
    private FileConfiguration lang;

    public LangManager(LifestealSMP plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.logger = plugin.getLogger();
        this.lang = YamlConfiguration.loadConfiguration(new File("fallback.yml")); // never null
        loadLanguage();
    }

    /**
     * Loads and merges the selected language file.
     */
    private void loadLanguage() {
        String langCode = plugin.getConfigManager().getLanguageFile(); // e.g. "en"
        String fileName = "lang_" + langCode + ".yml";
        File langFile = new File(plugin.getDataFolder(), fileName);

        if (!langFile.exists()) {
            plugin.saveResource(fileName, false);
            logger.info("[Lang] Created default language file: " + fileName);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        try (InputStream stream = plugin.getResource(fileName)) {
            if (stream != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                lang.setDefaults(defaults);
                lang.options().copyDefaults(true);
            }
        } catch (Exception e) {
            logger.warning("[Lang] Failed to load defaults for " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves a language string by key, with formatting.
     *
     * @param key the key path
     * @return formatted string
     */
    public String get(String key) {
        return get(key, "&cMissing lang: " + key);
    }

    /**
     * Gets a language string by key, or fallback if missing.
     *
     * @param key key path
     * @param def default fallback
     * @return formatted string
     */
    public String get(String key, String def) {
        if (key == null || key.trim().isEmpty()) {
            logger.warning("[Lang] Null or empty key requested.");
            return ColorUtils.color(def);
        }

        String value = lang.getString(key);
        if (value == null) {
            logger.warning("[Lang] Missing key: '" + key + "'");
            return ColorUtils.color(def);
        }

        return ColorUtils.color(value);
    }

    /**
     * Returns a formatted and colored string with placeholders.
     *
     * @param key  config key
     * @param args placeholder values
     * @return formatted string
     */
    public String getFormatted(String key, Object... args) {
        String base = get(key);
        try {
            return ColorUtils.color(String.format(base, args));
        } catch (Exception e) {
            logger.warning("[Lang] Format error on key '" + key + "': " + e.getMessage());
            return base;
        }
    }

    /**
     * Gets a key if exists or fallback directly.
     *
     * @param key config key
     * @param fallback fallback value
     * @return formatted string
     */
    public String getOrDefault(String key, String fallback) {
        return get(key, fallback);
    }

    /**
     * Reloads the language file.
     */
    public void reload() {
        loadLanguage();
        logger.info("[Lang] Language reloaded.");
    }

    /**
     * Raw config section (advanced use).
     *
     * @return root config
     */
    public ConfigurationSection getLang() {
        return lang;
    }
}
