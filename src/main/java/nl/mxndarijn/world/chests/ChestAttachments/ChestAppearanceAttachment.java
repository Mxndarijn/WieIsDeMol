package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.data.ChestAppearance;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Optional;

public class ChestAppearanceAttachment extends ChestAttachment {
    private ChestAppearance appearance;

    public static Optional<ChestAppearanceAttachment> createFromSection(File file, String path) {
        ChestAppearanceAttachment attachment = new ChestAppearanceAttachment();
        if(!getDefaultValues(attachment, file, path)) {
            return Optional.empty();
        }

        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(path);
        assert(section != null);

        if(!section.contains("appearance")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load appearance. " + file.getAbsolutePath() + " Section: " + section);
            return Optional.empty();
        }
        if(!attachment.setAppearance(ChestAppearance.valueOf(section.getString("appearance")))) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not find appearance. " + file.getAbsolutePath() + " Section: " + section);
            return Optional.empty();
        }

        return Optional.of(attachment);
    }

    public boolean setAppearance(ChestAppearance appearance) {
        this.appearance = appearance;

        return appearance != null;
    }
}
