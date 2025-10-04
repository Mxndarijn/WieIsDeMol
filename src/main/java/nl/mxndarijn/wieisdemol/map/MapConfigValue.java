package nl.mxndarijn.wieisdemol.map;

import lombok.Getter;

@Getter
public enum MapConfigValue {
    NAME("name"),
    DATE_CREATED("date-created"),
    DATE_MODIFIED("date-modified"),
    SHARED_PLAYERS("shared-players"),
    OWNER("owner"),
    COLORS("colors"),
    PEACEKEEPER_KILLS("peacekeeperKills");
    private final String configValue;

    MapConfigValue(String value) {
        this.configValue = value;
    }

    @Override
    public String toString() {
        return configValue;
    }
}
