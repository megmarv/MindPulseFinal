package org.project.mindpulse.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.project.mindpulse.CoreModules.Article;
import org.project.mindpulse.Database.ArticleHandler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class ArticleFetcher {

    private static final String API_KEY = "PEhxuJG5Y6LQQSwv04E55xpRdpUHHdC3rbJXaq_DLlcFVT9h";
    private static final String API_URL = "https://api.currentsapi.services/v1/latest-news?language=en";

    public List<Article> fetchArticles() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch articles: " + response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("Raw API Response: " + responseBody); // Debugging log

            Gson gson = new GsonBuilder().setLenient().create();
            CurrentsApiResponse apiResponse = gson.fromJson(responseBody, CurrentsApiResponse.class);

            List<Article> articles = new ArrayList<>();
            for (CurrentsApiResponse.ArticleResponse articleResponse : apiResponse.getNews()) {
                Date publishedDate = parsePublishedDate(articleResponse.getPublished());

                Article article = new Article(
                        0,
                        -1,
                        articleResponse.getTitle(),
                        articleResponse.getAuthor(),
                        articleResponse.getDescription(),
                        publishedDate
                );

                // Debugging log for parsed articles
                System.out.println("Parsed Article: " + article);

                // Assign category
                String category = ArticleSorter.assignCategory(article);
                if (category != null) {
                    int categoryId = new ArticleHandler().getCategoryIdByName(category);
                    article.setCategoryId(categoryId);
                }

                articles.add(article);
            }

            return articles;
        } catch (IOException e) {
            throw new RuntimeException("Error while making API call: " + e.getMessage(), e);
        }
    }


    /**
     * Parses the published date from the API into a standard Date object.
     *
     * @param publishedDate The date as a string from the API response.
     * @return The parsed Date object or the current date if parsing fails.
     */
    private Date parsePublishedDate(String publishedDate) {
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            java.util.Date utilDate = apiDateFormat.parse(publishedDate);
            return new Date(utilDate.getTime()); // Convert java.util.Date to java.sql.Date
        } catch (ParseException e) {
            System.err.println("Failed to parse published date: " + publishedDate + ". Using current date instead.");
            return new Date(System.currentTimeMillis());
        }
    }

}
