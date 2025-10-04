package nl.mxndarijn.wieisdemol.data;

import lombok.Getter;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Getter
public enum PeacekeeperLoot {
    HEAD(MxDefaultItemStackBuilder.create(Material.NETHERITE_HELMET)
            .setName("<gold>Peacekeeper's Helmet")
            .addLore("<gold>Peacekeeper-Item")
            .setUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION, 5, true)
            .addEnchantment(Enchantment.BINDING_CURSE, 1, true)
            .build(),
            EquipmentSlot.HEAD),
    CHESTPLATE(MxDefaultItemStackBuilder.create(Material.NETHERITE_CHESTPLATE)
            .setName("<gold>Peacekeeper's Chestplate")
            .addLore("<gold>Peacekeeper-Item")
            .setUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION, 5, true)
            .addEnchantment(Enchantment.BINDING_CURSE, 1, true)
            .build(),
            EquipmentSlot.CHEST),
    LEGGINGS(MxDefaultItemStackBuilder.create(Material.NETHERITE_LEGGINGS)
            .setName("<gold>Peacekeeper's Leggings")
            .addLore("<gold>Peacekeeper-Item")
            .setUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION, 5, true)
            .addEnchantment(Enchantment.BINDING_CURSE, 1, true)
            .build(),
            EquipmentSlot.LEGS),

    BOOTS(MxDefaultItemStackBuilder.create(Material.NETHERITE_BOOTS)
            .setName("<gold>Peacekeeper's Boots")
            .addLore("<gold>Peacekeeper-Item")
            .setUnbreakable(true)
            .addEnchantment(Enchantment.PROTECTION, 5, true)
            .addEnchantment(Enchantment.BINDING_CURSE, 1, true)
            .build(),
            EquipmentSlot.FEET),

    SWORD(MxDefaultItemStackBuilder.create(Material.NETHERITE_SWORD)
            .setName("<gold>Peacekeeper's Sword")
            .addLore("<gold>Peacekeeper-Item")
            .setUnbreakable(true)
            .addEnchantment(Enchantment.SHARPNESS, 5, true)
            .build(),
            null),
    ;


    private final ItemStack is;
    private final EquipmentSlot slot;

    PeacekeeperLoot(ItemStack is, EquipmentSlot slot) {
        this.is = is;
        this.slot = slot;
    }

}


