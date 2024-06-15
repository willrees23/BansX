package dev.wand;

import lombok.experimental.UtilityClass;

import java.sql.SQLException;

@UtilityClass
public class Logger {

    public void printSQLException(
            SQLException e
    ) {
        System.out.println("Error: " + e.getMessage());
    }
}
