package nl.mxndarijn.api.mxscoreboard;

import me.neznamy.tab.shared.ProtocolVersion;
import me.neznamy.tab.shared.chat.IChatBaseComponent;
import net.kyori.adventure.text.Component;
import nl.mxndarijn.api.util.Functions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class MxScoreBoard {
    public final int MAX_LINE_LENGTH = 128;
    private final List<MxScoreBoardTeam> teams;
    private final JavaPlugin plugin;
    public List<UUID> playersUsingScoreboard;
    private Scoreboard scoreboard;
    private Objective objective;
    private long updateTimer = -1;
    private BukkitTask task;


    public MxScoreBoard(JavaPlugin plugin) {
        this.playersUsingScoreboard = new ArrayList<>();
        this.teams = new ArrayList<>();

        this.plugin = plugin;
    }


    public void addPlayer(UUID uuid) {
        if (playersUsingScoreboard.contains(uuid))
            return;
        createScoreboardIfNotExists();
        playersUsingScoreboard.add(uuid);
        Player p = Bukkit.getPlayer(uuid);
        p.setScoreboard(scoreboard);
        checkTimer();
    }

    public void removePlayer(UUID uuid) {
        if (!playersUsingScoreboard.contains(uuid))
            return;

        playersUsingScoreboard.remove(uuid);
        Player p = Bukkit.getPlayer(uuid);
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        if (playersUsingScoreboard.isEmpty())
            delete();
    }

    private void createScoreboardIfNotExists() {
        if (this.scoreboard != null)
            return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("Title", "dummy", Component.text("Default"));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard();
        setPlayersScoreboard();
        checkTimer();

    }

    private void setPlayersScoreboard() {
        if (scoreboard == null)
            return;

        playersUsingScoreboard.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null)
                return;

            p.setScoreboard(scoreboard);
        });
    }

    public void delete() {
        if (this.scoreboard == null)
            return;
        if (task != null) {
            task.cancel();
            task = null;
        }
        removeAllPlayers();
        teams.forEach(t -> {
            t.destroy();
        });
        teams.clear();
        objective.unregister();
        this.objective = null;
        this.scoreboard = null;
    }

    private void removeAllPlayers() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        playersUsingScoreboard.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);

            if (p != null) {
                p.setScoreboard(manager.getMainScoreboard());
            }
        });
    }

    public void updateScoreboard() {
        if(playersUsingScoreboard.size() == 0)
            return;
        String title = getTitle();
        List<String> lines = getLines();

        if (!Functions.convertComponentToString(this.objective.displayName()).equals(title)) {
            this.objective.displayName(IChatBaseComponent.optimizedComponent(title).toAdventureComponent(ProtocolVersion.V1_19_4));
        }

        while (teams.size() < lines.size()) {
            MxScoreBoardTeam team = new MxScoreBoardTeam(this);
            teams.add(team);
        }

        while (teams.size() > lines.size()) {
            MxScoreBoardTeam team = teams.remove(teams.size() - 1);
            team.destroy();
        }
        Collections.reverse(lines);
        for (int i = 0; i < teams.size(); i++) {
            MxScoreBoardTeam team = teams.get(i);
            String line = lines.get(i);
            if (!team.getLine().equals(line)) {
                team.setLine(line);
            }
            objective.getScore(team.getEntry()).setScore(i + 1);
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    abstract String getTitle();

    abstract List<String> getLines();

    public List<UUID> getPlayersUsingScoreboard() {
        return playersUsingScoreboard;
    }

    public void setUpdateTimer(long l) {
        this.updateTimer = l;
    }

    private void checkTimer() {
        if (this.updateTimer == -1 || task != null)
            return;

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updateScoreboard();
        }, 0L, updateTimer);

    }
}
