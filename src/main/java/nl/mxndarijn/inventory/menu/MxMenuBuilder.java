package nl.mxndarijn.inventory.menu;

import nl.mxndarijn.inventory.MxInventory;
import nl.mxndarijn.inventory.MxInventoryBuilder;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.MxItemClicked;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MxMenuBuilder extends MxInventoryBuilder {

    protected Optional<MxInventory> previousMenu;
    protected ItemStack previousItem;
    protected int previousItemStackSlot;

    public MxMenuBuilder(String name, MxInventorySlots slotType) {
        super(name, slotType);
        previousMenu = Optional.empty();
    }

    public MxMenuBuilder setPrevious(MxInventory menu) {
        this.previousMenu = Optional.of(menu);
        return this;
    }

    public MxMenuBuilder setPreviousItemStack(ItemStack previousItem) {
        this.previousItem = previousItem;
        return this;
    }

    public MxMenuBuilder setPreviousItemStackSlot(int slot) {
        this.previousItemStackSlot = slot;
        return this;
    }

    @Override
    public MxInventory build() {
        previousMenu.ifPresent(mxInventory -> setItem(previousItem, previousItemStackSlot, (inv, e) -> {
            e.getWhoClicked().openInventory(mxInventory.getInv());
        }));
        return super.build();
    }
}
