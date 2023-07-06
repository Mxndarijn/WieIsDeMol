package nl.mxndarijn.world.map;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.SpecialDirectories;
import nl.mxndarijn.game.InteractionManager;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.items.Items;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.WorldManager;
import nl.mxndarijn.world.changeworld.ChangeWorldManager;
import nl.mxndarijn.world.changeworld.SaveInventoryChangeWorld;
import nl.mxndarijn.world.chests.ChestManager;
import nl.mxndarijn.world.doors.DoorManager;
import nl.mxndarijn.world.mxworld.MxAtlas;
import nl.mxndarijn.world.mxworld.MxWorld;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.shulkers.ShulkerManager;
import nl.mxndarijn.world.warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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


    public Map(File directory) {
        Logger.logMessage(LogLevel.DebugHighlight, "Created map");
        this.directory = directory;
        File mapConfigFile = new File(directory + File.separator + "map.yml");
        this.mapConfig = new MapConfig(mapConfigFile, YamlConfiguration.loadConfiguration(mapConfigFile));

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
        this.mapConfig = new MapConfig(mapConfigFile, YamlConfiguration.loadConfiguration(mapConfigFile), name, owner);

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
            mapConfig.save();
            MxAtlas.getInstance().unloadMxWorld(this.mxWorld.get(), true);
        });
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
}
