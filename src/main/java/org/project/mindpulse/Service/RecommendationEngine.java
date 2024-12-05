package org.project.mindpulse.Service;

import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.UserHandler;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.project.mindpulse.Database.ArticleHandler;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEngine {

    private static final int INTERACTION_THRESHOLD = 5;

    public static List<Article> recommendArticles(User user) {
        // Step 1: Retrieve user preferences
        List<UserPreference> preferences = UserHandler.getUserPreferences(user);

        // Step 2: Determine the top category based on NormalizedScore
        if (preferences.isEmpty()) {
            System.out.println("No preferences found for the user. Unable to recommend.");
            return Collections.emptyList();
        }

        preferences.sort(Comparator.comparingDouble(UserPreference::getTotalScore).reversed());
        int topCategoryId = preferences.get(0).getCategoryId();
        System.out.println("Top category for recommendation: " + topCategoryId);

        // Step 3: Fetch articles for the top category
        List<Article> categoryArticles = ArticleHandler.getArticlesForCategory(topCategoryId);

        // Step 4: Fetch user's article history
        List<Article> userHistory = ArticleHandler.getUserArticlesForRecommendation(user);

        // Step 5: Compute similarity and skip already interacted articles
        Map<Article, Double> similarityMap = new HashMap<>();
        for (Article categoryArticle : categoryArticles) {
            if (user.hasInteractedWithArticle(categoryArticle.getArticleId())) {
                continue; // Skip already interacted articles
            }
            double maxSimilarity = 0.0;
            for (Article historyArticle : userHistory) {
                double similarity = findSimilarity(categoryArticle, historyArticle);
                maxSimilarity = Math.max(maxSimilarity, similarity);
            }
            similarityMap.put(categoryArticle, maxSimilarity);
        }

        // Step 6: Sort articles by similarity index in descending order
        List<Article> sortedRecommendations = similarityMap.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Recommended articles for the user:");
        for (Article article : sortedRecommendations) {
            System.out.println("Article ID: " + article.getArticleId() + ", Title: " + article.getTitle());
        }

        return sortedRecommendations;
    }



    private static double findSimilarity(Article article1, Article article2) {
        CosineSimilarity similarity = new CosineSimilarity();
        Map<CharSequence, Integer> vector1 = toVector(article1.getContent());
        Map<CharSequence, Integer> vector2 = toVector(article2.getContent());
        return similarity.cosineSimilarity(vector1, vector2);
    }

    private static Map<CharSequence, Integer> toVector(String content) {
        Map<CharSequence, Integer> vector = new HashMap<>();
        for (String word : content.toLowerCase().split("\\W+")) {
            vector.put(word, vector.getOrDefault(word, 0) + 1);
        }
        return vector;
    }
}
