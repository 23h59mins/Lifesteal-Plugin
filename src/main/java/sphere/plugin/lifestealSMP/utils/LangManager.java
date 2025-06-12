package sphere.plugin.lifestealSMP.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import sphere.plugin.lifestealSMP.LifestealSMP;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {

    private final LifestealSMP plugin;
    private FileConfiguration lang;

    public LangManager(LifestealSMP plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    private void loadLanguage() {
        String langCode = plugin.getConfigManager().getLanguageFile();
        String langFileName = "lang_" + langCode + ".yml";
        File langFile = new File(plugin.getDataFolder(), langFileName);

        if (!langFile.exists()) {
            plugin.saveResource(langFileName, false);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        InputStream defaultStream = plugin.getResource(langFileName);
        if (defaultStream != null) {
            YamlConfiguration defaultLang = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            lang.setDefaults(defaultLang);
        }
    }

    public String get(String key) {
        return get(key, "&cMissing lang: " + key);
    }

    public String get(String key, String def) {
        String value = lang.getString(key, def);
        return ColorUtils.color(value);
    }

    public void reload() {
        loadLanguage();
    }

    public ConfigurationSection getLang() {
        return lang;
    }
}
