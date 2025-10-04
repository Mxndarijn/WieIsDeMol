package nl.mxndarijn.wieisdemol.data;

import lombok.Getter;
import nl.mxndarijn.wieisdemol.managers.database.PlayerData;
import org.bukkit.Material;

import java.util.Optional;

@Getter
public enum Role {
    SPELER("player", "<gold>Speler", "<gold>Speler-Peacekeeper", "gold-block", CustomInventoryOverlay.ROLES_PLAYER.getUnicodeCharacter(), Material.GOLD_BLOCK, "<gold>Spelers", "Hebben gewonnen", PlayerData.UserDataType.SPELERWINS),
    MOL("mol", "<dark_aqua>Mol", "<dark_aqua>Mol-Peacekeeper", "diamond-block", CustomInventoryOverlay.ROLES_MOLE.getUnicodeCharacter(), Material.DIAMOND_BLOCK, "<#00FFFF>Mollen", "Hebben gewonnen", PlayerData.UserDataType.MOLWINS),
    EGO("ego", "<gray>Ego", "<gray>Ego-Peacekeeper", "emerald-block", CustomInventoryOverlay.ROLES_EGO.getUnicodeCharacter(), Material.AIR, "<gray>Ego", "heeft gewonnen", PlayerData.UserDataType.EGOWINS),
    SHAPESHIFTER("shapeshifter", "<dark_green>Shapeshifter", "<green>Shapeshifter-Peacekeeper", "emerald-block", CustomInventoryOverlay.ROLES_SHAPESHIFTER.getUnicodeCharacter(), Material.AIR, "<green>Shapeshifter", "heeft gewonnen", PlayerData.UserDataType.EGOWINS);

    private final String rolName;
    private final String peacekeeperName;
    private final String roleType;
    private final String headKey;
    private final String unicode;
    private final Material type;
    private final String title;
    private final String subTitle;
    private final PlayerData.UserDataType winType;

    Role(String rolType, String normalName, String peacekeeperName, String headKey, String unicode, Material type, String title, String subTitle, PlayerData.UserDataType winType) {
        this.roleType = rolType;
        this.rolName = normalName;
        this.peacekeeperName = peacekeeperName;
        this.headKey = headKey;
        this.unicode = unicode;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.winType = winType;
    }

    public static Optional<Role> getRoleByType(String type) {
        for (Role value : values()) {
            if (value.roleType.equalsIgnoreCase(type))
                return Optional.of(value);
        }
        return Optional.empty();
    }

}
