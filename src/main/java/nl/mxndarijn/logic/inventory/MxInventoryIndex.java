package nl.mxndarijn.logic.inventory;

public enum MxInventoryIndex {
    ROW_ONE_ONLY(0, 8),
    ROW_TWO_ONLY(9, 17),
    ROW_THREE_ONLY(18, 26),
    ROW_FOUR_ONLY(27, 35),
    ROW_FIVE_ONLY(36, 44),
    ROW_SIX_ONLY(45, 53),
    ROW_ONE_TO_TWO(0, 17),
    ROW_ONE_TO_THREE(0, 26),
    ROW_ONE_TO_FOUR(0, 35),

    ROW_ONE_TO_FIVE(0, 44),
    ROW_ONE_TO_SIX(0, 53);

    private final int beginIndex;
    private final int endIndex;
    MxInventoryIndex(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
