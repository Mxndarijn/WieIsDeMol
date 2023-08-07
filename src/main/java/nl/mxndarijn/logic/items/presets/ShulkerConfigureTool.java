package nl.mxndarijn.logic.items.presets;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.logic.inventory.MxInventoryManager;
import nl.mxndarijn.logic.inventory.MxInventorySlots;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.logic.items.util.MxItem;
import nl.mxndarijn.managers.chatinput.MxChatInputManager;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.logic.presets.Preset;
import nl.mxndarijn.logic.presets.PresetConfig;
import nl.mxndarijn.managers.PresetsManager;
import nl.mxndarijn.managers.shulkers.ShulkerInformation;
import nl.mxndarijn.managers.shulkers.ShulkerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

public class ShulkerConfigureTool extends MxItem {


    public ShulkerConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if(!optionalPreset.isPresent()) {
            return;
        }

        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();
        ShulkerManager manager = preset.getShulkerManager();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof ShulkerBox) {
            e.setCancelled(true);
            MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
            if(manager.containsLocation(location)) {
                MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultInventoryBuilder.create(ChatColor.GRAY + "Verwijder shulker", MxInventorySlots.THREE_ROWS)
                        .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE)
                                        .setName(ChatColor.GREEN + "Verwijder shulker")
                                        .build(),
                                14,
                                (mxInv, e1) -> {
                                    Optional<ShulkerInformation> information = manager.getShulkerByLocation(location);
                                    information.ifPresent(inf -> {
                                        manager.removeShulker(inf);
                                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_CHEST_REMOVED, ChatPrefix.WIDM));
                                    });
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE)
                                        .setName(ChatColor.RED + "Behoud shulker")
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
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_ENTER_NAME, ChatPrefix.WIDM));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(),
                message -> {
                    if(manager.containsLocation(location))
                        return;

                    ShulkerInformation information = new ShulkerInformation(message, location, e.getClickedBlock().getType());
                    manager.addShulker(information);
                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_CHEST_ADDED, Collections.singletonList(message)));
                    }
            );
        }

    }
}
