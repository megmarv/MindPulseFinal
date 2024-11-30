package org.project.mindpulse.CoreModules;

public class ArticleRecord {

    private int interactionId;
    private int articleID;
    private int categoryId;
    private int userId;
    private boolean liked;
    private boolean disliked;
    private long timeTakenMillis;

    public ArticleRecord(int interactionId, int articleID, int categoryId, int userId, String rating, long timeTakenMillis) {
        this.interactionId = interactionId;
        this.articleID = articleID;
        this.categoryId = categoryId;
        this.userId = userId;

        if(this.liked==true){
            rating = "liked";
        }
        else if(this.disliked==true){
            rating = "disliked";
        }
        else{
            rating = "none";
        }

        this.timeTakenMillis = timeTakenMillis;
    }

    public ArticleRecord(int interactionId, int articleID, int categoryId, int userId,long timeTakenMillis) {
        this.interactionId = interactionId;
        this.articleID = articleID;
        this.categoryId = categoryId;
        this.userId = userId;
        this.liked = false;
        this.disliked = false;
        this.timeTakenMillis = timeTakenMillis;
    }

    public int getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(int interactionId) {
        this.interactionId = interactionId;
    }

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public void setTimeTakenMillis(long timeTakenMillis) {
        this.timeTakenMillis = timeTakenMillis;
    }

}
