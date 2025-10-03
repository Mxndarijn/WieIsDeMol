package nl.mxndarijn.wieisdemol.items.maps;

import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerType;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Chest;
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

public class MapContainerItem extends MxItem {


    public MapContainerItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            assert e.getClickedBlock() != null;
            if (!ContainerType.isSupported(e.getClickedBlock().getType())) {
                return;
            }
            Optional<ContainerInformation> inf = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(e.getClickedBlock().getLocation()));
            if (inf.isPresent()) {
                inf.get().openAttachmentsInventory(e.getPlayer());
            } else {
                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_COULD_NOT_BE_FOUND));
            }
            return;
        }

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        map.getChestManager().getChests().forEach(container -> {
            list.add(new Pair<>(
                    MxDefaultItemStackBuilder.create(container.getType().getIcon(), 1)
                            .setName("<gray>" + container.getName())
                            .addBlankLore()
                            .addLore("<gray>Location: " + container.getLocation().getX() + " " + container.getLocation().getY() + " " + container.getLocation().getZ())
                            .addBlankLore()
                            .addLore("<yellow>Klik om de container op afstand te openen.")
                            .build(),
                    (mxInv, e12) -> {
                        Location loc = container.getLocation().getLocation(p.getWorld());
                        Block block = p.getWorld().getBlockAt(loc);
                        Optional<ContainerType> typeOpt = ContainerType.fromBlock(block);
                        if (typeOpt.isEmpty()) {
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_IS_NOT_A_CHEST));
                            p.closeInventory();
                            return;
                        }
                        if (!typeOpt.get().equals(container.getType())) {
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_IS_NOT_A_CHEST));
                            p.closeInventory();
                            return;
                        }
                        if (block.getState() instanceof Container state) {
                            p.openInventory(state.getInventory());
                        } else if (block.getState() instanceof Chest chestBlock) {
                            p.openInventory(chestBlock.getBlockInventory());
                        } else {
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_CHEST_IS_NOT_A_CHEST));
                            p.closeInventory();
                        }
                    }
            ));
        });


        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Container Hulp Tool", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                .build());

    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!ContainerType.isSupported(e.getBlockPlaced().getType())) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        ContainerType type = ContainerType.fromMaterial(e.getBlockPlaced().getType()).orElse(ContainerType.CHEST);
        map.getChestManager().addChest(new ContainerInformation("Automatisch toegevoegde container", MxLocation.getFromLocation(e.getBlockPlaced().getLocation()), type));
        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_CHEST_ADDED));
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!ContainerType.isSupported(e.getBlock().getType())) {
            return;
        }
        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        Optional<ContainerInformation> info = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(e.getBlock().getLocation()));
        if (info.isEmpty()) {
            return;
        }

        map.getChestManager().removeChest(info.get());
        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_AUTOMATED_CHEST_REMOVED));
    }

    @EventHandler
    public void open(InventoryOpenEvent e) {
        Location loc = e.getInventory().getLocation();
        if (loc == null) {
            return;
        }
        Block b = loc.getBlock();

        Player p = (Player) e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        Optional<ContainerInformation> optionalChestInformation = map.getChestManager().getChestByLocation(MxLocation.getFromLocation(b.getLocation()));
        if (optionalChestInformation.isEmpty()) {
            return;
        }

        ContainerInformation containerInformation = optionalChestInformation.get();
        containerInformation.getChestAttachmentList().forEach(a -> {
            a.onOpenChest(e);
        });

    }
}
