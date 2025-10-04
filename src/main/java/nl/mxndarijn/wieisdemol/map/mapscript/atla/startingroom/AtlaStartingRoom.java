package nl.mxndarijn.wieisdemol.map.mapscript.atla.startingroom;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.api.builders.ArmorStandHelper;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.map.mapscript.MapRoom;
import nl.mxndarijn.wieisdemol.map.mapscript.MapRoomResult;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import java.util.List;
import java.util.UUID;

public class AtlaStartingRoom extends MapRoom {


    public AtlaStartingRoom(@NotNull MapScript mapScript) {
        super(mapScript);
    }

    @Override
    public @NotNull MapRoomResult build() {
        return MapRoomResult.builder(this)
                .addMapAction(new AtlaStartingRoomTeleportAppa(this))
                .build();
    }

    @Override
    public void mapSetup() {

    }

    @Override
    public void mapUnload() {

    }

    @Override
    public @NotNull String getTitle() {
        return "Beginkamer";
    }

    public void teleportPlayers() {
        Game game = this.getGame().orElseThrow();
        MxWorld world = this.getMxWorld().orElseThrow();

        World w = Bukkit.getWorld(world.getWorldUID());
        assert w != null;

        Location loc = new Location(w, -153, 65, 47);
        int maxRadius = 6;

        Collection<Player> nearbyPlayers = w.getEntitiesByClass(Player.class);
        List<GamePlayer> aliveGamePlayers = game.getAlivePlayers();
        List<UUID> playersToTeleport = new java.util.ArrayList<>(nearbyPlayers.stream()
                .filter(player -> player.getLocation().distance(loc) <= maxRadius &&
                        aliveGamePlayers.stream().anyMatch(gp ->
                                gp.getPlayer().filter(uuid ->
                                        uuid.equals(player.getUniqueId())).isPresent()))
                .map(Player::getUniqueId)
                .toList());

        playersToTeleport.addAll(game.getHosts());
        playersToTeleport.addAll(game.getSpectators());

        Location teleportLocation = new Location(w, -150.53, 56.00, -32.46, -180, 0);

        playersToTeleport.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.teleport(teleportLocation);
            }
        });


    }

    private int taskId;
    private int playersAtLocation = 0;

    private void updatePlayerCount() {
        Game game = this.getGame().orElseThrow();
        World w = this.getWorld().orElseThrow();
        Location loc = new Location(w, -153, 66, 47);
        int maxRadius = 6;

        Collection<Player> nearbyPlayers = w.getEntitiesByClass(Player.class);
        List<GamePlayer> aliveGamePlayers = game.getAlivePlayers();

        playersAtLocation = (int) nearbyPlayers.stream()
                .filter(player -> player.getLocation().distance(loc) <= maxRadius &&
                        aliveGamePlayers.stream().anyMatch(gp ->
                                gp.getPlayer().filter(uuid ->
                                        uuid.equals(player.getUniqueId())).isPresent()))
                .count();
    }

    private ArmorStand armorStand;

    @Override
    public void gameSetup() {
        Logger.logMessage("Game Setup");

        World w = this.getWorld().orElseThrow();
        Game game = this.getGame().orElseThrow();

        armorStand = ArmorStandHelper.create()
                .setLocation(new Location(w, -153, 66, 47))
                .setInvisible(true)
                .setInvulnerable(true)
                .setCollidable(false)
                .setCustomNameVisible(true)
                .setName("<gray>" + playersAtLocation + "<dark_gray>/<gray>" + game.getAlivePlayerCount() + " spelers aanwezig")
                .build();

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getPlugin(), () -> {
            Game currentGame = this.getGame().orElseThrow();
            updatePlayerCount();
            armorStand.customName(Functions.buildComponentFromString("<gray>" + playersAtLocation + "<dark_gray>/<gray>" + currentGame.getAlivePlayerCount() + " spelers aanwezig."));
            if(playersAtLocation == currentGame.getAlivePlayerCount()) {
                Bukkit.getScheduler().cancelTask(taskId);

                Bukkit.getScheduler().runTaskLater(game.getPlugin(), () -> {
                    new BukkitRunnable() {
                        int count = 10;

                        @Override
                        public void run() {
                            if (count == 10 || count <= 5) {
                                game.sendMessageToAll("<gray>Teleporteren in " + count + "...");
                            }
                            if (count <= 0) {
                                cancel();
                                teleportPlayers();
                                return;
                            }
                            count--;
                        }
                    }.runTaskTimer(game.getPlugin(), 0L, 20L);
                }, 20L * 5);
            }
        }, 0L, 20L);


    }

    @Override
    public void gameUnload() {
        Bukkit.getScheduler().cancelTask(taskId);
        if (armorStand != null) {
            armorStand.remove();
        }
    }
}
