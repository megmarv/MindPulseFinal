package org.project.mindpulse.CoreModules;

import java.util.ArrayList;
import java.util.List;

public class User extends Individual {

    private String email;
    private List<ArticleRecord> userHistory = new ArrayList<>();
    private List<UserPreference> userPreferences = new ArrayList<>();

    public User(int userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    public void addArticleRecord(ArticleRecord articleRecord) {
        userHistory.add(articleRecord);
        System.out.println("Added to history: " + articleRecord.toString());
    }

    public List<UserPreference> getAllPreferences() {
        return userPreferences;
    }

    public List<ArticleRecord> getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(List<ArticleRecord> userHistory) {
        this.userHistory = userHistory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserPreference> getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(List<UserPreference> userPreferences) {
        this.userPreferences = userPreferences;
    }

    public boolean hasInteractedWithArticle(int articleId) {
        for (ArticleRecord articleRecord : userHistory) {
            if (articleRecord.getArticleID() == articleId) {
                return true;
            }
        }
        return false;
    }
}
