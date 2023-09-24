package nl.mxndarijn.wieisdemol.managers.gamemanager;

import org.bukkit.ChatColor;

public enum UpcomingGameStatus {
    WAITING(ChatColor.YELLOW + "Wachtend", true),
    CHOOSING_PLAYERS(ChatColor.YELLOW + "Spelers worden gekozen", true),
    PLAYING(ChatColor.GREEN + "Bezig", false);

    private final String status;
    private final boolean canJoinQueue;

    UpcomingGameStatus(String status, boolean canJoinQueue) {
        this.status = status;
        this.canJoinQueue = canJoinQueue;
    }

    @Override
    public String toString() {
        return status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isCanJoinQueue() {
        return canJoinQueue;
    }
}
