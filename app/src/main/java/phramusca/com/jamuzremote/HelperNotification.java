package phramusca.com.jamuzremote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.CountDownTimer;
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
    public void notifyBar(Notification notification, String msg, long millisInFuture) {
        notifyBar(notification, msg);
        disableNotificationIn(millisInFuture, notification.id);
    }

    public void notifyBar(Notification notification, String msg) {
        notifyBar(notification, msg, 0, 0, false, true, false);
    }

    public void notifyBar(Notification notification, String msg,
                          int max, int progress, boolean indeterminate, boolean setWhen,
                          boolean usesChronometer) {
        notification.builder.setContentText(msg);
        if(setWhen) {
            notification.builder.setWhen(System.currentTimeMillis());
        }
        notification.builder.setUsesChronometer(usesChronometer);
        notification.builder.setProgress(max, progress, indeterminate);
        notification.builder.setContentIntent(pendingIntent);
        notificationManager.notify(notification.id, notification.builder.build());
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