package nl.mxndarijn.inventory.heads;

import nl.mxndarijn.data.ConfigFiles;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MxHeadSection {

    private Optional<String> value = Optional.empty();
    private Optional<UUID> uuid = Optional.empty();
    private Optional<MxHeadsType> type = Optional.empty();
    private Optional<String> name = Optional.empty();

    private String key;
    private MxHeadSection() {

    }

    public static Optional<MxHeadSection> create(String textureName, String displayName, MxHeadsType type, String texture, UUID uuid) {
        MxHeadSection mxHeadSection = new MxHeadSection();
        mxHeadSection.setKey(textureName);
        mxHeadSection.setName(displayName);
        mxHeadSection.setType(type);
        mxHeadSection.setUuid(uuid);
        mxHeadSection.setValue(texture);
        return mxHeadSection.validate() ? Optional.of(mxHeadSection) : Optional.empty();
    }

    public static Optional<MxHeadSection> create(String textureName, String displayName, MxHeadsType type, String texture) {
        MxHeadSection mxHeadSection = new MxHeadSection();
        mxHeadSection.setKey(textureName);
        mxHeadSection.setName(displayName);
        mxHeadSection.setType(type);
        mxHeadSection.setValue(texture);
        return mxHeadSection.validate() ? Optional.of(mxHeadSection) : Optional.empty();
    }


    public Optional<String> getValue() {
        return value;
    }

    public Optional<UUID> getUuid() {
        return uuid;
    }

    public Optional<MxHeadsType> getType() {
        return type;
    }

    public Optional<String> getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = Optional.ofNullable(value);
    }

    public void setUuid(UUID uuid) {
        this.uuid = Optional.ofNullable(uuid);
    }

    public void setType(MxHeadsType type) {
        this.type = Optional.ofNullable(type);
    }

    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean validate() {
        return name.isPresent() && value.isPresent() && type.isPresent() && (type.get() != MxHeadsType.PLAYER || uuid.isPresent());
    }

    public static Optional<MxHeadSection> loadHead(String key) {
        ConfigurationSection section = ConfigFiles.HEAD_DATA.getFileConfiguration().getConfigurationSection(key);
        if(section == null) {
            Logger.logMessage(LogLevel.Error, Prefix.MXHEADMANAGER, "Could not load head: " + key);
            return Optional.empty();
        }
        MxHeadSection mxHeadSection = new MxHeadSection();
        mxHeadSection.name = Optional.ofNullable(section.getString("name", null));
        mxHeadSection.value = Optional.ofNullable(section.getString("value", null));

        String uuidValue = section.getString("uuid", null);
        if(uuidValue != null) {
            mxHeadSection.uuid = Optional.of(UUID.fromString(uuidValue));
        }
        mxHeadSection.type = MxHeadsType.getTypeFromName(section.getString("type", null));
        mxHeadSection.key = key;
        return mxHeadSection.validate() ? Optional.of(mxHeadSection) : Optional.empty();

    }

    public void apply() {
        Logger.logMessage(LogLevel.Debug, Prefix.MXHEADMANAGER, "Saving MxHeadSection " + key + "...");
        if(!validate()) {
            Logger.logMessage(LogLevel.Error, Prefix.MXHEADMANAGER, "Could not save MxHeadSection " + key + " because it was not valid.");
            return;
        }
        FileConfiguration cf = ConfigFiles.HEAD_DATA.getFileConfiguration();
        ConfigurationSection section = cf.getConfigurationSection(key);
        if(section == null) {
            section = cf.createSection(key);
        }
        section.set("name", name.get());
        section.set("value", value.get());
        section.set("type", type.get().getType());
        if(uuid.isPresent()) {
            section.set("uuid", uuid.get().toString());
        }

    }
}
