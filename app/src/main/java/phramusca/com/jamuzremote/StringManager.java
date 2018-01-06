package phramusca.com.jamuzremote;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by raph on 01/05/17.
 */
public class StringManager {
    /**
     * Return left portion of a string
     * @param text
     * @param length
     * @return
     */
    @NonNull
    public static String Left(String text, int length)
    {
        return text.substring(0, length);
    }

    /**
     * Return right portion of a string
     * @param text
     * @param length
     * @return
     */
    @NonNull
    public static String Right(String text, int length)
    {
        return text.substring(text.length() - length, text.length());
    }

    /**
     * Return portion of a string
     * @param text
     * @param start
     * @param end
     * @return
     */
    @NonNull
    public static String Mid(String text, int start, int end)
    {
        return text.substring(start, end);
    }

    /**
     * Return portion of a string
     * @param text
     * @param start
     * @return
     */
    @NonNull
    public static String Mid(String text, int start)
    {
        return text.substring(start, text.length() - start);
    }

    /**
     * Remove illegal characters from path and filename.
     * Includes nearly (no one is perfect) all windows and linux ones.
     * Windows has much more that Linux but removing on both
     * systems for compatibility. Anyway, not that important characters
     * for an audio filename ...
     * @param str
     * @return
     */
    public static String removeIllegal(String str) {
        String pattern = "[\\\\/:\"*?<>|.!]+"; //NOI18N
        return str.replaceAll(pattern, "_"); //NOI18N
    }

    /**
     *
     * @param text
     * @return
     */
    public static String getNullableText(String text) {
        if(text==null) {
            return "null";
        }
        else {
            return text;
        }
    }

    /**
     * Convert number of bytes into human readable formatDisplay (Kio, Ko, ...)
     *
     * @param bytes
     * @param si
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        if (bytes < 0) {
            bytes = Math.abs(bytes);
        }

        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " o"; //NOI18N
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i"); //NOI18N
        return String.format("%.1f %so", bytes / Math.pow(unit, exp), pre); //NOI18N
    }

    /**
     *
     * @param seconds
     * @return
     */
    public static String secondsToMMSS(int seconds) {
        return String.format("%02d:%02d", //NOI18N
                TimeUnit.SECONDS.toMinutes(seconds),
                TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );
    }

    /**
     *
     * @param seconds
     * @return
     */
    public static String secondsToHHMM(int seconds) {
        return String.format("%02d h %02d", //NOI18N
                TimeUnit.SECONDS.toHours(seconds),
                TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds))
        );
    }

    /**
     *
     * @param seconds
     * @return
     */
    @NonNull
    public static String humanReadableSeconds(long seconds) {
        if (seconds <= 0) {
            return "-";
        }

        final long days = TimeUnit.SECONDS.toDays(seconds);
        seconds -= TimeUnit.DAYS.toSeconds(days);
        final long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        final long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);
//        final long seconds = TimeUnit.SECONDS.toSeconds(millis);
//        millis -= TimeUnit.SECONDS.toMillis(seconds);

        final StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days);
            sb.append("d ");
        }
        if (hours > 0) {
            sb.append(String.format("%02d", hours));
            sb.append("h ");
        }
        if (minutes > 0) {
            sb.append(String.format("%02d", minutes));
            sb.append("m ");
        }
        if (seconds > 0) {
            sb.append(String.format("%02d", seconds));
            sb.append("s");
        }
//        if ((seconds <= 0) && (millis > 0) && showMS) {
//            sb.append(String.format("%02d", millis));
//            sb.append("ms");
//        }

        return sb.toString();
    }

    @NonNull
    public static List<String> parseSlashList(String string) {
        return Arrays.asList(string.split(" / ")); //NOI18N
    }

    //	public static String humanReadableMilliSeconds(long millis)
//    {
//        long hours = TimeUnit.MILLISECONDS.toHours(millis);
//        millis -= TimeUnit.HOURS.toMillis(hours);
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
//        millis -= TimeUnit.MINUTES.toMillis(minutes);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
//
//        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
//    }
}
