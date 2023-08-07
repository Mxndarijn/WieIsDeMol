package nl.mxndarijn.data;

import org.bukkit.ChatColor;

import java.util.Optional;

public enum Role {
    SPELER("player", ChatColor.GOLD + "Speler", ChatColor.GOLD + "Speler-Peacekeeper", "gold-block", CustomInventoryOverlay.ROLES_PLAYER.getUnicodeCharacter()),
    MOL("mol", ChatColor.DARK_AQUA + "Mol", ChatColor.DARK_AQUA + "Mol-Peacekeeper", "diamond-block", CustomInventoryOverlay.ROLES_MOLE.getUnicodeCharacter()),
    EGO("ego",ChatColor.GRAY + "Ego", ChatColor.GRAY + "Ego-Peacekeeper", "emerald-block", CustomInventoryOverlay.ROLES_EGO.getUnicodeCharacter());

    private final String rolName;
    private final String peacekeeperName;
    private final String roleType;
    private final String headKey;
    private final String unicode;
    Role(String rolType, String normalName, String peacekeeperName, String headKey, String unicode) {
        this.roleType = rolType;
        this.rolName = normalName;
        this.peacekeeperName = peacekeeperName;
        this.headKey = headKey;
        this.unicode = unicode;
    }


    public String getRolName() {
        return rolName;
    }

    public String getPeacekeeperName() {
        return peacekeeperName;
    }

    public static Optional<Role> getRoleByType(String type) {
        for (Role value : values()) {
            if(value.roleType.equalsIgnoreCase(type))
                return Optional.of(value);
        }
        return Optional.empty();
    }

    public String getRoleType() {
        return roleType;
    }

    public String getHeadKey() {
        return headKey;
    }

    public String getUnicode() {
        return unicode;
    }
}
