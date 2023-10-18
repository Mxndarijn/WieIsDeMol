package nl.mxndarijn.wieisdemol.data;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public interface ItemTagContainer {

    ItemStack getItem(String data);
}
