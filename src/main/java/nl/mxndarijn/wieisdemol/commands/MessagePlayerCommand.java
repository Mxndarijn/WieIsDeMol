package nl.mxndarijn.wieisdemol.commands;

import nl.mxndarijn.api.mxcommand.MxCommand;
import nl.mxndarijn.api.util.MSG;
import nl.mxndarijn.wieisdemol.data.Permissions;
import nl.mxndarijn.wieisdemol.game.Game;
import nl.mxndarijn.wieisdemol.managers.language.LanguageManager;
import nl.mxndarijn.wieisdemol.managers.language.LanguageText;
import nl.mxndarijn.wieisdemol.managers.world.GameWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class MessagePlayerCommand extends MxCommand {


    public MessagePlayerCommand(Permissions permissions, boolean b, boolean b1) {
        super(permissions, b, b1);
    }


    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;
        Optional<Game> optionalGame = GameWorldManager.getInstance().getGameByWorldUID(p.getWorld().getUID());
        if (optionalGame.isEmpty()) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_NOT_HOST));
            return;
        }
        Game game = optionalGame.get();

        if (!game.getHosts().contains(p.getUniqueId())) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_NOT_HOST));
            return;
        }

        if (args.length < 2) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_SYNTAX_ERROR));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_PLAYER_NOT_FOUND));
            return;
        }

        if (game.getGamePlayerOfPlayer(player.getUniqueId()).isEmpty()) {
            MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_NOT_IN_GAME));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        MSG.msg(player, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST, Collections.singletonList(message)));
        game.sendMessageToHosts(LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_TO_ALL_HOSTS, Arrays.asList(player.getName(), message)));
        MSG.msg(p, LanguageManager.getInstance().getLanguageString(LanguageText.GAME_MESSAGE_FROM_HOST_SEND));
    }
}
