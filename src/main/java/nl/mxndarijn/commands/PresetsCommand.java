package nl.mxndarijn.commands;

import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.inventory.*;
import nl.mxndarijn.inventory.heads.MxHeadsType;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.menu.MxDefaultInventoryBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class PresetsCommand extends MxCommand {


    public PresetsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    public PresetsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);
    }

    @Override
    void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        MxInventory inv = MxDefaultInventoryBuilder.create(ChatColor.GRAY + "Presets", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                .setName(ChatColor.GREEN + "Alle presets")
                                .addLore(ChatColor.GRAY + "Bekijk alle presets")
                                .addLore(ChatColor.GRAY + "Daarna kan je ze ook aanpassen.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        12,
                        (clickedInv, e) -> {
                            // Click on Book
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.ANVIL)
                                .setName(ChatColor.GREEN + "Configureer nieuwe preset")
                                .addLore(ChatColor.GRAY + "Bekijk alle presets die nog niet")
                                .addLore(ChatColor.GRAY + "zijn geconfigueerd.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        14,
                        (clickedInv, e) -> {
                            // Create Map
                        })
                .build();
        MxInventoryManager.getInstance().addAndOpenInventory(p, inv);
        //MxHeadManager.getInstance().storeSkullTexture(p.getInventory().getItemInMainHand(), "test", MxHeadsType.MANUALLY_ADDED);
    }
}
