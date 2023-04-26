package nl.mxndarijn.util.language;

public enum LanguageText {
    NO_PERMISSION("no-permission"),
    NO_PLAYER("no-player"),
    NOT_CORRECT_WORLD("not-correct-world"),
    ERROR_WHILE_EXECUTING_COMMAND("error-while-executing-command"),
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
    PRESET_CONFIGURE_TOOL_SPAWN_CHANGED("preset-configure-tool-spawn-changed");

    private final String configValue;
    LanguageText(String value) {
        this.configValue = value;
    }

    public String getConfigValue() {
        return configValue;
    }
}
