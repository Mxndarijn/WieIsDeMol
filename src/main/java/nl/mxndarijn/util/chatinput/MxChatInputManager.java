package nl.mxndarijn.util.chatinput;

import io.papermc.paper.event.player.AsyncChatEvent;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class MxChatInputManager implements Listener {

    private static MxChatInputManager instance;
    private HashMap<UUID, MxChatInputCallback> map;

    public static MxChatInputManager getInstance() {
        if(instance == null) {
            instance = new MxChatInputManager();
        }
        return instance;
    }

    private MxChatInputManager() {
        map = new HashMap<>();
        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Logger.logMessage(LogLevel.Information, Prefix.MXCHATINPUTMANAGER, "MxChatInputManager loaded...");

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatEvent(AsyncChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if(map.containsKey(uuid)) {
            e.setCancelled(true);
            MxChatInputCallback inputCallback = map.get(uuid);
            map.remove(uuid);
            inputCallback.textReceived(Functions.convertComponentToString(e.message()));
        }
    }

    public void addChatInputCallback(UUID uuid, MxChatInputCallback callback) {
        map.put(uuid, callback);
    }
}
