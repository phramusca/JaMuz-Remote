package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.launchdarkly.eventsource.MessageEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    private final IBinder binder = new MyBinder();
    private final List<ServiceRemoteCallback> callbacks = new ArrayList<>();
    private ClientInfo clientInfo;
    private Notification notification;
    private BroadcastReceiver userStopReceiver;
    private WifiManager.WifiLock wifiLock;
    private SSEClient sseClient;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        public ServiceRemote getService() {
            return ServiceRemote.this;
        }
    }

    public void registerCallback(ServiceRemoteCallback callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(ServiceRemoteCallback callback) {
        callbacks.remove(callback);
    }

    private void notifyCallbacks(String event, MessageEvent messageEvent) {
        for (ServiceRemoteCallback callback : callbacks) {
            callback.onServiceDataReceived(event, messageEvent);
        }
    }

    @Override
    public void onCreate() {
        notification = new Notification(this, NotificationId.get(), getString(R.string.remote_control),
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
            if (wifiLock != null && !wifiLock.isHeld()) {
                wifiLock.acquire();
            }
        }
        startSse();
        getPlaying();
        return START_REDELIVER_INTENT;
    }

    boolean isConnected() {
        return sseClient.isConnected();
    }

    private void startSse() {
        new Thread() {
            @Override
            public void run() {
                try {
                    helperNotification.notifyBar(notification, getString(R.string.connected_to_remote_control));
                    clientInfo.getBodyString(clientInfo.getConnectRequest(), client); //NON-NLS

//https://medium.com/@anugrahasb1997/implementing-server-sent-events-sse-in-android-with-okhttp-eventsource-226dc9b2599d
//FIXME ! implementation 'com.launchdarkly:okhttp-eventsource:1.0.0' => update until Duration / API 26
//TODO: Or Switch, when ready, to https://github.com/square/okhttp/tree/master/okhttp-sse

                    //https://github.com/Aarkan1/java-express?tab=readme-ov-file#server-sent-events
                    sseClient = new SSEClient(new SSEHandler() {
                        @Override
                        public void onSSEConnectionOpened() {
                            System.out.println("SSE connection opened");
                        }

                        @Override
                        public void onSSEEventReceived(String event, MessageEvent messageEvent) {
                            System.out.println("SSE received: " + messageEvent.getData());
                            notifyCallbacks(event, messageEvent);
                        }

                        @Override
                        public void onSSEError(Throwable t) {
                            System.err.println("Error occurred: " + t.getMessage());
                        }
                    }, clientInfo.getUrlBuilder("sse").build().uri(),
                            clientInfo.getHeaders());

                    sseClient.start();
                } catch (Exception e) {
                    Log.e(TAG, "Error ServiceRemote", e); //NON-NLS
                    stopSync("ERROR: " + e.getLocalizedMessage(), -1);
                }
            }
        }.start();
    }

    private void getPlaying() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String bodyString = clientInfo.getBodyString("playing", client);
                    notifyCallbacks("playing", new MessageEvent(bodyString));
                } catch (IOException | ClientInfo.ServerException e) {
                    Log.e(TAG, "getPlaying", e); //NON-NLS
                }
            }
        }.start();
    }

    public void send(String action) {
        send(action, "");
    }

    public void send(String action, String value) {
        new Thread() {
            @Override
            public void run() {
                try {
                    HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("action"); //NON-NLS
                    JSONObject obj = new JSONObject();
                    obj.put("action", action);
                    obj.put("value", value);
                    Request request = clientInfo.getRequestBuilder(urlBuilder) //NON-NLS
                            .post(RequestBody.create(obj.toString(), MediaType.parse("application/json; charset=utf-8"))).build(); //NON-NLS
                    clientInfo.getBodyString(request, client);
                } catch (IOException | ClientInfo.ServerException | JSONException e) {
                    Log.e(TAG, "sending " + action, e); //NON-NLS
                }
            }
        }.start();
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(userStopReceiver);
        if (wifiLock != null) {
            wifiLock.release();
        }
        super.onDestroy();
    }

    private void stopSync(String msg, long millisInFuture) {
        helperNotification.notifyBar(notification, getString(R.string.closing_remote_control));
        if (sseClient != null) {
            sseClient.disconnect();
        }
        if (!msg.isEmpty()) {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notification, msg, millisInFuture);
                helperToast.toastLong(msg);
            });
        }
        runOnUiThread(() -> helperNotification.notifyBar(notification, getString(R.string.closed_remote_control), 5000));

        sendMessage("enableRemote"); //NON-NLS
        stopSelf();
    }

    public class UserStopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()"); //NON-NLS
            stopSync(getString(R.string.serviceSyncNotifySyncUserStopped), 1500);
        }
    }
}