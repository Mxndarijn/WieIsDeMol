package nl.mxndarijn.wieisdemol.items.presets;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.PresetsManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.shulkers.ShulkerInformation;
import nl.mxndarijn.wieisdemol.managers.shulkers.ShulkerManager;
import nl.mxndarijn.wieisdemol.presets.Preset;
import nl.mxndarijn.wieisdemol.presets.PresetConfig;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShulkerConfigureTool extends MxItem {


    public ShulkerConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
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
        ShulkerManager manager = preset.getShulkerManager();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof ShulkerBox) {
            e.setCancelled(true);
            MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
            if (manager.containsLocation(location)) {
                Optional<ShulkerInformation> information = manager.getShulkerByLocation(location);
                ShulkerInformation inf = information.get();
                MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultInventoryBuilder.create("<gray>Verwijder shulker", MxInventorySlots.THREE_ROWS)
                        .setItem(MxDefaultItemStackBuilder.create(Material.LIME_STAINED_GLASS_PANE)
                                        .setName("<green>Verwijder shulker")
                                        .build(),
                                14,
                                (mxInv, e1) -> {
                                    manager.removeShulker(inf);
                                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_CHEST_REMOVED, ChatPrefix.WIDM));
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxDefaultItemStackBuilder.create(Material.RED_STAINED_GLASS_PANE)
                                        .setName("<red>Behoud shulker")
                                        .build(),
                                12,
                                (mxInv, e1) -> {
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxSkullItemStackBuilder.create(1)
                                        .setSkinFromHeadsData("shulker-light-blue")
                                        .setName("<gray>Toggle beginkist")
                                        .addBlankLore()
                                        .addLore("<gray>Beginkist: " + (inf.isStartingRoom() ? "<green>Ja" : "<red>Nee"))
                                        .addBlankLore()
                                        .addLore("<yellow>Klik hier om de beginkist te togglen")
                                        .build(),
                                13,
                                (mxInv, e12) -> {
                                    inf.setStartingRoom(!inf.isStartingRoom());
                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_TOGGLED_SHULKER, Collections.singletonList(inf.isStartingRoom() ? "<green>Ja" : "<red>Nee")));
                                    p.closeInventory();
                                })
                        .build());
                return;
            }
            // Create new
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_ENTER_NAME, ChatPrefix.WIDM));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(),
                    message -> {
                        if (manager.containsLocation(location))
                            return;
                        AtomicBoolean bool = new AtomicBoolean(true);
                        preset.getShulkerManager().getShulkers().forEach(shulker -> {
                            if (shulker.getMaterial() == e.getClickedBlock().getType()) {
                                bool.set(false);
                            }
                        });
                        ShulkerInformation shulkerInformation = new ShulkerInformation(message, location, e.getClickedBlock().getType(), bool.get());
                        manager.addShulker(shulkerInformation);
                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_CHEST_ADDED, Arrays.asList(message, (bool.get() ? "Ja" : "Nee"))));

                    }
            );
        }

    }
}
