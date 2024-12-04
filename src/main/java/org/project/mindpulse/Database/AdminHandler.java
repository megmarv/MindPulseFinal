package org.project.mindpulse.Database;

import java.sql.*;

public class AdminHandler {

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    public boolean adminExists(String username) {
        String query = "SELECT COUNT(*) FROM admins WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Return true if at least one user exists with the given username
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if user does not exist or an error occurs
    }

    // method to check if the password is correct for a given username
    public boolean passwordCorrect(String username, String password) {
        String query = "SELECT password FROM admins WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password); // Check if the stored password matches the provided password
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if the password does not match or an error occurs
    }


}
