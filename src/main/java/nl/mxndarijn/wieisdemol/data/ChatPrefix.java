package nl.mxndarijn.wieisdemol.data;



public enum ChatPrefix {
    WIDM("<green>WIDM"),
    NO_PERMISSION("<red>Geen-Permissie");

    private final String prefix;
    private final String name;

    ChatPrefix(String prefix) {
        this.prefix = prefix + "<dark_green> \u00BB <gray>";
        this.name = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }
}
