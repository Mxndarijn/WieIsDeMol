package nl.mxndarijn.inventory.item;

import nl.mxndarijn.data.ConfigFiles;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

public class MxHeadManager {
    private static MxHeadManager instance;
    private String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    public static MxHeadManager getInstance() {
        if(instance == null) {
            instance = new MxHeadManager();
        }
        return instance;
    }
    private FileConfiguration fileConfiguration;
    public MxHeadManager() {
        fileConfiguration = ConfigFiles.HEAD_DATA.getFileConfiguration();
    }

    public Optional<String> getTextureValue(String name) {
        String value = fileConfiguration.getString(name, null);
        if(value != null) {
            value = prefix + value;
        }
        return Optional.ofNullable(value);
    }

}
