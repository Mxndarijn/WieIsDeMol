package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.mxscoreboard.MxScoreBoard;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.ScoreBoardManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ToggleScoreboardCommand extends MxCommand implements Listener {
    private HashMap<UUID, MxScoreBoard> scoreBoardHashMap = new HashMap<>();
    public ToggleScoreboardCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);

        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(WieIsDeMol.class));

    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) throws Exception {
        Player p = (Player) sender;
        Optional<MxScoreBoard> scoreboard = ScoreBoardManager.getInstance().getPlayerScoreboard(p);
        if(scoreboard.isPresent()) {
            scoreBoardHashMap.put(p.getUniqueId(), scoreboard.get());
            ScoreBoardManager.getInstance().removePlayerScoreboard(p.getUniqueId(), scoreboard.get());
           MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.SCOREBOARD_HIDDEN));
        } else if(scoreBoardHashMap.containsKey(p.getUniqueId())) {
               ScoreBoardManager.getInstance().setPlayerScoreboard(p.getUniqueId(), scoreBoardHashMap.get(p.getUniqueId()));
               scoreBoardHashMap.remove(p.getUniqueId());
               MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.SCOREBOARD_SHOWED));
        } else {
               MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.SCOREBOARD_NOT_FOUND));

        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        scoreBoardHashMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void changeWorld(PlayerChangedWorldEvent e) {
        scoreBoardHashMap.remove(e.getPlayer().getUniqueId());
    }
}
