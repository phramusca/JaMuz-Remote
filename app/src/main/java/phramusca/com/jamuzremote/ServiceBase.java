package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import java.io.File;

public class ServiceBase extends Service {

    protected File getAppDataPath;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    protected HelperNotification helperNotification;
    protected HelperToast helperToast = new HelperToast(this);

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        helperNotification= new HelperNotification(getApplicationIntent(), mNotifyManager);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        getAppDataPath = (File)intent.getSerializableExtra("getAppDataPath");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    protected void sendMessage(String msg) {
        Message completeMessage =
                mHandler.obtainMessage(1, msg);
        completeMessage.sendToTarget();
    }

    protected void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    //This is to have application opened when clicking on notification
    private PendingIntent getApplicationIntent() {
        Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
    }
}