package nl.mxndarijn.wieisdemol.items.maps;

import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.MxInventoryIndex;
import nl.mxndarijn.api.inventory.MxInventoryManager;
import nl.mxndarijn.api.inventory.MxInventorySlots;
import nl.mxndarijn.api.inventory.MxItemClicked;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.mxworld.MxLocation;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ChatPrefix;
import nl.mxndarijn.wieisdemol.managers.MapManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.map.Map;
import nl.mxndarijn.wieisdemol.map.mapscript.MapParameter;
import nl.mxndarijn.wieisdemol.map.mapscript.MapParameterType;
import nl.mxndarijn.wieisdemol.map.mapscript.MapRoom;
import nl.mxndarijn.wieisdemol.map.mapscript.MapScript;
import nl.mxndarijn.wieisdemol.map.mapscript.manager.ScriptParameterManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MapScriptParameterToolItem extends MxItem {

    public MapScriptParameterToolItem(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Map> mapOptional = MapManager.getInstance().getMapByWorldUID(p.getWorld().getUID());
        if (mapOptional.isEmpty()) {
            return;
        }
        Map map = mapOptional.get();
        Optional<MapScript> msOpt = map.getOptionalMapScript();
        if (msOpt.isEmpty()) {
            MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_NO_MAPSCRIPT));
            return;
        }
        MapScript mapScript = msOpt.get();
        ScriptParameterManager spm = mapScript.getScriptParameterManager();

        ArrayList<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
        for (MapParameter<?> param : mapScript.getMapParameters()) {
            String id = param.getId();
            String desc = param.getDescription();
            String room = safeRoomTitle(param.getMapRoom());
            String currentVal = currentValueString(spm, param);
            ItemStack stack = MxDefaultItemStackBuilder.create(Material.PAPER, 1)
                    .setName("<gray>" + desc)
                    .addBlankLore()
                    .addLore("<gray>Kamer: <white>" + room)
                    .addLore("<gray>Id: <white>" + id)
                    .addLore("<gray>Type: <white>" + param.getType().name().toLowerCase())
                    .addBlankLore()
                    .addLore("<gray>Huidige waarde: <white>" + currentVal)
                    .addBlankLore()
                    .addLore("<yellow>Klik om te wijzigen.")
                    .build();

            list.add(new Pair<>(stack, (mxInv, click) -> {
                p.closeInventory();
                // Prompt messages
                String line1 = LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_PROMPT, Collections.singletonList(desc));
                String line2 = LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_FORMAT, Collections.singletonList(formatFor(param.getType())));
                MSG.msg(p, ChatPrefix.WIDM + line1 + "\n" + ChatPrefix.WIDM + line2);

                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                    boolean ok = applyInput(p, spm, param, message);
                    if (ok) {
                        spm.saveAll();
                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_UPDATED, Collections.singletonList(message)));
                    } else {
                        MSG.msg(p, ChatPrefix.WIDM + LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_INVALID_INPUT));
                    }
                });
            }));
        }

        MxInventoryManager.getInstance().addAndOpenInventory(p,
                MxListInventoryBuilder.create(LanguageManager.getInstance().getLanguageString(LanguageText.MAP_SCRIPT_PARAM_MENU_TITLE), MxInventorySlots.SIX_ROWS)
                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                        .setListItems(list)
                        .build());
    }

    private String safeRoomTitle(MapRoom room) {
        try {
            return room.getTitle();
        } catch (Throwable t) {
            return room.getClass().getSimpleName();
        }
    }

    private String currentValueString(ScriptParameterManager spm, MapParameter<?> param) {
        return switch (param.getType()) {
            case STRING -> spm.getString(param.getId()).map(Object::toString).orElse("<gray>niet ingesteld");
            case NUMBER -> spm.getNumber(param.getId()).map(Object::toString).orElse("<gray>niet ingesteld");
            case DECIMAL -> spm.getDecimal(param.getId()).map(Object::toString).orElse("<gray>niet ingesteld");
            case BOOLEAN -> spm.getBoolean(param.getId()).map(Object::toString).orElse("<gray>niet ingesteld");
            case LOCATION -> spm.getLocation(param.getId()).map(Object::toString).orElse("<gray>niet ingesteld");
        };
    }

    private String formatFor(MapParameterType type) {
        return switch (type) {
            case STRING -> "tekst (bijv: hallo wereld) of 'null' om leeg te maken";
            case NUMBER -> "geheel getal (bijv: 5)";
            case DECIMAL -> "kommagetal (bijv: 3.14)";
            case BOOLEAN -> "true/false (of ja/nee)";
            case LOCATION -> "'here' voor huidige locatie of 'x y z [yaw pitch]'";
        };
    }

    private boolean applyInput(Player p, ScriptParameterManager spm, MapParameter<?> param, String message) {
        String trimmed = message == null ? "" : message.trim();
        try {
            switch (param.getType()) {
                case STRING -> {
                    if (trimmed.equalsIgnoreCase("null")) {
                        spm.setString(param.getId(), null);
                    } else {
                        spm.setString(param.getId(), trimmed);
                    }
                    return true;
                }
                case NUMBER -> {
                    Integer val = Integer.parseInt(trimmed);
                    spm.setNumber(param.getId(), val);
                    return true;
                }
                case DECIMAL -> {
                    Double val = Double.parseDouble(trimmed.replace(",", "."));
                    spm.setDecimal(param.getId(), val);
                    return true;
                }
                case BOOLEAN -> {
                    String low = trimmed.toLowerCase(Locale.ROOT);
                    boolean val = low.equals("true") || low.equals("yes") || low.equals("ja") || low.equals("1");
                    if (!(low.equals("true") || low.equals("false") || low.equals("yes") || low.equals("no") || low.equals("ja") || low.equals("nee") || low.equals("1") || low.equals("0"))) {
                        return false;
                    }
                    spm.setBoolean(param.getId(), val);
                    return true;
                }
                case LOCATION -> {
                    if (trimmed.equalsIgnoreCase("here")) {
                        spm.setLocation(param.getId(), MxLocation.getFromLocation(p.getLocation()));
                        return true;
                    }
                    String[] parts = trimmed.split("\\s+");
                    if (parts.length == 3 || parts.length == 5) {
                        double x = Double.parseDouble(parts[0].replace(",", "."));
                        double y = Double.parseDouble(parts[1].replace(",", "."));
                        double z = Double.parseDouble(parts[2].replace(",", "."));
                        int yaw = 0;
                        int pitch = 0;
                        if (parts.length == 5) {
                            yaw = (int) Double.parseDouble(parts[3].replace(",", "."));
                            pitch = (int) Double.parseDouble(parts[4].replace(",", "."));
                        }
                        MxLocation mx = new MxLocation((int) Math.round(x), (int) Math.round(y), (int) Math.round(z), yaw, pitch);
                        spm.setLocation(param.getId(), mx);
                        return true;
                    }
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }
}
