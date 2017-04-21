package cvut.cz.dp.nss.util;

import java.util.Calendar;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */

public class DateTimeUtil {

    public static String getDateString(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
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

}
