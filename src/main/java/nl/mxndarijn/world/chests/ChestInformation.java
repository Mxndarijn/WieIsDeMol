package nl.mxndarijn.world.chests;

import nl.mxndarijn.inventory.MxInventoryIndex;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.chests.ChestAttachments.ChestAttachment;
import nl.mxndarijn.world.chests.ChestAttachments.ChestAttachments;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class ChestInformation {
    private String uuid;
    private String name;
    private MxLocation location;

    private File file;
    private String path;

    private List<ChestAttachment> chestAttachmentList;
    public ChestInformation(String name, MxLocation location) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.chestAttachmentList = new ArrayList<>();
    }

    private ChestInformation() {

    }

    public static Optional<ChestInformation> load(ConfigurationSection section) {
        if(section == null) {
            return Optional.empty();
        }
        ChestInformation i = new ChestInformation();
        i.uuid = section.getName();
        i.name = section.getString("name");
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        optionalMxLocation.ifPresent(location -> i.location = location);
        i.chestAttachmentList = new ArrayList<>(); //TODO Load items
        section.getMapList("attachments").forEach( map -> {
            Map<String, Object> convertedMap = (Map<String, Object>) map;
            String type = (String) convertedMap.get("type");
            Optional<ChestAttachments> attachment = ChestAttachments.getAttachmentByType(type);
            if(attachment.isEmpty()) {
                Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load attachment (Type not found) : " + type);
            } else {
                Optional<ChestAttachment> opt = attachment.get().getExistingInstance(convertedMap, i);
                if(opt.isEmpty()) {
                    Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER + "Could not load attachment " + type);
                }
                opt.ifPresent(att -> {
                    i.chestAttachmentList.add(att);
                });
            }

        });

        if(i.location != null) {
            return Optional.of(i);
        }
        return Optional.empty();
    }

    public void save(FileConfiguration fc) {
        ConfigurationSection section = fc.createSection(uuid);
        section.set("name", name);
        location.write(section.createSection("location"));

        List<Map<String, Object>> list = new ArrayList<>();
        chestAttachmentList.forEach(chestAttachment -> {
            list.add(chestAttachment.getDataForSaving());
        });
        section.set("attachments", list);
    }


    public String getName() {
        return name;
    }

    private boolean containsAttachment(ChestAttachments attachments) {
        for (ChestAttachment at : chestAttachmentList) {
            if (at.getClass().equals(attachments.getAttachmentClass())) {
                return true;
            }
        }
        return false;
    }

    public void openAttachmentsInventory(Player p) {
        p.closeInventory();
        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        Arrays.stream(ChestAttachments.values()).forEach(attachments -> {
            if(!containsAttachment(attachments))
                list.add(attachments.getAddItemStack(this));
        });

        chestAttachmentList.forEach(chestAttachment -> {
            list.add(chestAttachment.getEditAttachmentItem());
        });

        Collections.reverse(list);

        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Chest Attachments", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .setListItems(list)
                .build());
    }
    public MxLocation getLocation() {
        return location;
    }
    public void addNewAttachment(Player p, ChestAttachments attachments) {
        if(containsAttachment(attachments)) {
                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_COULD_NOT_ADD, Collections.singletonList(attachments.getDisplayName())));
                p.closeInventory();
                return;
        }
        Optional<ChestAttachment> attachment = attachments.createNewInstance(this);
        if(attachment.isPresent()) {
            chestAttachmentList.add(attachment.get());
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_ADDED, Collections.singletonList(attachments.getDisplayName())));
            p.closeInventory();
            return;
        }
        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_COULD_NOT_ADD, Collections.singletonList(attachments.getDisplayName())));

    }


    public String getUuid() {
        return uuid;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public List<ChestAttachment> getChestAttachmentList() {
        return chestAttachmentList;
    }
}
