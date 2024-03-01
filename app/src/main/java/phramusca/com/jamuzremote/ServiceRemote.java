package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.launchdarkly.eventsource.MessageEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author phramusca
 */
public class ServiceRemote extends ServiceBase {

    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_REMOTE"; //NON-NLS
    private static final String TAG = ServiceRemote.class.getName();
    protected static OkHttpClient client = new OkHttpClient();
    private ClientInfo clientInfo;
    private Notification notification;
    private BroadcastReceiver userStopReceiver;
    private WifiManager.WifiLock wifiLock;
    private ProcessRemote processRemote;

    @Override
    public void onCreate() {
        notification = new Notification(this, NotificationId.get(), getString(R.string.serviceSyncNotifySyncTitle),
                "Remote service",
                "Remote control JaMuz Server.");
        userStopReceiver = new UserStopServiceReceiver();
        registerReceiver(userStopReceiver, new IntentFilter(USER_STOP_SERVICE_REQUEST));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        clientInfo = (ClientInfo) intent.getSerializableExtra("clientInfo");
          WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TAG);
            if(wifiLock!=null && !wifiLock.isHeld()) {
                wifiLock.acquire();
            }
        }
        processRemote = new ProcessRemote("Thread.ServiceSync.processRemote");
        processRemote.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(userStopReceiver);
        if(wifiLock!=null) {
            wifiLock.release();
        }
        super.onDestroy();
    }

    private void stopSync(String msg, long millisInFuture) {
        processRemote.abort();
        if (!msg.equals("")) {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notification, msg, millisInFuture);
                helperToast.toastLong(msg);
            });
        }
        sendMessage("enableRemote");
        stopSelf();
    } //NON-NLS

    private class ProcessRemote extends ProcessAbstract {

        ProcessRemote(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                helperNotification.notifyBar(notification, getString(R.string.syncLabelConnecting));
                checkAbort();

                HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("connect"); //NON-NLS
                Request request = clientInfo.getRequestBuilder(urlBuilder)
                        .addHeader("password", clientInfo.getPassword())
                        .addHeader("rootPath", clientInfo.getRootPath())
                        .addHeader("model", clientInfo.getModel())
                        .build();
                clientInfo.getBodyString(request, client); //NON-NLS

//https://medium.com/@anugrahasb1997/implementing-server-sent-events-sse-in-android-with-okhttp-eventsource-226dc9b2599d
//https://github.com/Aarkan1/java-express?tab=readme-ov-file#server-sent-events
//FIXME ! implementation 'com.launchdarkly:okhttp-eventsource:1.0.0' => update until Duration / API 26
//TODO: Or Switch to https://github.com/square/okhttp/tree/master/okhttp-sse when ready


                SSEClient sseClient = new SSEClient(new SSEHandler() {
                    @Override
                    public void onSSEConnectionOpened() {
                        System.out.println("SSE connection opened");
                    }

                    @Override
                    public void onSSEEventReceived(String event, MessageEvent messageEvent) {
                        System.out.println("SSE received: " + messageEvent.getData());
                    }

                    @Override
                    public void onSSEError(Throwable t) {
                        System.err.println("Error occurred: " + t.getMessage());
                    }
                }, clientInfo.getUrlBuilder("sse").build().uri(),
                        clientInfo.getHeaders());

                sseClient.start();

                HttpUrl.Builder urlBuilderPlay = clientInfo.getUrlBuilder("play"); //NON-NLS
                Request requestPlay = clientInfo.getRequestBuilder(urlBuilderPlay)
                        .addHeader("idFile", "1361")
                        .build();
                clientInfo.getBodyString(requestPlay, client); //NON-NLS

            } catch (InterruptedException e) {
                Log.e(TAG, "Error ProcessRemote", e); //NON-NLS
                stopSync(getString(R.string.serviceSyncNotifySyncInterrupted), -1);
            } catch (Exception e) {
                Log.e(TAG, "Error ProcessRemote", e); //NON-NLS
                stopSync("ERROR: " + e.getLocalizedMessage(), -1);
            }
        }
    }

    public class UserStopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()"); //NON-NLS
            stopSync(getString(R.string.serviceSyncNotifySyncUserStopped), 1500);
        }
    }
}