package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.data.Colors;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class LimitedChoiceAttachment extends ChestAttachment {
    private int choices;

    public static Optional<LimitedChoiceAttachment> createFromSection(File file, String path) {
        LimitedChoiceAttachment attachment = new LimitedChoiceAttachment();
        if(!getDefaultValues(attachment, file, path)) {
            return Optional.empty();
        }

        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(path);
        assert(section != null);

        if(!section.contains("choices")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load choices amount. " + file.getAbsolutePath() + " Section: " + section);
            return Optional.empty();
        }
        attachment.setChoices(section.getInt("choices"));

        return Optional.of(attachment);
    }

    public void setChoices(int choices) {
        this.choices = choices;
    }
}
