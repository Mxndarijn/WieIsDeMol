package nl.mxndarijn.wieisdemol.game;

import nl.mxndarijn.api.mxscoreboard.MxSupplierScoreBoard;
import nl.mxndarijn.wieisdemol.data.PeacekeeperLoot;
import nl.mxndarijn.wieisdemol.data.ScoreBoard;
import nl.mxndarijn.wieisdemol.managers.ScoreBoardManager;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class GamePlayer {

    private MapPlayer mapPlayer;
    private Optional<UUID> player;
    private boolean alive;
    private MxSupplierScoreBoard scoreboard;

    private boolean beginChestOpened;
    private boolean peacekeeperChestOpened;

    private Game game;
    private JavaPlugin plugin;
    private boolean canReborn;

    public GamePlayer(MapPlayer mapPlayer, JavaPlugin plugin, Game game) {
        this.mapPlayer = mapPlayer;
        this.player = Optional.empty();
        this.game = game;
        this.plugin = plugin;
        alive = true;
        this.beginChestOpened = false;
        this.peacekeeperChestOpened = false;
        this.canReborn = !mapPlayer.isPeacekeeper();
         String host = Bukkit.getOfflinePlayer(game.getMainHost()).getName();
        scoreboard = new MxSupplierScoreBoard(plugin, () -> {
            return ScoreBoard.GAME_HOST.getTitle(new HashMap<>() {{
                put("%%map_name%%", game.getConfig().getPresetConfig().getName());
            }});
        }, () -> {
            return ScoreBoard.GAME_PLAYER.getLines(new HashMap<>() {{
                put("%%game_status%%", game.getGameInfo().getStatus().getStatus());
                put("%%game_time%%", game.getGameTime());
                put("%%color%%", mapPlayer.getColor().getDisplayName());
                put("%%host%%", host);
                put("%%role%%", (beginChestOpened ? (peacekeeperChestOpened ? mapPlayer.getRoleDisplayString() : mapPlayer.getRoleDisplayWithoutPeacekeeper()) : ChatColor.GRAY + "Onbekend"));
            }});
        });
        scoreboard.setUpdateTimer(10);
    }

    public void setPlayingPlayer(UUID player) {
        this.player.ifPresent(p -> {
            scoreboard.removePlayer(p);
        });
        if(player != null) {
            this.player = Optional.of(player);
            ScoreBoardManager.getInstance().setPlayerScoreboard(player, scoreboard);
        } else {
            this.player = Optional.empty();
        }
    }

    public MapPlayer getMapPlayer() {
        return mapPlayer;
    }

    public Optional<UUID> getPlayer() {
        return player;
    }

    public void setMapPlayer(MapPlayer mapPlayer) {
        this.mapPlayer = mapPlayer;
    }

    public void setPlayer(Optional<UUID> player) {
        this.player = player;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setScoreboard(MxSupplierScoreBoard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setBeginChestOpened(boolean beginChestOpened) {
        this.beginChestOpened = beginChestOpened;
    }

    public void setPeacekeeperChestOpened(boolean peacekeeperChestOpened) {
        this.peacekeeperChestOpened = peacekeeperChestOpened;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isAlive() {
        return alive;
    }

    public MxSupplierScoreBoard getScoreboard() {
        return scoreboard;
    }

    public boolean isBeginChestOpened() {
        return beginChestOpened;
    }

    public boolean isPeacekeeperChestOpened() {
        return peacekeeperChestOpened;
    }

    public Game getGame() {
        return game;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void givePeacekeeperLoot() {
        if(player.isPresent()) {
            Player p = Bukkit.getPlayer(player.get());
            if(p != null) {
                for (PeacekeeperLoot loot : PeacekeeperLoot.values()) {
                    if(loot.getSlot() == null) {
                        p.getInventory().addItem(loot.getIs());
                    } else {
                        p.getInventory().setItem(loot.getSlot(), loot.getIs());
                    }
                }
            }
        }
    }
}
