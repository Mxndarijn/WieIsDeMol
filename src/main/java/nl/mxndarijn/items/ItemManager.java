package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class ItemManager {

    private static ItemManager instance;

    private ArrayList<MxItem> items;

    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    private ItemManager() {
        Logger.logMessage(LogLevel.Information, Prefix.ITEMMANAGER, "Loading Item-Manager...");

        items = new ArrayList<>();
        for (Items item : Items.values()) {
            try {
                Logger.logMessage(LogLevel.Debug, Prefix.ITEMMANAGER, "Loading usable item " + item.getClassObject().getName() + "...");
                MxItem mxItem = item.getClassObject().getDeclaredConstructor(
                        ItemStack.class,
                        MxWorldFilter.class,
                        boolean.class,
                        Action[].class
                ).newInstance(item.getItemStack(), item.getWorldFilter(), item.isGameItem(), item.getActions());
                items.add(mxItem);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                Logger.logMessage(LogLevel.Error, Prefix.ITEMMANAGER, "Could not load MxItem: " + item.getClassObject().getName());
                e.printStackTrace();
            }
        }

    }
}
