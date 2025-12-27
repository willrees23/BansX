package com.github.wand.util;

public class TimeUtil {
    public static String formatTimeAgo(long ms) {
        if (ms < 1000) return "just now";
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return seconds + "s ago";
    }

    public static String formatDuration(long ms) {
        if (ms <= 0) return "Permanent";
        long days = ms / (24 * 60 * 60 * 1000);
        ms %= (24 * 60 * 60 * 1000);
        long hours = ms / (60 * 60 * 1000);
        ms %= (60 * 60 * 1000);
        long minutes = ms / (60 * 1000);
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        return sb.toString().trim();
    }

    public static String formatRemainingTime(long ms) {
        if (ms <= 0) return "permanently";
        long days = ms / (24 * 60 * 60 * 1000);
        ms %= (24 * 60 * 60 * 1000);
        long hours = ms / (60 * 60 * 1000);
        ms %= (60 * 60 * 1000);
        long minutes = ms / (60 * 1000);
        ms %= (60 * 1000);
        long seconds = ms / 1000;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s ");
        return sb.toString().trim();
    }
}
