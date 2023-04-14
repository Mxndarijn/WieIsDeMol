package nl.mxndarijn.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface MxItemClicked {

    public void OnItemClicked(MxInventory inv, InventoryClickEvent e);

}
