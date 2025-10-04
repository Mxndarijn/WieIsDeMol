package nl.mxndarijn.wieisdemol.game;

import lombok.Getter;
import lombok.Setter;
import nl.mxndarijn.api.mxscoreboard.MxSupplierScoreBoard;
import nl.mxndarijn.wieisdemol.data.PeacekeeperLoot;
import nl.mxndarijn.wieisdemol.data.ScoreBoard;
import nl.mxndarijn.wieisdemol.managers.ScoreBoardManager;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class GamePlayer {

    private final boolean canReborn;
    @Setter
    @Getter
    private MapPlayer mapPlayer;
    @Setter
    @Getter
    private Optional<UUID> player;
    @Setter
    @Getter
    private boolean alive;
    @Setter
    @Getter
    private MxSupplierScoreBoard scoreboard;
    @Setter
    @Getter
    private boolean beginChestOpened;
    @Setter
    @Getter
    private boolean peacekeeperChestOpened;
    @Setter
    @Getter
    private Game game;
    @Setter
    @Getter
    private JavaPlugin plugin;
    @Setter
    @Getter
    private Optional<GamePlayer> votedOn;

    public GamePlayer(MapPlayer mapPlayer, JavaPlugin plugin, Game game) {
        this.mapPlayer = mapPlayer;
        this.player = Optional.empty();
        this.game = game;
        this.plugin = plugin;
        alive = true;
        this.beginChestOpened = false;
        this.peacekeeperChestOpened = false;
        this.votedOn = Optional.empty();
        this.canReborn = !mapPlayer.isPeacekeeper();
        String host = Bukkit.getOfflinePlayer(game.getMainHost()).getName();
        scoreboard = new MxSupplierScoreBoard(plugin, () -> {
            if(player.isPresent()) {
                Player p = Bukkit.getPlayer(player.get());
                if (p != null) {
                    return PlaceholderAPI.setPlaceholders(p, ScoreBoard.GAME_HOST.getTitle(new HashMap<>() {{
                        put("%%map_name%%", game.getConfig().getPresetConfig().getName());
                    }}));
                }
            }
            return ScoreBoard.GAME_HOST.getTitle(new HashMap<>() {{
                put("%%map_name%%", game.getConfig().getPresetConfig().getName());
            }});
        }, () -> {
            if(player.isPresent()) {
                Player p = Bukkit.getPlayer(player.get());
                if (p != null) {
                    return PlaceholderAPI.setPlaceholders(p, ScoreBoard.GAME_PLAYER.getLines(new HashMap<>() {{
                        put("%%game_status%%", game.getGameInfo().getStatus().getStatus());
                        put("%%game_time%%", game.getGameTime());
                        put("%%color%%", mapPlayer.getColor().getDisplayName());
                        put("%%host%%", host);
                        put("%%role%%", (beginChestOpened ? (peacekeeperChestOpened ? mapPlayer.getRoleDisplayString() : mapPlayer.getRoleDisplayWithoutPeacekeeper()) : "<gray>Onbekend"));
                    }}));
                }
            }
            return ScoreBoard.GAME_PLAYER.getLines(new HashMap<>() {{
                put("%%game_status%%", game.getGameInfo().getStatus().getStatus());
                put("%%game_time%%", game.getGameTime());
                put("%%color%%", mapPlayer.getColor().getDisplayName());
                put("%%host%%", host);
                put("%%role%%", (beginChestOpened ? (peacekeeperChestOpened ? mapPlayer.getRoleDisplayString() : mapPlayer.getRoleDisplayWithoutPeacekeeper()) : "<gray>Onbekend"));
            }});
        });
        scoreboard.setUpdateTimer(10);
    }

    public void setPlayingPlayer(UUID player) {
        this.player.ifPresent(p -> {
            scoreboard.removePlayer(p);
        });
        if (player != null) {
            this.player = Optional.of(player);
            ScoreBoardManager.getInstance().setPlayerScoreboard(player, scoreboard);
        } else {
            this.player = Optional.empty();
        }
    }

    public void givePeacekeeperLoot() {
        if (player.isPresent()) {
            Player p = Bukkit.getPlayer(player.get());
            if (p != null) {
                for (PeacekeeperLoot loot : PeacekeeperLoot.values()) {
                    if (loot.getSlot() == null) {
                        p.getInventory().addItem(loot.getIs());
                    } else {
                        p.getInventory().setItem(loot.getSlot(), loot.getIs());
                    }
                }
            }
        }
    }

}
