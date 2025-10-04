package nl.mxndarijn.wieisdemol.map.mapscript;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Result of building a MapRoom. Bundles all components created by the room.
 * Now uses a builder to allow incremental construction.
 */
@Getter
public final class MapRoomResult {
    private final @NotNull MapRoom mapRoom;
    private final @NotNull List<Portal> portals;
    private final @NotNull List<RedstoneTrigger<?>> redstoneTriggers;
    private final @NotNull List<MapParameter<?>> mapParameters;
    private final @NotNull List<MapAction<?>> mapActions;

    private MapRoomResult(@NotNull MapRoom mapRoom,
                          @NotNull List<Portal> portals,
                          @NotNull List<RedstoneTrigger<?>> redstoneTriggers,
                          @NotNull List<MapParameter<?>> mapParameters,
                          @NotNull List<MapAction<?>> mapActions) {
        this.mapRoom = Objects.requireNonNull(mapRoom, "mapRoom");
        this.portals = List.copyOf(Objects.requireNonNull(portals, "portals"));
        this.redstoneTriggers = List.copyOf(Objects.requireNonNull(redstoneTriggers, "redstoneTriggers"));
        this.mapParameters = List.copyOf(Objects.requireNonNull(mapParameters, "mapParameters"));
        this.mapActions = List.copyOf(Objects.requireNonNull(mapActions, "mapActions"));
    }

    // ---- Builder ----
    public static @NotNull Builder builder(@NotNull MapRoom mapRoom) {
        return new Builder(mapRoom);
    }

    public static final class Builder {
        private final @NotNull MapRoom mapRoom;
        private final List<Portal> portals = new ArrayList<>();
        private final List<RedstoneTrigger<?>> redstoneTriggers = new ArrayList<>();
        private final List<MapParameter<?>> mapParameters = new ArrayList<>();
        private final List<MapAction<?>> mapActions = new ArrayList<>();

        private Builder(@NotNull MapRoom mapRoom) {
            this.mapRoom = Objects.requireNonNull(mapRoom, "mapRoom");
        }

        public @NotNull Builder setPortals(@NotNull Collection<Portal> portals) {
            this.portals.clear();
            this.portals.addAll(Objects.requireNonNull(portals, "portals"));
            return this;
        }
        public @NotNull Builder addPortal(@NotNull Portal portal) {
            this.portals.add(Objects.requireNonNull(portal, "portal"));
            return this;
        }
        public @NotNull Builder addPortals(@NotNull Collection<Portal> portals) {
            this.portals.addAll(Objects.requireNonNull(portals, "portals"));
            return this;
        }

        public @NotNull Builder setRedstoneTriggers(@NotNull Collection<RedstoneTrigger<?>> triggers) {
            this.redstoneTriggers.clear();
            this.redstoneTriggers.addAll(Objects.requireNonNull(triggers, "triggers"));
            return this;
        }
        public @NotNull Builder addRedstoneTrigger(@NotNull RedstoneTrigger<?> trigger) {
            this.redstoneTriggers.add(Objects.requireNonNull(trigger, "trigger"));
            return this;
        }
        public @NotNull Builder addRedstoneTriggers(@NotNull Collection<RedstoneTrigger<?>> triggers) {
            this.redstoneTriggers.addAll(Objects.requireNonNull(triggers, "triggers"));
            return this;
        }

        public @NotNull Builder setMapParameters(@NotNull Collection<MapParameter<?>> parameters) {
            this.mapParameters.clear();
            this.mapParameters.addAll(Objects.requireNonNull(parameters, "parameters"));
            return this;
        }
        public @NotNull Builder addMapParameter(@NotNull MapParameter<?> parameter) {
            this.mapParameters.add(Objects.requireNonNull(parameter, "parameter"));
            return this;
        }
        public @NotNull Builder addMapParameters(@NotNull Collection<MapParameter<?>> parameters) {
            this.mapParameters.addAll(Objects.requireNonNull(parameters, "parameters"));
            return this;
        }

        public @NotNull Builder setMapActions(@NotNull Collection<MapAction<?>> actions) {
            this.mapActions.clear();
            this.mapActions.addAll(Objects.requireNonNull(actions, "actions"));
            return this;
        }
        public @NotNull Builder addMapAction(@NotNull MapAction<?> action) {
            this.mapActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }
        public @NotNull Builder addMapActions(@NotNull Collection<MapAction<?>> actions) {
            this.mapActions.addAll(Objects.requireNonNull(actions, "actions"));
            return this;
        }

        public @NotNull MapRoomResult build() {
            return new MapRoomResult(mapRoom, portals, redstoneTriggers, mapParameters, mapActions);
            }
        }

    // ---- Backward-compatible factories ----
    public static MapRoomResult of(@NotNull MapRoom mapRoom,
                                   @NotNull List<Portal> portals,
                                   @NotNull List<RedstoneTrigger<?>> redstoneTriggers,
                                   @NotNull List<MapParameter<?>> mapParameters,
                                   @NotNull List<MapAction<?>> mapActions) {
        return builder(mapRoom)
                .setPortals(portals)
                .setRedstoneTriggers(redstoneTriggers)
                .setMapParameters(mapParameters)
                .setMapActions(mapActions)
                .build();
    }

    public static MapRoomResult empty(@NotNull MapRoom mapRoom) {
        return builder(mapRoom).build();
    }
}
