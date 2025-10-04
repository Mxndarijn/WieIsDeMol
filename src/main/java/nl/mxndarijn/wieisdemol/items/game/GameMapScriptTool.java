package nl.mxndarijn.wieisdemol.items.game;

import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import nl.mxndarijn.wieisdemol.map.mapscript.MapAction;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class GameMapScriptTool extends MxItem {

    public GameMapScriptTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Game> optGame = GameWorldManager.getInstance().getGameByWorldUID(p.getWorld().getUID());
        if (optGame.isEmpty()) return;
        Game game = optGame.get();

        // Only hosts can use this tool
        if (!game.getHosts().contains(p.getUniqueId())) return;

        Optional<MapScript> optScript = game.getMapScript();
        if (optScript.isEmpty()) return;
        MapScript script = optScript.get();

        ArrayList<Pair<ItemStack, nl.mxndarijn.api.inventory.MxItemClicked>> list = new ArrayList<>();
        for (MapAction<?> action : script.getMapActions()) {
            ItemStack display = action.createItemStack();
            list.add(new Pair<>(display, (mxInv, clickEvent) -> {
                try {
                    action.onActivate(clickEvent, p);
                    MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MAPSCRIPT_ACTION_EXECUTED));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    p.closeInventory();
                }
            }));
        }

        MxInventoryManager.getInstance().addAndOpenInventory(p,
                MxListInventoryBuilder.create("<gray>Script Acties", MxInventorySlots.SIX_ROWS)
                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                        .setListItems(list)
                        .build()
        );
    }
}
