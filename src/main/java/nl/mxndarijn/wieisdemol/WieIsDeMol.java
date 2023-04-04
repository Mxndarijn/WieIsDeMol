package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class WieIsDeMol extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        MxInventoryManager.createInstance(getServer(), this);
        getCommand("test").setExecutor(new TestCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
