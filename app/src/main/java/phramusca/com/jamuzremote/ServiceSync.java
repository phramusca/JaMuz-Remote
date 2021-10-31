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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * @author phramusca
 */
public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_REMOTE";

    private ProcessDownload processDownload;
    private ClientInfo clientInfo;
    private Notification notificationSync;
    private BroadcastReceiver userStopReceiver;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private ProcessSync processSync;
    protected static OkHttpClient client = new OkHttpClient();
    private Notification notificationDownload;
    protected static OkHttpClient clientDownload;

    @Override
    public void onCreate() {
        notificationSync = new Notification(this, NotificationId.SYNC, "Sync");
        notificationDownload = new Notification(this, NotificationId.SYNC_DOWN, "Sync");
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
                helperNotification.notifyBar(notificationSync, getString(R.string.connecting));
                checkAbort();
                clientInfo.getBodyString("connect", client);

                helperNotification.notifyBar(notificationSync, getString(R.string.readingList));
                checkAbort();
                RepoSync.read();

                checkAbort();
                getTags();

                checkAbort();
                getGenres();

                checkAbort();
                requestMerge();

                checkAbort();
                checkFiles(Track.Status.NEW);

                checkAbort();
                startDownloads(RepoSync.getDownloadList());

                checkFiles(Track.Status.INFO);

                //Remove files in db but not received from server
                helperNotification.notifyBar(notificationSync, "Removing deleted files...");
                List<Track> trackList = RepoSync.getNotSyncedList();
                int nbTracks = trackList.size();
                int i = 0;
                for (Track track : trackList) {
                    checkAbort();
                    i++;
                    helperNotification.notifyBar(notificationSync, "Removing deleted files", 10, i, nbTracks);
                    File file = new File(track.getPath());
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    HelperLibrary.musicLibrary.deleteTrack(track.getIdFileServer());
                }

                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, "Check complete.", -1));
                if (processDownload != null) {
                    processDownload.join();
                    processDownload.checkCompleted();
                } else {
                    stopSync("Sync Complete. No downloads.", -1);
                }
                RepoAlbums.reset();

            } catch (InterruptedException e) {
                Log.e(TAG, "Error ProcessSync", e);
                helperNotification.notifyBar(notificationSync, "Interrupted ");
            } catch (Exception e) {
                Log.e(TAG, "Error ProcessSync", e);
                helperNotification.notifyBar(notificationSync, "ERROR: " + e.getLocalizedMessage(), 0, 0, false,
                        true, false, "ERROR: " + e.getLocalizedMessage());
            }
        }

        private void checkFiles(Track.Status status)
                throws InterruptedException, ServerException, IOException {
            int nbFilesInBatch = 500;
            int nbFilesServer = getFilesCount(status);
            String msg = "Checking " + status.name().toLowerCase() + " files ...";
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
                            //FIXME: Update track for other changes too (format, metadata ...)
                            if (trackServer.getSize() != trackRemote.getSize()
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

            //Interceptor cannot catch .body().string() network errors, so looping here instead
            //RetryInterceptor interceptor = new RetryInterceptor(5,5, helperNotification, notificationSync);
            //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            int nbRetries = 0;
            int sleepSeconds = 5;
            int maxNbRetries = 20;
            Map<Integer, Track> newTracks = null;
            String msg = "";
            do {
                nbRetries++;
                try {
                    HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files/" + status.name());
                    urlBuilder.addQueryParameter("idFrom", String.valueOf(idFrom));
                    urlBuilder.addQueryParameter("nbFilesInBatch", String.valueOf(nbFilesInBatch));
                    String body = clientInfo.getBodyString(urlBuilder, client);
                    final JSONObject jObject = new JSONObject(body);
                    JSONArray files = (JSONArray) jObject.get("files");
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
                    Log.d(TAG, "ERROR: " + msg);
                    helperNotification.notifyBar(notificationSync, sleepSeconds + "s before " +
                            (nbRetries + 1) + "/" + maxNbRetries + " : " + msg);
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
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files/" + status.name());
            urlBuilder.addQueryParameter("getCount", "true");
            String body = clientInfo.getBodyString(urlBuilder, client);
            helperNotification.notifyBar(notificationSync, "Received " + status.name() + " files count ... ");
            return Integer.valueOf(body);
        }

        private void getTags() throws IOException, ServerException, JSONException {
            String body = clientInfo.getBodyString("tags", client);
            helperNotification.notifyBar(notificationSync, "Received tags ... ");
            final JSONObject jObject = new JSONObject(body);
            //FIXME: Get tags list with their respective number of files, for sorting
            //TODO: Then add a "x/y" button to display pages x/y (# of tags per page to be defined/optional)
            final JSONArray jsonTags = (JSONArray) jObject.get("tags");
            final List<String> newTags = new ArrayList<>();
            for (int i = 0; i < jsonTags.length(); i++) {
                newTags.add((String) jsonTags.get(i));
            }
            RepoTags.set(newTags);
            sendMessage("setupTags");
        }

        private void getGenres() throws IOException, ServerException, JSONException {
            String body = clientInfo.getBodyString("genres", client);
            helperNotification.notifyBar(notificationSync, "Received genres ... ");
            final JSONObject jObject = new JSONObject(body);
            final JSONArray jsonGenres = (JSONArray) jObject.get("genres");
            final List<String> newGenres = new ArrayList<>();
            for (int i = 0; i < jsonGenres.length(); i++) {
                final String genre = (String) jsonGenres.get(i);
                newGenres.add(genre);
            }
            RepoGenres.set(newGenres);
            sendMessage("setupGenres");
        }

        private void requestMerge() throws JSONException, ServerException, IOException {
            helperNotification.notifyBar(notificationSync, "Preparing statistics merge.");
            List<Track> tracks = RepoSync.getMergeList();
            for (Track track : tracks) {
                track.getTags(true);
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(Math.min(Math.max(tracks.size(), 30), 600), TimeUnit.SECONDS) //Between 30s and 10min
                    .build();
            JSONObject obj = new JSONObject();
            obj.put("type", "FilesToMerge");
            JSONArray filesToMerge = new JSONArray();
            for (Track track : tracks) {
                filesToMerge.put(track.toJSONObject());
            }
            obj.put("files", filesToMerge);
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("files");
            Request request = clientInfo.getRequestBuilder(urlBuilder)
                    .post(RequestBody.create(obj.toString(), MediaType.parse("application/json; charset=utf-8"))).build();
            helperNotification.notifyBar(notificationSync, "Requesting statistics merge.");
            String body = clientInfo.getBodyString(request, client);
            helperNotification.notifyBar(notificationSync, "Updating database with merge changes ... ");
            final JSONObject jObject = new JSONObject(body);
            JSONArray filesToUpdate = (JSONArray) jObject.get("files");
            for (int i = 0; i < filesToUpdate.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) filesToUpdate.get(i),
                        getAppDataPath, true);
                fileReceived.setStatus(Track.Status.REC);
                HelperLibrary.musicLibrary.updateTrack(fileReceived, true);
                helperNotification.notifyBar(notificationSync, "Updating database with merge changes",
                        10, i + 1, filesToUpdate.length());
            }
            helperNotification.notifyBar(notificationSync, "Merge complete.");
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
        }
    }

    public class UserStopServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()");
            stopSync("User stopped.", 1500);
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
    }

    private void startDownloads(List<Track> newTracks) {
        if ((processDownload == null || !processDownload.isAlive()) && newTracks.size() > 0) {
            Log.i(TAG, "START ProcessDownload");
            processDownload = new ProcessDownload("ProcessDownload", newTracks);
            processDownload.start();
        }
    }

    class ProcessDownload extends ProcessAbstract {

        private final List<Track> newTracks;
        private List<DownloadTask> downloadServices;
        private ExecutorService pool;
        private int nbRetries = 0;
        private final int maxNbRetries = 10;//TODO: Make number of retries an option eventually
        private final int nbFilesStart;
        private long sizeTotal;
        private long sizeRemaining;
        private int nbFailed;
        private Benchmark bench;

        ProcessDownload(String name, List<Track> newTracks) {
            super(name);
            this.newTracks = newTracks;
            nbFilesStart = newTracks.size();
            downloadServices = new ArrayList<>();
        }

        private void checkCompleted() {
            int remaining = newTracks.size();
            String msg = "Sync complete.\n\n";
            if (remaining < 1) {
                msg = msg + "All " + nbFilesStart + " files" +
                        " have been downloaded successfully.";
            } else {
                msg = msg + (nbFilesStart - remaining) + " files downloaded, " +
                        "but still " + remaining + " files to be downloaded.";
            }
            Log.i(TAG, msg);
            String finalToastMsg = msg;
            runOnUiThread(() -> helperToast.toastLong(finalToastMsg));
            stopSync(msg, -1);
        }

        private void notifyBarProgress(Track track) {
            if (track != null) {
                if (!track.getStatus().equals(Track.Status.REC)) {
                    nbFailed++;
                } else {
                    sizeRemaining -= track.getSize();
                    newTracks.remove(track);
                }
                bench.get(track.getSize());
            }
            String bigText = "-" + newTracks.size() + "/" + nbFilesStart +
                    "\n" + StringManager.humanReadableByteCount(sizeRemaining, false)
                    + "/" + StringManager.humanReadableByteCount(sizeTotal, false) + "\n" +
                    "Attempt " + (nbRetries + 1) + "/" + maxNbRetries + ". " + nbFailed + " Error(s).\n";
            String msg = "Downloading ... " + bench.getLast();
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, msg,
                    nbFilesStart, (nbFilesStart - newTracks.size()), false,
                    true, true,
                    msg + "\n" + bigText));
        }

        @Override
        public void run() {
            try {
                do {
                    checkAbort();
                    if (isCompleted() || !startDownloads()) {
                        break;
                    }
                    checkAbort();
                    if (isCompleted()) {
                        break;
                    }
                    nbRetries++;
                    int sleepSeconds = nbRetries * 10;
                    runOnUiThread(() -> helperNotification.notifyBar(notificationDownload,
                            "Waiting " + sleepSeconds + "s before attempt " +
                                    (nbRetries + 1) + "/" + maxNbRetries));
                    //noinspection BusyWait
                    sleep(sleepSeconds * 1000L);
                    checkAbort();
                } while (nbRetries < maxNbRetries - 1);
            } catch (InterruptedException e) {
                Log.i(TAG, "ProcessDownload received InterruptedException");
            }
        }

        private boolean isCompleted() {
            return newTracks.size() <= 0;
        }

        private boolean startDownloads() throws InterruptedException {
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, "Starting download ... "));
            bench = new Benchmark(newTracks.size(), 10);
            pool = Executors.newFixedThreadPool(20); //FIXME: Make number of threads an option AND add benchmark back
            downloadServices = new ArrayList<>();
            sizeTotal = 0;
            nbFailed = 0;
            wifiLock.acquire();
            for (Track track : newTracks) {
                track.getTags(true);
                DownloadTask downloadTask = new DownloadTask(track, this::notifyBarProgress, clientInfo);
                downloadServices.add(downloadTask);
                pool.submit(downloadTask);
                sizeTotal += track.getSize();
            }
            pool.shutdown();
            notifyBarProgress(null);
            return pool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
        }

        private void stopDownloads() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Stopping downloads ... "); //, 5000);
            });
            pool.shutdownNow();
            for (DownloadTask downloadService : downloadServices) {
                downloadService.abort();
            }
            abort();
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, "Downloads stopped.", 5000));
        }
    }

    static class DownloadTask extends ProcessAbstract implements Runnable {
        private final IListenerSyncDown callback;
        private final ClientInfo clientInfo;
        private final Track track;

        DownloadTask(Track track, IListenerSyncDown callback, ClientInfo clientInfo) {
            super("DownloadTask idFileServer=" + track.getIdFileServer());
            this.track = track;
            this.callback = callback;
            this.clientInfo = clientInfo;
        }

        @Override
        public void run() {
            try {
                File destinationFile = new File(track.getPath());
                File destinationPath = destinationFile.getParentFile();
                //noinspection ResultOfMethodCallIgnored
                destinationPath.mkdirs();
                checkAbort();
                if (clientDownload == null) {
                    clientDownload = new OkHttpClient.Builder()
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                }
                HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("download");
                urlBuilder.addQueryParameter("id", String.valueOf(track.getIdFileServer()));
                Request request = clientInfo.getRequestBuilder(urlBuilder).build();
                Response response = clientDownload.newCall(request).execute();
                if (response.isSuccessful()) {
                    if (destinationFile.exists()) {
                        boolean fileDeleted = destinationFile.delete();
                        Log.v("fileDeleted", fileDeleted + "");
                    }
                    boolean fileCreated = destinationFile.createNewFile();
                    Log.v("fileCreated", fileCreated + "");
                    BufferedSink sink = Okio.buffer(Okio.sink(destinationFile));
                    sink.writeAll(Objects.requireNonNull(response.body()).source());
                    Objects.requireNonNull(response.body()).source().close();
                    sink.close();
                    RepoSync.checkReceivedFile(track);
                } else {
                    switch (response.code()) {
                        case 301:
                            throw new ServerException(request.header("api-version") + " not supported. " + Objects.requireNonNull(response.body()).string());
                        case 410: //Gone
                            //Transcoded file is not available
                            track.setStatus(Track.Status.ERROR);
                            RepoSync.update(track);
                            break;
                        case 404: // File does not exist on server
                            HelperLibrary.musicLibrary.deleteTrack(track.getIdFileServer());
                            track.setStatus(Track.Status.REC); //To be ignored by current sync process
                            RepoSync.update(track);
                            break;
                        default:
                            throw new ServerException(response.code() + ": " + response.message());
                    }
                }
            } catch (InterruptedException e) {
                Log.w(TAG, "Download interrupted for " + track.getRelativeFullPath(), e);
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, "Error downloading " + track.getRelativeFullPath(), e);
                if (e.getMessage().contains("ENOSPC")) {
                    //FIXME: Stop downloads if java.io.IOException: write failed: ENOSPC (No space left on device)
                    // BUT only if sync check has completed as it can free some space
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading " + track.getRelativeFullPath(), e);
            }
            callback.setStatus(track);
        }
    }
}