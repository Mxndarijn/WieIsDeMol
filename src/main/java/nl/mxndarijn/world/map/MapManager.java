package nl.mxndarijn.world.map;

import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.presets.Preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class MapManager {
    private static MapManager instance;

    private ArrayList<Map> maps;

    public static MapManager getInstance() {
        if(instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    private MapManager() {
        maps = new ArrayList<>();
        Arrays.stream(SpecialDirectories.MAP_WORLDS.getDirectory().listFiles()).forEach(file -> {
            if(file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(mapFile -> {
                    Optional<Map> optionalMap = Map.create(mapFile);
                    if(optionalMap.isPresent()) {
                        Logger.logMessage(LogLevel.Information, Prefix.MAPS_MANAGER, "Map Added: " + mapFile.getName());
                        maps.add(optionalMap.get());
                    } else {
                        Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not load map: " + mapFile.getName());
                    }
                });
            }
        });
    }

    public ArrayList<Map> getAllMaps() {
        return maps;
    }

    public Optional<Map> getMapById(String s) {
        for(Map m : maps) {
            if(m.getDirectory().getName().equals(s)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public Optional<Map> getMapByWorldUID(UUID uid) {
        for(Map m : maps) {
            if(m.getMxWorld().isPresent()) {
                MxWorld mxWorld = m.getMxWorld().get();
                if(mxWorld.isLoaded()) {
                    if(mxWorld.getWorldUID() == uid) {
                        return Optional.of(m);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void addMap(Map map) {
        maps.add(map);
    }
}
