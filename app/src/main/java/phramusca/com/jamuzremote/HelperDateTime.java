package phramusca.com.jamuzremote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTime formatting class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class HelperDateTime {
    /**
     * Supported dateTime formats
     */
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
        private DateTimeFormat(String display) {
            this.pattern = display;
        }

        /**
         *
         * @return
         */
        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Format UTC dateTime to custom format
     * @param date
     * @param format
     * @param toLocal
     * @return
     */
    public static String formatUTC(Date date, String format, boolean toLocal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if(!toLocal) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return simpleDateFormat.format(date);
    }

    /**
     * Format UTC dateTime to desired format
     * @param date
     * @param format
     * @param toLocal
     * @return
     */
    public static String formatUTC(Date date, DateTimeFormat format, boolean toLocal) {
        return formatUTC(date, format.getPattern(), toLocal);
    }

    /**
     * Format UTC dateTime to UTC dateTime in SQL format
     * @param date
     * @return
     */
    public static String formatUTCtoSqlUTC(Date date) {
        return formatUTC(date, DateTimeFormat.SQL, false);
    }

    /**
     * Format UTC dateTime to local dateTime in SQL format
     * @param date
     * @return
     */
    public static String formatUTCtoSqlLocal(Date date) {
        return formatUTC(date, DateTimeFormat.SQL, true);
    }

    /**
     * Get current local dateTime in desired format
     * @param format
     * @return
     */
    public static String getCurrentLocal(DateTimeFormat format) {
        return formatUTC(new Date(), format, true);
    }

    /**
     * Get current UTC dateTime in SQL format.
     * @return
     */
    public static String getCurrentUtcSql() {
        return formatUTC(new Date(), DateTimeFormat.SQL, false);
    }

    /**
     * Parse date given as string according to desired format
     * @param date
     * @param format
     * @return
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
     * Parse SQL formatted UTC dateTime
     * @param date
     * @return
     */
    public static Date parseSqlUtc(String date) {
        return parseUTC(date, DateTimeFormat.SQL);
    }
}
