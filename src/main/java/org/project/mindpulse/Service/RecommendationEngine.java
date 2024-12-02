package org.project.mindpulse.Service;

import org.project.mindpulse.Controllers.UserController;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {

    // populate the user history article Record objects to populate user preferences
    // populate a separate list of Article objects from the user history to make comparisons for the recommendations
    // update the user preferences list consistently after every interaction
    // compare all articles for the particular category in the database with articles of the topmost preference
    // category for the user consistently using cosine similarity

    public void initialize() {
        UserHandler.getUserArticlesForRecommendation(UserController.getLoggedInUser());
    }

    private static final int FEATURE_DIMENSION = 4; // Assuming you have 4 features

    private double encodeIsLiked(boolean liked){
        if(liked){
            return 1;
        }
        return 0;
    }

    private double encodeIsDisliked(boolean disliked){
        if(disliked){
            return 1;
        }
        return 0;
    }

    public double[] getFeatureVector(ArticleRecord record) {
        double[] featureVector = new double[FEATURE_DIMENSION];
        featureVector[0] = record.getCategoryId();
        featureVector[1] = encodeIsLiked(record.isLiked());
        featureVector[2] = encodeIsDisliked(record.isDisliked());
        featureVector[3] = record.getTimeTakenMillis();
        return featureVector;
    }

    public double cosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public List<Article> recommendArticles(User user) {
        List<Article> recommendedArticles = new ArrayList<>();
        for (ArticleRecord history : user.getUserHistory()) {
            double[] userHistoryFeatures = getFeatureVector(history);
            for (Article candidateArticle : Article.articleList) {
                if(user.hasInteractedWithArticle(candidateArticle.getArticleId())){
                    continue;
                }
                double[] candidateFeatures = getFeatureVector(candidateArticle);
                double similarity = cosineSimilarity(userHistoryFeatures, candidateFeatures);

                if (similarity > 0.5) {  // Threshold for similarity (adjustable)
                    recommendedArticles.add(candidateArticle);
                }
            }
        }
        return recommendedArticles;
    }


}
