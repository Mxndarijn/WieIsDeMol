package nl.mxndarijn.wieisdemol.items.game.books;

import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public abstract class Book extends MxItem {

    public ItemStack is;
    public Game game;

    public Book(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
        this.is = is;
    }

    public void sendBookMessageToAll(String message) {
        if (game != null)
            game.sendMessageToAll(message);

    }

    public void getGame(World w) {
        Optional<Game> g = GameWorldManager.getInstance().getGameByWorldUID(w.getUID());
        g.ifPresent(value -> game = value);
    }

    public Optional<GamePlayer> getGamePlayer(UUID uuid) {
        return game.getGamePlayerOfPlayer(uuid);
    }

    public boolean isItemTheSame(ItemStack item) {
        if (item.getType() == Material.AIR || item.getItemMeta() == null)
            return false;
        if (item.getType() == is.getType()) {
            if (is.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName() && Functions.convertComponentToString(item.getItemMeta().displayName()).equalsIgnoreCase(Functions.convertComponentToString(is.getItemMeta().displayName()))) {
                return true;
            } else {
                return !is.getItemMeta().hasDisplayName();
            }
        }
        return false;
    }
}
