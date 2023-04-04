package nl.mxndarijn.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class MxInventoryBuilder {
    protected Inventory inv;
    protected HashMap<Integer, MxItemClicked> onClickedMap;
    protected boolean delete = true;
    protected boolean cancelEvent = true;
    protected boolean canBeClosed = true;
    protected MxInventoryBuilder(String name, MxInventorySlots slotType) {
        inv = Bukkit.createInventory(null, slotType.slots, name);
        onClickedMap = new HashMap<>();
    }

    public static MxInventoryBuilder create(String name, MxInventorySlots slotType) {
        return new MxInventoryBuilder(name, slotType);
    }

    public MxInventoryBuilder addItem(ItemStack is, MxItemClicked onClicked) {
        inv.addItem(is);
        onClickedMap.put(inv.first(is), onClicked);
        return this;
    }

    public MxInventoryBuilder setItem(ItemStack is, int slot, MxItemClicked onClicked) {
        inv.setItem(slot, is);
        onClickedMap.put(slot, onClicked);
        return this;
    }

    public MxInventoryBuilder deleteInventoryWhenClosed(boolean delete) {
        this.delete = delete;
        return this;
    }

    public MxInventoryBuilder canBeClosed(boolean closed) {
        this.canBeClosed = closed;
        return this;
    }


    public MxInventory build() {
        return new MxInventory(inv, onClickedMap, delete, cancelEvent, canBeClosed);
    }

    public MxInventoryBuilder defaultCancelEvent(boolean b) {
        cancelEvent = true;
        return this;
    }
}
