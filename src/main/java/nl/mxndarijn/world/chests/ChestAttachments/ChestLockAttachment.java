package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChestLockAttachment extends ChestAttachment {
    private String lockTag;
    private boolean locked = true;

    public static Optional<ChestLockAttachment> createFromSection(Map<String, Object> section, ChestInformation information) {
        ChestLockAttachment attachment = new ChestLockAttachment();
        if(!getDefaultValues(attachment, information, section)) {
            return Optional.empty();
        }
        assert(section != null);

        if(!section.containsKey("lockTag")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load lockTag.  Type: " + attachment.type);
            return Optional.empty();
        }
        attachment.locked = (Boolean) section.get("locked") ? (Boolean) section.get("locked") : true;
        attachment.setLockTag((String) section.get("lockTag"));

        return Optional.of(attachment);
    }

    public static ChestAttachment createNewInstance(String type,ChestInformation information, MxLocation location) {
        ChestLockAttachment attachment = new ChestLockAttachment();
        attachment.setDefaults(type, information);
        attachment.locked = true;
        attachment.lockTag = UUID.randomUUID().toString();
        return attachment;
    }

    @Override
    public Map<String, Object> getDataForSaving() {
        Map<String, Object> map = new HashMap<>();
        getDataDefaults(map);
        map.put("lockTag", lockTag);
        map.put("locked", locked);

        return map;
    }

    public void setLockTag(String lockTag) {
        this.lockTag = lockTag;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        return new Pair<>(
                MxSkullItemStackBuilder.create(1)
                        .setSkinFromHeadsData("locked-chest")
                        .setName(ChatColor.GREEN + "Kist slot")
                        .addBlankLore()
                        .addLore(ChatColor.GRAY + "Kist gelocked: " + (locked ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"))
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    //TODO
                }

        );
    }
}
