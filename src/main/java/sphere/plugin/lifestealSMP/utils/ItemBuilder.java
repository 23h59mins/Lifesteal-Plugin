package sphere.plugin.lifestealSMP.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder class for constructing customized ItemStacks.
 * Provides chainable methods for setting name, lore, amount, and more.
 */
public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    /**
     * Constructs an ItemBuilder with the specified material.
     * Always ensures a valid ItemStack is created.
     *
     * @param material the material to use
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(Objects.requireNonNullElse(material, Material.STONE));
        this.meta = this.item.getItemMeta(); // Can still be null (e.g., Material.AIR)
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the name string
     * @return this builder
     */
    public ItemBuilder name(String name) {
        if (meta != null && name != null && !name.isBlank()) {
            meta.setDisplayName(ColorUtils.color(name));
        }
        return this;
    }

    /**
     * Sets the lore of the item using varargs.
     *
     * @param lore lines of lore
     * @return this builder
     */
    public ItemBuilder lore(String... lore) {
        if (lore != null && lore.length > 0) {
            return lore(List.of(lore));
        }
        return this;
    }

    /**
     * Sets the lore of the item from a list.
     *
     * @param lore list of lines
     * @return this builder
     */
    public ItemBuilder lore(List<String> lore) {
        if (meta != null && lore != null && !lore.isEmpty()) {
            meta.setLore(applyColors(lore));
        }
        return this;
    }

    /**
     * Sets the amount of items (clamped to 1-64).
     *
     * @param amount the item count
     * @return this builder
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    /**
     * Returns a finalized copy of the customized ItemStack.
     *
     * @return the item
     */
    public ItemStack build() {
        ItemStack result = item.clone();
        if (meta != null) {
            result.setItemMeta(meta);
        }
        return result;
    }

    /**
     * Convenience method for generating a tiered heart item.
     *
     * @param material base material
     * @param tier     tier 1-5
     * @return custom heart item
     */
    public static ItemStack buildHeart(Material material, int tier) {
        int safeTier = Math.max(1, Math.min(5, tier));
        return new ItemBuilder(material)
                .name("&c❤ Heart Tier " + safeTier)
                .lore("&7Right-click to gain " + safeTier + " heart" + (safeTier > 1 ? "s" : "") + "!")
                .build();
    }

    /**
     * Converts a list of strings to their colored equivalents.
     *
     * @param input raw lore lines
     * @return list with color formatting
     */
    private List<String> applyColors(List<String> input) {
        List<String> result = new ArrayList<>(input.size());
        for (String line : input) {
            result.add(ColorUtils.color(line));
        }
        return result;
    }
}
