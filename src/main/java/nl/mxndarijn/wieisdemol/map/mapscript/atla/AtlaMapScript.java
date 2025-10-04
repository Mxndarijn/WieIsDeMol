package nl.mxndarijn.wieisdemol.map.mapscript.atla;

import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.map.mapscript.*;
import nl.mxndarijn.wieisdemol.map.mapscript.atla.bumichallenge.AtlaBumiChallenge;
import nl.mxndarijn.wieisdemol.map.mapscript.atla.fishsacrifice.AtlaFishSacrifice;
import nl.mxndarijn.wieisdemol.map.mapscript.atla.startingroom.AtlaStartingRoom;

import java.io.File;
import java.util.List;

public class AtlaMapScript extends MapScript {

    public AtlaMapScript(File file) {
        super(file);
        Logger.logMessage("Loaded AtlaMapScript");
    }

    public AtlaMapScript(File file,Game game) {
        super(file,game);
        Logger.logMessage("Loaded AtlaMapScript game");
    }

    @Override
    protected List<Class<? extends MapRoom>> createMapRoomClasses() {
        return List.of(
                AtlaStartingRoom.class,
                AtlaBumiChallenge.class,
                AtlaFishSacrifice.class
        );
    }
}
