package nl.mxndarijn.wieisdemol.managers.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ConfigFiles;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseManager implements Listener {

    private static DatabaseManager instance;
    @Getter
    private HashMap<UUID, PlayerData> playerDataHashMap;
    private final HikariDataSource hikari;
    public static DatabaseManager getInstance() {
        if(instance == null)
            instance = new DatabaseManager();
        return instance;
    }
    private DatabaseManager() {
        JavaPlugin.getPlugin(WieIsDeMol.class).getServer().getPluginManager().registerEvents(this, JavaPlugin.getPlugin(WieIsDeMol.class));
        Logger.logMessage(LogLevel.INFORMATION, Prefix.DATABASEMANAGER,"Starting DatabaseManager...");
        try {
            playerDataHashMap = new HashMap<>();
            FileConfiguration fc = ConfigFiles.MAIN_CONFIG.getFileConfiguration();

            hikari = new HikariDataSource();
            hikari.setJdbcUrl(fc.getString("database-connection-string", ""));
            hikari.setUsername(fc.getString("database-username", ""));

            String password = fc.getString("database-password", "");
            if(!password.equalsIgnoreCase("")) {
                hikari.setPassword(password);
            }

            Connection connection = getConnection();
            DatabaseMetaData dbm = connection.getMetaData();

            ResultSet tables = dbm.getTables(null, null, "userdata", null);
            if (!tables.next()) {
                Logger.logMessage(LogLevel.INFORMATION, "No table found for userdata, creating one...");
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE `userdata` ("
                        + "`userid` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',"
                        + "`spelerwins` INT(11) NULL DEFAULT '0',"
                        + "`molwins` INT(11) NULL DEFAULT '0',"
                        + "`egowins` INT(11) NULL DEFAULT '0',"
                        + "`gamesplayed` INT(11) NULL DEFAULT '0',"
                        + "PRIMARY KEY (`userid`) USING BTREE"
                        + ") COLLATE='utf8mb4_general_ci' ENGINE=InnoDB;");
            }
            connection.close();
        }  catch (SQLException e) {
            Logger.logMessage(LogLevel.FATAL, Prefix.DATABASEMANAGER,"Could not establish connection with database...");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayerData getPlayerData(UUID uuid) {
        if(playerDataHashMap.containsKey(uuid))
            playerDataHashMap.put(uuid, PlayerData.create(uuid));
        return playerDataHashMap.get(uuid);
    }

    public PlayerData getTopPlayerData(String top, int number) {
        Comparator<PlayerData> comparator = Comparator.comparingInt(player -> player.getData(PlayerData.UserDataType.valueOf(top)));
        return playerDataHashMap.values().stream()
                .map(player -> player)
                .sorted(comparator.reversed())  // reversed comparator to flip order
                .skip((number >= 1 && number <= 5) ? number - 1 : 4)
                .findFirst()
                .orElse(null);
    }

    public PlayerData getTopWinrate(int number) {
        Comparator<PlayerData> comparator = Comparator.comparingDouble(PlayerData::winRate);
        return playerDataHashMap.values().stream()
                .map(player -> player)
                .sorted(comparator.reversed())  // reversed comparator to flip order
                .skip((number >= 1 && number <= 5) ? number - 1 : 4)
                .findFirst()
                .orElse(null);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        playerDataHashMap.put(e.getPlayer().getUniqueId(), PlayerData.create(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        PlayerData pd = playerDataHashMap.get(e.getPlayer().getUniqueId());
        if(pd != null) {
            pd.saveData();
        }
    }

    public void loadAllPlayers() {
        for (OfflinePlayer p : Bukkit.getOfflinePlayers())
            playerDataHashMap.put(p.getUniqueId(), PlayerData.create(p.getUniqueId()));
    }
}
