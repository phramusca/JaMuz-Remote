package phramusca.com.jamuzkids;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTime formatting class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class HelperDateTime {

    public enum DateTimeFormat {
        /**
         * SQL dateTime format ("yyyy-MM-dd HH:mm:ss")
         */
        SQL("yyyy-MM-dd HH:mm:ss"),
        /**
         * Human dateTime format ("dd/MM/yyyy HH:mm:ss")
         */
        HUMAN("dd/MM/yyyy HH:mm:ss"),
        /**
         * File dateTime format ("yyyy-MM-dd--HH-mm-ss")
         */
        FILE("yyyy-MM-dd--HH-mm-ss") ;

        private final String pattern;
        DateTimeFormat(String display) {
            this.pattern = display;
        }

        /**
         * @return pattern
         */
        public String getPattern() {
            return pattern;
        }
    }

    /**
     * @param date given date
     * @param format custom format
     * @param toLocal convert to local ?
     * @return UTC dateTime to custom format
     */
    private static String formatUTC(Date date, String format, boolean toLocal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if(!toLocal) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return simpleDateFormat.format(date);
    }

    /**
     * @param date UTC dateTime
     * @param format given format
     * @param toLocal convert to local ?
     * @return  to desired format
     */
    private static String formatUTC(Date date, DateTimeFormat format, boolean toLocal) {
        return formatUTC(date, format.getPattern(), toLocal);
    }

    /**
     * @param date  UTC dateTime
     * @return  to SQL format
     */
    public static String formatUTCtoSqlUTC(Date date) {
        return formatUTC(date, DateTimeFormat.SQL, false);
    }

    /**
     * @param date UTC dateTime
     * @return to local dateTime in SQL format
     */
    public static String formatUTCtoSqlLocal(Date date) {
        return formatUTC(date, DateTimeFormat.SQL, true);
    }

    /**
     * @param format desired format
     * @return current local dateTime
     */
    public static String getCurrentLocal(DateTimeFormat format) {
        return formatUTC(new Date(), format, true);
    }

    /**
     * @return current UTC dateTime in SQL format.
     */
    public static String getCurrentUtcSql() {
        return formatUTC(new Date(), DateTimeFormat.SQL, false);
    }

    /**
     * @param date UTC dateTime as string
     * @param format above UTC date as string format
     * @return UTC dateTime
     */
    private static Date parseUTC(String date, DateTimeFormat format) {
        if(date.equals("")) {
            return new Date(0);
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format.getPattern());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.parse(date);

        } catch (NumberFormatException | ParseException | ArrayIndexOutOfBoundsException ex) {
            return new Date(0);
        }
    }

    /**
     * @param date SQL formatted UTC dateTime as string
     * @return UTC dateTime
     */
    public static Date parseSqlUtc(String date) {
        return parseUTC(date, DateTimeFormat.SQL);
    }
}
