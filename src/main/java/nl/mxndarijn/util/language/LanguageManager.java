package nl.mxndarijn.util.language;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class LanguageManager {
    private static LanguageManager instance;

    public static LanguageManager getInstance() {
        if(instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    private FileConfiguration languageConfig;
    private File languageFile;

    public LanguageManager() {
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        String path = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getString("language-file");
        languageFile = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + path);
        if(!languageFile.exists()) {
            Logger.logMessage(LogLevel.Information, Prefix.LANGUAGE_MANAGER, languageConfig.getString("language-name") + " has been created and loaded! (" + path + ")");
            Functions.copyFileFromResources(path.split("/")[0],path);
        }
        if(!languageFile.exists()) {
            Logger.logMessage(LogLevel.Fatal, Prefix.LANGUAGE_MANAGER, "Could not load language file... using default nl-NL.yml");
            languageFile = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + "nl-NL.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        Logger.logMessage(LogLevel.Information, Prefix.LANGUAGE_MANAGER, languageConfig.getString("language-name") + " has been loaded! (" + path + ")");
    }

    public String getLanguageString(LanguageText text, List<String> placeholders) {
        checkAvailability(text);
        String languageString = ChatColor.translateAlternateColorCodes('&', languageConfig.getString(text.getConfigValue()));
        for (int i = 0; i < placeholders.size(); i++) {
            String v = placeholders.get(i);
            languageString = languageString.replace("%%" + (i+1) + "%%", v);
        }
        return languageString;
    }

    public String getLanguageString(LanguageText text) {
        return getLanguageString(text, Collections.emptyList());
    }

    public String getLanguageString(LanguageText text, List<String> placeholders, ChatPrefix prefix) {

        return prefix + getLanguageString(text, placeholders);
    }

    public String getLanguageString(LanguageText text, ChatPrefix prefix) {

        return prefix + getLanguageString(text, Collections.emptyList());
    }

    private void checkAvailability(LanguageText text) {
        if(!languageConfig.contains(text.getConfigValue())) {
            JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(ConfigFiles.DEFAULT_LANGUAGE.getFileName())));
            if(fileConfiguration.contains(text.getConfigValue())) {
                String value = fileConfiguration.getString(text.getConfigValue());
                languageConfig.addDefault(text.getConfigValue(), value);
                try {
                    languageConfig.save(languageFile);
                } catch (IOException e) {
                    Logger.logMessage(LogLevel.Error, Prefix.LANGUAGE_MANAGER, "Could not save language file.");
                    e.printStackTrace();
                }
            } else {
                languageConfig.addDefault(text.getConfigValue(), "LANGUAGE_NOT_FOUND");
                Logger.logMessage(LogLevel.Error, Prefix.LANGUAGE_MANAGER, text.getConfigValue() + " has no default value, please add one.");
            }
        }
    }
}
