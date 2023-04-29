package nl.mxndarijn.game;

import nl.mxndarijn.data.Interaction;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.warps.Warp;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class InteractionManager {

    private File interactionFile;
    private FileConfiguration fc;
    private HashMap<Interaction, Boolean> interactions;

    public InteractionManager(File f) {
        this.interactionFile = f;
        interactions = new HashMap<>();
        if(!this.interactionFile.exists()) {
            try {
                this.interactionFile.createNewFile();
                fc = YamlConfiguration.loadConfiguration(f);
                for (Interaction value : Interaction.values()) {
                    interactions.put(value, value.isDefaultValue());
                    save();
                }
            } catch (IOException e) {
                Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not create interaction file (" + f.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        } else {
            fc = YamlConfiguration.loadConfiguration(f);
            loadInteractions();
        }
    }

    public void loadInteractions() {
        fc.getKeys(false).forEach(key -> {
            interactions.put(Interaction.valueOf(key), fc.getBoolean(key));
        });
    }

    public void save() {
        interactions.forEach((k,v) -> {
            fc.set(k.toString(),v);
        });
        try {
            fc.save(interactionFile);
        } catch (IOException e) {
            Logger.logMessage(LogLevel.Error, Prefix.CONFIG_FILES, "Could not save file. (" + interactionFile.getAbsolutePath() + ")");
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

    public FileConfiguration getFc() {
        return fc;
    }

}
