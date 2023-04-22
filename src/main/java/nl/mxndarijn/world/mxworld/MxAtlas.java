package nl.mxndarijn.world.mxworld;

import nl.mxndarijn.util.VoidGenerator;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.wieisdemol.Functions;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MxAtlas {
    private static MxAtlas instance;
    public static MxAtlas getInstance() {
        if(instance == null) {
            instance = new MxAtlas();
        }
        return instance;
    }

    private ArrayList<MxWorld> worlds;

    private MxAtlas() {
        Logger.logMessage(LogLevel.Information, Prefix.MXATLAS, "Started MxAtlas... (World-Manager)");
        worlds = new ArrayList<>();
    }

    public Optional<MxWorld> getMxWorld(String name) {
        for (MxWorld w : worlds) {
            if (w.getName().equals(name)) {
                return Optional.of(w);
            }
        }
        return Optional.empty();
    }

    public Optional<MxWorld> getMxWorld(UUID uuid) {
        for (MxWorld w : worlds) {
            if (w.getUUID().equals(uuid)) {
                return Optional.of(w);
            }
        }
        return Optional.empty();
    }

    public boolean addMxWorld(MxWorld world) {
        return worlds.add(world);
    }

    public boolean removeMxWorld(MxWorld world) {
        return worlds.remove(world);
    }

    public boolean loadMxWorld(MxWorld mxWorld) {
        Logger.logMessage(LogLevel.Debug, Prefix.MXATLAS, "Loading MxWorld: " + mxWorld.getName());
        if(mxWorld.isLoaded()) {
            Logger.logMessage(LogLevel.Warning, Prefix.MXATLAS,  mxWorld.getName() + " is already loaded.");
            return true;
        }
        String path = mxWorld.getDir().toString().replace("\\", "/");
        WorldCreator wc = new WorldCreator(path);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.FLAT);
        wc.generator(new VoidGenerator());
        wc.generateStructures(false);

        World world = wc.createWorld();
        if(world == null) {
            return false;
        }
        File worldSettings = new File(mxWorld.getDir() + "/worldsettings.yml");
        if(!worldSettings.exists()) {
            Functions.copyFileFromResources("worldsettings.yml", worldSettings);
        }
        Logger.logMessage(LogLevel.Debug, Prefix.MXATLAS, "Loading worldsettings.yml... ");
        FileConfiguration worldSettingsCfg = YamlConfiguration.loadConfiguration(worldSettings);
        world.setAutoSave(worldSettingsCfg.getBoolean("autosave"));
        world.setKeepSpawnInMemory(worldSettingsCfg.getBoolean("keepSpawnInMemory"));

        worldSettingsCfg.getConfigurationSection("gamerules").getKeys(false).forEach(val -> {
           world.setGameRuleValue(val, worldSettingsCfg.getConfigurationSection("gamerules").get(val).toString());
        });
        ConfigurationSection spawn = worldSettingsCfg.getConfigurationSection("spawn");
        if(spawn != null) {
            Logger.logMessage(LogLevel.Debug, Prefix.MXATLAS, "Setting spawnlocation... ");
            world.setSpawnLocation(Functions.getLocationFromConfiguration(world, spawn));
        }

        mxWorld.setWorldUID(world.getUID());
        mxWorld.setLoaded(true);

        return true;
    }

    public boolean unloadMxWorld(MxWorld mxWorld, boolean save) {
        if(!mxWorld.isLoaded())
            return true;
        Logger.logMessage(LogLevel.Debug, Prefix.MXATLAS, "Unloading MxWorld: " + mxWorld.getName());
        World w = Bukkit.getWorld(mxWorld.getWorldUID());
        for(Player p : w.getPlayers()) {
            p.teleport(Functions.getSpawnLocation());
        }
        boolean unloaded = Bukkit.unloadWorld(Bukkit.getWorld(mxWorld.getWorldUID()), save);
        if(unloaded) {
            mxWorld.setLoaded(false);
        } else {
            Logger.logMessage(LogLevel.Warning, Prefix.MXATLAS, "Could not unload MxWorld: " + mxWorld.getName());
        }
        return unloaded;
    }

    public boolean deleteMxWorld(MxWorld mxWorld) {
        if(mxWorld.isLoaded()) {
            if(!unloadMxWorld(mxWorld, false)) {
                return false;
            }
        }

        try {
            FileUtils.deleteDirectory(mxWorld.getDir());
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Warning, Prefix.MXATLAS, "Could not delete MxWorld: " + mxWorld.getName());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Optional<MxWorld> duplicateMxWorld(String name, MxWorld worldToClone, File dir) {
        UUID uuid = UUID.randomUUID();

        File directoryToCloneTo = new File(dir + "/" + uuid);
        try {
            FileUtils.copyDirectory(worldToClone.getDir(), directoryToCloneTo);
            File uidDat = new File(directoryToCloneTo.getAbsoluteFile() + "/uid.dat");
            uidDat.delete();

        } catch (IOException e) {
            Logger.logMessage(LogLevel.Warning, Prefix.MXATLAS, "Could not duplicate MxWorld: " + worldToClone.getName());
            e.printStackTrace();
            return Optional.empty();
        }
        MxWorld mxWorld = new MxWorld(name, uuid.toString(), directoryToCloneTo);
        worlds.add(mxWorld);

        return Optional.of(mxWorld);
    }

    public void unloadAll() {
        worlds.forEach(w -> {
            unloadMxWorld(w, true);
        });
    }

    public List<MxWorld> loadFolder(File dir) {
        List<MxWorld> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                File uidDat = new File(file.getAbsolutePath() + "/uid.dat");
                if(uidDat.exists()) {
                    MxWorld mxWorld = new MxWorld(file.getName(), file.getName(), file);
                    list.add(mxWorld);
                    worlds.add(mxWorld);
                    Logger.logMessage(LogLevel.Debug, Prefix.MXATLAS, "Adding world to MxAtlas: " + file.getName() + " (" + file.getAbsolutePath() + ")");
                }
            }
        }
        return list;
    }
}
