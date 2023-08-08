package nl.mxndarijn.api.inventory.menu;

import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MxMenuBuilder<T extends MxInventoryBuilder<T>> extends MxInventoryBuilder<T> {

    protected Optional<MxInventory> previousMenu;
    protected ItemStack previousItem;
    protected int previousItemStackSlot;

    public MxMenuBuilder(String name, MxInventorySlots slotType) {
        super(name, slotType);
        previousMenu = Optional.empty();
        previousItem = MxDefaultItemStackBuilder.create(Material.BARRIER, 1)
                .setName(ChatColor.GRAY + "Terug")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS)
                .build();

        this.previousItemStackSlot = slotType.slots - 9;
    }

    public T setPrevious(MxInventory menu) {
        this.previousMenu = Optional.of(menu);
        return (T) this;
    }

    public T setPreviousItemStack(ItemStack previousItem) {
        this.previousItem = previousItem;
        return (T) this;
    }

    public T setPreviousItemStackSlot(int slot) {
        this.previousItemStackSlot = slot;
        return (T) this;
    }

    @Override
    public MxInventory build() {
        previousMenu.ifPresent(mxInventory -> setItem(previousItem, previousItemStackSlot, (inv, e) -> {
            MxInventoryManager.getInstance().addAndOpenInventory((Player) e.getWhoClicked(), mxInventory);
        }));
        return super.build();
    }
}
