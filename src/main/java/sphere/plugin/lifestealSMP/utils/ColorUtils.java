package sphere.plugin.lifestealSMP.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for applying legacy (&) and hex color codes to messages.
 * Supports BungeeCord-compatible hex parsing: &#RRGGBB
 */
public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Applies color formatting to a message string.
     * Includes both legacy codes (&c, &l) and HEX codes (&#FFAA00).
     *
     * @param message input message
     * @return colored string or null if input was null
     */
    public static String color(String message) {
        if (message == null) return null;

        // Replace HEX codes using matcher
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            try {
                ChatColor hexColor = ChatColor.of("#" + hex);
                matcher.appendReplacement(buffer, hexColor.toString());
            } catch (IllegalArgumentException ex) {
                // Skip invalid hex color codes silently
                matcher.appendReplacement(buffer, ""); // remove invalid match
            }
        }
        matcher.appendTail(buffer);

        // Legacy color codes
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Strips all color codes (legacy and hex) from the message.
     *
     * @param message input message
     * @return clean string
     */
    public static String strip(String message) {
        if (message == null) return null;
        return ChatColor.stripColor(message.replaceAll(HEX_PATTERN.pattern(), ""));
    }
}
