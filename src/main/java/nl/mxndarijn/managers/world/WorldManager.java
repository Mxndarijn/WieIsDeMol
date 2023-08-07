package nl.mxndarijn.managers.world;

import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.logic.util.logger.Prefix;
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
