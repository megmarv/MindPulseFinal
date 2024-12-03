package org.project.mindpulse.Service;

import org.project.mindpulse.Controllers.UserController;
import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import javax.swing.text.AbstractDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.CosineSimilarity;


public class RecommendationEngine {

    // Variable to store the category with the highest score in the user preferences
    public static int categoryToRecommend;

    public static double findSimilarity(Article article1, Article article2) {
        CosineSimilarity similarity = new CosineSimilarity();

        // Get article content as CharSequences
        CharSequence left = article1.getContent();
        CharSequence right = article2.getContent();

        // Check for null content to avoid exceptions
        if (left == null || right == null) {
            System.err.println("Article content is null. Similarity cannot be calculated.");
            return 0.0;
        }

        // Convert content into term frequency vectors
        Map<CharSequence, Integer> leftVector = toVector(left);
        Map<CharSequence, Integer> rightVector = toVector(right);

        // Calculate and return cosine similarity
        double result = similarity.cosineSimilarity(leftVector, rightVector);
        System.out.println("Cosine Similarity: " + result);

        return result;
    }

    private static Map<CharSequence, Integer> toVector(CharSequence content) {
        Map<CharSequence, Integer> frequencyMap = new HashMap<>();
        // Split content into words and count their occurrences
        for (String word : content.toString().toLowerCase().split("\\W+")) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
        return frequencyMap;
    }

    public static List<Article> recommendArticles(User user) {
        assumePreferences(UserController.getLoggedInUser());

        Map<Article, Double> articleSimilarityMap = new HashMap<>();
        List<Article> userHistory = UserHandler.getUserArticlesForRecommendation(user);

        // Retrieve the category by ID
        Category category = Category.getCategories()
                .stream()
                .filter(c -> c.getCategoryID() == categoryToRecommend)
                .findFirst()
                .orElse(null);

        if (category == null) {
            System.out.println("No category found with ID: " + categoryToRecommend);
            return new ArrayList<>();
        }

        // Get articles for the recommended category
        List<Article> articlesForThisCategory = category.getArticlesForThisCategory();

        // Iterate over user history and category articles
        for (Article history : userHistory) {
            for (Article article : articlesForThisCategory) {
                // Skip articles already interacted with by the user
                if (user.hasInteractedWithArticle(article.getArticleId())) {
                    continue;
                }
                // Compute similarity and store in the map
                double similarity = findSimilarity(article, history);
                articleSimilarityMap.put(article, Math.max(articleSimilarityMap.getOrDefault(article, 0.0), similarity));
            }
        }

        // Sort articles by similarity in descending order
        return articleSimilarityMap.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public static void assumePreferences(User user) {
        // Populate user history and preferences
        UserHandler.populateUserHistory(user);
        UserHandler.populateUserPreferences(user);

        // Define weights for preference calculation
        double time_weight = 0.4;
        double like_weight = 0.7; // Prioritize likes
        double dislike_weight = -0.5; // Penalize dislikes
        double null_weight = 0.1; // Neutral weight for null interactions

        // Calculate total score for each preference
        for (UserPreferences preference : user.getAllPreferences()) {
            preference.setTotalScore(
                    preference.getLikes() * like_weight +
                            preference.getDislikes() * dislike_weight +
                            preference.getNullInteractions() * null_weight +
                            preference.getTimeSpent() * time_weight
            );
            System.out.println("Category: " + preference.getCategoryId() + ", Total Score: " + preference.getTotalScore());
        }

        // Sort preferences by total score in descending order
        user.getAllPreferences().sort((p1, p2) -> Double.compare(p2.getTotalScore(), p1.getTotalScore()));

        // Select the top category to recommend
        if (!user.getAllPreferences().isEmpty()) {
            categoryToRecommend = user.getAllPreferences().get(0).getCategoryId();
            System.out.println("Category to Recommend: " + categoryToRecommend);
        } else {
            System.out.println("No preferences available for recommendation.");
        }
    }
}
