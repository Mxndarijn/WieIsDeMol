package nl.mxndarijn.wieisdemol.items.game;

import nl.mxndarijn.api.inventory.MxInventory;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GameHostItem extends MxItem {


    public GameHostItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Game> optionalGame = GameWorldManager.getInstance().getGameByWorldUID(e.getPlayer().getWorld().getUID());
        if(optionalGame.isEmpty())
            return;
        Game game = optionalGame.get();

        if(!game.getHosts().contains(e.getPlayer().getUniqueId()))
            return;

        MxInventoryManager.getInstance().addAndOpenInventory(p, new MxDefaultMenuBuilder(ChatColor.GRAY + "Host-Tool", MxInventorySlots.THREE_ROWS)

                .setItem(MxDefaultItemStackBuilder.create(Material.COMPARATOR)
                                .setName(ChatColor.GRAY + "Verander Game status")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de game status te veranderen.")
                                .build(), 13
                        , (mxInv, e1) -> {
                    MxInventoryManager.getInstance().addAndOpenInventory(p, new MxDefaultMenuBuilder(ChatColor.GRAY + "Verander Game Status", MxInventorySlots.THREE_ROWS)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("ice-block")
                                                    .setName(UpcomingGameStatus.FREEZE.getStatus())
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de status te veranderen naar: " + UpcomingGameStatus.FREEZE.getStatus())
                                                    .build(),
                                            10,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.FREEZE);
                                                p.closeInventory();
                                            })
                            .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-block")
                                                    .setName(UpcomingGameStatus.FINISHED.getStatus())
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de status te veranderen naar: " + UpcomingGameStatus.FINISHED.getStatus())
                                                    .build(),
                                            16,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.FINISHED);
                                                p.closeInventory();
                                            })
                            .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("light-green-block")
                                                    .setName(UpcomingGameStatus.PLAYING.getStatus())
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de status te veranderen naar: " + UpcomingGameStatus.PLAYING.getStatus())
                                                    .build(),
                                            13,
                                            (mxInv1, e2) -> {
                                                game.setGameStatus(UpcomingGameStatus.PLAYING);
                                                p.closeInventory();
                                            })
                                    .build());

                        })
                .build());

    }
}
