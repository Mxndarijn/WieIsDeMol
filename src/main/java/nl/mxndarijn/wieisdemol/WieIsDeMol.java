package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class WieIsDeMol extends JavaPlugin {

    @Override
    public void onEnable() {
        setLogLevel();
        Logger.logMessage(LogLevel.Information, "Starting Wie Is De Mol...");
        getCommand("test").setExecutor(new TestCommand());
        WorldManager.getInstance();
        LanguageManager.getInstance();

        Logger.logMessage(LogLevel.Information, "Started Wie Is De Mol...");
    }


    @Override
    public void onDisable() {
        Logger.logMessage(LogLevel.Information, "Stopping Wie Is De Mol...");

        Logger.logMessage(LogLevel.Information, "Stopped Wie Is De Mol...");
    }
    private void setLogLevel() {
        Logger.setLogLevel(LogLevel.Debug);
        Optional<LogLevel> level = LogLevel.getLevelByInt(ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("log-level"));
        if(level.isPresent()) {
            Logger.setLogLevel(level.get());
            Logger.logMessage(LogLevel.Information, Prefix.LOGGER, "Log-level has been set to " + level.get().getName() + ChatColor.DARK_GRAY + " (Found in config)");
        } else {
            Logger.setLogLevel(LogLevel.Debug);
            Logger.logMessage(LogLevel.Information, Prefix.LOGGER, "Log-level has been set to " + LogLevel.Debug.getName() + ChatColor.DARK_GRAY + " (default, not found in config)");
        }
    }
}
