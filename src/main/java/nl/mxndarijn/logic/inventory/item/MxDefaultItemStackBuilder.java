package nl.mxndarijn.logic.inventory.item;

import org.bukkit.Material;

public class MxDefaultItemStackBuilder extends MxItemStackBuilder<MxDefaultItemStackBuilder> {

    MxDefaultItemStackBuilder(Material mat) {
        super(mat);
    }

    MxDefaultItemStackBuilder(Material mat, int amount) {
        super(mat, amount);
    }

    MxDefaultItemStackBuilder(Material mat, int amount, int byteAmount) {
        super(mat, amount, byteAmount);
    }

    public static MxDefaultItemStackBuilder create(Material mat) {
        return new MxDefaultItemStackBuilder(mat);
    }

    public static MxDefaultItemStackBuilder create(Material mat, int amount) {
        return new MxDefaultItemStackBuilder(mat, amount);
    }

    public static MxDefaultItemStackBuilder create(Material mat, int amount, int byteAmount) {
        return new MxDefaultItemStackBuilder(mat, amount, byteAmount);
    }
}
