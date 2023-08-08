package nl.mxndarijn.api.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface MxOnInventoryCloseEvent {

    void onClose(Player p, MxInventory inv, InventoryCloseEvent e);
}
