package nl.mxndarijn.wieisdemol.items.presets;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.PresetsManager;
import nl.mxndarijn.wieisdemol.managers.doors.DoorInformation;
import nl.mxndarijn.wieisdemol.managers.doors.DoorManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.presets.Preset;
import nl.mxndarijn.wieisdemol.presets.PresetConfig;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DoorConfigureTool extends MxItem {

    private final HashMap<UUID, DoorInformation> players;

    public DoorConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);

        players = new HashMap<>();
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if (optionalPreset.isEmpty()) {
            return;
        }
        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();
        DoorManager manager = preset.getDoorManager();
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            // Open menu
            ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
            manager.getDoors().forEach(door -> {
                list.add(new Pair<>(
                        MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData("trapdoor")
                                .setName("<gray>" + door.getName())
                                .addBlankLore()
                                .addLore("<gray>Aantal blocks: " + door.getLocations().size())
                                .addBlankLore()
                                .addLore("<yellow>Klik om de opties te bekijken voor deze deur.")
                                .build(),
                        (mxInv, e12) -> {
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Door Configure-Tool", MxInventorySlots.THREE_ROWS)
                                    .setPrevious(mxInv)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-minus")
                                                    .setName("<red>Verwijder deur")
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om de deur te verwijderen.")
                                                    .build(),
                                            11,
                                            (mxInv1, e13) -> {
                                                manager.getDoors().remove(door);
                                                manager.save();
                                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_REMOVED, Collections.singletonList(door.getName()), ChatPrefix.WIDM));
                                                players.remove(p.getUniqueId());
                                                p.closeInventory();
                                            }
                                    )
                                    .setItem(MxDefaultItemStackBuilder.create(Material.CHEST)
                                                    .setName("<gray>Selecteer")
                                                    .addBlankLore()
                                                    .addLore("<yellow>Klik hier om de deur te selecteren.")
                                                    .build(),
                                            15,
                                            (mxInv1, e13) -> {
                                                players.put(p.getUniqueId(), door);
                                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_SELECTED, Collections.singletonList(door.getName()), ChatPrefix.WIDM));
                                                p.closeInventory();
                                            }
                                    )
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("trapdoor")
                                                    .setName("<gray>" + door.getName())
                                                    .build(),
                                            13, null)
                                    .build());

                        }
                ));
            });

            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Door Configure-Tool", MxInventorySlots.THREE_ROWS)
                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                    .setListItems(list)
                    .setNextPageItemStackSlot(25)
                    .setItem(MxDefaultItemStackBuilder.create(Material.OAK_DOOR)
                                    .setName("<gray>Nieuwe deur")
                                    .addBlankLore()
                                    .addLore("<yellow>Klik hier om een nieuwe door te maken.")
                                    .build(),
                            26,
                            (mxInv, e1) -> {
                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_CREATE_INPUT_NAME, ChatPrefix.WIDM));
                                p.closeInventory();
                                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                    DoorInformation inf = new DoorInformation(message);
                                    manager.getDoors().add(inf);
                                    players.put(p.getUniqueId(), inf);
                                    manager.save();
                                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_CREATED, Collections.singletonList(message), ChatPrefix.WIDM));
                                });
                            }).build());
        } else {
            if (players.containsKey(p.getUniqueId())) {
                DoorInformation information = players.get(p.getUniqueId());
                if (manager.getDoors().contains(information)) {
                    MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
                    Optional<MxLocation> optionalMxLocation = information.getLocation(location);
                    if (optionalMxLocation.isEmpty() && !p.isSneaking()) {
                        information.addLocation(location, e.getClickedBlock().getType());
                        manager.save();
                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_ADDED, ChatPrefix.WIDM));
                    } else {
                        if (p.isSneaking()) {
                            if (optionalMxLocation.isPresent()) {
                                information.removeLocation(optionalMxLocation.get());
                                manager.save();
                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_LOCATION_REMOVED, ChatPrefix.WIDM));
                            } else {
                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_LOCATION_NOT_FOUND, ChatPrefix.WIDM));

                            }
                        } else {
                            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_ALREADY_ADDED, ChatPrefix.WIDM));
                        }
                    }
                }
            } else {
                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_NO_DOOR_SELECTED, ChatPrefix.WIDM));
            }
        }

    }
}
