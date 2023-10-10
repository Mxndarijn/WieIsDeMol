package nl.mxndarijn.wieisdemol.managers;

import nl.mxndarijn.api.changeworld.ChangeWorldManager;
import nl.mxndarijn.api.changeworld.MxChangeWorld;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.items.Items;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnManager implements Listener {

    private static SpawnManager instance;
    private final World spawn;
    private final JavaPlugin plugin;
    private final Location spawnPoint;

    private SpawnManager() {
        plugin = JavaPlugin.getPlugin(WieIsDeMol.class);
        PluginManager manager = plugin.getServer().getPluginManager();
        this.spawnPoint = Functions.getSpawnLocation();
        this.spawn = spawnPoint.getWorld();

        manager.registerEvents(this, plugin);

        ChangeWorldManager.getInstance().addWorld(spawn.getUID(), new MxChangeWorld() {
            @Override
            public void enter(Player p, World w, PlayerChangedWorldEvent e) {
                p.closeInventory();
                p.getInventory().clear();
                p.setGameMode(GameMode.ADVENTURE);
                p.teleport(spawnPoint);
                p.getInventory().addItem(Items.GAMES_ITEM.getItemStack());

                // Add Items
            }

            @Override
            public void leave(Player p, World w, PlayerChangedWorldEvent e) {
                p.closeInventory();
                p.getInventory().clear();
            }
        });

    }

    public static SpawnManager getInstance() {
        if (instance == null)
            instance = new SpawnManager();
        return instance;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (GameWorldManager.getInstance().isPlayerInAGame(e.getPlayer().getUniqueId())) {
            return;
        }
        e.getPlayer().teleport(spawnPoint);
        if (e.getPlayer().getWorld() == spawn) {
            plugin.getServer().getPluginManager().callEvent(new PlayerChangedWorldEvent(e.getPlayer(), e.getPlayer().getWorld()));
        }
    }

    @EventHandler
    public void breakEvent(BlockBreakEvent e) {
        if (!e.getBlock().getWorld().equals(spawn))
            return;

        if (!e.getPlayer().hasPermission(Permissions.SPAWN_BLOCK_BREAK.getPermission()))
            e.setCancelled(true);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!e.getBlock().getWorld().equals(spawn))
            return;

        if (!e.getPlayer().hasPermission(Permissions.SPAWN_BLOCK_PLACE.getPermission()))
            e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!e.getEntity().getWorld().equals(spawn))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        if (!e.getPlayer().getWorld().equals(spawn))
            return;

        if (!e.getPlayer().hasPermission(Permissions.SPAWN_DROP_ITEM.getPermission()))
            e.setCancelled(true);
    }


    @EventHandler
    public void pickupItem(EntityPickupItemEvent e) {
        if (!e.getEntity().getWorld().equals(spawn))
            return;

        if (!e.getEntity().hasPermission(Permissions.SPAWN_PICKUP_ITEM.getPermission()))
            e.setCancelled(true);
    }

    @EventHandler
    public void inventoryChange(InventoryClickEvent e) {
        if (!e.getWhoClicked().getWorld().equals(spawn))
            return;

        if (e.getClickedInventory() == e.getWhoClicked().getInventory()) {
            if (!e.getWhoClicked().hasPermission(Permissions.SPAWN_CHANGE_INVENTORY.getPermission()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().equals(spawn))
            return;
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

}
