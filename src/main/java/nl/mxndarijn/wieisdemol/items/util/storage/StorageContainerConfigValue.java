package nl.mxndarijn.wieisdemol.items.util.storage;

import lombok.Getter;

@Getter
public enum StorageContainerConfigValue {

    ITEMS("items"),
    NAME("name"),
    SKULL("skull"),
    OWNER("owner"),
    IS_PUBLIC("is-public");

    private final String configValue;

    StorageContainerConfigValue(String name) {
        this.configValue = name;
    }

    @Override
    public String toString() {
        return configValue;
    }
}
