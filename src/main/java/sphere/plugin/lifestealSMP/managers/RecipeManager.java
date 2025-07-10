package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.ItemBuilder;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class RecipeManager {

    private final LifestealSMP plugin;
    private final NamespacedKey heartRecipeKey;
    private final NamespacedKey reviveBeaconRecipeKey;

    public RecipeManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartRecipeKey = new NamespacedKey(plugin, "heart_crafting");
        this.reviveBeaconRecipeKey = new NamespacedKey(plugin, "revive_beacon");
    }

    public void registerHeartRecipe() {
        if (!plugin.getConfig().getBoolean("heart-crafting.enabled", false)) {
            plugin.log("&e[Recipe] Heart crafting is disabled in the config.");
            return;
        }

        ItemStack heartItem = new ItemBuilder(plugin.getConfigManager().getHeartItemMaterial())
                .name("&c❤ Heart")
                .lore("&7Right-click to gain 1 heart!")
                .amount(1)
                .build();

        Bukkit.removeRecipe(heartRecipeKey);

        List<String> shapeList = plugin.getConfig().getStringList("heart-crafting.recipe.shape");
        if (shapeList == null || shapeList.isEmpty()) {
            plugin.log("&c[Recipe] No shape defined for heart recipe.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(heartRecipeKey, heartItem);
        recipe.shape(shapeList.toArray(new String[0]));

        if (!parseAndSetIngredients(recipe, "heart-crafting.recipe.ingredients", shapeList)) return;

        Bukkit.addRecipe(recipe);
        plugin.log("&a[Recipe] Heart crafting recipe registered.");
    }

    public void registerReviveBeaconRecipe() {
        if (!plugin.getConfig().getBoolean("revive-beacon-crafting.enabled", false)) {
            plugin.log("&e[Recipe] Revive beacon crafting is disabled in the config.");
            return;
        }

        ItemStack beaconItem = new ItemBuilder(Material.BEACON)
                .name("&b&lRevive Beacon")
                .lore("&7Use this item to revive a banned player.")
                .amount(1)
                .build();

        Bukkit.removeRecipe(reviveBeaconRecipeKey);

        List<String> shapeList = plugin.getConfig().getStringList("revive-beacon-crafting.recipe.shape");
        if (shapeList == null || shapeList.isEmpty()) {
            plugin.log("&c[Recipe] No shape defined for revive beacon recipe.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(reviveBeaconRecipeKey, beaconItem);
        recipe.shape(shapeList.toArray(new String[0]));

        if (!parseAndSetIngredients(recipe, "revive-beacon-crafting.recipe.ingredients", shapeList)) return;

        Bukkit.addRecipe(recipe);
        plugin.log("&a[Recipe] Revive Beacon recipe registered.");
    }

    private boolean parseAndSetIngredients(ShapedRecipe recipe, String configPath, List<String> shapeList) {
        Set<Character> seenSymbols = new HashSet<>();

        for (String row : shapeList) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ' || seenSymbols.contains(symbol)) continue;
                seenSymbols.add(symbol);

                String matName = plugin.getConfig().getString(configPath + "." + symbol);
                if (matName == null || matName.isEmpty()) {
                    plugin.log("&c[Recipe] Missing ingredient for symbol '" + symbol + "' in config.");
                    return false;
                }

                Material material = Material.matchMaterial(matName.toUpperCase());
                if (material == null) {
                    plugin.log("&c[Recipe] Invalid material '" + matName + "' for symbol '" + symbol + "'.");
                    return false;
                }

                recipe.setIngredient(symbol, material);
            }
        }

        return true;
    }

    public void unregisterRecipe() {
        Bukkit.removeRecipe(heartRecipeKey);
        Bukkit.removeRecipe(reviveBeaconRecipeKey);
        plugin.log("&e[Recipe] All custom recipes unregistered.");
    }
}
