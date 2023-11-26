package nl.mxndarijn.wieisdemol.managers.database;

import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;
import nl.mxndarijn.wieisdemol.WieIsDeMol;
import nl.mxndarijn.wieisdemol.data.ConfigFiles;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseManager implements Listener {

    private static DatabaseManager instance;
    private HashMap<UUID, PlayerData> playerDataHashMap;
    private final Connection connection;
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
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionString = fc.getString("database-connection-string", "");
            String username = fc.getString("database-username", "");
            String password = fc.getString("database-password", "");
            if(password.equalsIgnoreCase("")) {
                connection = DriverManager.getConnection(
                        connectionString,
                        username, null);
            } else {
                connection = DriverManager.getConnection(
                        connectionString,
                        username, password);
            }

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


        }  catch (ClassNotFoundException | SQLException e) {
            Logger.logMessage(LogLevel.FATAL, Prefix.DATABASEMANAGER,"Could not establish connection with database...");
            throw new RuntimeException(e);
        }
    }

    public HashMap<UUID, PlayerData> getPlayerDataHashMap() {
        return playerDataHashMap;
    }

    public Connection getConnection() {
        return connection;
    }

    public PlayerData getPlayerData(UUID uuid) {
        if(playerDataHashMap.containsKey(uuid))
            playerDataHashMap.put(uuid, PlayerData.create(uuid));
        return playerDataHashMap.get(uuid);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        playerDataHashMap.put(e.getPlayer().getUniqueId(), PlayerData.create(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        PlayerData pd = playerDataHashMap.remove(e.getPlayer().getUniqueId());
        if(pd != null) {
            pd.saveData();
        }
    }
}
