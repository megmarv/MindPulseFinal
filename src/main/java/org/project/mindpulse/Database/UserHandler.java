package org.project.mindpulse.Database;

import javafx.scene.chart.PieChart;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.CoreModules.UserPreference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserHandler extends DatabaseHandler {

    // Method to check if a user exists for a given username
    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ? AND role = 'user'";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Return true if a user with role 'user' exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists for username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Return false if user does not exist or an error occurs
    }

    // Method to check if the password is correct for a given username
    public boolean passwordCorrect(String username, String password) {
        String query = "SELECT password FROM Users WHERE username = ? AND role = 'user'";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password); // Check if the stored password matches the provided password
            }
        } catch (SQLException e) {
            System.err.println("Error checking password for username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Return false if the password does not match or an error occurs
    }

    // Method to create a new user in the database
    public boolean createUser(String name, String email, String username, String password) {
        String query = "INSERT INTO Users (name, email, username, password, role) VALUES (?, ?, ?, ?, 'user')";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, username);
            statement.setString(4, password);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Return true if the user was successfully created
        } catch (SQLException e) {
            System.err.println("Error creating user with username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Return false if an error occurs
    }

    // Method to get user details based on the username
    public static User getUserDetails(String username) {
        String query = "SELECT * FROM Users WHERE username = ? AND role = 'user'";
        User user = null;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                // Create a User object with the retrieved details
                user = new User(userId, name, email, username, password);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user details for username " + username + ": " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    // Method to populate all user preferences
    public static List<UserPreference> getUserPreferences(User user) {
        List<UserPreference> userPreferences = new ArrayList<>();
        String query = """
        SELECT CategoryID, Likes, Dislikes, NullInteractions, TimeSpent, NormalizedScore
        FROM UserPreferences
        WHERE UserID = ?;
    """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int categoryId = resultSet.getInt("CategoryID");
                int likes = resultSet.getInt("Likes");
                int dislikes = resultSet.getInt("Dislikes");
                int nullInteractions = resultSet.getInt("NullInteractions");
                double timeSpent = resultSet.getDouble("TimeSpent");
                double totalScore = resultSet.getDouble("NormalizedScore");

                // Create a UserPreference object and add it to the list
                userPreferences.add(new UserPreference(categoryId, likes, dislikes, nullInteractions, timeSpent, totalScore));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user preferences for user ID " + user.getUserId() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return userPreferences;
    }
}
