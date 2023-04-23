package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.commands.*;
import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.items.ItemManager;
import nl.mxndarijn.util.chatinput.MxChatInputManager;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.WorldManager;
import nl.mxndarijn.world.changeworld.ChangeWorldManager;
import nl.mxndarijn.world.presets.PresetsManager;
import org.bukkit.Bukkit;
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
        MxHeadManager.getInstance();
        MxChatInputManager.getInstance();
        PresetsManager.getInstance();
        ChangeWorldManager.getInstance();
        ItemManager.getInstance();
        registerCommands();
        configFilesSaver();

        Logger.logMessage(LogLevel.Information, "Started Wie Is De Mol...");
    }


    @Override
    public void onDisable() {
        Logger.logMessage(LogLevel.Information, "Stopping Wie Is De Mol...");
        ConfigFiles.saveAll();
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

    private void registerCommands() {
        Logger.logMessage(LogLevel.Information,"Registering commands...");
        getCommand("maps").setExecutor(new MapCommand(Permissions.COMMAND_MAPS, true, false));
        getCommand("presets").setExecutor(new PresetsCommand(Permissions.COMMAND_MAPS, true, false));
        getCommand("skulls").setExecutor(new SkullsCommand(Permissions.COMMAND_SKULLS, true, false));
        getCommand("spawn").setExecutor(new SpawnCommand(Permissions.COMMAND_SPAWN, true, false));
    }

    private void configFilesSaver() {
        int interval = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("auto-save-configs-interval");
        if(interval == 0) {
            interval = 5;
            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Interval for auto-save is 0, autosetting it to 5.. (Needs to be higher than 0)");
            ConfigFiles.MAIN_CONFIG.getFileConfiguration().set("auto-save-configs-interval", 5);
        }
        Logger.logMessage(LogLevel.Information, Prefix.CONFIG_FILES, "Saving interval for config files is " + interval + " minutes.");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ConfigFiles.saveAll();
        },20L * 60L * interval,20L * 60L * interval);
    }
}
