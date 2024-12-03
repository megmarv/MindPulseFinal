package org.project.mindpulse.CoreModules;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int userId;
    private String name;
    private String email;
    private String username;
    private String password;

    private List<ArticleRecord> userHistory = new ArrayList<>(); // aggregation between user and ArticleRecord

    // composition relationship
    private List<UserPreferences> preferredCategories = new ArrayList<>();

    public void addArticleRecord(ArticleRecord articleRecord) {
        userHistory.add(articleRecord);
        // Optionally log for debugging
        System.out.println("Added to history: " + articleRecord.toString());
    }

    // Add a new preference
    public void addPreference(int categoryId, int likes, int dislikes, int nullInteractions,double timeSpent) {
        // Check if the category already exists
        for (UserPreferences pref : preferredCategories) {
            if (pref.getCategoryId() == categoryId) {
                System.out.println("Category " + categoryId + " already exists. Use updatePreference instead.");
                return;
            }
        }
        // Add new preference
        UserPreferences newPreference = new UserPreferences(categoryId, likes, dislikes, nullInteractions,timeSpent);
        preferredCategories.add(newPreference);
    }

    // Update an existing preference
    public void updatePreference(int categoryId, int likes, int dislikes, int nullInteractions, double timeSpent) {
        for (UserPreferences pref : preferredCategories) {
            if (pref.getCategoryId() == categoryId) {
                pref.setLikes(pref.getLikes() + likes);
                pref.setDislikes(pref.getDislikes() + dislikes);
                pref.setNullInteractions(pref.getNullInteractions() + nullInteractions);
                pref.setTimeSpent(pref.getTimeSpent() + timeSpent);
                return;
            }
        }
        // Add a new preference if it doesn't exist
        addPreference(categoryId, likes, dislikes, nullInteractions, timeSpent);
    }


    // Get a specific preference
    public UserPreferences getPreference(int categoryId) {
        for (UserPreferences pref : preferredCategories) {
            if (pref.getCategoryId() == categoryId) {
                return pref;
            }
        }
        return null;
    }

    // Get all preferences
    public List<UserPreferences> getAllPreferences() {
        return new ArrayList<>(preferredCategories); // Return a copy to prevent external modification
    }

    // constructor without providing the userHistory and preferredCategories array lists
    public User(int userId, String name, String email, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userHistory = new ArrayList<>();
        this.preferredCategories = new ArrayList<>();
    }

    // constructor providing all the required elements as parameters except for the preferredCategories
    public User(int userId, String name, String email, String username, String password, List<ArticleRecord> articleRecords) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userHistory = articleRecords;
        this.preferredCategories = new ArrayList<>();
    }

    // completely parameterized constructor
    public User(int userId, String name, String email, String username,String password, List<ArticleRecord> articleRecords, List<UserPreferences> favouriteCategories) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userHistory = articleRecords;
        this.preferredCategories = favouriteCategories;
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
