package nl.mxndarijn.wieisdemol.managers.chests.chestattachments;

import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.wieisdemol.data.Colors;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.chests.ChestInformation;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestColorBindAttachment extends ChestAttachment {
    private final int changeTime = 2000;
    private List<Colors> colors;
    private long time;
    private Colors currentColor;

    public static Optional<ChestAttachment> createFromSection(Map<String, Object> section, ChestInformation inf) {
        ChestColorBindAttachment attachment = new ChestColorBindAttachment();
        if (!getDefaultValues(attachment, inf, section)) {
            return Optional.empty();
        }
        assert (section != null);

        List<String> colorsString = (List<String>) section.get("colors");
        if (colorsString == null) {
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
        map.put("colors", colors.stream()
                .map(Colors::getType)
                .toList());

        return map;
    }

    @Override
    public Pair<ItemStack, MxItemClicked> getEditAttachmentItem() {
        MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("wool-chest")
                .setName("<green>Colorbind chest")
                .addBlankLore();
        builder.addLore("<gray>Kleuren die de kist kunnen openen:");
        if (colors.isEmpty()) {
            builder.addLore("<gray> - <red>Geen");
        }
        colors.forEach(color -> {
            builder.addLore("<gray> - " + color.getDisplayName());
        });

        return new Pair<>(
                builder.addBlankLore()
                        .addLore("<yellow>Klik hier om deze chest attachment aan te passen.")
                        .build(),
                (mxInv, e) -> {
                    Player p = (Player) e.getWhoClicked();
                    ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                    Optional<nl.mxndarijn.wieisdemol.map.Map> opt = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());
                    if (opt.isEmpty())
                        return;

                    nl.mxndarijn.wieisdemol.map.Map map = opt.get();
                    map.getMapConfig().getColors().forEach(mapPlayer -> {
                        list.add(new Pair<>(
                                getColorItemStack(mapPlayer),
                                (mxInv1, e1) -> {
                                    if (colors.contains(mapPlayer.getColor())) {
                                        colors.remove(mapPlayer.getColor());
                                    } else {
                                        colors.add(mapPlayer.getColor());
                                    }
                                    mxInv1.getInv().setItem(e1.getSlot(), getColorItemStack(mapPlayer));
                                }
                        ));
                    });
                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>ColorBind", MxInventorySlots.THREE_ROWS)
                            .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                            .setListItems(list)
                            .setItem(MxSkullItemStackBuilder.create(1)
                                            .setSkinFromHeadsData("red-minus")
                                            .setName("<red>Verwijder chest attachment")
                                            .addBlankLore()
                                            .addLore("<yellow>Klik hier om de chest attachment te verwijderen")

                                            .build(), 18,
                                    (mxInv12, e12) -> {
                                        information.removeChestAttachment(p, this, ChestAttachments.CHEST_COLOR_BIND);
                                        p.closeInventory();
                                    }
                            )
                            .setPreviousPageItemStackSlot(19)
                            .setItem(MxDefaultItemStackBuilder.create(Material.BARRIER)
                                            .setName("<gray>Terug")
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
                .addLore("<gray>Kan kist openen: " + (colors.contains(mapPlayer.getColor()) ? "<green>Ja" : "<red>Nee"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de kleur " + (colors.contains(mapPlayer.getColor()) ? "toe te voegen." : "te verwijderen."))

                .build();

    }

    @Override
    public void onGameStart(Game game) {
        super.onGameStart(game);
        spawnArmorStand();
        if (armorStand.isPresent() && !colors.isEmpty()) {
            armorStand.get().getEquipment().setHelmet(MxSkullItemStackBuilder.create(1).setSkinFromHeadsData(colors.get(0).getHeadKey()).build());
            this.currentColor = colors.get(0);
        }
        time = changeTime;

    }

    @Override
    public void onGameUpdate(long delta) {
        super.onGameUpdate(delta);
        if (colors.isEmpty())
            return;
        if (armorStand.isEmpty())
            return;

        time -= delta;
        if (time <= 0) {
            int index = colors.indexOf(currentColor) + 1;
            if (index >= colors.size()) {
                index = 0;
            }
            currentColor = colors.get(index);
            armorStand.get().getEquipment().setHelmet(MxSkullItemStackBuilder.create(1).setSkinFromHeadsData(currentColor.getHeadKey()).build());
            time = changeTime;
        }

    }

    @Override
    public boolean canOpenChest(GamePlayer gamePlayer) {
        return colors.contains(gamePlayer.getMapPlayer().getColor());
    }
}
