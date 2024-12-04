package org.project.mindpulse.Service;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentsApiResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("news")
    private List<ArticleResponse> news;

    public String getStatus() {
        return status;
    }

    public List<ArticleResponse> getNews() {
        return news;
    }

    public static class ArticleResponse {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("author")
        private String author;

        @SerializedName("published")
        private String published;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getAuthor() {
            return author;
        }

        public String getPublished() {
            return published;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setPublished(String published) {
            this.published = published;
        }
    }
}
