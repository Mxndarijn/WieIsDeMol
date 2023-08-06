package nl.mxndarijn.data;

public enum ChestAppearance {
    CHOICE_TWO("Twee-Items-Keuze"),
    CHOICE_THREE("Drie-Items-Keuze"),
    CHOICE_FOUR("Vier-Items-Keuze");

    private final String name;
    ChestAppearance(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
}
