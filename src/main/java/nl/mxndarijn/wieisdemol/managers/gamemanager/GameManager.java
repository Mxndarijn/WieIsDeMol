package nl.mxndarijn.wieisdemol.managers.gamemanager;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstacne() {
        if(instance == null)
            instance = new GameManager();
        return instance;
    }

    private GameManager() {

    }
}
