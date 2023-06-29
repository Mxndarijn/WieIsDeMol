package nl.mxndarijn.world.presets;

import nl.mxndarijn.game.Colors;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class PresetConfig {
    private FileConfiguration fc;
    private File file;

    private String name;
    private int hostDifficulty;
    private int playDifficulty;
    private String skullId;

    private boolean locked;
    private String lockedBy;
    private String lockReason;

    private boolean configured;

    private HashMap<Colors, MxLocation> colors;
    public PresetConfig(File file, FileConfiguration fc) {
        this.file = file;
        this.fc = fc;
        Arrays.stream(PresetConfigValue.values()).forEach(value -> {
            if(!fc.contains(value.getConfigValue())) {
                Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not find config value: " + value + " (" + file.getAbsolutePath() + ")");
            }
        });

        name = fc.getString(PresetConfigValue.NAME.getConfigValue());
        hostDifficulty = fc.getInt(PresetConfigValue.HOST_DIFFICULTY.getConfigValue());
        playDifficulty = fc.getInt(PresetConfigValue.PLAY_DIFFICULTY.getConfigValue());
        skullId = fc.getString(PresetConfigValue.SKULL_ID.getConfigValue());

        locked = fc.getBoolean(PresetConfigValue.LOCKED.getConfigValue());
        lockedBy = fc.getString(PresetConfigValue.LOCKED_BY.getConfigValue());
        lockReason = fc.getString(PresetConfigValue.LOCK_REASON.getConfigValue());

        configured = fc.getBoolean(PresetConfigValue.CONFIGURED.getConfigValue());

        colors = new HashMap<>();
        ConfigurationSection colorSection = fc.getConfigurationSection(PresetConfigValue.COLORS.getConfigValue());
        colorSection.getKeys(false).forEach(key -> {
            Optional<Colors> color = Colors.getColorByType(key);
            if(color.isPresent()) {
                Optional<MxLocation> optionalMxLocation = MxLocation.loadFromConfigurationSection(colorSection.getConfigurationSection(key));
                if(optionalMxLocation.isPresent()) {
                    MxLocation mxLocation = optionalMxLocation.get();
                    colors.put(color.get(), mxLocation);
                } else {
                    Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not load spawnpoint for color: " + key + " (" + file.getAbsolutePath() + ")");
                }

            } else {
                Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not load color: " + key + " (" + file.getAbsolutePath() + ")");
            }

        });
    }

    public void save() {
        fc.set(PresetConfigValue.NAME.getConfigValue(),name);
        fc.set(PresetConfigValue.HOST_DIFFICULTY.getConfigValue(),hostDifficulty);
        fc.set(PresetConfigValue.PLAY_DIFFICULTY.getConfigValue(),playDifficulty);
        fc.set(PresetConfigValue.SKULL_ID.getConfigValue(),skullId);
        fc.set(PresetConfigValue.LOCKED.getConfigValue(),locked);
        fc.set(PresetConfigValue.LOCKED_BY.getConfigValue(),lockedBy);
        fc.set(PresetConfigValue.LOCK_REASON.getConfigValue(),lockReason);
        fc.set(PresetConfigValue.CONFIGURED.getConfigValue(),configured);

        fc.set(PresetConfigValue.COLORS.getConfigValue(), null);

        ConfigurationSection section = fc.createSection(PresetConfigValue.COLORS.getConfigValue());
        for(Colors c : colors.keySet()) {
            colors.get(c).write(section.createSection(c.getType()));
        }

        try {
            fc.save(file);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.PRESETS_MANAGER, "Could not save preset config: " + file.getAbsolutePath());
            e.printStackTrace();
        }

    }

    public void setFc(FileConfiguration fc) {
        this.fc = fc;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHostDifficulty(int hostDifficulty) {
        this.hostDifficulty = hostDifficulty;
    }

    public void setPlayDifficulty(int playDifficulty) {
        this.playDifficulty = playDifficulty;
    }

    public void setSkullId(String skullId) {
        this.skullId = skullId;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public String getName() {
        return name;
    }

    public int getHostDifficulty() {
        return hostDifficulty;
    }

    public int getPlayDifficulty() {
        return playDifficulty;
    }

    public String getSkullId() {
        return skullId;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public String getLockReason() {
        return lockReason;
    }

    public boolean isConfigured() {
        return configured;
    }

    public HashMap<Colors, MxLocation> getColors() {
        return colors;
    }

}
