package dev.wand.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {

    public String millisToTime(long millis) {
        // Convert milliseconds to time
        // only show hours if there are any, etc.
        if (millis == -1) {
            return "Permanent";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        // return as string of xd, yh, zm, sn
        return (days > 0 ? days + "d " : "") + (hours % 24 > 0 ? hours % 24 + "h " : "") + (minutes % 60 > 0 ? minutes % 60 + "m " : "") + (seconds % 60 > 0 ? seconds % 60 + "s" : "");
    }

    public long timeToMillis(String time) {
        // Convert time to milliseconds
        // e.g. 1d, 1h, 1m, 1s
        if (time == null || time.isEmpty() || time.isBlank()) {
            return -1;
        }

        long millis = 0;
        String[] split = time.split(" ");
        for (String s : split) {
            char unit = s.charAt(s.length() - 1);
            int value = Integer.parseInt(s.substring(0, s.length() - 1));
            switch (unit) {
                case 'y':
                    millis += (long) value * 365 * 24 * 60 * 60 * 1000;
                    break;
                case 'w':
                    millis += (long) value * 7 * 24 * 60 * 60 * 1000;
                    break;
                case 'd':
                    millis += (long) value * 24 * 60 * 60 * 1000;
                    break;
                case 'h':
                    millis += (long) value * 60 * 60 * 1000;
                    break;
                case 'm':
                    millis += (long) value * 60 * 1000;
                    break;
                case 's':
                    millis += value * 1000L;
                    break;
            }
        }
        return millis;
    }

    // method that turns a long of milliseconds, into the difference between the current time and the given time

    /**
     * Converts milliseconds to a difference between the current time and the given time.
     * Example: Time = 1000, Current Time = 2000, Difference = 1000, returns "1s"
     *
     * @param millis The time in milliseconds
     * @return The difference between the current time and the given time
     */
    public String millisToDifference(long millis) {
        long difference = System.currentTimeMillis() - millis;
        return millisToTime(difference);
    }
}
