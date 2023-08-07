package nl.mxndarijn.managers.changeworld;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.saver.InventoryManager;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class SaveInventoryChangeWorld  implements MxChangeWorld {

    private ArrayList<Pair<ItemStack, String>> defaultItems;
    private File inventoryFile;
    private WorldReachedZeroPlayersEvent event;
    public SaveInventoryChangeWorld(File inventoryFile, ArrayList<Pair<ItemStack, String>> items, WorldReachedZeroPlayersEvent event) {
        defaultItems = items;
        this.inventoryFile = inventoryFile;
        this.event = event;
    }

    @Override
    public void enter(Player p, World w, PlayerChangedWorldEvent e) {
        p.getInventory().clear();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(inventoryFile);
        InventoryManager.loadInventoryForPlayer(fc, p.getUniqueId().toString(), p);
        defaultItems.forEach(itemPair -> {
            if(!InventoryManager.containsItem(p.getInventory(), itemPair.first)) {
                p.getInventory().addItem(itemPair.first);
            }
            p.sendMessage(itemPair.second);
        });
    }

    @Override
    public void leave(Player p, World w, PlayerChangedWorldEvent e) {
        UUID uuid = p.getUniqueId();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(inventoryFile);
        InventoryManager.saveInventory(inventoryFile, fc, uuid.toString(), p.getInventory());
        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INVENTORY_SAVED));
        p.getInventory().clear();
        if (w.getPlayers().size() == 0) {
            event.worldReachedZeroPlayers(p, w, e);
        }
    }
}
