package nl.mxndarijn.managers.changeworld;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public interface WorldReachedZeroPlayersEvent {
    public void worldReachedZeroPlayers(Player p, World w, PlayerChangedWorldEvent e);
}
