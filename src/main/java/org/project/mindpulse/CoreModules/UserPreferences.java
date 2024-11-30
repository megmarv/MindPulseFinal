package org.project.mindpulse.CoreModules;


public class UserPreferences {

    private int categoryId;
    private int likes;
    private int dislikes;
    private int nullInteractions;

    public UserPreferences(int categoryId, int likes, int dislikes, int nullInteractions) {
        this.categoryId = categoryId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.nullInteractions = nullInteractions;
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