package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_REMOTE"; //NON-NLS

    private DownloadProcess processDownload;
    private ClientInfo clientInfo;
    private Notification notificationSync;
    private BroadcastReceiver userStopReceiver;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private ProcessSync processSync;
    protected static OkHttpClient client = new OkHttpClient();

    protected static OkHttpClient clientDownload;

    @Override
    public void onCreate() {
        notificationSync = new Notification(this, NotificationId.get(), getString(R.string.serviceSyncNotifySyncTitle));
        clientDownload = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        userStopReceiver = new UserStopServiceReceiver();
        registerReceiver(userStopReceiver, new IntentFilter(USER_STOP_SERVICE_REQUEST));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        clientInfo = (ClientInfo) intent.getSerializableExtra("clientInfo");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire(24 * 60 * 60 * 1000); //24 hours, enough to download a lot !
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TAG);
            wifiLock.acquire();
        }
        processSync = new ProcessSync("Thread.ServiceSync.processSync");
        processSync.start();
        return START_REDELIVER_INTENT;
    }

    private class ProcessSync extends ProcessAbstract {

        ProcessSync(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                helperNotification.notifyBar(notificationSync, getString(R.string.syncLabelConnecting));
                checkAbort();
                clientInfo.getBodyString("connect", client); //NON-NLS

                long startTime = System.currentTimeMillis();
                long startTimeTotal = startTime;
                helperNotification.notifyBar(notificationSync, getString(R.string.syncLabelReadingList));
                checkAbort();
                RepoSync.read();
                Log.w(TAG, "RepoSync.read() :"+(System.currentTimeMillis() - startTime)+" ms");

                checkAbort();
                getTags();

                checkAbort();
                getGenres();

                startTime = System.currentTimeMillis();
                checkAbort();
                requestMerge();
                Log.w(TAG, "requestMerge() :"+(System.currentTimeMillis() - startTime)+" ms");

                startTime = System.currentTimeMillis();
                checkAbort();
                checkFiles(Track.Status.NEW);
                Log.w(TAG, "checkFiles(Track.Status.NEW) :"+(System.currentTimeMillis() - startTime)+" ms");

                startTime = System.currentTimeMillis();
                checkAbort();

                Map<Track, Integer> map = new HashMap<>();
                for (Track track : RepoSync.getDownloadList()) {
                    map.put(track, -1);
                }
                startDownloads(map);
                Log.w(TAG, "startDownloads(RepoSync.getDownloadList()) :"+(System.currentTimeMillis() - startTime)+" ms");

                startTime = System.currentTimeMillis();
                checkFiles(Track.Status.INFO);
                Log.w(TAG, "checkFiles(Track.Status.INFO) :"+(System.currentTimeMillis() - startTime)+" ms");

                startTime = System.currentTimeMillis();
                //Remove files in db but not received from server
                helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncRemovingDeleted));
                List<Track> trackList = RepoSync.getNotSyncedList();
                Log.w(TAG, "RepoSync.getNotSyncedList() :"+(System.currentTimeMillis() - startTime)+" ms");
                int nbTracks = trackList.size();
                int i = 0;
                for (Track track : trackList) {
                    checkAbort();
                    i++;
                    helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncRemovingDeleted), 10, i, nbTracks);
                    File file = new File(track.getPath());
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    HelperLibrary.musicLibrary.deleteTrack(track.getIdFileServer());
                }
                Log.w(TAG, "TOTAL Sync :"+(System.currentTimeMillis() - startTimeTotal)+" ms");

                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncCheckComplete), -1));
                if (processDownload != null) {
                    processDownload.join();
                    String finalToastMsg = processDownload.checkCompleted();
                    stopSync(finalToastMsg, -1);
                } else {
                    stopSync(getString(R.string.serviceSyncNotifySyncCompleteNoDownloads), -1);
                }
                RepoAlbums.reset();

            } catch (InterruptedException e) {
                Log.e(TAG, "Error ProcessSync", e); //NON-NLS
                helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncInterrupted));
            } catch (Exception e) {
                Log.e(TAG, "Error ProcessSync", e); //NON-NLS
                helperNotification.notifyBar(notificationSync, "ERROR: " + e.getLocalizedMessage(), 0, 0, false, //NON-NLS
                        true, false, "ERROR: " + e.getLocalizedMessage()); //NON-NLS
            }
        }

        private void checkFiles(Track.Status status)
                throws InterruptedException, ServerException, IOException {
            int nbFilesInBatch = 500;
            int nbFilesServer = getFilesCount(status);
            String msg = String.format(
                    "%s \"%s\"...", //NON-NLS
                    getString(R.string.serviceSyncNotifySyncChecking),
                    status.name().toLowerCase());
            helperNotification.notifyBar(notificationSync, msg);
            if (nbFilesServer > 0) {
                for (int i = 0; i <= nbFilesServer; i = i + nbFilesInBatch) {
                    checkAbort();
                    Map<Integer, Track> filesMapBatch = getFiles(i, nbFilesInBatch, status);
                    int j = 0;
                    for (Track trackServer : filesMapBatch.values()) {
                        checkAbort();
                        helperNotification.notifyBar(notificationSync, msg, 50, i + j, nbFilesServer);
                        j++;
                        Track trackRemote = RepoSync.getFile(trackServer.getIdFileServer());
                        if (trackRemote != null) {
                            //Only path, length and size can be compared.
                            // Other available fields are for merge usage
                            // Other metadata changes done in JaMuz should result in a file save (so changes getModifDate)
                            if (trackServer.getSize() != trackRemote.getSize()
                                    || trackServer.getLength() != trackRemote.getLength()
                                    || !trackServer.getModifDate().equals(trackRemote.getModifDate())
                                    || !trackServer.getRelativeFullPath().equals(trackRemote.getRelativeFullPath())) {
                                File file = new File(trackRemote.getPath());
                                //noinspection ResultOfMethodCallIgnored
                                file.delete();
                                trackServer.setIdFileRemote(trackRemote.getIdFileRemote());
                                HelperLibrary.musicLibrary.updateTrack(trackServer, false);
                            } else {
                                switch (trackServer.getStatus()) {
                                    case INFO:
                                        if (!trackRemote.getStatus().equals(Track.Status.INFO)) {
                                            File file = new File(trackRemote.getPath());
                                            //noinspection ResultOfMethodCallIgnored
                                            file.delete();
                                            HelperLibrary.musicLibrary.updateStatus(trackServer);
                                        }
                                        break;
                                    case NEW:
                                        if (trackRemote.getStatus().equals(Track.Status.REC)) {
                                            trackServer.setStatus(Track.Status.REC);
                                        } else if (!trackRemote.getStatus().equals(Track.Status.NEW)) {
                                            HelperLibrary.musicLibrary.updateStatus(trackServer);
                                        }
                                        break;
                                }
                            }
                        } else {
                            if (trackServer.getStatus().equals(Track.Status.NEW) && RepoSync.checkFile(trackServer)) {
                                trackServer.setStatus(Track.Status.REC);
                            }
                            HelperLibrary.musicLibrary.insertTrack(trackServer);
                        }
                        RepoSync.update(trackServer);
                    }
                }
            }
        }

        //TODO: use gson (or retrofit !) instead of JSONObject to deserialize body
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;

        private Map<Integer, Track> getFiles(int idFrom, int nbFilesInBatch, Track.Status status) throws IOException {

            //RetryInterceptor cannot catch .body().string() network errors, so looping here instead
            int nbRetries = 0;
            int sleepSeconds = 5;
            int maxNbRetries = 20;
            Map<Integer, Track> newTracks = null;
            String msg = "";
            do {
                nbRetries++;
                try {
                    HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files/" + status.name()); //NON-NLS
                    urlBuilder.addQueryParameter("idFrom", String.valueOf(idFrom));
                    urlBuilder.addQueryParameter("nbFilesInBatch", String.valueOf(nbFilesInBatch));
                    String body = clientInfo.getBodyString(urlBuilder, client);
                    final JSONObject jObject = new JSONObject(body);
                    JSONArray files = (JSONArray) jObject.get("files"); //NON-NLS
                    newTracks = new LinkedHashMap<>();
                    for (int i = 0; i < files.length(); i++) {
                        Track fileReceived = new Track(
                                (JSONObject) files.get(i),
                                getAppDataPath, false);
                        fileReceived.setSync();
                        newTracks.put(fileReceived.getIdFileServer(), fileReceived);
                    }
                    break;
                } catch (Exception e) {
                    msg = e.getLocalizedMessage();
                    Log.d(TAG, "ERROR: " + msg); //NON-NLS
                    helperNotification.notifyBar(notificationSync, String.format(
                            Locale.getDefault(),
                            "%ds %s %d/%d : %s", //NON-NLS
                            sleepSeconds,
                            getString(R.string.globalLabelBefore),
                            nbRetries + 1,
                            maxNbRetries,
                            msg));
                    try {
                        sleep(sleepSeconds * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } while (nbRetries < maxNbRetries - 1);
            if (newTracks == null) {
                throw new IOException(msg);
            }
            return newTracks;
        }

        private Integer getFilesCount(Track.Status status) throws IOException, ServerException {
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files/" + status.name()); //NON-NLS
            urlBuilder.addQueryParameter("getCount", "true"); //NON-NLS
            String body = clientInfo.getBodyString(urlBuilder, client);
            helperNotification.notifyBar(notificationSync, String.format(
                    "%s \"%s\" %s", //NON-NLS
                    getString(R.string.serviceSyncNotifySyncReceived),
                    status.name(),
                    getString(R.string.serviceSyncNotifySyncReceivedSuffix)));
            return Integer.valueOf(body);
        }

        private void getTags() throws IOException, ServerException, JSONException {
            String body = clientInfo.getBodyString("tags", client); //NON-NLS
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncReceivedTags));
            final JSONObject jObject = new JSONObject(body);
            //TODO Get tags list with their respective number of files, for sorting
            //TODO Add a "x/y" button to display tag & genre (in playlist) pages x/y (# of tags per page to be defined/optional)
            final JSONArray jsonTags = (JSONArray) jObject.get("tags"); //NON-NLS
            final List<String> newTags = new ArrayList<>();
            for (int i = 0; i < jsonTags.length(); i++) {
                newTags.add((String) jsonTags.get(i));
            }
            RepoTags.set(newTags);
            sendMessage("setupTags");
        }

        private void getGenres() throws IOException, ServerException, JSONException { //NON-NLS
            String body = clientInfo.getBodyString("genres", client); //NON-NLS
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncReceivedGenres));
            final JSONObject jObject = new JSONObject(body);
            final JSONArray jsonGenres = (JSONArray) jObject.get("genres"); //NON-NLS
            final List<String> newGenres = new ArrayList<>();
            for (int i = 0; i < jsonGenres.length(); i++) {
                final String genre = (String) jsonGenres.get(i);
                newGenres.add(genre);
            }
            RepoGenres.set(newGenres);
            sendMessage("setupGenres");
        }

        private void requestMerge() throws JSONException, ServerException, IOException {
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncPreparingMerge));
            List<Track> tracks = RepoSync.getMergeList();
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(Math.min(Math.max(tracks.size(), 30), 600), TimeUnit.SECONDS) //Between 30s and 10min
                    .build();
            JSONObject obj = new JSONObject();
            obj.put("type", "FilesToMerge");
            JSONArray filesToMerge = new JSONArray();
            for (Track track : tracks) {
                track.getTags(true);
                filesToMerge.put(track.toJSONObject());
            }
            obj.put("files", filesToMerge); //NON-NLS
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files"); //NON-NLS
            Request request = clientInfo.getRequestBuilder(urlBuilder) //NON-NLS
                    .post(RequestBody.create(obj.toString(), MediaType.parse("application/json; charset=utf-8"))).build(); //NON-NLS
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncRequestingMerge));
            String body = clientInfo.getBodyString(request, client);
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncUpdateDatabase)); //NON-NLS
            final JSONObject jObject = new JSONObject(body);
            JSONArray filesToUpdate = (JSONArray) jObject.get("files"); //NON-NLS
            for (int i = 0; i < filesToUpdate.length(); i++) {
                Track trackServer = new Track(
                        (JSONObject) filesToUpdate.get(i),
                        getAppDataPath, true);
                trackServer.setStatus(Track.Status.REC);
                Track trackRemote = RepoSync.getFile(trackServer.getIdFileServer());
                if (trackRemote != null) {
                    trackServer.setIdFileRemote(trackRemote.getIdFileRemote());
                    HelperLibrary.musicLibrary.updateTrack(trackServer, true);
                }
                helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncUpdateDatabase),
                        10, i + 1, filesToUpdate.length());
            }
            helperNotification.notifyBar(notificationSync, getString(R.string.serviceSyncNotifySyncMergeComplete));
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(userStopReceiver);
        wakeLock.release();
        wifiLock.release();
        super.onDestroy();
    }

    static class ServerException extends Exception {
        public ServerException(String errorMessage) {
            super(errorMessage);
        } //NON-NLS
    }

    public class UserStopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()"); //NON-NLS
            stopSync(getString(R.string.serviceSyncNotifySyncUserStopped), 1500);
        }
    }

    private void stopSync(String msg, long millisInFuture) {
        if (processDownload != null) {
            processDownload.stopDownloads();
            processDownload = null;
        }
        processSync.abort();
        if (!msg.equals("")) {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationSync, msg, millisInFuture);
                helperToast.toastLong(msg);
            });
        }
        sendMessage("enableSync");
        stopSelf();
    } //NON-NLS

    private void startDownloads(Map<Track, Integer> newTracks) {
        if ((processDownload == null || !processDownload.isAlive()) && newTracks.size() > 0) {
            Log.i(TAG, "START ProcessDownload"); //NON-NLS
            processDownload = new DownloadProcess("ProcessDownload", newTracks, this, helperNotification, clientInfo, clientDownload, getString(R.string.serviceSyncNotifyDownloadTitle), null);
            processDownload.start();
        }
    }
}