package nl.mxndarijn.wieisdemol.managers.gamemanager;

import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;

import java.util.Optional;
import java.util.UUID;

public class GamePlayer {

    private MapPlayer mapPlayer;
    private Optional<UUID> player;
    public GamePlayer(MapPlayer mapPlayer) {
        this.mapPlayer = mapPlayer;
        this.player = Optional.empty();
    }

    public void setPlayingPlayer(UUID player) {
        this.player = Optional.of(player);
    }

    public MapPlayer getMapPlayer() {
        return mapPlayer;
    }

    public Optional<UUID> getPlayer() {
        return player;
    }
}
