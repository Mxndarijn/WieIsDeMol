package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.heads.MxHeadManager;
import nl.mxndarijn.api.inventory.heads.MxHeadSection;
import nl.mxndarijn.api.inventory.heads.MxHeadsType;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SkullsCommand extends MxCommand {

    public SkullsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    public SkullsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        MxItemClicked giver = (inv, e) -> {
            if (e.getCurrentItem() != null) {
                ItemStack skullItemStack = e.getCurrentItem().clone();
                ItemMeta im = skullItemStack.getItemMeta();
                im.lore(Collections.emptyList());
                skullItemStack.setItemMeta(im);
                if (p.hasPermission(Permissions.COMMAND_SKULLS_REMOVE_SKULL.getPermission())) {
                    if (e.getClick() == ClickType.SHIFT_LEFT) {
                        MxItemClicked deleteSkull = (mxInv, e12) -> {
                            if (e12.getCurrentItem() != null) {
                                ItemStack is = e12.getCurrentItem();
                                boolean deleteSkullItem = is.getType() == Material.LIME_STAINED_GLASS_PANE;
                                e.getWhoClicked().closeInventory();
                                if (deleteSkullItem) {
                                    PersistentDataContainer container = im.getPersistentDataContainer();
                                    MxHeadManager.getInstance().removeHead(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "skull_key"), PersistentDataType.STRING));
                                    MSG.msg(e.getWhoClicked(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_DELETED, Collections.emptyList()));
                                } else {
                                    MxInventoryManager.getInstance().addAndOpenInventory(p, inv);
                                    MSG.msg(e.getWhoClicked(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_NOT_DELETED, Collections.emptyList()));
                                }

                            }
                        };
                        MxDefaultMenuBuilder menuBuilder =
                                MxDefaultMenuBuilder.create("<gray>Skull verwijderen", MxInventorySlots.THREE_ROWS)
                                        .setItem(skullItemStack, 13, null)
                                        .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE, 1)
                                                .setName("<green>Ja")
                                                .addLore(" ")
                                                .addLore("<yellow>Ja, verwijder de skull.")
                                                .build(), 15, deleteSkull)
                                        .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE, 1)
                                                .setName("<red>Nee")
                                                .addLore(" ")
                                                .addLore("<yellow>Nee, verwijder de skull niet.")
                                                .build(), 11, deleteSkull)
                                        .setPrevious(inv);

                        MxInventoryManager.getInstance().addAndOpenInventory(p, menuBuilder.build());
                        return;
                    }
                }
                e.getWhoClicked().getInventory().addItem(skullItemStack);
            }
        };
        MxHeadManager instance = MxHeadManager.getInstance();
        instance.getAllHeadKeys().forEach(key -> {
            Optional<MxHeadSection> section = instance.getHeadSection(key);
            section.ifPresent(mxHeadSection -> {
                MxSkullItemStackBuilder b = MxSkullItemStackBuilder.create(1)
                        .setSkinFromHeadsData(key)
                        .setName("<gray>" + mxHeadSection.getName().get())
                        .addLore(" ")
                        .addLore("<yellow>Klik om dit item toe te voegen aan je inventory.")
                        .addCustomTagString("skull_key", mxHeadSection.getKey());
                if (p.hasPermission(Permissions.COMMAND_SKULLS_REMOVE_SKULL.getPermission())) {
                    b.addLore("<yellow>Shift-klik op dit item om het te verwijderen.");
                }
                list.add(new Pair<>(b.build(), giver));
            });
        });
        MxListInventoryBuilder builder =
                MxListInventoryBuilder.create("<red>Heads-Database", MxInventorySlots.SIX_ROWS)
                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                        .addListItems(list)
                        .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                .setName("<gray>Info")
                                .addLore(" ")
                                .addLore("<gray>Wil je een skull toevoegen?")
                                .addLore("<gray>Vraag een stafflid dit te doen.")
                                .build(), 48, null);

        if (p.hasPermission(Permissions.COMMAND_SKULLS_ADD_SKULL.getPermission())) {
            builder.setItem(MxDefaultItemStackBuilder.create(Material.SKELETON_SKULL)
                    .setName("<gray>Voeg een skull toe")
                    .addLore(" ")
                    .addLore("<yellow>Klik hier om een skull toe te voegen.")
                    .build(), 50, (inv, e) -> {
                MxDefaultMenuBuilder menuBuilder = MxDefaultMenuBuilder.create("<gray>Skull toevoegen", MxInventorySlots.FIVE_ROWS)
                        .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                .setName("<gray>Info")
                                .addLore(" ")
                                .addLore("<gray>Klik op de skull die je wilt toevoegen.")
                                .addLore("<gray>De skull moet in je inventory zitten")
                                .addLore("<gray>om hem toe te kunnen voegen.")
                                .build(), 40, null)
                        .setPrevious(inv);

                MxItemClicked clickedOnSkull = (mxInv, e1) -> {
                    if (e1.getCurrentItem() == null)
                        return;
                    ItemStack skull = e1.getCurrentItem();
                    MxItemClicked clickedOnPlayerSkullQuestion = (mxInv1, e2) -> {
                        if (e2.getCurrentItem() != null) {
                            ItemStack is = e2.getCurrentItem();
                            boolean isPlayerSkull = is.getType() == Material.LIME_STAINED_GLASS_PANE;
                            e.getWhoClicked().closeInventory();
                            MSG.msg(e.getWhoClicked(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_GET_CHAT_INPUT, Collections.emptyList()));
                            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                if (MxHeadManager.getInstance().storeSkullTexture(skull, UUID.randomUUID().toString(), message, isPlayerSkull ? MxHeadsType.PLAYER : MxHeadsType.MANUALLY_ADDED)) {
                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_SKULL_ADDED, Collections.emptyList()));
                                } else {
                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_SKULL_NOT_ADDED, Collections.emptyList()));
                                }
                            });
                        }
                    };
                    MxDefaultMenuBuilder menuBuilder1 = MxDefaultMenuBuilder.create("<gray>Is het een PlayerSkull?", MxInventorySlots.THREE_ROWS)
                            .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                    .setName("<gray>Info")
                                    .addLore(" ")
                                    .addLore("<gray>Is het item een PlayerSkull:")
                                    .addLore("<gray>- Het is geen custom skull.")
                                    .addLore("<gray>- Elke restart moet de skin opnieuw worden opgevraagd.")
                                    .addLore("<gray>- De skull heeft een eigenaar.")
                                    .build(), 13, null)
                            .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE, 1)
                                    .setName("<green>Ja")
                                    .addLore(" ")
                                    .addLore("<yellow>Ja, het is een PlayerSkull.")
                                    .build(), 15, clickedOnPlayerSkullQuestion)
                            .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE, 1)
                                    .setName("<red>Nee")
                                    .addLore(" ")
                                    .addLore("<yellow>Nee, het is geen PlayerSkull.")
                                    .build(), 11, clickedOnPlayerSkullQuestion)
                            .setPrevious(mxInv);
                    MxInventoryManager.getInstance().addAndOpenInventory(p, menuBuilder1.build());
                    MSG.msg(e.getWhoClicked(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_IS_SKULL_PLAYER_SKULL, Collections.emptyList()));
                };
                Arrays.stream(e.getWhoClicked().getInventory().getContents()).forEach(itemStack -> {
                    if (itemStack != null && itemStack.getType() == Material.PLAYER_HEAD) {
                        ItemStack is = itemStack.clone();
                        is.setAmount(1);
                        menuBuilder.addItem(is, clickedOnSkull);
                    }
                });
                MxInventoryManager.getInstance().addAndOpenInventory(p, menuBuilder.build());
            });
        }
        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_SKULLS_DEFAULT, Collections.emptyList()));
        MxInventoryManager.getInstance().addAndOpenInventory(
                p,
                builder.build()
        );
    }


}
