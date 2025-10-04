package nl.mxndarijn.wieisdemol.items.util.storage;

import lombok.Getter;
import lombok.Setter;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.data.Permissions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StorageContainer {

    @Getter
    private final ArrayList<ItemStack> contents;
    @Getter
    private final String skull;
    @Getter
    private final String name;
    @Getter
    private final String owner;
    private final File file;
    @Setter
    @Getter
    private boolean isPublic;

    public StorageContainer(File file) {
        this.file = file;
        contents = new ArrayList<>();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        name = fc.getString(StorageContainerConfigValue.NAME.getConfigValue(), "Niet gevonden");
        skull = fc.getString(StorageContainerConfigValue.SKULL.getConfigValue(), "Niet gevonden");
        owner = fc.getString(StorageContainerConfigValue.OWNER.getConfigValue(), "Niet gevonden");
        isPublic = fc.getBoolean(StorageContainerConfigValue.IS_PUBLIC.getConfigValue(), false);
        if (fc.contains(StorageContainerConfigValue.ITEMS.getConfigValue())) {
            List<?> list = fc.getList(StorageContainerConfigValue.ITEMS.getConfigValue());
            if (list == null) {
                Logger.logMessage(LogLevel.ERROR, Prefix.STORAGE_MANAGER, "Could not load items in " + file.getAbsolutePath());
                return;
            }
            list.forEach(item -> {
                if (item instanceof ItemStack) {
                    contents.add((ItemStack) item);
                }
            });
        } else {
            Logger.logMessage(LogLevel.ERROR, Prefix.STORAGE_MANAGER, "No items to load in " + file.getAbsolutePath());
        }
    }

    public StorageContainer(String name, String skull, String owner, boolean isPublic, File file) {
        this.file = file;
        this.isPublic = isPublic;
        this.owner = owner;
        this.skull = skull;
        this.name = name;
        contents = new ArrayList<>();

        save();
    }

    public ItemStack getItemStack() {
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData(skull)
                .setName("<gray>" + name)
                .addBlankLore()
                .addLore("<yellow>Klik om deze opslag te openen")
                .build();
    }

    public boolean hasPermissionToEdit(Player p) {
        if (p.getUniqueId().toString().equalsIgnoreCase(owner)) {
            return true;
        }
        if (owner.equalsIgnoreCase("server")) {
            return p.hasPermission(Permissions.ITEM_ITEMS_EDIT_SERVER_CONTAINERS.getPermission());
        }
        return false;
    }

    public void delete() {
        if (!this.file.delete()) {
            Logger.logMessage(LogLevel.ERROR, Prefix.STORAGE_MANAGER, "Could not delete container: " + this.file.getAbsolutePath());
            return;
        }
        StorageManager.getInstance().removeContainer(this);
    }

    public void save() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.STORAGE_MANAGER, "Could not create container: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        fc.set(StorageContainerConfigValue.NAME.getConfigValue(), name);
        fc.set(StorageContainerConfigValue.SKULL.getConfigValue(), skull);
        fc.set(StorageContainerConfigValue.OWNER.getConfigValue(), owner);
        fc.set(StorageContainerConfigValue.IS_PUBLIC.getConfigValue(), isPublic);
        fc.set(StorageContainerConfigValue.ITEMS.getConfigValue(), contents);

        try {
            fc.save(file);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.STORAGE_MANAGER, "Could not save container: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
