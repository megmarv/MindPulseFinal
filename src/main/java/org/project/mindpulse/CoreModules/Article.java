package org.project.mindpulse.CoreModules;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Article {

    private int articleId;
    private int categoryId; // Foreign key from the database
    private String title;
    private String authorName;
    private String content;
    private Date dateOfPublish;

    private Category category; // aggregation pointer

    private List<ArticleRecord> articleHistory;

    public Article(int articleId, int categoryId, String title, String authorName, String content, Date dateOfPublish) {
        this.articleId = articleId;
        this.categoryId = categoryId;
        this.title = title;
        this.authorName = authorName;
        this.content = content;
        this.dateOfPublish = dateOfPublish;
        this.articleHistory = new ArrayList<>();
    }

    // Set the associated Category object based on categoryId
    public void setCategory(Category category) {
        if (category.getCategoryID() == this.categoryId) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("Category ID mismatch");
        }
    }

    public void addArticleRecord(ArticleRecord record) {
        articleHistory.add(record);
    }

    public List<ArticleRecord> getArticleHistory() {
        return articleHistory;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateOfPublish() {
        return dateOfPublish;
    }

    public void setDateOfPublish(Date dateOfPublish) {
        this.dateOfPublish = dateOfPublish;
    }
}
