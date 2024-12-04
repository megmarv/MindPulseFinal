package org.project.mindpulse.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.Database.ArticleHandler;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AddNewArticleController {

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
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        // Assign category
        String category = assignCategory(content);
        if (category == null) {
            showAlert(Alert.AlertType.ERROR, "Category Error", "Unable to determine the article category.");
            return;
        }

        int categoryId = ArticleHandler.getCategoryIdByName(category);
        if (categoryId == -1) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Invalid category: " + category);
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
            ArticleHandler.saveArticleToDatabase(article);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Article added successfully.");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save article: " + e.getMessage());
        }
    }

    private void clearFields() {
        titleTextField.clear();
        authorTextField.clear();
        contentTextField.clear();
        dateField.setValue(null);
    }

    private String assignCategory(String content) {
        String[] sportsKeywords = {"football", "cricket", "basketball", "tournament"};
        String[] entertainmentKeywords = {"movie", "series", "celebrity", "festival"};
        String[] healthKeywords = {"health", "fitness", "medicine", "wellness"};
        String[] businessKeywords = {"economy", "business", "stock", "market"};
        String[] politicsKeywords = {"election", "policy", "government", "politics"};
        String[] educationKeywords = {"school", "university", "education", "learning"};

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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
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

    private void loadScene(ActionEvent event, String fxmlPath, String title, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent window = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(window, width, height));
        stage.show();
    }
}
