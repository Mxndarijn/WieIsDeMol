package nl.mxndarijn.wieisdemol.items.game.books;

import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.BookFailureAction;
import nl.mxndarijn.wieisdemol.data.BookFailurePlayersHolder;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.Random;
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

    public boolean canItemExecute(Player p, Integer entry, ItemStack is, BookFailurePlayersHolder holder) {


        ItemMeta im = is.getItemMeta();

        if (is.getAmount() > 1) {
            is.setAmount(is.getAmount() - 1);

            p.getInventory().setItem(entry, is);
        } else {
            p.getInventory().setItem(entry, new ItemStack(Material.AIR));
        }

        PersistentDataContainer container = im.getPersistentDataContainer();
        int data = container.getOrDefault(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "success-rating"), PersistentDataType.INTEGER, 100);

        if(checkChance(data)) {
            return true;
        }
        else {
            String dataFail = container.getOrDefault(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), "fail-action"), PersistentDataType.STRING, "");
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.BOOK_ACTION_FAILED));
            if(dataFail.isEmpty())
                return false;

            BookFailureAction.executeBookFailure(dataFail, holder);
            return false;
        }

    }

    public static boolean checkChance(int chance) {
        Random random = new Random();
        int generatedNumber = random.nextInt(101);

        return generatedNumber <= chance;
    }
}
