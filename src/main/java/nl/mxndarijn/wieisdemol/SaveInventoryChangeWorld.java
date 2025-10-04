package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.api.changeworld.MxChangeWorld;
import nl.mxndarijn.api.changeworld.WorldReachedZeroPlayersEvent;
import nl.mxndarijn.api.inventory.saver.InventoryManager;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class SaveInventoryChangeWorld implements MxChangeWorld {

    private final ArrayList<Pair<ItemStack, String>> defaultItems;
    private final File inventoryFile;
    private final WorldReachedZeroPlayersEvent event;

    public SaveInventoryChangeWorld(File inventoryFile, ArrayList<Pair<ItemStack, String>> items, WorldReachedZeroPlayersEvent event) {
        defaultItems = items;
        this.inventoryFile = inventoryFile;
        this.event = event;
    }

    @Override
    public void enter(Player p, World w, PlayerChangedWorldEvent e) {
        Logger.logMessage("1");
        p.getInventory().clear();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(inventoryFile);
        Logger.logMessage("2");
        InventoryManager.loadInventoryForPlayer(fc, p.getUniqueId().toString(), p);
        Logger.logMessage("3");
        defaultItems.forEach(itemPair -> {
            Logger.logMessage("4");
            if (!InventoryManager.containsItem(p.getInventory(), itemPair.first)) {
                p.getInventory().addItem(itemPair.first);
            }
            MSG.msg(p, itemPair.second);
        });
        Logger.logMessage("5");
    }

    @Override
    public void leave(Player p, World w, PlayerChangedWorldEvent e) {
        UUID uuid = p.getUniqueId();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(inventoryFile);
        InventoryManager.saveInventory(inventoryFile, fc, uuid.toString(), p.getInventory());
        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INVENTORY_SAVED));
        p.getInventory().clear();
        if (w.getPlayers().isEmpty()) {
            event.worldReachedZeroPlayers(p, w, e);
        }
    }

    @Override
    public void quit(Player p, World w, PlayerQuitEvent e) {
        // do nothing
    }
}

