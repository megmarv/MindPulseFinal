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

            while (resultSet.next()) {
                int interactionId = resultSet.getInt("interactionId");
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                boolean liked = resultSet.getString("rating").equalsIgnoreCase("like");
                boolean disliked = resultSet.getString("rating").equalsIgnoreCase("dislike");
                String timeTaken = resultSet.getString("timeTaken");

                ArticleRecord record = new ArticleRecord(interactionId, articleId, categoryId, user.getUserId(), liked, disliked, timeTaken);
                user.addArticleRecord(record);
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


    // Method to populate all user preferences
    public static void populateUserPreferences(User user) {
            String query = """
        SELECT 
            categoryid,
            COUNT(CASE WHEN rating = 'like' THEN 1 END) AS likes,
            COUNT(CASE WHEN rating = 'dislike' THEN 1 END) AS dislikes,
            COUNT(CASE WHEN rating = 'none' THEN 1 END) AS nullratings,
            SUM(EXTRACT(EPOCH FROM timetaken)) AS totalTimeSpent
        FROM ArticleInteractions
        WHERE userid = ?
        GROUP BY categoryid;
    """;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int categoryId = resultSet.getInt("categoryid");
                int likes = resultSet.getInt("likes");
                int dislikes = resultSet.getInt("dislikes");
                int nullRatings = resultSet.getInt("nullratings");
                double timeSpent = resultSet.getDouble("totalTimeSpent");

                user.updatePreference(categoryId, likes, dislikes, nullRatings, timeSpent);
                System.out.println("Updated Preferences: " + user.getAllPreferences());
                System.out.println("Category ID: " + categoryId + ", Time Spent: " + timeSpent + " seconds");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Helper method to parse HH:mm:ss to milliseconds
    private static long parseTimeTakenToMillis(String timeTaken) {
        try {
            String[] parts = timeTaken.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            return (hours * 3600 + minutes * 60 + seconds) * 1000L;
        } catch (Exception e) {
            System.err.println("Error parsing timeTaken: " + timeTaken);
            e.printStackTrace();
            return 0L; // Default to 0 if parsing fails
        }
    }


}
