package nl.mxndarijn.wieisdemol.managers.chests.chestattachments;

import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.managers.chests.ContainerInformation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public enum ContainerAttachments {
    CONTAINER_COLOR_BIND("CONTAINER_COLOR_BIND", ContainerColorBindAttachment.class, "Container-color-bind", "wool-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("wool-chest")
                    .setName("<red>Colorbind container")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat de container maar door een")
                    .addLore("<gray>aantal kleuren geopend kan worden.")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze container attachment toe te voegen.")
                    .build()),
    CONTAINER_LIMITED_CHOICE("CONTAINER_LIMITED_CHOICE", ContainerLimitedChoiceAttachment.class, "Container-limited-items", "open-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("open-chest")
                    .setName("<red>Keuze Container")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat er maar een x aantal items")
                    .addLore("<gray>uit de container kan worden gehaald")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze container attachment toe te voegen.")
                    .build()),
    CONTAINER_LOCK("CONTAINER_LOCK", ContainerLockAttachment.class, "Container-lock", "locked-chest",
            MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("locked-chest")
                    .setName("<red>Container slot")
                    .addBlankLore()
                    .addLore("<gray>Deze Attachment zorgt ervoor dat de container eerst moet worden")
                    .addLore("<gray>geopend met een sleutel voordat de container open kan.")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze container attachment toe te voegen.")
                    .build());


    private final Class<? extends ContainerAttachment> attachmentClass;
    private final String name;

    private final String displayName;
    private final ItemStack is;
    private final String skullName;

    ContainerAttachments(String name, Class<? extends ContainerAttachment> attachment, String displayName, String skullName, ItemStack is) {
        this.attachmentClass = attachment;
        this.name = name;
        this.displayName = displayName;
        this.is = is;
        this.skullName = skullName;
    }

    public static Optional<ContainerAttachments> getAttachmentByType(String type) {
        for (ContainerAttachments value : values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public Optional<ContainerAttachment> getExistingInstance(Map<String, Object> map, ContainerInformation inf) {
        try {
            Method createFromSectionMethod = attachmentClass.getDeclaredMethod("createFromSection", Map.class, ContainerInformation.class);

            return (Optional<ContainerAttachment>) createFromSectionMethod.invoke(null, map, inf);
        } catch (Exception ex) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load createFromSection for: " + attachmentClass.getName());
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<ContainerAttachment> createNewInstance(ContainerInformation inf) {
        try {
            Method createFromSectionMethod = attachmentClass.getDeclaredMethod("createNewInstance", String.class, ContainerInformation.class);
            return Optional.of((ContainerAttachment) createFromSectionMethod.invoke(null, name, inf));
        } catch (Exception ex) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MAPS_MANAGER, "Could not load createNewInstance for: " + attachmentClass.getName());
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    public Pair<ItemStack, MxItemClicked> getAddItemStack(ContainerInformation information) {

        MxItemClicked clicked = (mxInv, e) -> {
            information.addNewAttachment((Player) e.getWhoClicked(), this);
        };
        return new Pair<>(is, clicked);
    }

    public Class<? extends ContainerAttachment> getAttachmentClass() {
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
