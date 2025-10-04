package nl.mxndarijn.wieisdemol.managers.chests;

import lombok.Getter;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerManager {

    private final File chestFile;
    @Getter
    private final List<ContainerInformation> chests;

    public ContainerManager(File f) {
        this.chestFile = f;
        if (!this.chestFile.exists()) {
            try {
                this.chestFile.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not create chest file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
        chests = loadFile();
    }

    private List<ContainerInformation> loadFile() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(chestFile);
        ArrayList<ContainerInformation> list = new ArrayList<>();

        fc.getKeys(false).forEach(key -> {
            Optional<ContainerInformation> optionalChestInformation = ContainerInformation.load(fc.getConfigurationSection(key));
            optionalChestInformation.ifPresent(list::add);
        });

        return list;
    }

    public void save() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(chestFile);
        fc.getKeys(false).forEach(k -> {
            fc.set(k, null);
        });
        chests.forEach(w -> {
            w.save(fc);
        });
        try {
            fc.save(chestFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save file. (" + chestFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void addChest(ContainerInformation information) {
        chests.add(information);
        save();
    }

    public void removeChest(ContainerInformation information) {
        chests.remove(information);
        save();
    }

    public boolean containsLocation(MxLocation location) {
        for (ContainerInformation chest : chests) {
            if (chest.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }


    public Optional<ContainerInformation> getChestByLocation(MxLocation location) {
        for (ContainerInformation chest : chests) {
            if (chest.getLocation().equals(location)) {
                return Optional.of(chest);
            }
        }
        return Optional.empty();
    }

    public int getAmountOfChestsFilled(Map map) {
        int filled = 0;
        Optional<MxWorld> world = map.getMxWorld();
        if (world.isEmpty())
            return -1;
        MxWorld m = world.get();
        World w = Bukkit.getWorld(m.getWorldUID());
        for (ContainerInformation chestInformation : chests) {
            Block b = chestInformation.getLocation().getLocation(w).getBlock();
            if (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST) {
                Chest c = (Chest) b.getState();
                if (!c.getBlockInventory().isEmpty()) {
                    filled++;
                }
            }
        }
        return filled;
    }

    public void onGameStart(Game game) {
        chests.forEach(c -> {
            c.getChestAttachmentList().forEach(a -> {
                a.onGameStart(game);
            });
        });
    }
}
