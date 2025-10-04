package nl.mxndarijn.wieisdemol.map.mapscript.manager;

import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.wieisdemol.map.mapscript.MapParameter;
import nl.mxndarijn.wieisdemol.map.mapscript.MapParameterType;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Persists and manages MapScript parameters to a YAML file.
 * Only the current value is stored on disk; type/description/default live in code.
 * Location values are stored under a nested "value" section via MxLocation.
 */
public class ScriptParameterManager {

    private final @NotNull File file;
    private final @NotNull MapScript mapScript;

    // in-memory current values by id
    private final Map<String, Object> values = new HashMap<>();

    public ScriptParameterManager(@NotNull MapScript mapScript, @NotNull File file) {
        this.mapScript = Objects.requireNonNull(mapScript, "mapScript");
        this.file = Objects.requireNonNull(file, "file");
        ensureFile();
        loadAndSync();
    }

    private void ensureFile() {
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not create parameters file (" + file.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }
    }

    /**
     * Load the configuration, ensure all parameters exist, and populate in-memory values.
     */
    private void loadAndSync() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, Prefix.CONFIG_FILES, "Loaded parameters file (" + file.getAbsolutePath() + ")");
        for (MapParameter<?> param : mapScript.getMapParameters()) {
            Logger.logMessage(LogLevel.DEBUG_HIGHLIGHT, Prefix.CONFIG_FILES, "Loading parameter " + param.getId() + " (" + param.getType() + ")");
            String id = sanitizeId(param.getId());
            ConfigurationSection sec = fc.getConfigurationSection(id);

            // Load existing persisted value; if absent, fall back to in-plugin default (do not write it).
            Object valueLoaded = loadValueFromSection(sec, param.getType());
            if (valueLoaded == null) {
                valueLoaded = normalizeValueForSave(param.getType(), param.getDefaultValue());
            }
            values.put(id, normalizeValueForMemory(param.getType(), valueLoaded));
        }
    }

    private @Nullable Object loadValueFromSection(@Nullable ConfigurationSection sec, MapParameterType type) {
        if (sec == null || !sec.contains("value")) return null;
        return switch (type) {
            case STRING -> sec.getString("value");
            case NUMBER -> sec.getInt("value");
            case DECIMAL -> sec.getDouble("value");
            case BOOLEAN -> sec.getBoolean("value");
            case LOCATION -> {
                ConfigurationSection locSec = sec.getConfigurationSection("value");
                if (locSec == null) yield null;
                yield MxLocation.loadFromConfigurationSection(locSec).orElse(null);
            }
        };
    }

    private void writeValueToSection(ConfigurationSection sec, MapParameterType type, @Nullable Object value) {
        if (value == null) {
            sec.set("value", null);
            return;
        }
        switch (type) {
            case STRING, NUMBER, DECIMAL, BOOLEAN -> sec.set("value", value);
            case LOCATION -> {
                ConfigurationSection locSec = sec.getConfigurationSection("value");
                if (locSec == null) locSec = sec.createSection("value");
                ((MxLocation) value).write(locSec);
            }
        }
    }

    private @Nullable Object normalizeValueForSave(MapParameterType type, @Nullable Object value) {
        if (value == null) return null;
        return switch (type) {
            case STRING, NUMBER, DECIMAL, BOOLEAN -> value;
            case LOCATION -> {
                if (value instanceof Location l) {
                    yield MxLocation.getFromLocation(l);
                }
                if (value instanceof MxLocation ml) {
                    yield ml;
                }
                yield null;
            }
        };
    }

    private @Nullable Object normalizeValueForMemory(MapParameterType type, @Nullable Object value) {
        if (value == null) return null;
        return switch (type) {
            case STRING, NUMBER, DECIMAL, BOOLEAN -> value;
            case LOCATION -> {
                if (value instanceof Location l) yield MxLocation.getFromLocation(l);
                if (value instanceof MxLocation ml) yield ml;
                yield null;
            }
        };
    }

    private String sanitizeId(String id) {
        // Ensure no spaces as requested; fallback to replacing with underscores
        return id == null ? "" : id.trim().replace(' ', '_');
    }

    // ---- Getters ----
    public Optional<String> getString(String id) { return Optional.ofNullable((String) values.get(sanitizeId(id))); }
    public Optional<Integer> getNumber(String id) { return Optional.ofNullable((Integer) values.get(sanitizeId(id))); }
    public Optional<Double> getDecimal(String id) { return Optional.ofNullable((Double) values.get(sanitizeId(id))); }
    public Optional<Boolean> getBoolean(String id) { return Optional.ofNullable((Boolean) values.get(sanitizeId(id))); }
    public Optional<MxLocation> getLocation(String id) { return Optional.ofNullable((MxLocation) values.get(sanitizeId(id))); }

    // ---- Setters (update in-memory and persist to file) ----
    public void setString(String id, @Nullable String value) { setValue(id, MapParameterType.STRING, value); }
    public void setNumber(String id, @Nullable Integer value) { setValue(id, MapParameterType.NUMBER, value); }
    public void setDecimal(String id, @Nullable Double value) { setValue(id, MapParameterType.DECIMAL, value); }
    public void setBoolean(String id, @Nullable Boolean value) { setValue(id, MapParameterType.BOOLEAN, value); }
    public void setLocation(String id, @Nullable MxLocation value) { setValue(id, MapParameterType.LOCATION, value); }

    private void setValue(String idRaw, MapParameterType type, @Nullable Object value) {
        String id = sanitizeId(idRaw);
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = fc.getConfigurationSection(id);
        if (sec == null) sec = fc.createSection(id);
        // keep metadata untouched; only write value
        writeValueToSection(sec, type, normalizeValueForSave(type, value));
        values.put(id, normalizeValueForMemory(type, value));
        saveConfig(fc);
    }

    public void saveAll() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        // Clear file and rewrite only current values
        for (String key : new HashSet<>(fc.getKeys(false))) {
            fc.set(key, null);
        }
        for (MapParameter<?> param : mapScript.getMapParameters()) {
            String id = sanitizeId(param.getId());
            ConfigurationSection sec = fc.createSection(id);
            Object current = values.get(id);
            writeValueToSection(sec, param.getType(), normalizeValueForSave(param.getType(), current));
        }
        saveConfig(fc);
    }

    private void saveConfig(FileConfiguration fc) {
        try {
            fc.save(file);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save parameters file (" + file.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }
}
