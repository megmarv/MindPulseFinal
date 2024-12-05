package org.project.mindpulse.Database;

import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class AdminHandler extends DatabaseHandler {

    // Method to check if the admin password is correct
    public boolean isAdminPasswordCorrect(String password) {
        String query = "SELECT password FROM Users WHERE role = 'admin'";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

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



    public static void deleteArticle(int articleId) {
        String query = "DELETE FROM articles WHERE articleid = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, articleId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveArticleToDatabase(Article article) {
        String checkQuery = "SELECT COUNT(*) FROM Articles WHERE Title = ? AND Content = ?";
        String insertQuery = "INSERT INTO Articles (Categoryid, Title, AuthorName, DateOfPublish, Content) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connect()) {
            // Check for duplicates
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, article.getTitle());
                checkStmt.setString(2, article.getContent());
                ResultSet resultSet = checkStmt.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Duplicate article found: " + article.getTitle());
                    return;
                }
            }

            // Insert article
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, article.getCategoryId());
                insertStmt.setString(2, article.getTitle());
                insertStmt.setString(3, article.getAuthorName());
                insertStmt.setDate(4, article.getDateOfPublish());
                insertStmt.setString(5, article.getContent());
                insertStmt.executeUpdate();
                System.out.println("Article inserted: " + article.getTitle());
            }
        } catch (SQLException e) {
            System.err.println("Error saving article to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean deleteUser(int userId) {
        String query = "DELETE FROM Users WHERE UserID = ? AND role = 'user'";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
                return true; // Indicate successful deletion
            } else {
                System.out.println("Deletion failed. Either the user does not exist or is an admin.");
                return false; // Indicate deletion was not successful
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user with UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate an error occurred
        }
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE role = 'user'"; // Filter for users with role 'user'

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String username = rs.getString("username");
                String password = rs.getString("password");

                // Add each user to the list
                User user = new User(userID, name, username, password);
                user.setEmail(email); // Set additional property specific to User
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }



}
