package nl.mxndarijn.wieisdemol.managers.language;

import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.ConfigFiles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LanguageManager {
    private static LanguageManager instance;
    private FileConfiguration languageConfig;
    private File languageFile;
    public LanguageManager() {
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        String path = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getString("language-file");
        languageFile = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + path);
        if (!languageFile.exists()) {
            // Create from internal resources if available
            Functions.copyFileFromResources(path.split("/")[0], path);
        }
        if (!languageFile.exists()) {
            Logger.logMessage(LogLevel.FATAL, Prefix.LANGUAGE_MANAGER, "Could not load language file '" + path + "'... using default nl-NL.yml");
            languageFile = new File(plugin.getDataFolder() + File.separator + "languages" + File.separator + "nl-NL.yml");
            if (!languageFile.exists()) {
                // Ensure default exists too
                Functions.copyFileFromResources(ConfigFiles.DEFAULT_LANGUAGE.getFileName(), ConfigFiles.DEFAULT_LANGUAGE.getFileName());
            }
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        // Sync all defaults from the selected language resource and base DEFAULT_LANGUAGE
        try {
            int before = languageConfig.getKeys(true).size();
            // First: defaults from the selected language resource (if present)
            InputStreamReader selReader = null;
            try {
                selReader = new InputStreamReader(Objects.requireNonNullElseGet(
                        plugin.getResource(path),
                        () -> plugin.getResource(ConfigFiles.DEFAULT_LANGUAGE.getFileName())
                ));
            } catch (Exception ignored) { }
            if (selReader != null) {
                FileConfiguration selDefaults = YamlConfiguration.loadConfiguration(selReader);
                languageConfig.addDefaults(selDefaults);
            }
            // Also: fall back to DEFAULT_LANGUAGE for any keys missing
            try (InputStreamReader defReader = new InputStreamReader(Objects.requireNonNull(plugin.getResource(ConfigFiles.DEFAULT_LANGUAGE.getFileName())))) {
                FileConfiguration baseDefaults = YamlConfiguration.loadConfiguration(defReader);
                languageConfig.addDefaults(baseDefaults);
            } catch (Exception e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.LANGUAGE_MANAGER, "Could not load base default language from resources: " + e.getMessage());
            }
            languageConfig.options().copyDefaults(true);
            languageConfig.save(languageFile);
            int after = languageConfig.getKeys(true).size();
            int added = Math.max(0, after - before);
            if (added > 0) {
                Logger.logMessage(LogLevel.INFORMATION, Prefix.LANGUAGE_MANAGER, "Added " + added + " missing language entries from defaults (" + path + ")");
            }
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.LANGUAGE_MANAGER, "Error while ensuring language defaults: " + e.getMessage());
        }

        Logger.logMessage(LogLevel.INFORMATION, Prefix.LANGUAGE_MANAGER, languageConfig.getString("language-name", path) + " has been loaded! (" + path + ")");
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public String getLanguageString(LanguageText text, List<String> placeholders) {
        checkAvailability(text);
        String languageString = languageConfig.getString(text.getConfigValue());
        for (int i = 0; i < placeholders.size(); i++) {
            String v = placeholders.get(i);
            languageString = languageString.replace("%%" + (i + 1) + "%%", v);
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
        if (!languageConfig.contains(text.getConfigValue())) {
            JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
            try {
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(plugin.getResource(ConfigFiles.DEFAULT_LANGUAGE.getFileName()))));
                if (fileConfiguration.contains(text.getConfigValue())) {
                    String value = fileConfiguration.getString(text.getConfigValue());
                    languageConfig.addDefault(text.getConfigValue(), value);
                    languageConfig.options().copyDefaults(true);
                    languageConfig.save(languageFile);
                } else {
                    languageConfig.addDefault(text.getConfigValue(), "LANGUAGE_NOT_FOUND");
                    languageConfig.options().copyDefaults(true);
                    languageConfig.save(languageFile);
                    Logger.logMessage(LogLevel.ERROR, Prefix.LANGUAGE_MANAGER, text.getConfigValue() + " has no default value, please add one.");
                }
            } catch (Exception e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.LANGUAGE_MANAGER, "Could not save or load default language file.");
                e.printStackTrace();
            }
        }
    }
}
