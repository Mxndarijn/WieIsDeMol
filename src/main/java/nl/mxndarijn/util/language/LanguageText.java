package nl.mxndarijn.util.language;

public enum
LanguageText {
    NO_PERMISSION("no-permission"),
    NO_PLAYER("no-player"),
    NOT_CORRECT_WORLD("not-correct-world"),
    ERROR_WHILE_EXECUTING_COMMAND("error-while-executing-command"),
    ERROR_WHILE_EXECUTING_ITEM("error-while-executing-item"),
    COMMAND_SKULLS_SKULL_ADDED("command-skulls-skull-added"),
    COMMAND_SKULLS_SKULL_NOT_ADDED("command-skulls-skull-not-added"),
    COMMAND_SKULLS_GET_CHAT_INPUT("command-skulls-get-chat-input"),
    COMMAND_SKULLS_IS_SKULL_PLAYER_SKULL("command-skulls-is-skull-player-skull"),
    COMMAND_SKULLS_DEFAULT("command-skulls-default"),
    COMMAND_SKULLS_NOT_DELETED("command-skulls-not-deleted"),
    COMMAND_SKULLS_DELETED("command-skulls-deleted"),

    COMMAND_PRESETS_WORLD_NOT_FOUND_BUT_LOADED("command-presets-world-not-found-but-loaded"),
    COMMAND_PRESETS_WORLD_COULD_NOT_BE_LOADED("command-presets-world-could-not-be-loaded"),
    COMMAND_PRESETS_NOW_IN_PRESET("command-presets-now-in-preset"),
    COMMAND_PRESETS_LOADING_WORLD("command-presets-loading-world"),
    PRESET_INVENTORY_LOADING("preset-loading-inventory"),
    PRESET_INVENTORY_SAVED("preset-loading-saved"),
    PRESET_INFO_CONFIGURE_TOOL("preset-info-configure-tool"),
    PRESET_CONFIGURE_TOOL_ENTER_NEW_NAME("preset-configure-tool-enter-new-name"),
    PRESET_CONFIGURE_TOOL_NAME_CHANGED("preset-configure-tool-name-changed"),
    PRESET_CONFIGURE_TOOL_SKULL_CHANGED("preset-configure-tool-skull-changed"),
    PRESET_CONFIGURE_TOOL_WARPS_CHANGE_NAME("preset-configure-tool-warps-change-name"),
    PRESET_CONFIGURE_TOOL_WARPS_WARP_CREATED("preset-configure-tool-warps-warp-created"),
    PRESET_CONFIGURE_TOOL_WARPS_WARP_NAME_ALREADY_EXISTS("preset-configure-tool-warps-warp-name-already-exists"),
    PRESET_CONFIGURE_TOOL_WARPS_WARP_NAME_CHANGED("preset-configure-tool-warps-name-changed"),
    PRESET_CONFIGURE_TOOL_WARPS_WARP_TELEPORTED("preset-configure-tool-warps-teleported"),
    PRESET_CONFIGURE_TOOL_WARPS_WARP_DELETED("preset-configure-tool-warps-deleted"),
    PRESET_CONFIGURE_TOOL_SPAWN_CHANGED("preset-configure-tool-spawn-changed"),
    PRESET_CONFIGURE_TOOL_COLOR_ADDED("preset-configure-tool-color-added"),
    PRESET_CONFIGURE_TOOL_COLOR_REMOVED("preset-configure-tool-color-removed"),
    PRESET_CONFIGURE_TOOL_COLOR_SPAWNPOINT_CHANGED("preset-configure-tool-spawnpoint-changed"),
    PRESET_CONFIGURE_TOOL_COLOR_TELEPORTED("preset-configure-tool-teleported"),
    CHEST_CONFIGURE_TOOL_CHEST_ADDED("chest-configure-tool-chest-added"),
    CHEST_CONFIGURE_TOOL_INFO("chest-configure-tool-info"),
    CHEST_CONFIGURE_TOOL_ENTER_NAME("chest-configure-tool-enter-name"),
    CHEST_CONFIGURE_TOOL_CHEST_REMOVED("chest-configure-tool-chest-removed"),
    SHULKER_CONFIGURE_TOOL_CHEST_ADDED("shulker-configure-tool-chest-added"),
    SHULKER_CONFIGURE_TOOL_INFO("shulker-configure-tool-info"),
    SHULKER_CONFIGURE_TOOL_ENTER_NAME("shulker-configure-tool-enter-name"),
    SHULKER_CONFIGURE_TOOL_CHEST_REMOVED("shulker-configure-tool-chest-removed"),
    DOOR_CONFIGURE_TOOL_SELECTED("door-configure-tool-selected"),
    DOOR_CONFIGURE_TOOL_DOOR_CREATE_INPUT_NAME("door-configure-tool-door-create-input-name"),
    DOOR_CONFIGURE_TOOL_DOOR_CREATED("door-configure-tool-door-created"),
    DOOR_CONFIGURE_TOOL_NO_DOOR_SELECTED("door-configure-tool-no-door-selected"),
    DOOR_CONFIGURE_TOOL_ALREADY_ADDED("door-configure-tool-already-added"),
    DOOR_CONFIGURE_TOOL_ADDED("door-configure-tool-added"),
    DOOR_CONFIGURE_TOOL_LOCATION_REMOVED("door-configure-tool-location-removed"),
    DOOR_CONFIGURE_TOOL_INFO("door-configure-tool-info"),
    DOOR_CONFIGURE_TOOL_LOCATION_NOT_FOUND("door-configure-tool-location-not-found"),
    DOOR_CONFIGURE_TOOL_DOOR_REMOVED("door-configure-tool-door-removed"),

    COMMAND_MAPS_COULD_NOT_FIND_PRESET("command-maps-could-not-find-preset"),

    COMMAND_ENTER_MAP_NAME_FOR_PRESET("command-maps-enter-mapname-for-preset"),
    COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED("command-maps-map-could-not-be-created"),
    COMMAND_MAPS_MAP_CREATED("command-maps-map-created"),
    COMMAND_MAPS_MAP_COULD_NOT_BE_LOADED("command-maps-map-could-not-be-loaded"),

    MAP_AUTOMATED_CHEST_ADDED("map-automated-chest-added"),
    MAP_AUTOMATED_CHEST_REMOVED("map-automated-chest-removed");

    private final String configValue;
    LanguageText(String value) {
        this.configValue = value;
    }

    public String getConfigValue() {
        return configValue;
    }
}
