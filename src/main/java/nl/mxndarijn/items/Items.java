package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.items.maps.ChestItem;
import nl.mxndarijn.items.presets.ChestConfigureTool;
import nl.mxndarijn.items.presets.DoorConfigureTool;
import nl.mxndarijn.items.presets.PresetConfigureTool;
import nl.mxndarijn.items.presets.ShulkerConfigureTool;
import nl.mxndarijn.items.util.MxItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public enum Items {

    // Preset Items
    PRESET_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                .setName(ChatColor.GRAY + "Preset Configure-Tool")
                .addLore(" ")
                .addLore(ChatColor.YELLOW + "Met dit item kan je instellingen in een preset aanpassen.")
                .build(),
            p -> {
                return true;
            },
            false,
            PresetConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),

    CHEST_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.STICK, 1)
                    .setName(ChatColor.GRAY + "Chest Configure-Tool")
                    .addLore(" ")
                    .addLore(ChatColor.YELLOW + "Met dit item kan je kisten in een preset aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            ChestConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    SHULKER_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.BLAZE_ROD, 1)
                    .setName(ChatColor.GRAY + "Shulker Configure-Tool")
                    .addLore(" ")
                    .addLore(ChatColor.YELLOW + "Met dit item kan je shulkers in een preset aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            ShulkerConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    DOOR_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.BRICK, 1)
                    .setName(ChatColor.GRAY + "Door Configure-Tool")
                    .addLore(" ")
                    .addLore(ChatColor.YELLOW + "Met dit item kan je deuren in een preset aanpassen.")
                    .addLore(ChatColor.YELLOW + "Gebruik in de lucht om deuren te maken.")
                    .addLore(ChatColor.YELLOW + "Gebruik tegen een block om het block toe te voegen aan een deur.")
                    .build(),
            p -> {
                return true;
            },
            false,
            DoorConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),

    // Map items

    CHEST_TOOL(
            MxDefaultItemStackBuilder.create(Material.STICK, 1)
                    .setName(ChatColor.GRAY + "Chest Tool")
                    .addLore(" ")
                    .addLore(ChatColor.YELLOW + "Met dit item kan je kisten op afstand openen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            ChestItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    ;



    private final ItemStack itemStack;
    private final MxWorldFilter worldFilter;
    private final boolean gameItem;
    private final Class<? extends MxItem> classObject;
    private final Action[] actions;
    Items(ItemStack is, MxWorldFilter mxWorldFilter, boolean gameItem, Class<? extends MxItem> classObject, Action... actions) {
        this.itemStack = is;
        this.worldFilter = mxWorldFilter;
        this.gameItem = gameItem;
        this.classObject = classObject;
        this.actions = actions;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public MxWorldFilter getWorldFilter() {
        return worldFilter;
    }

    public boolean isGameItem() {
        return gameItem;
    }

    public Class<? extends MxItem> getClassObject() {
        return classObject;
    }

    public Action[] getActions() {
        return actions;
    }
}
