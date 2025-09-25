package nl.mxndarijn.wieisdemol.items.spawn;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.ConfigFiles;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.GameManager;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import nl.mxndarijn.wieisdemol.map.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GamesItem extends MxItem {


    public GamesItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        GameManager.getInstance().getUpcomingGameList().forEach(upcomingGame -> {
            list.add(new Pair<>(
                    upcomingGame.getItemStack(p),
                    (mxInv, e14) -> {
                        if (!GameManager.getInstance().getUpcomingGameList().contains(upcomingGame)) {
                            // Kon game niet vinden (is verwijderdt)
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_COULD_NOT_FIND_GAME));
                            p.closeInventory();
                            return;
                        }
                        Duration timeBetween = Duration.between(LocalDateTime.now(), upcomingGame.getTime());
                        int hours = ConfigFiles.MAIN_CONFIG.getFileConfiguration().getInt("time-before-queue-is-open-in-hours");
                        if (!e14.isShiftClick() && Math.abs(timeBetween.toMinutes()) > hours * 60L) {
                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_TO_EARLY_TO_JOIN, Collections.singletonList(hours + "")));
                            return;
                        }
                        if (upcomingGame.getStatus() == UpcomingGameStatus.PLAYING) {
                            // Spectate
                            Optional<Game> game = GameWorldManager.getInstance().getGameByGameInfo(upcomingGame);
                            game.ifPresent(value -> value.addSpectator(p.getUniqueId()));
                            return;
                        }
                        if (upcomingGame.getStatus().isCanJoinQueue() && !e14.isShiftClick()) {
                            if (upcomingGame.getQueue().contains(p.getUniqueId())) {
                                upcomingGame.getQueue().remove(p.getUniqueId());
                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_LEFT_QUEUE));
                                p.closeInventory();
                            } else {
                                if (GameManager.getInstance().getUpcomingGameList().stream().anyMatch(gameInfo -> gameInfo.getQueue().contains(p.getUniqueId()))) {
                                    // Message leave to join other
                                    MSG.msg(p, "§cLeave de huidige queue om een andere game te joinen.");
                                } else {
                                    upcomingGame.getQueue().add(p.getUniqueId());
                                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ENTERED_QUEUE));
                                    p.closeInventory();
                                }
                            }
                            return;
                        }
                        if (e14.isLeftClick() && e14.isShiftClick()) {
                            if (p.hasPermission(Permissions.ITEM_GAMES_MANAGE_OTHER_GAMES.getPermission()) || upcomingGame.getHost().equals(p.getUniqueId())) {
                                // Manage game
                                if (upcomingGame.getStatus() == UpcomingGameStatus.WAITING) {
                                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Beheer Game", MxInventorySlots.THREE_ROWS)
                                            .setPrevious(mxInv)
                                            .setItem(MxDefaultItemStackBuilder.create(Material.FIREWORK_ROCKET)
                                                            .setName("<green>Start")
                                                            .addBlankLore()
                                                            .addLore("<yellow>Klik hier om de game te starten!")
                                                            .build(),
                                                    13,
                                                    (mxInv1, e12) -> {
                                                        Optional<Game> gameOptional = Game.createGameFromGameInfo(p.getUniqueId(), upcomingGame);
                                                        if (gameOptional.isPresent()) {
                                                            gameOptional.get().addHost(p.getUniqueId());
                                                            upcomingGame.setStatus(UpcomingGameStatus.CHOOSING_PLAYERS);

                                                        } else {
                                                            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_COULD_NOT_CREATE_GAME));
                                                        }
                                                        p.closeInventory();

                                                    }
                                            )
                                            .setItem(MxDefaultItemStackBuilder.create(Material.RED_CONCRETE)
                                                            .setName("<red>Verwijder Game")
                                                            .addBlankLore()
                                                            .addLore("<yellow>Klik hier om de game te verwijderen.")
                                                            .build(), 26,
                                                    (mxInv12, e13) -> {
                                                        GameManager.getInstance().removeUpcomingGame(upcomingGame);
                                                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_GAME_DELETED));
                                                        p.closeInventory();
                                                    })
                                            .build());
                                }
                            }
                        }


                    }
            ));
        });
        MxListInventoryBuilder builder = MxListInventoryBuilder.create("<gray>Games", MxInventorySlots.THREE_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                .setListItems(list);
        if (p.hasPermission(Permissions.ITEM_GAMES_CREATE_GAME.getPermission())) {
            builder.setItem(MxSkullItemStackBuilder.create(1)
                            .setName("<gray>Plan Game")
                            .setSkinFromHeadsData("wooden-plus")
                            .addBlankLore()
                            .addLore("<gray>Plan een nieuwe game.")
                            .addBlankLore()
                            .addLore("<yellow>Klik hier om een nieuwe game te plannen.")
                            .build(),
                    22,
                    (mxInv, e1) -> {
                        getMap(mxInv, p);
                    }
            );
        }

        MxInventoryManager.getInstance().addAndOpenInventory(p, builder.build());
    }

    private void getMap(MxInventory prevInv, Player p) {
        MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Map Selecteren", MxInventorySlots.THREE_ROWS)
                .setPrevious(prevInv)
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                                .setName("<green>Eigen Mappen")
                                .addLore("<gray>Bekijk je eigen mappen")
                                .addLore(" ")
                                .addLore("<yellow>Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(),
                        12,
                        (mxInv, e) -> {
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getOwner().equals(p.getUniqueId())).toList();

                            MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p, prevInv);

                            ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Eigen Mappen", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(prevInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName("<gray>Info")
                                            .addLore(" ")
                                            .addLore("<yellow>Klik op een map om deze te selecteren.")
                                            .build(), 49, null)
                                    .build());

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOKSHELF)
                                .setName("<green>Gedeelde mappen")
                                .addLore("<gray>Bekijk alle mappen die met je gedeeld zijn.")
                                .addBlankLore()
                                .addLore("<yellow>Klik om te bekijken")
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                                .build(), 14,
                        (mxInv, e) -> {
                            List<Map> playerMaps = MapManager.getInstance().getAllMaps().stream().filter(m -> m.getMapConfig().getSharedPlayers().contains(p.getUniqueId())).toList();

                            MxItemClicked clickedOnPlayerMap = getClickedOnPlayerMap(p, prevInv);

                            ArrayList<Pair<ItemStack, MxItemClicked>> list = playerMaps.stream().map(map -> new Pair<>(map.getItemStack(), clickedOnPlayerMap)).collect(Collectors.toCollection(ArrayList::new));
                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Gedeelde Mappen", MxInventorySlots.SIX_ROWS)
                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                    .setPreviousItemStackSlot(46)
                                    .setPrevious(prevInv)
                                    .setListItems(list)
                                    .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                            .setName("<gray>Info")
                                            .addLore(" ")
                                            .addLore("<yellow>Klik op een map om deze te selecteren.")
                                            .build(), 49, null)
                                    .build());

                        })
                .build())
        ;
    }

    private void getTime(MxInventory prevInv, Player p, Map map) {
        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 16; i++) {
            LocalDate date = now.plusDays(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.forLanguageTag("nl-NL"));
            String formattedDate = date.format(formatter);
            MxSkullItemStackBuilder builder = MxSkullItemStackBuilder.create(1)
                    .setSkinFromHeadsData("clock")
                    .setName("<gray>" + formattedDate)
                    .addBlankLore()
                    .addLore("<gray>Datum: " + (i != 0 ? (i == 1 ? "Morgen" : "Over " + i + " dagen.") : "Vandaag"))
                    .addBlankLore()
                    .addLore("<yellow>Klik om de game te hosten op " + formattedDate + ".");
            if (i == 0)
                formattedDate += " (Vandaag)";
            if (i == 1)
                formattedDate += " (Morgen)";

            String finalFormattedDate = formattedDate;
            list.add(new Pair<>(builder.build(), (mxInv, e) -> {
                p.closeInventory();
                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_DATE_SELECTED, Collections.singletonList(finalFormattedDate)));
                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_ENTER_TIME));
                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                    String[] patterns = {"H:mm", "HH:mm"};
                    DateTimeFormatter formatter1 = null;
                    LocalTime t = null;
                    for (String pattern : patterns) {
                        try {
                            formatter1 = DateTimeFormatter.ofPattern(pattern);
                            t = LocalTime.parse(message, formatter1);
                            LocalDateTime localDateTime = LocalDateTime.of(date, t);

                            if (localDateTime.isAfter(LocalDateTime.now())) {
                                GameManager.getInstance().addUpcomingGame(p.getUniqueId(), map, localDateTime);
                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_UPCOMING_GAME_ADDED));
                            } else {
                                MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_UPCOMING_GAME_TIME_IS_PAST));
                            }

                            break; // Stop het zoeken zodra parsing gelukt is
                        } catch (DateTimeParseException ex) {
                            // Ga door met volgend patroon als parsing niet lukt
                        }
                    }

                    if (t == null) {
                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAMES_ITEM_COULD_NOT_PARSE_TIME));
                    }
                });

            }));
        }
        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Datum selecteren", MxInventorySlots.THREE_ROWS)
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
                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.COMMAND_MAPS_COULD_NOT_FIND_MAP));
                    return;
                }
                Map map = optionalMap.get();
                getTime(prevInv, p, map);

            });
        };
    }
}
