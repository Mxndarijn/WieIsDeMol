package nl.mxndarijn.wieisdemol.managers.world;

import nl.mxndarijn.wieisdemol.data.SpecialDirectories;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

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
        Logger.logMessage(LogLevel.INFORMATION, Prefix.WORLD_MANAGER, "Deleting old game worlds...");
        try {
            FileUtils.deleteDirectory(SpecialDirectories.GAMES_WORLDS.getDirectory());
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.WORLD_MANAGER, "Could not delete current game worlds");
            e.printStackTrace();
        }
        SpecialDirectories.GAMES_WORLDS.getDirectory().mkdirs();
    }
}
