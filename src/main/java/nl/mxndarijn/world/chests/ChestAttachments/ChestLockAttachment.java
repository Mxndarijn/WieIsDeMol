package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Optional;

public class ChestLockAttachment extends ChestAttachment {
    private String lockTag;
    private boolean locked = true;

    public static Optional<ChestLockAttachment> createFromSection(File file, String path) {
        ChestLockAttachment attachment = new ChestLockAttachment();
        if(!getDefaultValues(attachment, file, path)) {
            return Optional.empty();
        }

        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(path);
        assert(section != null);

        if(!section.contains("lockTag")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load lockTag. " + file.getAbsolutePath() + " Section: " + section);
            return Optional.empty();
        }

        attachment.setLockTag(section.getString("lockTag"));

        return Optional.of(attachment);
    }

    public void setLockTag(String lockTag) {
        this.lockTag = lockTag;
    }
}
