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

public class ArticleHandler {

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    public static void insertArticle(Article article) {
        String query = "INSERT INTO Articles (articleId, categoryId, title, authorName, dateOfPublish, content) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, article.getArticleId());
            statement.setInt(2, article.getCategoryId());
            statement.setString(3, article.getTitle());
            statement.setString(4, article.getAuthorName());
            statement.setDate(5, article.getDateOfPublish());
            statement.setString(6, article.getContent());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getCategoryIdByName(String categoryName) {
        String query = "SELECT categoryid FROM Categories WHERE LOWER(categoryname) = LOWER(?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("categoryid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Invalid category
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

                // Create an Article object
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);

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

                // Create a new Article object and add it to the static list
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);
                Article.articleList.add(article); // Add the article to the static list

                System.out.println("Added Article: " + article); // Log each added article
            }

            System.out.println("Total articles retrieved: " + Article.articleList.size()); // Log total number of articles

        } catch (SQLException e) {
            e.printStackTrace(); // Handle any SQL exceptions
        }
    }

    public static void deleteArticle(int articleId) {
        String query = "DELETE FROM articles WHERE articleid = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
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

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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


}

