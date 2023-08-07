package nl.mxndarijn.managers.chests.ChestAttachments;

import nl.mxndarijn.data.Colors;
import nl.mxndarijn.logic.inventory.*;
import nl.mxndarijn.logic.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.MxSkullItemStackBuilder;
import nl.mxndarijn.logic.inventory.item.Pair;
import nl.mxndarijn.logic.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.managers.chests.ChestInformation;
import nl.mxndarijn.managers.MapManager;
import nl.mxndarijn.logic.map.mapplayer.MapPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        List<Colors> colorsList = new ArrayList<>(colorsString.stream()
                .map(Colors::getColorByType)
                .flatMap(Optional::stream)
                .toList());
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
                    Player p = (Player) e.getWhoClicked();
                    ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                    Optional<nl.mxndarijn.logic.map.Map> opt = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());
                    if(opt.isEmpty())
                        return;

                    nl.mxndarijn.logic.map.Map map = opt.get();
                    map.getMapConfig().getColors().forEach(mapPlayer -> {
                        list.add(new Pair<>(
                                getColorItemStack(mapPlayer),
                                (mxInv1, e1) -> {
                                    if(colors.contains(mapPlayer.getColor())) {
                                        colors.remove(mapPlayer.getColor());
                                    } else {
                                        colors.add(mapPlayer.getColor());
                                    }
                                    mxInv1.getInv().setItem(e1.getSlot(), getColorItemStack(mapPlayer));
                                }
                        ));
                    });
                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "ColorBind", MxInventorySlots.THREE_ROWS)
                            .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                            .setListItems(list)
                                    .setItem(MxSkullItemStackBuilder.create(1)
                                                    .setSkinFromHeadsData("red-minus")
                                                    .setName(ChatColor.RED + "Verwijder chest attachment")
                                                    .addBlankLore()
                                                    .addLore(ChatColor.YELLOW + "Klik hier om de chest attachment te verwijderen")

                                                    .build(), 18,
                                            (mxInv12, e12) -> {
                                                information.removeChestAttachment(p, this, ChestAttachments.CHEST_COLOR_BIND);
                                                p.closeInventory();
                                            }
                                    )
                                    .setPreviousPageItemStackSlot(19)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.BARRIER)
                                                    .setName(ChatColor.GRAY + "Terug")
                                                    .build()
                                            , 22,
                                            (mxInv13, e13) -> {
                                                information.openAttachmentsInventory(p);
                                            })
                            .build()

                    );
                }

        );
    }

    private ItemStack getColorItemStack(MapPlayer mapPlayer) {
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData(mapPlayer.getColor().getHeadKey())
                .setName(mapPlayer.getColor().getDisplayName())
                .addBlankLore()
                .addLore(ChatColor.GRAY +"Kan kist openen: " + (colors.contains(mapPlayer.getColor()) ? ChatColor.GREEN + "Ja" : ChatColor.RED + "Nee"))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de kleur " + (colors.contains(mapPlayer.getColor()) ?  "toe te voegen." : "te verwijderen."))

                .build();

    }
}
