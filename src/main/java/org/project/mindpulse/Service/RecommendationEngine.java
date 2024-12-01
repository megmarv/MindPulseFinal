package org.project.mindpulse.Service;

import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.ArticleRecord;
import org.project.mindpulse.CoreModules.User;
import smile.classification.KNN;
import smile.data.DataFrame;
import smile.math.distance.EuclideanDistance;
import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {

    private KNN<double[]> knnModel;

    // Train model using likes/dislikes as labels
    public KNN<double[]> trainModelForUser(User user) {
        List<ArticleRecord> userHistory = user.getUserHistory();
        if (userHistory.isEmpty()) {
            throw new IllegalArgumentException("User history is empty. Cannot train model.");
        }

        // Prepare features and labels
        double[][] features = prepareFeaturesForUser(userHistory);
        int[] labels = prepareLabelsForUser(userHistory);

        // Train the KNN model using the features and labels
        knnModel = KNN.fit(features, labels, 5); // k=5
        return knnModel;
    }

    // Recommend articles based on user feedback and history
    public int recommendForUser(User user, ArticleRecord record) {
        if (knnModel == null) {
            trainModelForUser(user); // Train if no model exists
        }

        // Transform input into feature vector
        double[] inputFeature = extractFeatures(record);

        // Predict recommendation based on user history and likes/dislikes
        int prediction = knnModel.predict(inputFeature); // 1 for recommend, 0 for not recommend

        // Adjust the recommendation based on user feedback (likes/dislikes)
        if (record.isLiked()) {
            // Boost recommendation for liked articles
            prediction = 1;  // Always recommend if liked
        } else if (!record.isLiked()) {
            // Avoid recommending similar articles for dislikes
            prediction = 0;  // Don't recommend if disliked
        }

        // Add feedback to user history to improve future recommendations
        updateUserHistory(user, record);  // Implement this method to add feedback to the user's history

        return prediction;
    }

    // Add user feedback to their history
    private void updateUserHistory(User user, ArticleRecord record) {
        if (record.isLiked()) {
            user.addArticleRecord(record);  // Add liked article to history
        } else {
            // Optionally update disliked articles to adjust future predictions
           // user.updateHistoryRecord(record);  // Optionally handle disliked records differently
        }

        // Optionally retrain the model with the updated user history after each interaction
        trainModelForUser(user);
    }


    /**
     * Generate recommendations for a user based on their history and a list of articles.
     *
     * @param user User object containing the history and preferences.
     * @param articles List of articles to evaluate for recommendations.
     * @return List of recommended ArticleRecords.
     */
    public List<Article> recommendArticlesForUser(User user, List<Article> articles) {
        // Ensure user has history for making recommendations
        if (user.getUserHistory() == null || user.getUserHistory().isEmpty()) {
            throw new IllegalArgumentException("User history is empty. Cannot make recommendations.");
        }

        // Train the model using the user's interaction history (ArticleRecords)
        trainModelForUser(user);

        // Prepare a list to collect recommended ArticleRecords
        List<Article> recommendedArticles = new ArrayList<>();

        // Loop through the user's history and recommend articles based on the record interactions
        for (ArticleRecord record : user.getUserHistory()) {
            // Recommend the article if the model suggests it
            if (recommendForUser(user, record) == 1) {
                recommendedArticles.add(record); // Add recommended ArticleRecord
            }
        }

        return recommendedArticles;
    }

    /**
     * Prepare features for the user using their article interaction history.
     *
     * @param userHistory List of article interactions (ArticleRecord).
     * @return A double array representing the feature vector for each record.
     */
    private double[][] prepareFeaturesForUser(List<ArticleRecord> userHistory) {
        double[][] features = new double[userHistory.size()][];

        for (int i = 0; i < userHistory.size(); i++) {
            ArticleRecord record = userHistory.get(i);
            features[i] = extractFeatures(record);
        }

        return features;
    }

    /**
     * Prepare labels for the user using their article interaction history.
     *
     * @param userHistory List of article interactions (ArticleRecord).
     * @return An integer array representing the labels (1 = liked, 0 = disliked).
     */
    private int[] prepareLabelsForUser(List<ArticleRecord> userHistory) {
        int[] labels = new int[userHistory.size()];
        for (int i = 0; i < userHistory.size(); i++) {
            labels[i] = userHistory.get(i).isLiked() ? 1 : 0;
        }
        return labels;
    }

    /**
     * Extract feature vector from an ArticleRecord.
     *
     * @param record The article record.
     * @return A double array representing the feature vector.
     */
    private double[] extractFeatures(ArticleRecord record) {
        return new double[] {
                record.getCategoryId(),
                briefContentAnalysis(record),
                record.getTimeTakenMillis() / 1000.0, // Convert to seconds
                record.isLiked() ? 1.0 : 0.0 // Likes/dislikes as binary feature
        };
    }

    /**
     * Analyze the content of the article briefly (mock implementation).
     *
     * @param record The article record.
     * @return A score representing content analysis.
     */
    private double briefContentAnalysis(ArticleRecord record) {
        // Placeholder for content analysis logic.
        // You can integrate NLP features here for advanced content scoring.
        return record.getArticleID() % 10; // Mock: Use articleID as a proxy for content feature
    }
}
