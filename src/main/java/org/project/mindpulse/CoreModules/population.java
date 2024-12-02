package org.project.mindpulse.CoreModules;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class population{

    private static final String URL = "jdbc:postgresql://localhost:5432/MindPulse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "aaqib2004";



    // Method to populate article history
    public void populateArticleHistory(Article article) {
        String query = "SELECT * FROM ArticleRecords WHERE articleId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, article.getArticleId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int interactionId = resultSet.getInt("interactionId");
                int articleId = resultSet.getInt("articleId");
                int categoryId = resultSet.getInt("categoryId");
                int userId = resultSet.getInt("userId");
                String rating = resultSet.getString("rating");
                long timeTakenMillis = resultSet.getLong("timeTaken");

                // Initialize booleans based on rating
                boolean liked = false;
                boolean disliked = false;

                if (rating.equals("liked")) {
                    liked = true;
                } else if (rating.equals("disliked")) {
                    disliked = true;
                }

                // Create an ArticleRecord based on the result set values
                ArticleRecord record = new ArticleRecord(interactionId, articleId, categoryId, userId, liked, disliked, timeTakenMillis);

                // Use the addArticleRecord method from the Article class to add the record
                article.addArticleRecord(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
