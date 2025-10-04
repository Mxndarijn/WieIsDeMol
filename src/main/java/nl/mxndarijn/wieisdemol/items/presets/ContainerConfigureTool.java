package nl.mxndarijn.wieisdemol.items.presets;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.PresetsManager;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerManager;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerType;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.presets.Preset;
import nl.mxndarijn.wieisdemol.presets.PresetConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Optional;

public class ContainerConfigureTool extends MxItem {


    public ContainerConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if (optionalPreset.isEmpty()) {
            return;
        }
        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();
        ContainerManager manager = preset.getContainerManager();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && ContainerType.isSupported(e.getClickedBlock().getType())) {
            e.setCancelled(true);
            MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
            ContainerType type = ContainerType.fromMaterial(e.getClickedBlock().getType()).orElse(ContainerType.CHEST);
            if (manager.containsLocation(location)) {
                MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultInventoryBuilder.create("<gray>Verwijder container", MxInventorySlots.THREE_ROWS)
                        .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE)
                                        .setName("<green>Verwijder " + type.getDisplay().toLowerCase())
                                        .build(),
                                14,
                                (mxInv, e1) -> {
                                    Optional<ContainerInformation> information = manager.getChestByLocation(location);
                                    information.ifPresent(inf -> {
                                        manager.removeChest(inf);
                                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_CHEST_REMOVED, ChatPrefix.WIDM));
                                    });
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE)
                                        .setName("<red>Behoud " + type.getDisplay().toLowerCase())
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
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_ENTER_NAME, ChatPrefix.WIDM));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(),
                    message -> {
                        if (manager.containsLocation(location))
                            return;

                        ContainerInformation information = new ContainerInformation(message, location, type);
                        manager.addChest(information);
                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_CHEST_ADDED, Collections.singletonList(message)));
                    }
            );
        }

    }
}