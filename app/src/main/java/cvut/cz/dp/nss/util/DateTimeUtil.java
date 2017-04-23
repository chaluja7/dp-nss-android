package cvut.cz.dp.nss.util;

import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */

public class DateTimeUtil {

    public static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    /**
     * je thread-safe
     */
    public static final org.joda.time.format.DateTimeFormatter JODA_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_TIME_PATTERN);

    public static String getDateString(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    public static String getDateString(int year, int month, int day) {
        return day + "." + month + "." + year;
    }

    public static String getTimeString(Calendar calendar) {
        return getTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static String getTimeString(int hour, int minute) {
        return hour + ":" + (minute < 10 ? "0" + minute : minute);
    }

    public static String getTimeWithoutSeconds(String time) {
        if(time == null || !time.contains(":")) return time;

        String[] split = time.split(":");
        return split.length == 3 ? split[0] + ":" + split[1] : time;
    }

    public static String getMinutesLabel(int numOfMinutes) {
        if(numOfMinutes == 1) return " minuta";
        if(numOfMinutes >= 2 && numOfMinutes <= 4) return " minuty";
        return " minut";
    }

}
