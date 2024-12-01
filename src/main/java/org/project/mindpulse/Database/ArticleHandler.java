package org.project.mindpulse.Database;

import org.project.mindpulse.Controllers.UserController;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.Category;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Service.RecommendationEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleHandler{

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    public int getCategoryIdByName(String categoryName) {
        return switch (categoryName) {
            case "sports" -> 5;
            case "entertainment" -> 4;
            case "education" -> 2;
            case "politics" -> 3;
            case "health" -> 1;
            case "business" -> 6;
            default -> -1; // Invalid category
        };
    }

    // Method to save an article interaction into the database
    public void saveInteractionToDatabase(ArticleRecord interaction) {
        String query = """
        INSERT INTO ARTICLEINTERACTIONS(articleid, categoryid, userid, rating, timetaken)
        VALUES (?, ?, ?, ?, ?::interval)
    """;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, interaction.getArticleID());
            statement.setInt(2, interaction.getCategoryId());
            statement.setInt(3, interaction.getUserId());
            statement.setString(4, interaction.isLiked() ? "like" : interaction.isDisliked() ? "dislike" : "none");

            // Use the formatted interval string
            statement.setString(5, interaction.getTimeTakenAsInterval());

            int rowsInserted = statement.executeUpdate();
            System.out.println("Interaction recorded: " + rowsInserted + " row(s) affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to populate articles for a specific category
    public void populateArticlesForCategory(int categoryId) {
        // Find the corresponding Category instance
        Category category = findCategoryById(categoryId);
        if (category == null) {
            System.out.println("Category with ID " + categoryId + " not found.");
            return;
        }

        String query = "SELECT * FROM Articles WHERE categoryId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("articleId");
                String title = resultSet.getString("title");
                String authorName = resultSet.getString("authorName");
                String content = resultSet.getString("content");
                Date dateOfPublish = resultSet.getDate("dateOfPublish");
                String link = resultSet.getString("linkToArticle");

                // Create an Article object
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish,link);

                // Add the article to the category's list
                category.addArticle(article);
            }
            System.out.println("Articles populated for category: " + category.getCategoryName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to find a Category by its ID
    public Category findCategoryById(int categoryId) {
        for (Category category : Category.getCategories()) {
            if (category.getCategoryID() == categoryId) {
                return category;
            }
        }
        return null;
    }


    public boolean hasUserInteracted(int userId, int articleId) {
        String query = "SELECT COUNT(*) FROM ARTICLEINTERACTIONS WHERE userid = ? AND articleid = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, articleId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // If the count is greater than 0, the user has already interacted with the article
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking user interaction: " + e.getMessage());
            e.printStackTrace();
        }

        return false; // User has not interacted with the article
    }

    // Refactor of retrieveAllArticles() method to correctly handle the ResultSet and add articles to static list
    public static void retrieveAllArticles() {
        System.out.println("Retrieving all articles from the database...");

        String query = "SELECT * FROM articles"; // Query to retrieve all articles

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) { // Execute the query

            // Clear the existing list to avoid duplicates
            Article.articleList.clear();

            // Iterate over the result set and populate the static article list
            while (rs.next()) {
                int articleId = rs.getInt("articleid");
                int categoryId = rs.getInt("categoryid");
                String title = rs.getString("title");
                String authorName = rs.getString("authorname");
                String content = rs.getString("content");
                Date dateOfPublish = rs.getDate("dateofpublish");
                String link = rs.getString("linkToArticle");

                // Create a new Article object and add it to the static list
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish,link);
                Article.articleList.add(article); // Add the article to the static list

                System.out.println("Added Article: " + article); // Log each added article
            }

            System.out.println("Total articles retrieved: " + Article.articleList.size()); // Log total number of articles

        } catch (SQLException e) {
            e.printStackTrace(); // Handle any SQL exceptions
        }
    }

    public List<Article> getRecommendedArticles() {

    }

}
