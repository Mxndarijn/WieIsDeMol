package nl.mxndarijn.world.presets;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.game.InteractionManager;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.inventory.saver.InventoryManager;
import nl.mxndarijn.items.Items;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.changeworld.SaveInventoryChangeWorld;
import nl.mxndarijn.world.changeworld.WorldReachedZeroPlayersEvent;
import nl.mxndarijn.world.chests.ChestManager;
import nl.mxndarijn.world.doors.DoorInformation;
import nl.mxndarijn.world.doors.DoorManager;
import nl.mxndarijn.world.shulkers.ShulkerManager;
import nl.mxndarijn.world.warps.WarpManager;
import nl.mxndarijn.world.changeworld.ChangeWorldManager;
import nl.mxndarijn.world.changeworld.MxChangeWorld;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Preset {

    private File directory;
    private File inventoriesFile;
    private PresetConfig config;

    private Optional<MxWorld> mxWorld;
    private WarpManager warpManager;
    private ChestManager chestManager;
    private ShulkerManager shulkerManager;
    private DoorManager doorManager;
    private InteractionManager interactionManager;

    public static final String PRESET_ITEMMETA_TAG = "preset_id";
    private Preset(File directory) {
        this.directory = directory;
        this.mxWorld = Optional.empty();
        File presetConfigFile = new File(directory + File.separator + "preset.yml");
        inventoriesFile = new File(directory + File.separator + "inventories.yml");
        if(containsWorld()) {
            if(!presetConfigFile.exists()) {
                Functions.copyFileFromResources("preset.yml", presetConfigFile);
            }
            if(!inventoriesFile.exists()) {
                try {
                    inventoriesFile.createNewFile();
                } catch (IOException e) {
                    Logger.logMessage(LogLevel.Error, "Could not create file: " + inventoriesFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
            this.config = new PresetConfig(presetConfigFile, YamlConfiguration.loadConfiguration(presetConfigFile));
            this.mxWorld = MxAtlas.getInstance().loadWorld(directory);
            this.warpManager = new WarpManager(new File(getDirectory(), "warps.yml"));
            this.chestManager = new ChestManager(new File(getDirectory(), "chests.yml"));
            this.shulkerManager = new ShulkerManager(new File(getDirectory(), "shulkers.yml"));
            this.doorManager = new DoorManager(new File(getDirectory(), "doors.yml"));
            this.interactionManager = new InteractionManager(new File(getDirectory(), "interactions.yml"));
        }
    }
    public static Optional<Preset> create(File file) {
        Preset preset = new Preset(file);

        if(preset.containsWorld() && preset.mxWorld.isPresent()) {
            return Optional.of(preset);
        } else {
            if(!preset.containsWorld()) {
                Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not find world. (" + preset.getDirectory() + ")");
            }
            if(preset.getMxWorld().isEmpty()) {
                Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not find MxWorld. (" + preset.getDirectory() + ")");
            }
            return Optional.empty();
        }
    }

    public File getDirectory() {
        return directory;
    }

    public PresetConfig getConfig() {
        return config;
    }

    public ItemStack getItemStack() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1);
        if(MxHeadManager.getInstance().getAllHeadKeys().contains(config.getSkullId())) {
            builder.setSkinFromHeadsData(config.getSkullId());
        } else {
            builder.setSkinFromHeadsData("question-mark");
        }

        builder.setName(ChatColor.GRAY + config.getName())
                .addLore(" ");

        if(!config.isConfigured()) {
            builder.addLore(ChatColor.GRAY + "Wereld-Naam: " + directory.getName());
            builder.addLore(" ");
        }
        builder.addLore(ChatColor.GRAY + "Host-Moeilijkheid:")
                .addLore(getStars(config.getHostDifficulty()))
                .addLore(ChatColor.GRAY + "Speel-Moeilijkheid:")
                .addLore(getStars(config.getPlayDifficulty()));
        if(config.isLocked()) {
            builder.addLore(ChatColor.GRAY + "Locked: " + (config.isLocked() ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"))
                    .addLore(ChatColor.GRAY + "Door: " + config.getLockedBy())
                    .addLore(ChatColor.GRAY + "Reden: ")
                    .addLore(ChatColor.RED + config.getLockReason());
        }
        builder.addLore(" ")
                .addLore(ChatColor.GRAY + "Geconfigureerd: " + (config.isConfigured() ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"));

        builder.addCustomTagString(PRESET_ITEMMETA_TAG, directory.getName());


        return builder.build();
    }

    public ItemStack getItemStackForNewMap() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1);
        if(MxHeadManager.getInstance().getAllHeadKeys().contains(config.getSkullId())) {
            builder.setSkinFromHeadsData(config.getSkullId());
        } else {
            builder.setSkinFromHeadsData("question-mark");
        }

        builder.addLore(" ")
                .addLore(ChatColor.GRAY + "Aantal Spelers: " + config.getColors().size());
        builder.setName(ChatColor.GRAY + config.getName())
                .addLore(" ");
        builder.addLore(ChatColor.GRAY + "Host-Moeilijkheid:")
                .addLore(getStars(config.getHostDifficulty()))
                .addLore(ChatColor.GRAY + "Speel-Moeilijkheid:")
                .addLore(getStars(config.getPlayDifficulty()));


        builder.addLore(" ")
                .addLore(ChatColor.DARK_GRAY + "Extra Info:")
                .addLore(ChatColor.GRAY + "Aantal Kisten: " + chestManager.getChests().size())
                .addLore(ChatColor.GRAY + "Aantal deuren: " + doorManager.getDoors().size());

        if(config.isLocked()) {
            builder.addLore(ChatColor.GRAY + "Locked: " + (config.isLocked() ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"))
                    .addLore(ChatColor.GRAY + "Door: " + config.getLockedBy())
                    .addLore(ChatColor.GRAY + "Reden: ")
                    .addLore(ChatColor.RED + config.getLockReason());
        }

        builder.addCustomTagString(PRESET_ITEMMETA_TAG, directory.getName());


        return builder.build();
    }



    private boolean containsWorld() {
        return containsFolder("region");
    }

    private boolean containsFolder(String folderName) {
        File f = new File(directory.getAbsolutePath() + File.separator + folderName);
        return f.exists();
    }

    public String getStars(int stars) {
        StringBuilder hostStars = new StringBuilder();
        for(int i = 1; i <= 5; i++) {
            if(i <= stars) {
                hostStars.append(ChatColor.YELLOW + "\u272B");
            } else {
                hostStars.append(ChatColor.GRAY + "\u272B");
            }
        }
        return hostStars.toString();
    }

    public CompletableFuture<Boolean> loadWorld() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(this.mxWorld.isEmpty()) {
            future.complete(false);
            return future;
        }
        if(this.mxWorld.get().isLoaded()) {
            future.complete(false);
            return future;
        }
        MxAtlas.getInstance().loadMxWorld(this.mxWorld.get()).thenAccept(loaded -> {
            if (loaded) {
                ChangeWorldManager.getInstance().addWorld(this.mxWorld.get().getWorldUID(), new SaveInventoryChangeWorld(getInventoriesFile(), new ArrayList<>(
                        Arrays.asList(
                                new Pair<>(Items.PRESET_CONFIGURE_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INFO_CONFIGURE_TOOL)),
                                new Pair<>(Items.CHEST_CONFIGURE_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_INFO)),
                                new Pair<>(Items.SHULKER_CONFIGURE_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_INFO)),
                                new Pair<>(Items.DOOR_CONFIGURE_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_INFO))
                        )),
                        (p, w, e) -> {
                            unloadWorld();
                        }));
            }
            future.complete(loaded);
        });
        return future;
    }

    public void unloadWorld() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
            if(!this.mxWorld.isPresent()) {
                return;
            }
            if(!this.mxWorld.get().isLoaded()) {
                return;
            }
            config.save();
            MxAtlas.getInstance().unloadMxWorld(this.mxWorld.get(), true);
        });
    }
    public Optional<MxWorld> getMxWorld() {
        return mxWorld;
    }


    public File getInventoriesFile() {
        return inventoriesFile;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public ShulkerManager getShulkerManager() {
        return shulkerManager;
    }

    public DoorManager getDoorManager() {
        return doorManager;
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }
}

