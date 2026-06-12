package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.ItemBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages registration of custom crafting recipes (Heart & Revive Beacon).
 */
public class RecipeManager {

    private final LifestealSMP plugin;
    private final NamespacedKey heartRecipeKey;
    private final NamespacedKey reviveBeaconRecipeKey;

    public RecipeManager(LifestealSMP plugin) {
        this.plugin = plugin;
        this.heartRecipeKey = new NamespacedKey(plugin, "heart_crafting");
        this.reviveBeaconRecipeKey = new NamespacedKey(plugin, "revive_beacon");
    }

    /**
     * Registers all plugin recipes in one call.
     */
    public void registerAllRecipes() {
        registerHeartRecipe();
        registerReviveBeaconRecipe();
    }

    public void registerHeartRecipe() {
        if (!plugin.getConfig().getBoolean("heart-crafting.enabled", false)) {
            plugin.log("&e[Recipe] Heart crafting is disabled in the config.");
            return;
        }

        int tier = plugin.getConfig().getInt("heart-crafting.tier", 1);
        ItemStack heartItem = new ItemBuilder(plugin.getConfigManager().getHeartItemMaterial())
                .name(plugin.getConfig().getString("heart-crafting.display-name", "&c❤ Heart Tier " + tier))
                .lore(plugin.getConfig().getStringList("heart-crafting.lore"))
                .amount(1)
                .build();

        removeRecipeIfExists(heartRecipeKey);

        List<String> shapeList = plugin.getConfig().getStringList("heart-crafting.recipe.shape");
        if (!validateShape(shapeList)) {
            plugin.log("&c[Recipe] Invalid or missing shape for heart recipe.");
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
                .name(plugin.getConfig().getString("revive-beacon-crafting.display-name", "&b&lRevive Beacon"))
                .lore(plugin.getConfig().getStringList("revive-beacon-crafting.lore"))
                .amount(1)
                .build();

        removeRecipeIfExists(reviveBeaconRecipeKey);

        List<String> shapeList = plugin.getConfig().getStringList("revive-beacon-crafting.recipe.shape");
        if (!validateShape(shapeList)) {
            plugin.log("&c[Recipe] Invalid or missing shape for revive beacon recipe.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(reviveBeaconRecipeKey, beaconItem);
        recipe.shape(shapeList.toArray(new String[0]));

        if (!parseAndSetIngredients(recipe, "revive-beacon-crafting.recipe.ingredients", shapeList)) return;

        Bukkit.addRecipe(recipe);
        plugin.log("&a[Recipe] Revive Beacon recipe registered.");
    }

    /**
     * Parses ingredient mappings from the config and applies them to the recipe.
     */
    private boolean parseAndSetIngredients(ShapedRecipe recipe, String configPath, List<String> shapeList) {
        Set<Character> seenSymbols = new HashSet<>();

        for (String row : shapeList) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ' || seenSymbols.contains(symbol)) continue;
                seenSymbols.add(symbol);

                String matName = plugin.getConfig().getString(configPath + "." + symbol);
                if (matName == null || matName.isEmpty()) {
                    plugin.log("&c[Recipe] Missing ingredient for symbol '" + symbol + "' in " + configPath);
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

    /**
     * Validates a shape list (must be 1-3 rows, max 3 chars each).
     */
    private boolean validateShape(List<String> shapeList) {
        if (shapeList == null || shapeList.isEmpty()) return false;
        if (shapeList.size() > 3) return false;
        return shapeList.stream().allMatch(row -> row.length() <= 3);
    }

    /**
     * Removes a recipe if it exists.
     */
    private void removeRecipeIfExists(NamespacedKey key) {
        try {
            Bukkit.removeRecipe(key);
        } catch (Exception ignored) {
            // Some Bukkit versions may throw NPEs if the recipe does not exist.
        }
    }

    public void unregisterRecipe() {
        removeRecipeIfExists(heartRecipeKey);
        removeRecipeIfExists(reviveBeaconRecipeKey);
        plugin.log("&e[Recipe] All custom recipes unregistered.");
    }
}
