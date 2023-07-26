package me.zxoir.smp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * MIT License Copyright (c) 2020/2021 Zxoir
 *
 * @author Zxoir
 * @since 10/20/2020
 */
public class TimeManager {

    @Nullable
    public static Long toMilliSecond(String input) {
        input = input.replaceAll("\\s", ""); // Remove white space
        String[] sl = input.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        if (sl.length == 1) {
            if (isLong(input))
                return Long.parseLong(input) * 1000;
            else return null;
        }

        long total = 0;
        long temp = 0;
        for (String key : sl) {

            if (isLong(key)) {
                temp = Long.parseLong(key);
                continue;
            }

            switch (key) {
                case "second":
                case "seconds":
                case "s":
                    total += temp * 1000;
                    break;
                case "minutes":
                case "minute":
                case "m":
                    total += temp * 1000 * 60;
                    break;
                case "hours":
                case "hour":
                case "h":
                    total += temp * 1000 * 60 * 60;
                    break;
                case "days":
                case "day":
                case "d":
                    total += temp * 1000 * 60 * 60 * 24;
                    break;
                case "weeks":
                case "week":
                case "w":
                    total += temp * 1000 * 60 * 60 * 24 * 7;
                    break;
                case "months":
                case "month":
                case "mo":
                    total += temp * 1000 * 60 * 60 * 24 * 30;
                    break;
                default:
                    return null;
            }
        }

        return total;
    }

    @NotNull
    public static String formatTimeWithoutSpace(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes((time % 3600));
        long seconds = TimeUnit.SECONDS.toSeconds(time % 60);

        String timeString;
        if (time >= 86400) { // days
            String day = (days != 1) ? "days" : "day";
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";

            if (hours > 0)
                timeString = days + day + hours + hour;
            else if (minutes == 0)
                timeString = days + day;
            else
                timeString = days + day + minutes + minute;

        } else if (time >= 3600) { // hours
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (minutes > 0)
                timeString = hours + hour + minutes + minute;
            else if (seconds == 0)
                timeString = hours + hour;
            else
                timeString = hours + hour + seconds + second;
        } else if (time >= 60) { // minutes
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (seconds > 0)
                timeString = minutes + minute + seconds + second;
            else
                timeString = minutes + minute;
        } else { // seconds
            String second = (seconds != 1) ? "seconds" : "second";
            timeString = seconds + second;
        }

        return timeString;
    }

    @NotNull
    public static String formatTime(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes((time % 3600));
        long seconds = TimeUnit.SECONDS.toSeconds(time % 60);

        String timeString;
        if (time >= 86400) { // days
            String day = (days != 1) ? "days" : "day";
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";

            if (hours > 0)
                timeString = days + " " + day + " " + hours + " " + hour;
            else if (minutes == 0)
                timeString = days + " " + day;
            else
                timeString = days + " " + day + " and " + minutes + " " + minute;

        } else if (time >= 3600) { // hours
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (minutes > 0)
                timeString = hours + " " + hour + " and " + minutes + " " + minute;
            else if (seconds == 0)
                timeString = hours + " " + hour;
            else
                timeString = hours + " " + hour + " and " + seconds + " " + second;
        } else if (time >= 60) { // minutes
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (seconds > 0)
                timeString = minutes + " " + minute + " and " + seconds + " " + second;
            else
                timeString = minutes + " " + minute;
        } else { // seconds
            String second = (seconds != 1) ? "seconds" : "second";
            timeString = seconds + " " + second;
        }

        return timeString;
    }

    @NotNull
    public static String formatTimeWithoutSeconds(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes((time % 3600));
        long seconds = TimeUnit.SECONDS.toSeconds(time % 60);

        String timeString;
        if (time >= 86400) { // days
            String day = (days != 1) ? "days" : "day";
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";

            if (hours > 0)
                timeString = days + " " + day + " " + hours + " " + hour;
            else if (minutes == 0)
                timeString = days + " " + day;
            else
                timeString = days + " " + day + " and " + minutes + " " + minute;

        } else if (time >= 3600) { // hours
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";
            if (minutes > 0)
                timeString = hours + " " + hour + " and " + minutes + " " + minute;
            else
                timeString = hours + " " + hour;
        } else if (time >= 60) { // minutes
            String minute = (minutes != 1) ? "minutes" : "minute";
            timeString = minutes + " " + minute;
        } else { // seconds
            String second = (seconds != 1) ? "seconds" : "second";
            timeString = seconds + " " + second;
        }

        return timeString;
    }

    @NotNull
    public static String formatTimeShort(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes((time % 3600));
        long seconds = TimeUnit.SECONDS.toSeconds(time % 60);

        String timeString;
        if (time >= 86400) { // days

            if (hours > 0)
                timeString = days + " d " + hours + " h";
            else if (minutes == 0)
                timeString = days + " d";
            else
                timeString = days + " d" + " " + minutes + " min";

        } else if (time >= 3600) { // hours
            if (minutes > 0)
                timeString = hours + " h " + minutes + " min";
            else
                timeString = hours + " h";
        } else if (time >= 60) { // minutes
            timeString = minutes + " min " + seconds + " s";
        } else { // seconds
            timeString = seconds + " s";
        }

        return timeString;
    }

    private static boolean isLong(String key) {
        try {
            Long.parseLong(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
