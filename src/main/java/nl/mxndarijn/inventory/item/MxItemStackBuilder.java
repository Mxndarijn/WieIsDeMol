package nl.mxndarijn.inventory.item;

import net.kyori.adventure.text.TextComponent;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MxItemStackBuilder<T extends MxItemStackBuilder<T>> {
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

    public T setName(String name) {
        itemMeta.setDisplayName(name);
        return (T) this;
    }

    public T addLore(String lore) {
        lores.add(lore);
        return (T) this;
    }

    public T addItemFlag(ItemFlag... flag) {
        itemMeta.addItemFlags(flag);
        return (T) this;
    }

    public T addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestrictions) {
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestrictions);
        return (T) this;
    }

    public T setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return (T) this;
    }

    public T addCustomTagString(String tagName, String value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), tagName), PersistentDataType.STRING, value);

        return (T) this;
    }

    public ItemStack build() {
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
