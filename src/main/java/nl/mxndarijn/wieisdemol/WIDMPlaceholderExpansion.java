package nl.mxndarijn.wieisdemol;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.mxndarijn.wieisdemol.game.GameInfo;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.GameManager;
import nl.mxndarijn.wieisdemol.managers.database.DatabaseManager;
import nl.mxndarijn.wieisdemol.managers.database.PlayerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WIDMPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "widm-identifier";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mxndarijn-WIDM";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }


    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("player_wins")){
            return DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.SPELERWINS) + "";
        }
        if(params.equalsIgnoreCase("mol_wins")){
            return DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.MOLWINS) + "";
        }
        if(params.equalsIgnoreCase("ego_wins")){
            return DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.EGOWINS) + "";
        }
        if(params.equalsIgnoreCase("games_played")){
            return DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.GAMESPLAYED) + "";
        }
        if(params.equalsIgnoreCase("next_game")){
            List<GameInfo> games = GameManager.getInstance().getUpcomingGameList();
            games.sort(Comparator.comparing(GameInfo::getTime));

            LocalDateTime now = LocalDateTime.now();
            Optional<GameInfo> firstFutureGame = games.stream()
                    .filter(game -> game.getTime().isAfter(now))
                    .findFirst();

            if (firstFutureGame.isPresent()) {
                LocalDateTime timeOfNextGame = firstFutureGame.get().getTime();
                if(timeOfNextGame.toLocalDate().isEqual(LocalDate.now())) {
                    return timeOfNextGame.format(DateTimeFormatter.ofPattern("Vandaag om hh:mm:ss"));
                }
                return timeOfNextGame.format(DateTimeFormatter.ofPattern("dd MMMM hh:mm:ss"));
            } else {
                return "geen games ingepland.";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }

}
