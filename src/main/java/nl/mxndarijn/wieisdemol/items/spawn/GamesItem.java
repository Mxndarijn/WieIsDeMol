package nl.mxndarijn.wieisdemol.items.spawn;

import nl.mxndarijn.api.chatinput.MxChatInputCallback;
import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxDefaultInventoryBuilder;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GamesItem extends MxItem {


    public GamesItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        MxListInventoryBuilder builder = MxListInventoryBuilder.create(ChatColor.GRAY + "Games", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO);
        if(p.hasPermission(Permissions.ITEM_GAMES_CREATE_GAME.getPermission())) {
            builder.setItem(MxSkullItemStackBuilder.create(1)
                            .setName(ChatColor.GRAY + "Plan Game")
                            .setSkinFromHeadsData("wooden-plus")
                            .addBlankLore()
                            .addLore(ChatColor.GRAY + "Plan een nieuwe game.")
                            .addBlankLore()
                            .addLore(ChatColor.YELLOW + "Klik hier om een nieuwe game te plannen.")
                            .build(),
                    22,
                    (mxInv, e1) -> {
                        LocalDateTime plannedTime = getTime(p);
                    }
            );
        }

        MxInventoryManager.getInstance().addAndOpenInventory(p, builder.build());
    }

    private void getMap(MxInventory prevInv, Player p) {
        MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create(ChatColor.GRAY + "Map Selecteren", MxInventorySlots.THREE_ROWS)
                        .setPrevious(prevInv)
                        .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                        .setName(ChatColor.GREEN + "Eigen Mappen")
                                        .addLore(ChatColor.GRAY + "Bekijk je eigen mappen")
                                        .addLore(" ")
                                        .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                        .build(),
                                12,
                                (mxInv, e) -> {
                                    List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getOwner().equals(p.getUniqueId())).toList();

                                    MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p, prevInv);

                                    ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Eigen Mappen", MxInventorySlots.SIX_ROWS)
                                            .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                            .setPreviousItemStackSlot(46)
                                            .setPrevious(prevInv)
                                            .setListItems(list)
                                            .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                                    .setName(ChatColor.GRAY + "Info")
                                                    .addLore(" ")
                                                    .addLore(ChatColor.YELLOW + "Klik op een map om deze te selecteren.")
                                                    .build(), 49,null)
                                            .build());

                                })
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOKSHELF)
                                .setName(ChatColor.GREEN + "Gedeelde mappen")
                                .addLore(ChatColor.GRAY + "Bekijk alle mappen die met je gedeeld zijn.")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(), 14,
                        (mxInv, e) -> {
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getSharedPlayers().contains(p.getUniqueId())).toList();

                            MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p, prevInv);

                            ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Gedeelde Mappen", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(prevInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName(ChatColor.GRAY + "Info")
                                            .addLore(" ")
                                            .addLore(ChatColor.YELLOW + "Klik op een map om deze te selecteren.")
                                            .build(), 49,null)
                                    .build());

                        })
                .build())
                ;
    }
    
    private void getTime(MxInventory prevInv,  Player p, Map map) {
        CompletableFuture<LocalDateTime> future = new CompletableFuture<>();
        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for(int i = 0; i < 16; i++) {
            LocalDate date = now.plusDays(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.forLanguageTag("nl-NL"));
            String formattedDate = date.format(formatter);
            MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("clock")
                    .setName(ChatColor.GRAY + formattedDate)
                    .addBlankLore()
                    .addLore(ChatColor.GRAY + "Datum: " + (i != 0 ? (i == 1 ? "Morgen" : "Over " + i + " dagen.") :  "Vandaag"))
                    .addBlankLore()
                    .addLore(ChatColor.YELLOW + "Klik om de game te hosten op " + formattedDate + ".");
            if(i == 0)
                formattedDate += " (Vandaag)";
            if(i == 1)
                formattedDate += " (Morgen)";

            String finalFormattedDate = formattedDate;
            list.add(new Pair<>(builder.build(), (mxInv, e) -> {
                p.closeInventory();
                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_DATE_SELECTED, Collections.singletonList(finalFormattedDate)));
                p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_ENTER_TIME));
                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm");
                    try {
                        LocalTime localTime = LocalTime.parse(message, formatter1);
                        LocalDateTime localDateTime = LocalDateTime.of(date, localTime);
                    } catch (DateTimeParseException ex) {
                        p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_COULD_NOT_PARSE_TIME));
                    }
                });

            }));
        }
        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Datum selecteren", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .setListItems(list)
                .setPrevious(prevInv)
                .build());
    }

    private MxItemClicked getClickedOnPlayerMap(Player p, MxInventory prevInv) {
        return (mxInv, e2) -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
                if (e2.getCurrentItem() == null) {
                    return;
                }
                ItemStack is = e2.getCurrentItem();
                ItemMeta im = is.getItemMeta();
                PersistentDataContainer container = im.getPersistentDataContainer();
                Optional<Map> optionalMap = MapManager.getInstance().getMapById(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), Map.MAP_ITEMMETA_TAG), PersistentDataType.STRING));
                p.closeInventory();
                if (optionalMap.isEmpty()) {
                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MAP));
                    return;
                }
                Map map = optionalMap.get();
                getTime(prevInv, p, map);

            });
        };
    }
}
