package nl.mxndarijn.util.logger;

import org.bukkit.ChatColor;

import java.util.Optional;

public enum LogLevel {
    Fatal("Fatal", 1, ChatColor.DARK_RED),
    DebugHighlight("Debug-Highlight", 1, ChatColor.LIGHT_PURPLE),
    Error("Error", 2, ChatColor.RED),
    Warning("Warning", 3, ChatColor.GOLD),
    Information("Information", 4, ChatColor.DARK_AQUA),
    Debug("Debug", 5, ChatColor.DARK_GREEN);

    private final String prefix;
    private final int level;
    private final String name;
    LogLevel(String logName, int level, ChatColor color) {
        this.level = level;
        this.name = color + logName;
        this.prefix = Logger.getMainWieIsDeMolPrefix() + ChatColor.DARK_GRAY + "-" + color + logName + ChatColor.DARK_GRAY + "]" + color + " \u00BB " + ChatColor.DARK_GRAY;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getLevel() {
        return level;
    }

    public static Optional<LogLevel> getLevelByInt(int i) {
        for(LogLevel l : values()) {
            if(l.level == i) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    public String getName() {
        return name;
    }
}
