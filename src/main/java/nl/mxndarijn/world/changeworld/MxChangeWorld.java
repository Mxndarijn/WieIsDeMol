package nl.mxndarijn.world.changeworld;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public interface MxChangeWorld {

    public void enter(Player p, World w, PlayerChangedWorldEvent e);

    public void leave(Player p, World w, PlayerChangedWorldEvent e);
}
