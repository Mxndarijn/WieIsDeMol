package nl.mxndarijn.wieisdemol.items.game;

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
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerType;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class GameContainerItem extends MxItem {


    public GameContainerItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Game> mapOptional = GameWorldManager.getInstance().getGameByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        Game game = mapOptional.get();

        if (!game.getHosts().contains(p.getUniqueId()))
            return;

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        game.getChestManager().getChests().forEach(container -> {
            ContainerType type = container.getType();
            list.add(new Pair<>(
                    MxDefaultItemStackBuilder.create(type.getIcon(), 1)
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
    public void open(InventoryOpenEvent e) {
        Location loc = e.getInventory().getLocation();
        if (loc == null) {
            return;
        }
        Block b = loc.getBlock();

        Player p = (Player) e.getPlayer();
        Optional<Game> gameOptional = GameWorldManager.getInstance().getGameByWorldUID(p.getWorld().getUID());

        if (gameOptional.isEmpty()) {
            return;
        }

        Game game = gameOptional.get();
        Optional<ContainerInformation> optionalChestInformation = game.getChestManager().getChestByLocation(MxLocation.getFromLocation(b.getLocation()));
        if (optionalChestInformation.isEmpty()) {
            return;
        }

        ContainerInformation containerInformation = optionalChestInformation.get();
        containerInformation.getChestAttachmentList().forEach(a -> {
            a.onOpenChest(e);
        });

    }
}