package nl.mxndarijn.world.presets;

import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.wieisdemol.Functions;
import nl.mxndarijn.world.MxAtlas;
import nl.mxndarijn.world.MxWorld;
import nl.mxndarijn.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Optional;

public class Preset {

    private File directory;
    private PresetConfig config;

    private Optional<MxWorld> mxWorld;

    public static final String PRESET_ITEMMETA_TAG = "preset_id";
    private Preset(File directory) {
        this.directory = directory;
        this.mxWorld = Optional.empty();
        File presetConfigFile = new File(directory + File.separator + "preset.yml");
        if(containsWorld()) {
            if(!presetConfigFile.exists()) {
                Functions.copyFileFromResources("preset.yml", presetConfigFile);
            }
            this.config = new PresetConfig(presetConfigFile, YamlConfiguration.loadConfiguration(presetConfigFile));
            this.mxWorld = WorldManager.getInstance().getPresetById(directory.getName());
        }

    }
    public static Optional<Preset> create(File file) {
        Preset preset = new Preset(file);

        if(preset.containsWorld() && preset.mxWorld.isPresent()) {
            return Optional.of(preset);
        } else {
            return Optional.empty();
            //TODO does not contain world.
        }

    }

    public File getDirectory() {
        return directory;
    }

    public PresetConfig getConfig() {
        return config;
    }

    public ItemStack getItemStack() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1);
        if(MxHeadManager.getInstance().getAllHeadKeys().contains(config.getSkullId())) {
            builder.setSkinFromHeadsData(config.getSkullId());
        } else {
            builder.setSkinFromHeadsData("question-mark");
        }

        builder.setName(ChatColor.GRAY + config.getName())
                .addLore(" ");

        if(!config.isConfigured()) {
            builder.addLore(ChatColor.GRAY + "Wereld-Naam: " + directory.getName());
            builder.addLore(" ");
        }
        builder.addLore(ChatColor.GRAY + "Host-Moeilijkheid:")
                .addLore(getStars(config.getHostDifficulty()))
                .addLore(ChatColor.GRAY + "Speel-Moeilijkheid:")
                .addLore(getStars(config.getPlayDifficulty()));
        if(config.isLocked()) {
            builder.addLore(ChatColor.GRAY + "Locked: " + (config.isLocked() ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"))
                    .addLore(ChatColor.GRAY + "Door: " + config.getLockedBy())
                    .addLore(ChatColor.GRAY + "Reden: ")
                    .addLore(ChatColor.RED + config.getLockReason());
        }
        builder.addLore(" ")
                .addLore(ChatColor.GRAY + "Geconfigureerd: " + (config.isConfigured() ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"));

        builder.addCustomTagString(PRESET_ITEMMETA_TAG, directory.getName());


        return builder.build();
    }


    private boolean containsWorld() {
        return containsFolder("region") && containsFolder("region");
    }

    private boolean containsFolder(String folderName) {
        File f = new File(directory.getAbsolutePath() + File.separator + folderName);
        return f.exists();
    }

    private String getStars(int stars) {
        StringBuilder hostStars = new StringBuilder();
        for(int i = 1; i <= 5; i++) {
            if(i <= stars) {
                hostStars.append(ChatColor.YELLOW + "\u272B");
            } else {
                hostStars.append(ChatColor.GRAY + "\u272B");
            }
        }
        return hostStars.toString();
    }

    public boolean loadWorld() {
        if(!this.mxWorld.isPresent()) {
            return false;
        }
        return MxAtlas.getInstance().loadMxWorld(this.mxWorld.get());
    }

    public Optional<MxWorld> getMxWorld() {
        return mxWorld;
    }
}
