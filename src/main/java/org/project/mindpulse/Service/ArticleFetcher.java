package org.project.mindpulse.Service;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class ArticleFetcher {

    private static final String API_KEY = "PEhxuJG5Y6LQQSwv04E55xpRdpUHHdC3rbJXaq_DLlcFVT9h";
    private static final String API_URL = "https://api.currentsapi.services/v1/latest-news?language=en";

    public List<currentsApiResponse.ArticleResponse> fetchArticles() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch articles: " + response.code());
            }

            Gson gson = new Gson();
            currentsApiResponse apiResponse = gson.fromJson(response.body().string(), currentsApiResponse.class);

            return apiResponse.getNews();

        }
    }
}
