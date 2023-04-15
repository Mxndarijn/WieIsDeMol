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
    COMMAND_SKULLS_DELETED("command-skulls-deleted");

    private final String configValue;
    LanguageText(String value) {
        this.configValue = value;
    }

    public String getConfigValue() {
        return configValue;
    }
}
