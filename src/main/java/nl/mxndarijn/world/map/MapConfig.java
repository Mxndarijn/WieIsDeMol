package nl.mxndarijn.world.map;

import nl.mxndarijn.game.Colors;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.mxworld.MxLocation;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.presets.PresetConfigValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapConfig {
    private FileConfiguration fc;
    private File file;
    private HashMap<Colors, MxLocation> colors;

    private String name;
    private UUID owner;

    private ArrayList<UUID> sharedPlayers;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    private PresetConfig presetConfig;

    public MapConfig(File file, FileConfiguration fc, String name, UUID owner) {
        this.file = file;
        this.fc = fc;

        File presetConfigFile = new File(file.getParent(), "preset.yml");
        FileConfiguration presetCf = YamlConfiguration.loadConfiguration(presetConfigFile);
        this.presetConfig = new PresetConfig(presetConfigFile, presetCf);

        this.name = name;
        this.owner = owner;
        this.dateCreated = LocalDateTime.now();
        this.dateModified = LocalDateTime.now();
    }

    public MapConfig(File file, FileConfiguration fc) {
        this.file = file;
        this.fc = fc;
        Arrays.stream(MapConfigValue.values()).forEach(value -> {
            if(!fc.contains(value.getConfigValue())) {
                Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Could not find config value: " + value + " (" + file.getAbsolutePath() + ")");
            }
        });
        File presetConfigFile = new File(file.getParent(), "preset.yml");
        FileConfiguration presetCf = YamlConfiguration.loadConfiguration(presetConfigFile);
        this.presetConfig = new PresetConfig(presetConfigFile, presetCf);

        this.name = fc.getString(MapConfigValue.NAME.getConfigValue());
        this.owner = UUID.fromString(fc.getString(MapConfigValue.OWNER.getConfigValue(), new File(file.getParent()).getName()));
        this.sharedPlayers = new ArrayList<>();
        this.sharedPlayers.addAll(fc.getStringList(MapConfigValue.SHARED_PLAYERS.getConfigValue())
                .stream()
                .map(UUID::fromString)
                .toList());
        this.dateModified = LocalDateTime.parse(fc.getString(MapConfigValue.DATE_MODIFIED.getConfigValue(), LocalDateTime.MIN.toString()));
        this.dateCreated = LocalDateTime.parse(fc.getString(MapConfigValue.DATE_CREATED.getConfigValue(), LocalDateTime.MIN.toString()));
    }

    public void save() {
        if (file == null || fc == null) {
            Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Cannot save MapConfig. File or FileConfiguration is null.");
            return;
        }

        fc.set(MapConfigValue.NAME.getConfigValue(), name);
        fc.set(MapConfigValue.OWNER.getConfigValue(), owner.toString());
        fc.set(MapConfigValue.SHARED_PLAYERS.getConfigValue(), sharedPlayers.stream().map(UUID::toString).collect(Collectors.toList()));
        fc.set(MapConfigValue.DATE_CREATED.getConfigValue(), dateCreated.toString());
        fc.set(MapConfigValue.DATE_MODIFIED.getConfigValue(), dateModified.toString());

        try {
            fc.save(file);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.MAPS_MANAGER, "Failed to save MapConfig: " + e.getMessage());
        }
    }



    public FileConfiguration getFc() {
        return fc;
    }

    public void setFc(FileConfiguration fc) {
        this.fc = fc;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public HashMap<Colors, MxLocation> getColors() {
        return colors;
    }

    public void setColors(HashMap<Colors, MxLocation> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public ArrayList<UUID> getSharedPlayers() {
        return sharedPlayers;
    }

    public void setSharedPlayers(ArrayList<UUID> sharedPlayers) {
        this.sharedPlayers = sharedPlayers;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public PresetConfig getPresetConfig() {
        return presetConfig;
    }

    public void setPresetConfig(PresetConfig presetConfig) {
        this.presetConfig = presetConfig;
    }
}
