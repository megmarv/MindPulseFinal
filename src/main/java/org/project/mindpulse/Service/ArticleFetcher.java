package org.project.mindpulse.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.project.mindpulse.CoreModules.Article;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
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
            System.out.println("Raw API Response: " + responseBody);

            Gson gson = new GsonBuilder().setLenient().create();
            CurrentsApiResponse apiResponse = gson.fromJson(responseBody, CurrentsApiResponse.class);

            if (apiResponse == null || apiResponse.getNews() == null) {
                throw new NullPointerException("API response or news list is null.");
            }

            List<Article> articles = new ArrayList<>();
            for (CurrentsApiResponse.ArticleResponse articleResponse : apiResponse.getNews()) {
                if (articleResponse == null) continue;

                Article article = new Article(
                        0, // Default article ID
                        -1, // Default category ID (to be updated later)
                        articleResponse.getTitle(),
                        articleResponse.getAuthor() != null ? articleResponse.getAuthor() : "Unknown Author",
                        articleResponse.getDescription(),
                        parsePublishedDate(articleResponse.getPublished())
                );

                ArticleSorter.assignCategory(article);
                articles.add(article);

                System.out.println("Processed Article: " + article.getTitle());
            }

            return articles;
        } catch (IOException e) {
            throw new RuntimeException("Error while making API call: " + e.getMessage(), e);
        }
    }

    private Date parsePublishedDate(String publishedDate) {
        if (publishedDate == null || publishedDate.isEmpty()) {
            return new Date(System.currentTimeMillis());
        }

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return new Date(apiDateFormat.parse(publishedDate).getTime());
        } catch (ParseException e) {
            System.err.println("Failed to parse published date: " + publishedDate + ". Using current date instead.");
            return new Date(System.currentTimeMillis());
        }
    }
}
