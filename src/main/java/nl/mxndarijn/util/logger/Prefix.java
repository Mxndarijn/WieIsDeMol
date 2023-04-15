package nl.mxndarijn.util.logger;

import org.bukkit.ChatColor;

public enum Prefix {
    MXATLAS(ChatColor.DARK_AQUA + "MxAtlas"),
    MXCOMMAND(ChatColor.DARK_GREEN + "MxCommand"),
    MXHEADMANAGER(ChatColor.DARK_BLUE + "MxHeadManager"),
    MXINVENTORY(ChatColor.DARK_GREEN + "MxInventory"),
    MXCHATINPUTMANAGER(ChatColor.DARK_PURPLE + "MxChatInputManager"),
    WORLD_MANAGER(ChatColor.AQUA + "World-Manager"),
    LOGGER(ChatColor.DARK_PURPLE + "Logger"),
    LANGUAGE_MANAGER(ChatColor.GOLD + "Language-Manager"),
    CONFIG_FILES(ChatColor.YELLOW + "Config-Files");

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
