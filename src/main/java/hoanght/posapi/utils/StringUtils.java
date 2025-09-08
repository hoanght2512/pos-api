package hoanght.posapi.utils;

import com.github.slugify.Slugify;

public class StringUtils {
    private static final Slugify SLUGIFY = Slugify.builder().build();
    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        return SLUGIFY.slugify(input);
    }
}
