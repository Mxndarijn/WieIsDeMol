package nl.mxndarijn.wieisdemol.map.mapscript.atla;

import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.map.mapscript.*;
import nl.mxndarijn.wieisdemol.map.mapscript.atla.startingroom.AtlaStartingRoom;

import java.util.List;

public class AtlaMapScript extends MapScript {

    public AtlaMapScript() {
        super();
        Logger.logMessage("Loaded AtlaMapScript");
    }

    public AtlaMapScript(Game game) {
        super(game);
        Logger.logMessage("Loaded AtlaMapScript game");
    }

    @Override
    protected List<Class<? extends MapRoom>> createMapRoomClasses() {
        return List.of(AtlaStartingRoom.class);
    }
}
