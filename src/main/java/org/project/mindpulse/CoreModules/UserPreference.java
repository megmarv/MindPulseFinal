package org.project.mindpulse.CoreModules;

public class UserPreference {

    private int categoryId;
    private int likes;
    private int dislikes;
    private int nullInteractions;
    private double timeSpent;
    private double totalScore;

    double timeWeight = 0.4, likeWeight = 0.7, dislikeWeight = -0.5, nullWeight = 0.1;

    public UserPreference(int categoryId, int likes, int dislikes, int nullInteractions, double timeSpent) {
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.nullInteractions = nullInteractions;
        this.timeSpent = timeSpent;

        // calculating the total score for each category based on likes, dislikes, null interactions and timeTaken with the help of predefined weights
        this.totalScore = likes*likeWeight+dislikes*dislikeWeight+nullInteractions*nullWeight+timeSpent*timeWeight;
    }

    public void addLike(int likes) {
        this.likes += likes;
    }

    public void addDislike(int dislikes) {
        this.dislikes += dislikes;
    }

    public void addNullInteractions(int nullInteractions) {
        this.nullInteractions += nullInteractions;
    }

    public void addTimeSpent(double timeSpent) {
        this.timeSpent += timeSpent;
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
