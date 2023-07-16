package nl.mxndarijn.data;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public enum ConfigFiles {
    MAIN_CONFIG("config.yml", "config.yml"),
    HEAD_DATA("head-data.yml", "head-data.yml"),
    DEFAULT_LANGUAGE("nl-NL.yml", "languages/nl-NL.yml");

    private final FileConfiguration fileConfiguration;
    private final File file;
    private final String fileName;

    private final String path;
    ConfigFiles(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        file = new File(plugin.getDataFolder() + File.separator + path);
        if(!file.exists()) {
            Logger.logMessage(LogLevel.INFORMATION, Prefix.CONFIG_FILES, "Could not load: " + file.getName() + ". Trying to load it from internal sources...");
            Functions.copyFileFromResources(fileName, path);
        }
        fileConfiguration =  YamlConfiguration.loadConfiguration(file);
    }


    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void save() {
        try {
            Logger.logMessage(LogLevel.DEBUG, Prefix.CONFIG_FILES, "Saving file... " + path);
            fileConfiguration.save(file);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save file... " + path);
        }
    }

    public static void saveAll() {
        Logger.logMessage(LogLevel.INFORMATION, Prefix.CONFIG_FILES, "Saving all files... ");
        for(ConfigFiles value : values()) {
            value.save();
        }
    }
}
