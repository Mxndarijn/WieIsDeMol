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
    COMMAND_PRESETS("widm.presets"),
    ITEM_GAMES_CREATE_GAME("widm.creategame"),
    ITEM_GAMES_MANAGE_OTHER_GAMES("widm.managegames"),

    SPAWN_BLOCK_BREAK("widm.blockbreakspawn"),
    SPAWN_BLOCK_PLACE("widm.blockplacespawn"),
    SPAWN_DROP_ITEM("widm.dropitemspawn"),
    SPAWN_PICKUP_ITEM("widm.pickupitemspawn"),
    SPAWN_CHANGE_INVENTORY("widm.changeinventoryspawn"),
    VANISH("widm.vanish");

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
