package nl.mxndarijn.data;

public enum Permissions {
    NO_PERMISSION(""),
    COMMAND_MAPS("widm.maps");

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
