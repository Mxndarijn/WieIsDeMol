package nl.mxndarijn.data;

import org.bukkit.ChatColor;

public enum CustomInventoryScreens {
    GAME_YOU_ARE_MOLE("a"),
    GAME_YOU_ARE_SPELER("b"),
        GAME_YOU_ARE_EGO("c"),
    BOOK_DEATHNOTE("a"),
    BOOK_TELEPORT("t");

    private final String name;
    CustomInventoryScreens(String name) {
        this.name = ChatColor.RESET + name;
    }

    public String getName() {
        return name;
    }
}
