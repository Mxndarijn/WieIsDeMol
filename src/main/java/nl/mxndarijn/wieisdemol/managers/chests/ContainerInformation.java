package nl.mxndarijn.wieisdemol.managers.chests;

import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.chests.chestattachments.ContainerAttachment;
import nl.mxndarijn.wieisdemol.managers.chests.chestattachments.ContainerAttachments;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class ContainerInformation {
    private String uuid;
    private String name;
    private MxLocation location;
    private ContainerType type;

    private File file;
    private String path;

    private List<ContainerAttachment> containerAttachmentList;

    public ContainerInformation(String name, MxLocation location) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.type = ContainerType.CHEST; // legacy default
        this.containerAttachmentList = new ArrayList<>();
    }

    public ContainerInformation(String name, MxLocation location, ContainerType type) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.type = type == null ? ContainerType.CHEST : type;
        this.containerAttachmentList = new ArrayList<>();
    }

    private ContainerInformation() {

    }

    public static Optional<ContainerInformation> load(ConfigurationSection section) {
        if (section == null) {
            return Optional.empty();
        }
        ContainerInformation i = new ContainerInformation();
        i.uuid = section.getName();
        i.name = section.getString("name");
        Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(section.getConfigurationSection("location"));
        optionalMxLocation.ifPresent(location -> i.location = location);
        String typeName = section.getString("type", ContainerType.CHEST.name());
        try {
            i.type = ContainerType.valueOf(typeName);
        } catch (IllegalArgumentException ex) {
            i.type = ContainerType.CHEST;
        }
        i.containerAttachmentList = new ArrayList<>(); //TODO Load items
        section.getMapList("attachments").forEach(map -> {
            Map<String, Object> convertedMap = (Map<String, Object>) map;
            String type = (String) convertedMap.get("type");
            Optional<ContainerAttachments> attachment = ContainerAttachments.getAttachmentByType(type);
            if (attachment.isEmpty()) {
                Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load attachment (Type not found) : " + type);
            } else {
                Optional<ContainerAttachment> opt = attachment.get().getExistingInstance(convertedMap, i);
                if (opt.isEmpty()) {
                    Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER + "Could not load attachment " + type);
                }
                opt.ifPresent(att -> {
                    i.containerAttachmentList.add(att);
                });
            }

        });

        if (i.location != null) {
            return Optional.of(i);
        }
        return Optional.empty();
    }

    public void save(FileConfiguration fc) {
        ConfigurationSection section = fc.createSection(uuid);
        section.set("name", name);
        location.write(section.createSection("location"));
        section.set("type", type == null ? ContainerType.CHEST.name() : type.name());

        List<Map<String, Object>> list = new ArrayList<>();
        containerAttachmentList.forEach(chestAttachment -> {
            list.add(chestAttachment.getDataForSaving());
        });
        section.set("attachments", list);
    }


    public String getName() {
        return name;
    }

    private boolean containsAttachment(ContainerAttachments attachments) {
        for (ContainerAttachment at : containerAttachmentList) {
            if (at.getClass().equals(attachments.getAttachmentClass())) {
                return true;
            }
        }
        return false;
    }

    public void openAttachmentsInventory(Player p) {
        p.closeInventory();
        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        Arrays.stream(ContainerAttachments.values()).forEach(attachments -> {
            if (!containsAttachment(attachments))
                list.add(attachments.getAddItemStack(this));
        });

        containerAttachmentList.forEach(chestAttachment -> {
            list.add(chestAttachment.getEditAttachmentItem());
        });

        Collections.reverse(list);

        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Container Attachments", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .setListItems(list)
                .build());
    }

    public MxLocation getLocation() {
        return location;
    }

    public ContainerType getType() {
        return type == null ? ContainerType.CHEST : type;
    }

    public void addNewAttachment(Player p, ContainerAttachments attachments) {
        if (containsAttachment(attachments)) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_COULD_NOT_ADD, Collections.singletonList(attachments.getDisplayName())));
            p.closeInventory();
            return;
        }
        Optional<ContainerAttachment> attachment = attachments.createNewInstance(this);
        if (attachment.isPresent()) {
            containerAttachmentList.add(attachment.get());
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_ADDED, Collections.singletonList(attachments.getDisplayName())));
            p.closeInventory();
            return;
        }
        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_COULD_NOT_ADD, Collections.singletonList(attachments.getDisplayName())));

    }


    public String getUuid() {
        return uuid;
    }

    public File getFile() {
        return file;
    }


    public List<ContainerAttachment> getChestAttachmentList() {
        return containerAttachmentList;
    }

    public void removeChestAttachment(Player p, ContainerAttachment at, ContainerAttachments chestAttachments) {
        getChestAttachmentList().remove(at);
        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_REMOVED, Collections.singletonList(chestAttachments.getDisplayName())));
    }

    public boolean canOpenChest(GamePlayer gamePlayer) {
        for (ContainerAttachment c : containerAttachmentList) {
            if (!c.canOpenChest(gamePlayer)) {
                return false;
            }
        }
        return true;
    }

    public void onChestInteract(GamePlayer gamePlayer, PlayerInteractEvent e, Game game, Player p) {
        for (ContainerAttachment c : containerAttachmentList) {
            c.onChestInteract(gamePlayer, e, game, p);
        }
    }

    public void onChestInventoryClick(GamePlayer gamePlayer, InventoryClickEvent e, Game game, Player p) {
        for (ContainerAttachment c : containerAttachmentList) {
            c.onChestInventoryClick(gamePlayer, e, game, p);
        }

    }

    public boolean containsChestAttachment(ContainerAttachments chestAttachments) {
        for (ContainerAttachment containerAttachment : containerAttachmentList) {
            if (containerAttachment.getClass().equals(chestAttachments.getAttachmentClass())) {
                return true;
            }
        }
        return false;
    }
}
