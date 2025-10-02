package nl.mxndarijn.api.logger;

import java.util.Optional;

public enum LogLevel {
    FATAL("Fatal", 1, "dark_red"),
    DEBUG_HIGHLIGHT("Debug-Highlight", 1, "#ff00ff"),
    ERROR("Error", 2, "red"),
    WARNING("Warning", 3, "gold"),
    INFORMATION("Information", 4, "aqua"),
    DEBUG("Debug", 5, "gray");

    private final String prefix;
    private final int level;
    private final String name;
    private final String color;

    LogLevel(String logName, int level, String color) {
        this.level = level;
        this.name = logName;
        this.color = color;
        this.prefix = Logger.getMainWieIsDeMolPrefix() + "-" + "<" + this.color + ">" + logName + "<dark_gray>] <yellow>» ";
    }

    public static Optional<LogLevel> getLevelByInt(int i) {
        for (LogLevel l : values()) {
            if (l.level == i) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    public String getPrefix() {
        return prefix;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
