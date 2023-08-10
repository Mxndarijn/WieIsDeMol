package nl.mxndarijn.api.mxscoreboard;

public class ScoreboardNameToLong extends RuntimeException {

    public ScoreboardNameToLong(String name, int maxSize) {
        super("The name " + name + "is to long, max is " + maxSize);
    }
}
