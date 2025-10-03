package nl.mxndarijn.wieisdemol.managers.chests.chestattachments;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContainerLimitedChoiceAttachment extends ContainerAttachment {
    private static final HashMap<Integer, String> map = new HashMap<>() {{
        put(0, "number-zero");
        put(1, "number-one");
        put(2, "number-two");
        put(3, "number-three");
        put(4, "number-four");
        put(5, "number-five");
        put(6, "number-six");
        put(7, "number-seven");
        put(8, "number-eight");
        put(9, "number-nine");
    }};
    private static final String moreThan9 = "wooden-plus";
    private int choices;
    private String currentHead = "";

    public static Optional<ContainerAttachment> createFromSection(Map<String, Object> section, ContainerInformation inf) {
        ContainerLimitedChoiceAttachment attachment = new ContainerLimitedChoiceAttachment();
        if (!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert (section != null);

        if (!section.containsKey("choices")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load choices amount. Type: " + attachment.type);
            return Optional.empty();
        }
        attachment.setChoices((int) section.get("choices"));

        return Optional.of(attachment);
    }

    public static ContainerAttachment createNewInstance(String type, ContainerInformation inf) {
        ContainerLimitedChoiceAttachment attachment = new ContainerLimitedChoiceAttachment();
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
                        .setName("<green>Keuze container")
                        .addBlankLore()
                        .addLore("<gray>Max items te verwijderen: " + choices)
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om deze container attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
//                    Optional<nl.mxndarijn.world.map.Map> opt = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());
//                    if(opt.isEmpty())
//                        return;
//
//                    nl.mxndarijn.world.map.Map map = opt.get();
                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Keuze container", MxInventorySlots.THREE_ROWS)
                            .setItem(MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData("open-chest")
                                            .setName("<green>Verander aantal")
                                            .addBlankLore()
                                            .addLore("<gray>Max items te verwijderen: " + choices)
                                            .addBlankLore()
                                            .addLore("<yellow>Klik hier om het aantal items dat uit de container kan worden gehaald")
                                            .addLore("<yellow>te veranderen.")
                                            .build(),
                                    13,
                                    (mxInv1, e1) -> {
                                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_ENTER_NEW_AMOUNT));
                                        p.closeInventory();
                                        MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                            try {
                                                int i = Integer.parseInt(message);
                                                choices = i;
                                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_CHANGED_TO, Collections.singletonList(i + "")));

                                            } catch (NumberFormatException ee) {
                                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LIMITED_NOT_A_NUMBER));
                                            }
                                        });
                                    }

                            )
                            .setItem(MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData("red-minus")
                                            .setName("<red>Verwijder chest attachment")
                                            .addBlankLore()
                                            .addLore("<yellow>Klik hier om de chest attachment te verwijderen")

                                            .build(), 18,
                                    (mxInv12, e12) -> {
                                        information.removeChestAttachment(p, this, ContainerAttachments.CONTAINER_LIMITED_CHOICE);
                                        p.closeInventory();
                                    }
                            )
                            .setItem(MxDefaultItemStackBuilder.create(Material.BARRIER)
                                            .setName("<gray>Terug")
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
    public void onGameStart(Game game) {
        super.onGameStart(game);
        spawnArmorStand();
    }

    @Override
    public void onGameUpdate(long delta) {
        super.onGameUpdate(delta);
        if (armorStand.isEmpty())
            return;
        String head = map.getOrDefault(choices, moreThan9);
        if (!currentHead.equals(head)) {
            armorStand.get().getEquipment().setHelmet(MxSkullItemStackBuilder.create(1).setSkinFromHeadsData(head).build());
            currentHead = head;
        }

    }

    @Override
    public void onChestInventoryClick(GamePlayer gamePlayer, InventoryClickEvent e, Game game, Player p) {
        if (choices == 0) {
            e.setCancelled(true);
            return;
        }
        Inventory inv = e.getClickedInventory();
        InventoryAction a = e.getAction();
        if (a == InventoryAction.COLLECT_TO_CURSOR || a == InventoryAction.PICKUP_ALL) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            choices--;
        } else {
            e.setCancelled(true);
        }
    }
}
