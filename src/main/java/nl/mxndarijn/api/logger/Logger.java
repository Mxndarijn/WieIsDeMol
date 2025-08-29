package nl.mxndarijn.api.logger;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class Logger {
    private static LogLevel logLevel = LogLevel.DEBUG;

    public static void setLogLevel(LogLevel newLogLevel) {
        logLevel = newLogLevel;
    }

    public static String getMainWieIsDeMolPrefix() {
        return "<dark_gray>[<gold>WIDM";
    }

    public static void logMessage(LogLevel level, String message) {
        if (level.getLevel() <= logLevel.getLevel()) {
            Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(level.getPrefix() + message));
        }
    }

    public static void logMessage(String message) {
        logMessage(LogLevel.DEBUG_HIGHLIGHT, message);
    }

    public static void logMessage(LogLevel level, Prefix prefix, String message) {
        if (level.getLevel() <= logLevel.getLevel()) {
            Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(level.getPrefix() + prefix + message));
        }
    }
}
