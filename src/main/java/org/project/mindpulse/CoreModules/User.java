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

    private List<String> preferredCategories = new ArrayList<String>();

    public void addArticleRecord(ArticleRecord articleRecord) {
        userHistory.add(articleRecord);
        // considering articleRecord.setUser(this) but would make it a composition
    }

    public void addPreference(String favouriteCategory) {
        preferredCategories.add(favouriteCategory);
    }

    public void removePreference(String favouriteCategory) {
        preferredCategories.remove(favouriteCategory);
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
    public User(int userId, String name, String email, String username,String password, List<ArticleRecord> articleRecords, List<String> favouriteCategories) {
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

    public List<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }
}
