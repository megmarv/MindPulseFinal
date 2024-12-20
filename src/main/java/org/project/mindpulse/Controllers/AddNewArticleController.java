package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.Database.AdminHandler;
import org.project.mindpulse.Database.ArticleHandler;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AddNewArticleController implements GeneralFeatures{

    @FXML private Button addNewArticleButton;
    @FXML private Button manageUsers;
    @FXML private Button viewArticles;
    @FXML private Button logOutButton;

    @FXML private TextField titleTextField;
    @FXML private TextField authorTextField;
    @FXML private TextArea contentTextField;
    @FXML private DatePicker dateField;

    @FXML
    private void addNewArticle(ActionEvent event) {

        String title = titleTextField.getText().trim();
        String author = authorTextField.getText().trim();
        String content = contentTextField.getText().trim();
        LocalDate date = dateField.getValue();

        // Input validation
        if (title.isEmpty() || author.isEmpty() || content.isEmpty() || date == null) {
            displayError("Error, All fields are required");
            return;
        }

        // Assign category
        String category = assignCategory(content);
        if (category == null) {
            displayError("Unable to determine the article category.");
            return;
        }

        int categoryId = ArticleHandler.getCategoryIdByName(category);
        if (categoryId == -1) {
            displayError("Invalid category: " + category);
            return;
        }

        // Create Article object
        Article article = new Article(
                0, // Auto-increment ID in DB
                categoryId,
                title,
                author,
                content,
                Date.valueOf(date)
        );

        // Save to database
        try {
            AdminHandler.saveArticleToDatabase(article);
            displayConfirmation("Article added successfully.");
            clearFields();
        } catch (Exception e) {
            displayError("Database Error, Failed to save article: " + e.getMessage());
        }
    }

    private void clearFields() {
        titleTextField.clear();
        authorTextField.clear();
        contentTextField.clear();
        dateField.setValue(null);
    }

    private String assignCategory(String content) {

        String[] sportsKeywords = {
                "football", "soccer", "cricket", "basketball", "tennis", "hockey", "golf", "rugby",
                "swimming", "athletics", "marathon", "volleyball", "badminton", "cycling", "wrestling",
                "boxing", "skiing", "skating", "surfing", "karate", "taekwondo", "tournament", "championship",
                "olympics", "league", "match", "goal", "score", "team", "coach"
        };

        String[] entertainmentKeywords = {
                "movie", "series", "film", "celebrity", "actor", "actress", "director", "producer",
                "festival", "award", "music", "concert", "album", "theater", "performance", "comedy",
                "drama", "romance", "horror", "blockbuster", "premiere", "documentary", "trailer",
                "streaming", "Netflix", "Disney", "Bollywood", "Hollywood", "show", "cinema"
        };

        String[] healthKeywords = {
                "health", "fitness", "medicine", "wellness", "nutrition", "diet", "exercise", "yoga",
                "mental health", "therapy", "clinic", "hospital", "doctor", "nurse", "disease", "virus",
                "vaccine", "symptoms", "treatment", "surgery", "diagnosis", "immune", "exercise", "workout",
                "hygiene", "prevention", "COVID", "meditation", "healthcare", "recovery", "sleep"
        };

        String[] businessKeywords = {
                "economy", "business", "stock", "market", "investment", "finance", "entrepreneur", "startup",
                "corporate", "company", "revenue", "profit", "loss", "strategy", "management", "leadership",
                "trade", "growth", "merger", "acquisition", "industry", "innovation", "capital", "shareholder",
                "marketing", "sales", "e-commerce", "global", "business plan", "income", "tax"
        };

        String[] politicsKeywords = {
                "election", "policy", "government", "politics", "president", "minister", "legislation", "senate",
                "parliament", "democracy", "voting", "campaign", "law", "rights", "constitution", "political",
                "debate", "diplomacy", "reform", "candidate", "governor", "mayor", "council", "agenda",
                "international", "party", "congress", "administration", "national security", "budget"
        };

        String[] educationKeywords = {
                "school", "university", "education", "learning", "student", "teacher", "classroom", "curriculum",
                "exam", "assignment", "degree", "scholarship", "tuition", "college", "lecture", "syllabus",
                "e-learning", "textbook", "academic", "research", "study", "assessment", "quiz", "online class",
                "graduation", "library", "knowledge", "education policy", "principal", "campus"
        };


        if (containsAny(content, sportsKeywords)) return "Sports";
        if (containsAny(content, entertainmentKeywords)) return "Entertainment";
        if (containsAny(content, healthKeywords)) return "Health";
        if (containsAny(content, businessKeywords)) return "Business";
        if (containsAny(content, politicsKeywords)) return "Politics";
        if (containsAny(content, educationKeywords)) return "Education";

        return null; // Uncategorized
    }

    private boolean containsAny(String content, String[] keywords) {
        for (String keyword : keywords) {
            if (content.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @FXML
    public void displayConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void redirectToAdminPortal(ActionEvent event) throws IOException {
        loadScene(event, "/org/project/mindpulse/AdminPage.fxml", "Admin Portal", 1100, 600);
    }

    @FXML
    private void backToMain(ActionEvent event) throws IOException {
        loadScene(event, "/org/project/mindpulse/FirstPage.fxml", "MindPulse", 600, 400);
    }

    public void loadScene(ActionEvent event, String fxmlPath, String title, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent window = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(window, width, height));
        stage.show();
    }

    public void Exit(ActionEvent event){ Platform.exit();}

}
