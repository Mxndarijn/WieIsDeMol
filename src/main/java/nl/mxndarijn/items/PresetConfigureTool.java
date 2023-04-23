package nl.mxndarijn.items;

import nl.mxndarijn.commands.util.MxWorldFilter;
import nl.mxndarijn.inventory.MxInventory;
import nl.mxndarijn.inventory.MxInventoryManager;
import nl.mxndarijn.inventory.MxInventorySlots;
import nl.mxndarijn.inventory.MxItemClicked;
import nl.mxndarijn.inventory.heads.MxHeadManager;
import nl.mxndarijn.inventory.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.inventory.menu.MxMenuBuilder;
import nl.mxndarijn.items.util.MxItem;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.world.presets.Preset;
import nl.mxndarijn.world.presets.PresetConfig;
import nl.mxndarijn.world.presets.PresetsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class PresetConfigureTool extends MxItem  {

    public PresetConfigureTool(ItemStack is, MxWorldFilter worldFilter, boolean gameItem, Action... actions) {
        super(is, worldFilter, gameItem, actions);
    }

    @Override
    public void execute(Player p, PlayerInteractEvent e) {
        Optional<Preset> optionalPreset = PresetsManager.getInstance().getPresetByWorldUID(e.getPlayer().getWorld().getUID());
        if(!optionalPreset.isPresent()) {
            return;
        }
        Preset preset = optionalPreset.get();
        PresetConfig config = preset.getConfig();

        MxInventoryManager.getInstance().addAndOpenInventory(p,MxDefaultMenuBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.SKELETON_SKULL)
                                .setName(ChatColor.GRAY + "Verander skull")
                                .addBlankLore()
                                .addLore(ChatColor.GRAY + "Status: " + (MxHeadManager.getInstance().getHeadSection(config.getSkullId()).isPresent() ? MxHeadManager.getInstance().getHeadSection(config.getSkullId()).get().getName().get() : "Niet-gevonden"))
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de skull van de preset te veranderen.")
                                .addLore(ChatColor.YELLOW + "Je krijgt een lijst met skulls van het commands /skulls.")
                                .build(),
                        9,
                        (mainInv, clickMain) -> {

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.NAME_TAG)
                                .setName(ChatColor.GRAY + "Verander naam")
                                .addBlankLore()
                                .addLore(ChatColor.GRAY + "Status: " + config.getName())
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de naam van de preset te veranderen.")
                                .addLore(ChatColor.YELLOW + "Vervolgens moet je in de chat de nieuwe naam sturen.")
                                .build(),
                        10,
                        (mainInv, clickMain) -> {

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.COMPASS)
                                .setName(ChatColor.GRAY + "Warps")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de warps van de preset te bekijken en te veranderen.")
                                .build(),
                        12,
                        (mainInv, clickMain) -> {

                        })
                .setItem(MxDefaultItemStackBuilder.create(Material.LIGHT_BLUE_SHULKER_BOX)
                                .setName(ChatColor.GRAY + "Kleuren")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Klik hier om de kleuren van de preset te bekijken en te veranderen.")
                                .build(),
                        14,
                        (mainInv, clickMain) -> {

                        })
                .setItem(getHostDifficulty(preset, config),
                        16,
                        (mainInv, clickMain) -> {
                            openStarsMenu(p, mainInv, preset, config, 1);
                        })
                .setItem(getPlayDifficulty(preset, config),
                        17,
                        (mainInv, clickMain) -> {
                            openStarsMenu(p, mainInv, preset, config, 0);
                        })
                .setItem(getConfiguredItemStack(config),
                        22,
                        (mainInv, clickMain) -> {
                            config.setConfigured(!config.isConfigured());
                            mainInv.getInv().setItem(22, getConfiguredItemStack(config));
                            config.save();
                        })
                .build());
    }

    private ItemStack getConfiguredItemStack(PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.ANVIL)
                .setName(ChatColor.GRAY + "Toggle configured")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + (config.isConfigured() ? ChatColor.GREEN + "Geconfigureerd" : ChatColor.RED + "Niet geconfigureerd"))
                .addLore(ChatColor.YELLOW + "Klik hier om de configuratie te togglen.")
                .addLore(ChatColor.YELLOW + "Geconfigueerd: Spelers kunnen er een map voor maken.")
                .addLore(ChatColor.YELLOW + "Niet geconfigueerd: Spelers ziet de preset niet.")
                .build();
    }

    private ItemStack getHostDifficulty(Preset preset, PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.GOLDEN_APPLE)
                .setName(ChatColor.GRAY + "Verander host-difficulty")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + preset.getStars(config.getHostDifficulty()))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de host-difficulty te veranderen.")
                .build();
    }

    private ItemStack getPlayDifficulty(Preset preset, PresetConfig config) {
        return MxDefaultItemStackBuilder.create(Material.BREAD)
                .setName(ChatColor.GRAY + "Verander play-difficulty")
                .addBlankLore()
                .addLore(ChatColor.GRAY + "Status: " + preset.getStars(config.getPlayDifficulty()))
                .addBlankLore()
                .addLore(ChatColor.YELLOW + "Klik hier om de play-difficulty te veranderen.")
                .build();
    }

    private void openStarsMenu(Player p,MxInventory mainInv, Preset preset, PresetConfig config, int type) {
        String levelTag = "level";
        MxItemClicked clicked = (mxInv, e) -> {
            PersistentDataContainer container = e.getCurrentItem().getItemMeta().getPersistentDataContainer();
            int level = container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), levelTag), PersistentDataType.INTEGER);
            if(type == 0) {
                config.setPlayDifficulty(level);
                mainInv.getInv().setItem(17, getPlayDifficulty(preset, config));
            } else {
                config.setHostDifficulty(level);
                mainInv.getInv().setItem(16, getHostDifficulty(preset, config));
            }
            config.save();
            MxInventoryManager.getInstance().addAndOpenInventory(p, mainInv);
        };

        MxInventoryManager.getInstance().addAndOpenInventory(p,MxDefaultMenuBuilder.create(ChatColor.GRAY + "Preset Configure-Tool", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 1")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 1)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 1.")
                                .build(),
                        11,
                       clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 2")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 2)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 2.")
                                .build(),
                        12,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 3")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 3)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 3.")
                                .build(),
                        13,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 4")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 4)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 4.")
                                .build(),
                        14,
                        clicked)
                .setItem(MxDefaultItemStackBuilder.create(Material.TURTLE_EGG)
                                .setName(ChatColor.GRAY + "Level: 5")
                                .addBlankLore()
                                .addCustomTagString(levelTag, 5)
                                .addLore(ChatColor.YELLOW + "Klik hier om het level te veranderen naar 5.")
                                .build(),
                        15,
                        clicked)
                        .setItem(MxDefaultItemStackBuilder.create(Material.PAPER)
                                .setName(ChatColor.GRAY + "Info")
                                .addBlankLore()
                                .addLore(ChatColor.YELLOW + "Verander het visuele aspect voor de " + (type == 0 ? "play-difficulty" : "host-difficulty"))
                                .build(),22,null)
                .setPrevious(mainInv)
                .build());
    }
}
