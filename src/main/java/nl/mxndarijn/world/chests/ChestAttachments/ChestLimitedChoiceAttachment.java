package nl.mxndarijn.world.chests.ChestAttachments;

import nl.mxndarijn.data.Colors;
import nl.mxndarijn.inventory.MxInventory;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.inventory.item.Pair;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import nl.mxndarijn.world.chests.ChestInformation;
import nl.mxndarijn.world.mxworld.MxLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChestLimitedChoiceAttachment extends ChestAttachment {
    private int choices;

    public static Optional<ChestAttachment> createFromSection(Map<String, Object> section, ChestInformation inf) {
        ChestLimitedChoiceAttachment attachment = new ChestLimitedChoiceAttachment();
        if(!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert(section != null);

        if(!section.containsKey("choices")) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load choices amount. Type: " + attachment.type);
            return Optional.empty();
        }
        attachment.setChoices((int)section.get("choices"));

        return Optional.of(attachment);
    }

    public static ChestAttachment createNewInstance(String type, ChestInformation inf) {
        ChestLimitedChoiceAttachment attachment = new ChestLimitedChoiceAttachment();
        attachment.setDefaults(type, inf);
        attachment.choices = 1;
        return attachment;
    }

    public void setChoices(int choices) {
        this.choices = choices;
    }


    @Override
    public Map<String, Object> getDataForSaving() {
        Map<String, Object> map = new HashMap<>();
        getDataDefaults(map);
        map.put("choices", choices);

        return map;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        return new Pair<>(
                MxSkullItemStackBuilder.create(1)
                        .setSkinFromHeadsData("open-chest")
                        .setName(ChatColor.GREEN + "Keuze kist")
                        .addBlankLore()
                        .addLore(ChatColor.GRAY + "Max items te verwijderen: " + choices)
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    //TODO
                }

        );
    }
}
