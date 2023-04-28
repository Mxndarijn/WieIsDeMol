package nl.mxndarijn.world.presets;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.saver.InventoryManager;
import nl.mxndarijn.items.Items;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
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
import java.util.Optional;
import java.util.UUID;

public class Preset {

    private File directory;
    private File inventoriesFile;
    private PresetConfig config;

    private Optional<MxWorld> mxWorld;
    private WarpManager warpManager;
    private ChestManager chestManager;
    private ShulkerManager shulkerManager;
    private DoorManager doorManager;

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
            this.mxWorld = WorldManager.getInstance().getPresetById(directory.getName());
            this.warpManager = new WarpManager(new File(getDirectory(), "warps.yml"));
            this.chestManager = new ChestManager(new File(getDirectory(), "chests.yml"));
            this.shulkerManager = new ShulkerManager(new File(getDirectory(), "shulkers.yml"));
            this.doorManager = new DoorManager(new File(getDirectory(), "doors.yml"));
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
            if(!preset.getMxWorld().isPresent()) {
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

    public boolean loadWorld() {
        if(!this.mxWorld.isPresent()) {
            return false;
        }
        if(this.mxWorld.get().isLoaded()) {
            return true;
        }
        boolean loaded = MxAtlas.getInstance().loadMxWorld(this.mxWorld.get());
        if(loaded) {
            ChangeWorldManager.getInstance().addWorld(this.mxWorld.get().getWorldUID(),new PresetChangeWorld());
        }
        return loaded;
    }

    private void unloadWorld() {
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

    static class PresetChangeWorld implements MxChangeWorld {

        @Override
        public void enter(Player p, World w, PlayerChangedWorldEvent e) {
            p.getInventory().clear();
            Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(w.getUID());
            if(optionalPreset.isPresent()) {
                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INVENTORY_LOADING));
                Preset preset = optionalPreset.get();
                FileConfiguration fc = YamlConfiguration.loadConfiguration(preset.inventoriesFile);
                InventoryManager.loadInventoryForPlayer(fc, p.getUniqueId().toString(), p);
            }
            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INFO_CONFIGURE_TOOL));
            if(!InventoryManager.containsItem(p.getInventory(), Items.PRESET_CONFIGURE_TOOL.getItemStack())) {
                p.getInventory().addItem(Items.PRESET_CONFIGURE_TOOL.getItemStack());
            }

            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_CONFIGURE_TOOL_INFO));
            if(!InventoryManager.containsItem(p.getInventory(), Items.CHEST_CONFIGURE_TOOL.getItemStack())) {
                p.getInventory().addItem(Items.CHEST_CONFIGURE_TOOL.getItemStack());
            }

            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_CONFIGURE_TOOL_INFO));
            if(!InventoryManager.containsItem(p.getInventory(), Items.SHULKER_CONFIGURE_TOOL.getItemStack())) {
                p.getInventory().addItem(Items.SHULKER_CONFIGURE_TOOL.getItemStack());
            }

            p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_CONFIGURE_TOOL_INFO));
            if(!InventoryManager.containsItem(p.getInventory(), Items.DOOR_CONFIGURE_TOOL.getItemStack())) {
                p.getInventory().addItem(Items.DOOR_CONFIGURE_TOOL.getItemStack());
            }
        }

        @Override
        public void leave(Player p, World w, PlayerChangedWorldEvent e) {
            Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(w.getUID());
            if(optionalPreset.isPresent()) {
                Preset preset = optionalPreset.get();
                UUID uuid = p.getUniqueId();
                FileConfiguration fc = YamlConfiguration.loadConfiguration(preset.inventoriesFile);
                InventoryManager.saveInventory(preset.inventoriesFile, fc, uuid.toString(), p.getInventory());
                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.PRESET_INVENTORY_SAVED));
                p.getInventory().clear();
                if (w.getPlayers().size() == 0) {
                    Logger.logMessage(LogLevel.Information, Prefix.PRESETS_MANAGER, "Unloading preset... (" + preset.getDirectory().getAbsolutePath() + ")");
                    preset.unloadWorld();
                }
            }
        }
    }
}

