package org.project.mindpulse.Service;

import java.util.List;

public class currentsApiResponse {

    private String status;
    private List<ArticleResponse> news;

    public String getStatus() {
        return status;
    }

    public List<ArticleResponse> getNews() {
        return news;
    }

    public static class ArticleResponse {

        private String id;
        private String title;
        private String description;
        private String author;
        private String published;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPublished() {
            return published;
        }

        public void setPublished(String published) {
            this.published = published;
        }

    }

}
