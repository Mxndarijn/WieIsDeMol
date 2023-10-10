package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.wieisdemol.items.Items;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;
        for (Items value : Items.values()) {
            if (value.isGameItem()) {
                p.getInventory().addItem(value.getItemStack());
            }
        }
        return true;
    }
}
