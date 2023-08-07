package nl.mxndarijn.data;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Optional;

public enum Colors {
    LICHTGROEN("light-green",  ChatColor.GREEN, "Lime", Material.LIME_SHULKER_BOX, "light-green-block"),
    GROEN("green", ChatColor.DARK_GREEN, "Groen",Material.GREEN_SHULKER_BOX, "green-block"),
    LICHTBLAUW("light-blue", ChatColor.BLUE, "Licht-Blauw",Material.LIGHT_BLUE_SHULKER_BOX, "light-blue-block"),
    BLAUW("blue", ChatColor.DARK_BLUE, "Blauw",Material.BLUE_SHULKER_BOX, "blue-block"),
    ZWART("black", ChatColor.BLACK, "Zwart",Material.BLACK_SHULKER_BOX, "black-block"),
    WIT("white", ChatColor.WHITE, "Wit",Material.WHITE_SHULKER_BOX, "white-block"),
    ORANJE("orange", ChatColor.GOLD, "Oranje",Material.ORANGE_SHULKER_BOX, "orange-block"),
    ROOD("red", ChatColor.RED, "Rood",Material.RED_SHULKER_BOX, "red-block"),
    MAGENTA("magenta", ChatColor.LIGHT_PURPLE, "Magenta",Material.MAGENTA_SHULKER_BOX, "magenta-block"),
    ROZE("pink", ChatColor.LIGHT_PURPLE, "Roze",Material.PINK_SHULKER_BOX, "pink-block"),
    PAARS("purple", ChatColor.DARK_PURPLE, "Paars",Material.PURPLE_SHULKER_BOX, "purple-block"),
    CYAN("cyan", ChatColor.DARK_AQUA, "Cyan",Material.CYAN_SHULKER_BOX, "cyan-block"),
    BRUIN("brown", ChatColor.GOLD, "Bruin",Material.BROWN_SHULKER_BOX, "brown-block"),
    LICHTGRIJS("light-gray", ChatColor.GRAY, "Licht-Grijs",Material.LIGHT_GRAY_SHULKER_BOX, "light-gray-block"),
    GRIJS("gray", ChatColor.DARK_GRAY, "Grijs",Material.GRAY_SHULKER_BOX, "gray-block"),
    GEEL("yellow", ChatColor.YELLOW, "Geel",Material.YELLOW_SHULKER_BOX, "yellow-block");

    private final String type;
    private final ChatColor color;
    private final String displayName;
    private final String displayNameWithoutColor;
    private final Material shulkerBlock;
    private final String headKey;
    Colors(String type, ChatColor color, String displayName, Material shulkerBlock, String headKey) {
        this.type = type;
        this.color = color;
        this.displayName = this.color + displayName;
        this.displayNameWithoutColor = displayName;
        this.shulkerBlock = shulkerBlock;
        this.headKey = headKey;
    }

    public static Optional<Colors> getColorByType(String type) {
        for(Colors color : values()) {
            if(color.type.equals(type)) {
                return Optional.of(color);
            }
        }

        return Optional.empty();
    }

    public static Optional<Colors> getColorByMaterial(Material type) {
        for(Colors color : values()) {
            if(color.shulkerBlock.equals(type)) {
                return Optional.of(color);
            }
        }

        return Optional.empty();
    }

    public String getType() {
        return type;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getShulkerBlock() {
        return shulkerBlock;
    }

    public String getHeadKey() {
        return headKey;
    }

    public String getDisplayNameWithoutColor() {
        return displayNameWithoutColor;
    }
}
