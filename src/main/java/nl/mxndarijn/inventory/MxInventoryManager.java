package nl.mxndarijn.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MxInventoryManager implements Listener {

    private static MxInventoryManager instance;
    public static MxInventoryManager getInstance() {
        return instance;
    }

    public static void createInstance(Server server, Plugin plugin) {
        if(instance == null) {
            instance = new MxInventoryManager(server, plugin);
        }
    }
    private HashMap<UUID, List<MxInventory>> inventories;
    private Server server;
    private Plugin plugin;
    private MxInventoryManager(Server server, Plugin plugin) {
        inventories = new HashMap<>();
        this.server = server;
        this.plugin = plugin;
        server.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() == null ||
            e.getClickedInventory() == null ||
            e.getClickedInventory().getTitle() == null) {
            return;
        }

        UUID uuid = e.getWhoClicked().getUniqueId();
        if(!inventories.containsKey(uuid)) {
            return;
        }
        inventories.get(uuid).forEach(mxInventory -> {
            if(mxInventory.getName().equals(e.getClickedInventory().getTitle())) {
                if(mxInventory.isCancelEvent()) {
                    e.setCancelled(true);
                }
                MxItemClicked clicked = mxInventory.getOnClickedMap().getOrDefault(e.getSlot(), null);
                if(clicked != null) {
                    clicked.OnItemClicked(e.getClickedInventory(), e);
                }
            }
        });
    }

    @EventHandler void InventoryClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if(!inventories.containsKey(uuid)) {
            return;
        }
        List<MxInventory> list = inventories.get(uuid);
        Iterator<MxInventory> i = list.iterator();
        while(i.hasNext()) {
            MxInventory mxInventory = i.next();
            if(mxInventory.getName().equals(e.getInventory().getTitle())) {
                if(!mxInventory.isCanBeClosed()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        e.getPlayer().openInventory(mxInventory.getInv());
                    }, 1);
                } else {
                    if(mxInventory.isDelete()) {
                        list.remove(mxInventory);
                        break;
                    }
                }
            }
        }
    }

    public void addInventory(UUID uuid, MxInventory inv) {
        if(inventories.containsKey(uuid)) {
            inventories.get(uuid).add(inv);
        } else {
            inventories.put(uuid, new ArrayList<>(Collections.singletonList(inv)));
        }
    }
    public void addAndOpenInventory(Player p, MxInventory inv) {
        if(p == null) {
            return;
        }
        addInventory(p.getUniqueId(), inv);
        p.openInventory(inv.getInv());

    }

    public void addAndOpenInventory(UUID uuid, MxInventory inv) {
        Player p = Bukkit.getPlayer(uuid);
        addAndOpenInventory(p, inv);
    }
}
