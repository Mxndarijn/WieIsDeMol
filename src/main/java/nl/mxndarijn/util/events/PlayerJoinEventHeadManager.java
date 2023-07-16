package nl.mxndarijn.util.events;


import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.heads.MxHeadSection;
import nl.mxndarijn.inventory.heads.MxHeadsType;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;

public class PlayerJoinEventHeadManager implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Optional<MxHeadSection> section = MxHeadManager.getInstance().getHeadSection(e.getPlayer().getUniqueId().toString());
        if(section.isEmpty()) {

            ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();

            // Stel de eigenaar van de skull in op de speler
            skullMeta.setOwningPlayer(e.getPlayer());

            // Stel de gewijzigde SkullMeta in op het hoofditem
            headItem.setItemMeta(skullMeta);

            MxHeadManager.getInstance().storeSkullTexture(headItem, e.getPlayer().getUniqueId().toString(),  e.getPlayer().getName(), MxHeadsType.PLAYER);
            Logger.logMessage(LogLevel.DEBUG, Prefix.MXHEAD_MANAGER, "Added skull of " + e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId() + ")");
        }
    }
}
