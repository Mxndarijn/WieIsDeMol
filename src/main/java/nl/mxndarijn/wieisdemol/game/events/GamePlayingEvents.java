package nl.mxndarijn.wieisdemol.game.events;

import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class GamePlayingEvents extends GameEvent {
    public GamePlayingEvents(Game g, JavaPlugin plugin) {
        super(g, plugin);
    }


    @EventHandler
    public void damage(PlayerArmorStandManipulateEvent e) {
        if(!validateWorld(e.getPlayer().getWorld()))
            return;
        if(Functions.convertComponentToString(e.getRightClicked().customName()).equals("attachment")) {
            e.setCancelled(true);
        }
    }

}
