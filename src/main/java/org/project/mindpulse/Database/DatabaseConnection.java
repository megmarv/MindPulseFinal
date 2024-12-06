package org.project.mindpulse.Database;

import java.sql.*;

public class DatabaseConnection {

    private static final String urlToDatabase = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String databaseUsername = "postgres";
    private static final String databasePassword = "aaqib2004";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(urlToDatabase, databaseUsername, databasePassword);
    }

}