package nl.mxndarijn.inventory.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class MxSkullItemStackBuilder extends MxItemStackBuilder<MxSkullItemStackBuilder> {

    private MxSkullItemStackBuilder(int amount) {
        super(Material.PLAYER_HEAD, amount);
    }

    private MxSkullItemStackBuilder(Material mat, int amount, int id) {
        super(mat, amount, id);
    }

    public static MxSkullItemStackBuilder create(int amount) {
        return new MxSkullItemStackBuilder(amount);
    }

    public static MxSkullItemStackBuilder create(Material mat, int amount, int id) {
        return new MxSkullItemStackBuilder(mat, amount, id);
    }

    //Slows down the process.
    @Deprecated
    public MxSkullItemStackBuilder setOwner(String name) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        return this;
    }

    //Slows down the process.
    @Deprecated
    public MxSkullItemStackBuilder setOwner(UUID id) {
        SkullMeta meta = (SkullMeta) itemMeta;
        OfflinePlayer p = Bukkit.getOfflinePlayer(id);
        Logger.logMessage(LogLevel.DebugHighlight, Prefix.MXINVENTORY, "Setting owner: " + p.getName());
        boolean returnedValue = meta.setOwningPlayer(p);
        if(!returnedValue) {
            Logger.logMessage(LogLevel.Error, Prefix.MXINVENTORY, "Could not set owner of skull");
        }
        return this;
    }

    public MxSkullItemStackBuilder setSkinFromHeadsData(String value) {
        Optional<String> dataOpt = MxHeadManager.getInstance().getTextureValue(value);
        if(dataOpt.isPresent()) {
            String data = dataOpt.get();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", data));
            try
            {
                Field profileField = itemMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(itemMeta, profile);
            }
            catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error)
            {
                Logger.logMessage(LogLevel.Error, Prefix.MXINVENTORY, "Could not load skull: " + value);
                error.printStackTrace();
            }
        } else {
            Logger.logMessage(LogLevel.Error, Prefix.MXINVENTORY, "Could not find skull: " + value + " in heads-data.yml");
        }

        return this;
    }

    @Override
    public ItemStack build() {
        return super.build();
    }
}
