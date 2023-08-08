package nl.mxndarijn.wieisdemol.managers.chests.ChestAttachments;

import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.managers.chests.ChestInformation;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class ChestAttachment {
    public ChestInformation information;
    public String type;


        public static boolean getDefaultValues(ChestAttachment attachment, ChestInformation information, Map<String, Object> section) {

        if(section == null) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load ChestAttachment (No Section)");
            return false;
        }
        if(!section.containsKey("type")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load ChestAttachment (No Type)");
            return false;
        }
        attachment.type = (String) section.get("type");
        attachment.information = information;
        return true;
    }
    public void onGameStart() {

    }

    public void setDefaults(String type, ChestInformation information) {
        this.type = type;
        this.information = information;
    }
    public void onGamePause() {

    }
    public void onGameUpdate() {

    }


        public abstract Pair<ItemStack, MxItemClicked> getEditAttachmentItem();

    public boolean canOpenChest() {
        return true;
    }

    public void onOpenChest(InventoryOpenEvent e) {

    }

    public void getDataDefaults(Map<String, Object> map) {
        map.put("type", type);
    }

    public abstract Map<String, Object> getDataForSaving();
}
