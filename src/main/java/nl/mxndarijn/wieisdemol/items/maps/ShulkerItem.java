package nl.mxndarijn.wieisdemol.items.maps;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Colors;
import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.wieisdemol.managers.shulkers.ShulkerInformation;
import org.bukkit.*;
import org.bukkit.block.Block;
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

    @EventHandler
    public void interactEvent(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        assert e.getClickedBlock() != null;
        if (!(e.getClickedBlock().getState() instanceof ShulkerBox)) {
            return;
        }

        Player p = e.getPlayer();
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();
        Optional<ShulkerInformation>  optionalShulkerInformation = map.getShulkerManager().getShulkerByLocation(MxLocation.getFromLocation(e.getClickedBlock().getLocation()));
        if(optionalShulkerInformation.isEmpty()) {
            return;
        }

        ShulkerInformation shulkerInformation = optionalShulkerInformation.get();

        Optional<Colors> c = Colors.getColorByMaterial(shulkerInformation.getMaterial());
        if(c.isEmpty())
            return;

        Optional<MapPlayer> mp = map.getMapConfig().getMapPlayerOfColor(c.get());
        if(mp.isEmpty())
            return;

        String title = mp.get().getRole().getUnicode();

        Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, "Changing shulker name...");
        ShulkerBox shulkerBox = (ShulkerBox) e.getClickedBlock().getState();
        shulkerBox.customName(Component.text(title));
        shulkerBox.update();
        // change name of shulker inventory
    }

}
