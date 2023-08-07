package nl.mxndarijn.logic.util.logger;

import org.bukkit.ChatColor;

public enum Prefix {
    MXATLAS(ChatColor.DARK_AQUA + "MxAtlas"),
    MXCOMMAND(ChatColor.DARK_GREEN + "MxCommand"),
    MXITEM(ChatColor.DARK_AQUA + "MxItem"),
    ITEM_MANAGER(ChatColor.GREEN + "Item-Manager"),
    MXHEAD_MANAGER(ChatColor.DARK_BLUE + "MxHead-Manager"),
    MXINVENTORY(ChatColor.DARK_GREEN + "MxInventory"),
    MXCHATINPUT_MANAGER(ChatColor.DARK_PURPLE + "MxChatInput-Manager"),
    CHANGEWORLD_MANAGER(ChatColor.AQUA + "ChangeWorld-Manager"),
    WORLD_MANAGER(ChatColor.AQUA + "World-Manager"),
    PRESETS_MANAGER(ChatColor.AQUA + "Presets-Manager"),
    MAPS_MANAGER(ChatColor.AQUA + "Maps-Manager"),
    GAMES_MANAGER(ChatColor.AQUA + "Games-Manager"),
    LOGGER(ChatColor.DARK_PURPLE + "Logger"),
    LANGUAGE_MANAGER(ChatColor.GOLD + "Language-Manager"),
    CONFIG_FILES(ChatColor.YELLOW + "Config-Files"),
    STORAGE_MANAGER(ChatColor.GREEN + "Storage-Manager");

    private final String prefix;
    private final String name;
    Prefix(String prefix) {
        this.prefix = ChatColor.DARK_GRAY + "[" + prefix + ChatColor.DARK_GRAY + "] ";
        this.name = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }
}
