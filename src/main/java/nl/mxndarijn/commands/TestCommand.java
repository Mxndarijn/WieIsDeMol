package nl.mxndarijn.commands;

import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        MxInventory inv = MxInventoryBuilder.create("Test", MxInventorySlots.FIVE_ROWS)
//                .setItem(MxItemStackBuilder.create(Material.ANVIL, 3)
//                                .setUnbreakable(true)
//                                .setName("Test")
//                                .addLore("a")
//                                .addLore("b")
//                                .addEnchantment(Enchantment.ARROW_DAMAGE, 3, true)
//                                .addLore("c")
//                                .build(),
//                        5,
//                        (inv1, e) -> {
//                            Bukkit.broadcastMessage("Clicked!");
//                        }
//                )
////                .canBeClosed(false)
//                .deleteInventoryWhenClosed(true)
//                .defaultCancelEvent(true)
//                .build();
//        MxInventoryManager.getInstance().addAndOpenInventory((Player) sender, inv);


        Player p = (Player) sender;
        MxWorld world = WorldManager.getInstance().getPlayersMap().get(p.getUniqueId()).get(0);
        if(MxAtlas.getInstance().loadMxWorld(world)) {
            p.teleport(Bukkit.getWorld(world.getWorldUID()).getSpawnLocation());
        }

//        ArrayList<Pair<ItemStack, MxItemClicked>> items = new ArrayList<>();
//        MxItemClicked clicked = (inv, e) -> {
//            try {
//                Logger.logMessage(LogLevel.Debug, Prefix.MXINVENTORY, "Item Clicked: " + PlainComponentSerializer.plain().serialize(Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().displayName())));
//            } catch (Exception ex) {
//                Logger.logMessage(LogLevel.Debug, Prefix.MXINVENTORY, "Item Clicked: AIR (NULL)");
//            }
//        };
//        for(int i = 0; i < 1500; i++) {
//            items.add(
//                    new Pair<>(
//                        MxDefaultItemStackBuilder.create(Material.BOOKSHELF, 1)
//                        .setName(ChatColor.GRAY + "Item-" + i)
//                        .build(),
//                        clicked
//                )
//            );
//        }
//        MxInventoryManager.getInstance().addAndOpenInventory(
//                p,
//                MxListInventoryBuilder.create(ChatColor.RED + "Test Inventory", MxInventorySlots.SIX_ROWS)
//                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
//                    .addListItems(items)
//                    .defaultCancelEvent(false)
//                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
//                            .setName(ChatColor.GRAY + "Test-Item")
//                            .build(), 52, null)
//                    .build()
//        );



        sender.sendMessage("done");
        return true;
    }
}
