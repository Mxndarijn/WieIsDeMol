package nl.mxndarijn.wieisdemol.map.mapscript.atla.fishsacrifice;

import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.wieisdemol.map.mapscript.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AtlaFishSacrifice extends MapRoom {


    public AtlaFishSacrifice(@NotNull MapScript mapScript) {
        super(mapScript);
    }

    @Override
    public @NotNull MapRoomResult build() {
        return MapRoomResult.builder(this)
                .addMapParameter(new MapParameter.NumberParam(this, "fish_sacrifice_amount", "Aantal Spelers die dood moeten in deze kamer?", 1))
                .build();
    }

    @Override
    public void mapSetup() {

    }

    @Override
    public void mapUnload() {

    }

    @Override
    public void gameSetup() {

    }

    @Override
    public void gameUnload() {

    }

    @Override
    public @NotNull String getTitle() {
        return "Bumi Challenge";
    }

}
