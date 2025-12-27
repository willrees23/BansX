package com.github.wand.util;

public class DurationUtil {
    public static long parseDuration(String duration) {
        if ("permanent".equalsIgnoreCase(duration)) {
            return 0;
        }
        if (duration.length() < 2) {
            throw new IllegalArgumentException("Invalid duration: " + duration);
        }
        long num;
        try {
            num = Long.parseLong(duration.substring(0, duration.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration number: " + duration);
        }
        char unit = duration.charAt(duration.length() - 1);
        return switch (unit) {
            case 'm' -> num * 60 * 1000;
            case 'h' -> num * 60 * 60 * 1000;
            case 'd' -> num * 24 * 60 * 60 * 1000;
            case 'w' -> num * 7 * 24 * 60 * 60 * 1000;
            default -> throw new IllegalArgumentException("Unknown duration unit: " + unit);
        };
    }
}
