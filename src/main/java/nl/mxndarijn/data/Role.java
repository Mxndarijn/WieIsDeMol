package nl.mxndarijn.data;

import org.bukkit.ChatColor;

import java.util.Optional;

public enum Role {
    SPELER("player", ChatColor.GOLD + "Speler", ChatColor.GOLD + "Speler-Peacekeeper", "gold-block", CustomInventoryScreens.GAME_YOU_ARE_SPELER),
    MOL("mol", ChatColor.DARK_AQUA + "Mol", ChatColor.DARK_AQUA + "Mol-Peacekeeper", "diamond-block", CustomInventoryScreens.GAME_YOU_ARE_MOLE),
    EGO("ego",ChatColor.GRAY + "Ego", ChatColor.GRAY + "Ego-Peacekeeper", "emerald-block", CustomInventoryScreens.GAME_YOU_ARE_EGO);

    private final String rolName;
    private final String peacekeeperName;
    private final String roleType;
    private final String headKey;
    private final CustomInventoryScreens screen;
    Role(String rolType, String normalName, String peacekeeperName, String headKey, CustomInventoryScreens screen) {
        this.roleType = rolType;
        this.rolName = normalName;
        this.peacekeeperName = peacekeeperName;
        this.headKey = headKey;
        this.screen = screen;
    }

    public CustomInventoryScreens getScreen() {
        return screen;
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
}
