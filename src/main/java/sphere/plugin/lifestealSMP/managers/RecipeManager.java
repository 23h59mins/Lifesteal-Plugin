package sphere.plugin.lifestealSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import sphere.plugin.lifestealSMP.LifestealSMP;
import sphere.plugin.lifestealSMP.utils.ItemBuilder;

import java.util.List;

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
            plugin.log("&e[Recipe] Heart crafting disabled in config.");
            return;
        }

        ItemStack heartItem = new ItemBuilder(plugin.getConfigManager().getHeartItemMaterial())
                .name("&c❤ Heart")
                .lore("&7Right-click to gain 1 heart!")
                .build();

        Bukkit.removeRecipe(heartRecipeKey);

        ShapedRecipe recipe = new ShapedRecipe(heartRecipeKey, heartItem);

        List<String> shapeList = plugin.getConfig().getStringList("heart-crafting.recipe.shape");
        if (shapeList.isEmpty()) {
            plugin.log("&c[Recipe] No recipe shape found in config.");
            return;
        }
        recipe.shape(shapeList.toArray(new String[0]));

        for (String row : shapeList) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ') continue;

                String materialName = plugin.getConfig().getString("heart-crafting.recipe.ingredients." + symbol);
                if (materialName == null) {
                    plugin.log("&c[Recipe] Missing material for symbol '" + symbol + "' in config.");
                    return;
                }

                Material mat = Material.matchMaterial(materialName.toUpperCase());
                if (mat == null) {
                    plugin.log("&c[Recipe] Invalid material: " + materialName);
                    return;
                }
                recipe.setIngredient(symbol, mat);
            }
        }

        Bukkit.addRecipe(recipe);
        plugin.log("&a[Recipe] Heart crafting recipe registered successfully.");
    }

    public void registerReviveBeaconRecipe() {
        if (!plugin.getConfig().getBoolean("revive-beacon-crafting.enabled", false)) {
            plugin.log("&e[Recipe] Revive Beacon crafting disabled in config.");
            return;
        }

        ItemStack reviveBeacon = new ItemBuilder(Material.BEACON)
                .name("&b&lRevive Beacon")
                .lore("&7Use this item to revive a banned player.")
                .amount(1)
                .build();

        Bukkit.removeRecipe(reviveBeaconRecipeKey);

        ShapedRecipe recipe = new ShapedRecipe(reviveBeaconRecipeKey, reviveBeacon);

        List<String> shapeList = plugin.getConfig().getStringList("revive-beacon-crafting.recipe.shape");
        if (shapeList.isEmpty()) {
            plugin.log("&c[Recipe] No revive beacon recipe shape found in config.");
            return;
        }
        recipe.shape(shapeList.toArray(new String[0]));

        for (String row : shapeList) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ') continue;

                String materialName = plugin.getConfig().getString("revive-beacon-crafting.recipe.ingredients." + symbol);
                if (materialName == null) {
                    plugin.log("&c[Recipe] Missing material for symbol '" + symbol + "' in revive beacon config.");
                    return;
                }

                Material mat = Material.matchMaterial(materialName.toUpperCase());
                if (mat == null) {
                    plugin.log("&c[Recipe] Invalid material: " + materialName);
                    return;
                }
                recipe.setIngredient(symbol, mat);
            }
        }

        Bukkit.addRecipe(recipe);
        plugin.log("&a[Recipe] Revive Beacon recipe registered successfully.");
    }

    public void unregisterRecipe() {
        Bukkit.removeRecipe(heartRecipeKey);
        Bukkit.removeRecipe(reviveBeaconRecipeKey);
    }
}
