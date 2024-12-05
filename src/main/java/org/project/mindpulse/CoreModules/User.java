package org.project.mindpulse.CoreModules;

import org.project.mindpulse.Controllers.UserController;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int userId;
    private String name;
    private String email;
    private String username;
    private String password;

    private List<ArticleRecord> userHistory = new ArrayList<>(); // composition between user and ArticleRecord
    private List<UserPreference> userPreferences = new ArrayList<>(); // composition between user and UserPreference


    public User(int userId, String name, String email, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userHistory = new ArrayList<>();
        this.userPreferences = new ArrayList<>();
    }

    public void addArticleRecord(ArticleRecord articleRecord) {
        userHistory.add(articleRecord);
        System.out.println("Added to history: " + articleRecord.toString());
    }

    public UserPreference getPreferenceWithCategoryId(int categoryId) {
        for (UserPreference pref : userPreferences) {
            if (pref.getCategoryId() == categoryId) {
                return pref;
            }
        }
        return null;
    }


    public List<UserPreference> getAllPreferences() {
        return userPreferences;
    }

    public List<ArticleRecord> getUserHistory() {
        return userHistory;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserHistory(List<ArticleRecord> userHistory) {
        this.userHistory = userHistory;
    }

    public boolean hasInteractedWithArticle(int articleId){
        for(ArticleRecord articleRecord : userHistory){
            if(articleRecord.getArticleID() == articleId){
                return true;
            }
        }
        return false;
    }



}
