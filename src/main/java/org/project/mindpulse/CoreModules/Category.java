package org.project.mindpulse.CoreModules;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName;
    private int categoryID;
    private String categoryDescription;

    private static final List<Category> categories = new ArrayList<>();  // Static list to hold predefined categories
    private List<Article> articlesForThisCategory = new ArrayList<>();  // Aggregation between category and article

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";

    public Category(String categoryName, int categoryID, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryID = categoryID;
        this.categoryDescription = categoryDescription;
    }

    // Static block to initialize predefined categories
    static {
        categories.add(new Category("Health", 1, "Latest information on global health"));
        categories.add(new Category("Education", 2, "News and insights on the education sector."));
        categories.add(new Category("Politics", 3, "Political news and discussions."));
        categories.add(new Category("Entertainment", 4, "Movies, music, and celebrity news."));
        categories.add(new Category("Sports", 5, "Sports news, scores, and events."));
        categories.add(new Category("Business", 6, "Business news, trends, and economy."));
    }

    // Method to populate articles for this category
    public void populateArticlesForThisCategory() {
        articlesForThisCategory.clear(); // Clear existing articles

        String query = "SELECT * FROM Articles WHERE CategoryID = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, categoryID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("ArticleID");
                String title = resultSet.getString("Title");
                String authorName = resultSet.getString("AuthorName");
                String content = resultSet.getString("Content");
                Date dateOfPublish = resultSet.getDate("DateOfPublish");

                // Create and add the Article to the list
                Article article = new Article(articleId, categoryID, title, authorName, content, dateOfPublish);
                articlesForThisCategory.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error populating articles for category ID " + categoryID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Article> getArticlesForThisCategory() {
        return articlesForThisCategory;
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
