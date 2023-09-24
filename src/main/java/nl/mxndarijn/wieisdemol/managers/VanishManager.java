package nl.mxndarijn.wieisdemol.managers;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY;

public class VanishManager implements Listener {

    private static VanishManager instance;

    private final Set<UUID> hiddenPlayers;
    private final ProtocolManager protocolManager;

    private final Plugin plugin;

    public static VanishManager getInstance() {
        if(instance == null)
            instance = new VanishManager(JavaPlugin.getPlugin(WieIsDeMol.class));
        return instance;
    }

    public VanishManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        protocolManager = ProtocolLibrary.getProtocolManager();
        hiddenPlayers = new HashSet<>();
    }


    private void hidePlayer(Player viewer, Player targetToHide) {
        if(!hiddenPlayers.contains(targetToHide.getUniqueId())) {
            hiddenPlayers.add(targetToHide.getUniqueId());
        }
        PacketContainer destroyEntity = new PacketContainer(ENTITY_DESTROY);
        Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, " ID: " + targetToHide.getEntityId());
        Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, " FIELDS: " + destroyEntity.getIntegerArrays().toString());

        destroyEntity.getModifier().write(0, new IntArrayList(new int[] {targetToHide.getEntityId()}));

        // Make the entity disappear
        protocolManager.sendServerPacket(viewer, destroyEntity);
    }

    private void showPlayer(Player player) {
        hiddenPlayers.remove(player.getUniqueId());

        protocolManager.updateEntity(player, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void join(PlayerLoginEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, (Runnable) () -> {
            if(e.getResult() != PlayerLoginEvent.Result.ALLOWED)
                return;
            hiddenPlayers.forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if(uuid != e.getPlayer().getUniqueId()) {
                    Logger.logMessage("Hiding player: " + p.getName());
                    hidePlayer(e.getPlayer(), p);

                }
            });
        }, 5L);
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        hiddenPlayers.remove(e.getPlayer().getUniqueId());
    }

    public void toggleVanish(Player p) {
        if(hiddenPlayers.contains(p.getUniqueId())) {
            showPlayer(p);
        } else {
            hidePlayerForAll(p);
        }
    }
    public boolean isPlayerHidden(Player p) {
        return hiddenPlayers.contains(p.getUniqueId());
    }
    public void hidePlayerForAll(Player p) {
        if(!hiddenPlayers.contains(p.getUniqueId()))
            hiddenPlayers.add(p.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(on -> {
            if(on.getUniqueId() != p.getUniqueId())
                hidePlayer(on, p);
        });
    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent e) {
        if(e.getFrom() == e.getPlayer().getWorld()) return;
        Player p = e.getPlayer();
        if(hiddenPlayers.contains(p.getUniqueId())) {
            hidePlayerForAll(p);
        }

        hiddenPlayers.forEach(uuid -> {
            if(uuid != p.getUniqueId()) {
                hidePlayer(p, Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            }
        });
    }
}
