package nl.mxndarijn.api.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface MxItemClicked {

    void OnItemClicked(MxInventory mxInv, InventoryClickEvent e);

}
