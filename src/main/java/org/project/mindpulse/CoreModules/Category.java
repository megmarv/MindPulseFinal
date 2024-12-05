package org.project.mindpulse.CoreModules;

import org.project.mindpulse.Database.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName;
    private int categoryID;
    private String categoryDescription;

    private static final List<Category> categories = new ArrayList<>();  // Static list to hold predefined categories
    private List<Article> articlesForThisCategory = new ArrayList<>();  // Aggregation between category and article

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
