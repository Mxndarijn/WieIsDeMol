package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.wieisdemol.game.Game;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class MapScript implements EventListener {

    private final MapContext context;

    private final List<Portal> portals;
    private final List<RedstoneTrigger<?>> redstoneTriggers;
    private final List<MapRoom> mapRooms;
    private final List<MapParameter<?>> mapParameters;
    private final List<MapAction<?>> mapActions;

    // No-arg constructor: edit/design mode (no Game)
    protected MapScript() {
        this(new MapContext(Optional.empty(), true));
    }

    // Runtime constructor: with Game
    protected MapScript(@NotNull Game game) {
        this(new MapContext(Optional.of(Objects.requireNonNull(game, "game")), false));
    }

    // Centralized constructor to reduce duplication
    protected MapScript(@NotNull MapContext context) {
        this.context = Objects.requireNonNull(context, "context");
        Aggregation agg = aggregate();
        this.mapRooms = agg.mapRooms();
        this.portals = agg.portals();
        this.redstoneTriggers = agg.redstoneTriggers();
        this.mapParameters = agg.mapParameters();
        this.mapActions = agg.mapActions();
    }

    // Existing constructor retained for explicit injection
    protected MapScript(List<Portal> portals, List<RedstoneTrigger<?>> redstoneTriggers, List<MapRoom> mapRooms, List<MapParameter<?>> mapParameters, List<MapAction<?>> mapActions) {
        this.context = new MapContext(Optional.empty(), true);
        this.portals = portals;
        this.redstoneTriggers = redstoneTriggers;
        this.mapRooms = mapRooms;
        this.mapParameters = mapParameters;
        this.mapActions = mapActions;
    }

    // Convenience accessors for Game via context
    public final boolean hasGame() { return context.hasGame(); }
    public final Optional<Game> getGame() { return context.getGame(); }
    public final Game requireGame() { return context.requireGame(); }

    // Subclasses provide their room class definitions; MapScript handles instantiation and aggregation
    protected abstract List<Class<? extends MapRoom>> createMapRoomClasses();

    // Optional lifecycle hooks at script level; delegate to rooms by default
    public void mapSetup() {
        for (MapRoom room : mapRooms) room.mapSetup();
    }

    public void mapUnload() {
        for (MapRoom room : mapRooms) room.mapUnload();
    }

    public void gameSetup() {
        if (!hasGame()) return; // edit mode: no wiring
        for (MapRoom room : mapRooms) room.gameSetup();
    }

    public void gameUnload() {
        if (!hasGame()) return; // edit mode: no-op
        for (MapRoom room : mapRooms) room.gameUnload();
    }

    // ---- Internal helpers to avoid duplication ----
    private Aggregation aggregate() {
        List<Class<? extends MapRoom>> roomClasses = createMapRoomClasses();
        List<MapRoom> rooms = roomClasses.stream().map(cls -> {
            try {
                return cls.getDeclaredConstructor(MapScript.class).newInstance(this);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to instantiate MapRoom " + cls.getName() + ": ensure it has a constructor accepting MapScript", e);
            }
        }).collect(Collectors.toList());

        List<MapRoomResult> results = rooms.stream().map(MapRoom::build).toList();

        List<Portal> portals = results.stream().flatMap(r -> r.getPortals().stream()).collect(Collectors.toList());
        List<RedstoneTrigger<?>> redstoneTriggers = results.stream().flatMap(r -> r.getRedstoneTriggers().stream()).collect(Collectors.toList());
        List<MapParameter<?>> mapParameters = results.stream().flatMap(r -> r.getMapParameters().stream()).collect(Collectors.toList());
        List<MapAction<?>> mapActions = results.stream().flatMap(r -> r.getMapActions().stream()).collect(Collectors.toList());

        return new Aggregation(rooms, portals, redstoneTriggers, mapParameters, mapActions);
    }

    public Optional<MxWorld> getMxWorld() {
        if(this.context.getGame().isEmpty()) {
            return Optional.empty();
        }
        Game game = this.context.getGame().get();
        return game.getMxWorld();

    }

    private record Aggregation(List<MapRoom> mapRooms,
                               List<Portal> portals,
                               List<RedstoneTrigger<?>> redstoneTriggers,
                               List<MapParameter<?>> mapParameters,
                               List<MapAction<?>> mapActions) {}
}
