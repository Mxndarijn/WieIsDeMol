package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.data.Colors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ColorBindAttachment extends ChestAttachment {
    private List<Colors> colors;

    public static Optional<ColorBindAttachment> createFromSection(File file, String path) {
        ColorBindAttachment attachment = new ColorBindAttachment();
        if(!getDefaultValues(attachment, file, path)) {
            return Optional.empty();
        }

        ConfigurationSection section = YamlConfiguration.loadConfiguration(file).getConfigurationSection(path);
        assert(section != null);

        List<String> colorsString = section.getStringList("colors");
        List<Colors> colorsList = colorsString.stream()
                .map(Colors::getColorByType)
                .flatMap(Optional::stream)
                .toList();
        attachment.setColors(colorsList);

        return Optional.of(attachment);
    }

    public void setColors(List<Colors> colors) {
        this.colors = colors;
    }
}
