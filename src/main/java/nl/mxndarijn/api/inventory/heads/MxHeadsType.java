package nl.mxndarijn.api.inventory.heads;

import java.util.Optional;

public enum MxHeadsType {
    MANUALLY_ADDED("manually-added"),
    PLAYER("player");

    private final String type;

    MxHeadsType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static Optional<MxHeadsType> getTypeFromName(String name) {
        for(MxHeadsType t : values()) {
            if(t.getType().equalsIgnoreCase(name)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
}
