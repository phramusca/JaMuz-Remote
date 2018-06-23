package phramusca.com.jamuzkids;

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
     * @param text string
     * @param length string lenght
     * @return left of string up to length
     */
    @NonNull
    public static String Left(String text, int length)
    {
        return text.substring(0, length);
    }

    /** Return left portion of a string
     * @param text string
     * @param length string length
     * @return right portion of a string
     */
    @NonNull
    public static String Right(String text, int length)
    {
        return text.substring(text.length() - length, text.length());
    }

    /**
     * Return portion of a string
     * @param text string
     * @param start where to start
     * @param end where to end
     * @return portion of a string
     */
    @NonNull
    public static String Mid(String text, int start, int end)
    {
        return text.substring(start, end);
    }

    /**
     * Return portion of a string
     * @param text string
     * @param start where to start
     * @return portion of a string
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
     * @param str string
     * @return string with illegal chars replaced by "_"
     */
    public static String removeIllegal(String str) {
        String pattern = "[\\\\/:\"*?<>|.!]+"; //NOI18N
        return str.replaceAll(pattern, "_"); //NOI18N
    }

    /**
     * @param text string
     * @return text or "null"
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
     * <p>Système international (SI) | ex: 1 mégabit (Mb) 	= 106 bits 	= 1 000 kb 	= 1 000 000 bits
     * <p>Préfixes binaires | ex: 1 mébibit (Mib) 	= 220 bits 	= 1 024 kib 	= 1 048 576 bits
     * @param bits number of bits
     * @param si Use SI (International System of Units) or not
     * @return Human readable file size
     */
    public static String humanReadableBitCount(long bits, boolean si) {
        return humanReadableByteCount(bits, si, "b");
    }

    /**
     * Convert number of bytes into human readable formatDisplay (Kio, Ko, ...)
     * <p>Système international (SI) | ex: 1 mégaoctet (Mo) 	= 106 octets 	= 1 000 ko 	= 1 000 000 octets
     * <p>Préfixes binaires | ex: 1 mébioctet (Mio) 	= 220 octets 	= 1 024 kio 	= 1 048 576 octets
     * @param bytes number of bytes
     * @param si Use SI (International System of Units) or not
     * @return Human readable file size
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        return humanReadableByteCount(bytes, si, "o");
    }

    private static String humanReadableByteCount(long bytes, boolean si, String unitChar) {
        if (bytes < 0) {
            bytes = Math.abs(bytes);
        }

        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " "+unitChar; //NOI18N
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i"); //NOI18N
        return String.format("%.1f %s"+unitChar, bytes / Math.pow(unit, exp), pre); //NOI18N
    }

    /**
     *
     * @param seconds
     * @return MM:SS
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
    public static String humanReadableSeconds(long seconds, String sign) {
        if (seconds <= 0) {
            return "-";
        } else if (seconds <= 59) {
            return "<1m";
        }

        final long days = TimeUnit.SECONDS.toDays(seconds);
        seconds -= TimeUnit.DAYS.toSeconds(days);
        final long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        final long minutes = TimeUnit.SECONDS.toMinutes(seconds);

        final StringBuilder sb = new StringBuilder();
        sb.append(sign);
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
            sb.append("m");
        }

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
