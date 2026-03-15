package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.launchdarkly.eventsource.MessageEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
        //FIXME: Fix and translate those strings (add new ones as needed)
        notification = new Notification(this, NotificationId.get(), getString(R.string.remote_control),
                "Remote service",
                "Remote control JaMuz Server.");
        userStopReceiver = new UserStopServiceReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(userStopReceiver, new IntentFilter(USER_STOP_SERVICE_REQUEST));
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
        return START_REDELIVER_INTENT;
    }

    boolean isConnected() {
        if (sseClient != null) {
            return sseClient.isConnected();
        }
        return false;
    }

    private void startSse() {
        new Thread() {
            @Override
            public void run() {
                try {
                    helperNotification.notifyBar(notification, getString(R.string.connected_to_remote_control));
                    clientInfo.getBodyString(clientInfo.getConnectRequest(), client); //NON-NLS

                    //https://github.com/Aarkan1/java-express?tab=readme-ov-file#server-sent-events
                    sseClient = new SSEClient(new SSEHandler() {
                        @Override
                        public void onSSEConnectionOpened() {
                            System.out.println("SSE connection opened");
                        }

                        @Override
                        public void onSSEConnectionClosed() {
                            System.out.println("SSE connection closed");
                            stopRemote(getString(R.string.closing_remote_control), 0);
                        }

                        @Override
                        public void onSSEEventReceived(String event, MessageEvent messageEvent) {
                            System.out.println("SSE received event: " + event + " | " + messageEvent.getData());
                            if ("ping".equals(event)) return; // server heartbeat, keep connection alive
                            notifyCallbacks(event, messageEvent);
                        }

                        @Override
                        public void onSSEError(Throwable t) {
                            Log.w(TAG, "SSE error (reconnecting if possible): " + t.getMessage());
                            // Do not stopRemote: library will auto-reconnect (reconnectTime/maxReconnectTime).
                            // onOpen() will be called again on successful reconnect.
                        }
                    }, clientInfo.getUrlBuilder("sse").build().uri(),
                            clientInfo.getHeaders());

                    sseClient.start();
                    getPlaying();
                } catch (Exception e) {
                    Log.e(TAG, "Error ServiceRemote", e); //NON-NLS
                    stopRemote("ERROR: " + e.getLocalizedMessage(), -1);
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

    /** Call when activity resumes while connected, to refresh displayed track from server instead of local. */
    public void refreshPlaying() {
        if (clientInfo != null) {
            getPlaying();
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userStopReceiver);
        if (wifiLock != null) {
            wifiLock.release();
        }
        super.onDestroy();
    }

    private void stopRemote(String msg, long millisInFuture) {
        helperNotification.notifyBar(notification, getString(R.string.closing_remote_control));
        new Thread(() -> {
            try {
                if (clientInfo != null) {
                    try {
                        clientInfo.getBodyString("disconnect", client);
                    } catch (IOException | ClientInfo.ServerException e) {
                        Log.e(TAG, "disconnect", e); //NON-NLS
                    }
                }
                if (sseClient != null) {
                    sseClient.disconnect();
                }
            } finally {
                runOnUiThread(() -> {
                    if (!msg.isEmpty()) {
                        helperNotification.notifyBar(notification, msg, millisInFuture);
                        helperToast.toastLong(msg);
                    }
                    helperNotification.notifyBar(notification, getString(R.string.closed_remote_control), 5000);
                    sendMessage("enableRemote"); //NON-NLS
                    stopSelf();
                });
            }
        }).start();
    }

    public class UserStopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()"); //NON-NLS
            stopRemote(getString(R.string.serviceSyncNotifySyncUserStopped), 1500);
        }
    }
}