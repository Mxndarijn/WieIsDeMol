package nl.mxndarijn.wieisdemol.map.mapscript.manager;

import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.events.GameEvent;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import nl.mxndarijn.wieisdemol.map.mapscript.RedstoneTrigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneTriggerManager extends GameEvent {

    private final MapScript mapScript;

    public RedstoneTriggerManager(Game g, JavaPlugin plugin, MapScript mapScript) {
        super(g, plugin);
        this.mapScript = mapScript;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block == null || !validateWorld(block.getWorld())) return;

            Material type = block.getType();
            if (type == Material.LEVER || isButton(type)) {
                boolean willBePowered = willToggleToPowered(block);
                if (willBePowered) {
                    dispatch(block.getLocation(), e, e.getPlayer(), true);
                } else {
                    dispatch(block.getLocation(), e, e.getPlayer(), false);
                }
            }
        } else if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block == null || !validateWorld(block.getWorld())) return;

            Material type = block.getType();
            if (isPressurePlate(type)) {
                // Stepping on a plate triggers activation; deactivation will be captured by BlockRedstoneEvent
                dispatch(block.getLocation(), e, e.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        Block block = e.getBlock();
        if (!validateWorld(block.getWorld())) return;

        Material type = block.getType();
        if (!(type == Material.LEVER || isButton(type) || isPressurePlate(type))) return;

        int oldP = e.getOldCurrent();
        int newP = e.getNewCurrent();
        if (oldP < newP && newP > 0) {
            dispatch(block.getLocation(), e, null, true);
        } else if (newP == 0 && oldP > 0) {
            dispatch(block.getLocation(), e, null, false);
        }
    }

    private void dispatch(Location loc, org.bukkit.event.Event e, @Nullable Player player, boolean activate) {
        List<RedstoneTrigger<?>> triggers = mapScript.getRedstoneTriggers();
        for (RedstoneTrigger<?> trigger : triggers) {
            for (Location tLoc : trigger.getTriggers()) {
                if (matches(loc, tLoc)) {
                    if (activate) trigger.onActivate(loc, e, player); else trigger.onDeactivate(loc, e, player);
                    break; // matched one location for this trigger
                }
            }
        }
    }

    private boolean matches(Location a, Location b) {
        // Compare by block coordinates within the same world as the game world
        if (a == null || b == null) return false;
        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    private boolean isButton(Material m) {
        return m == Material.STONE_BUTTON || m == Material.OAK_BUTTON || m == Material.SPRUCE_BUTTON ||
                m == Material.BIRCH_BUTTON || m == Material.JUNGLE_BUTTON || m == Material.ACACIA_BUTTON ||
                m == Material.DARK_OAK_BUTTON || m == Material.MANGROVE_BUTTON || m == Material.CHERRY_BUTTON ||
                m == Material.BAMBOO_BUTTON || m == Material.CRIMSON_BUTTON || m == Material.WARPED_BUTTON ||
                m == Material.POLISHED_BLACKSTONE_BUTTON;
    }

    private boolean isPressurePlate(Material m) {
        return m == Material.STONE_PRESSURE_PLATE || m == Material.LIGHT_WEIGHTED_PRESSURE_PLATE ||
                m == Material.HEAVY_WEIGHTED_PRESSURE_PLATE || m == Material.OAK_PRESSURE_PLATE ||
                m == Material.SPRUCE_PRESSURE_PLATE || m == Material.BIRCH_PRESSURE_PLATE ||
                m == Material.JUNGLE_PRESSURE_PLATE || m == Material.ACACIA_PRESSURE_PLATE ||
                m == Material.DARK_OAK_PRESSURE_PLATE || m == Material.MANGROVE_PRESSURE_PLATE ||
                m == Material.CHERRY_PRESSURE_PLATE || m == Material.BAMBOO_PRESSURE_PLATE ||
                m == Material.CRIMSON_PRESSURE_PLATE || m == Material.WARPED_PRESSURE_PLATE ||
                m == Material.POLISHED_BLACKSTONE_PRESSURE_PLATE;
    }

    private boolean willToggleToPowered(Block block) {
        // For levers/buttons, PlayerInteractEvent fires before toggle; infer next state
        try {
            if (block.getBlockData() instanceof Switch sw) {
                return !sw.isPowered();
            }
            if (block.getBlockData() instanceof Powerable pw) {
                return !pw.isPowered();
            }
        } catch (Throwable ignored) {}
        return true; // default assume activation
    }
}
