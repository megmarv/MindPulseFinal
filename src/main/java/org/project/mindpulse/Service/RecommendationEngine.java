package org.project.mindpulse.Service;

import org.project.mindpulse.Controllers.UserController;
import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import javax.swing.text.AbstractDocument;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.CosineSimilarity;

public class RecommendationEngine {

    public static int categoryToRecommend;

    public static double findSimilarity(Article article1, Article article2) {
        CosineSimilarity similarity = new CosineSimilarity();

        // Get article content as CharSequences
        CharSequence left = article1.getContent();
        CharSequence right = article2.getContent();

        if (left == null || right == null) {
            System.err.println("Article content is null. Similarity cannot be calculated.");
            return 0.0;
        }

        Map<CharSequence, Integer> leftVector = toVector(left);
        Map<CharSequence, Integer> rightVector = toVector(right);

        double result = similarity.cosineSimilarity(leftVector, rightVector);
        System.out.println("Calculated similarity: " + result + " for articles: "
                + article1.getArticleId() + " & " + article2.getArticleId());
        return result;
    }

    private static Map<CharSequence, Integer> toVector(CharSequence content) {
        Map<CharSequence, Integer> frequencyMap = new HashMap<>();
        for (String word : content.toString().toLowerCase().split("\\W+")) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
        return frequencyMap;
    }

    public static List<Article> recommendArticles(User user) {
        assumePreferences(user);

        Map<Article, Double> articleSimilarityMap = new HashMap<>();
        List<Article> userHistory = UserHandler.getUserArticlesForRecommendation(user);

        if (userHistory.isEmpty()) {
            System.out.println("User history is empty. Unable to generate recommendations.");
            return Collections.emptyList();
        }

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

        System.out.println("Recommended Articles: " + sortedArticles);
        return sortedArticles;
    }

    public static void assumePreferences(User user) {
        UserHandler.populateUserHistory(user);
        UserHandler.populateUserPreferences(user);

        double timeWeight = 0.4, likeWeight = 0.7, dislikeWeight = -0.5, nullWeight = 0.1;

        for (UserPreferences preference : user.getAllPreferences()) {
            double score = preference.getLikes() * likeWeight +
                    preference.getDislikes() * dislikeWeight +
                    preference.getNullInteractions() * nullWeight +
                    preference.getTimeSpent() * timeWeight;

            preference.setTotalScore(score);
            System.out.println("Category " + preference.getCategoryId() + " Score: " + score);
        }

        user.getAllPreferences().sort(Comparator.comparingDouble(UserPreferences::getTotalScore).reversed());
        if (!user.getAllPreferences().isEmpty()) {
            categoryToRecommend = user.getAllPreferences().get(0).getCategoryId();
            System.out.println("Selected Category for Recommendation: " + categoryToRecommend);
        } else {
            System.out.println("No preferences found for user.");
        }
    }
}