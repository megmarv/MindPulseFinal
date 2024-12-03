package org.project.mindpulse.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleSorter {

    private static final Map<String, String> KEYWORDS_TO_CATEGORY = new HashMap<>();

    static {
        KEYWORDS_TO_CATEGORY.put("technology", "Technology");
        KEYWORDS_TO_CATEGORY.put("sports", "Sports");
        KEYWORDS_TO_CATEGORY.put("politics", "Politics");
        KEYWORDS_TO_CATEGORY.put("health", "Health");
        KEYWORDS_TO_CATEGORY.put("business", "Business");
        // Add more mappings as needed...
    }

    public String assignCategory(String title, String description) {
        String combined = title.toLowerCase() + " " + description.toLowerCase();

        for (Map.Entry<String, String> entry : KEYWORDS_TO_CATEGORY.entrySet()) {
            if (combined.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "Uncategorized"; // Default category
    }

}
