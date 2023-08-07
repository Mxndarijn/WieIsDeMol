package nl.mxndarijn.logic.items.maps;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.Colors;
import nl.mxndarijn.logic.inventory.MxInventoryIndex;
import nl.mxndarijn.logic.inventory.MxInventoryManager;
import nl.mxndarijn.logic.inventory.MxInventorySlots;
import nl.mxndarijn.logic.inventory.MxItemClicked;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.logic.items.util.MxItem;
import nl.mxndarijn.logic.map.mapplayer.MapPlayer;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import nl.mxndarijn.managers.chests.ChestInformation;
import nl.mxndarijn.logic.map.Map;
import nl.mxndarijn.managers.MapManager;
import nl.mxndarijn.managers.shulkers.ShulkerInformation;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            assert e.getClickedBlock() != null;
            if(e.getClickedBlock().getType() != Material.CHEST) {
                return;
            }
            Optional<ChestInformation> inf = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(e.getClickedBlock().getLocation()));
            if(inf.isPresent()) {
                inf.get().openAttachmentsInventory(e.getPlayer());
            } else {
                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_COULD_NOT_BE_FOUND));
            }
            return;
        }

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        map.getChestManager().getChests().forEach(chest -> {
            list.add(new Pair<>(
                    MxDefaultItemStackBuilder.create(Material.CHEST, 1)
                            .setName(ChatColor.GRAY + chest.getName())
                            .addBlankLore()
                            .addLore(ChatColor.GRAY + "Location: " + chest.getLocation().getX() + " " + chest.getLocation().getY() + " " + chest.getLocation().getZ())
                            .addBlankLore()
                            .addLore(ChatColor.YELLOW + "Klik om de kist op afstand te openen.")
                            .build(),
                    (mxInv, e12) -> {
                        Location loc = chest.getLocation().getLocation(p.getWorld());
                        Block block = p.getWorld().getBlockAt(loc);
                        if(block.getType() == Material.CHEST) {
                            Chest chestBlock = (Chest) block.getState();
                            p.openInventory(chestBlock.getBlockInventory());
                        } else {
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_IS_NOT_A_CHEST));
                            p.closeInventory();
                        }
                    }
            ));
        });


        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Chest Hulp Tool", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                .build());

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

    @EventHandler
    public void open(InventoryOpenEvent e) {
        Location loc = e.getInventory().getLocation();
        if(loc == null) {
            return;
        }
        if(!(e.getInventory().getHolder() instanceof Chest))
            return;
        Block b = loc.getBlock();

        Player p = (Player) e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        Optional<ChestInformation>  optionalChestInformation = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(b.getLocation()));
        if(optionalChestInformation.isEmpty()) {
            return;
        }

        ChestInformation chestInformation = optionalChestInformation.get();
        chestInformation.getChestAttachmentList().forEach(a -> {
            a.onOpenChest(e) ;
        });

    }
}
