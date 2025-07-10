package sphere.plugin.lifestealSMP.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class AdminTabCompleter implements TabCompleter {

    private static final Map<String, String> SUBCOMMANDS = new LinkedHashMap<>();
    private static final List<String> HEART_AMOUNTS = List.of("1", "5", "10", "20", "40");
    private static final List<String> TIER_VALUES = List.of("1", "2", "3", "4", "5");
    private static final List<String> MAX_HEART_OPTIONS = List.of("40", "50", "100", "200");
    private static final Set<String> PLAYER_SUBCOMMANDS = Set.of("set", "add", "remove", "giveheart", "revivebeacon", "unban");

    static {
        SUBCOMMANDS.put("set", "lifesteal.admin");
        SUBCOMMANDS.put("add", "lifesteal.admin");
        SUBCOMMANDS.put("remove", "lifesteal.admin");
        SUBCOMMANDS.put("giveheart", "lifesteal.admin");
        SUBCOMMANDS.put("revivebeacon", "lifesteal.admin");
        SUBCOMMANDS.put("unban", "lifesteal.admin");
        SUBCOMMANDS.put("purgebans", "lifesteal.admin");
        SUBCOMMANDS.put("setmax", "lifesteal.admin");
        SUBCOMMANDS.put("resetall", "lifesteal.admin");
        SUBCOMMANDS.put("banlist", "lifesteal.admin");
        SUBCOMMANDS.put("reload", "lifesteal.admin");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null || args.length == 0 || sender == null) {
            return Collections.emptyList();
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (args.length) {
            case 1:
                return partialMatch(args[0], getAllowedSubcommands(sender));

            case 2:
                if (PLAYER_SUBCOMMANDS.contains(sub)) {
                    return partialMatch(args[1], getOnlinePlayerNames());
                } else if ("setmax".equals(sub)) {
                    return partialMatch(args[1], MAX_HEART_OPTIONS);
                }
                return Collections.emptyList();

            case 3:
                if (List.of("set", "add", "remove").contains(sub)) {
                    return partialMatch(args[2], HEART_AMOUNTS);
                } else if ("giveheart".equals(sub)) {
                    return partialMatch(args[2], TIER_VALUES);
                }
                return Collections.emptyList();

            case 4:
                if ("giveheart".equals(sub)) {
                    return partialMatch(args[3], HEART_AMOUNTS);
                }
                return Collections.emptyList();

            default:
                return Collections.emptyList();
        }
    }

    private List<String> getAllowedSubcommands(CommandSender sender) {
        List<String> allowed = new ArrayList<>();
        for (Map.Entry<String, String> entry : SUBCOMMANDS.entrySet()) {
            String permission = entry.getValue();
            if (permission == null || sender.hasPermission(permission)) {
                allowed.add(entry.getKey());
            }
        }
        return allowed;
    }

    private List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (name != null && !name.isBlank()) {
                names.add(name);
            }
        }
        return names;
    }

    private List<String> partialMatch(String input, List<String> options) {
        if (input == null || input.isBlank()) return new ArrayList<>(options);

        String lowered = input.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lowered)) {
                result.add(option);
            }
        }

        return result;
    }
}
