package nl.mxndarijn.inventory;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MxItemStackBuilder {
    ItemStack itemStack;
    ItemMeta itemMeta;
    List<String> lores;
    MxItemStackBuilder(Material mat) {
        this(mat, 1);
    }

    MxItemStackBuilder(Material mat, int amount) {
        this(mat, 1, 0);
    }

    MxItemStackBuilder(Material mat, int amount, int byteAmount) {
        itemStack = new ItemStack(mat, amount, (byte) byteAmount);
        itemMeta = itemStack.getItemMeta();
        lores = new ArrayList<>();
    }

    public static MxItemStackBuilder create(Material mat) {
        return new MxItemStackBuilder(mat);
    }

    public static MxItemStackBuilder create(Material mat, int amount) {
        return new MxItemStackBuilder(mat, amount);
    }

    public static MxItemStackBuilder create(Material mat, int amount, int byteAmount) {
        return new MxItemStackBuilder(mat, amount, byteAmount);
    }

    public MxItemStackBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public MxItemStackBuilder addLore(String lore) {
        lores.add(lore);
        return this;
    }

    public MxItemStackBuilder addItemFlag(ItemFlag... flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    public MxItemStackBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestrictions) {
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestrictions);
        return this;
    }

    public MxItemStackBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build() {
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
