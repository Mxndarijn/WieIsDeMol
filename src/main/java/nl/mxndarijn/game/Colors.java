package nl.mxndarijn.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Optional;

public enum Colors {
    LICHTGROEN("light-green",  ChatColor.GREEN, "Lime", Material.LIME_SHULKER_BOX),
    GROEN("green", ChatColor.DARK_GREEN, "Groen",Material.GREEN_SHULKER_BOX),
    LICHTBLAUW("light-blue", ChatColor.BLUE, "Licht-Blauw",Material.LIGHT_BLUE_SHULKER_BOX),
    BLAUW("blue", ChatColor.DARK_BLUE, "Blauw",Material.BLUE_SHULKER_BOX),
    ZWART("black", ChatColor.BLACK, "Zwart",Material.BLACK_SHULKER_BOX),
    WIT("white", ChatColor.WHITE, "Wit",Material.WHITE_SHULKER_BOX),
    ORANJE("orange", ChatColor.GOLD, "Oranje",Material.ORANGE_SHULKER_BOX),
    ROOD("red", ChatColor.RED, "Rood",Material.RED_SHULKER_BOX),
    MAGENTA("magenta", ChatColor.LIGHT_PURPLE, "Magenta",Material.MAGENTA_SHULKER_BOX),
    ROZE("pink", ChatColor.LIGHT_PURPLE, "Roze",Material.PINK_SHULKER_BOX),
    PAARS("purple", ChatColor.DARK_PURPLE, "Paars",Material.PURPLE_SHULKER_BOX),
    CYAN("cyan", ChatColor.DARK_AQUA, "Cyan",Material.CYAN_SHULKER_BOX),
    BRUIN("brown", ChatColor.GOLD, "Bruin",Material.BROWN_SHULKER_BOX),
    LICHTGRIJS("light-gray", ChatColor.GRAY, "Licht-Grijs",Material.LIGHT_GRAY_SHULKER_BOX),
    GRIJS("gray", ChatColor.DARK_GRAY, "Grijs",Material.GRAY_SHULKER_BOX),
    GEEL("yellow", ChatColor.YELLOW, "Geel",Material.YELLOW_SHULKER_BOX);

    private final String type;
    private final ChatColor color;
    private final String displayName;
    private final Material shulkerBlock;
    Colors(String type, ChatColor color, String displayName, Material shulkerBlock) {
        this.type = type;
        this.color = color;
        this.displayName = displayName;
        this.shulkerBlock = shulkerBlock;
    }

    public static Optional<Colors> getColorByType(String type) {
        for(Colors color : values()) {
            if(color.type.equals(type)) {
                return Optional.of(color);
            }
        }

        return Optional.empty();
    }

}
