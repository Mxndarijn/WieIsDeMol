package nl.mxndarijn.wieisdemol.managers.world;

import nl.mxndarijn.wieisdemol.data.SpecialDirectories;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.game.Game;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class GameWorldManager {
    private static GameWorldManager instance;
    private ArrayList<Game> games;
    public static GameWorldManager getInstance() {
        if(instance == null) {
            instance = new GameWorldManager();
        }
        return instance;
    }

    private GameWorldManager() {
        this.games = new ArrayList<>();
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

    public void addGame(Game game) {
        this.games.add(game);
    }

    public Optional<Game> getGameByWorldUID(UUID uid) {
        for (Game game : games) {
            if (game.getMxWorld().isPresent()) {
                if (game.getMxWorld().get().getWorldUID().equals(uid)) {
                    return Optional.of(game);
                }
            }
        }
        return Optional.empty();
    }
}
