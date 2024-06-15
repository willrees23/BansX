package dev.wand;

import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DataConnector {

    private Connection connection;

    public DataConnector() {
        // check for className
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find the JDBC driver.");
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void connect(String location) throws SQLException {
        if (!isConnected()) {
            File dataFolder = new File(location);
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            String url = "jdbc:sqlite:" + location + "\\data.db";

            connection = DriverManager.getConnection(url);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                Logger.printSQLException(e);
            }
        }
    }
}
