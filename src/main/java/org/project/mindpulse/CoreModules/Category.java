package org.project.mindpulse.CoreModules;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String categoryName;
    private int categoryID;
    private String categoryDescription;

    private static final List<Category> categories = new ArrayList<>();  // Static list to hold predefined categories
    private  List<Article> articlesForThisCategory = new ArrayList<>();  // aggregation between category and article

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

    public Category() { }

    public String getCategoryName() {
        return categoryName;
    }

    public void addArticle(Article article) {
        articlesForThisCategory.add(article);
        article.setCategory(this);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public List<Article> getArticlesForThisCategory() {
        return articlesForThisCategory;
    }
}
