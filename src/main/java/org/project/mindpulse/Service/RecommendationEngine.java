package org.project.mindpulse.Service;

import org.project.mindpulse.Controllers.UserController;
import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import javax.swing.text.AbstractDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {

    // variable to store the category with the highest score in the user preferences
    public static int categoryToRecommend;

    private static final int FEATURE_DIMENSION = 2; // Article content and category

    // method to convert article content to numbers using TF-IDF for the vector
    public static double encodeContent(String content) {
        return content.hashCode();
    }

    public static double[] getFeatureVector(Article article) {
        double[] featureVector = new double[FEATURE_DIMENSION];
        featureVector[0] = article.getCategoryId();
        featureVector[1] = encodeContent(article.getContent());
        return featureVector;
    }

    public static double cosineSimilarity(double[] vector1, double[] vector2) {
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

    public static List<Article> recommendArticles(User user) {

        assumePreferences(UserController.getLoggedInUser());

        List<Article> recommendedArticles = new ArrayList<>();
        List<Article> userHistory = UserHandler.getUserArticlesForRecommendation(user);

        // Retrieve the category by ID
        Category category = Category.getCategories()
                .stream()
                .filter(c -> c.getCategoryID() == categoryToRecommend)
                .findFirst()
                .orElse(null);

        if (category == null) {
            System.out.println("No category found with ID: " + categoryToRecommend);
            return recommendedArticles;
        }

        List<Article> articlesForThisCategory = category.getArticlesForThisCategory();

        for (Article history : userHistory) {
            double[] userHistoryFeatures = getFeatureVector(history);
            for (Article article : articlesForThisCategory) {
                if (user.hasInteractedWithArticle(article.getArticleId())) {
                    continue;
                }
                double[] candidateFeatures = getFeatureVector(article);
                double similarity = cosineSimilarity(userHistoryFeatures, candidateFeatures);

                if (similarity > 0.5) {  // Threshold for similarity (adjustable)
                    recommendedArticles.add(article);
                }
            }
        }
        return recommendedArticles;
    }


    public static void assumePreferences(User user) {
        UserHandler.populateUserHistory(user);
        UserHandler.populateUserPreferences(user);

        double time_weight = 0.4;
        double like_weight = 0.7; // Prioritize likes
        double dislike_weight = -0.5; // Penalize dislikes
        double null_weight = 0.1; // Neutral weight for null interactions

        for (UserPreferences preference : user.getAllPreferences()) {
            preference.setTotalScore(
                    preference.getLikes() * like_weight +
                            preference.getDislikes() * dislike_weight +
                            preference.getNullInteractions() * null_weight +
                            preference.getTimeSpent() * time_weight
            );
            System.out.println("Category: " + preference.getCategoryId() + ", Total Score: " + preference.getTotalScore());
        }

        // Ensure preferences are sorted correctly
        user.getAllPreferences().sort((p1, p2) -> Double.compare(p2.getTotalScore(), p1.getTotalScore()));

        if (!user.getAllPreferences().isEmpty()) {
            categoryToRecommend = user.getAllPreferences().get(0).getCategoryId();
            System.out.println("Category to Recommend: " + categoryToRecommend);
        } else {
            System.out.println("No preferences available for recommendation.");
        }
    }


    // Method to sort the list by totalScore using Stream API
    public static void sortPreferencesByTotalScore(List<UserPreferences> preferredCategories) {
        preferredCategories.sort((p1, p2) -> Double.compare(p2.getTotalScore(), p1.getTotalScore())); // Sort in descending order
    }



}
