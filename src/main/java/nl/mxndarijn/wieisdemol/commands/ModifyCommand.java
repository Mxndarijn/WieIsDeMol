package nl.mxndarijn.wieisdemol.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.api.util.MxWorldFilter;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.*;
import nl.mxndarijn.wieisdemol.items.game.books.Book;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_NO_ITEM_FOUND));
            return;
        }
        MxDefaultMenuBuilder menu = MxDefaultMenuBuilder.create("<gray>Modify", MxInventorySlots.THREE_ROWS);
        if(is.getType() == Material.BOOK) {
            menu.setItem(MxDefaultItemStackBuilder.create(Material.BOOK)
                    .setName("<gray>Book modify")
                    .addBlankLore()
                    .addLore("<gray>Met book modify kan je succes kansen instellen,")
                    .addLore("<gray>en eventueel wat er moet gebeuren als het faalt.")
                    .addBlankLore()
                    .addLore("<yellow>Klik hier om de itemtags van het item aan te passen.")
                    .build(), 16, getOnClickBookLore(p, is));
        }
        MxInventoryManager.getInstance().addAndOpenInventory(p, menu
                .setItem(MxDefaultItemStackBuilder.create(Material.OAK_SIGN)
                        .setName("<gray>Verander Naam")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om de naam van het item aan te passen.")
                        .build(), 10, getClickOnName(p, is))

                .setItem(MxDefaultItemStackBuilder.create(Material.ANVIL)
                        .setName("<gray>Verander Durability")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om de durability van het item aan te passen.")
                        .build(), 12, getClickOnDurability(p, is))

                .setItem(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                        .setName("<gray>Verander Enchantments")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om de enchants van het item aan te passen.")
                        .build(), 14, getClickOnEnchantments(p, is))
                .setItem(MxDefaultItemStackBuilder.create(Material.PIGLIN_BANNER_PATTERN)
                        .setName("<gray>Verander Lore")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om de lore van het item aan te passen.")
                        .build(), 11, getClickOnLore(p, is))
                .setItem(MxDefaultItemStackBuilder.create(Material.NAME_TAG)
                        .setName("<gray>Verander Itemtags")
                        .addBlankLore()
                        .addLore("<yellow>Klik hier om de itemtags van het item aan te passen.")
                        .build(), 15, getClickOnItemTags(p, is))
                .build()

        );
    }

    private MxItemClicked getOnClickBookLore(Player p, ItemStack is) {
        return (mxInv, e) -> {
            String key = "success-rating";
            ItemMeta im = is.getItemMeta();

            PersistentDataContainer container = im.getPersistentDataContainer();
            int data = container.getOrDefault(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), key), PersistentDataType.INTEGER, 100);

            MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Book Modify", MxInventorySlots.THREE_ROWS)
                    .setItem(MxDefaultItemStackBuilder.create(Material.EXPERIENCE_BOTTLE)
                                    .setName("<gray>Succeskans")
                                    .addBlankLore()
                                    .addLore("<gray>Succeskans: " + data)
                                    .addBlankLore()
                                    .addLore("<yellow>Klik hier om de kans aan te passen.").build(), 12,
                            (mxInv1, e1) -> {
                                p.closeInventory();
                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.BOOK_MODIFY_SUCCESS_ENTER_NUMBER));
                                MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                                   try {
                                       int i = Integer.parseInt(message);
                                       if(i < 0 || i > 100) {
                                           MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.BOOK_MODIFY_SUCCESS_NOT_A_NUMBER));
                                           return;
                                       }
                                       container.set(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), key), PersistentDataType.INTEGER, i);
                                       MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.BOOK_MODIFY_SUCCESS_RATING_CHANGED, Collections.singletonList(i + "")));
                                       String lore = "<blue>" + "Succeskans: " + ((i > 50) ? "<green>" : "<red>") + i + "%";
                                       List<Component> list = im.hasLore() ? im.lore() : new ArrayList<>();
                                       List<Component> newList = new ArrayList<>();
                                       list.forEach(c -> {
                                           if (!Functions.convertComponentToString(c).contains("<blue>" + "Succeskans: ")) {
                                               newList.add(c);
                                           }
                                       });
                                       list = newList;
                                       list.add(MiniMessage.miniMessage().deserialize("<!i>" + lore));
                                       im.lore(list);
                                       is.setItemMeta(im);
                                   } catch(NumberFormatException ex) {
                                       MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.BOOK_MODIFY_SUCCESS_NOT_A_NUMBER));
                                   }
                                });

                            })
                    .setItem(MxDefaultItemStackBuilder.create(Material.LECTERN)
                                    .setName("<gray>Faal-Actie")
                                    .addBlankLore()
                                    .addBlankLore()
                                    .addLore("<yellow>Klik hier om de faal-actie aan te passen.").build(), 14,
                            (mxInv1, e1) -> {
                                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                Optional<BookData> optionalBook = BookData.getBookByItemStack(is);
                                if(optionalBook.isEmpty()) {
                                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_BOOK_NOT_FOUND));
                                    return;
                                }
                                for (BookFailureAction value : BookFailureAction.values()) {
                                    if(value.getPlayersNeeded() > optionalBook.get().getPersons().length) {
                                        continue;
                                    }
                                    list.add(new Pair<>(
                                            value.getIs(),
                                            (mxInv2, e2) -> {
                                                List<AvailablePerson> persons = new ArrayList<>();
                                                AtomicInteger index = new AtomicInteger();
                                                StringBuilder dataBuilder = new StringBuilder(value.getType() + "[");
                                                List<String> items = value.getSelectors();

                                                List<Pair<ItemStack, MxItemClicked>> selectorsList = new ArrayList<>();
                                                for (AvailablePerson availablePerson : optionalBook.get().getPersons()) {
                                                    selectorsList.add(new Pair<>(
                                                            MxDefaultItemStackBuilder.create(Material.PLAYER_HEAD)
                                                                    .setName("<gray>" + availablePerson.getName())
                                                                    .addBlankLore()
                                                                    .addLore("<yellow>Selecteer " + availablePerson.getName())
                                                                    .build(),
                                                            (mxInv3, e3) -> {
                                                                        persons.add(availablePerson);
                                                                        index.getAndIncrement();

                                                                        if(index.get() < value.getPlayersNeeded()) {
                                                                            MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(items.get(index.get()), MxInventorySlots.THREE_ROWS)
                                                                                    .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                                                                                    .setListItems(selectorsList)
                                                                                    .build());
                                                                        } else {
                                                                            persons.forEach(person -> {
                                                                                dataBuilder.append(person.getType()).append(",");
                                                                            });
                                                                            dataBuilder.deleteCharAt(dataBuilder.length() -1);
                                                                            dataBuilder.append("]");

                                                                            String keyForFail = "fail-action";
                                                                            container.set(new NamespacedKey(JavaPlugin.getPlugin(WieIsDeMol.class), keyForFail), PersistentDataType.STRING, dataBuilder.toString());
                                                                            String lore = "<blue>" +"<gray><dark_aqua><reset><red>Faal-Actie: ";
                                                                            List<Component> listLore = im.hasLore() ? im.lore() : new ArrayList<>();
                                                                            List<Component> newList = new ArrayList<>();
                                                                            listLore.forEach(c -> {
                                                                                if (!Functions.convertComponentToString(c).contains(lore)) {
                                                                                    newList.add(c);
                                                                                }
                                                                            });
                                                                            listLore = newList;
                                                                            listLore.add(MiniMessage.miniMessage().deserialize("<!i>" + lore + value.getTextInterface().getText(persons)));
                                                                            im.lore(listLore);
                                                                            is.setItemMeta(im);

                                                                            p.closeInventory();
                                                                            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_BOOK_CHANGED));

                                                                        }
                                                            }
                                                    ));
                                                }
                                                if(value.getPlayersNeeded() > 0) {
                                                    MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create(items.get(index.get()), MxInventorySlots.THREE_ROWS)
                                                            .setPrevious(mxInv1)
                                                            .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                                                                    .setListItems(selectorsList)
                                                            .build());
                                                }
                                            }
                                    ));
                                }
                                MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>"+ "Selecteer actie", MxInventorySlots.THREE_ROWS)
                                                .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_TWO)
                                                .setPrevious(mxInv1)
                                                .setListItems(list)
                                        .build());

                            }).build());
        };
    }

    private MxItemClicked getClickOnName(Player p, ItemStack is) {
        return (mxInv, e) -> {
            p.closeInventory();
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_NAME));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                ItemMeta im = is.getItemMeta();
                im.displayName(MiniMessage.miniMessage().deserialize("<!i>" + message));
                is.setItemMeta(im);
                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_NAME_CHANGED));
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
          MxInventoryManager.getInstance().addAndOpenInventory(p, new MxListInventoryBuilder("<gray>ItemTags", MxInventorySlots.THREE_ROWS)
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
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_LORE));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                List<String> lore = new ArrayList<>(List.of(message.split("/n")));
                List<Component> loreComp = lore.stream().map(l -> MiniMessage.miniMessage().deserialize("<!i>" + l)).collect(Collectors.toList());
                ItemMeta im = is.getItemMeta();
                im.lore(loreComp);
                is.setItemMeta(im);
                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_LORE_CHANGED));
            });
        };
    }

    private MxItemClicked getClickOnDurability(Player p, ItemStack is) {
        return (mxInv, e) -> {
            p.closeInventory();
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENTER_NEW_DURABILITY));
            MxChatInputManager.getInstance().addChatInputCallback(p.getUniqueId(), message -> {
                try {
                    int dura = Integer.parseInt(message);
                    if(is.getItemMeta() instanceof Damageable im) {
                        im.setDamage(is.getType().getMaxDurability() - dura);
                        is.setItemMeta(im);
                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_CHANGED));
                    } else {
                        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_DOES_NOT_HAVE));
                    }
                } catch (NumberFormatException ee) {
                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_DURABILITY_ENTER_A_NUMBER));

                }
            });
        };
    }

    private MxItemClicked getClickOnEnchantments(Player p, ItemStack is) {
        return (prev, e) -> {
            MxInventoryManager.getInstance().addAndOpenInventory(p, MxDefaultMenuBuilder.create("<gray>Enchantments", MxInventorySlots.THREE_ROWS)
                            .setPrevious(prev)
                    .setItem(MxSkullItemStackBuilder.create(1)
                                    .setName("<gray>Voeg enchantments toe")
                                    .setSkinFromHeadsData("light-green-block")
                                    .addBlankLore()
                                    .addLore("<yellow>Klik hier om enchantments toe te voegen.")
                                    .build(),
                            12, (mxInv1, e1) -> {
                                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                for (Enchantment value : Enchantment.values()) {
                                    for(int i = value.getStartLevel(); i <= value.getMaxLevel(); i++) {
                                        int finalI = i;
                                        list.add(new Pair<>(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                                                .setName("<gray>" + Functions.convertComponentToString(value.displayName(i)))
                                                .addEnchantment(value, i, true)
                                                .addBlankLore()
                                                .addLore("<yellow>Klik hier om deze enchantment toe te voegen")
                                                .build(),
                                                (mxInv2, e2) -> {
                                                    is.addUnsafeEnchantment(value, finalI);
                                                    MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENCHANTMENT_ADDED));
                                                    p.closeInventory();
                                                }

                                        ));
                                    }
                                }

                                MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Enchantments toevoegen", MxInventorySlots.SIX_ROWS)
                                        .setAvailableSlots(MxInventoryIndex.ROW_ONE_TO_FIVE)
                                                .setPrevious(mxInv1)
                                                .setListItems(list)

                                        .build());
                            }
                    )
                    .setItem(MxSkullItemStackBuilder.create(1)
                                    .setName("<gray>Verwijder enchantments")
                                    .setSkinFromHeadsData("red-block")
                                    .addBlankLore()
                                    .addLore("<yellow>Klik hier om enchantments te verwijderen.")
                                    .build(),
                            14, (mxInv1, e1) -> {
                                List<Pair<ItemStack, MxItemClicked>> list = new ArrayList<>();
                                is.getEnchantments().forEach((enchantment, level) -> {
                                    list.add(new Pair<>(MxDefaultItemStackBuilder.create(Material.ENCHANTED_BOOK)
                                            .setName("<gray>" + Functions.convertComponentToString(enchantment.displayName(level)))
                                            .addEnchantment(enchantment, level, true)
                                            .addBlankLore()
                                            .addLore("<yellow>Klik hier om deze enchantment te verwijderen")
                                            .build(),
                                            (mxInv2, e2) -> {
                                                is.removeEnchantment(enchantment);
                                                MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.MODIFY_ENCHANTMENT_REMOVED));
                                                p.closeInventory();
                                            }

                                    ));
                                });

                                MxInventoryManager.getInstance().addAndOpenInventory(p, MxListInventoryBuilder.create("<gray>Enchantments verwijderen", MxInventorySlots.SIX_ROWS)
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
