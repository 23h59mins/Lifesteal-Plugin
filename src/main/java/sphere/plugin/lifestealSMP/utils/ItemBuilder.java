package sphere.plugin.lifestealSMP.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(ColorUtils.color(name));
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (meta != null) {
            List<String> loreList = new ArrayList<>();
            for (String s : lore) {
                loreList.add(ColorUtils.color(s));
            }
            meta.setLore(loreList);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (meta != null) {
            List<String> colored = new ArrayList<>();
            for (String s : lore) {
                colored.add(ColorUtils.color(s));
            }
            meta.setLore(colored);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    // ✅ New: Helper for tiered heart items
    public static ItemStack buildHeart(Material material, int tier) {
        int heartsToAdd = Math.max(1, Math.min(tier, 5)); // limit tier 1-5
        String name = "&c❤ Heart Tier " + heartsToAdd;
        String lore = "&7Right-click to gain " + heartsToAdd + " heart" + (heartsToAdd > 1 ? "s" : "") + "!";
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .build();
    }
}
