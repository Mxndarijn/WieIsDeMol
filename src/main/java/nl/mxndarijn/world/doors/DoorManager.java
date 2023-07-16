package nl.mxndarijn.world.doors;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoorManager {

    private File doorFile;
    private List<DoorInformation> doors;

    public DoorManager(File f) {
        this.doorFile = f;
        if(!this.doorFile.exists()) {
            try {
                this.doorFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not create door file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        doors = loadFile();
    }

    private List<DoorInformation> loadFile() {
        ArrayList<DoorInformation> list = new ArrayList<>();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(doorFile);
        fc.getKeys(false).forEach(key -> {
            Optional<DoorInformation> optionalChestInformation = DoorInformation.load(fc.getConfigurationSection(key));
            optionalChestInformation.ifPresent(list::add);
        });

        return list;
    }

    public void save() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(doorFile);
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        doors.forEach( w -> {
            w.save(fc);
        });
        try {
            fc.save(doorFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save file. (" + doorFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void addDoor(DoorInformation information) {
        doors.add(information);
        save();
    }

    public void removeDoor(DoorInformation information) {
        doors.remove(information);
        save();
    }


    public Optional<DoorInformation> getDoorById(String id) {
        for (DoorInformation door : doors) {
            if(door.getUuid().equalsIgnoreCase(id)) {
                return Optional.of(door);
            }
        }
        return Optional.empty();
    }

    public List<DoorInformation> getDoors() {
        return doors;
    }
}
