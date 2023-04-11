package nl.mxndarijn.util.language;

public enum LanguageText {
    NO_PERMISSION("no-permission"),
    NO_PLAYER("no-player"),
    NOT_CORRECT_WORLD("not-correct-world"),
    ERROR_WHILE_EXECUTING_COMMAND("error-while-executing-command");

    private final String configValue;
    LanguageText(String value) {
        this.configValue = value;
    }

    public String getConfigValue() {
        return configValue;
    }
}
