package nl.mxndarijn.wieisdemol.map.mapscript;

import nl.mxndarijn.wieisdemol.map.mapscript.atla.AtlaMapScript;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Registry of available MapScripts with a human-friendly id and the backing class.
 */
public enum MapScripts {
    ATLA("The Last Airbender", AtlaMapScript.class);

    private final String id;
    private final Class<? extends MapScript> scriptClass;

    MapScripts(@NotNull String id, @NotNull Class<? extends MapScript> scriptClass) {
        this.id = id;
        this.scriptClass = scriptClass;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Class<? extends MapScript> getScriptClass() {
        return scriptClass;
    }

    public static Optional<MapScripts> byId(@NotNull String id) {
        return Arrays.stream(values()).filter(e -> e.id.equalsIgnoreCase(id)).findFirst();
    }
}
