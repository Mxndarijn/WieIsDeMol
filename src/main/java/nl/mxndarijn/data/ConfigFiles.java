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
import java.nio.file.Files;

public enum ConfigFiles {
    MAIN_CONFIG("config", "config"),
    DEFAULT_LANGUAGE("nl-NL", "languages/nl-NL");

    private final FileConfiguration fileConfiguration;
    private final File file;
    private final String fileName;
    ConfigFiles(String fileName, String path) {
        if(!fileName.endsWith(".yml")) {
            fileName = fileName + ".yml";
        }
        if(!path.endsWith(".yml")) {
            path = path + ".yml";
        }
        this.fileName = fileName;
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        file = new File(plugin.getDataFolder() + File.separator + path);
        if(!file.exists()) {
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
}
