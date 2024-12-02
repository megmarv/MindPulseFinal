package org.project.mindpulse.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Service.RecommendationEngine;

import java.util.ArrayList;
import java.util.List;

public class HomeController extends ArticleHandler{

    @FXML private Label contentHeader;
    @FXML private WebView webview;
    @FXML private Button nextArticleButton;

    @FXML private Label user;

    @FXML private Button sports;
    @FXML private Button entertainment;
    @FXML private Button business;
    @FXML private Button politics;
    @FXML private Button health;
    @FXML private Button education;
    @FXML private Button home; // Added button for Home category

    @FXML private Button profile;
    @FXML private Button thumbsUp;  // Thumbs Up button
    @FXML private Button thumbsDown; // Thumbs Down button

    private List<Article> currentArticles = new ArrayList<>();
    private int currentArticleIndex = 0; // To track the current article

    @FXML
    public void initialize() {
    }

    public void updateUserLabel(String userName) {
        user.setText(userName);
    }


    // Reset thumbs buttons when a new article is displayed
    private void resetThumbsButtons() {
        thumbsUp.setDisable(false);
        thumbsDown.setDisable(false);
    }

    public int interactionCount = 0;

    @FXML
    public void nextArticle(ActionEvent event) {

        if (!currentArticles.isEmpty()) {
            // Record interaction for the current article
            Article currentArticle = currentArticles.get(currentArticleIndex);
            recordInteraction(currentArticle);

            // Move to the next article in the filtered list
            currentArticleIndex = (currentArticleIndex + 1) % currentArticles.size();
            displayArticle(currentArticles.get(currentArticleIndex));

            interactionCount++;

        }

        // Reset thumbs buttons for the new article
        resetThumbsButtons();
    }

    @FXML
    public void handleCategorySelection(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        String categoryText = selectedButton.getText().toLowerCase();

        // Map the button text to category IDs or "recommended" for the Home button
        int categoryId = categoryText.equals("home") ? 0 : getCategoryIdByName(categoryText);

        if (categoryId != -1) {
            // Filter the articles by the selected category or fetch recommended articles for Home
            if (categoryId == 0) {
                filterRecommendedArticles(); // Home category logic for recommended articles
            } else {
                filterArticlesByCategory(categoryId);
            }

            if (currentArticles.isEmpty()) {
                contentHeader.setText("No articles found for " + categoryText);
                webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
            } else {
                displayArticle(currentArticles.get(currentArticleIndex)); // Display first article
            }
        }
    }

    @FXML
    public void filterArticlesByCategory(int categoryId) {

        currentArticles.clear();
        populateArticlesForCategory(categoryId);

        // add all articles for this category to the currentArticles list
        currentArticles.addAll(findCategoryById(categoryId).getArticlesForThisCategory());

        currentArticleIndex = 0; // Reset to the first article
    }

    @FXML
    public void filterRecommendedArticles() {
        // Clear the current articles list
        currentArticles.clear();

        // Fetch recommended articles. This can be dynamically based on user data or other logic.
        List<Article> recommendedList = RecommendationEngine.recommendArticles(UserController.getLoggedInUser());

        // Add the recommended articles to the currentArticles list
        currentArticles.addAll(recommendedList);

        // Check if any recommended articles exist
        if (currentArticles.isEmpty()) {
            contentHeader.setText("No recommended articles found.");
            webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
        } else {
            // Display the first recommended article and set the content header
            contentHeader.setText("Recommended Articles");
            displayArticle(currentArticles.get(currentArticleIndex)); // Display first article
        }

        currentArticleIndex = 0; // Reset to the first article
    }


    @FXML
    private void displayArticle(Article article) {

        // Resolve the category name directly from the article
        String category = "Unknown Category"; // Default value

        if (article.getCategory() != null) {
            category = article.getCategory().getCategoryName();
        } else {
            // Optional fallback if category objects are not set
            // Add logic here if you have a way to map categoryId to category name
        }

        // Set the content header
        contentHeader.setText(category);

        java.net.URL cssUrl = getClass().getResource("/org/project/mindpulse/articleStyles.css");

        // HTML content linking to the external stylesheet
        String articleContent = "<html><head>" +
                "<link rel='stylesheet' type='text/css' href='" + cssUrl.toExternalForm() + "' />" +
                "</head><body>" +
                "<h1>" + article.getTitle() + "</h1>" +
                "<p><strong>Author :</strong> " + article.getAuthorName() + "</p>" +
                "<p><strong>Published on :</strong> " + article.getDateOfPublish() + "</p>" +
                "<p class='content'>" + article.getContent() + "</p>" +
                "<a href='" + article.getLinkToArticle() + "' target='_blank' class='article-button'>View the Full Article Here</a>" +
                "</body></html>";

        // Load content into WebView
        webview.getEngine().loadContent(articleContent);

        // implement redirect to website source on desktop browser

    }

    private void recordInteraction(Article article) {
        // Determine the interaction type (default to 'none')
        boolean liked = false;
        boolean disliked = false;

        if (thumbsUp.isDisabled() && !thumbsDown.isDisabled()) {
            liked = true;
        } else if (thumbsDown.isDisabled() && !thumbsUp.isDisabled()) {
            disliked = true;
        }

        User loggedInUser = UserController.getLoggedInUser();
        // Get the logged-in user
        if (loggedInUser == null) {
            System.out.println("No user logged in. Interaction not recorded.");
            return; // Exit if no user is logged in
        }

        // Capture the time taken in milliseconds
        long timeTakenMillis = System.currentTimeMillis() % 100000; // Example duration

        // Prepare the interaction object
        ArticleRecord interaction = new ArticleRecord(
                0, // Interaction ID (auto-generated in DB)
                article.getArticleId(),
                article.getCategoryId(),
                loggedInUser.getUserId(),
                liked,
                disliked,
                timeTakenMillis
        );

        // Log interaction details for debugging
        System.out.println("Recording interaction: " + interaction);

        // Save the interaction to the database
        saveInteractionToDatabase(interaction);
    }



    @FXML
    public void likedArticle(ActionEvent event) {
        thumbsDown.setDisable(true);
    }

    @FXML
    public void dislikedArticle(ActionEvent event) {
        thumbsUp.setDisable(true);
    }

}
