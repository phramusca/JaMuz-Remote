package phramusca.com.jamuzremote;

import java.util.Date;

/**
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class NotificationId {
    public static int get() {
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        return Integer.parseInt(last4Str);
    }
}