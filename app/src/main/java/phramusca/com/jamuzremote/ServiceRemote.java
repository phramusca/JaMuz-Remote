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
                    //FIXME !! Use bodyString when required
                    String bodyString = clientInfo.getBodyString(request, client);
                } catch (IOException | ClientInfo.ServerException | JSONException e) {
                    Log.e(TAG, "sending " + action, e); //NON-NLS
                }
            }
        }.start();
    }

    //FIXME !!! Use this code to get returns from server
//    class ListenerRemote implements IListenerRemote {
//
//        private final String TAG = ListenerRemote.class.getName();
//
//        @Override
//        public void onReceivedJson(final String json) {
//            try {
//                JSONObject jObject = new JSONObject(json);
//                String type = jObject.getString("type"); //NON-NLS //NON-NLS
//                switch (type) {
//                    case "playlists": //NON-NLS
//                        String selectedPlaylist = jObject.getString("selectedPlaylist"); //NON-NLS
//                        Playlist temp = new Playlist(selectedPlaylist, false);
//                        final JSONArray jsonPlaylists = (JSONArray) jObject.get("playlists"); //NON-NLS
//                        final List<Playlist> playlists = new ArrayList<>();
//                        for (int i = 0; i < jsonPlaylists.length(); i++) {
//                            String playlist = (String) jsonPlaylists.get(i);
//                            Playlist playList = new Playlist(playlist, false);
//                            if (playlist.equals(selectedPlaylist)) {
//                                playList = temp;
//                            }
//                            playlists.add(playList);
//                        }
//                        ArrayAdapter<Playlist> arrayAdapter =
//                                new ArrayAdapter<>(ActivityMain.this,
//                                        R.layout.spinner_item, playlists);
//                        setupPlaylistSpinner(arrayAdapter, temp);
//                        enablePlaylistEdit(false);
//                        break;
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, e.toString());
//            }
//        }
//    }

    //FIXME ! Get playing track info from remote and display it
//    private void getTags() throws IOException, ClientInfo.ServerException, JSONException {
//        String body = clientInfo.getBodyString("tags", client); //NON-NLS
//        helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncReceivedTags));
//        final JSONObject jObject = new JSONObject(body);
//        //FIXME Get tags list with their respective number of files, for sorting
//        //FIXME Add a "x/y" button to display tag & genre (in playlist) pages x/y (# of tags per page to be defined/optional)
//        final JSONArray jsonTags = (JSONArray) jObject.get("tags"); //NON-NLS
//        final List<String> newTags = new ArrayList<>();
//        for (int i = 0; i < jsonTags.length(); i++) {
//            newTags.add((String) jsonTags.get(i));
//        }
//        RepoTags.set(newTags);
//        sendMessage("setupTags");
//    }

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