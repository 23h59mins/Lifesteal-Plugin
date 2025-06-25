package sphere.plugin.lifestealSMP.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class AdminTabCompleter implements TabCompleter {

    private static final Map<String, String> SUBCOMMANDS = new LinkedHashMap<>();

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

        if (args.length == 1) {
            return partialMatch(args[0], getAllowedSubcommands(sender));
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            // Suggest player names only if player argument provided (not mandatory)
            if (List.of("set", "add", "remove", "giveheart", "revivebeacon", "unban").contains(sub)) {
                List<String> players = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    players.add(player.getName());
                }
                return partialMatch(args[1], players);
            }

            if (sub.equals("setmax")) {
                return List.of("40", "50", "100", "200");
            }
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase();

            if (List.of("set", "add", "remove").contains(sub)) {
                return List.of("1", "5", "10", "20", "40");
            }

            if (sub.equals("giveheart")) {
                // Suggest tiers 1-5 for giveheart
                return List.of("1", "2", "3", "4", "5");
            }
        }

        if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("giveheart")) {
                return List.of("1", "5", "10", "20", "40");
            }
        }

        return Collections.emptyList();
    }

    private List<String> getAllowedSubcommands(CommandSender sender) {
        List<String> allowed = new ArrayList<>();
        for (Map.Entry<String, String> entry : SUBCOMMANDS.entrySet()) {
            if (entry.getValue() == null || sender.hasPermission(entry.getValue())) {
                allowed.add(entry.getKey());
            }
        }
        return allowed;
    }

    private List<String> partialMatch(String input, List<String> options) {
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(input.toLowerCase())) {
                result.add(option);
            }
        }
        return result;
    }
}
