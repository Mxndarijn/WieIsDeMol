package nl.mxndarijn.util.logger;

import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Logger {
    private static LogLevel logLevel = LogLevel.Debug;

    public static void setLogLevel(LogLevel newLogLevel) {
        logLevel = newLogLevel;
    }
    public static String getMainWieIsDeMolPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "WIDM";
    }

    public static void logMessage(LogLevel level, String message) {
        if(level.getLevel() <= logLevel.getLevel()) {
            Bukkit.getConsoleSender().sendMessage(level.getPrefix() + message);
        }
    }

    public static void logMessage(LogLevel level, Prefix prefix, String message) {
        if(level.getLevel() <= logLevel.getLevel()) {
            Bukkit.getConsoleSender().sendMessage(level.getPrefix() + prefix + message);
        }
    }
}
