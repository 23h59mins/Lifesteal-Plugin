package sphere.plugin.lifestealSMP.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String message) {
        if (message == null) return null;

        // HEX support
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            ChatColor color = ChatColor.of("#" + hexCode);
            message = message.replace("&#" + hexCode, color.toString());
        }

        // Legacy color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
