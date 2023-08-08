package nl.mxndarijn.wieisdemol.data;

public enum Permissions {
    NO_PERMISSION(""),
    COMMAND_MAPS("widm.maps"),
    COMMAND_SKULLS("widm.skulls"),
    COMMAND_SKULLS_ADD_SKULL("widm.addskull"),
    COMMAND_SPAWN("widm.spawn"),
    COMMAND_SKULLS_REMOVE_SKULL("widm.removeskull"),
    ITEM_ITEMS_EDIT_SERVER_CONTAINERS("widm.servercontainers.edit"),
    COMMAND_MAPS_ITEMS("widm.items"),
    COMMAND_PRESETS("widm.presets");

    private final String permission;
    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return this.permission;
    }
}
