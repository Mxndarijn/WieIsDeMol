package nl.mxndarijn.world.changeworld;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class ChangeWorldManager implements Listener {

    private static ChangeWorldManager instance;

    private HashMap<UUID, MxChangeWorld> worlds;

    public static ChangeWorldManager getInstance() {
        if(instance == null) {
            instance = new ChangeWorldManager();
        }
        return instance;
    }
    
    private ChangeWorldManager() {
        Logger.logMessage(LogLevel.Debug, Prefix.CHANGEWORLDMANAGER, "Loading...");
        worlds = new HashMap<>();

        JavaPlugin plugin = JavaPlugin.getPlugin(WieIsDeMol.class);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent e) {
        UUID from = e.getFrom().getUID();
        UUID to = e.getPlayer().getWorld().getUID();
        if(worlds.containsKey(from)) {
            worlds.get(from).leave(e.getPlayer(),e.getFrom(), e);
        } else {
            Logger.logMessage(LogLevel.Debug, Prefix.CHANGEWORLDMANAGER, "World: " + e.getFrom().getName() + " not found (leaving this world). (" + e.getPlayer().getName() + ")");
        }
        if(worlds.containsKey(to)) {
            worlds.get(to).enter(e.getPlayer(),e.getPlayer().getWorld(), e);
        } else {
            Logger.logMessage(LogLevel.Debug, Prefix.CHANGEWORLDMANAGER, "World: " + e.getPlayer().getWorld().getName() + " not found (going to this world). (" + e.getPlayer().getName() + ")");
        }
    }

    @EventHandler
    public void worldUnload(WorldUnloadEvent e) {
        UUID worldUID = e.getWorld().getUID();
        if(worlds.containsKey(worldUID)) {
            Logger.logMessage(LogLevel.Debug, Prefix.CHANGEWORLDMANAGER, "World: " + e.getWorld().getName() + " has been unloaded.");
            worlds.remove(worldUID);

        }
    }

    public void addWorld(UUID uid, MxChangeWorld changeWorld) {
        worlds.put(uid, changeWorld);
    }

    public void removeWorld(UUID uid) {
        worlds.remove(uid);
    }
}
