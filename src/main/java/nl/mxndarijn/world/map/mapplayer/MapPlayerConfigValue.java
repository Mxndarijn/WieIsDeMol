package nl.mxndarijn.world.map.mapplayer;

import nl.mxndarijn.world.map.MapPlayer;

public enum MapPlayerConfigValue {
    LOCATION("location"),
    ROLE("role");
    private final String configValue;

    MapPlayerConfigValue(String value) {
        this.configValue = value;
    }

    public String getConfigValue() {
        return configValue;
    }

    @Override
    public String toString() {
        return configValue;
    }
}
