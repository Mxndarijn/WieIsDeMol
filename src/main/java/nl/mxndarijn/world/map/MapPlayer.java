package nl.mxndarijn.world.map;

import nl.mxndarijn.game.Colors;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class MapPlayer {
    private Colors color;
    private MxLocation location;

    public MapPlayer(Colors color, MxLocation location) {
        this.color = color;
        this.location = location;
    }

    public static Optional<MapPlayer> loadMapPlayerFromConfigurationSection(ConfigurationSection section) {
        Optional<Colors> color = Colors.getColorByType(section.getName());
        if(color.isEmpty()) {
            Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not load color");
            return Optional.empty();
        }
        ConfigurationSection locationSection = section.getConfigurationSection("location");

        if(locationSection == null) {
            Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not load spawnpoint for color: (Section null) " + color.get().getType() + " Path: " + section.getCurrentPath());
            return Optional.empty();
        }

        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(locationSection);

        if(optionalMxLocation.isEmpty()) {
            Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not load spawnpoint for color: " + color.get().getType());
            return Optional.empty();
        }

        MxLocation mxLocation = optionalMxLocation.get();
        return Optional.of(new MapPlayer(color.get(), mxLocation));
    }

    public void save(ConfigurationSection section) {
        ConfigurationSection colorSection = section.createSection(color.getType());

        ConfigurationSection locationSection = colorSection.createSection("location");
        location.write(locationSection);
    }
}
