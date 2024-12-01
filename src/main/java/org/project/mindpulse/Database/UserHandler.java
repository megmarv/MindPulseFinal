package org.project.mindpulse.Database;

import org.project.mindpulse.CoreModules.User;

import java.sql.*;

public class UserHandler {

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    // method to check if a user exists for a given username
    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
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
        String query = "SELECT password FROM Users WHERE username = ?";
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

    // method to create a new user in the database
    public boolean createUser(String name, String email, String username, String password) {
        String query = "INSERT INTO Users (name, email, username, password) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, username);
            statement.setString(4, password);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Return true if the user was successfully created
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if an error occurs
    }

    // method to delete a user from the database
    public boolean deleteUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0; // Return true if the user was successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if an error occurs
    }

    public User getUserDetails(String username) {
        // SQL query to fetch user details based on the username
        String query = "SELECT * FROM Users WHERE username = ?";
        User user = null;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the username parameter in the query
            statement.setString(1, username);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // If the result set contains a row, map the result to a User object
            if (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password"); // Ideally, this should be hashed

                // Create a User object with the retrieved details
                user = new User(userId, name, email, username, password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

}
