package nl.mxndarijn.wieisdemol.managers;

import nl.mxndarijn.api.mxscoreboard.MxScoreBoard;
import nl.mxndarijn.api.mxscoreboard.MxSupplierScoreBoard;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.UUID;

public class ScoreBoardManager implements Listener {

    private static ScoreBoardManager instance;

    public static ScoreBoardManager getInstance() {
        if(instance == null)
            instance = new ScoreBoardManager();
        return instance;
    }


    private HashMap<UUID, MxScoreBoard> playerList;
    private ScoreBoardManager() {
        playerList = new HashMap<>();


        Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getPlugin(WieIsDeMol.class));
    }

    public void setPlayerScoreboard(UUID uuid, MxScoreBoard scoreboard) {
        if(playerList.containsKey(uuid))
            playerList.get(uuid).removePlayer(uuid);
        scoreboard.addPlayer(uuid);
        playerList.put(uuid, scoreboard);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        if(playerList.containsKey(e.getPlayer().getUniqueId()))
            playerList.get(e.getPlayer().getUniqueId()).removePlayer(e.getPlayer().getUniqueId());
        playerList.remove(e.getPlayer().getUniqueId());
    }


    public void removePlayerScoreboard(UUID uniqueId, MxScoreBoard scoreboard) {
        if(playerList.containsKey(uniqueId) && playerList.get(uniqueId).equals(scoreboard))
            playerList.get(uniqueId).removePlayer(uniqueId);
    }
}
