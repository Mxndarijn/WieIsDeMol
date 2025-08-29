package nl.mxndarijn.api.logger;

import java.util.Optional;

public enum LogLevel {
    FATAL("Fatal", 1),
    DEBUG_HIGHLIGHT("Debug-Highlight", 1),
    ERROR("Error", 2),
    WARNING("Warning", 3),
    INFORMATION("Information", 4),
    DEBUG("Debug", 5);

    private final String prefix;
    private final int level;
    private final String name;

    LogLevel(String logName, int level) {
        this.level = level;
        this.name = logName;
        this.prefix = Logger.getMainWieIsDeMolPrefix() +"-" +logName + "] » ";
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
