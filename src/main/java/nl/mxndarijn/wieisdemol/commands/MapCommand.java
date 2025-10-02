package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.api.chatinput.MxChatInputCallback;
import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.PresetsManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;
import nl.mxndarijn.wieisdemol.presets.Preset;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    private static final LanguageManager lang = LanguageManager.getInstance();

    public MapCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    public MapCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        MxInventory inv = MxDefaultInventoryBuilder.create("<gray>Mappen", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                .setName("<green>Eigen Mappen")
                                .addLore("<gray>Bekijk je eigen mappen")
                                .addLore("<gray>Je kunt ze ook aanpassen of hosten.")
                                .addLore(" ")
                                .addLore("<yellow>Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        10,
                        (clickedInv, e) -> {
                            // Click on Book
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getOwner().equals(p.getUniqueId())).toList();
                            MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p);

                            ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Eigen Mappen", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(clickedInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName("<gray>Info")
                                            .addLore(" ")
                                            .addLore("<yellow>Klik op een map om deze aan te passen.")
                                            .build(), 49, null)
                                    .build());

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.CRAFTING_TABLE)
                                .setName("<green>Nieuwe Map")
                                .addLore("<gray>Bekijk alle standaard mappen die er zijn.")
                                .addLore("<gray>Vervolgens kan je er een maken.")
                                .addLore(" ")
                                .addLore("<yellow>Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        13,
                        (clickedInv, e) -> {
                            ArrayList<Preset> presets = new ArrayList<>(PresetsManager.getInstance().getAllPresets());
                            presets.removeIf(preset -> !preset.getConfig().isConfigured());
                            presets.sort((p1, p2) -> {
                                boolean p1Permission = p.hasPermission(Permissions.COMMAND_MAPS_CREATE_SPECIFIC_MAP + p1.getConfig().getName().toLowerCase().replaceAll(" ", "_"));
                                boolean p2Permission = p.hasPermission(Permissions.COMMAND_MAPS_CREATE_SPECIFIC_MAP + p2.getConfig().getName().toLowerCase().replaceAll(" ", "_"));
                                if (p1Permission != p2Permission) {
                                    return Boolean.compare(p2Permission, p1Permission);
                                }
                                int difficultyCompare = Integer.compare(p1.getConfig().getHostDifficulty(), p2.getConfig().getHostDifficulty());
                                if (difficultyCompare != 0) {
                                    return difficultyCompare;
                                }
                                return p1.getConfig().getName().compareTo(p2.getConfig().getName());
                            });

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
                                        MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_PRESET));
                                        p.closeInventory();
                                        return;
                                    }
                                    if(!p.hasPermission(Permissions.COMMAND_MAPS_CREATE_SPECIFIC_MAP + optionalPreset.get().getConfig().getName().toLowerCase().replaceAll(" ", "_"))) {
                                        MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_DONT_HAVE_PERMISSION_FOR_MAP));
                                        return;
                                    }
                                    MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                        Optional<Map> map = Map.createFromPreset(message, optionalPreset.get(), p.getUniqueId());
                                        if (map.isPresent()) {
                                            if (map.get().getMxWorld().isEmpty()) {
                                                MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                                return;
                                            }
                                            MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_CREATED));
                                            MxWorld mxWorld = map.get().getMxWorld().get();
                                            map.get().loadWorld().thenAccept(loaded -> {
                                                if (loaded) {
                                                    World w = Bukkit.getWorld(mxWorld.getWorldUID());
                                                    if (w == null) {
                                                        MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                                        return;
                                                    }
                                                    p.teleport(w.getSpawnLocation());
                                                } else {
                                                    MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_LOADED));
                                                }

                                            });
                                        } else {
                                            MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_MAP_COULD_NOT_BE_CREATED));
                                        }
                                    });
                                    MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_ENTER_MAP_NAME_FOR_PRESET, Collections.singletonList(optionalPreset.get().getConfig().getName())));
                                    p.closeInventory();
                                });
                            };
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = presets.stream().map(preset -> new Pair<>(preset.getItemStackForNewMap(p), clickedOnConfiguredPreset)).collect(Collectors.toCollection(ArrayList::new));

                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Nieuwe map", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(clickedInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName("<gray>Info")
                                            .addLore(" ")
                                            .addLore("<yellow>Klik op een preset om deze te vullen en uiteindelijk te hosten.")
                                            .build(), 49, null)
                                    .build());


                            // Create Map
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOKSHELF)
                                .setName("<green>Gedeelde mappen")
                                .addLore("<gray>Bekijk alle mappen die met je gedeeld zijn.")
                                .addLore("<gray>Je kunt ze ook aanpassen of hosten.")
                                .addLore(" ")
                                .addLore("<yellow>Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        16,
                        (clickedInv, e) -> {
                            // Bookshelf
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getSharedPlayers().contains(p.getUniqueId())).toList();

                            if(p.hasPermission(Permissions.COMMAND_MAPS_VIEW_ALL.getPermission())) {
                                playerMaps = MapManager.getInstance().getAllMaps();
                            }
                            openMap(p, playerMaps, clickedInv);

                        })
                .build();
        MxInventoryManager.getInstance().addAndOpenInventory(p, inv);
    }

    private void openMap(Player p, List<Map> maps, MxInventory prev) {
        MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p);
        ArrayList<Pair<ItemStack, MxItemClicked>> list = maps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
        MxListInventoryBuilder sharedInv = MxListInventoryBuilder.create("<gray>Gedeelde Mappen", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setPreviousItemStackSlot(46)
                .setPrevious(prev)
                .setListItems(list)
                .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                        .setName("<gray>Info")
                        .addLore(" ")
                        .addLore("<yellow>Klik op een map om deze aan te passen.")
                        .build(), 49, null);
        sharedInv.setItem(MxDefaultItemStackBuilder.create(Material.PLAYER_HEAD)
                        .setName("<gray>Filter op owner")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om op owner te filteren.")
                        .build(),
                52, (mxInv, e12) -> {
                    p.closeInventory();
                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_MAPS_ENTER_FILTER));
                    MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(message);
                        List<Map> filteredMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getOwner().equals(offlinePlayer.getUniqueId())).toList();
                        openMap(p, filteredMaps, prev);
                    });
                });

        MxInventoryManager.getInstance().addAndOpenInventory(p, sharedInv.build());
    }

    private MxItemClicked getClickedOnPlayerMap(Player p) {
        return (mxInv, e2) -> {
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
                    MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MAP));
                    return;
                }
                Map map = optionalMap.get();
                MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_LOADING_MAP));
                map.loadWorld().thenAccept(loaded -> {
                    if (map.getMxWorld().isEmpty()) {
                        MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MXWORLD));
                        return;
                    }
                    World w = Bukkit.getWorld(map.getMxWorld().get().getWorldUID());
                    if (w == null) {
                        MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_WORLD));
                        return;
                    }

                    p.teleport(w.getSpawnLocation());
                    MSG.msg(p, ChatPrefix.WIDM + lang.getLanguageString(LanguageText.COMMAND_MAPS_TELEPORTED_TO_SPAWN));
                });
            });
        };
    }
}
