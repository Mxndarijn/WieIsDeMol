package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.wieisdemol.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract representation of a room in a map.
 * Now a room only keeps a reference to its owning MapScript.
 * Concrete rooms will build their components via build().
 */
@Getter
public abstract class MapRoom implements Listener {
    private final @NotNull MapScript mapScript;

    protected MapRoom(@NotNull MapScript mapScript) {
        this.mapScript = Objects.requireNonNull(mapScript, "mapScript");
    }

    /**
     * Access the MapContext from the owning script.
     */
    public final @NotNull MapContext getContext() {
        return mapScript.getContext();
    }

    /**
     * Convenience: check/access the Game from this room via context.
     */
    public final boolean hasGame() { return getContext().hasGame(); }
    public final Optional<Game> getGame() { return getContext().getGame(); }
    public final Game requireGame() { return getContext().requireGame(); }

    /**
     * Convenience: safely view the owning MapScript as a specific subtype.
     * Example: getMapScriptAs(AtlaMapScript.class).ifPresent(atla -> { ... })
     */
    public final <T extends MapScript> Optional<T> getMapScriptAs(@NotNull Class<T> type) {
        Objects.requireNonNull(type, "type");
        return type.isInstance(mapScript) ? Optional.of(type.cast(mapScript)) : Optional.empty();
    }

    /**
     * Convenience: require the owning MapScript to be of a specific subtype, else throw IllegalStateException.
     */
    public final <T extends MapScript> T requireMapScript(@NotNull Class<T> type) {
        return getMapScriptAs(type).orElseThrow(() -> new IllegalStateException("MapRoom's MapScript is not of type " + type.getName() + ", but was " + mapScript.getClass().getName()));
    }

    /**
     * Build/setup this room and return its components bundle.
     */
    public abstract @NotNull MapRoomResult build();

    /**
     * Optional lifecycle hooks. Subclasses may override as needed.
     */
    public abstract void mapSetup();
    public abstract void mapUnload();
    public abstract void gameSetup();
    public abstract void gameUnload();

    public abstract @NotNull String getTitle();

    protected Optional<MxWorld> getMxWorld() {
        return this.mapScript.getMxWorld();
    }

    protected Optional<World> getWorld() {
        Optional<MxWorld> optionalMxWorld = this.getMxWorld();
        if(optionalMxWorld.isEmpty()) {
            return Optional.empty();
        }
        World w = Bukkit.getWorld(optionalMxWorld.get().getWorldUID());
        if(w == null) {
            return Optional.empty();
        }
        return Optional.of(w);
    }
}
