package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.*;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.chatinput.MxChatInputCallback;
import nl.mxndarijn.util.chatinput.MxChatInputManager;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.world.doors.DoorInformation;
import nl.mxndarijn.world.doors.DoorManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.presets.PresetsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class DoorConfigureTool extends MxItem {

    private HashMap<UUID, DoorInformation> players;
    public DoorConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);

        players = new HashMap<>();
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if(!optionalPreset.isPresent()) {
            return;
        }
        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();
        DoorManager manager = preset.getDoorManager();
        if(e.getAction() == Action.RIGHT_CLICK_AIR) {
            // Open menu
            ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
            manager.getDoors().forEach(door -> {
                list.add(new Pair<>(
                        MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData("trapdoor")
                                .setName(ChatColor.GRAY + door.getName())
                                .addBlankLore()
                                .addLore(ChatColor.GRAY + "Aantal blocks: " + door.getLocations().size())
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik om de opties te bekijken voor deze deur.")
                                .build(),
                        (mxInv, e12) -> {
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create(ChatColor.GRAY + "Door Configure-Tool", MxInventorySlots.THREE_ROWS)
                                    .setPrevious(mxInv)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-minus")
                                                    .setName(ChatColor.RED + "Verwijder deur")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de deur te verwijderen.")
                                                    .build(),
                                            11,
                                            (mxInv1, e13) -> {
                                                manager.getDoors().remove(door);
                                                manager.save();
                                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_REMOVED, Collections.singletonList(door.getName()), ChatPrefix.WIDM));
                                                players.remove(p.getUniqueId());
                                                p.closeInventory();
                                            }
                                    )
                                    .setItem(MxDefaultItemStackBuilder.create(Material.CHEST)
                                                    .setName(ChatColor.GRAY + "Selecteer")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de deur te selecteren.")
                                                    .build(),
                                            15,
                                            (mxInv1, e13) -> {
                                                players.put(p.getUniqueId(), door);
                                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_SELECTED, Collections.singletonList(door.getName()), ChatPrefix.WIDM));
                                                p.closeInventory();
                                            }
                                    )
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData("trapdoor")
                                            .setName(ChatColor.GRAY + door.getName())
                                            .build(),
                                            13, null)
                                    .build());

                        }
                ));
            });

            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Door Configure-Tool", MxInventorySlots.THREE_ROWS)
                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                    .setListItems(list)
                    .setNextPageItemStackSlot(25)
                    .setItem(MxDefaultItemStackBuilder.create(Material.OAK_DOOR)
                                    .setName(ChatColor.GRAY + "Nieuwe deur")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Klik hier om een nieuwe door te maken.")
                                    .build(),
                            26,
                            (mxInv, e1) -> {
                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_CREATE_INPUT_NAME, ChatPrefix.WIDM));
                                p.closeInventory();
                                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                    DoorInformation inf = new DoorInformation(message);
                                    manager.getDoors().add(inf);
                                    players.put(p.getUniqueId(), inf);
                                    manager.save();
                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_DOOR_CREATED, Collections.singletonList(message), ChatPrefix.WIDM));
                                });
                            }).build());
        } else {
            if(players.containsKey(p.getUniqueId())) {
                DoorInformation information = players.get(p.getUniqueId());
                if(manager.getDoors().contains(information)) {
                    MxLocation location = MxLocation.getFromLocation(e.getClickedBlock().getLocation());
                    Optional<MxLocation> optionalMxLocation = information.getLocation(location);
                    if(!optionalMxLocation.isPresent() && !p.isSneaking()) {
                        information.addLocation(location, e.getClickedBlock().getType());
                        manager.save();
                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_ADDED, ChatPrefix.WIDM));
                    } else {
                        if(p.isSneaking()) {
                            if(optionalMxLocation.isPresent()) {
                                information.removeLocation(optionalMxLocation.get());
                                manager.save();
                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_LOCATION_REMOVED, ChatPrefix.WIDM));
                            } else {
                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_LOCATION_NOT_FOUND, ChatPrefix.WIDM));

                            }
                        } else {
                            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_ALREADY_ADDED, ChatPrefix.WIDM));
                        }
                    }
                }
            } else {
                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_NO_DOOR_SELECTED, ChatPrefix.WIDM));
            }
        }

    }
}
