package nl.mxndarijn.wieisdemol.managers.gamemanager;

import org.bukkit.ChatColor;

public enum UpcomingGameStatus {
    WAITING(ChatColor.YELLOW + "Wachtend"),
    PLAYING(ChatColor.GREEN + "Bezig");

    private final String status;

    UpcomingGameStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public String getStatus() {
        return status;
    }
}
