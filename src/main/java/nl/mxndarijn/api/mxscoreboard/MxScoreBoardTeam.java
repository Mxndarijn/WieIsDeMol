package nl.mxndarijn.api.mxscoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class MxScoreBoardTeam {
    private String id;
    private String line;

    private Team team;
    private final String entry;
    private MxScoreBoard scoreboard;


    protected MxScoreBoardTeam(MxScoreBoard scoreboard) {
        this.line = "";
        this.id = UUID.randomUUID().toString();
        this.scoreboard = scoreboard;

        Random r = new Random();

        ChatColor randomColor = ChatColor.values()[r.nextInt(ChatColor.values().length)];
        ChatColor randomColor1 = ChatColor.values()[r.nextInt(ChatColor.values().length)];
        ChatColor randomColor2 = ChatColor.values()[r.nextInt(ChatColor.values().length)];
        ChatColor randomColor3 = ChatColor.values()[r.nextInt(ChatColor.values().length)];

        team = scoreboard.getScoreboard().registerNewTeam(this.id);
        this.entry = randomColor.toString() + randomColor1.toString() + randomColor2.toString() + randomColor3.toString() + ChatColor.RESET;
        team.addEntry(entry);

    }


    public void setLine(String line) {
        if(line.length() > scoreboard.MAX_LINE_LENGTH) {
            throw new ScoreboardNameToLong(line, scoreboard.MAX_LINE_LENGTH);
        }
        int lineLength = line.length();
        if(lineLength <= scoreboard.MAX_LINE_LENGTH / 2) {
            team.prefix(Component.text(line));
            team.suffix(Component.text(""));
        } else {
            String prefix = line.substring(0, scoreboard.MAX_LINE_LENGTH / 2);
            String suffix = getLatestChatColor(prefix) + line.substring(scoreboard.MAX_LINE_LENGTH / 2);
            if((prefix + suffix).length() > scoreboard.MAX_LINE_LENGTH)
                throw new ScoreboardNameToLong(line, scoreboard.MAX_LINE_LENGTH);
            team.prefix(Component.text(prefix));
            team.suffix(Component.text(suffix));

        }
    }

    public String getId() {
        return id;
    }

    public String getLine() {
        return line;
    }

    public Team getTeam() {
        return team;
    }

    public void destroy() {
        team.unregister();
    }

    private String getLatestChatColor(String s) {
        return ChatColor.getLastColors(s);
    }

    public String getEntry() {
        return entry;
    }
}
