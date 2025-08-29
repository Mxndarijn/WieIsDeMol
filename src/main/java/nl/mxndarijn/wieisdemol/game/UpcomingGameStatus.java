package nl.mxndarijn.wieisdemol.game;

public enum UpcomingGameStatus {
    WAITING("<yellow>Wachtend", true),
    CHOOSING_PLAYERS("<yellowSpelers kiezen", true),
    PLAYING("<green>Bezig", false),
    FREEZE("<aqua>Freezed", false),
    FINISHED("<red>Afgelopen", false);

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
