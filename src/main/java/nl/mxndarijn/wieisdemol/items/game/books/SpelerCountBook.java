package nl.mxndarijn.wieisdemol.items.game.books;

import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.Role;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SpelerCountBook extends Book {
    public SpelerCountBook(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        getGame(p.getWorld());
        if(game == null)
            return;

        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(p.getUniqueId());

        if(optionalGamePlayer.isPresent()) {
            if(game.getGameInfo().getStatus() != UpcomingGameStatus.PLAYING)
                return;
            GamePlayer gp = optionalGamePlayer.get();
            if(!gp.isAlive())
                return;

            for (Map.Entry<Integer, ? extends ItemStack> entry : p.getInventory().all(is.getType()).entrySet()) {
                Integer key = entry.getKey();
                ItemStack value = entry.getValue();
                if (isItemTheSame(value)) {
                    if (value.getAmount() > 1) {
                        value.setAmount(value.getAmount() - 1);

                        p.getInventory().setItem(key, value);
                    } else {
                        p.getInventory().setItem(key, new ItemStack(Material.AIR));
                    }
                    AtomicInteger count = new AtomicInteger(0);
                    game.getColors().forEach(g -> {
                        if(!g.isAlive())
                            return;
                        if(g.getPlayer().isEmpty())
                            return;
                        if(g.getMapPlayer().getRole() == Role.SPELER) {
                            count.getAndIncrement();
                        }
                    });
                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_SPELERCOUNT_MESSAGE, Collections.singletonList(count.get() + "")));
                    break;
                }
            }
        }
    }
}