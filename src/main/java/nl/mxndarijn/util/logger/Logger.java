package nl.mxndarijn.util.logger;

import org.bukkit.ChatColor;

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
            System.out.println(level.getPrefix() + message);
        }
    }

    public static void logMessage(LogLevel level, Prefix prefix, String message) {
        if(level.getLevel() <= logLevel.getLevel()) {
            System.out.println(level.getPrefix() + prefix + message);
        }
    }
}
