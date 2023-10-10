package nl.mxndarijn.wieisdemol.items.game.spectate;

import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TeleportItem extends MxItem {
    public TeleportItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Game> optionalGame = GameWorldManager.getInstance().getGameByWorldUID(p.getWorld().getUID());
        if(optionalGame.isPresent()) {
            if(optionalGame.get().getSpectators().contains(p.getUniqueId())) {
                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                optionalGame.get().getColors().forEach( gp -> {
                    if(gp.getPlayer().isPresent() && gp.isAlive()) {
                        Player pl = Bukkit.getPlayer(gp.getPlayer().get());
                        if(pl != null) {
                        list.add(new Pair<>(MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData(gp.getPlayer().get().toString())
                                .setName(ChatColor.GRAY + pl.getName())
                                        .addBlankLore()
                                        .addLore(ChatColor.GRAY + "Kleur: " + gp.getMapPlayer().getColor().getDisplayName())
                                .build(),
                                (mxInv, e1) -> {
                                    p.teleport(pl.getLocation());
                                    p.closeInventory();
                                }));
                        }
                    }
                });
                if(list.isEmpty())
                    return;
                MxInventoryManager.getInstance().addAndOpenInventory(p, new MxListInventoryBuilder(ChatColor.GRAY + "Teleporteer naar speler", MxInventorySlots.THREE_ROWS)
                        .setListItems(list)
                                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_THREE)
                        .build());

            }
        }
    }
}
