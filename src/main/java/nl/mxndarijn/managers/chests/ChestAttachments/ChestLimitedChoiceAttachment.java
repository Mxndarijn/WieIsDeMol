package nl.mxndarijn.managers.chests.ChestAttachments;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.logic.inventory.*;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.managers.chatinput.MxChatInputManager;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.logic.util.logger.Prefix;
import nl.mxndarijn.managers.chests.ChestInformation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestLimitedChoiceAttachment extends ChestAttachment {
    private int choices;

    public static Optional<ChestAttachment> createFromSection(Map<String, Object> section, ChestInformation inf) {
        ChestLimitedChoiceAttachment attachment = new ChestLimitedChoiceAttachment();
        if(!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert(section != null);

        if(!section.containsKey("choices")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load choices amount. Type: " + attachment.type);
            return Optional.empty();
        }
        attachment.setChoices((int)section.get("choices"));

        return Optional.of(attachment);
    }

    public static ChestAttachment createNewInstance(String type, ChestInformation inf) {
        ChestLimitedChoiceAttachment attachment = new ChestLimitedChoiceAttachment();
        attachment.setDefaults(type, inf);
        attachment.choices = 1;
        return attachment;
    }

    public void setChoices(int choices) {
        this.choices = choices;
    }


    @Override
    public Map<String, Object> getDataForSaving() {
        Map<String, Object> map = new HashMap<>();
        getDataDefaults(map);
        map.put("choices", choices);

        return map;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        return new Pair<>(
                MxSkullItemStackBuilder.create(1)
                        .setSkinFromHeadsData("open-chest")
                        .setName(ChatColor.GREEN + "Keuze kist")
                        .addBlankLore()
                        .addLore(ChatColor.GRAY + "Max items te verwijderen: " + choices)
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
//                    Optional<nl.mxndarijn.world.map.Map> opt = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());
//                    if(opt.isEmpty())
//                        return;
//
//                    nl.mxndarijn.world.map.Map map = opt.get();
                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create(ChatColor.GRAY + "Keuze kist", MxInventorySlots.THREE_ROWS)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("open-chest")
                                                    .setName(ChatColor.GREEN + "Verander aantal")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.GRAY + "Max items te verwijderen: " + choices)
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om het aantal items dat uit de kist kan worden gehaald")
                                                    .addLore(ChatColor.YELLOW + "te veranderen.")
                                                    .build(),
                                            13,
                                            (mxInv1, e1) -> {
                                                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_ENTER_NEW_AMOUNT));
                                                p.closeInventory();
                                                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                                    try {
                                                        int i = Integer.parseInt(message);
                                                        choices = i;
                                                        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_CHANGED_TO, Collections.singletonList(i + "")));

                                                    } catch (NumberFormatException ee) {
                                                        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_NOT_A_NUMBER));
                                                    }
                                                });
                                            }

                                    )
                            .setItem(MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData("red-minus")
                                            .setName(ChatColor.RED + "Verwijder chest attachment")
                                            .addBlankLore()
                                            .addLore(ChatColor.YELLOW + "Klik hier om de chest attachment te verwijderen")

                                            .build(), 18,
                                    (mxInv12, e12) -> {
                                        information.removeChestAttachment(p, this, ChestAttachments.CHEST_LIMITED_CHOICE);
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
