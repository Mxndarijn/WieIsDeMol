package nl.mxndarijn.wieisdemol.items.maps;

import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class MapDoorItem extends MxItem {


    public MapDoorItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Map map = mapOptional.get();

        World w = Bukkit.getWorld(map.getMxWorld().get().getWorldUID());
        if (w == null) {
            return;
        }

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        map.getDoorManager().getDoors().forEach(door -> {
            String status = "<red>Lege deur";
            boolean opened;
            boolean foundDoor = door.getLocations().size() > 0;
            if (foundDoor) {
                MxLocation inf = door.getLocations().keySet().iterator().next();
                Location loc = inf.getLocation(w);
                Block placedBlock = loc.getBlock();
                if (placedBlock.getType() != Material.AIR) {
                    opened = false;
                    status = "<red>Gesloten";
                } else {
                    status = "<green>Open";
                    opened = true;
                }
            } else {
                opened = false;
            }
            list.add(new Pair<>(
                    MxSkullItemStackBuilder.create(1)
                            .setSkinFromHeadsData("trapdoor")
                            .setName("<gray>" + door.getName())
                            .addBlankLore()
                            .addLore("<gray>Status: " + status)
                            .addBlankLore()
                            .addLore("<yellow>" + (opened ? "Klik om de deur te sluiten" : "Klik om de deur te openen"))
                            .build(),
                    (mxInv, e12) -> {
                        p.closeInventory();
                        if (!foundDoor) {
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_IS_NOT_A_DOOR));
                            return;
                        }
                        if (opened) { // close
                            door.close(w);
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_CLOSED));
                        } else { // open
                            door.open(w);
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_DOORITEM_DOOR_OPENED));
                        }
                    }
            ));
        });

        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Door Hulp Tool", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                .build());

    }
}
