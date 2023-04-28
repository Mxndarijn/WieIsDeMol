package nl.mxndarijn.world.chests;

import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class ChestInformation {
    private String uuid;
    private String name;
    private MxLocation location;
    public ChestInformation(String name, MxLocation location) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
    }

    private ChestInformation() {

    }

    public static Optional<ChestInformation> load(ConfigurationSection section) {
        if(section == null) {
            return Optional.empty();
        }
        ChestInformation i = new ChestInformation();
        i.uuid = section.getName();
        i.name = section.getString("name");
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        optionalMxLocation.ifPresent(location -> i.location = location);

        if(i.location != null) {
            return Optional.of(i);
        }
        return Optional.empty();
    }

    public void save(FileConfiguration fc) {
        ConfigurationSection section = fc.createSection(uuid);
        section.set("name", name);
        location.write(section.createSection("location"));
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public MxLocation getLocation() {
        return location;
    }

}
