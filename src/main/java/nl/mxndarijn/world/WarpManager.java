package nl.mxndarijn.world;

import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.warps.Warp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarpManager {

    private File warpFile;
    private FileConfiguration fc;
    private List<Warp> warps;

    public WarpManager(File f) {
        this.warpFile = f;
        if(!this.warpFile.exists()) {
            try {
                this.warpFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not create warp file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        fc = YamlConfiguration.loadConfiguration(f);
        warps = Warp.getWarpsFromFile(f, fc);
    }

    public List<Warp> getWarps() {
        return warps;
    }

    public void save() {
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        warps.forEach( w -> {
            w.save(warpFile, fc);
        });
        try {
            fc.save(warpFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not save file. (" + warpFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void addWarp(Warp w) {
        warps.add(w);
        save();
    }

    public void removeWarp(Warp w) {
        warps.remove(w);
        save();
    }

    public File getWarpFile() {
        return warpFile;
    }

    public FileConfiguration getFc() {
        return fc;
    }

    public Optional<Warp> getWarpByName(String warpName) {
        for (Warp warp : warps) {
            if(warp.getName().equals(warpName))
                return Optional.of(warp);
        }
        return Optional.empty();
    }
}
