package nl.mxndarijn.wieisdemol.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.myzelyam.api.vanish.VanishAPI;
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


    private final Plugin plugin;

    public VanishManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static VanishManager getInstance() {
        if (instance == null)
            instance = new VanishManager(JavaPlugin.getPlugin(WieIsDeMol.class));
        return instance;
    }

    private void showPlayer(Player player) {
        VanishAPI.showPlayer(player);
    }



    public void toggleVanish(Player p) {
        if (VanishAPI.isInvisible(p)) {
            showPlayer(p);
        } else {
            hidePlayerForAll(p);
        }
    }

    public boolean isPlayerHidden(Player p) {
        return VanishAPI.isInvisible(p);
    }

    public void hidePlayerForAll(Player p) {
        VanishAPI.hidePlayer(p);
    }

    public void showPlayerForAll(Player p) {
        showPlayer(p);
    }
}
