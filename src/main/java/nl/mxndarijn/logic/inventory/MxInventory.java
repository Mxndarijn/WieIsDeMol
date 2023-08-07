package nl.mxndarijn.logic.inventory;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class MxInventory {
    private final Inventory inv;
    private final HashMap<Integer, MxItemClicked> onClickedMap;
    private final String name;
    private final boolean delete;
    private final boolean cancelEvent;
    private final boolean canBeClosed;

    private final MxOnInventoryCloseEvent closeEvent;

    public MxInventory(Inventory inv, String invName, HashMap<Integer, MxItemClicked> onClickedMap, boolean delete, boolean cancelEvent, boolean closed, MxOnInventoryCloseEvent closeEvent) {
        this.inv = inv;
        this.onClickedMap = onClickedMap;
        this.name = invName;
        this.delete = delete;
        this.cancelEvent = cancelEvent;
        this.canBeClosed = closed;
        this.closeEvent = closeEvent;
    }

    public Inventory getInv() {
        return inv;
    }

    public HashMap<Integer, MxItemClicked> getOnClickedMap() {
        return onClickedMap;
    }

    public String getName() {
        return name;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isCancelEvent() {
        return cancelEvent;
    }

    public boolean isCanBeClosed() {
        return canBeClosed;
    }

    public MxOnInventoryCloseEvent getCloseEvent() {
        return closeEvent;
    }
}
