package nl.mxndarijn.wieisdemol.map.mapscript;

import nl.mxndarijn.api.mxworld.MxLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Portal definition for map scripts.
 * <p>
 * Fields:
 * - corner1, corner2: opposite corners of a cuboid region (inclusive) that defines the portal area
 * - teleportsTo: list of target locations this portal can teleport to (selection logic handled elsewhere)
 * - applicableTo: which players are affected by this portal
 * - shouldReceiveFallDamage: whether players should receive fall damage after teleporting
 * - mapRoom: back-reference to the room that owns this portal
 */
public record Portal(@NotNull MxLocation corner1,
                     @NotNull MxLocation corner2,
                     @NotNull List<MxLocation> teleportsTo,
                     @NotNull MapPlayerType applicableTo,
                     boolean shouldReceiveFallDamage,
                     @NotNull MapRoom mapRoom) {
    public Portal(@NotNull MxLocation corner1,
                  @NotNull MxLocation corner2,
                  @NotNull List<MxLocation> teleportsTo,
                  @NotNull MapPlayerType applicableTo,
                  boolean shouldReceiveFallDamage,
                  @NotNull MapRoom mapRoom) {
        this.corner1 = Objects.requireNonNull(corner1, "corner1");
        this.corner2 = Objects.requireNonNull(corner2, "corner2");
        this.teleportsTo = Objects.requireNonNull(teleportsTo, "teleportsTo");
        this.applicableTo = Objects.requireNonNull(applicableTo, "applicableTo");
        this.shouldReceiveFallDamage = shouldReceiveFallDamage;
        this.mapRoom = Objects.requireNonNull(mapRoom, "mapRoom");
    }
}
