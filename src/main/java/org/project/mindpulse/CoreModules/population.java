package org.project.mindpulse.CoreModules;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class population{

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    // Method to populate user history
    public void populateUserHistory(User user) {
        String query = "SELECT * FROM ArticleInteractions WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int interactionId = resultSet.getInt("interactionId");
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                int userId = resultSet.getInt("userId");
                String rating = resultSet.getString("rating");
                long timeTaken = resultSet.getLong("timeTaken");

                // Create ArticleRecord object
                ArticleRecord record = new ArticleRecord(interactionId, articleId, categoryId, userId, rating, timeTaken);

                // Use the addArticleRecord method to associate the record with the user
                user.addArticleRecord(record);  // This method will add the record to the user's history
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to populate all user preferences
    public void populateUserPreferences(User user) {
        String query = """
    SELECT 
        categoryid,
        COUNT(CASE WHEN rating = 'like' THEN 1 END) AS likes,
        COUNT(CASE WHEN rating = 'dislike' THEN 1 END) AS dislikes,
        COUNT(CASE WHEN rating = 'none' THEN 1 END) AS nullratings
    FROM ArticleInteractions
    WHERE userid = ?
    GROUP BY categoryid;
    """;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Use the userId directly from the User object
            statement.setInt(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();

            // Directly updating the provided User object
            while (resultSet.next()) {
                int categoryId = resultSet.getInt("categoryid");
                int likes = resultSet.getInt("likes");
                int dislikes = resultSet.getInt("dislikes");
                int nullRatings = resultSet.getInt("nullratings");

                // Add the preference to the provided User object
                user.addPreference(categoryId, likes, dislikes, nullRatings);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to populate article history
    public void populateArticleHistory(Article article) {
        String query = "SELECT * FROM ArticleRecords WHERE articleId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, article.getArticleId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int interactionId = resultSet.getInt("interactionId");
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                int userId = resultSet.getInt("userId");
                String rating = resultSet.getString("rating");
                long timeTakenMillis = resultSet.getLong("timeTaken");

                // Create an ArticleRecord based on the result set values
                ArticleRecord record = new ArticleRecord(interactionId, articleId, categoryId, userId,rating, timeTakenMillis);

                // Use the addArticleRecord method from the Article class to add the record
                article.addArticleRecord(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to populate articles for a specific category
    public void populateArticlesForCategory(Category category) {
        String query = "SELECT * FROM Articles WHERE categoryId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, category.getCategoryID());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                String title = resultSet.getString("title");
                String authorName = resultSet.getString("authorName");
                String content = resultSet.getString("content");
                Date dateOfPublish = resultSet.getDate("dateOfPublish");

                // Create Article object
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);

                // Use the addArticle method to associate the article with the category
                category.addArticle(article);  // This method will add the article to the category's list and set the category
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}