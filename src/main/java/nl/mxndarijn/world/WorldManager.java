package nl.mxndarijn.world;

import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.map.Map;
import nl.mxndarijn.world.map.MapManager;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetsManager;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

public class WorldManager {
    private static WorldManager instance;

    public static WorldManager getInstance() {
        if(instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    private WorldManager() {
        deleteGameWorlds();

    }

    private void deleteGameWorlds() {
        Logger.logMessage(LogLevel.Information, Prefix.WORLD_MANAGER, "Deleting old game worlds...");
        try {
            FileUtils.deleteDirectory(SpecialDirectories.GAMES_WORLDS.getDirectory());
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.WORLD_MANAGER, "Could not delete current game worlds");
            e.printStackTrace();
        }
        SpecialDirectories.GAMES_WORLDS.getDirectory().mkdirs();
    }
}
