package nl.mxndarijn.wieisdemol.managers.shulkers;

import lombok.Getter;
import lombok.Setter;
import nl.mxndarijn.api.mxworld.MxLocation;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;
import java.util.UUID;

@Getter
public class ShulkerInformation {
    private String uuid;
    private String name;
    private MxLocation location;
    private Material material;
    @Setter
    private boolean isStartingRoom;

    public ShulkerInformation(String name, MxLocation location, Material material, boolean isStartingRoom) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.material = material;
        this.isStartingRoom = isStartingRoom;
    }

    private ShulkerInformation() {

    }

    public static Optional<ShulkerInformation> load(ConfigurationSection section) {
        if (section == null) {
            return Optional.empty();
        }
        ShulkerInformation i = new ShulkerInformation();
        i.uuid = section.getName();
        i.name = section.getString("name");
        i.material = Material.matchMaterial(section.getString("material"));
        i.isStartingRoom = section.getBoolean("isStartingRoom", false);
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        optionalMxLocation.ifPresent(location -> i.location = location);

        if (i.location != null) {
            return Optional.of(i);
        }
        return Optional.empty();
    }

    public void save(FileConfiguration fc) {
        ConfigurationSection section = fc.createSection(uuid);
        section.set("name", name);
        section.set("material", material.toString());
        section.set("isStartingRoom", isStartingRoom);
        location.write(section.createSection("location"));
    }

}
