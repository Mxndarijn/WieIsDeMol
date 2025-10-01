package nl.mxndarijn.wieisdemol.items.game;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Role;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.warps.Warp;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GameHostItem extends MxItem {


    public GameHostItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Game> optionalGame = GameWorldManager.getInstance().getGameByWorldUID(e.getPlayer().getWorld().getUID());
        if (optionalGame.isEmpty())
            return;
        Game game = optionalGame.get();

        if (!game.getHosts().contains(e.getPlayer().getUniqueId()))
            return;

        MxInventoryManager.getInstance().addAndOpenInventory(p, new MxDefaultMenuBuilder("<gray>Host-Tool", MxInventorySlots.THREE_ROWS)

                .setItem(MxDefaultItemStackBuilder.create(Material.COMPARATOR)
                                .setName("<gray>Verander Game status")
                                .addBlankLore()
                                .addLore("<yellow>Klik hier om de game status te veranderen.")
                                .build(), 13
                        , (mxInv, e1) -> {
                            MxInventoryManager.getInstance().addAndOpenInventory(p, new MxDefaultMenuBuilder("<gray>Verander Game Status", MxInventorySlots.THREE_ROWS)
                                    .setPrevious(mxInv)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("ice-block")
                                                    .setName(UpcomingGameStatus.FREEZE.getStatus())
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om de status te veranderen naar: " + UpcomingGameStatus.FREEZE.getStatus())
                                                    .build(),
                                            10,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.FREEZE, Optional.empty());
                                                p.closeInventory();
                                            })
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-block")
                                                    .setName(UpcomingGameStatus.FINISHED.getStatus())
                                                    .addBlankLore()
                                                    .addLore("<gray>Hierbij worden geen statistics aangepast!")
                                                    .addLore("<yellow>Klik hier om de status te veranderen naar: " + UpcomingGameStatus.FINISHED.getStatus())
                                                    .build(),
                                            14,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.FINISHED, Optional.empty());
                                                p.closeInventory();
                                            })
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("emerald-block")
                                                    .setName("<dark_green>Ego-Win")
                                                    .addBlankLore()
                                                    .addLore("<gray>Hierbij wint de ego")
                                                    .addLore("<yellow>Klik hier om de status te veranderen naar: " + UpcomingGameStatus.FINISHED.getStatus())
                                                    .addLore("<yellow>Hierbij wint de ego.")
                                                    .build(),
                                            16,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.FINISHED, Optional.of(Role.EGO));
                                                p.closeInventory();
                                            })
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("light-green-block")
                                                    .setName(UpcomingGameStatus.PLAYING.getStatus())
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om de status te veranderen naar: " + UpcomingGameStatus.PLAYING.getStatus())
                                                    .build(),
                                            12,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.PLAYING, Optional.empty());
                                                p.closeInventory();
                                            })

                                    .build());

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.COMPASS)
                                .setName("<gray>Warps")
                                .addBlankLore()
                                .addLore("<yellow>Klik hier om de warps van de game te bekijken.")
                                .build(),
                        18, (mxInv13, e22) -> {
                            List<Warp> warps = game.getWarpManager().getWarps();
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                            warps.forEach(warp -> {
                                list.add(new Pair<>(
                                        MxSkullItemStackBuilder.create(1)
                                                .setSkinFromHeadsData(warp.getSkullId())
                                                .setName("<gray>" + warp.getName())
                                                .addBlankLore()
                                                .addLore("<yellow>Klik om naar deze warp te teleporten.")
                                                .build(),
                                        (mxInv14, e23) -> {
                                            p.teleport(warp.getMxLocation().getLocation(p.getWorld()));
                                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_TELEPORTED));
                                        }
                                ));
                            });
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Warps", MxInventorySlots.THREE_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                                    .setPrevious(mxInv13)
                                    .setListItems(list)
                                    .build()
                            );
                        })
                .setItem(MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData("wooden-plus")
                                .setName("<gray>Voeg host toe")
                                .addBlankLore()
                                .addLore("<yellow>Klik hier om een host toe te voegen.")
                                .build(),
                        11,
                        (mxInv, e12) -> {
                            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_HOST_ADD_ENTER_NAME));
                            p.closeInventory();
                            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    Player player = Bukkit.getPlayer(message);
                                    if (player == null) {
                                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_HOST_ADD_NOT_FOUND));
                                        return;
                                    }
                                    if (game.getHosts().contains(player.getUniqueId())) {
                                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_HOST_ADD_ALREADY_HOST));
                                        return;
                                    }
                                    if (game.getSpectators().contains(player.getUniqueId()) || game.getGameInfo().getQueue().contains(player.getUniqueId())) {
                                        if (game.getSpectators().contains(player.getUniqueId())) {
                                            game.removeSpectator(player.getUniqueId(), false);
                                        }
                                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_HOST_ADD_HOST_ADDED, Collections.singletonList(player.getName())));
                                        game.addHost(player.getUniqueId());
                                    } else {
                                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_HOST_ADD_NOT_IN_QUEUE, Collections.singletonList(player.getName())));
                                    }
                                });
                            });
                        })
                .setItem(MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData("book")
                                .setName("<gray>Votes Beheren")
                                .addBlankLore()
                                .addLore("<yellow>Klik hier om votes te beheren")
                                .build(),
                        15,
                        (mxInv, e13) -> {
                            MxInventoryManager.getInstance().addAndOpenInventory(p,
                                    MxDefaultMenuBuilder.create("<gray>Beheer Votes", MxInventorySlots.THREE_ROWS)
                                            .setPrevious(mxInv)
                                            .setItem(MxSkullItemStackBuilder.create(1)
                                                            .setSkinFromHeadsData("book")
                                                            .setName("<gray>Toggle Votes")
                                                            .addBlankLore()
                                                            .addLore("<gray>Status: " + (game.isPlayersCanEndVote() ? "<green>Spelers kunnen de votes beeindigen" : "<red>Spelers kunnen niet de votes beeindigen"))
                                                            .addBlankLore()
                                                            .addLore("<yellow>Klik hier om de status te togglen")
                                                            .build(),
                                                    12,
                                                    (mxInv12, e14) -> {
                                                        game.setPlayersCanEndVote(!game.isPlayersCanEndVote());
                                                        p.closeInventory();
                                                        MSG.msg(p, (game.isPlayersCanEndVote() ? "<green>Spelers kunnen de votes beeindigen." : "<red>Spelers kunnen niet de votes beeindigen."));
                                                    }
                                            )
                                            .setItem(MxSkullItemStackBuilder.create(1)
                                                            .setSkinFromHeadsData("message-icon")
                                                            .setName("<gray>Laat resultaten zien")
                                                            .addBlankLore()
                                                            .addLore("<yellow>Klik hier om de vote resultaten te bekijken.")
                                                            .build(),
                                                    14,
                                                    (mxInv12, e14) -> {
                                                        game.showVotingResults("Host");
                                                        p.closeInventory();
                                                    }
                                            )
                                            .build()

                            );
                        }
                )
                .build());

    }
}
