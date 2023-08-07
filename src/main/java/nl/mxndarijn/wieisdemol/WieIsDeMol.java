package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.commands.*;
import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.logic.inventory.heads.MxHeadManager;
import nl.mxndarijn.logic.items.ItemManager;
import nl.mxndarijn.logic.items.util.storage.StorageManager;
import nl.mxndarijn.managers.chatinput.MxChatInputManager;
import nl.mxndarijn.logic.util.events.PlayerJoinEventHeadManager;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.logic.util.logger.Prefix;
import nl.mxndarijn.managers.world.WorldManager;
import nl.mxndarijn.managers.changeworld.ChangeWorldManager;
import nl.mxndarijn.managers.MapManager;
import nl.mxndarijn.managers.PresetsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class WieIsDeMol extends JavaPlugin {

    @Override
    public void onEnable() {
        setLogLevel();
        Logger.logMessage(LogLevel.INFORMATION, "Starting Wie Is De Mol...");
        getCommand("test").setExecutor(new TestCommand());
        WorldManager.getInstance();
        LanguageManager.getInstance();
        MxHeadManager.getInstance();
        MxChatInputManager.getInstance();
        PresetsManager.getInstance();
        MapManager.getInstance();
        ChangeWorldManager.getInstance();
        ItemManager.getInstance();
        StorageManager.getInstance();
        registerCommands();
        configFilesSaver();

        getServer().getPluginManager().registerEvents(new PlayerJoinEventHeadManager(), this);

        Logger.logMessage(LogLevel.INFORMATION, "Started Wie Is De Mol...");
    }


    @Override
    public void onDisable() {
        Logger.logMessage(LogLevel.INFORMATION, "Stopping Wie Is De Mol...");
        ConfigFiles.saveAll();
        StorageManager.getInstance().save();
        Logger.logMessage(LogLevel.INFORMATION, "Stopped Wie Is De Mol...");
    }
    private void setLogLevel() {
        Logger.setLogLevel(LogLevel.DEBUG);
        Optional<LogLevel> level = LogLevel.getLevelByInt(ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("log-level"));
        if(level.isPresent()) {
            Logger.setLogLevel(level.get());
            Logger.logMessage(LogLevel.INFORMATION, Prefix.LOGGER, "Log-level has been set to " + level.get().getName() + ChatColor.DARK_GRAY + " (Found in config)");
        } else {
            Logger.setLogLevel(LogLevel.DEBUG);
            Logger.logMessage(LogLevel.INFORMATION, Prefix.LOGGER, "Log-level has been set to " + LogLevel.DEBUG.getName() + ChatColor.DARK_GRAY + " (default, not found in config)");
        }
    }

    private void registerCommands() {
        Logger.logMessage(LogLevel.INFORMATION,"Registering commands...");
        getCommand("maps").setExecutor(new MapCommand(Permissions.COMMAND_MAPS, true, false));
        getCommand("presets").setExecutor(new PresetsCommand(Permissions.COMMAND_PRESETS, true, false));
        getCommand("skulls").setExecutor(new SkullsCommand(Permissions.COMMAND_SKULLS, true, false));
        getCommand("spawn").setExecutor(new SpawnCommand(Permissions.COMMAND_SPAWN, true, false));
        getCommand("items").setExecutor(new ItemsCommand());
    }

    private void configFilesSaver() {
        int interval = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("auto-save-configs-interval");
        if(interval == 0) {
            interval = 5;
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Interval for auto-save is 0, autosetting it to 5.. (Needs to be higher than 0)");
            ConfigFiles.MAIN_CONFIG.getFileConfiguration().set("auto-save-configs-interval", 5);
        }
        Logger.logMessage(LogLevel.INFORMATION, Prefix.CONFIG_FILES, "Saving interval for config files is " + interval + " minutes.");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ConfigFiles.saveAll();
        },20L * 60L * interval,20L * 60L * interval);
    }
}
