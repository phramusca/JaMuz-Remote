package phramusca.com.jamuzkids;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by raph on 10/06/17.
 */

public class HelperNotification {

    private static final String TAG = HelperNotification.class.getName();

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    HelperNotification(PendingIntent pendingIntent, NotificationManager notificationManager) {
        this.pendingIntent = pendingIntent;
        this.notificationManager = notificationManager;
    }

    //Ends a notification
    public void notifyBar(Notification notification, String msg, long millisInFuture) {
        notifyBar(notification, msg);
        disableNotificationIn(millisInFuture, notification.id);
    }

    public void notifyBar(Notification notification, final String action, int every,
                          int nbFiles, int nbFilesTotal) {
        every=nbFilesTotal<every?nbFilesTotal/10:every;
        if(((nbFiles-1) % (every>0?every:1)) == 0) { //To prevent UI from freezing
            String msg = nbFiles + "/" + nbFilesTotal + " " + action;
            notifyBar(notification, msg, nbFilesTotal, nbFiles, false,
                    false, false, "");
        }
    }

    public void notifyBar(Notification notification, String msg) {
        notifyBar(notification, msg, 0, 0, false,
                true, false, "");
    }

    public void notifyBar(Notification notification, String msg,
                          int max, int progress, boolean indeterminate, boolean setWhen,
                          boolean usesChronometer, String bigText) {
        notification.builder.setContentText(msg);
        if(!bigText.equals("")) {
            notification.builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        } else {
            notification.builder.setStyle(null);
        }
        if(setWhen) {
            notification.builder.setWhen(System.currentTimeMillis());
        }
        notification.builder.setUsesChronometer(usesChronometer);
        notification.builder.setProgress(max, progress, indeterminate);
        notification.builder.setContentIntent(pendingIntent);
        notificationManager.notify(notification.id, notification.builder.build());
        Log.i(TAG, "NOTIFICATION " + notification.id + ": " + msg);
    }

    private void disableNotificationIn(final long millisInFuture, final int id) {
        if(millisInFuture>0) {
            CountDownTimer timer = new CountDownTimer(millisInFuture, millisInFuture / 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, (millisUntilFinished / 1000) + "s remaining before " +
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
}