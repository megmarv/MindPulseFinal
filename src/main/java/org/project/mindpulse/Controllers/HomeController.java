package org.project.mindpulse.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.*;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Service.RecommendationEngine;

import java.io.IOException;
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

    private long startTime; // To track the start time for article interactions


    @FXML
    public void initialize() {
        // Set the default header
        contentHeader.setText("Recommended For You");

        // Check if a user is logged in
        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser != null) {
            // Load and display recommended articles for the logged-in user
            filterRecommendedArticles();
        } else {
            // Handle case when no user is logged in (optional)
            System.out.println("No user logged in, Unable to load recommended articles");
            webview.getEngine().loadContent("<html><body><p>Error Loading Content --> UserException </p></body></html>");
        }
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
            Article currentArticle = currentArticles.get(currentArticleIndex);
            recordInteraction(currentArticle);

            // Move to the next article in the current list
            currentArticleIndex = (currentArticleIndex + 1) % currentArticles.size();
            displayArticle(currentArticles.get(currentArticleIndex));

            System.out.println("Moved to next article. Current Index: " + currentArticleIndex);
        } else {
            System.out.println("No more articles to display.");
        }

        resetThumbsButtons();
    }

    private boolean isViewingRecommended = false; // Track if the user is viewing recommended articles



    @FXML
    public void handleCategorySelection(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        String categoryText = selectedButton.getText().toLowerCase();

        // Map the button text to category IDs or "recommended" for the Home button
        int categoryId = categoryText.equals("home") ? 0 : getCategoryIdByName(categoryText);

        if (categoryId != -1) {
            if (categoryId == 0) {
                isViewingRecommended = true; // Viewing recommended articles
                filterRecommendedArticles();
            } else {
                isViewingRecommended = false; // Viewing articles of a specific category
                filterArticlesByCategory(categoryId);
            }

            if (currentArticles.isEmpty()) {
                contentHeader.setText("No articles found for " + categoryText);
                webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
            } else {
                currentArticleIndex = 0; // Reset to the first article
                displayArticle(currentArticles.get(currentArticleIndex));
            }
        }
    }


    @FXML
    public void filterArticlesByCategory(int categoryId) {
        currentArticles.clear();

        // Populate articles for the selected category
        populateArticlesForCategory(categoryId);

        // Add all articles for this category to the currentArticles list
        Category category = findCategoryById(categoryId);
        if (category != null) {
            currentArticles.addAll(category.getArticlesForThisCategory());
        }

        if (currentArticles.isEmpty()) {
            System.out.println("No articles found for category ID: " + categoryId);
        } else {
            System.out.println("Articles loaded for category: " + category.getCategoryName());
        }

        currentArticleIndex = 0; // Reset to the first article
    }

    @FXML
    public void filterRecommendedArticles() {
        currentArticles.clear();
        User loggedInUser = UserController.getLoggedInUser();

        if (loggedInUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        // Get recommended articles (already sorted)
        List<Article> recommendedList = RecommendationEngine.recommendArticles(loggedInUser);

        currentArticles.addAll(recommendedList);

        if (currentArticles.isEmpty()) {
            contentHeader.setText("No recommended articles found.");
            webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
        } else {
            contentHeader.setText("Recommended Articles");
            displayArticle(currentArticles.get(0));
        }

        currentArticleIndex = 0; // Reset to the first article
    }

    @FXML
    private void displayArticle(Article article) {
        this.startTime = System.currentTimeMillis(); // Set the start time when displaying the article

        String category = article.getCategory() != null ? article.getCategory().getCategoryName() : "Unknown Category";

        // Set the content header
        contentHeader.setText(category);

        java.net.URL cssUrl = getClass().getResource("/org/project/mindpulse/articleStyles.css");

        String articleContent = "<html><head>" +
                "<link rel='stylesheet' type='text/css' href='" + cssUrl.toExternalForm() + "' />" +
                "</head><body>" +
                "<h1>" + article.getTitle() + "</h1>" +
                "<p><strong>Author :</strong> " + article.getAuthorName() + "</p>" +
                "<p><strong>Published on :</strong> " + article.getDateOfPublish() + "</p>" +
                "<p class='content'>" + article.getContent() + "</p>" +
                "</body></html>";

        webview.getEngine().loadContent(articleContent);

        System.out.println("Displaying article: " + article.getTitle());
    }


    private void recordInteraction(Article article) {
        long timeTakenMillis = System.currentTimeMillis() - this.startTime; // Calculate time taken for interaction
        boolean liked = !thumbsUp.isDisable() && thumbsDown.isDisable();
        boolean disliked = !thumbsDown.isDisable() && thumbsUp.isDisable();

        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("No user logged in. Interaction not recorded.");
            return;
        }

        ArticleRecord interaction = new ArticleRecord(
                0, // Auto-generated in DB
                article.getArticleId(),
                article.getCategoryId(),
                loggedInUser.getUserId(),
                liked,
                disliked,
                timeTakenMillis
        );

        saveInteractionToDatabase(interaction); // Save to DB
        loggedInUser.addOrUpdatePreference(
                article.getCategoryId(),
                liked ? 1 : 0,
                disliked ? 1 : 0,
                (!liked && !disliked) ? 1 : 0,
                timeTakenMillis / 1000.0
        );

    }

    @FXML
    public void likedArticle(ActionEvent event) {
        thumbsDown.setDisable(true);
    }

    @FXML
    public void dislikedArticle(ActionEvent event) {
        thumbsUp.setDisable(true);
    }

    @FXML private Button visitProfile;

    @FXML
    private void redirectToProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/UserProfile.fxml"));
        Parent MainMenuWindow = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("My Profile");
        Scene scene = new Scene(MainMenuWindow, 798, 450);
        stage.setScene(scene);
        stage.show();
    }

}
