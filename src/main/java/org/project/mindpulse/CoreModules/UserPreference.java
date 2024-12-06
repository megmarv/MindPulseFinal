package org.project.mindpulse.CoreModules;

public class UserPreference {

    private int categoryId;
    private int likes;
    private int dislikes;
    private int nullInteractions;
    private double timeSpent;
    private double normalizedScore;

    public UserPreference(int categoryId, int likes, int dislikes, int nullInteractions, double timeSpent, double normalizedScore) {
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.nullInteractions = nullInteractions;
        this.timeSpent = timeSpent;
        this.normalizedScore = normalizedScore;
    }

    public double getNormalizedScore() {
        return normalizedScore;
    }

    public void setNormalizedScore(double normalizedScore) {
        this.normalizedScore = normalizedScore;
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
