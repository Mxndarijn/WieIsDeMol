package nl.mxndarijn.wieisdemol;

import nl.mxndarijn.inventory.*;
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
        MxInventory inv = MxInventoryBuilder.create("Test", MxInventorySlots.FIVE_ROWS)
                .setItem(MxItemStackBuilder.create(Material.ANVIL, 3)
                                .setUnbreakable(true)
                                .setName("Test")
                                .addLore("a")
                                .addLore("b")
                                .addEnchantment(Enchantment.ARROW_DAMAGE, 3, true)
                                .addLore("c")
                                .build(),
                        5,
                        (inv1, e) -> {
                            Bukkit.broadcastMessage("Clicked!");
                            System.out.println("Clicked!");

                        }
                )
//                .canBeClosed(false)
                .deleteInventoryWhenClosed(true)
                .defaultCancelEvent(true)
                .build();
        MxInventoryManager.getInstance().addAndOpenInventory((Player) sender, inv);
        sender.sendMessage("done");
        return true;
    }
}
