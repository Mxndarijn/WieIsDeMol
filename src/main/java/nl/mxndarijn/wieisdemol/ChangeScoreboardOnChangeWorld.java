package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.api.changeworld.MxChangeWorld;
import nl.mxndarijn.api.mxscoreboard.MxScoreBoard;
import nl.mxndarijn.wieisdemol.managers.ScoreBoardManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChangeScoreboardOnChangeWorld implements MxChangeWorld {
    private final MxScoreBoard scoreboard;
    public ChangeScoreboardOnChangeWorld(MxScoreBoard scoreBoard) {
        this.scoreboard = scoreBoard;
    }
    @Override
    public void enter(Player p, World w, PlayerChangedWorldEvent e) {
        ScoreBoardManager.getInstance().setPlayerScoreboard(p.getUniqueId(), scoreboard);
    }

    @Override
    public void leave(Player p, World w, PlayerChangedWorldEvent e) {
        ScoreBoardManager.getInstance().removePlayerScoreboard(p.getUniqueId(), scoreboard);
    }
}
