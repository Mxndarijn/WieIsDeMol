package nl.mxndarijn.data;

import org.bukkit.ChatColor;

public enum CustomInventoryOverlay {

    ROLES_PLAYER("\uE004"),
    ROLES_MOLE("\uE005"),
    ROLES_EGO("\uE006"),
    ROLES_PEACEKEEPER("\uE007"),
    CHEST_APPEARANCE_2_OPTIONS("\uE008"),
    CHEST_APPEARANCE_3_OPTIONS("\uE009"),
    CHEST_APPEARANCE_4_OPTIONS("\uE010");

    private final String PREFIX = ChatColor.WHITE + "\uE001\uE001\uE001\uE001\uE001\uE001\uE001\uE001";
    private final String unicodeCharacter;

    CustomInventoryOverlay(String unicodeCharacter) {
        this.unicodeCharacter = PREFIX + unicodeCharacter;
    }

    @Override
    public String toString() {
        return unicodeCharacter;
    }

    public String getUnicodeCharacter() {
        return unicodeCharacter;
    }
}