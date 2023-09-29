package nl.mxndarijn.wieisdemol.game;

import nl.mxndarijn.api.changeworld.ChangeWorldManager;
import nl.mxndarijn.api.changeworld.MxChangeWorld;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.mxscoreboard.MxSupplierScoreBoard;
import nl.mxndarijn.api.mxworld.MxAtlas;
import nl.mxndarijn.api.mxworld.MxWorld;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.ScoreBoard;
import nl.mxndarijn.wieisdemol.data.SpecialDirectories;
import nl.mxndarijn.wieisdemol.game.events.GameEvent;
import nl.mxndarijn.wieisdemol.game.events.GameFreezeEvents;
import nl.mxndarijn.wieisdemol.game.events.GamePlayingEvents;
import nl.mxndarijn.wieisdemol.game.events.GamePreStartEvents;
import nl.mxndarijn.wieisdemol.items.Items;
import nl.mxndarijn.wieisdemol.managers.InteractionManager;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.ScoreBoardManager;
import nl.mxndarijn.wieisdemol.managers.chests.ChestManager;
import nl.mxndarijn.wieisdemol.managers.doors.DoorManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.shulkers.ShulkerManager;
import nl.mxndarijn.wieisdemol.managers.warps.WarpManager;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import nl.mxndarijn.wieisdemol.map.Map;
import nl.mxndarijn.wieisdemol.map.MapConfig;
import nl.mxndarijn.wieisdemol.map.mapplayer.MapPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Game {

    private GameInfo gameInfo;
    private UUID mainHost;
    private Optional<MxWorld> mxWorld;

    private WarpManager warpManager;
    private ChestManager chestManager;
    private ShulkerManager shulkerManager;
    private DoorManager doorManager;
    private InteractionManager interactionManager;
    private File directory;

    private ArrayList<UUID> hosts;

    private MapConfig config;

    private List<GamePlayer> colors;

    private MxSupplierScoreBoard hostScoreboard;

    private boolean firstStart = false;

    private JavaPlugin plugin;
    public Game(UUID mainHost, GameInfo gameInfo, MapConfig mapConfig, MxWorld mxWorld) {
        this.gameInfo = gameInfo;
        this.mainHost = mainHost;
        this.mxWorld = Optional.of(mxWorld);
        this.config = mapConfig;
        this.directory = mxWorld.getDir();

        this.hosts = new ArrayList<>();

        this.warpManager = new WarpManager(new File(getDirectory(), "warps.yml"));
        this.chestManager = new ChestManager(new File(getDirectory(), "chests.yml"));
        this.shulkerManager = new ShulkerManager(new File(getDirectory(), "shulkers.yml"));
        this.doorManager = new DoorManager(new File(getDirectory(), "doors.yml"));
        this.interactionManager = new InteractionManager(new File(getDirectory(), "interactions.yml"));

        this.plugin = JavaPlugin.getPlugin(WieIsDeMol.class);

        this.colors = new ArrayList<>();
        mapConfig.getColors().forEach(mapPlayer -> {
            colors.add(new GamePlayer(mapPlayer));
        });

        this.hostScoreboard = new MxSupplierScoreBoard(plugin, () -> {
            return ScoreBoard.GAME_HOST.getTitle(new HashMap<>() {{
                put("%%map_name%%", mapConfig.getPresetConfig().getName());
            }});
        }, () -> {
            return ScoreBoard.GAME_HOST.getLines(new HashMap<>() {{
                put("%%map_name%%", mapConfig.getPresetConfig().getName());
                put("%%game_status%%", gameInfo.getStatus().getStatus());
                put("%%game_time%%", "00:00");
                put("%%players_alive%%", "0");
                put("%%mollen_alive%%", "0");
                put("%%ego_alive%%", "0");

            }});
        });
        this.hostScoreboard.setUpdateTimer(10);

        GameWorldManager.getInstance().addGame(this);
    }

    private File getDirectory() {
        return directory;
    }


    public static Optional<Game> createGameFromGameInfo(UUID mainHost, GameInfo gameInfo) {
        Optional<Map> map = MapManager.getInstance().getMapById(gameInfo.getMapId());
        if(map.isEmpty() || map.get().getMxWorld().isEmpty()) {
            return Optional.empty();
        }

        File newDir = new File(SpecialDirectories.GAMES_WORLDS.getDirectory() + "");
        Optional<MxWorld> optionalWorld = MxAtlas.getInstance().duplicateMxWorld(map.get().getMxWorld().get(), newDir);

        if(optionalWorld.isEmpty()) {
            return Optional.empty();
        }

        Game g = new Game(mainHost, gameInfo, map.get().getMapConfig(), optionalWorld.get());

        return Optional.of(g);
    }

    public CompletableFuture<Boolean> loadWorld() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(this.mxWorld.isEmpty()) {
            future.complete(false);
            return future;
        }
        if(this.mxWorld.get().isLoaded()) {
            future.complete(false);
            return future;
        }
        MxAtlas.getInstance().loadMxWorld(this.mxWorld.get()).thenAccept(loaded -> {
            future.complete(loaded);
            if(loaded) {
                registerEvents();
                updateChestAttachments();
            }
        });

        ChangeWorldManager.getInstance().addWorld(this.mxWorld.get().getWorldUID(), new MxChangeWorld() {
            @Override
            public void enter(Player p, World w, PlayerChangedWorldEvent e) {
            }

            @Override
            public void leave(Player p, World w, PlayerChangedWorldEvent e) {
                ScoreBoardManager.getInstance().removePlayerScoreboard(p.getUniqueId(), hostScoreboard);
            }
        });
        return future;
    }

    private List<GameEvent> events;
    public void registerEvents() {
        unregisterEvents();
        events = new ArrayList<>(Arrays.asList(
                new GamePreStartEvents(this, plugin),
                new GameFreezeEvents(this, plugin),
                new GamePlayingEvents(this, plugin)
        ));
    }

    public void unregisterEvents() {
        if(events == null)
            return;
        events.forEach(HandlerList::unregisterAll);
    }

    public CompletableFuture<Boolean> unloadWorld() {
        unregisterEvents();

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(WieIsDeMol.class), () -> {
            if (this.mxWorld.isEmpty()) {
                future.complete(true);
                return;
            }
            if (!this.mxWorld.get().isLoaded()) {
                future.complete(true);
                return;
            }
            MxAtlas.getInstance().unloadMxWorld(this.mxWorld.get(), true);
            future.complete(true);
        });
        return future;
    }
    public void addHost(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p != null) {
            hosts.add(uuid);
            loadWorld().thenAccept(loaded -> {
                if(loaded) {
                    World w = Bukkit.getWorld(mxWorld.get().getWorldUID());
                    p.teleport(w.getSpawnLocation());
                    ScoreBoardManager.getInstance().setPlayerScoreboard(uuid, hostScoreboard);
                    p.setGameMode(GameMode.CREATIVE);
                    p.sendMessage(ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAME_YOU_ARE_NOW_HOST));
                    p.getInventory().clear();
                    p.getInventory().addItem(Items.PLAYER_MANAGEMENT_ITEM.getItemStack());
                    p.getInventory().addItem(Items.HOST_TOOL.getItemStack());
                    p.getInventory().addItem(Items.GAME_CHEST_TOOL.getItemStack());
                    p.getInventory().addItem(Items.GAME_SHULKER_TOOL.getItemStack());
                    p.getInventory().addItem(Items.GAME_DOOR_ITEM.getItemStack());
                    p.getInventory().addItem(Items.VANISH_ITEM.getItemStack());
                }
            });
        }
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public UUID getMainHost() {
        return mainHost;
    }

    public Optional<MxWorld> getMxWorld() {
        return mxWorld;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public ShulkerManager getShulkerManager() {
        return shulkerManager;
    }

    public DoorManager getDoorManager() {
        return doorManager;
    }

    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public ArrayList<UUID> getHosts() {
        return hosts;
    }

    public MapConfig getConfig() {
        return config;
    }

    public MxSupplierScoreBoard getHostScoreboard() {
        return hostScoreboard;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public List<GamePlayer> getColors() {
        return colors;
    }

    public boolean addPlayer(UUID playerUUID, GamePlayer gamePlayer) {
        //TODO Change Inventory
        Player p = Bukkit.getPlayer(playerUUID);
        if(p == null || mxWorld.isEmpty())
            return false;

        if(gamePlayer.getPlayer().isPresent()) {
            removePlayer(gamePlayer.getPlayer().get());
        }

        gamePlayer.setPlayingPlayer(playerUUID);
        MapPlayer mp = gamePlayer.getMapPlayer();

        p.teleport(mp.getLocation().getLocation(Bukkit.getWorld(mxWorld.get().getWorldUID())));
        p.setHealth(20);
        p.setFoodLevel(20);
        sendMessageToAll(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_PLAYER_JOINED, new ArrayList<>(Arrays.asList(p.getName(), mp.getColor().getDisplayName()))));
        //Add Scoreboard

        return true;
    }

    public Optional<GamePlayer> getGamePlayerOfPlayer(UUID uuid) {
        for (GamePlayer color : colors) {
            if(color.getPlayer().isPresent() && color.getPlayer().get().equals(uuid)) {
                return Optional.of(color);
            }
        }
        return Optional.empty();
    }

    public boolean removePlayer(UUID playerUUID) {
        //TODO replace player
        Optional<GamePlayer> player = getGamePlayerOfPlayer(playerUUID);
        if(player.isEmpty())
            return false;

        GamePlayer gamePlayer = player.get();
        gamePlayer.setPlayingPlayer(null);

        Player p = Bukkit.getPlayer(playerUUID);
        if(p != null) {
            p.teleport(Functions.getSpawnLocation());
            sendMessageToAll(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_PLAYER_LEAVED, new ArrayList<>(Arrays.asList(p.getName(), gamePlayer.getMapPlayer().getColor().getDisplayName()))));
        }
        sendMessageToAll(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_PLAYER_LEAVED, new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayer(playerUUID).getName(), gamePlayer.getMapPlayer().getColor().getDisplayName()))));
        //Add Scoreboard

        return true;
    }

    public void sendMessageToAll(String message) {
        sendMessageToHosts(message);
        sendMessageToPlayers(message);
        sendMessageToSpectators(message);

    }

    public void sendMessageToHosts(String message) {
        hosts.forEach(host -> {
            Player p = Bukkit.getPlayer(host);
            if(p != null) {
                p.sendMessage(message);
            }
        });
    }

    public void sendMessageToSpectators(String message) {
        //TODO
    }

    public void sendMessageToPlayers(String message) {
        colors.forEach(color -> {
            if(color.getPlayer().isPresent()) {
                Player p = Bukkit.getPlayer(color.getPlayer().get());
                if(p != null) {
                    p.sendMessage(message);
                }
            }
        });
    }

    public void setGameStatus(UpcomingGameStatus upcomingGameStatus) {
        getGameInfo().setStatus(upcomingGameStatus);
        if(upcomingGameStatus == UpcomingGameStatus.PLAYING && !firstStart) {
            firstStart = true;
            chestManager.onGameStart(this);
        }
        sendMessageToAll(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_STATUS_CHANGED, Collections.singletonList(upcomingGameStatus.getStatus())));
    }

    private BukkitTask chestAttachmentUpdater;
    public void updateChestAttachments() {
        if(chestAttachmentUpdater != null)
            return;
        AtomicLong lastUpdateTime = new AtomicLong(System.currentTimeMillis());
        chestAttachmentUpdater = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(this.mxWorld.isEmpty())
                chestAttachmentUpdater.cancel();
            if(gameInfo.getStatus() != UpcomingGameStatus.PLAYING)
                return;
            chestManager.getChests().forEach(chestInformation -> {
                chestInformation.getChestAttachmentList().forEach(chestAttachment -> {
                    long l = System.currentTimeMillis();
                    chestAttachment.onGameUpdate(l - lastUpdateTime.get());
                    lastUpdateTime.set(l);
                });
            });
        }, 0L, 20L);
    }
}