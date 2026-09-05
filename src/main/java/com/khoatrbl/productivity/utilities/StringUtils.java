package com.khoatrbl.productivity.utilities;

public final class StringUtils {
    private StringUtils() {}

    public static String normalizeTitle(String title) {
        if (title == null) {
            return null;
        }

        // Shrink continuous white spaces
        return title.trim().replaceAll("\\s+", " ");
    }

    public static String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        return description
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim()
                .replaceAll("\\n{3,}", "\n\n");
    }
}
