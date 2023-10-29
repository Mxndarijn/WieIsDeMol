package nl.mxndarijn.wieisdemol.commands;

import net.kyori.adventure.text.Component;
import nl.mxndarijn.api.chatinput.MxChatInputCallback;
import nl.mxndarijn.api.chatinput.MxChatInputManager;
import nl.mxndarijn.api.inventory.*;
import nl.mxndarijn.api.inventory.menu.MxDefaultMenuBuilder;
import nl.mxndarijn.api.inventory.menu.MxListInventoryBuilder;
import nl.mxndarijn.api.item.MxDefaultItemStackBuilder;
import nl.mxndarijn.api.item.MxSkullItemStackBuilder;
import nl.mxndarijn.api.item.Pair;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.util.Functions;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ItemTag;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModifyCommand extends MxCommand {

    public ModifyCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        super(permission, onlyPlayersCanExecute, canBeExecutedInGame, worldFilter);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) throws Exception {
        Player p = (Player) sender;

        ItemStack is = p.getInventory().getItemInMainHand();
        if(is == null || is.getType() == Material.AIR) {
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_NO_ITEM_FOUND));
            return;
        }
        MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create(ChatColor.GRAY + "Modify", MxInventorySlots.THREE_ROWS)
                .setItem(MxDefaultItemStackBuilder.create(Material.OAK_SIGN)
                        .setName(ChatColor.GRAY + "Verander Naam")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om de naam van het item aan te passen.")
                        .build(), 10, getClickOnName(p, is))

                .setItem(MxDefaultItemStackBuilder.create(Material.ANVIL)
                        .setName(ChatColor.GRAY + "Verander Durability")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om de durability van het item aan te passen.")
                        .build(), 12, getClickOnDurability(p, is))

                .setItem(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                        .setName(ChatColor.GRAY + "Verander Enchantments")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om de enchants van het item aan te passen.")
                        .build(), 14, getClickOnEnchantments(p, is))
                .setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                        .setName(ChatColor.GRAY + "Verander Lore")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om de lore van het item aan te passen.")
                        .build(), 11, getClickOnLore(p, is))
                .setItem(MxDefaultItemStackBuilder.create(Material.NAME_TAG)
                        .setName(ChatColor.GRAY + "Verander Itemtags")
                        .addBlankLore()
                        .addLore(ChatColor.YELLOW + "Klik hier om de itemtags van het item aan te passen.")
                        .build(), 15, getClickOnItemTags(p, is))

                .build()

        );
    }

    private MxItemClicked getClickOnName(Player p, ItemStack is) {
        return (mxInv, e) -> {
            p.closeInventory();
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_NAME));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                ItemMeta im = is.getItemMeta();
                im.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', message)));
                is.setItemMeta(im);
                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_NAME_CHANGED));
            });
        };
    }

    private MxItemClicked getClickOnItemTags(Player p, ItemStack is) {
        return (mxInv, e) -> {
            PersistentDataContainer container = is.getItemMeta().getPersistentDataContainer();
            List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
            for (ItemTag value : ItemTag.values()) {
                try {
                list.add(new Pair<>(
                        value.getContainer().getItem(container.get(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), value.getPersistentDataTag()), PersistentDataType.STRING)),
                        value.getClicked()
                    ));
                } catch(Exception ex) {
                    Logger.logMessage(LogLevel.ERROR, "Could not load itemtag: ");
                    ex.printStackTrace();
                }
            }
          MxInventoryManager.getInstance().addAndOpenInventory(p, new MxListInventoryBuilder(ChatColor.GRAY + "ItemTags", MxInventorySlots.THREE_ROWS)
                  .setShowPageNumbers(false)
                  .setListItems(list)
                  .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                  .setPrevious(mxInv)
                  .build());
        };

    }

    private MxItemClicked getClickOnLore(Player p, ItemStack is) {
        return (mxInv, e) -> {

            p.closeInventory();
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_LORE));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                List<String> lore = new ArrayList<>(List.of(message.split("\n")));
                List<Component> loreComp = lore.stream().map(l -> Component.text(ChatColor.translateAlternateColorCodes('&', l))).collect(Collectors.toList());
                ItemMeta im = is.getItemMeta();
                im.lore(loreComp);
                is.setItemMeta(im);
                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_LORE_CHANGED));
            });
        };
    }

    private MxItemClicked getClickOnDurability(Player p, ItemStack is) {
        return (mxInv, e) -> {
            p.closeInventory();
            p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_DURABILITY));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                try {
                    int dura = Integer.parseInt(message);
                    if(is.getItemMeta() instanceof Damageable im) {
                        im.setDamage(is.getType().getMaxDurability() - dura);
                        is.setItemMeta(im);
                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_CHANGED));
                    } else {
                        p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_DOES_NOT_HAVE));
                    }
                } catch (NumberFormatException ee) {
                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_ENTER_A_NUMBER));

                }
            });
        };
    }

    private MxItemClicked getClickOnEnchantments(Player p, ItemStack is) {
        return (prev, e) -> {
            MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create(ChatColor.GRAY + "Enchantments", MxInventorySlots.THREE_ROWS)
                            .setPrevious(prev)
                    .setItem(MxSkullItemStackBuilder.create(1)
                                    .setName(ChatColor.GRAY + "Voeg enchantments toe")
                                    .setSkinFromHeadsData("light-green-block")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Klik hier om enchantments toe te voegen.")
                                    .build(),
                            12, (mxInv1, e1) -> {
                                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                for (Enchantment value : Enchantment.values()) {
                                    for(int i = value.getStartLevel(); i <= value.getMaxLevel(); i++) {
                                        int finalI = i;
                                        list.add(new Pair<>(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                                                .setName(ChatColor.GRAY + Functions.convertComponentToString(value.displayName(i)))
                                                .addEnchantment(value, i, true)
                                                .addBlankLore()
                                                .addLore(ChatColor.YELLOW + "Klik hier om deze enchantment toe te voegen")
                                                .build(),
                                                (mxInv2, e2) -> {
                                                    is.addUnsafeEnchantment(value, finalI);
                                                    p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENCHANTMENT_ADDED));
                                                    p.closeInventory();
                                                }

                                        ));
                                    }
                                }

                                MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Enchantments toevoegen", MxInventorySlots.SIX_ROWS)
                                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                                .setPrevious(mxInv1)
                                                .setListItems(list)

                                        .build());
                            }
                    )
                    .setItem(MxSkullItemStackBuilder.create(1)
                                    .setName(ChatColor.GRAY + "Verwijder enchantments")
                                    .setSkinFromHeadsData("red-block")
                                    .addBlankLore()
                                    .addLore(ChatColor.YELLOW + "Klik hier om enchantments te verwijderen.")
                                    .build(),
                            14, (mxInv1, e1) -> {
                                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                is.getEnchantments().forEach((enchantment, level) -> {
                                    list.add(new Pair<>(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                                            .setName(ChatColor.GRAY + Functions.convertComponentToString(enchantment.displayName(level)))
                                            .addEnchantment(enchantment, level, true)
                                            .addBlankLore()
                                            .addLore(ChatColor.YELLOW + "Klik hier om deze enchantment te verwijderen")
                                            .build(),
                                            (mxInv2, e2) -> {
                                                is.removeEnchantment(enchantment);
                                                p.sendMessage(LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENCHANTMENT_REMOVED));
                                                p.closeInventory();
                                            }

                                    ));
                                });

                                MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(ChatColor.GRAY + "Enchantments verwijderen", MxInventorySlots.SIX_ROWS)
                                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                        .setPrevious(mxInv1)
                                        .setListItems(list)

                                        .build());
                            }
                    )

                    .build()

            );
        };
    }
}
