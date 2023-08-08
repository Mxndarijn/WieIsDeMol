package nl.mxndarijn.api.inventory.menu;

import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MxListInventoryBuilder extends MxMenuBuilder<MxListInventoryBuilder> {

    private ArrayList<Pair<ItemStack, MxItemClicked>> itemStackList;
    private ArrayList<Integer> availableItemsStackSlots;
    private Optional<ItemStack> nextPageItemStack;
    private Optional<ItemStack> previousPageItemStack;
    private int previousPageItemStackSlot;
    private int nextPageItemStackSlot;

    public MxListInventoryBuilder(String name, MxInventorySlots slotType) {
        super(name, slotType);

        previousPageItemStackSlot = slotType.slots - 9;
        nextPageItemStackSlot = slotType.slots - 1;
        previousItemStackSlot = slotType.slots - 5;

        itemStackList = new ArrayList<>();
        availableItemsStackSlots = new ArrayList<>();

        this.previousPageItemStack = Optional.of(
                MxSkullItemStackBuilder.create(1)
                        .setName(ChatColor.GRAY + "Vorige pagina")
                        .setSkinFromHeadsData("arrow-left")
                        .build());

        this.nextPageItemStack = Optional.of(
                MxSkullItemStackBuilder.create(1)
                        .setName(ChatColor.GRAY + "Volgende pagina")
                        .setSkinFromHeadsData("arrow-right")
                        .build());
    }

    public static MxListInventoryBuilder create(String name, MxInventorySlots slotType) {
        return new MxListInventoryBuilder(name, slotType);
    }

    public MxListInventoryBuilder addListItems(ArrayList<Pair<ItemStack, MxItemClicked>> list) {
        itemStackList.addAll(list);
        return this;
    }

    public MxListInventoryBuilder setListItems(ArrayList<Pair<ItemStack, MxItemClicked>> list) {
        itemStackList.clear();
        itemStackList.addAll(list);
        return this;
    }

    public MxListInventoryBuilder setAvailableSlots(MxInventoryIndex... indexes) {
        Arrays.stream(indexes).forEach(index -> {
            for (int i = index.getBeginIndex(); i <= index.getEndIndex(); i++) {
                if(!availableItemsStackSlots.contains(i)) {
                    availableItemsStackSlots.add(i);
                }
            }
        });
        return this;
    }

    public MxListInventoryBuilder setNextItemStack(ItemStack nextItemStack) {
        this.nextPageItemStack = Optional.of(nextItemStack);

        return this;
    }

    public MxListInventoryBuilder setPreviousItemStack(ItemStack previousItemStack) {
        this.previousPageItemStack = Optional.of(previousItemStack);

        return this;
    }

    public MxListInventoryBuilder setNextPageItemStackSlot(int nextItemStackSlot) {
        this.nextPageItemStackSlot = nextItemStackSlot;

        return this;
    }

    public MxListInventoryBuilder setPreviousPageItemStackSlot(int previousPageItemStackSlot) {
        this.previousPageItemStackSlot = previousPageItemStackSlot;

        return this;
    }

    @Override
    public MxInventory build() {
        if(availableItemsStackSlots.size() == 0) {
            Logger.logMessage(LogLevel.FATAL, Prefix.MXINVENTORY, "No available item slots...");
        }
        Collections.sort(availableItemsStackSlots);
        Iterator<Pair<ItemStack, MxItemClicked>> iterator = itemStackList.iterator();

        int amountOfInventories = (int) Math.ceil((double) itemStackList.size() / availableItemsStackSlots.size());
        if(amountOfInventories == 0) amountOfInventories = 1; // If no values preset, we want to show an inv;
        String nameWithoutSuffix = this.name;
        changeTitle(this.name + getSuffix(1, amountOfInventories));
        availableItemsStackSlots.forEach(i -> {
            if(iterator.hasNext()) {
                Pair<ItemStack, MxItemClicked> entry = iterator.next();
                setItem(entry.first, i, entry.second);
            }
        });

        List<MxDefaultInventoryBuilder> extraInventories = new ArrayList<>();
        List<MxInventory> inventories = new ArrayList<>();

        for(int i = 2; i <= amountOfInventories; i++) { // 2 because the main inv is 1
            MxDefaultInventoryBuilder builder = MxDefaultInventoryBuilder.create(nameWithoutSuffix + getSuffix(i, amountOfInventories), slotType);
            onClickedMap.forEach((index, clicked) -> {
                if(!availableItemsStackSlots.contains(index)) {
                    builder.setItem(inv.getItem(index), index, clicked);
                }
            });
            availableItemsStackSlots.forEach(itemIndex -> {
                if(iterator.hasNext()) {
                    Pair<ItemStack, MxItemClicked> entry = iterator.next();
                    builder.setItem(entry.first, itemIndex, entry.second);
                }
            });
            previousMenu.ifPresent(mxInventory -> builder.setItem(previousItem, previousItemStackSlot, (inv, e) -> {
                MxInventoryManager.getInstance().addAndOpenInventory(e.getWhoClicked().getUniqueId(), mxInventory);
            }));

            extraInventories.add(builder);
        }

        inventories.add(super.build());
        if(amountOfInventories > 1 && nextPageItemStack.isPresent() && previousPageItemStack.isPresent()) {
            MxItemClicked goNext = (inv, e) -> {
                int index = inventories.indexOf(inv);
                if(index + 1 < inventories.size()) {
                    MxInventoryManager.getInstance().addAndOpenInventory(e.getWhoClicked().getUniqueId(), inventories.get(index + 1));
                } else {
                    Logger.logMessage(LogLevel.ERROR, Prefix.MXINVENTORY, "Could not go next in inventory " + e.getView().getTitle());
                }
            };
            MxItemClicked goPrevious = (inv, e) -> {
                int index = inventories.indexOf(inv);
                if(index - 1 >= 0) {
                    MxInventoryManager.getInstance().addAndOpenInventory(e.getWhoClicked().getUniqueId(), inventories.get(index - 1));
                } else {
                    Logger.logMessage(LogLevel.ERROR, Prefix.MXINVENTORY, "Could not go previous in inventory " + e.getView().getTitle());
                }
            };
            setItem(nextPageItemStack.get(), nextPageItemStackSlot, goNext);
            extraInventories.forEach(inventoryBuilder -> {
                int index = extraInventories.indexOf(inventoryBuilder);
                inventoryBuilder.setItem(previousPageItemStack.get(), previousPageItemStackSlot, goPrevious);
                if(index + 1 < extraInventories.size()) {   // plus 1 because we want to check for the next inv.
                    inventoryBuilder.setItem(nextPageItemStack.get(), nextPageItemStackSlot, goNext);
                }

                inventories.add(inventoryBuilder.build());
            });
        }


        //TODO Add Items to inventory
        //TODO return main inventory
        return inventories.get(0);
    }

    private String getSuffix(int a, int b) {
        return ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + a + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + b + ChatColor.DARK_GRAY + ")";
    }
}
