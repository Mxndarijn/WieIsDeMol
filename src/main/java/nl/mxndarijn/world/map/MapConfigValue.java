package nl.mxndarijn.world.map;

public enum MapConfigValue {
    NAME("name"),
    DATE_CREATED("date-created"),
    DATE_MODIFIED("date-modified"),
    SHARED_PLAYERS("shared-players"),
    OWNER("owner"),
    COLORS("colors");
    private final String configValue;

    MapConfigValue(String value) {
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
