package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstract representation of an action that can be performed in a map.
 * Holds a typed reference to its owning MapRoom for compile-time safety.
 *
 * Subclasses must provide:
 * - an ItemStack representation via createItemStack()
 * - an activation handler via onActivate(Event, Player)
 */
public abstract class MapAction<T extends MapRoom> {

    @Getter
    private final @NotNull T mapRoom;

    protected MapAction(@NotNull T mapRoom) {
        this.mapRoom = Objects.requireNonNull(mapRoom, "mapRoom");
    }

    /**
     * Called when this action is activated.
     */
    public abstract void onActivate(@NotNull Event event, @Nullable Player player);

    /**
     * Create an ItemStack that represents this action in a GUI or inventory.
     */
    public abstract @NotNull MxSkullItemStackBuilder createItemStack();
}
