package phramusca.com.jamuzremote;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * @author phramusca ( <a href="https://github.com/phramusca/JaMuz/">...</a> )
 */
public class Notification {

    protected NotificationCompat.Builder builder;
    protected int id;

    Notification(Context context, int id, String title, String channelName, String channelDescription) {
        String channelId = channelName+channelDescription;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            chan.setDescription(channelDescription);
            NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(chan);
        }
        builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentTitle(title)
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.ic_process);
        this.id = id;
    }
}
