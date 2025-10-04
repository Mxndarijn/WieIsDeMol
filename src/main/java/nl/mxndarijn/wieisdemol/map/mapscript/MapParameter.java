package nl.mxndarijn.wieisdemol.map.mapscript;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * MapParameter as a sealed, typed hierarchy (Java 21).
 * Each subtype represents a concrete parameter type with a strongly-typed default value,
 * and includes a back-reference to its MapRoom.
 */
public sealed interface MapParameter<T> permits MapParameter.StringParam, MapParameter.NumberParam, MapParameter.DecimalParam, MapParameter.BooleanParam, MapParameter.LocationParam {

    @NotNull MapParameterType getType();
    @NotNull String getId();
    @NotNull String getDescription();
    @Nullable T getDefaultValue();
    @NotNull MapRoom getMapRoom();

    // ---- Factories (ergonomic) ----
    static MapParameter<String> stringParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable String defaultValue) {
        return new StringParam(mapRoom, id, description, defaultValue);
    }

    static MapParameter<Integer> numberParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, int defaultValue) {
        return new NumberParam(mapRoom, id, description, defaultValue);
    }

    static MapParameter<Double> decimalParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, double defaultValue) {
        return new DecimalParam(mapRoom, id, description, defaultValue);
    }

    static MapParameter<Boolean> booleanParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, boolean defaultValue) {
        return new BooleanParam(mapRoom, id, description, defaultValue);
    }

    static MapParameter<Location> locationParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable Location defaultValue) {
        return new LocationParam(mapRoom, id, description, defaultValue);
    }

    // ---- Convenience typed Optional accessors (compat-friendly) ----
    default Optional<String> defaultAsString() {
        return this instanceof StringParam sp ? Optional.ofNullable(sp.defaultValue()) : Optional.empty();
    }
    default Optional<Integer> defaultAsInt() {
        return this instanceof NumberParam np ? Optional.ofNullable(np.defaultValue()) : Optional.empty();
    }
    default Optional<Double> defaultAsDouble() {
        return this instanceof DecimalParam dp ? Optional.ofNullable(dp.defaultValue()) : Optional.empty();
    }
    default Optional<Boolean> defaultAsBoolean() {
        return this instanceof BooleanParam bp ? Optional.ofNullable(bp.defaultValue()) : Optional.empty();
    }
    default Optional<Location> defaultAsLocation() {
        return this instanceof LocationParam lp ? Optional.ofNullable(lp.defaultValue()) : Optional.empty();
    }

    // ---- Subtypes ----
    record StringParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable String defaultValue) implements MapParameter<String> {
        @Override public @NotNull MapParameterType getType() { return MapParameterType.STRING; }
        @Override public @NotNull String getId() { return id; }
        @Override public @NotNull String getDescription() { return description; }
        @Override public @Nullable String getDefaultValue() { return defaultValue; }
        @Override public @NotNull MapRoom getMapRoom() { return mapRoom; }
    }

    record NumberParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable Integer defaultValue) implements MapParameter<Integer> {
        @Override public @NotNull MapParameterType getType() { return MapParameterType.NUMBER; }
        @Override public @NotNull String getId() { return id; }
        @Override public @NotNull String getDescription() { return description; }
        @Override public @Nullable Integer getDefaultValue() { return defaultValue; }
        @Override public @NotNull MapRoom getMapRoom() { return mapRoom; }
    }

    record DecimalParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable Double defaultValue) implements MapParameter<Double> {
        @Override public @NotNull MapParameterType getType() { return MapParameterType.DECIMAL; }
        @Override public @NotNull String getId() { return id; }
        @Override public @NotNull String getDescription() { return description; }
        @Override public @Nullable Double getDefaultValue() { return defaultValue; }
        @Override public @NotNull MapRoom getMapRoom() { return mapRoom; }
    }

    record BooleanParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable Boolean defaultValue) implements MapParameter<Boolean> {
        @Override public @NotNull MapParameterType getType() { return MapParameterType.BOOLEAN; }
        @Override public @NotNull String getId() { return id; }
        @Override public @NotNull String getDescription() { return description; }
        @Override public @Nullable Boolean getDefaultValue() { return defaultValue; }
        @Override public @NotNull MapRoom getMapRoom() { return mapRoom; }
    }

    record LocationParam(@NotNull MapRoom mapRoom, @NotNull String id, @NotNull String description, @Nullable Location defaultValue) implements MapParameter<Location> {
        @Override public @NotNull MapParameterType getType() { return MapParameterType.LOCATION; }
        @Override public @NotNull String getId() { return id; }
        @Override public @NotNull String getDescription() { return description; }
        @Override public @Nullable Location getDefaultValue() { return defaultValue; }
        @Override public @NotNull MapRoom getMapRoom() { return mapRoom; }
    }
}
