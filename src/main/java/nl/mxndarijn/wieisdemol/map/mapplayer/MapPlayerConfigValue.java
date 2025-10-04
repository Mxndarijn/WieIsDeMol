package nl.mxndarijn.wieisdemol.map.mapplayer;

import lombok.Getter;

@Getter
public enum MapPlayerConfigValue {
    LOCATION("location"),
    ROLE("role"),
    IS_PEACEKEEPER("is-peacekeeper");
    private final String configValue;

    MapPlayerConfigValue(String value) {
        this.configValue = value;
    }

    @Override
    public String toString() {
        return configValue;
    }
}
