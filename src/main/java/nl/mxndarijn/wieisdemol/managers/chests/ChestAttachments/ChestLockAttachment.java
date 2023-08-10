package nl.mxndarijn.wieisdemol.managers.chests.ChestAttachments;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.managers.chests.ChestInformation;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.api.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.List;

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

    public static ChestAttachment createNewInstance(String type,ChestInformation information) {
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
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
                    MxInventoryManager.getInstance().addAndOpenInventory(p.getUniqueId(),
                            MxDefaultMenuBuilder.create("Kist slot", MxInventorySlots.THREE_ROWS)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("locked-chest")
                                                    .setName(ChatColor.GRAY + "Krijg sleutel")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om het item in je hand de sleutel te maken.")
                                                    .build(),
                                            13,
                                            (mxInv1, e1) -> {
                                                ItemStack is = p.getInventory().getItemInOffHand();
                                                if(is.getType() == Material.AIR) {
                                                    p.closeInventory();
                                                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_NO_ITEM_IN_HAND));
                                                    return;
                                                }

                                                ItemMeta im = is.getItemMeta();
                                                if(im == null) {
                                                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_NO_ITEM_IN_HAND));
                                                    return;
                                                }
                                                NamespacedKey nbtKey = new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), lockTag);
                                                im.getPersistentDataContainer().set(nbtKey, PersistentDataType.STRING, "YourValue");

                                                List<Component> list = im.lore();
                                                if(list == null) {
                                                    list = new ArrayList<>();
                                                }
                                                list.add(Component.text(""));
                                                list.add(Component.text(ChatColor.GOLD + "Sleutel"));

                                                im.lore(list);
                                                is.setItemMeta(im);

                                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_ITEM_SET));
                                            }
                                    )

                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-minus")
                                                    .setName(ChatColor.RED + "Verwijder chest attachment")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de chest attachment te verwijderen")

                                                    .build(), 18,
                                            (mxInv12, e12) -> {
                                                information.removeChestAttachment(p, this, ChestAttachments.CHEST_COLOR_BIND);
                                                p.closeInventory();
                                            }
                                    )
                                    .setItem(MxDefaultItemStackBuilder.create(Material.BARRIER)
                                                    .setName(ChatColor.GRAY + "Terug")
                                                    .build()
                                            , 22,
                                            (mxInv13, e13) -> {
                                                information.openAttachmentsInventory(p);
                                            })
                                    .build()
                    );
                }

        );
    }
}
