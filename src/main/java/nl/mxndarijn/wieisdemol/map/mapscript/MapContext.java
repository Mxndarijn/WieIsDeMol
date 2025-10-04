package nl.mxndarijn.wieisdemol.map.mapscript;

import nl.mxndarijn.wieisdemol.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Context for a MapScript/MapRoom tree.
 * Holds optional runtime Game reference and whether we are in edit mode.
 */
public record MapContext(@NotNull Optional<Game> game, boolean editMode) {

    public boolean hasGame() {
        return game.isPresent();
    }

    public @NotNull Optional<Game> getGame() {
        return game;
    }

    public @NotNull Game requireGame() {
        return game.orElseThrow(() -> new IllegalStateException("This MapScript/Room has no Game (edit mode)."));
    }
}
