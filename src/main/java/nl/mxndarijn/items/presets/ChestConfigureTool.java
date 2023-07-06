package nl.mxndarijn.items.presets;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.chatinput.MxChatInputManager;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.chests.ChestManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.presets.PresetsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

public class ChestConfigureTool extends MxItem {


    public ChestConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if(optionalPreset.isEmpty()) {
            return;
        }
        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();
        ChestManager manager = preset.getChestManager();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST) {
            e.setCancelled(true);
            MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
            if(manager.containsLocation(location)) {
                MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultInventoryBuilder.create(ChatColor.GRAY + "Verwijder kist", MxInventorySlots.THREE_ROWS)
                        .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE)
                                        .setName(ChatColor.GREEN + "Verwijder kist")
                                        .build(),
                                14,
                                (mxInv, e1) -> {
                                    Optional<ChestInformation> information = manager.getChestByLocation(location);
                                    information.ifPresent(inf -> {
                                        manager.removeChest(inf);
                                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_CHEST_REMOVED, ChatPrefix.WIDM));
                                    });
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE)
                                        .setName(ChatColor.RED + "Behoud kist")
                                        .build(),
                                12,
                                (mxInv, e1) -> {
                                    p.closeInventory();
                                }
                        )
                        .build());
                return;
            }
            // Create new
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_ENTER_NAME, ChatPrefix.WIDM));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(),
                message -> {
                    if(manager.containsLocation(location))
                        return;

                    ChestInformation information = new ChestInformation(message, location);
                    manager.addChest(information);
                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_CHEST_ADDED, Collections.singletonList(message)));
                    }
            );
        }

    }
}
