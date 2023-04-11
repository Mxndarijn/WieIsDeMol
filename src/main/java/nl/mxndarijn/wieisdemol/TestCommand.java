package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.inventory.*;
import nl.mxndarijn.world.MxAtlas;
import nl.mxndarijn.world.MxWorld;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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

        sender.sendMessage("done");
        return true;
    }
}
