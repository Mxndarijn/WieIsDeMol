package nl.mxndarijn.wieisdemol.data;

import lombok.Getter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum SpecialDirectories {
    PRESET_WORLDS("presets"),
    MAP_WORLDS("maps"),
    GAMES_WORLDS("games"),
    LANGUAGE_FILES("languages"),
    STORAGE_FILES("storages");

    private static final List<SpecialDirectories> values = Collections.unmodifiableList(Arrays.asList(values()));
    private final File directory;
    private final String folderName;

    SpecialDirectories(String folderName) {
        this.folderName = folderName;
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        directory = new File(plugin.getDataFolder() + getPath());
        if (!directory.exists()) {
            boolean success = directory.mkdirs();

        }

    }

    private String getPath() {
        return folderName.startsWith("/") ? folderName : "/" + folderName;
    }

}
