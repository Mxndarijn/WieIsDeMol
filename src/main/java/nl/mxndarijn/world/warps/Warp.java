package nl.mxndarijn.world.warps;

import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Optional;

public class Warp {

    private String name;
    private MxLocation mxLocation;

    private Warp(File file, FileConfiguration fc, ConfigurationSection section) {
        name = section.getName();
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        if(optionalMxLocation.isPresent()) {
            mxLocation = optionalMxLocation.get();
        }
    }

    public static Optional<Warp> create(File file, FileConfiguration fc, ConfigurationSection section) {
        Warp w = new Warp(file, fc, section);
        if(w.mxLocation != null) {
            return Optional.of(w);
        }
        return Optional.empty();
    }

}
