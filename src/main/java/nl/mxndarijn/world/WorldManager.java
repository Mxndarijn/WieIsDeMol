package nl.mxndarijn.world;

import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

public class WorldManager {
    private static WorldManager instance;
    private final MxAtlas atlas;
    private HashMap<UUID, List<MxWorld>> playersMap;
    private HashMap<String, MxWorld> presets;

    public static WorldManager getInstance() {
        if(instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    private WorldManager() {
        atlas = MxAtlas.getInstance();
        playersMap = new HashMap<>();
        presets = new HashMap<>();
        deleteGameWorlds();
        loadMaps();
        loadPresets();

    }

    private void loadMaps() {
        Logger.logMessage(LogLevel.Information, Prefix.WORLD_MANAGER, "Loading maps...");
        Arrays.stream(Objects.requireNonNull(SpecialDirectories.MAP_WORLDS.getDirectory().listFiles())).forEach(file -> {
            if(file.isDirectory()) {
                List<MxWorld> list = atlas.loadFolder(file);
                playersMap.put(UUID.fromString(file.getName()),list);
            }
        });
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

    private void loadPresets() {
        List<MxWorld> list = atlas.loadFolder(SpecialDirectories.PRESET_WORLDS.getDirectory());
        list.forEach(mxWorld -> {
            presets.put(mxWorld.getUUID(), mxWorld);
        });
    }

    public HashMap<UUID, List<MxWorld>> getPlayersMap() {
        return playersMap;
    }

    public Optional<MxWorld> getPresetById(String id) {
        return presets.containsKey(id) ? Optional.of(presets.get(id)) : Optional.empty();
    }
}
