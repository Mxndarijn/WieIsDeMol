package nl.mxndarijn.wieisdemol.managers.chests;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ContainerType {
    CHEST(Material.CHEST, "Chest"),
    TRAPPED_CHEST(Material.TRAPPED_CHEST, "Trapped Chest"),
    HOPPER(Material.HOPPER, "Hopper"),
    DROPPER(Material.DROPPER, "Dropper"),
    BARREL(Material.BARREL, "Barrel"),
    DISPENSER(Material.DISPENSER, "Dispenser");

    private final Material icon;
    private final String display;

    ContainerType(Material icon, String display) {
        this.icon = icon;
        this.display = display;
    }

    public static Optional<ContainerType> fromMaterial(Material material) {
        if (material == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(t -> t.matches(material))
                .findFirst();
    }

    public boolean matches(Material material) {
        if (this == CHEST) return material == Material.CHEST;
        if (this == TRAPPED_CHEST) return material == Material.TRAPPED_CHEST;
        if (this == HOPPER) return material == Material.HOPPER;
        if (this == DROPPER) return material == Material.DROPPER;
        if (this == DISPENSER) return material == Material.DISPENSER;
        return false;
    }

    public static boolean isSupported(Material material) {
        return fromMaterial(material).isPresent();
    }

    public static Optional<ContainerType> fromBlock(Block block) {
        if (block == null) return Optional.empty();
        return fromMaterial(block.getType());
    }
}
