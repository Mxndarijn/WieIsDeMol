package nl.mxndarijn.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class MxSkullItemStackBuilder extends MxItemStackBuilder {

    public MxSkullItemStackBuilder(int amount) {
        super(Material.SKULL_ITEM, amount, SkullType.PLAYER.ordinal());
    }

    public MxSkullItemStackBuilder(Material mat, int amount, int id) {
        super(mat, amount, id);
    }

    public MxSkullItemStackBuilder setOwner(String name) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        return this;
    }

    public MxSkullItemStackBuilder setOwner(UUID id) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
        return this;
    }
}
