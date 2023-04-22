package nl.mxndarijn.data;

import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.wieisdemol.Functions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SpecialItems {
    PRESET_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                .setName(ChatColor.GRAY + "Preset Configure-Tool")
                .addLore(" ")
                .addLore(ChatColor.YELLOW + "Met item kan je instellingen in een preset aanpassen.")
                .build()
    );



    private final ItemStack itemStack;
    SpecialItems(ItemStack is) {
        Logger.logMessage(LogLevel.DebugHighlight, Functions.convertComponentToString(is.getItemMeta().displayName()));
        this.itemStack = is;

    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }
}
