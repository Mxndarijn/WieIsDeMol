package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.wieisdemol.presets.Preset;
import nl.mxndarijn.wieisdemol.managers.PresetsManager;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class PresetsCommand extends MxCommand {


    public PresetsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    public PresetsCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        MxInventory inv = MxDefaultInventoryBuilder.create(ChatColor.GRAY + "Presets", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                .setName(ChatColor.GREEN + "Alle presets")
                                .addLore(ChatColor.GRAY + "Bekijk alle presets")
                                .addLore(ChatColor.GRAY + "Daarna kan je ze ook aanpassen.")
                                .addLore(" ")
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        12,
                        (clickedInv, e) -> {
                            // Click on Book
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.ANVIL)
                                .setName(ChatColor.GREEN + "Configureer preset")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik om alle presets te bekijken.")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        14,
                        (clickedInv, e) -> {
                            ArrayList<Preset> presets = PresetsManager.getInstance().getAllPresets();
                            MxItemClicked clickedOnNonConfiguredPreset = (mxInv, e1) -> {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
                                if(e1.getCurrentItem() != null) {
                                    ItemStack is = e1.getCurrentItem();
                                    ItemMeta im = is.getItemMeta();
                                    PersistentDataContainer container = im.getPersistentDataContainer();
                                    Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetById(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), Preset.PRESET_ITEMMETA_TAG), PersistentDataType.STRING));
                                    if(optionalPreset.isPresent()) {
                                        Preset preset = optionalPreset.get();
                                        e.getWhoClicked().closeInventory();
                                        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_PRESETS_LOADING_WORLD, Collections.emptyList()));
                                        preset.loadWorld().thenAccept(loaded -> {
                                            if(loaded) {
                                                MxWorld mxWorld = preset.getMxWorld().get();
                                                World w = Bukkit.getWorld(mxWorld.getWorldUID());
                                                if(w != null) {
                                                    p.teleport(w.getSpawnLocation());
                                                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_PRESETS_NOW_IN_PRESET, Collections.emptyList()));
                                                } else {
                                                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_PRESETS_WORLD_NOT_FOUND_BUT_LOADED, Collections.emptyList()));
                                                }
                                            }
                                        });
                                    } else {
                                        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_PRESETS_WORLD_COULD_NOT_BE_LOADED, Collections.emptyList()));
                                    }
                                }
                            });
                            };
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = presets.stream().map(preset -> new Pair<>(preset.getItemStack(), clickedOnNonConfiguredPreset)).collect(Collectors.toCollection(ArrayList::new));

                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Configureer nieuwe preset", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(clickedInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName(ChatColor.GRAY + "Info")
                                            .addLore(" ")
                                            .addLore(ChatColor.YELLOW + "Klik op een preset om deze te configuren.")
                                            .build(), 49,null)
                                    .build());


                            // Create Map
                        })
                .build();
        MxInventoryManager.getInstance().addAndOpenInventory(p, inv);
    }
}
