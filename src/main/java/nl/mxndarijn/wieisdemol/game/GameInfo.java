package nl.mxndarijn.wieisdemol.game;

import lombok.Getter;
import lombok.Setter;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.wieisdemol.data.ConfigFiles;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.GameManager;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class GameInfo {

    private final List<UUID> queue;
    private String mapId;
    private UUID host;
    private LocalDateTime time;
    @Setter
    private UpcomingGameStatus status;

    private GameInfo() {
        queue = new ArrayList<>();
        status = UpcomingGameStatus.WAITING;
    }

    public static GameInfo create(Map map, UUID host, LocalDateTime time) {
        GameInfo game = new GameInfo();
        game.host = host;
        game.mapId = map.getDirectory().getName();
        game.time = time;

        return game;
    }

    public static Optional<GameInfo> loadFromFile(java.util.Map<String, Object> map) {
        GameInfo game = new GameInfo();
        game.host = UUID.fromString((String) map.get("host"));
        game.mapId = (String) map.get("mapId");
        game.time = LocalDateTime.parse((String) map.get("time"));

        if (game.time.isBefore(LocalDateTime.now()))
            return Optional.empty();


        Optional<Map> optionalMap = MapManager.getInstance().getMapById(game.mapId);
        if (optionalMap.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(game);
    }

    public java.util.Map<String, Object> getDataForSaving() {
        java.util.Map<String, Object> map = new HashMap<>();
        map.put("host", host.toString());
        map.put("mapId", mapId);
        map.put("time", time.toString());

        return map;
    }

    public ItemStack getItemStack(Player p) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = time.format(formatter);
        Optional<Map> optionalMap = MapManager.getInstance().getMapById(mapId);
        if (optionalMap.isEmpty()) {
            GameManager.getInstance().removeUpcomingGame(this);
        }
        Map map = optionalMap.get();
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData(map.getMapConfig().getPresetConfig().getSkullId())
                .setName("<gray>" + map.getMapConfig().getName())
                .addBlankLore()
                .addLore("<gray>Status: " + status.getStatus())
                .addLore("<gray>Host: " + Bukkit.getOfflinePlayer(map.getMapConfig().getOwner()).getName())
                .addBlankLore()
                .addLore("<gray>Speel-Moeilijkheid: " + map.getStars(map.getMapConfig().getPresetConfig().getPlayDifficulty()));

        Duration duration = Duration.between(time, LocalDateTime.now());
        Long minutes = Math.abs(duration.toMinutes());
        if (status.isCanJoinQueue()) {
            if (minutes < ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("time-before-queue-is-open-in-hours") * 60L) {
                if(time.isAfter(LocalDateTime.now())) {
                    builder.addBlankLore()
                            .addLore("<gray>Begint om: " + formattedTime + " (Over " + minutes + (minutes > 1 ? " minuten)" : " minuut)"))
                            .addLore("<gray>Aantal wachtend: " + queue.size());

                } else {
                    builder.addBlankLore()
                            .addLore("<gray>Begon om: " + formattedTime + "<red> (Tijd al geweest).")
                            .addLore("<gray>Aantal wachtend: " + queue.size());
                }

                if(queue.contains(p.getUniqueId())) {
                    builder.addLore("<yellow>Klik hier om uit de wachtrij te gaan.");
                } else {
                    builder.addLore("<yellow>Klik hier om in de wachtrij te komen.");
                }
                if (host == p.getUniqueId() || p.hasPermission(Permissions.ITEM_GAMES_MANAGE_OTHER_GAMES.getPermission())) {
                    builder.addLore("<yellow>Shift-Klik om de game te beheren.");
                }
            } else {
                builder.addBlankLore();
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEEE HH:mm", new Locale("nl", "NL"));
                String formattedTime1 = time.format(formatter1);
                builder.addLore("<gray>Begint om: " + formattedTime1);
            }
        } else {
            builder.addLore("<yellow>Klik hier om de game te spectaten.");
        }

        return builder.build();

    }

}
