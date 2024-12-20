package org.project.mindpulse.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.Category;
import org.project.mindpulse.CoreModules.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleHandler extends DatabaseConnection {

    // Method to save an article interaction into the database
    public void saveInteractionToDatabase(ArticleRecord interaction) {
        String query = """
        INSERT INTO ARTICLEINTERACTIONS(articleid, categoryid, userid, rating, timetaken)
        VALUES (?, ?, ?, ?, ?::interval)
    """;

        try (Connection connection = connect();
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

    // Helper method to find a Category by its ID
    public static Category findCategoryById(int categoryId) {
        for (Category category : Category.getCategories()) {
            if (category.getCategoryID() == categoryId) {
                return category;
            }
        }
        return null;
    }

    public static int getCategoryIdByName(String name) {
        System.out.println("Searching for category: " + name);
        for (Category category : Category.getCategories()) {
            if (category.getCategoryName().equalsIgnoreCase(name.trim())) {
                System.out.println("Found category: " + category.getCategoryName() + " with ID: " + category.getCategoryID());
                return category.getCategoryID();
            }
        }
        System.out.println("Category not found: " + name);
        return -1;
    }

    // Refactor of getArticlesForTableView() method to correctly handle the ResultSet and add articles to static list
    public static ObservableList<Article> getArticlesForTableView() {
        System.out.println("Retrieving all articles from the database...");

        String query = "SELECT * FROM articles"; // Query to retrieve all articles
        ObservableList<Article> observableArticleList = FXCollections.observableArrayList();

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) { // Execute the query

            while (rs.next()) {
                int articleId = rs.getInt("articleid");
                int categoryId = rs.getInt("categoryid");
                String title = rs.getString("title");
                String authorName = rs.getString("authorname");
                String content = rs.getString("content");
                Date dateOfPublish = rs.getDate("dateofpublish");

                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);
                observableArticleList.add(article); // Add the article to the observable list
                System.out.println("Added Article: " + article); // Log each added article
            }

            System.out.println("Total articles retrieved: " + observableArticleList.size()); // Log total number of articles

        } catch (SQLException e) {
            e.printStackTrace(); // Handle any SQL exceptions
        }

        return observableArticleList; // Return the observable list
    }


    public static List<Article> getArticlesForCategory(int categoryId) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM Articles WHERE CategoryID = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("ArticleID");
                String title = resultSet.getString("Title");
                String authorName = resultSet.getString("AuthorName");
                String content = resultSet.getString("Content");
                Date dateOfPublish = resultSet.getDate("DateOfPublish");

                // Create an Article object and add it to the list
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching articles for category ID " + categoryId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }

    public static void populateUserHistory(User user) {
        String query = "SELECT * FROM ArticleInteractions WHERE userId = ?";
        try (Connection connection = connect();
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


    public static List<Article> getUserArticlesForRecommendationEngine(User user) {
        List<Article> userArticles = new ArrayList<>();
        String query = """
        SELECT ai.articleId, ai.categoryId, a.title, a.authorName, a.content, a.dateOfPublish
        FROM ArticleInteractions ai
        JOIN Articles a ON ai.articleId = a.articleId
        WHERE ai.userId = ?
    """;

        try (Connection connection = connect();
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

                // Create an Article object with the retrieved data
                Article article = new Article(articleId, categoryId, title, authorName, content, dateOfPublish);

                // Add the Article object to the list
                userArticles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching articles for recommendation: " + e.getMessage());
            e.printStackTrace();
        }

        return userArticles;
    }


}

