package nl.mxndarijn.items.maps;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.MxInventoryIndex;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.map.Map;
import nl.mxndarijn.world.map.MapManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.shulkers.ShulkerInformation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class ShulkerItem extends MxItem  {


    public ShulkerItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        map.getShulkerManager().getShulkers().forEach(shulker -> {
            list.add(new Pair<>(
                    MxDefaultItemStackBuilder.create(shulker.getMaterial(), 1)
                            .setName(ChatColor.GRAY + shulker.getName())
                            .addBlankLore()
                            .addLore(ChatColor.GRAY + "Location: " + shulker.getLocation().getX() + " " + shulker.getLocation().getY() + " " + shulker.getLocation().getZ())
                            .addBlankLore()
                            .addLore(ChatColor.YELLOW + "Klik om de shulker op afstand te openen.")
                            .build(),
                    (mxInv, e12) -> {
                        World w = Bukkit.getWorld(map.getMxWorld().get().getWorldUID());
                        Location loc = shulker.getLocation().getLocation(w);
                        if(map.getMxWorld().isEmpty()) {
                            return;
                        }
                        Block block = loc.getBlock();
                        if(block.getState() instanceof ShulkerBox) {
                            ShulkerBox shulkerBox = (ShulkerBox) block.getState();
                            p.openInventory(shulkerBox.getInventory());
                        } else {
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SHULKER_IS_NOT_A_SHULKER));
                            p.closeInventory();
                        }
                    }
            ));
        });


        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Shulker Hulp Tool", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                .build());

    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!(e.getBlockPlaced().getState() instanceof ShulkerBox)) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        map.getShulkerManager().addShulker(new ShulkerInformation("Automatisch toegevoegde shulker", MxLocation.getFromLocation(e.getBlockPlaced().getLocation()), e.getBlock().getType()));
        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_SHULKER_ADDED));
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!(e.getBlock().getState() instanceof ShulkerBox)) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
       Optional<ShulkerInformation>  info = map.getShulkerManager().getShulkerByLocation(MxLocation.getFromLocation(e.getBlock().getLocation()));
       if(info.isEmpty()) {
           return;
       }

        map.getShulkerManager().removeShulker(info.get());
        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_SHULKER_REMOVED));
    }
}
