package nl.mxndarijn.wieisdemol.managers.warps;

import lombok.Getter;
import lombok.Setter;
import nl.mxndarijn.api.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class Warp {

    private String name;
    private MxLocation mxLocation;
    private String skullId;

    private Warp(ConfigurationSection section) {
        name = section.getName();
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        optionalMxLocation.ifPresent(location -> mxLocation = location);
        skullId = section.getString("skull");
    }

    public Warp(String name, String skullId, MxLocation location) {
        this.name = name;
        this.mxLocation = location;
        this.skullId = skullId;
    }

    public static Optional<Warp> create(ConfigurationSection section) {
        Warp w = new Warp(section);
        if (w.mxLocation != null) {
            return Optional.of(w);
        }
        return Optional.empty();
    }

    public static List<Warp> getWarpsFromFile(File f, FileConfiguration fc) {
        ArrayList<Warp> warps = new ArrayList<>();
        fc.getKeys(false).forEach(key -> {
            ConfigurationSection section = fc.getConfigurationSection(key);
            Optional<Warp> opt = create(section);
            opt.ifPresent(warps::add);
        });

        return warps;

    }

    public void save(File file, FileConfiguration fc) {
        ConfigurationSection section = fc.createSection(name);
        section.set("skull", skullId);
        mxLocation.write(section.createSection("location"));
    }

}
