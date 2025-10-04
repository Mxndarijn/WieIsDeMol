package nl.mxndarijn.wieisdemol.map.mapscript.atla.bumichallenge;

import nl.mxndarijn.api.builders.ArmorStandHelper;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.map.mapscript.*;
import nl.mxndarijn.wieisdemol.map.mapscript.atla.startingroom.AtlaActionStartingRoomTeleportAppa;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AtlaBumiChallenge extends MapRoom {


    public AtlaBumiChallenge(@NotNull MapScript mapScript) {
        super(mapScript);
    }

    @Override
    public @NotNull MapRoomResult build() {
        return MapRoomResult.builder(this)
                //Portal to Easy
                .addPortal(new Portal(
                        new MxLocation(-165, 56, -166),
                        new MxLocation(-160, 66, -166),
                        List.of(new MxLocation(-128, 62, -180, -180, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
                //Portal to Medium
                .addPortal(new Portal(
                        new MxLocation(-158, 56, -166),
                        new MxLocation(-153, 65, -166),
                        List.of(new MxLocation(-187, 61, -171, -180, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
                //Portal to Hard
                .addPortal(new Portal(
                        new MxLocation(-151, 56, -166),
                        new MxLocation(-146, 66, -166),
                        List.of(new MxLocation(-171, 59, -236, -180, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
                //Portal from Hard
                .addPortal(new Portal(
                        new MxLocation(-172, 59, -293),
                        new MxLocation(-170, 63, -293),
                        List.of(new MxLocation(-95, 56, -134, -180, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
                //Portal from Easy
                .addPortal(new Portal(
                        new MxLocation(-129, 61, -227),
                        new MxLocation(-127, 65, -227),
                        List.of(new MxLocation(-8, 56, -132, 90, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
                //Portal from Medium
                .addPortal(new Portal(
                        new MxLocation(-189, 60, -218),
                        new MxLocation(-185, 68, -218),
                        List.of(new MxLocation(-52, 56, -181, 0, 0)),
                        MapPlayerType.ALL,
                        false,
                        this
                ))
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
