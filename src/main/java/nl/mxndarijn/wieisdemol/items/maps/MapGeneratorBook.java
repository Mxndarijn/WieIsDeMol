package nl.mxndarijn.wieisdemol.items.maps;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapGeneratorBook extends MxItem {
    public static final String containerKey = "generator";

    public MapGeneratorBook(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());

        if (mapOptional.isEmpty()) {
            return;
        }

        ItemStack is = e.getItem();
        ItemMeta im = is.getItemMeta();
        PersistentDataContainer container = im.getPersistentDataContainer();
        String json = container.getOrDefault(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), containerKey), PersistentDataType.STRING, "{}");


        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        List<ItemStack> items = getAllItems(json);
        items.forEach(itemStack -> {
            ItemStack i = itemStack.clone();
            ItemMeta imm = i.getItemMeta();
            List<Component> lore = imm.hasLore() ? imm.lore() : new ArrayList<>();
            lore.add(MiniMessage.miniMessage().deserialize("<!i>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!i>" + "<yellow>Klik hier om dit item te verwijderen."));
            imm.lore(lore);
            i.setItemMeta(imm);
                list.add(new Pair<>(
                        i,
                        (mxInv, e1) -> {
                            items.remove(itemStack);
                            saveItems(items, is);
                            p.closeInventory();
                            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MAP_GENERATOR_ITEM_REMOVED));
                            //TODO delete
                        }
                ));
            });


        MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Generator Configurator", MxInventorySlots.SIX_ROWS)
                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                .setListItems(list)
                        .setItem(MxSkullItemStackBuilder.create(1)
                                .setSkinFromHeadsData("wooden-plus")
                                .setName("<gray>Item toevoegen")
                                .addBlankLore()
                                .addLore("<yellow>Klik hier om een item toe te voegen.")
                                .build(), 52, (mxInv, e12) -> {
                            ArrayList<Pair<ItemStack, MxItemClicked>> list1 = new ArrayList<>();
                            for (ItemStack content : p.getInventory().getContents()) {
                                if(content == null)
                                    continue;

                                ItemStack i = content.clone();
                                ItemMeta imm = i.getItemMeta();
                                List<Component> lore = imm.hasLore() ? imm.lore() : new ArrayList<>();
                                lore.add(MiniMessage.miniMessage().deserialize("<!i>"));
                                lore.add(MiniMessage.miniMessage().deserialize("<!i>" + "<yellow>Klik hier om dit item toe te voegen."));
                                imm.lore(lore);
                                i.setItemMeta(imm);
                                list1.add(new Pair<>(i, (mxInv1, e13) -> {
                                    items.add(content);
                                    saveItems(items, is);
                                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MAP_GENERATOR_ITEM_ADDED));

                                }));
                            }
                                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Generator add item", MxInventorySlots.SIX_ROWS)
                                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                                    .setListItems(list1)
                                            .build()
                                    );
                        })
                .build());

    }

    private void saveItems(List<ItemStack> list, ItemStack is)
    {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("items", list);
        String data = configuration.saveToString();
        ItemMeta im = is.getItemMeta();
        PersistentDataContainer container = im.getPersistentDataContainer();
        container.set(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), containerKey), PersistentDataType.STRING, data);
        is.setItemMeta(im);
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
