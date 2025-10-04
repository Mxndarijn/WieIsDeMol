package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.events.*;
import nl.mxndarijn.wieisdemol.map.mapscript.manager.PortalManager;
import nl.mxndarijn.wieisdemol.map.mapscript.manager.RedstoneTriggerManager;
import nl.mxndarijn.wieisdemol.map.mapscript.manager.ScriptParameterManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class MapScript implements Listener {

    private final MapContext context;
    @Getter
    private final ScriptParameterManager scriptParameterManager;

    private final List<Portal> portals;
    private final List<RedstoneTrigger<?>> redstoneTriggers;
    private final List<MapRoom> mapRooms;
    private final List<MapParameter<?>> mapParameters;
    private final List<MapAction<?>> mapActions;

    private List<Listener> events;

    // No-arg constructor: edit/design mode (no Game)
    protected MapScript(File scriptParamFile) {
        this(scriptParamFile, new MapContext(Optional.empty(), true));
    }

    // Runtime constructor: with Game
    protected MapScript(File scriptParamFile, @NotNull Game game) {
        this(scriptParamFile, new MapContext(Optional.of(Objects.requireNonNull(game, "game")), false));
    }

    // Centralized constructor to reduce duplication
    protected MapScript(File scriptParamFile, @NotNull MapContext context) {
        Logger.logMessage("Loaded MapScript");
        this.context = Objects.requireNonNull(context, "context");
        Aggregation agg = aggregate();
        this.mapRooms = agg.mapRooms();
        this.portals = agg.portals();
        this.redstoneTriggers = agg.redstoneTriggers();
        this.mapParameters = agg.mapParameters();
        this.mapActions = agg.mapActions();
        this.scriptParameterManager = new ScriptParameterManager(this, scriptParamFile);
    }

    // Existing constructor retained for explicit injection
    protected MapScript(File scriptParamFile, List<Portal> portals, List<RedstoneTrigger<?>> redstoneTriggers, List<MapRoom> mapRooms, List<MapParameter<?>> mapParameters, List<MapAction<?>> mapActions) {
        this.context = new MapContext(Optional.empty(), true);
        this.portals = portals;
        this.redstoneTriggers = redstoneTriggers;
        this.mapRooms = mapRooms;
        this.mapParameters = mapParameters;
        this.mapActions = mapActions;
        this.scriptParameterManager = new ScriptParameterManager(this, scriptParamFile);
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
        registerEvents();
        for (MapRoom room : mapRooms) room.gameSetup();
    }

    public void gameUnload() {
        if (!hasGame()) return; // edit mode: no-op
        for (MapRoom room : mapRooms) room.gameUnload();
        unregisterEvents();
    }

    // ---- Internal helpers to avoid duplication ----
    private Aggregation aggregate() {
        Logger.logMessage("Aggregating MapScript");
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

        Logger.logMessage("Aggregated MapScript");
        return new Aggregation(rooms, portals, redstoneTriggers, mapParameters, mapActions);
    }

    public Optional<MxWorld> getMxWorld() {
        if(this.context.getGame().isEmpty()) {
            return Optional.empty();
        }
        Game game = this.context.getGame().get();
        return game.getMxWorld();

    }

    public void registerEvents() {
        unregisterEvents();
        if (!hasGame()) return;
        JavaPlugin plugin = requireGame().getPlugin();
        events = new ArrayList<>();
        events.add(this);
        events.addAll(mapRooms);
        events.forEach(e -> Bukkit.getPluginManager().registerEvents(e, plugin));

        Game game = requireGame();
        events.add(new PortalManager(this, game, plugin));
        events.add(new RedstoneTriggerManager(game, plugin, this));
    }

    public void unregisterEvents() {
        if (events == null) return;
        events.forEach(HandlerList::unregisterAll);
    }

    private record Aggregation(List<MapRoom> mapRooms,
                               List<Portal> portals,
                               List<RedstoneTrigger<?>> redstoneTriggers,
                               List<MapParameter<?>> mapParameters,
                               List<MapAction<?>> mapActions) {}
}
