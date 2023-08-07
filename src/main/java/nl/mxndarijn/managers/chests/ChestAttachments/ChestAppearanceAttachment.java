package nl.mxndarijn.managers.chests.ChestAttachments;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.ChestAppearance;
import nl.mxndarijn.logic.inventory.*;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.logic.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.logic.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.logic.util.Functions;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.logic.util.logger.Prefix;
import nl.mxndarijn.managers.chests.ChestInformation;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestAppearanceAttachment extends ChestAttachment {
    private ChestAppearance appearance;

    public static Optional<ChestAppearanceAttachment> createFromSection(Map<String, Object> section, ChestInformation inf) {
        ChestAppearanceAttachment attachment = new ChestAppearanceAttachment();
        if(!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert(section != null);

        if(!section.containsKey("appearance")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load appearance.  Type: " + attachment.type);
            return Optional.empty();
        }
        if(!attachment.setAppearance(ChestAppearance.valueOf((String) section.get("appearance")))) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not find appearance.  Type: " + attachment.type);
            return Optional.empty();
        }

        return Optional.of(attachment);
    }

    public boolean setAppearance(ChestAppearance appearance) {
        this.appearance = appearance;

        return appearance != null;
    }

    public static ChestAttachment createNewInstance(String type, ChestInformation inf) {
        ChestAppearanceAttachment attachment = new ChestAppearanceAttachment();
        attachment.setDefaults(type,  inf);
        attachment.appearance = ChestAppearance.CHOICE_THREE;
        return attachment;
    }


    @Override
    public Map<String, Object> getDataForSaving() {
        Map<String, Object> map = new HashMap<>();
        getDataDefaults(map);
        map.put("appearance", appearance.name());

        return map;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        return new Pair<>(
                MxSkullItemStackBuilder.create(1)
                        .setSkinFromHeadsData("rainbow-chest")
                        .setName(ChatColor.GREEN + "Kist uiterlijk")
                        .addBlankLore()
                        .addLore(ChatColor.GRAY + "Uiterlijk: " + appearance.getName())
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
                    MxInventoryManager.getInstance().addAndOpenInventory(p.getUniqueId(),
                            MxDefaultMenuBuilder.create("Kist uiterlijk", MxInventorySlots.THREE_ROWS)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("rainbow-chest")
                                                    .setName(ChatColor.GRAY + "Verander kist uiterlijk")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.GRAY + "Huidige status: " + appearance.getName())
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om het kist uiterlijk aan te passen")
                                                    .build(),
                                            13,
                                            (mxInv1, e1) -> {
                                                 ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                                for (ChestAppearance value : ChestAppearance.values()) {
                                                    list.add(new Pair<>(
                                                            MxSkullItemStackBuilder.create(1)
                                                                    .setSkinFromHeadsData("rainbow-chest")
                                                                    .addBlankLore()
                                                                    .setName(ChatColor.GRAY + value.getName())
                                                                    .addLore(ChatColor.YELLOW + "Klik hier om het uiterlijk aan te passen naar: " + value.getName())
                                                                    .build(),
                                                            (mxInv2, e2) -> {
                                                                appearance = value;
                                                                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_APPEARANCE_CHANGED_TO, Collections.singletonList(value.getName())));
                                                                information.openAttachmentsInventory(p);
                                                            }
                                                    ));
                                                }


                                                MxInventoryManager.getInstance().addAndOpenInventory(p.getUniqueId(),
                                                        MxListInventoryBuilder.create("Verander kist uiterlijk", MxInventorySlots.THREE_ROWS)
                                                                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                                                                .setListItems(list)
                                                                .setPrevious(mxInv1)
                                                                .setPreviousItemStackSlot(22)
                                                                .build());
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
                                                Block b = p.getWorld().getBlockAt(information.getLocation().getLocation(p.getWorld()));
                                                if(!(b.getState() instanceof Chest))
                                                    return;
                                                Chest c = (Chest) b.getState();
                                                c.customName(null);
                                                c.update();
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

    @Override
    public void onOpenChest(InventoryOpenEvent e) {
        Block b = e.getInventory().getLocation().getWorld().getBlockAt(information.getLocation().getLocation(e.getInventory().getLocation().getWorld()));
        Chest chestState = (Chest) b.getState();
        if(chestState.customName() != null && Functions.convertComponentToString(chestState.customName()).equalsIgnoreCase(appearance.getUnicode())) {
            return;
        }
        chestState.customName(Component.text(appearance.getUnicode()));
        chestState.update();

        e.setCancelled(true);
        Player p = (Player) e.getPlayer();
        p.closeInventory();
        p.openInventory(chestState.getInventory());
    }
}
