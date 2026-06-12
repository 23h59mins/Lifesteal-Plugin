package sphere.plugin.lifestealSMP.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling both legacy (&) and hex color codes (&#RRGGBB).
 * Converts color codes to Bukkit-compatible color strings.
 */
public final class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtils() {
        // Prevent instantiation
    }

    /**
     * Applies color formatting to a string. Supports:
     * <ul>
     *   <li>Hex codes: &#FFAA00</li>
     *   <li>Legacy codes: &c, &l, etc.</li>
     * </ul>
     *
     * @param message the input string
     * @return colored string; returns original if message is null/blank
     */
    public static String color(String message) {
        if (message == null || message.isBlank()) return message;

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            try {
                ChatColor hexColor = ChatColor.of("#" + hexCode);
                matcher.appendReplacement(result, hexColor.toString());
            } catch (IllegalArgumentException e) {
                matcher.appendReplacement(result, ""); // skip malformed hex codes
            }
        }

        matcher.appendTail(result);
        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }

    /**
     * Strips all color codes, including legacy and hex.
     *
     * @param message the input string
     * @return clean string with color codes removed
     */
    public static String strip(String message) {
        if (message == null || message.isBlank()) return message;

        String withoutHex = HEX_PATTERN.matcher(message).replaceAll("");
        return ChatColor.stripColor(withoutHex);
    }

    /**
     * Checks if a string contains color codes (legacy or hex).
     *
     * @param input input string
     * @return true if color codes are found
     */
    public static boolean containsColor(String input) {
        return input != null &&
                (HEX_PATTERN.matcher(input).find() || input.contains("&"));
    }
}
