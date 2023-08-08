package nl.mxndarijn.api.inventory.menu;

import nl.mxndarijn.api.inventory.MxInventoryBuilder;
import nl.mxndarijn.api.inventory.MxInventorySlots;

public class MxDefaultInventoryBuilder extends MxInventoryBuilder<MxDefaultInventoryBuilder> {
    protected MxDefaultInventoryBuilder(String name, MxInventorySlots slotType) {
        super(name, slotType);
    }

    public static MxDefaultInventoryBuilder create(String name, MxInventorySlots slotType) {
        return new MxDefaultInventoryBuilder(name, slotType);
    }
}
