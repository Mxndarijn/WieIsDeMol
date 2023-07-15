package nl.mxndarijn.world.chests;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.warps.Warp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChestManager {

    private File chestFile;
    private List<ChestInformation> chests;

    public ChestManager(File f) {
        this.chestFile = f;
        if(!this.chestFile.exists()) {
            try {
                this.chestFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not create chest file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        chests = loadFile();
    }

    private List<ChestInformation> loadFile() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(chestFile);
        ArrayList<ChestInformation> list = new ArrayList<>();

        fc.getKeys(false).forEach(key -> {
            Optional<ChestInformation> optionalChestInformation = ChestInformation.load(fc.getConfigurationSection(key));
            optionalChestInformation.ifPresent(list::add);
        });

        return list;
    }

    public void save() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(chestFile);
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        chests.forEach( w -> {
            w.save(fc);
        });
        try {
            fc.save(chestFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not save file. (" + chestFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void addChest(ChestInformation information) {
        chests.add(information);
        save();
    }

    public void removeChest(ChestInformation information) {
        chests.remove(information);
        save();
    }

    public boolean containsLocation(MxLocation location) {
        for (ChestInformation chest : chests) {
            if(chest.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public List<ChestInformation> getChests() {
        return chests;
    }


    public Optional<ChestInformation> getChestByLocation(MxLocation location) {
        for (ChestInformation chest : chests) {
            if(chest.getLocation().equals(location)) {
                return Optional.of(chest);
            }
        }
        return Optional.empty();
    }
}
