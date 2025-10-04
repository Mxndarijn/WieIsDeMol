package nl.mxndarijn.wieisdemol.map.mapscript.manager;

import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.events.GameEvent;
import nl.mxndarijn.wieisdemol.map.mapscript.MapPlayerType;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import nl.mxndarijn.wieisdemol.map.mapscript.Portal;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

import java.util.Random;

import java.util.List;

public class PortalManager extends GameEvent {

    private final MapScript mapScript;

    public PortalManager(MapScript mapScript, Game g, JavaPlugin plugin) {
        super(g, plugin);
        this.mapScript = mapScript;
    }


    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!validateWorld(e.getTo().getWorld())) {
            return;
        }

        Location loc = e.getTo();

        List<Portal> portals = this.mapScript.getPortals();
        for (Portal portal : portals) {
            Location corner1 = portal.corner1().getLocation(e.getTo().getWorld());
            Location corner2 = portal.corner2().getLocation(e.getTo().getWorld());
            BoundingBox box = BoundingBox.of(
                    new Location(loc.getWorld(),
                            Math.min(corner1.getBlockX(), corner2.getBlockX()),
                            Math.min(corner1.getBlockY(), corner2.getBlockY()),
                            Math.min(corner1.getBlockZ(), corner2.getBlockZ())),
                    new Location(loc.getWorld(),
                            Math.max(corner1.getBlockX() + 1, corner2.getBlockX() + 1),
                            Math.max(corner1.getBlockY() + 1, corner2.getBlockY() + 1),
                            Math.max(corner1.getBlockZ() + 1, corner2.getBlockZ() + 1))
            );

            if (box.contains(loc.toVector())) {
                MapPlayerType applicableTo = portal.applicableTo();
                boolean pass = switch (applicableTo) {
                    case ALL -> true;
                    case PLAYER -> this.game.getAlivePlayers().stream()
                            .filter(gp -> gp.getPlayer().isPresent())
                            .map(gp -> gp.getPlayer().get())
                            .toList()
                            .contains(e.getPlayer().getUniqueId());
                    case SPECTATOR -> this.game.getSpectators().contains(e.getPlayer().getUniqueId());
                    case HOST -> this.game.getHosts().contains(e.getPlayer().getUniqueId());
                };
                if (pass) {
                    Random random = new Random();
                    Location teleportTo = portal.teleportsTo().get(random.nextInt(portal.teleportsTo().size())).getLocation(e.getPlayer().getWorld());
                    if (!portal.shouldReceiveFallDamage()) {
                        e.getPlayer().setFallDistance(0);
                    }
                    e.getPlayer().teleport(teleportTo);
                }
            }
        }
    }
}
