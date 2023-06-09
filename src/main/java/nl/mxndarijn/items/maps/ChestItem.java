package nl.mxndarijn.items.maps;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.map.Map;
import nl.mxndarijn.world.map.MapManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ChestItem extends MxItem  {


    public ChestItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();

    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() != Material.CHEST) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        map.getChestManager().addChest(new ChestInformation("Automatisch toegevoegde kist", MxLocation.getFromLocation(e.getBlockPlaced().getLocation())));
        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_CHEST_ADDED));
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.CHEST) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
       Optional<ChestInformation>  info = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(e.getBlock().getLocation()));
       if(info.isEmpty()) {
           return;
       }

        map.getChestManager().removeChest(info.get());
        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_CHEST_REMOVED));
    }
}
