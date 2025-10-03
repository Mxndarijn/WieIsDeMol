package nl.mxndarijn.wieisdemol.managers.chests.chestattachments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ContainerLockAttachment extends ContainerAttachment {
    private static final HashMap<Boolean, String> map = new HashMap<>() {{
        put(true, "locked-chest");
        put(false, "open-chest");
    }};
    String currentHead = "";
    private String lockTag;
    private boolean locked = true;

    public static Optional<ContainerLockAttachment> createFromSection(Map<String, Object> section, ContainerInformation information) {
        ContainerLockAttachment attachment = new ContainerLockAttachment();
        if (!getDefaultValues(attachment, information, section)) {
            return Optional.empty();
        }
        assert (section != null);

        if (!section.containsKey("lockTag")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load lockTag.  Type: " + attachment.type);
            return Optional.empty();
        }
        attachment.locked = (Boolean) section.get("locked") ? (Boolean) section.get("locked") : true;
        attachment.setLockTag((String) section.get("lockTag"));

        return Optional.of(attachment);
    }

    public static ContainerAttachment createNewInstance(String type, ContainerInformation information) {
        ContainerLockAttachment attachment = new ContainerLockAttachment();
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
                        .setName("<green>Container slot")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om deze container attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
                    MxInventoryManager.getInstance().addAndOpenInventory(p.getUniqueId(),
                            MxDefaultMenuBuilder.create("Container slot", MxInventorySlots.THREE_ROWS)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("locked-chest")
                                                    .setName("<gray>Krijg sleutel")
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om het item in je off-hand de sleutel te maken.")
                                                    .build(),
                                            13,
                                            (mxInv1, e1) -> {
                                                ItemStack is = p.getInventory().getItemInOffHand();
                                                if (is.getType() == Material.AIR) {
                                                    p.closeInventory();
                                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_NO_ITEM_IN_HAND));
                                                    return;
                                                }

                                                ItemMeta im = is.getItemMeta();
                                                if (im == null) {
                                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_NO_ITEM_IN_HAND));
                                                    return;
                                                }
                                                NamespacedKey nbtKey = new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "lockTag");
                                                im.getPersistentDataContainer().set(nbtKey, PersistentDataType.STRING, lockTag);

                                                List<Component> list = im.lore();
                                                if (list == null) {
                                                    list = new ArrayList<>();
                                                }
                                                list.add(MiniMessage.miniMessage().deserialize("<!i>" + ""));
                                                list.add(MiniMessage.miniMessage().deserialize("<!i>" + "<gold>Sleutel"));

                                                im.lore(list);
                                                is.setItemMeta(im);

                                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_ATTACHMENT_LOCK_ITEM_SET));
                                            }
                                    )

                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-minus")
                                                    .setName("<red>Verwijder container attachment")
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om de container attachment te verwijderen")

                                                    .build(), 18,
                                            (mxInv12, e12) -> {
                                                information.removeChestAttachment(p, this, ContainerAttachments.CONTAINER_LOCK);
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

        String h = map.get(locked);
        if (!h.equals(currentHead)) {
            armorStand.get().getEquipment().setHelmet(MxSkullItemStackBuilder.create(1).setSkinFromHeadsData(h).build());
            currentHead = h;
        }

    }

    @Override
    public boolean canOpenChest(GamePlayer gamePlayer) {
        return !locked;
    }

    @Override
    public void onChestInteract(GamePlayer gamePlayer, PlayerInteractEvent e, Game game, Player p) {
        if (!locked)
            return;
        ItemStack is = p.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR) {
            return;
        }

        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return;
        }
        NamespacedKey nbtKey = new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "lockTag");
        String s = im.getPersistentDataContainer().get(nbtKey, PersistentDataType.STRING);
        if (s != null) {
            if (s.equals(lockTag)) {
                locked = false;
                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_CHEST_ATTACHMENTS_CHEST_UNLOCKED));
            }
        }
    }
}
