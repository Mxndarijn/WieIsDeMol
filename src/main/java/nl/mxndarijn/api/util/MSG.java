package nl.mxndarijn.api.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MSG {

    public static void msg(CommandSender player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<!i>" + message));
    }

}
