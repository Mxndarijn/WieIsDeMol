package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.wieisdemol.Functions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public enum Items {
    PRESET_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                .setName(ChatColor.GRAY + "Preset Configure-Tool")
                .addLore(" ")
                .addLore(ChatColor.YELLOW + "Met item kan je instellingen in een preset aanpassen.")
                .build(),
            p -> {
                return true;
            },
            false,
            PresetConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    );



    private final ItemStack itemStack;
    private final MxWorldFilter worldFilter;
    private final boolean gameItem;
    private final Class<? extends MxItem> classObject;
    private final Action[] actions;
    Items(ItemStack is, MxWorldFilter mxWorldFilter, boolean gameItem, Class<? extends MxItem> classObject, Action... actions) {
        Logger.logMessage(LogLevel.DebugHighlight, Functions.convertComponentToString(is.getItemMeta().displayName()));
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
