package nl.mxndarijn.game;

import nl.mxndarijn.data.Interaction;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class InteractionManager {

    private File interactionFile;
    private HashMap<Interaction, Boolean> interactions;

    public InteractionManager(File f) {
        this.interactionFile = f;
        interactions = new HashMap<>();
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        if(!this.interactionFile.exists()) {
            try {
                this.interactionFile.createNewFile();
                fc = YamlConfiguration.loadConfiguration(f);
                for (Interaction value : Interaction.values()) {
                    interactions.put(value, value.isDefaultValue());
                    save();
                }
            } catch (IOException e) {
                Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not create interaction file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        } else {
            fc = YamlConfiguration.loadConfiguration(f);
            loadInteractions();
        }
    }

    public void loadInteractions() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(interactionFile);
        fc.getKeys(false).forEach(key -> {
            interactions.put(Interaction.valueOf(key), fc.getBoolean(key));
        });
        for (Interaction value : Interaction.values()) {
            if(!interactions.containsKey(value)) {
                interactions.put(value, value.isDefaultValue());
            }
        }
    }

    public void save() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(interactionFile);
        interactions.forEach((k,v) -> {
            fc.set(k.toString(),v);
        });
        try {
            fc.save(interactionFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.CONFIG_FILES, "Could not save file. (" + interactionFile.getAbsolutePath() + ")");
            e.printStackTrace();
        }
    }

    public void setInteraction(Interaction i, boolean value) {
        interactions.put(i, value);
        save();
    }

    public File getInteractionFile() {
        return interactionFile;
    }

    public HashMap<Interaction, Boolean> getInteractions() {
        return interactions;
    }


}
