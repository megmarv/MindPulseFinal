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

public class HomeController extends ArticleHandler {

    @FXML private Label contentHeader;
    @FXML private WebView webview;
    @FXML private Button nextArticleButton;
    @FXML private Label user;
    @FXML private Button backToLobby;

    @FXML private Button sports, entertainment, business, politics, health, education, home; // Category buttons
    @FXML private Button profile, thumbsUp, thumbsDown, visitProfile;

    private List<Article> currentArticles = new ArrayList<>();
    private int currentArticleIndex = 0;
    private long startTime;
    private boolean isViewingRecommended = false;
    private int interactionCount = 0;

    @FXML
    public void initialize() {
        contentHeader.setText("Recommended For You");

        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser != null) {
            if (loggedInUser.getUserHistory().size() >= 5) {
                filterRecommendedArticles();
            } else {
                System.out.println("User must read at least 5 articles before accessing recommendations.");
                webview.getEngine().loadContent("<html><body><p>Read at least 5 articles to get personalized recommendations.</p></body></html>");
            }
        } else {
            System.out.println("No user logged in, Unable to load recommended articles");
            webview.getEngine().loadContent("<html><body><p>Error Loading Content --> UserException </p></body></html>");
        }
    }


    public void updateUserLabel(String userName) {
        user.setText(userName);
    }

    private void resetThumbsButtons() {
        thumbsUp.setDisable(false);
        thumbsDown.setDisable(false);
    }

    private List<Article> filterOutViewedArticles(User user, List<Article> articles) {
        return articles.stream()
                .filter(article -> !user.hasInteractedWithArticle(article.getArticleId()))
                .toList();
    }

    @FXML
    public void moveToNextArticle(ActionEvent event) {
        if (currentArticles.isEmpty()) {
            System.out.println("No articles available.");
            return;
        }

        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("No user logged in.");
            return;
        }

        Article currentArticle = currentArticles.get(currentArticleIndex);
        recordInteraction(currentArticle);

        int startingIndex = currentArticleIndex;
        do {
            currentArticleIndex = (currentArticleIndex + 1) % currentArticles.size();
            if (currentArticleIndex == startingIndex) {
                System.out.println("All articles have been viewed.");
                webview.getEngine().loadContent("<html><body><p>All articles have been viewed.</p></body></html>");
                return;
            }
        } while (loggedInUser.hasInteractedWithArticle(currentArticles.get(currentArticleIndex).getArticleId()));

        displayArticle(currentArticles.get(currentArticleIndex));
        resetThumbsButtons();
    }

    @FXML
    public void handleCategorySelection(ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        String categoryText = selectedButton.getText().toLowerCase();

        int categoryId = categoryText.equals("home") ? 0 : getCategoryIdByName(categoryText);

        if (categoryId == 0) {
            isViewingRecommended = true;
            filterRecommendedArticles();
        } else if (categoryId > 0) {
            isViewingRecommended = false;
            filterArticlesByCategory(categoryId);
        } else {
            System.out.println("Invalid category selected.");
            webview.getEngine().loadContent("<html><body><p>Invalid category selected.</p></body></html>");
        }
    }

    @FXML
    public void filterArticlesByCategory(int categoryId) {
        currentArticles.clear();

        List<Article> categoryArticles = ArticleHandler.getArticlesForCategory(categoryId);

        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser != null) {
            categoryArticles.removeIf(article -> loggedInUser.hasInteractedWithArticle(article.getArticleId()));
        }

        currentArticles.addAll(categoryArticles);

        if (currentArticles.isEmpty()) {
            contentHeader.setText("No articles found for this category.");
            webview.getEngine().loadContent("<html><body><p>No articles available.</p></body></html>");
            System.out.println("No articles found for category ID: " + categoryId);
        } else {
            Category category = ArticleHandler.findCategoryById(categoryId);
            contentHeader.setText(category != null ? category.getCategoryName() : "Category");
            System.out.println("Articles loaded for category ID: " + categoryId);

            currentArticleIndex = 0;
            displayArticle(currentArticles.get(currentArticleIndex));
        }
    }

    @FXML
    public void filterRecommendedArticles() {
        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("No user logged in.");
            webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
            return;
        }

        if (loggedInUser.getUserHistory().size() < 5) {
            System.out.println("User must read at least 5 articles before accessing recommendations.");
            webview.getEngine().loadContent("<html><body><p>Read at least 5 articles to get personalized recommendations.</p></body></html>");
            return;
        }

        List<Article> recommendedArticles = RecommendationEngine.recommendArticles(loggedInUser);

        currentArticles.clear();
        currentArticles.addAll(recommendedArticles);

        if (currentArticles.isEmpty()) {
            contentHeader.setText("No recommended articles found.");
            webview.getEngine().loadContent("<html><body><p>No content available</p></body></html>");
        } else {
            contentHeader.setText("Recommended Articles");
            displayArticle(currentArticles.get(0));
        }
        currentArticleIndex = 0;
    }


    private void recordInteraction(Article article) {
        long timeTakenMillis = System.currentTimeMillis() - this.startTime;

        User loggedInUser = UserController.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("No user logged in. Interaction not recorded.");
            return;
        }

        // Record interaction as null (neither liked nor disliked)
        ArticleRecord interaction = new ArticleRecord(
                0,
                article.getArticleId(),
                article.getCategoryId(),
                loggedInUser.getUserId(),
                !thumbsUp.isDisable() && thumbsDown.isDisable(),
                !thumbsDown.isDisable() && thumbsUp.isDisable(),
                timeTakenMillis
        );

        saveInteractionToDatabase(interaction);
        loggedInUser.addArticleRecord(interaction);

        System.out.println("Interaction recorded for user: " + loggedInUser.getUsername() + " with article ID: " + article.getArticleId());
    }


    @FXML
    private void displayArticle(Article article) {
        this.startTime = System.currentTimeMillis();

        String category = article.getCategory() != null ? article.getCategory().getCategoryName() : "Unknown Category";

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

    @FXML
    public void likedArticle(ActionEvent event) {
        thumbsDown.setDisable(true);
    }

    @FXML
    public void dislikedArticle(ActionEvent event) {
        thumbsUp.setDisable(true);
    }

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

    @FXML
    private void redirectToLobby(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/FirstPage.fxml"));
        Parent MainMenuWindow = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("MindPulse");
        Scene scene = new Scene(MainMenuWindow, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

}
