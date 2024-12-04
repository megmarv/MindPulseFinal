package org.project.mindpulse.Service;

import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.UserHandler;
import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEngine {

    public static List<Article> recommendArticles(User user) {
        Map<Article, Double> articleSimilarityMap = new HashMap<>();
        List<Article> userHistory = UserHandler.getUserArticlesForRecommendation(user);

        if (userHistory.isEmpty()) {
            System.out.println("User history is empty. Unable to generate recommendations.");
            return Collections.emptyList();
        }

        int categoryToRecommend = assumePreferences(user);

        Category category = Category.getCategories()
                .stream()
                .filter(c -> c.getCategoryID() == categoryToRecommend)
                .findFirst()
                .orElse(null);

        if (category == null) {
            System.out.println("No category found with ID: " + categoryToRecommend);
            return new ArrayList<>();
        }

        List<Article> articlesForThisCategory = category.getArticlesForThisCategory();

        // Debug: Log articles for this category
        System.out.println("Articles for Category ID " + categoryToRecommend + ":");
        for (Article article : articlesForThisCategory) {
            System.out.println("Article ID: " + article.getArticleId() + ", Title: " + article.getTitle());
        }

        if (articlesForThisCategory.isEmpty()) {
            System.out.println("No articles available for category: " + category.getCategoryName());
            return Collections.emptyList();
        }

        for (Article history : userHistory) {
            for (Article article : articlesForThisCategory) {
                if (user.hasInteractedWithArticle(article.getArticleId())) {
                    continue; // Skip articles already interacted with
                }
                double similarity = findSimilarity(article, history);
                articleSimilarityMap.put(article, Math.max(articleSimilarityMap.getOrDefault(article, 0.0), similarity));
            }
        }

        List<Article> sortedArticles = articleSimilarityMap.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Recommended Articles:");
        for (Article article : sortedArticles) {
            System.out.println("Article ID: " + article.getArticleId() + ", Title: " + article.getTitle());
        }
        return sortedArticles;
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

    public static int assumePreferences(User user) {
        int categoryToRecommend = 0;

        UserHandler.populateUserHistory(user);
        UserHandler.populateUserPreferences(user);

        // Debug: Log all preferences before sorting
        System.out.println("User Preferences Before Sorting:");
        for (UserPreference preference : user.getAllPreferences()) {
            System.out.println("Category ID: " + preference.getCategoryId() + ", Total Score: " + preference.getTotalScore());
        }

        // Sort preferences in descending order of total score
        user.getAllPreferences().sort(Comparator.comparingDouble(UserPreference::getTotalScore).reversed());

        // Debug: Log the sorted preferences
        System.out.println("User Preferences After Sorting:");
        for (UserPreference preference : user.getAllPreferences()) {
            System.out.println("Category ID: " + preference.getCategoryId() + ", Total Score: " + preference.getTotalScore());
        }

        if (!user.getAllPreferences().isEmpty()) {
            categoryToRecommend = user.getAllPreferences().get(0).getCategoryId();
            System.out.println("Selected Category for Recommendation: " + categoryToRecommend);
        } else {
            System.out.println("No preferences found for user.");
        }
        return categoryToRecommend;
    }

}
