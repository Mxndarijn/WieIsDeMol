package nl.mxndarijn.wieisdemol.data;

import lombok.Getter;

public enum CustomInventoryOverlay {

    ROLES_PLAYER("\uE004"),
    ROLES_MOLE("\uE005"),
    ROLES_EGO("\uE006"),
    ROLES_SHAPESHIFTER("\uE003"),
    ROLES_PEACEKEEPER("\uE007"),
    CHEST_APPEARANCE_2_OPTIONS("\uE008"),
    CHEST_APPEARANCE_3_OPTIONS("\uE009"),
    CHEST_APPEARANCE_4_OPTIONS("\uE010"),
    GAME_DEATHNOTE("\uE043"),
    GAME_INVCLEAR("\uE044");

    private final String PREFIX = "<white>\uE001\uE001\uE001\uE001\uE001\uE001\uE001\uE001";
    @Getter
    private final String unicodeCharacter;

    CustomInventoryOverlay(String unicodeCharacter) {
        this.unicodeCharacter = PREFIX + unicodeCharacter;
    }

    @Override
    public String toString() {
        return unicodeCharacter;
    }

}