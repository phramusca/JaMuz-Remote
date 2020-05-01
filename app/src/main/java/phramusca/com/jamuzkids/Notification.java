package phramusca.com.jamuzkids;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Notification {

    protected NotificationCompat.Builder builder;
    protected int id;

    Notification(Context context, int id, String title) {
        builder = new NotificationCompat.Builder(context, "MyNotificationChannelId");
        builder.setContentTitle(title)
                .setContentText("Download in progress")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.ic_process);
        this.id =id;
    }
}
