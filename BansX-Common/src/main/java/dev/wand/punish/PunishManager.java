package dev.wand.punish;

import dev.wand.DataConnector;
import dev.wand.data.DataPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PunishManager {

    private final Connection connection;

    public PunishManager(DataConnector connector) {
        this.connection = connector.getConnection();
    }

    /**
     * Add a punishment to the database
     *
     * @param player     The player to punish
     * @param punishment The punishment to add
     * @return Whether the punishment was added successfully
     */
    public boolean addPunishment(DataPlayer player, Punishment punishment) {
        // Add punishment to database
        boolean success = false;
        try {
            PreparedStatement statement = connection.prepareStatement(" " +
                    "INSERT INTO punishments (userId, executorId, server, reason, datetime, expiry, duration, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, player.getUuid());
            statement.setString(2, punishment.getExecutor());
            statement.setString(3, punishment.getServer());
            statement.setString(4, punishment.getReason());
            statement.setLong(5, punishment.getDatetime());
            statement.setLong(6, punishment.getExpiry());
            statement.setLong(7, punishment.getDuration());
            statement.setString(8, punishment.getType().name());

            statement.execute();

            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }
}