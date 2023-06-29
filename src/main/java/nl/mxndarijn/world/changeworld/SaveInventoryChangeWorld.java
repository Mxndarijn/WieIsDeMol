package nl.mxndarijn.world.changeworld;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.saver.InventoryManager;
import nl.mxndarijn.items.Items;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetsManager;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
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
        /*p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INFO_CONFIGURE_TOOL));
        if(!InventoryManager.containsItem(p.getInventory(), Items.PRESET_CONFIGURE_TOOL.getItemStack())) {
            p.getInventory().addItem(Items.PRESET_CONFIGURE_TOOL.getItemStack());
        }

        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_INFO));
        if(!InventoryManager.containsItem(p.getInventory(), Items.CHEST_CONFIGURE_TOOL.getItemStack())) {
            p.getInventory().addItem(Items.CHEST_CONFIGURE_TOOL.getItemStack());
        }

        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_INFO));
        if(!InventoryManager.containsItem(p.getInventory(), Items.SHULKER_CONFIGURE_TOOL.getItemStack())) {
            p.getInventory().addItem(Items.SHULKER_CONFIGURE_TOOL.getItemStack());
        }

        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_INFO));
        if(!InventoryManager.containsItem(p.getInventory(), Items.DOOR_CONFIGURE_TOOL.getItemStack())) {
            p.getInventory().addItem(Items.DOOR_CONFIGURE_TOOL.getItemStack());
        }*/
    }

    @Override
    public void leave(Player p, World w, PlayerChangedWorldEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(w.getUID());
        if(optionalPreset.isPresent()) {
            Preset preset = optionalPreset.get();
            UUID uuid = p.getUniqueId();
            FileConfiguration fc = YamlConfiguration.loadConfiguration(preset.getInventoriesFile());
            InventoryManager.saveInventory(preset.getInventoriesFile(), fc, uuid.toString(), p.getInventory());
            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INVENTORY_SAVED));
            p.getInventory().clear();
            if (w.getPlayers().size() == 0) {
                event.worldReachedZeroPlayers(p, w, e);
                /*Logger.logMessage(LogLevel.Information, Prefix.PRESETS_MANAGER, "Unloading world... (" + preset.getDirectory().getAbsolutePath() + ")");
                preset.unloadWorld();*/
            }
        }
    }
}

