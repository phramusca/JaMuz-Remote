package phramusca.com.jamuzremote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by raph on 10/06/17.
 */

public class HelperNotification {

    private static final String TAG = HelperNotification.class.getSimpleName();

    private NotificationManager notificationManager;
    PendingIntent pendingIntent;

    public HelperNotification(PendingIntent pendingIntent, NotificationManager notificationManager) {
        this.pendingIntent = pendingIntent;
        this.notificationManager = notificationManager;
    }

    //Ends a notification
    public void notifyBar(NotificationCompat.Builder builder, int id, String msg, long millisInFuture) {
        notifyBar(builder, id, msg);
        disableNotificationIn(millisInFuture, id);
    }

    public void notifyBar(NotificationCompat.Builder builder, int id, String msg) {
        notifyBar(builder, id, msg, 0, 0, false, true, false);
    }

    public void notifyBar(NotificationCompat.Builder builder, int id, String msg,
                          int max, int progress, boolean indeterminate, boolean setWhen,
                          boolean usesChronometer) {
        builder.setContentText(msg);
        if(setWhen) {
            builder.setWhen(System.currentTimeMillis());
        }
        builder.setUsesChronometer(usesChronometer);
        builder.setProgress(max, progress, indeterminate);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(id, builder.build());
    }

    private void disableNotificationIn(final long millisInFuture, final int id) {
        CountDownTimer timer = new CountDownTimer(millisInFuture, millisInFuture/10) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, (millisUntilFinished/1000)+"s remaining before " +
                        "disabling notification");
            }

            @Override
            public void onFinish() {
                notificationManager.cancel(id);
            }
        };
        timer.start();
    }
}