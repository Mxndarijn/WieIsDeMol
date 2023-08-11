package nl.mxndarijn.wieisdemol.managers.gamemanager;

import nl.mxndarijn.wieisdemol.data.ConfigFiles;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if(instance == null)
            instance = new GameManager();
        return instance;
    }


    private List<UpcomingGame> upcomingGameList;
    private final ConfigFiles config;
    private GameManager() {
        config = ConfigFiles.UPCOMING_GAMES;
        loadGames();
    }

    public void removeAllGamesWithMap(Map map) {
        List<UpcomingGame> gamesToRemove = new ArrayList<>();

        for (UpcomingGame game : upcomingGameList) {
            if (game.getMapId().equals(map.getDirectory().getName())) {
                gamesToRemove.add(game);
            }
        }
        upcomingGameList.removeAll(gamesToRemove);
    }

    private void loadGames() {
        upcomingGameList = new ArrayList<>();
        ConfigurationSection section = config.getFileConfiguration();
        List<java.util.Map<?, ?>> list = section.getMapList("upcoming-games");
        list.forEach(map -> {
            java.util.Map<String, Object> convertedMap = (java.util.Map<String, Object>) map;
            Optional<UpcomingGame> game = UpcomingGame.loadFromFile(convertedMap);
            game.ifPresent(upcomingGameList::add);
        });
    }

    public void addUpcomingGame(UUID host, Map map, LocalDateTime date) {
        UpcomingGame upcomingGame = UpcomingGame.create(map, host, date);
            upcomingGameList.add(upcomingGame);
    }

    public void save() {

        List<java.util.Map<String, Object>> list = new ArrayList<>();
        upcomingGameList.forEach(upcomingGame -> {
            list.add(upcomingGame.getDataForSaving());
        });

        config.getFileConfiguration().set("upcoming-games", list);
    }

    public List<ItemStack> getUpcomingGamesItemStacks(Player p) {
        return upcomingGameList.stream()
                .map(upcomingGame -> upcomingGame.getItemStack(p))
                .collect(Collectors.toList());
    }

    public void removeUpcomingGame(UpcomingGame upcomingGame) {
        upcomingGameList.remove(upcomingGame);
    }
}
