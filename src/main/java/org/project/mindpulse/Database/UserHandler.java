package org.project.mindpulse.Database;

import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // Method to populate user history
    public static void populateUserHistory(User user) {
        String query = "SELECT * FROM ArticleInteractions WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            boolean liked;
            boolean disliked;

            while (resultSet.next()) {
                int interactionId = resultSet.getInt("interactionId");
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                int userId = resultSet.getInt("userId");
                String rating = resultSet.getString("rating");

                // Initialize booleans based on rating
                liked = false;
                disliked = false;

                if (rating.equals("liked")) {
                    liked = true;
                } else if (rating.equals("disliked")) {
                    disliked = true;
                }

                long timeTaken = resultSet.getLong("timeTaken");

                // Create ArticleRecord object
                ArticleRecord record = new ArticleRecord(interactionId, articleId, categoryId, userId, liked, disliked, timeTaken);

                // Use the addArticleRecord method to associate the record with the user
                user.addArticleRecord(record);  // This method will add the record to the user's history
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to return a list of Article objects for the Recommendation Engine
    public static List<Article> getUserArticlesForRecommendation(User user) {
        List<Article> userArticles = new ArrayList<>();
        String query = "SELECT ai.articleId, ai.categoryId, a.title, a.authorName, a.content, a.dateOfPublish, a.linkToArticle " +
                "FROM ArticleInteractions ai " +
                "JOIN Articles a ON ai.articleId = a.articleId " +
                "WHERE ai.userId = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                String title = resultSet.getString("title");
                String authorName = resultSet.getString("authorName");
                String content = resultSet.getString("content");
                Date dateOfPublish = resultSet.getDate("dateOfPublish");
                String linkToArticle = resultSet.getString("linkToArticle");

                // Create an Article object with the retrieved data
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish, linkToArticle);

                // Add the Article object to the list
                userArticles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userArticles;
    }


}
