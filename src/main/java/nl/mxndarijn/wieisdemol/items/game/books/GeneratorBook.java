package nl.mxndarijn.wieisdemol.items.game.books;

import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.AvailablePerson;
import nl.mxndarijn.wieisdemol.data.BookFailurePlayersHolder;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.data.Role;
import nl.mxndarijn.wieisdemol.game.GamePlayer;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.items.maps.MapGeneratorBook;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorBook extends Book {
    public GeneratorBook(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        getGame(p.getWorld());
        if (game == null)
            return;

        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(p.getUniqueId());

        if (optionalGamePlayer.isPresent()) {
            if (game.getGameInfo().getStatus() != UpcomingGameStatus.PLAYING)
                return;
            GamePlayer gp = optionalGamePlayer.get();
            if (!gp.isAlive())
                return;

            for (Map.Entry<Integer, ? extends ItemStack> entry : p.getInventory().all(is.getType()).entrySet()) {
                Integer key = entry.getKey();
                ItemStack value = entry.getValue();
                ItemStack realItem = value.clone();
                if (isItemTheSame(value)) {
                    if(!canItemExecute(p, key, value, BookFailurePlayersHolder.create().setData(AvailablePerson.EXECUTOR, p)))
                        return;
                    ItemMeta im = realItem.getItemMeta();
                    PersistentDataContainer container = im.getPersistentDataContainer();
                    String json = container.getOrDefault(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), MapGeneratorBook.containerKey), PersistentDataType.STRING, "{}");
                    List<ItemStack> items = getAllItems(json);
                    if(items.isEmpty()) {
                        game.sendMessageToHosts(ChatPrefix.WIDM + "<red>Generator niet goed geconfigured! Zitten geen items in.");
                        return;
                    }

                    Random random = new Random();
                    ItemStack randomItem = items.get(random.nextInt(items.size()));

                    p.getInventory().addItem(randomItem);

                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_YOU_RECEIVED_ITEM));
                    break;
                }
            }

        }
    }

    private List<ItemStack> getAllItems(String data) {
        YamlConfiguration ob = convertString(data);
        if(ob == null) {
            ob = new YamlConfiguration();
        }
        ArrayList<ItemStack> items = new ArrayList<>();
        if(ob.contains("items")) {
            ob.getList("items").forEach(itemStackAsObject -> {
                ItemStack itemStack =  (ItemStack) itemStackAsObject;
                items.add(itemStack);
            });
        }

        return items;
    }

    private YamlConfiguration convertString(String s) {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(s);
        } catch (InvalidConfigurationException e) {
            return null;
        }

        return configuration;
    }
}
