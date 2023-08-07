package nl.mxndarijn.commands;

import nl.mxndarijn.commands.util.MxCommand;
import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.logic.inventory.*;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.logic.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.managers.chatinput.MxChatInputManager;
import nl.mxndarijn.managers.language.LanguageManager;
import nl.mxndarijn.managers.language.LanguageText;
import nl.mxndarijn.logic.util.logger.LogLevel;
import nl.mxndarijn.logic.util.logger.Logger;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.logic.map.Map;
import nl.mxndarijn.managers.MapManager;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.logic.presets.Preset;
import nl.mxndarijn.managers.PresetsManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapCommand extends MxCommand {

    private static LanguageManager lang = LanguageManager.getInstance();
    public MapCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    public MapCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        MxInventory inv = MxDefaultInventoryBuilder.create(ChatColor.GRAY + "Mappen", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                .setName(ChatColor.GREEN + "Eigen Mappen")
                                .addLore(ChatColor.GRAY + "Bekijk je eigen mappen")
                                .addLore(ChatColor.GRAY + "Je kunt ze ook aanpassen of hosten.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        10,
                        (clickedInv, e) -> {
                            // Click on Book
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getOwner().equals(p.getUniqueId())).toList();
                            MxItemClicked clickedOnPlayerMap = (mxInv, e2) -> {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
                                    if (e2.getCurrentItem() == null) {
                                        return;
                                    }
                                    ItemStack is = e2.getCurrentItem();
                                    ItemMeta im = is.getItemMeta();
                                    PersistentDataContainer container = im.getPersistentDataContainer();
                                    Optional<Map> optionalMap = MapManager.getInstance().getMapById(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), Map.MAP_ITEMMETA_TAG), PersistentDataType.STRING));
                                    p.closeInventory();
                                    if (optionalMap.isEmpty()) {
                                        p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MAP));
                                        return;
                                    }
                                    Map map = optionalMap.get();
                                    p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_LOADING_MAP));
                                    map.loadWorld().thenAccept(loaded -> {
                                        if(map.getMxWorld().isEmpty()) {
                                            p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MXWORLD));
                                            return;
                                        }
                                        World w = Bukkit.getWorld(map.getMxWorld().get().getWorldUID());
                                        if(w == null) {
                                            p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_WORLD));
                                            return;
                                        }

                                        p.teleport(w.getSpawnLocation());
                                        p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_TELEPORTED_TO_SPAWN));
                                    });
                                });
                            };
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Eigen Mappen", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(clickedInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName(ChatColor.GRAY + "Info")
                                            .addLore(" ")
                                            .addLore(ChatColor.YELLOW + "Klik op een map om deze aan te passen.")
                                            .build(), 49,null)
                                    .build());

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.CRAFTING_TABLE)
                                .setName(ChatColor.GREEN + "Nieuwe Map")
                                .addLore(ChatColor.GRAY + "Bekijk alle standaard mappen die er zijn.")
                                .addLore(ChatColor.GRAY + "Vervolgens kan je er een maken.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        13,
                        (clickedInv, e) -> {
                            ArrayList<Preset> presets = new ArrayList<>(PresetsManager.getInstance().getAllPresets());
                            presets.removeIf(preset -> !preset.getConfig().isConfigured());
                            MxItemClicked clickedOnConfiguredPreset = (mxInv, e1) -> {
                                //Create map
                                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
                                    if (e1.getCurrentItem() == null) {
                                        return;
                                    }
                                    ItemStack is = e1.getCurrentItem();
                                    ItemMeta im = is.getItemMeta();
                                    PersistentDataContainer container = im.getPersistentDataContainer();
                                    Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetById(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), Preset.PRESET_ITEMMETA_TAG), PersistentDataType.STRING));
                                    if (optionalPreset.isEmpty()) {
                                        p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_PRESET));
                                        p.closeInventory();
                                        return;
                                    }
                                    Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, "Making...");
                                    MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                        Optional<Map> map = Map.createFromPreset(message, optionalPreset.get(), p.getUniqueId());
                                        if(map.isPresent()) {
                                            if(map.get().getMxWorld().isEmpty()) {
                                                p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                                return;
                                            }
                                            p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_CREATED));
                                            MxWorld mxWorld = map.get().getMxWorld().get();
                                            map.get().loadWorld().thenAccept(loaded -> {
                                                if(loaded) {
                                                    World w = Bukkit.getWorld(mxWorld.getWorldUID());
                                                    if (w == null) {
                                                        p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                                        return;
                                                    }
                                                    p.teleport(w.getSpawnLocation());
                                                } else {
                                                    p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_LOADED));
                                                    return;
                                                }

                                            });
                                        } else {
                                            p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                        }
                                    });
                                    p.sendMessage(ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_ENTER_MAP_NAME_FOR_PRESET, Collections.singletonList(optionalPreset.get().getConfig().getName())));
                                    p.closeInventory();
                                });
                            };
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = presets.stream().map(preset -> new Pair<>(preset.getItemStackForNewMap(), clickedOnConfiguredPreset)).collect(Collectors.toCollection(ArrayList::new));

                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Nieuwe map", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(clickedInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName(ChatColor.GRAY + "Info")
                                            .addLore(" ")
                                            .addLore(ChatColor.YELLOW + "Klik op een preset om deze te vullen en uiteindelijk te hosten.")
                                            .build(), 49,null)
                                    .build());


                            // Create Map
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOKSHELF)
                                .setName(ChatColor.GREEN + "Gedeelde mappen")
                                .addLore(ChatColor.GRAY + "Bekijk alle mappen die met je gedeeld zijn.")
                                .addLore(ChatColor.GRAY + "Je kunt ze ook aanpassen of hosten.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        16,
                        (clickedInv, e) -> {
                        // Bookshelf
                        })
                .build();
        MxInventoryManager.getInstance().addAndOpenInventory(p, inv);
    }
}
