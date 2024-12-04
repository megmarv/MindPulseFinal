package org.project.mindpulse.Service;

import org.project.mindpulse.CoreModules.Article;

import java.util.HashMap;
import java.util.Map;

public class ArticleSorter {

    // Keyword mappings for each category
    private static final Map<String, String[]> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        CATEGORY_KEYWORDS.put("Sports", new String[]{"football", "cricket", "basketball", "tournament"});
        CATEGORY_KEYWORDS.put("Entertainment", new String[]{"movie", "series", "celebrity", "festival"});
        CATEGORY_KEYWORDS.put("Health", new String[]{"health", "fitness", "medicine", "wellness"});
        CATEGORY_KEYWORDS.put("Business", new String[]{"economy", "business", "stock", "market"});
        CATEGORY_KEYWORDS.put("Politics", new String[]{"election", "policy", "government", "politics"});
        CATEGORY_KEYWORDS.put("Education", new String[]{"school", "university", "education", "learning"});
    }

    /**
     * Categorizes an article based on its content using predefined keywords.
     *
     * @param content The content of the article to categorize.
     * @return The category name, or null if no category is matched.
     */
    public static String categorizeArticle(String content) {
        if (content == null || content.isEmpty()) {
            System.err.println("Article content is empty or null. Unable to categorize.");
            return null; // Uncategorized
        }

        for (Map.Entry<String, String[]> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (containsAny(content, entry.getValue())) {
                return entry.getKey(); // Return the category name
            }
        }

        System.out.println("No matching category found for content: " + content);
        return null; // Uncategorized
    }

    /**
     * Checks if the content contains any keywords from the given array.
     *
     * @param content  The content to search.
     * @param keywords An array of keywords to match against.
     * @return True if any keyword is found, false otherwise.
     */
    private static boolean containsAny(String content, String[] keywords) {
        for (String keyword : keywords) {
            if (content.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a category to the article based on its content.
     *
     * @param article The article to categorize.
     * @return The category name, or null if no category is assigned.
     */
    public static String assignCategory(Article article) {
        String category = categorizeArticle(article.getContent());
        if (category != null) {
            System.out.println("Assigned Category: " + category + " to article: " + article.getTitle());
        } else {
            System.out.println("Unable to assign a category to article: " + article.getTitle());
        }
        return category;
    }
}
