package nl.mxndarijn.items.util;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.saver.InventoryManager;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;

public abstract class MxItem implements Listener {

    private final ItemStack is;
    private final MxWorldFilter worldFilter;
    private final LanguageManager languageManager;

    private final boolean gameItem;
    private final Action[] actions;
    public final JavaPlugin plugin;


    public MxItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        this.is = is;
        this.worldFilter = worldFilter;
        this.gameItem = gameItem;
        this.languageManager = LanguageManager.getInstance();
        this.actions = actions;

        plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if(!Arrays.stream(actions).anyMatch(a -> a == e.getAction())) {
            return;
        }

        if(e.getHand() != EquipmentSlot.HAND) {
            return;
        }


        if(e.getItem() == null || !e.getItem().hasItemMeta() || e.getItem().getType() == Material.AIR) {
            return;
        }

        Player p = e.getPlayer();
        if(gameItem) {

            // TODO check ingame
        }

        if(!InventoryManager.validateItem(e.getItem(), is)) {
            return;
        }


        try {
            execute(p, e);
        } catch (Exception ex) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MXITEM, "Could not execute item: " + Functions.convertComponentToString(is.getItemMeta().displayName()));
            ex.printStackTrace();
            p.sendMessage(languageManager.getLanguageString(LanguageText.ERROR_WHILE_EXECUTING_ITEM, Collections.emptyList(),  ChatPrefix.WIDM));
        }

    }

    public abstract void execute(Player p, PlayerInteractEvent e);

//    @EventHandler
//    public void interactEntity(PlayerInteractEntityEvent e) {
//        if(e.get)
//
//    }

//    @EventHandler
//    public void interactAtEntity(PlayerInteractAtEntityEvent e) {
//
//    }





}
