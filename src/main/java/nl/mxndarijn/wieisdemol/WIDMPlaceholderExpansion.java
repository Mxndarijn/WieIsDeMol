package nl.mxndarijn.wieisdemol;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import nl.mxndarijn.wieisdemol.game.GameInfo;
import nl.mxndarijn.wieisdemol.game.UpcomingGameStatus;
import nl.mxndarijn.wieisdemol.managers.GameManager;
import nl.mxndarijn.wieisdemol.managers.database.DatabaseManager;
import nl.mxndarijn.wieisdemol.managers.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
import java.util.UUID;

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
        if(params.equalsIgnoreCase("winrate")){
            // Haal de gegevens uit de database
            int playerWins = DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.SPELERWINS);
            int molWins = DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.MOLWINS);
            int egoWins = DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.EGOWINS);
            int totalGamesPlayed = DatabaseManager.getInstance().getPlayerData(player.getUniqueId()).getData(PlayerData.UserDataType.GAMESPLAYED);

// Bereken de totale overwinningen
            int totalWins = playerWins + molWins + egoWins;

// Bereken de winrate
            double winRate = totalGamesPlayed > 0 ? ((double) totalWins / totalGamesPlayed) * 100 : 0;

// Rond de winrate af naar het dichtstbijzijnde geheel getal
            int roundedWinRate = (int) Math.round(winRate);

// Retourneer de winrate als een percentage
            return roundedWinRate + "%";

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

        if (params.startsWith("games_wr_top5_")) {
            char lastChar = params.charAt(params.length() - 1);

            if (Character.isDigit(lastChar) && "12345".indexOf(lastChar) != -1) {
                int number = Character.getNumericValue(lastChar);

                if (DatabaseManager.getInstance().getTopWinrate(number) != null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(DatabaseManager.getInstance().getTopWinrate(number).getUserid()));

                    return String.format("%s > %s", offlinePlayer.getName(), DatabaseManager.getInstance().getTopWinrate(number).winRate());
                }

                return "-";
            }
        }

        if (params.startsWith("games_pw_top5_")) {
            char lastChar = params.charAt(params.length() - 1);

            if (Character.isDigit(lastChar) && "12345".indexOf(lastChar) != -1) {
                int number = Character.getNumericValue(lastChar);

                if (DatabaseManager.getInstance().getTopPlayerData("SPELERWINS", number) != null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(DatabaseManager.getInstance().getTopPlayerData("SPELERWINS", number).getUserid()));

                    return String.format("%s > %s", offlinePlayer.getName(), DatabaseManager.getInstance().getTopPlayerData("SPELERWINS", number).getData(PlayerData.UserDataType.SPELERWINS));
                }

                return "-";
            }
        }

        if (params.startsWith("games_mw_top5_")) {
            char lastChar = params.charAt(params.length() - 1);

            if (Character.isDigit(lastChar) && "12345".indexOf(lastChar) != -1) {
                int number = Character.getNumericValue(lastChar);

                if (DatabaseManager.getInstance().getTopPlayerData("MOLWINS", number) != null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(DatabaseManager.getInstance().getTopPlayerData("MOLWINS", number).getUserid()));

                    return String.format("%s > %s", offlinePlayer.getName(), DatabaseManager.getInstance().getTopPlayerData("MOLWINS", number).getData(PlayerData.UserDataType.MOLWINS));
                }

                return "-";
            }
        }

        if (params.startsWith("games_ew_top5_")) {
            char lastChar = params.charAt(params.length() - 1);

            if (Character.isDigit(lastChar) && "12345".indexOf(lastChar) != -1) {
                int number = Character.getNumericValue(lastChar);

                if (DatabaseManager.getInstance().getTopPlayerData("EGOWINS", number) != null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(DatabaseManager.getInstance().getTopPlayerData("EGOWINS", number).getUserid()));

                    return String.format("%s > %s", offlinePlayer.getName(), DatabaseManager.getInstance().getTopPlayerData("EGOWINS", number).getData(PlayerData.UserDataType.EGOWINS));
                }

                return "-";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }

}
