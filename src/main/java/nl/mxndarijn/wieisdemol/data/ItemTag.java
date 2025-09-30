package nl.mxndarijn.wieisdemol.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import nl.mxndarijn.wieisdemol.map.Map;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public enum ItemTag {
    NOTSILENT("notsilent", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("Silent")
                .setName("<gray>Silent")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Not silent" : "<red>silent"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "notsilent", "Silent");
    }),
    LIFEBOUND("lifebound", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("Lifebound")
                .setName("<gray>Lifebound")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Not lifebound" : "<red>lifebound"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "lifebound", "Lifebound");
    }),
    SOULBOUND("soulbound", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("Soulbound")
                .setName("<gray>Soulbound")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Not soulbound" : "<red>soulbound"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "soulbound", "Soulbound");
    }),
    DROPPABLEONCE("droppableonce", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("droppable-once")
                .setName("<gray>Droppable Once")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Not droppable" : "<red>droppable"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "droppableonce", "Droppable Once");
    }),
    DROPPABLE("droppable", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("dropper")
                .setName("<gray>Droppable")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Droppable" : "<red>Undroppable"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "droppable", "Undroppable");
    }),
    VANISHABLE("vanishable", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("ghost")
                .setName("<gray>Vanish")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Blijft" : "<red>Verdwijnt"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "vanishable", "Vanish");
    }),
    ITEM_LOCK("itemlock", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("redstone-block")
                .setName("<gray>Item-Lock")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Verplaatsbaar" : "<red>Locked"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "itemlock", "Item-Lock");
        //        boolean dataBoolean = onClick(e, "itemlock", "Item-Lock");
//
//        if (!dataBoolean) {
//            e.setCancelled(true);
//        }
    }),
    PLACEABLE("placeable", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("piston")
                .setName("<gray>Placeable")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Placeable" : "<red>Unplaceable"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "placeable", "Unplaceable");
    }),
    COLORBIND("colorbind", data -> {
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("blue-block")
                .setName("<gray>Colorbind")
                .addBlankLore()
                .addLore("<yellow>Klik hier om de colorbind van dit item aan te passen")
                .build();
    }, (mxInv, e) -> {
        AtomicReference<List<Colors>> colors = new AtomicReference<>(new ArrayList<>());
        Optional<Map> optionalMap = MapManager.getInstance().getMapByWorldUID(e.getWhoClicked().getWorld().getUID());
        Optional<Game> optionalGame = GameWorldManager.getInstance().getGameByWorldUID(e.getWhoClicked().getWorld().getUID());
        optionalMap.ifPresent(map -> {
            colors.set(map.getMapConfig().getColors().stream().map(MapPlayer::getColor).collect(Collectors.toList()));
        });
        optionalGame.ifPresent(game -> {
            colors.set(game.getColors().stream().map(g -> g.getMapPlayer().getColor()).collect(Collectors.toList()));
        });

        List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        List<Colors> addedColors = new ArrayList<>();

        String key = "colorbind";
        ItemStack is = e.getWhoClicked().getInventory().getItemInMainHand();
        ItemMeta im = is.getItemMeta();

        PersistentDataContainer container = im.getPersistentDataContainer();
        String specialTag = "<red><gray><dark_aqua><underline><reset><dark_red><reset>";
        String data = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), key), PersistentDataType.STRING);
        if (data != null) {
            for (String s : data.split(";")) {
                if (Colors.getColorByType(s).isPresent())
                    addedColors.add(Colors.getColorByType(s).get());
            }
        }

        colors.get().forEach(color -> {
            list.add(new Pair<>(MxSkullItemStackBuilder.create(1)
                    .setName("<gray>" + color.getDisplayNameWithoutColor())
                    .setSkinFromHeadsData(color.getHeadKey())
                    .addBlankLore()
                    .addLore("<gray>Status: " + (addedColors.contains(color) ? "<green>Toegevoegd" : "<red>Niet toegevoegd"))
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om deze kleur te togglen.")
                    .build(), (mxInv1, e1) -> {
                if (addedColors.contains(color)) {
                    addedColors.remove(color);
                    MSG.msg(e.getWhoClicked(), LanguageManager.getInstance().getLanguageString(LanguageText.COLORBIND_REMOVED));
                } else {
                    MSG.msg(e.getWhoClicked(), LanguageManager.getInstance().getLanguageString(LanguageText.COLORBIND_ADDED));
                    addedColors.add(color);
                }
                StringBuilder dataTag = new StringBuilder();
                addedColors.forEach(c -> {
                    dataTag.append(c.getType()).append(";");
                });
                if (!addedColors.isEmpty())
                    dataTag.deleteCharAt(dataTag.length() - 1);
                container.set(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), key), PersistentDataType.STRING, dataTag.toString());
                List<Component> loreList = im.hasLore() ? im.lore() : new ArrayList<>();
                List<Component> newLoreList = loreList.stream().filter(lore -> !Functions.convertComponentToString(lore).contains(specialTag)).collect(Collectors.toList()).stream().map(c -> MiniMessage.miniMessage().deserialize(Functions.convertComponentToString(c))).toList();
                if (!addedColors.isEmpty()) {
                    newLoreList.add(MiniMessage.miniMessage().deserialize("<!i>" + specialTag + "<blue>Colorbind:"));
                    addedColors.forEach(c -> {
                        newLoreList.add(MiniMessage.miniMessage().deserialize("<!i>" + specialTag + "<gray> - " + c.getDisplayName()));
                    });
                }
                im.lore(newLoreList);
                is.setItemMeta(im);

                e1.setCurrentItem(MxSkullItemStackBuilder.create(1)
                        .setName("<gray>" + color.getDisplayNameWithoutColor())
                        .setSkinFromHeadsData(color.getHeadKey())
                        .addBlankLore()
                        .addLore("<gray>Status: " + (addedColors.contains(color) ? "<green>Toegevoegd" : "<red>Niet toegevoegd"))
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om deze kleur te togglen.")
                        .build());
            }));
        });


        MxInventoryManager.getInstance().addAndOpenInventory((Player) e.getWhoClicked(), MxListInventoryBuilder.create("<gray>Colorbind", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .setPrevious(mxInv)
                .setShowPageNumbers(false)
                .setListItems(list)
                .build());
    }),
    CLEARABLE("clearable", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("barrier")
                .setName("<gray>Clearable")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Clearable" : "<red>Unclearable"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "clearable", "Unclearable");
    }),
    DETECTABLE("detectable", data -> {
        boolean dataBoolean = true;
        if (data != null && data.equalsIgnoreCase("false"))
            dataBoolean = false;
        return MxSkullItemStackBuilder.create(1)
                .setSkinFromHeadsData("sculk-sensor")
                .setName("<gray>Detectable")
                .addBlankLore()
                .addLore("<gray>Status: " + (dataBoolean ? "<green>Detectable" : "<red>Undetectable"))
                .addBlankLore()
                .addLore("<yellow>Klik hier om de status te togglen.")
                .build();
    }, (mxInv, e) -> {
        onClick(e, "detectable", "Undetectable");
    });

    private final String persistentDataTag;
    private final ItemTagContainer container;
    private final MxItemClicked clicked;
    ItemTag(String persistentDataTag, ItemTagContainer item, MxItemClicked clicked) {
        this.persistentDataTag = persistentDataTag;
        this.container = item;
        this.clicked = clicked;

    }

    private static void onClick(InventoryClickEvent e, String key, String loreName) {
        ItemStack is = e.getWhoClicked().getInventory().getItemInMainHand();
        ItemMeta im = is.getItemMeta();

        PersistentDataContainer container = im.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), key);

        String data = container.get(namespacedKey, PersistentDataType.STRING);

        boolean dataBoolean = data != null && data.equalsIgnoreCase("true");
        dataBoolean = !dataBoolean;

        container.set(namespacedKey, PersistentDataType.STRING, String.valueOf(dataBoolean));

        Component loreComponent = MiniMessage.miniMessage().deserialize("<!i><red>" + loreName);

        List<Component> list = im.hasLore() ? new ArrayList<>(im.lore()) : new ArrayList<>();

        if (!dataBoolean) {
            list.add(loreComponent);
        } else {
            list.removeIf(c -> PlainTextComponentSerializer.plainText().serialize(c)
                    .equalsIgnoreCase(PlainTextComponentSerializer.plainText().serialize(loreComponent)));
        }

        im.lore(list);
        is.setItemMeta(im);

        openItemTagInventory((Player) e.getWhoClicked(), is);
        MSG.msg(e.getWhoClicked(), LanguageManager.getInstance().getLanguageString(LanguageText.ITEMTAG_CHANGED));
    }

    private static void openItemTagInventory(Player p, ItemStack is) {
        PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
        List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        for (ItemTag value : ItemTag.values()) {
            try {
                list.add(new Pair<>(
                        value.getContainer().getItem(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), value.getPersistentDataTag()), PersistentDataType.STRING)),
                        value.getClicked()
                ));
            } catch(Exception ex) {
                Logger.logMessage(LogLevel.ERROR, "Could not load itemtag: ");
                ex.printStackTrace();
            }
        }
        MxInventoryManager.getInstance().addAndOpenInventory(p, new MxListInventoryBuilder("<gray>ItemTags", MxInventorySlots.THREE_ROWS)
                .setShowPageNumbers(false)
                .setListItems(list)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .build());
    }

    public String getPersistentDataTag() {
        return persistentDataTag;
    }

    public ItemTagContainer getContainer() {
        return container;
    }

    public MxItemClicked getClicked() {
        return clicked;
    }
}

