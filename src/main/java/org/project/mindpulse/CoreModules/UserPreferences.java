package org.project.mindpulse.CoreModules;


public class UserPreferences {

    private int categoryId;
    private int likes;
    private int dislikes;
    private int nullInteractions;
    private double timeSpent;
    private double totalScore;

    public UserPreferences(int categoryId, int likes, int dislikes, int nullInteractions,double timeSpent, double totalScore) {
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.nullInteractions = nullInteractions;
        this.timeSpent = timeSpent;
        this.totalScore = totalScore;
    }

    public UserPreferences(int categoryId, int likes, int dislikes, int nullInteractions, double timeSpent) {
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.nullInteractions = nullInteractions;
        this.timeSpent = timeSpent;
        this.totalScore = 0;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getNullInteractions() {
        return nullInteractions;
    }

    public void setNullInteractions(int nullInteractions) {
        this.nullInteractions = nullInteractions;
    }
}
