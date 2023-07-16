package nl.mxndarijn.items.maps;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.MxInventoryIndex;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.world.doors.DoorInformation;
import nl.mxndarijn.world.map.Map;
import nl.mxndarijn.world.map.MapManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.shulkers.ShulkerInformation;
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

public class DoorItem extends MxItem  {


    public DoorItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if(mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();

        World w = Bukkit.getWorld(map.getMxWorld().get().getWorldUID());
        if(w == null) {
            return;
        }

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        map.getDoorManager().getDoors().forEach(door -> {
            String status = ChatColor.RED + "Lege deur";
            boolean opened;
            boolean foundDoor =door.getLocations().size() > 0;
            if(foundDoor) {
                MxLocation inf = door.getLocations().keySet().iterator().next();
                Location loc = inf.getLocation(w);
                Block placedBlock = loc.getBlock();
                if(placedBlock.getType() != Material.AIR) {
                    opened = false;
                    status = ChatColor.RED + "Gesloten";
                } else {
                    status = ChatColor.GREEN + "Open";
                    opened = true;
                }
            } else {
                opened = false;
            }
            list.add(new Pair<>(
                    MxSkullItemStackBuilder.create(1)
                            .setSkinFromHeadsData("trapdoor")
                            .setName(ChatColor.GRAY + door.getName())
                            .addBlankLore()
                            .addLore(ChatColor.GRAY + "Status: " + status)
                            .addBlankLore()
                            .addLore(ChatColor.YELLOW + (opened ? "Klik om de deur te sluiten" : "Klik om de deur te openen"))
                            .build(),
                    (mxInv, e12) -> {
                        p.closeInventory();
                        if(!foundDoor) {
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_IS_NOT_A_DOOR));
                            return;
                        }
                        if(opened) { // close
                            door.close(w);
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_CLOSED));
                        } else { // open
                            door.open(w);
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_OPENED));
                        }
                    }
            ));
        });

        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Door Hulp Tool", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                .build());

    }
}
