package nl.mxndarijn.wieisdemol.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
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

    public VanishManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        protocolManager = ProtocolLibrary.getProtocolManager();
        hiddenPlayers = new HashSet<>();

        protocolManager.addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT,
                        PacketType.Play.Server.ENTITY_SOUND) {
                    @Override
                    public void onPacketSending(PacketEvent event) {

                    }
                }
        );
    }

    public static VanishManager getInstance() {
        if (instance == null)
            instance = new VanishManager(JavaPlugin.getPlugin(WieIsDeMol.class));
        return instance;
    }

    private void hidePlayer(Player viewer, Player targetToHide) {
        hiddenPlayers.add(targetToHide.getUniqueId());
        PacketContainer destroyEntity = new PacketContainer(ENTITY_DESTROY);

        destroyEntity.getModifier().write(0, new IntArrayList(new int[]{targetToHide.getEntityId()}));

        // Make the entity disappear
        protocolManager.sendServerPacket(viewer, destroyEntity);
    }

    private void showPlayer(Player player) {
        hiddenPlayers.remove(player.getUniqueId());

        protocolManager.updateEntity(player, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void join(PlayerLoginEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (e.getResult() != PlayerLoginEvent.Result.ALLOWED)
                return;
            hiddenPlayers.forEach(uuid -> {
                Player p = Bukkit.getPlayer(uuid);
                if (uuid != e.getPlayer().getUniqueId()) {
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
        if (hiddenPlayers.contains(p.getUniqueId())) {
            showPlayer(p);
        } else {
            hidePlayerForAll(p);
        }
    }

    public boolean isPlayerHidden(Player p) {
        return hiddenPlayers.contains(p.getUniqueId());
    }

    public void hidePlayerForAll(Player p) {
        hiddenPlayers.add(p.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(on -> {
            if (on.getUniqueId() != p.getUniqueId())
                hidePlayer(on, p);
        });
    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent e) {
        if (e.getFrom() == e.getPlayer().getWorld()) return;
        Player p = e.getPlayer();
        if (hiddenPlayers.contains(p.getUniqueId())) {
            hidePlayerForAll(p);
        }

        hiddenPlayers.forEach(uuid -> {
            if (uuid != p.getUniqueId()) {
                hidePlayer(p, Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            }
        });
    }

    public void showPlayerForAll(Player p) {
        showPlayer(p);
    }
}
