package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract RedstoneTrigger definition for map scripts.
 * <p>
 * Subclasses implement onActivate and onDeactivate to react to redstone changes.
 * Fields:
 * - triggers: list of block locations representing the redstone sources to watch
 * - mapRoom: back-reference to the owning room
 */
public abstract class RedstoneTrigger<T extends MapRoom> {

    @Getter
    private final @NotNull List<Location> triggers;
    @Getter
    private final @NotNull T mapRoom;

    protected RedstoneTrigger(@NotNull List<Location> triggers,
                              @NotNull T mapRoom) {
        this.triggers = Objects.requireNonNull(triggers, "triggers");
        this.mapRoom = Objects.requireNonNull(mapRoom, "mapRoom");
    }

    /**
     * Called when one of the trigger locations becomes powered/active.
     */
    public abstract void onActivate(@NotNull Location location, @NotNull Event event, @Nullable Player player);

    /**
     * Called when the trigger becomes unpowered/inactive.
     */
    public abstract void onDeactivate(@NotNull Location location, @NotNull Event event, @Nullable Player player);
}
