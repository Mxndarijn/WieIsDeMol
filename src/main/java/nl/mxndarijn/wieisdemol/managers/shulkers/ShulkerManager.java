package nl.mxndarijn.wieisdemol.managers.shulkers;

import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.mxworld.MxLocation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShulkerManager {

    private File shulkerFile;
    private List<ShulkerInformation> shulkers;

    public ShulkerManager(File f) {
        this.shulkerFile = f;
        if(!this.shulkerFile.exists()) {
            try {
                this.shulkerFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not create shulker file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        shulkers = loadFile();
    }

    private List<ShulkerInformation> loadFile() {
        ArrayList<ShulkerInformation> list = new ArrayList<>();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(shulkerFile);
        fc.getKeys(false).forEach(key -> {
            Optional<ShulkerInformation> optionalShulkerInformation = ShulkerInformation.load(fc.getConfigurationSection(key));
            optionalShulkerInformation.ifPresent(list::add);
        });

        return list;
    }

    public void save() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(shulkerFile);
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        shulkers.forEach(w -> {
            w.save(fc);
        });
        try {
            fc.save(shulkerFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save file. (" + shulkerFile.getAbsolutePath() + ")");
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
