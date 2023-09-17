package cz.gennario.newrotatingheads.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class TimeUtils {

    public static boolean isBetweenDates(Date date, Date dateStart, Date dateEnd) {
        if (date != null && dateStart != null && dateEnd != null) {
            return date.after(dateStart) && date.before(dateEnd);
        }
        return false;
    }

    public static long calculateRemainTime(Date date, TimeUnit timeUnit){
        java.util.Date currentDate = new Date();
        try {
            long diffInMillies = date.getTime() - currentDate.getTime();
            return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int decodeTime(String timeString) {
        int time = 0;
        if (timeString.contains("+")) {
            for (String s : timeString.split("\\+")) {
                time += decodeString(s);
            }
        } else {
            time += decodeString(timeString);
        }
        return time;
    }

    private static int decodeString(String s) {
        Character character = s.charAt(s.length() - 1);
        String lastLet = character.toString();
        switch (lastLet) {
            case "d":
                return Integer.parseInt(s.replace("d", "")) * (60 * 60 * 24);
            case "h":
                return Integer.parseInt(s.replace("h", "")) * (60 * 60);
            case "m":
                return Integer.parseInt(s.replace("m", "")) * (60);
            case "s":
                return Integer.parseInt(s.replace("s", ""));
        }
        return 0;
    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        int weeks = 0;
        if(day > 7) {
            weeks = (int) Math.floor((double) day/7d);
            day = day-(weeks*7);
        }

        String s = "";
        if (weeks > 0) s += weeks + "w ";
        if (day > 0) s += day + "d ";
        if (hours > 0) s += hours + "h ";
        if (minute > 0) s += minute + "m ";
        if (second > 0) s += second + "s";

        return s;
    }

    public static Date dateFromString(String string) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            return formatter.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            return formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date dateFromString(String string, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}