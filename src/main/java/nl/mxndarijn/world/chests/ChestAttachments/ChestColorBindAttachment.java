package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.data.Colors;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChestColorBindAttachment extends ChestAttachment {
    private List<Colors> colors;

    public static Optional<ChestAttachment> createFromSection(Map<String, Object> section, ChestInformation inf) {
        ChestColorBindAttachment attachment = new ChestColorBindAttachment();
        if(!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert(section != null);

        List<String> colorsString = (List<String>) section.get("colors");
        if(colorsString == null) {
            colorsString = new ArrayList<>();
        }
        List<Colors> colorsList = colorsString.stream()
                .map(Colors::getColorByType)
                .flatMap(Optional::stream)
                .toList();
        attachment.setColors(colorsList);

        return Optional.of(attachment);
    }

    public static ChestAttachment createNewInstance(String type, ChestInformation inf) {
        ChestColorBindAttachment attachment = new ChestColorBindAttachment();
        attachment.setDefaults(type, inf);
        attachment.colors = new ArrayList<>();
        return attachment;
    }

    public void setColors(List<Colors> colors) {
        this.colors = colors;
    }

    @Override
    public Map<String, Object> getDataForSaving() {
        Map<String, Object> map = new HashMap<>();
        getDataDefaults(map);
        map.put("colors",  colors.stream()
                .map(Colors::getType)
                .toList());

        return map;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("wool-chest")
                .setName(ChatColor.GREEN + "Colorbind chest")
                .addBlankLore();
        builder.addLore(ChatColor.GRAY + "Kleuren die de kist kunnen openen:");
        if(colors.isEmpty()) {
            builder.addLore(ChatColor.GRAY + " - " + ChatColor.RED + "Geen");
        }
        colors.forEach(color -> {
            builder.addLore(ChatColor.GRAY + " - " +color.getDisplayName());
        });

        return new Pair<>(
                builder.addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    //TODO
                }

        );
    }
}
