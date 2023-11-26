package nl.mxndarijn.wieisdemol.managers.database;

import nl.mxndarijn.api.logger.LogLevel;
import nl.mxndarijn.api.logger.Logger;
import nl.mxndarijn.api.logger.Prefix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private String userid;

    private Map<UserDataType, Integer> map;

    private PlayerData(UUID uuid) {
        userid = uuid.toString();
        map = new HashMap<>();
        loadData();

    }


    public static PlayerData create(UUID uuid) {
        PlayerData data = new PlayerData(uuid);

        return data;
    }

    public void loadData() {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            String query = "SELECT * FROM userdata WHERE userid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userid);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                for (UserDataType type : UserDataType.values()) {
                    int value = resultSet.getInt(type.toString().toLowerCase());
                    map.put(type, value);
                }
            } else {
                for (UserDataType type : UserDataType.values()) {
                    map.put(type, 0);
                }
                saveData();
            }
        } catch (SQLException e) {
            Logger.logMessage(LogLevel.ERROR,  Prefix.DATABASEMANAGER,"Could not load data of user " + userid);
            e.printStackTrace();
        }
    }

    public void updateData(HashMap<UserDataType, Integer> values) {
        for(UserDataType type : values.keySet()) {
            map.put(type, values.get(type));
        }
        saveData();
    }

    public String getUserid() {
        return userid;
    }

    public Map<UserDataType, Integer> getMap() {
        return map;
    }

    public int getData(UserDataType type) {
        return map.get(type);
    }

    public void saveData() {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            String query = "INSERT INTO userdata (userid, spelerwins, molwins, egowins, gamesplayed) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "spelerwins = VALUES(spelerwins), " +
                    "molwins = VALUES(molwins), " +
                    "egowins = VALUES(egowins), " +
                    "gamesplayed = VALUES(gamesplayed)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userid);

            for (Map.Entry<UserDataType, Integer> entry : map.entrySet()) {
                UserDataType type = entry.getKey();
                int value = entry.getValue();
                statement.setInt(type.ordinal() + 2, value); // Adding 2 to match SQL column indexes
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.logMessage(LogLevel.ERROR,  Prefix.DATABASEMANAGER,"Could not save data");
            e.printStackTrace();
        }
    }

    public enum UserDataType {
        SPELERWINS,
        MOLWINS,
        EGOWINS,
        GAMESPLAYED,
    }
}
