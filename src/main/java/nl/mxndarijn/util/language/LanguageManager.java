package nl.mxndarijn.util.language;

import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LanguageManager {
    private static LanguageManager instance;

    public static LanguageManager getInstance() {
        if(instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    private FileConfiguration languageFile;

    public LanguageManager() {
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        String path = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getString("language-file");
        File file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + path);
        if(!file.exists()) {
            Logger.logMessage(LogLevel.Information, Prefix.LANGUAGE_MANAGER, languageFile.getString("language-name") + " has been created and loaded! (" + path + ")");
            Functions.copyFileFromResources(path.split("/")[0],path);
        }
        if(!file.exists()) {
            Logger.logMessage(LogLevel.Fatal, Prefix.LANGUAGE_MANAGER, "Could not load language file... using default nl-NL.yml");
            file = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + "nl-NL.yml");
        }
        languageFile = YamlConfiguration.loadConfiguration(file);
        Logger.logMessage(LogLevel.Information, Prefix.LANGUAGE_MANAGER, languageFile.getString("language-name") + " has been loaded! (" + path + ")");
    }

    public String getLanguageText
}
