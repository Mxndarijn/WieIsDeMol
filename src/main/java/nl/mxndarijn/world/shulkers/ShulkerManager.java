package nl.mxndarijn.world.shulkers;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShulkerManager {

    private File shulkerFile;
    private FileConfiguration fc;
    private List<ShulkerInformation> shulkers;

    public ShulkerManager(File f) {
        this.shulkerFile = f;
        if(!this.shulkerFile.exists()) {
            try {
                this.shulkerFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not create shulker file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        fc = YamlConfiguration.loadConfiguration(f);
        shulkers = loadFile();
    }

    private List<ShulkerInformation> loadFile() {
        ArrayList<ShulkerInformation> list = new ArrayList<>();

        fc.getKeys(false).forEach(key -> {
            Optional<ShulkerInformation> optionalShulkerInformation = ShulkerInformation.load(fc.getConfigurationSection(key));
            optionalShulkerInformation.ifPresent(list::add);
        });

        return list;
    }

    public void save() {
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        shulkers.forEach(w -> {
            w.save(fc);
        });
        try {
            fc.save(shulkerFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not save file. (" + shulkerFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void addShulker(ShulkerInformation information) {
        shulkers.add(information);
        save();
    }

    public void removeShulker(ShulkerInformation information) {
        shulkers.remove(information);
        save();
    }

    public boolean containsLocation(MxLocation location) {
        for (ShulkerInformation shulker : shulkers) {
            if(shulker.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }


    public Optional<ShulkerInformation> getShulkerByLocation(MxLocation location) {
        for (ShulkerInformation shulker : shulkers) {
            if(shulker.getLocation().equals(location)) {
                return Optional.of(shulker);
            }
        }
        return Optional.empty();
    }

    public List<ShulkerInformation> getShulkers() {
        return shulkers;
    }
}
