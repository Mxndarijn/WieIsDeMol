package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Optional;

public abstract class ChestAttachment {
    public MxLocation mxLocation;
    public File file;
    public String path;


    public static boolean getDefaultValues(ChestAttachment attachment, File file, String path) {

        attachment.file = file;
        attachment.path = path;
        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(path);
        if(section == null) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load ChestAttachment (No Section) " + file.getAbsolutePath() + " Section: " + path);
            return false;
        }
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        if(optionalMxLocation.isEmpty()) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load ChestAttachment (No MxLocation) " + file.getAbsolutePath() + " Section: " + path);
            return false;
        }
        attachment.mxLocation = optionalMxLocation.get();
        return true;
    }

    public void onGameStart() {

    }
    public void onGamePause() {

    }
    public void onGameUpdate() {

    }

    public boolean canOpenChest() {
        return true;
    }

    public void onOpenChest() {

    };

}
