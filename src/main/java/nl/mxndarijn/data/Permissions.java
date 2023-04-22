package nl.mxndarijn.data;

public enum Permissions {
    NO_PERMISSION(""),
    COMMAND_MAPS("widm.maps"),
    COMMAND_SKULLS("widm.skulls"),
    COMMAND_SKULLS_ADD_SKULL("widm.addskull"),
    COMMAND_SPAWN("widm.spawn"),
    COMMAND_SKULLS_REMOVE_SKULL("widm.removeskull");

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
