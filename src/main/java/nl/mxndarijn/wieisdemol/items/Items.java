package nl.mxndarijn.wieisdemol.items;

import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.mxitem.MxItem;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.data.ItemTag;
import nl.mxndarijn.wieisdemol.items.game.*;
import nl.mxndarijn.wieisdemol.items.game.books.*;
import nl.mxndarijn.wieisdemol.items.game.spectate.LeaveGameItem;
import nl.mxndarijn.wieisdemol.items.game.spectate.TeleportItem;
import nl.mxndarijn.wieisdemol.items.maps.*;
import nl.mxndarijn.wieisdemol.items.presets.ChestConfigureTool;
import nl.mxndarijn.wieisdemol.items.presets.DoorConfigureTool;
import nl.mxndarijn.wieisdemol.items.presets.PresetConfigureTool;
import nl.mxndarijn.wieisdemol.items.presets.ShulkerConfigureTool;
import nl.mxndarijn.wieisdemol.items.spawn.GamesItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public enum Items {

    // Preset Items
    PRESET_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                    .setName("<gray>Preset Configure-Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je instellingen in een preset aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            PresetConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),

    CHEST_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.STICK, 1)
                    .setName("<gray>Chest Configure-Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je kisten in een preset aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            ChestConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    SHULKER_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.SHULKER_SHELL, 1)
                    .setName("<gray>Shulker Configure-Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je shulkers in een preset aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            ShulkerConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    DOOR_CONFIGURE_TOOL(
            MxDefaultItemStackBuilder.create(Material.BRICK, 1)
                    .setName("<gray>Door Configure-Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je deuren in een preset aanpassen.")
                    .addLore("<yellow>Gebruik in de lucht om deuren te maken.")
                    .addLore("<yellow>Gebruik tegen een block om het block toe te voegen aan een deur.")
                    .build(),
            p -> {
                return true;
            },
            false,
            DoorConfigureTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),

    // Map items

    CHEST_TOOL(
            MxDefaultItemStackBuilder.create(Material.STICK, 1)
                    .setName("<gray>Chest Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je kisten op afstand openen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            MapChestItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    GAME_CHEST_TOOL(
            MxDefaultItemStackBuilder.create(Material.STICK, 1)
                    .setName("<gray>Chest Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je kisten op afstand openen tijdens een game.")
                    .addLore("<yellow>Daarnaast merkt niemand dat je de kist opent.")
                    .build(),
            p -> {
                return true;
            },
            false,
            GameChestItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    SHULKER_TOOL(
            MxDefaultItemStackBuilder.create(Material.SHULKER_SHELL, 1)
                    .setName("<gray>Shulker Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je shulkers op afstand openen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            MapShulkerItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    GAME_SHULKER_TOOL(
            MxDefaultItemStackBuilder.create(Material.SHULKER_SHELL, 1)
                    .setName("<gray>Shulker Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je shulkers op afstand openen tijdens een game.")
                    .build(),
            p -> {
                return true;
            },
            false,
            GameShulkerItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    DOOR_ITEM(
            MxDefaultItemStackBuilder.create(Material.BRICK, 1)
                    .setName("<gray>Door Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je deuren openen en sluiten.")
                    .build(),
            p -> {
                return true;
            },
            false,
            MapDoorItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    GAME_DOOR_ITEM(
            MxDefaultItemStackBuilder.create(Material.BRICK, 1)
                    .setName("<gray>Door Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je deuren openen en sluiten tijdens de game.")
                    .build(),
            p -> {
                return true;
            },
            false,
            GameDoorItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    VUL_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                    .setName("<gray>Vul Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je instellingen in een map aanpassen.")
                    .build(),
            p -> {
                return true;
            },
            false,
            VulTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    HOST_TOOL(
            MxDefaultItemStackBuilder.create(Material.NETHER_STAR, 1)
                    .setName("<gray>Host Tool")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je een game beheren.")
                    .build(),
            p -> {
                return true;
            },
            false,
            GameHostItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    GAMES_ITEM(
            MxDefaultItemStackBuilder.create(Material.COMPASS, 1)
                    .setName("<gray>Game Menu")
                    .addLore(" ")
                    .addLore("<yellow>Met dit item kan je games joinen,")
                    .addLore("<yellow>en games aanmaken als je een host bent.")
                    .build(),
            p -> {
                return p.getWorld().getUID().equals(Functions.getSpawnLocation().getWorld().getUID());
            },
            false,
            GamesItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    ),
    VANISH_ITEM(MxDefaultItemStackBuilder.create(Material.ENDER_EYE)
            .setName("<gray>Vanish")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je ontzichtbaar worden.")
            .build(),
            p -> {
                return true;
            },
            false,
            VanishItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    PLAYER_MANAGEMENT_ITEM(MxDefaultItemStackBuilder.create(Material.CYAN_DYE)
            .setName("<gray>Kleuren tool")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je kleuren beheren.")
            .build(),
            p -> {
                return true;
            },
            false,
            PlayerManagementItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),

    GAME_SPECTATOR_LEAVE_ITEM(MxDefaultItemStackBuilder.create(Material.RED_BED)
            .setName("<gray>Verlaat Game")
            .addBlankLore()
            .addLore("<yellow>Gebruik dit item om te stoppen met spectaten.")
            .addCustomTagString(ItemTag.DROPPABLE.getPersistentDataTag(), false)
            .build(),
            p -> {
                return true;
            },
            false,
            LeaveGameItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_SPECTATOR_TELEPORT_ITEM(MxDefaultItemStackBuilder.create(Material.COMPASS)
            .setName("<gray>Teleporteer naar speler")
            .addBlankLore()
            .addLore("<yellow>Gebruik dit item om te teleporteren naar spelers.")
            .addCustomTagString(ItemTag.DROPPABLE.getPersistentDataTag(), false)
            .build(),
            p -> {
                return true;
            },
            false,
            TeleportItem.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_DEATHNOTE(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Deathnote")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je iemand")
            .addLore("<yellow>deathnoten (vermoorden).")
            .build(),
            p -> {
                return true;
            },
            true,
            DeathnoteBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_INVCLEAR(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Inv-Clear")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je iemand zijn")
            .addLore("<yellow>inventory clearen. (leegmaken)")
            .build(),
            p -> {
                return true;
            },
            true,
            InvClearBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_SWITCH(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Switch")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je met iemand")
            .addLore("<yellow>switchen (verwisellen)")
            .build(),
            p -> {
                return true;
            },
            true,
            SwitchBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_SPELERCOUNT(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Speler-Count")
            .addBlankLore()
            .addLore("<yellow>Met dit item krijg je te horen")
            .addLore("<yellow>hoeveel spelers er nog zijn.")
            .build(),
            p -> {
                return true;
            },
            true,
            SpelerCountBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),

    GAME_MOLCOUNT(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Mol-Count")
            .addBlankLore()
            .addLore("<yellow>Met dit item krijg je te horen")
            .addLore("<yellow>hoeveel mollen er nog zijn.")
            .build(),
            p -> {
                return true;
            },
            true,
            MolCountBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),

    GAME_EGOCOUNT(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Ego-Count")
            .addBlankLore()
            .addLore("<yellow>Met dit item krijg je te horen")
            .addLore("<yellow>hoeveel ego's er nog zijn.")
            .build(),
            p -> {
                return true;
            },
            true,
            EgoCountBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_SHAPESHIFTERCOUNT(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Shapeshifter-Count")
            .addBlankLore()
            .addLore("<yellow>Met dit item krijg je te horen")
            .addLore("<yellow>hoeveel shapeshifter's er nog zijn.")
            .build(),
            p -> {
                return true;
            },
            true,
            ShapeShifterCountBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_PEACEKEEPER_CHECKER(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Peacekeeper-Check")
            .addBlankLore()
            .addLore("<yellow>Met dit item krijgt iedereen te ")
            .addLore("<yellow>zien of iemand de peacekeeper is.")
            .build(),
            p -> {
                return true;
            },
            true,
            PeacekeeperCheckBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_REBORN_BOOK(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Reborn")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je iemand ")
            .addLore("<yellow>rebornen. (genezen)")
            .build(),
            p -> {
                return true;
            },
            true,
            RebornBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_INVCHECK_BOOK(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Inv-Check")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je iemand ")
            .addLore("<yellow>checken op een item.")
            .build(),
            p -> {
                return true;
            },
            true,
            InvCheckBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_TELEPORT_BOOK(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Teleport")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je iemand ")
            .addLore("<yellow>teleporteren naar iemand anders.")
            .build(),
            p -> {
                return true;
            },
            true,
            TeleportBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),

    MAP_GENERATOR_CONFIGURATOR(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Generator")
            .addBlankLore()
            .addLore("<yellow>Met deze generator krijg je een random item.")
            .build(),
            p -> {
                return true;
            },
            false,
            MapGeneratorBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_GENERATOR(MxDefaultItemStackBuilder.create(Material.BOOK)
            .setName("<gray>Generator")
            .addBlankLore()
            .addLore("<yellow>Met deze generator krijg je een random item.")
            .build(),
            p -> {
                return true;
            },
            true,
            GeneratorBook.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    GAME_PLAYER_TOOL(MxDefaultItemStackBuilder.create(Material.NETHER_STAR)
            .setName("<gray>Speler Tool")
            .addBlankLore()
            .addLore("<yellow>Met dit item kan je stemmen,")
            .addLore("<yellow>hosts een vraag stellen,")
            .addLore("<yellow>of alle kleuren zien.")
            .addCustomTagString(ItemTag.DROPPABLE.getPersistentDataTag(), false)
            .addCustomTagString(ItemTag.VANISHABLE.getPersistentDataTag(), false)
            .build(),
            p -> {
                return true;
            },
            true,
            GameSpelerTool.class,
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    ;


    private final ItemStack itemStack;
    private final MxWorldFilter worldFilter;
    private final boolean gameItem;
    private final Class<? extends MxItem> classObject;
    private final Action[] actions;

    Items(ItemStack is, MxWorldFilter mxWorldFilter, boolean gameItem, Class<? extends MxItem> classObject, Action... actions) {
        this.itemStack = is;
        this.worldFilter = mxWorldFilter;
        this.gameItem = gameItem;
        this.classObject = classObject;
        this.actions = actions;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public MxWorldFilter getWorldFilter() {
        return worldFilter;
    }

    public boolean isGameItem() {
        return gameItem;
    }

    public Class<? extends MxItem> getClassObject() {
        return classObject;
    }

    public Action[] getActions() {
        return actions;
    }
}
