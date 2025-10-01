package nl.mxndarijn.wieisdemol.managers.chests.chestattachments;

import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.managers.chests.ChestInformation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public enum ChestAttachments {
    CHEST_COLOR_BIND("CHEST_COLOR_BIND", ChestColorBindAttachment.class, "Kist-color-bind", "wool-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("wool-chest")
                    .setName("<red>Colorbind kist")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat de kist maar door een")
                    .addLore("<gray>aantal kleuren geopend kan worden.")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze chest attachment toe te voegen.")
                    .build()),
    CHEST_LIMITED_CHOICE("CHEST_LIMITED_CHOICE", ChestLimitedChoiceAttachment.class, "Kist-limited-items", "open-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("open-chest")
                    .setName("<red>Keuze Kist")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat er maar een x aantal items")
                    .addLore("<gray>uit de kist kan worden gehaald")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze chest attachment toe te voegen.")
                    .build()),
    CHEST_LOCK("CHEST_LOCK", ChestLockAttachment.class, "Kist-lock", "locked-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("locked-chest")
                    .setName("<red>Kist slot")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat de kist eerst moet worden")
                    .addLore("<gray>geopent met een sleutel voordat de kisten open kan.")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze chest attachment toe te voegen.")
                    .build());


    private final Class<? extends ChestAttachment> attachmentClass;
    private final String name;

    private final String displayName;
    private final ItemStack is;
    private final String skullName;

    ChestAttachments(String name, Class<? extends ChestAttachment> attachment, String displayName, String skullName, ItemStack is) {
        this.attachmentClass = attachment;
        this.name = name;
        this.displayName = displayName;
        this.is = is;
        this.skullName = skullName;
    }

    public static Optional<ChestAttachments> getAttachmentByType(String type) {
        for (ChestAttachments value : values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public Optional<ChestAttachment> getExistingInstance(Map<String, Object> map, ChestInformation inf) {
        try {
            Method createFromSectionMethod = attachmentClass.getDeclaredMethod("createFromSection", Map.class, ChestInformation.class);

            return (Optional<ChestAttachment>) createFromSectionMethod.invoke(null, map, inf);
        } catch (Exception ex) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load createFromSection for: " + attachmentClass.getName());
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<ChestAttachment> createNewInstance(ChestInformation inf) {
        try {
            Method createFromSectionMethod = attachmentClass.getDeclaredMethod("createNewInstance", String.class, ChestInformation.class);
            return Optional.of((ChestAttachment) createFromSectionMethod.invoke(null, name, inf));
        } catch (Exception ex) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load createNewInstance for: " + attachmentClass.getName());
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public Pair<ItemStack, MxItemClicked> getAddItemStack(ChestInformation information) {

        MxItemClicked clicked = (mxInv, e) -> {
            information.addNewAttachment((Player) e.getWhoClicked(), this);
        };
        return new Pair<>(is, clicked);
    }

    public Class<? extends ChestAttachment> getAttachmentClass() {
        return attachmentClass;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getIs() {
        return is;
    }

    public String getSkullName() {
        return skullName;
    }
}
