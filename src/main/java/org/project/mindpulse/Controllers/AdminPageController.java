package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.CoreModules.Category;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Database.AdminHandler;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminPageController implements GeneralFeatures {

    @FXML
    private TableView<Object> sharedTable; // Generic TableView
    @FXML
    private TableColumn<Object, String> column1;
    @FXML
    private TableColumn<Object, String> column2;
    @FXML
    private TableColumn<Object, String> column3;
    @FXML
    private TableColumn<Object, String> column4;

    @FXML
    private Button viewUsers;
    @FXML
    private Button viewArticles;

    private List<Article> articles = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    @FXML private Button logOut;

    @FXML private Button deleteButton;

    @FXML private Button goTofetchArticles;

    @FXML private ImageView placeholderImage;

    @FXML
    public void initialize() {

        toggleTableView(false);
        deleteButton.setVisible(false);
        // Load data from the database
        articles = ArticleHandler.getArticlesForTableView();
        users = AdminHandler.getAllUsers();

    }

    private void toggleTableView(boolean showTable) {
        placeholderImage.setVisible(!showTable);
        sharedTable.setVisible(showTable);
        deleteButton.setVisible(true);
    }


    // Helper method to configure the TableView for users
    private void configureTableForUsers() {
        column1.setText("User ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("userId"));

        column2.setText("Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));

        column3.setText("Email");
        column3.setCellValueFactory(new PropertyValueFactory<>("email"));

        column4.setText("Username");
        column4.setCellValueFactory(new PropertyValueFactory<>("username"));

        sharedTable.setItems(FXCollections.observableArrayList(users));
    }

    // Helper method to configure the TableView for articles
    private void configureTableForArticles() {
        column1.setText("Article ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("articleId"));

        column2.setText("Category");
        column2.setCellValueFactory(cellData -> {
            Article article = (Article) cellData.getValue();
            String categoryName = Category.getCategories().stream()
                    .filter(category -> category.getCategoryID() == article.getCategoryId())
                    .map(Category::getCategoryName)
                    .findFirst()
                    .orElse("Unknown Category");
            return new SimpleStringProperty(categoryName);
        });

        column3.setText("Author");
        column3.setCellValueFactory(new PropertyValueFactory<>("authorName"));

        column4.setText("Title");
        column4.setCellValueFactory(new PropertyValueFactory<>("title"));

        sharedTable.setItems(FXCollections.observableArrayList(articles));
    }

    @FXML
    public void viewUsers(ActionEvent event) {
        users = FXCollections.observableArrayList(AdminHandler.getAllUsers()); // Reload users from DB
        configureTableForUsers();
        toggleTableView(true);
    }

    @FXML
    public void viewArticles(ActionEvent event) {
        articles = FXCollections.observableArrayList(ArticleHandler.getArticlesForTableView()); // Reload articles from DB
        configureTableForArticles();
        toggleTableView(true);
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
    public void handleDelete(ActionEvent event) {
        Object selectedItem = sharedTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this item?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.setTitle("Confirm Delete");
            confirmationAlert.showAndWait();

            if (confirmationAlert.getResult() == ButtonType.YES) {
                if (selectedItem instanceof User) {
                    User selectedUser = (User) selectedItem;
                    AdminHandler.deleteUser(selectedUser.getUserId());
                    users.remove(selectedUser);

                    // Refresh the TableView
                    configureTableForUsers();
                } else if (selectedItem instanceof Article) {
                    Article selectedArticle = (Article) selectedItem;
                    AdminHandler.deleteArticle(selectedArticle.getArticleId());
                    articles.remove(selectedArticle);

                    // Refresh the TableView
                    configureTableForArticles();
                }

                displayConfirmation("Item deleted successfully!");
            }
        } else {
            displayError("No item selected for deletion.");
        }
    }

    @FXML
    private void redirectToFetchingArticles(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/AddNewArticle.fxml","Admin Portal",1100,600);
    }

    @FXML
    private void backToMain(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/FirstPage.fxml","MindPulse",600,400);
    }

    @FXML
    public void Exit(ActionEvent exit) { Platform.exit(); }

    public void loadScene(ActionEvent event, String fxmlPath, String title, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent window = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(window, width, height));
        stage.show();
    }

}
