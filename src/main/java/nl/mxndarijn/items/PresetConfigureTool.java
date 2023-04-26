package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.inventory.*;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.heads.MxHeadSection;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.inventory.menu.MxMenuBuilder;
import nl.mxndarijn.inventory.saver.InventoryManager;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.chatinput.MxChatInputCallback;
import nl.mxndarijn.util.chatinput.MxChatInputManager;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.WarpManager;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.presets.PresetsManager;
import nl.mxndarijn.world.warps.Warp;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PresetConfigureTool extends MxItem  {

    public PresetConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
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

        MxInventoryManager.getInstance().addAndOpenInventory(p,MxDefaultMenuBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.ENDER_PEARL)
                                .setName(ChatColor.GRAY + "Verander spawn locatie")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Verander de spawn locatie naar je huidige locatie.")
                                .build(),
                        0,
                        (mxInv1, e12) -> {
                            File settings = new File(preset.getDirectory(), "worldsettings.yml");
                            if(settings.exists()) {
                                FileConfiguration fc = YamlConfiguration.loadConfiguration(settings);
                                Location l = p.getLocation();
                                ConfigurationSection section = fc.createSection("spawn");
                                section.set("x", l.getBlockX());
                                section.set("y", l.getBlockY());
                                section.set("z", l.getBlockZ());
                                try {
                                    fc.save(settings);
                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_SPAWN_CHANGED));
                                } catch (IOException ex) {
                                    Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not save file. (" + settings.getAbsolutePath() + ")");
                                    ex.printStackTrace();
                                }
                                p.closeInventory();
                            } else {
                                Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not find file. (" + settings.getAbsolutePath() + ")");
                            }
                        }
                )
                .setItem(getSkull(config),
                        9,
                        (mainInv, clickMain) -> {
                            MxHeadManager mxHeadManager = MxHeadManager.getInstance();
                            ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                            MxItemClicked clicked = (mxInv, e1) -> {
                                ItemStack is = e1.getCurrentItem();
                                ItemMeta im = is.getItemMeta();
                                PersistentDataContainer container = im.getPersistentDataContainer();
                                String key = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "skull_key"), PersistentDataType.STRING);

                                Optional<MxHeadSection> section = MxHeadManager.getInstance().getHeadSection(key);
                                if(section.isPresent() && section.get().getName().isPresent()) {
                                    clickMain.getWhoClicked().sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_SKULL_CHANGED, Collections.singletonList(section.get().getName().get())));
                                }
                                config.setSkullId(key);
                                config.save();
                                mainInv.getInv().setItem(9, getSkull(config));
                                MxInventoryManager.getInstance().addAndOpenInventory(p, mainInv);

                            };

                            MxHeadManager.getInstance().getAllHeadKeys().forEach(key -> {
                                Optional<MxHeadSection> section = mxHeadManager.getHeadSection(key);
                                section.ifPresent(mxHeadSection -> {
                                    MxSkullItemStackBuilder b = MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData(key)
                                            .setName(ChatColor.GRAY + mxHeadSection.getName().get())
                                            .addBlankLore()
                                            .addLore(ChatColor.YELLOW + "Klik om de skull te selecteren.")
                                            .addCustomTagString("skull_key", mxHeadSection.getKey());
                                    list.add(new Pair<>(b.build(), clicked));
                                });
                            });

                            MxInventoryManager.getInstance().addAndOpenInventory(p,
                                    MxListInventoryBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.SIX_ROWS)
                                            .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                            .addListItems(list)
                                            .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                                    .setName(ChatColor.GRAY + "Info")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik op de skull om dat de nieuwe skull van de preset te maken.")
                                                    .build(), 48, null)
                                            .setPrevious(mainInv)
                                            .build());
                        })
                .setItem(getNameItemStack(config),
                        10,
                        (mainInv, clickMain) -> {
                            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_ENTER_NEW_NAME, ChatPrefix.WIDM));
                            p.closeInventory();
                            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                config.setName(message);
                                config.save();
                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_NAME_CHANGED, Collections.singletonList(message), ChatPrefix.WIDM));
                                mainInv.getInv().setItem(10, getNameItemStack(config));
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    MxInventoryManager.getInstance().addAndOpenInventory(p.getUniqueId(), mainInv);
                                });
                            });
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.COMPASS)
                                .setName(ChatColor.GRAY + "Warps")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de warps van de preset te bekijken en te veranderen.")
                                .build(),
                        12,
                        (mainInv, clickMain) -> {
                            openWarpsMenu(p, mainInv, preset, config);
                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.LIGHT_BLUE_SHULKER_BOX)
                                .setName(ChatColor.GRAY + "Kleuren")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de kleuren van de preset te bekijken en te veranderen.")
                                .build(),
                        14,
                        (mainInv, clickMain) -> {

                        })
                .setItem(getHostDifficulty(preset, config),
                        16,
                        (mainInv, clickMain) -> {
                            openStarsMenu(p, mainInv, preset, config, 1);
                        })
                .setItem(getPlayDifficulty(preset, config),
                        17,
                        (mainInv, clickMain) -> {
                            openStarsMenu(p, mainInv, preset, config, 0);
                        })
                .setItem(getConfiguredItemStack(config),
                        22,
                        (mainInv, clickMain) -> {
                            config.setConfigured(!config.isConfigured());
                            mainInv.getInv().setItem(22, getConfiguredItemStack(config));
                            config.save();
                        })
                .build());
    }

    private ItemStack getConfiguredItemStack(PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.ANVIL)
                .setName(ChatColor.GRAY + "Toggle configured")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + (config.isConfigured() ? ChatColor.GREEN + "Geconfigureerd" : ChatColor.RED + "Niet geconfigureerd"))
                .addLore(ChatColor.YELLOW + "Klik hier om de configuratie te togglen.")
                .addLore(ChatColor.YELLOW + "Geconfigueerd: Spelers kunnen er een map voor maken.")
                .addLore(ChatColor.YELLOW + "Niet geconfigueerd: Spelers ziet de preset niet.")
                .build();
    }

    private ItemStack getHostDifficulty(Preset preset, PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.GOLDEN_APPLE)
                .setName(ChatColor.GRAY + "Verander host-difficulty")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + preset.getStars(config.getHostDifficulty()))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de host-difficulty te veranderen.")
                .build();
    }

    private ItemStack getPlayDifficulty(Preset preset, PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.BREAD)
                .setName(ChatColor.GRAY + "Verander play-difficulty")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + preset.getStars(config.getPlayDifficulty()))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de play-difficulty te veranderen.")
                .build();
    }

    private ItemStack getNameItemStack(PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.NAME_TAG)
                .setName(ChatColor.GRAY + "Verander naam")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + config.getName())
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de naam van de preset te veranderen.")
                .addLore(ChatColor.YELLOW + "Vervolgens moet je in de chat de nieuwe naam sturen.")
                .build();
    }

    private ItemStack getSkull(PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.SKELETON_SKULL)
                .setName(ChatColor.GRAY + "Verander skull")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + (MxHeadManager.getInstance().getHeadSection(config.getSkullId()).isPresent() ? MxHeadManager.getInstance().getHeadSection(config.getSkullId()).get().getName().get() : "Niet-gevonden"))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de skull van de preset te veranderen.")
                .addLore(ChatColor.YELLOW + "Je krijgt een lijst met skulls van het commands /skulls.")
                .build();
    }

    private void openStarsMenu(Player p,MxInventory mainInv, Preset preset, PresetConfig config, int type) {
        String levelTag = "level";
        MxItemClicked clicked = (mxInv, e) -> {
            PersistentDataContainer container = e.getCurrentItem().getItemMeta().getPersistentDataContainer();
            int level = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), levelTag), PersistentDataType.INTEGER);
            if(type == 0) {
                config.setPlayDifficulty(level);
                mainInv.getInv().setItem(17, getPlayDifficulty(preset, config));
            } else {
                config.setHostDifficulty(level);
                mainInv.getInv().setItem(16, getHostDifficulty(preset, config));
            }
            config.save();
            MxInventoryManager.getInstance().addAndOpenInventory(p, mainInv);
        };

        MxInventoryManager.getInstance().addAndOpenInventory(p,MxDefaultMenuBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 1")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 1)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 1.")
                                .build(),
                        11,
                       clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 2")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 2)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 2.")
                                .build(),
                        12,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 3")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 3)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 3.")
                                .build(),
                        13,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 4")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 4)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 4.")
                                .build(),
                        14,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 5")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 5)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 5.")
                                .build(),
                        15,
                        clicked)
                        .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                .setName(ChatColor.GRAY + "Info")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Verander het visuele aspect voor de " + (type == 0 ? "play-difficulty" : "host-difficulty"))
                                .build(),22,null)
                .setPrevious(mainInv)
                .build());
    }

    private void openWarpsMenu(Player p,MxInventory mainInv, Preset preset, PresetConfig config) {
        List<Warp> warps = preset.getWarpManager().getWarps();
        MxItemClicked clicked = (mxInv, e) -> {
            ItemStack is = e.getCurrentItem();
            ItemMeta im = is.getItemMeta();
            PersistentDataContainer container = im.getPersistentDataContainer();
            String warpName = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "warp-name"), PersistentDataType.STRING);

            Optional<Warp> optW = preset.getWarpManager().getWarpByName(warpName);
            if(optW.isPresent()) {
                Warp w = optW.get();
                MxLocation l = w.getMxLocation();
                MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                        .setPrevious(mxInv)
                        .setItem(MxDefaultItemStackBuilder.create(Material.ENDER_PEARL)
                                    .setName(ChatColor.GRAY + "Verander warp locatie")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Verander de warp locatie naar je huidige locatie.")
                                    .build(),
                                12,
                                (mxInv1, e12) -> {
                                    preset.getWarpManager().removeWarp(w);
                                    w.setMxLocation(MxLocation.getFromLocation(p.getLocation()));
                                    preset.getWarpManager().addWarp(w);

                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_NAME_CHANGED, ChatPrefix.WIDM));
                                    p.closeInventory();
                                }
                        )
                        .setItem(MxDefaultItemStackBuilder.create(Material.COMPASS)
                                    .setName(ChatColor.GRAY + "Teleporteer")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Teleporteer naar de warp.")
                                    .build(),
                                14,
                                (mxInv1, e12) -> {
                                    p.teleport(w.getMxLocation().getLocation(p.getWorld()));
                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_TELEPORTED, ChatPrefix.WIDM));
                                    p.closeInventory();
                                }
                        )

                        .setItem(MxSkullItemStackBuilder.create(1)
                                    .setSkinFromHeadsData("red-minus")
                                    .setName(ChatColor.GRAY + "Verwijder")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Verwijder de warp.")
                                    .build(),
                                26,
                                (mxInv1, e12) -> {
                                    preset.getWarpManager().removeWarp(w);
                                    p.closeInventory();
                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_DELETED));
                                }
                        )
                        .setItem(MxSkullItemStackBuilder.create(1)
                                    .setSkinFromHeadsData(w.getSkullId())
                                    .setName(ChatColor.GRAY + "Info")
                                    .addBlankLore()
                                    .addLore(ChatColor.GRAY + "Naam: " + w.getName())
                                    .addBlankLore()
                                    .addLore(ChatColor.GRAY + "X: " + l.getX())
                                    .addLore(ChatColor.GRAY + "Y: " + l.getY())
                                    .addLore(ChatColor.GRAY + "Z: " + l.getZ())
                                    .addLore(ChatColor.GRAY + "Pitch: " + l.getPitch())
                                    .addLore(ChatColor.GRAY + "Yaw: " + l.getYaw())
                                    .build(),
                                22,
                                (mxInv1, e12) -> {
                                    p.teleport(w.getMxLocation().getLocation(p.getWorld()));
                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_TELEPORTED, ChatPrefix.WIDM));
                                    p.closeInventory();
                                }
                        )
                        .build());
            }

        };

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        warps.forEach(warp -> {
            list.add(new Pair<>(
                    MxSkullItemStackBuilder.create(1)
                            .setSkinFromHeadsData(warp.getSkullId())
                            .setName(ChatColor.GRAY + warp.getName())
                            .addBlankLore()
                            .addCustomTagString("warp-name", warp.getName())
                            .addLore(ChatColor.YELLOW + "Klik op de warp om deze aan te passen.")
                            .build(),
                    clicked
            ));
        });

        MxInventoryManager.getInstance().addAndOpenInventory(p,MxListInventoryBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .addListItems(list)
                .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                        .setName(ChatColor.GRAY + "Info")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Hier staan alle warps, je kan er ook zelf toevoegen.")
                        .build(), 22, null)
                .setItem(MxDefaultItemStackBuilder.create(Material.CRAFTING_TABLE)
                        .setName(ChatColor.GRAY + "Nieuwe warp")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om een nieuwe warp te te voegen.")
                        .addLore(ChatColor.YELLOW + "De locatie zal je huidige locatie worden.")
                        .build(), 26, (mxInv, e) -> {
                    Location loc = p.getLocation();
                    MxHeadManager mxHeadManager = MxHeadManager.getInstance();
                    ArrayList<Pair<ItemStack, MxItemClicked>> listSkulls = new ArrayList<>();
                    MxItemClicked clickedOnSkull = (skullInv, e1) -> {
                        ItemStack is = e1.getCurrentItem();
                        ItemMeta im = is.getItemMeta();
                        PersistentDataContainer container = im.getPersistentDataContainer();
                        String warpHeadKey = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "skull_key"), PersistentDataType.STRING);

                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_CHANGE_NAME, ChatPrefix.WIDM));
                        p.closeInventory();
                        WarpManager warpManager = preset.getWarpManager();
                        MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                            if(warps.stream().anyMatch(w -> w.getName().equalsIgnoreCase(message))) {
                                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_NAME_ALREADY_EXISTS, Collections.singletonList(message)));
                                return;
                            }
                            Warp w = new Warp(message, warpHeadKey, MxLocation.getFromLocation(loc));
                            warpManager.addWarp(w);
                            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_CONFIGURE_TOOL_WARPS_WARP_CREATED, Collections.singletonList(message)));
                        });

                    };

                    MxHeadManager.getInstance().getAllHeadKeys().forEach(key -> {
                        Optional<MxHeadSection> section = mxHeadManager.getHeadSection(key);
                        section.ifPresent(mxHeadSection -> {
                            MxSkullItemStackBuilder b = MxSkullItemStackBuilder.create(1)
                                    .setSkinFromHeadsData(key)
                                    .setName(ChatColor.GRAY + mxHeadSection.getName().get())
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Klik om te selecteren.")
                                    .addCustomTagString("skull_key", mxHeadSection.getKey());
                            listSkulls.add(new Pair<>(b.build(), clickedOnSkull));
                        });
                    });

                    MxInventoryManager.getInstance().addAndOpenInventory(p,
                            MxListInventoryBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .addListItems(listSkulls)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName(ChatColor.GRAY + "Info")
                                            .addBlankLore()
                                            .addLore(ChatColor.YELLOW + "Klik op een skull om dat de nieuwe skull van de preset te maken.")
                                            .build(), 48, null)
                                    .setPrevious(mxInv)
                                    .build());
                        })
                .setPrevious(mainInv)
                .build()
        );

    }
}
