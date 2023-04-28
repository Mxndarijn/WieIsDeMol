package nl.mxndarijn.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class MxInventoryBuilder<T extends MxInventoryBuilder<T>> {
    protected Inventory inv;
    protected HashMap<Integer, MxItemClicked> onClickedMap;
    protected MxInventorySlots slotType;
    protected String name;
    protected boolean delete = true;
    protected boolean cancelEvent = true;
    protected boolean canBeClosed = true;
    protected MxInventoryBuilder(String name, MxInventorySlots slotType) {
        this.slotType = slotType;
        this.name = getRandomPrefix() + name;
        inv = Bukkit.createInventory(null, slotType.slots, this.name);
        onClickedMap = new HashMap<>();
    }

    /*public static MxInventoryBuilder create(String name, MxInventorySlots slotType) {
        return new MxInventoryBuilder(name, slotType);
    }*/

    public T addItem(ItemStack is, MxItemClicked onClicked) {
        int index = inv.firstEmpty();
        if(index == -1) {
            return (T) this;
        }
        inv.setItem(index, is);
        onClickedMap.put(index, onClicked);
        return (T) this;
    }

    public T setItem(ItemStack is, int slot, MxItemClicked onClicked) {
        inv.setItem(slot, is);
        onClickedMap.put(slot, onClicked);
        return (T) this;
    }

    public T deleteInventoryWhenClosed(boolean delete) {
        this.delete = delete;
        return (T) this;
    }

    public T canBeClosed(boolean closed) {
        this.canBeClosed = closed;
        return (T) this;
    }


    public MxInventory build() {
        return new MxInventory(inv, name, onClickedMap, delete, cancelEvent, canBeClosed);
    }

    public T defaultCancelEvent(boolean b) {
        cancelEvent = b;
        return (T) this;
    }

    public T changeTitle(String newTitle) {
        this.name = getRandomPrefix() + newTitle;
        Inventory inventory = Bukkit.createInventory(null, slotType.slots, this.name);
        onClickedMap.forEach((index, clicked) -> {
            inventory.setItem(index, inv.getItem(index));
        });
        this.inv = inventory;
        return (T) this;
    }


    private String getRandomPrefix() {
        StringBuilder prefix = new StringBuilder();
        ChatColor[] list = ChatColor.values();
        Random random = new Random();
        for(int i = 0; i < 5; i++) {
            ChatColor r = list[random.nextInt(list.length)];
            prefix.append(r);
        }
        prefix.append(ChatColor.RESET);
        return prefix.toString();
    }

    private int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
