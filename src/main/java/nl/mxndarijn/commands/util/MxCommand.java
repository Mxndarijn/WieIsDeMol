package nl.mxndarijn.commands.util;

import nl.mxndarijn.data.ChatPrefix;
import nl.mxndarijn.data.Permissions;
import nl.mxndarijn.util.language.LanguageManager;
import nl.mxndarijn.util.language.LanguageText;
import nl.mxndarijn.util.logger.LogLevel;
import nl.mxndarijn.util.logger.Logger;
import nl.mxndarijn.util.logger.Prefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public abstract class MxCommand implements CommandExecutor {

    private final Permissions permission;
    private final boolean onlyPlayersCanExecute;
    private final boolean canBeExecutedInGame;
    private final MxWorldFilter worldFilter;
    private final LanguageManager languageManager;

    public MxCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame, MxWorldFilter worldFilter) {
        this.permission = permission;
        this.onlyPlayersCanExecute = onlyPlayersCanExecute;
        this.worldFilter = worldFilter;
        this.canBeExecutedInGame = canBeExecutedInGame;
        this.languageManager = LanguageManager.getInstance();
    }
    public MxCommand(Permissions permission, boolean onlyPlayersCanExecute, boolean canBeExecutedInGame) {
        this(permission, onlyPlayersCanExecute, canBeExecutedInGame,null);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(onlyPlayersCanExecute) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(languageManager.getLanguageString(LanguageText.NO_PLAYER, Collections.emptyList(),  ChatPrefix.WIDM));
                return true;
            }
            if(worldFilter != null && !worldFilter.isPlayerInCorrectWorld((Player) sender)) {
                sender.sendMessage(languageManager.getLanguageString(LanguageText.NOT_CORRECT_WORLD, Collections.emptyList(),  ChatPrefix.WIDM));
                return true;
            }
        }
        if(!sender.hasPermission(permission.getPermission())) {
            sender.sendMessage(languageManager.getLanguageString(LanguageText.NO_PERMISSION, Collections.emptyList(),  ChatPrefix.WIDM));
            return true;
        }
        if(sender instanceof Player) {
            // TODO check ingame
        }
        try {
            execute(sender, command, label, args);
        } catch (Exception e) {
            Logger.logMessage(LogLevel.ERROR, Prefix.MXCOMMAND, "Could not execute command " + command.getName());
            e.printStackTrace();
            sender.sendMessage(languageManager.getLanguageString(LanguageText.ERROR_WHILE_EXECUTING_COMMAND, Collections.emptyList(),  ChatPrefix.WIDM));
        }
        return true;
    }

    public abstract void execute(CommandSender sender, Command command, String label, String[] args) throws Exception;
}
