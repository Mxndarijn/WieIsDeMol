package nl.mxndarijn.world.map;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.game.InteractionManager;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.items.Items;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.changeworld.ChangeWorldManager;
import nl.mxndarijn.world.changeworld.SaveInventoryChangeWorld;
import nl.mxndarijn.world.chests.ChestManager;
import nl.mxndarijn.world.doors.DoorManager;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.shulkers.ShulkerManager;
import nl.mxndarijn.world.warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Map {

    private File directory;
    private MapConfig mapConfig;
    private File inventoriesFile;

    private Optional<MxWorld> mxWorld;

    private WarpManager warpManager;
    private ChestManager chestManager;
    private ShulkerManager shulkerManager;
    private DoorManager doorManager;
    private InteractionManager interactionManager;

    public static final String  MAP_ITEMMETA_TAG = "map_id";


    public Map(File directory) {
        Logger.logMessage(LogLevel.DebugHighlight, "Created map");
        this.directory = directory;
        File mapConfigFile = new File(directory + File.separator + "map.yml");
        this.mapConfig = new MapConfig(mapConfigFile);

        inventoriesFile = new File(directory + File.separator + "inventories.yml");
        if(containsWorld()) {
            if(!inventoriesFile.exists()) {
                try {
                    inventoriesFile.createNewFile();
                } catch (IOException e) {
                    Logger.logMessage(LogLevel.Error, "Could not create file: " + inventoriesFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
            this.mxWorld = MxAtlas.getInstance().loadWorld(directory);
            this.warpManager = new WarpManager(new File(getDirectory(), "warps.yml"));
            this.chestManager = new ChestManager(new File(getDirectory(), "chests.yml"));
            this.shulkerManager = new ShulkerManager(new File(getDirectory(), "shulkers.yml"));
            this.doorManager = new DoorManager(new File(getDirectory(), "doors.yml"));
            this.interactionManager = new InteractionManager(new File(getDirectory(), "interactions.yml"));
        }
    }

    public static Optional<Map> create(File file) {
        Map map = new Map(file);

        if(map.containsWorld() && map.mxWorld.isPresent()) {
            return Optional.of(map);
        } else {
            if(!map.containsWorld()) {
                Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not find world. (" + map.getDirectory() + ")");
                return Optional.empty();
            }
            if(map.getMxWorld().isEmpty()) {
                Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not find MxWorld. (" + map.getDirectory() + ")");
            }
            return Optional.empty();
        }
    }

    public Map(File directory, String name, UUID owner) {
        Logger.logMessage(LogLevel.DebugHighlight, "Created map1");
        this.directory = directory;
        File mapConfigFile = new File(directory + File.separator + "map.yml");
        if(!mapConfigFile.exists()) {
            try {
                mapConfigFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Failed to create map configuration file: " + e.getMessage());
            }

        }
        this.mapConfig = new MapConfig(mapConfigFile, name, owner);

        inventoriesFile = new File(directory + File.separator + "inventories.yml");
        if(containsWorld()) {
            if(!inventoriesFile.exists()) {
                try {
                    inventoriesFile.createNewFile();
                } catch (IOException e) {
                    Logger.logMessage(LogLevel.Error, "Could not create file: " + inventoriesFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
            this.mxWorld = MxAtlas.getInstance().loadWorld(directory);
            Logger.logMessage(LogLevel.DebugHighlight, "Searching for MxWorld with id: " + directory.getName());
            Logger.logMessage(LogLevel.DebugHighlight, "Found mxworld: " + this.mxWorld.isPresent());
            this.warpManager = new WarpManager(new File(getDirectory(), "warps.yml"));
            this.chestManager = new ChestManager(new File(getDirectory(), "chests.yml"));
            this.shulkerManager = new ShulkerManager(new File(getDirectory(), "shulkers.yml"));
            this.doorManager = new DoorManager(new File(getDirectory(), "doors.yml"));
            this.interactionManager = new InteractionManager(new File(getDirectory(), "interactions.yml"));
        }
    }

    public static Optional<Map> createFromPreset(String name, Preset p, UUID owner) {
        if(p.getMxWorld().isEmpty()) {
            return Optional.empty();
        }
        File newDir = new File(SpecialDirectories.MAP_WORLDS.getDirectory() + File.separator + owner.toString());
        Optional<MxWorld> optionalWorld = MxAtlas.getInstance().duplicateMxWorld(p.getMxWorld().get(), newDir);

        if(optionalWorld.isEmpty()) {
            return Optional.empty();
        }
        Logger.logMessage(LogLevel.DebugHighlight, "Player map adding: " + owner.toString() + " , " + optionalWorld.get().getDir().getAbsolutePath());
        Logger.logMessage(LogLevel.DebugHighlight, "UUID of mxworld: " + optionalWorld.get().getUUID());

        File inventoryFile = new File(optionalWorld.get().getDir() + File.separator + "inventories.yml");
        inventoryFile.delete();

        Map map = new Map(optionalWorld.get().getDir(), name, owner);
        MapManager.getInstance().addMap(map);
        return Optional.of(map);

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
            if(loaded) {
                ChangeWorldManager.getInstance().addWorld(this.mxWorld.get().getWorldUID(),new SaveInventoryChangeWorld(getInventoriesFile(), new ArrayList<>(
                        Arrays.asList(
                                new Pair<>(Items.VUL_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.VUL_TOOL_INFO)),
                                new Pair<>(Items.CHEST_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.CHEST_TOOL_INFO)),
                                new Pair<>(Items.SHULKER_TOOL.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.SHULKER_TOOL_INFO)),
                                new Pair<>(Items.DOOR_ITEM.getItemStack(), ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.DOOR_TOOL_INFO))
                        )),
                        (p, w, e) -> {
                            unloadWorld();
                            mapConfig.setDateModified(LocalDateTime.now());
                            mapConfig.save();
                            mapConfig.getPresetConfig().save();
                        }));

            }
            future.complete(loaded);
        });
        return future;
    }

    public CompletableFuture<Boolean> unloadWorld() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
            if (!Map.this.mxWorld.isPresent()) {
                future.complete(true);
                return;
            }
            if (!Map.this.mxWorld.get().isLoaded()) {
                future.complete(true);
                return;
            }
            mapConfig.save();
            MxAtlas.getInstance().unloadMxWorld(Map.this.mxWorld.get(), true);
            future.complete(true);
        });
        return future;
    }

    private boolean containsWorld() {
        return containsFolder("region");
    }

    private boolean containsFolder(String folderName) {
        File f = new File(directory.getAbsolutePath() + File.separator + folderName);
        return f.exists();
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public void setMapConfig(MapConfig mapConfig) {
        this.mapConfig = mapConfig;
    }

    public File getInventoriesFile() {
        return inventoriesFile;
    }

    public void setInventoriesFile(File inventoriesFile) {
        this.inventoriesFile = inventoriesFile;
    }

    public Optional<MxWorld> getMxWorld() {
        return mxWorld;
    }

    public void setMxWorld(Optional<MxWorld> mxWorld) {
        this.mxWorld = mxWorld;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public void setWarpManager(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public void setChestManager(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    public ShulkerManager getShulkerManager() {
        return shulkerManager;
    }

    public void setShulkerManager(ShulkerManager shulkerManager) {
        this.shulkerManager = shulkerManager;
    }

    public DoorManager getDoorManager() {
        return doorManager;
    }

    public void setDoorManager(DoorManager doorManager) {
        this.doorManager = doorManager;
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public void setInteractionManager(InteractionManager interactionManager) {
        this.interactionManager = interactionManager;
    }

    public ItemStack getItemStack() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1);

        if(MxHeadManager.getInstance().getAllHeadKeys().contains(mapConfig.getPresetConfig().getSkullId())) {
            builder.setSkinFromHeadsData(mapConfig.getPresetConfig().getSkullId());
        } else {
            builder.setSkinFromHeadsData("question-mark");
        }

        builder.setName(ChatColor.GRAY + mapConfig.getName());

        builder.addBlankLore();
        builder.addLore(ChatColor.GRAY + "Aantal spelers: " + mapConfig.getColors().size());
        builder.addBlankLore();

        builder.addLore(ChatColor.GRAY + "Host-Moeilijkheid:")
                .addLore(getStars(mapConfig.getPresetConfig().getHostDifficulty()))
                .addLore(ChatColor.GRAY + "Speel-Moeilijkheid:")
                .addLore(getStars(mapConfig.getPresetConfig().getPlayDifficulty()));

        builder.addBlankLore();
        builder.addLore(ChatColor.GRAY + "Eigenaar: " + Bukkit.getOfflinePlayer(mapConfig.getOwner()).getName());
        if(mapConfig.getSharedPlayers().size() > 0) {
            builder.addLore(ChatColor.GRAY + "Gedeeld met:");
        }
        mapConfig.getSharedPlayers().forEach(u -> {
            builder.addLore(ChatColor.GRAY + " - " + Bukkit.getOfflinePlayer(u).getName());
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        builder.addBlankLore()
                        .addLore(ChatColor.GRAY + "Laatst aangepast: " + mapConfig.getDateModified().format(formatter))
                        .addLore(ChatColor.GRAY + "Aangemaakt op: " + mapConfig.getDateCreated().format(formatter));

        builder.addCustomTagString(MAP_ITEMMETA_TAG, directory.getName());

        return builder.build();

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

    public void delete() {
        unloadWorld().thenAccept(unloaded -> {
            MapManager.getInstance().removeMap(this);
        });

    }
}
