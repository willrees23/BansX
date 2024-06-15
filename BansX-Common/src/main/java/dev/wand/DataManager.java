package dev.wand;

import dev.wand.data.DataPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager {

    private final Connection connection;

    public DataManager(DataConnector connector) {
        this.connection = connector.getConnection();
    }

    // removal:
    // private final String id;
    //    private final String punishmentId; // The ID of the punishment that was removed
    //    private final String uuid;
    //
    //    private final String server;
    //
    //    private final String executor;
    //    private final String reason;
    //
    //    private final long datetime;
    public void createTables() {
        String sqlPlayers = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid TEXT PRIMARY KEY, " +
                "last_known_name TEXT, " +
                "ip TEXT, " +
                "last_seen INTEGER" +
                ");";

        String sqlPunishments = "CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY, " +
                "userId INTEGER NOT NULL, " +
                "executorId TEXT NOT NULL, " +
                "server VARCHAR(255) NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "datetime BIGINT NOT NULL, " +
                "expiry BIGINT NOT NULL, " +
                "duration BIGINT NOT NULL, " +
                "type VARCHAR(255) NOT NULL, " +
                "removalId INTEGER, " +
                "FOREIGN KEY (userId) REFERENCES players(id), " +
                "FOREIGN KEY (removalId) REFERENCES Removals(id)" +
                ");";

        String sqlRemovals = "CREATE TABLE IF NOT EXISTS removals (" +
                "id INTEGER PRIMARY KEY, " +
                "executorId TEXT NOT NULL, " +
                "server VARCHAR(255) NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "datetime BIGINT NOT NULL, " +
                "punishmentId INTEGER, " +
                "FOREIGN KEY (executorId) REFERENCES players(uuid), " +
                "FOREIGN KEY (punishmentId) REFERENCES Punishments(id)" +
                ");";

        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            // Create players table
            statement.addBatch(sqlPlayers);

            // Create punishments table with foreign keys
            statement.addBatch(sqlPunishments);

            // Create removals table with foreign keys
            statement.addBatch(sqlRemovals);

            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            Logger.printSQLException(e);
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback in case of exception
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Reset auto-commit to true
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // attempts to find a player via their name, should search last_known_name
    // should return list of all players with that name
    public List<DataPlayer> findPlayerData(String name) {
        String sql = "SELECT * FROM players WHERE last_known_name = ?;";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();
            List<DataPlayer> players = new ArrayList<>();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String ip = resultSet.getString("ip");
                long lastSeen = resultSet.getLong("last_seen");

                DataPlayer player = new DataPlayer(uuid, name, ip, lastSeen);
                players.add(player);
            }
            return players;
        } catch (SQLException e) {
            Logger.printSQLException(e);
        }
        return null;
    }

    public DataPlayer getPlayerData(
            String uuid
    ) {
        String sql = "SELECT * FROM players WHERE uuid = ?;";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("last_known_name");
                String ip = resultSet.getString("ip");
                long lastSeen = resultSet.getLong("last_seen");

                return new DataPlayer(uuid, name, ip, lastSeen);
            }
        } catch (SQLException e) {
            Logger.printSQLException(e);
        }
        return null;
    }

    public DataPlayer getPlayerData(UUID uuid) {
        return getPlayerData(uuid.toString());
    }

    public void savePlayer(
            String uuid,
            String name,
            String ip
    ) {
        String sql = "INSERT OR REPLACE INTO players (uuid, last_known_name, ip, last_seen) VALUES (?, ?, ?, ?);";

        try {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, ip);
            statement.setLong(4, System.currentTimeMillis());

            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            Logger.printSQLException(e);
        }
    }
}
