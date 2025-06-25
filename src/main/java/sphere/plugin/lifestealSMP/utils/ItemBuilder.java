package sphere.plugin.lifestealSMP.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to build customized ItemStacks with fluent API.
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    /**
     * Constructs an ItemBuilder for the specified material.
     *
     * @param material material type
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta(); // can be null for AIR or invalid items
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name
     * @return the builder instance
     */
    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(ColorUtils.color(name));
        }
        return this;
    }

    /**
     * Sets the item lore from varargs.
     *
     * @param lore lines of lore
     * @return the builder instance
     */
    public ItemBuilder lore(String... lore) {
        if (lore == null || lore.length == 0) return this;
        return lore(List.of(lore));
    }

    /**
     * Sets the item lore from a list.
     *
     * @param lore list of lore lines
     * @return the builder instance
     */
    public ItemBuilder lore(List<String> lore) {
        if (meta != null && lore != null && !lore.isEmpty()) {
            meta.setLore(colorList(lore));
        }
        return this;
    }

    /**
     * Sets the item amount (1-64).
     *
     * @param amount amount to set
     * @return the builder instance
     */
    public ItemBuilder amount(int amount) {
        this.item.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    /**
     * Builds and returns a new cloned ItemStack with applied metadata.
     *
     * @return customized ItemStack
     */
    public ItemStack build() {
        ItemStack cloned = item.clone();
        if (meta != null) {
            cloned.setItemMeta(meta);
        }
        return cloned;
    }

    /**
     * Utility to build a heart-tiered item with name/lore.
     *
     * @param material item material
     * @param tier     tier between 1 and 5
     * @return custom heart item
     */
    public static ItemStack buildHeart(Material material, int tier) {
        int heartsToAdd = Math.max(1, Math.min(tier, 5));
        String name = "&c❤ Heart Tier " + heartsToAdd;
        String lore = "&7Right-click to gain " + heartsToAdd + " heart" + (heartsToAdd > 1 ? "s" : "") + "!";
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .build();
    }

    /**
     * Applies color formatting to a list of strings.
     *
     * @param input list of raw strings
     * @return new list with colored strings
     */
    private List<String> colorList(List<String> input) {
        if (input == null) return Collections.emptyList();
        List<String> result = new ArrayList<>(input.size());
        for (String line : input) {
            result.add(ColorUtils.color(line));
        }
        return result;
    }
}
