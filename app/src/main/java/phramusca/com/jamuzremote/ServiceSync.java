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

//FIXME: Update track if it has changed on server (format, filename, metadata ...) other than only status

/**
 *
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
    public void onCreate(){
        notificationSync = new Notification(this, NotificationId.SYNC, "Sync");
        notificationDownload= new Notification(this, NotificationId.SYNC_DOWN, "Sync");
        clientDownload = new OkHttpClient.Builder()
                //FIXME: Add and interceptor for file downloads, but need to handle better retries and notifications since it is shared among X instances
                //.addInterceptor(new RetryInterceptor(5, 5, helperNotification, notificationDownload))
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        userStopReceiver=new UserStopServiceReceiver();
        registerReceiver(userStopReceiver,  new IntentFilter(USER_STOP_SERVICE_REQUEST));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        clientInfo = (ClientInfo)intent.getSerializableExtra("clientInfo");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire(24*60*60*1000); //24 hours, enough to download a lot !
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            wifiLock= wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL,"MyWifiWakelockTag");
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
                checkAbort();

                helperNotification.notifyBar(notificationSync, getString(R.string.connecting));
                String version = getVersion();
                if(!version.equals("1")) {
                    stopSync("Server version \""+version+"\" is not supported.", 5000);
                    return;
                }

                helperNotification.notifyBar(notificationSync, getString(R.string.readingList));
                RepoSync.read();
                checkAbort();

                checkAbort();
                getTags();

                checkAbort();
                getGenres();

                checkAbort();
                requestMerge();

                checkAbort();
                //Check NEW files and start downloads
                checkFiles(Track.Status.NEW);
                checkAbort();
                startDownloads();

                //Check INFO files
                checkFiles(Track.Status.INFO);
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, "Check complete.", 5000));

                //FIXME: Do this different as filesMap gets too big an crash application !!
                //A/art: art/runtime/indirect_reference_table.cc:145] JNI ERROR (app bug): weak global reference table overflow (max=51200)
                //    art/runtime/indirect_reference_table.cc:145] weak global reference table dump:
                //    art/runtime/indirect_reference_table.cc:145]   Last 10 entries (of 51200):
                //    art/runtime/indirect_reference_table.cc:145]     51199: 0x29a78570 phramusca.com.jamuzremote.Track

                //Remove tracks that have been removed from server
//                int i=0;
//                for(Track track : RepoSync.getList()) {
//                    checkAbort();
//                    helperNotification.notifyBar(notificationSync,
//                            "Checking deleted files ...", 50, i, nbFilesServer);
//                    if(!filesMap.containsKey(track.getIdFileServer())) {
//                        File file = new File(track.getPath());
//                        file.delete();
//                        HelperLibrary.musicLibrary.deleteTrack(track.getIdFileServer());
//                    }
//                }
                checkCompleted();

            } catch (InterruptedException e) {
                Log.e(TAG, "Error ProcessSync", e);
                helperNotification.notifyBar(notificationSync, "Interrupted.");
                //stopSync also stop downloads if any so not stopping and letting user stop and restart
                //stopSync(e.getLocalizedMessage(), 5000);
            } catch (IOException | UnauthorizedException | JSONException e) {
                Log.e(TAG, "Error ProcessSync", e);
                helperNotification.notifyBar(notificationSync, "ERROR: "+e.getLocalizedMessage());
                //stopSync also stop downloads if any so not stopping and letting user stop and restart
                //stopSync(e.getLocalizedMessage(), 5000);
            }
        }

        private void checkFiles(Track.Status status)
                throws InterruptedException, JSONException, UnauthorizedException, IOException {
            int nbFilesInBatch=5000;
            int nbFilesServer = getFilesCount(status);
            String msg = "Checking "+status.name().toLowerCase()+" files ...";
            helperNotification.notifyBar(notificationSync, msg);
            if(nbFilesServer>0) {
                for (int i=0; i<=nbFilesServer; i = i + nbFilesInBatch) {
                    checkAbort();
                    Map<Integer, Track> filesMapBatch = getFiles(i, nbFilesInBatch, status);
                    int j =0;
                    for (Track trackServer:filesMapBatch.values()) {
                        checkAbort();
                        j++;
                        helperNotification.notifyBar(notificationSync, msg, 10, i+j, nbFilesServer);
                        Track trackRemote = RepoSync.getFile(trackServer.getIdFileServer());
                        if(trackRemote!=null) {
                            switch (trackServer.getStatus()) {
                                case INFO:
                                    if(!trackRemote.getStatus().equals(Track.Status.INFO)) {
                                        File file = new File(trackRemote.getPath());
                                        file.delete();
                                        HelperLibrary.musicLibrary.updateStatus(trackServer);
                                        RepoSync.updateStatus(trackServer);
                                    }
                                    break;
                                case NEW:
                                    if(! (trackRemote.getStatus().equals(Track.Status.REC)
                                            || trackRemote.getStatus().equals(Track.Status.NEW))) {
                                        HelperLibrary.musicLibrary.updateStatus(trackServer);
                                        RepoSync.updateStatus(trackServer);
                                    }
                                    break;
                            }
                        } else {
                            if(trackServer.getStatus().equals(Track.Status.NEW) && RepoSync.checkFile(trackServer)) {
                                trackServer.setStatus(Track.Status.REC);
                            }
                            HelperLibrary.musicLibrary.insertTrack(trackServer);
                            RepoSync.updateStatus(trackServer);
                        }
                    }
                    checkAbort();
                }
            }
        }

        private Map<Integer, Track> getFiles(int idFrom, int nbFilesInBatch, Track.Status status) throws IOException, UnauthorizedException, JSONException {
            RetryInterceptor interceptor = new RetryInterceptor(5,5, helperNotification, notificationSync);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files/"+status.name().toLowerCase()).newBuilder();
            urlBuilder.addQueryParameter("idFrom", String.valueOf(idFrom));
            urlBuilder.addQueryParameter("nbFilesInBatch", String.valueOf(nbFilesInBatch));
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .get().url(url).build();
            Response response;
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new UnauthorizedException(response.message());
            }
            String body = response.body().string();
            //TODO: use gson instead (or retrofit !)
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            Map<Integer, Track> newTracks = new LinkedHashMap<>();
            JSONArray files = (JSONArray) jObject.get("files");
            for (int i = 0; i < files.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) files.get(i),
                        getAppDataPath);
                newTracks.put(fileReceived.getIdFileServer(), fileReceived);
            }
            return newTracks;
        }

        private String getVersion() throws IOException, UnauthorizedException {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/version").newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .url(url).build();
            Response response;
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new UnauthorizedException(response.message());
            }
            helperNotification.notifyBar(notificationSync, "Received version ... ");
            return response.body().string();
        }

        private Integer getFilesCount(Track.Status status) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files/"+status.name().toLowerCase()).newBuilder();
            urlBuilder.addQueryParameter("getCount", "true");
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .url(url).build();
            Response response;
            try {
                response = client.newCall(request).execute();
                helperNotification.notifyBar(notificationSync, "Received "+status.name()+" files count ... ");
                return Integer.valueOf(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        private void getTags() throws IOException, UnauthorizedException, JSONException {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/tags").newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .url(url).build();
            Response response;
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new UnauthorizedException(response.message());
            }
            helperNotification.notifyBar(notificationSync, "Received tags ... ");
            String body = response.body().string();
            //TODO: use gson instead (or retrofit !)
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
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

        private void getGenres() throws IOException, UnauthorizedException, JSONException {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/genres").newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .url(url).build();
            Response response;
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new UnauthorizedException(response.message());
            }
            helperNotification.notifyBar(notificationSync, "Received genres ... ");
            String body = response.body().string();
            //TODO: use gson instead (or retrofit !)
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
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

        private void requestMerge() throws JSONException, UnauthorizedException, IOException {
            helperNotification.notifyBar(notificationSync,"Requesting statistics merge.");
            List<Track> tracks = RepoSync.getMergeList();
            for(Track track : tracks) {
                track.getTags(true);
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files").newBuilder();
            String url = urlBuilder.build().toString();
            JSONObject obj = new JSONObject();
            obj.put("type", "FilesToMerge");
            JSONArray filesToMerge = new JSONArray();
            for (Track track : tracks) {
                filesToMerge.put(track.toJSONObject());
            }
            obj.put("files", filesToMerge);
            Request request = new Request.Builder()
                    .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                    .post(RequestBody.create(obj.toString(), MediaType.parse("application/json; charset=utf-8"))).url(url).build();
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new UnauthorizedException(response.message());
            }
            helperNotification.notifyBar(notificationSync,"Updating database with merge changes ... ");
            String body = response.body().string();
            //TODO: use gson instead (or retrofit !)
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            JSONArray filesToUpdate = (JSONArray) jObject.get("files");
            for (int i = 0; i < filesToUpdate.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) filesToUpdate.get(i),
                        getAppDataPath);
                fileReceived.setStatus(Track.Status.REC);
                HelperLibrary.musicLibrary.insertOrUpdateTrack(fileReceived);
                helperNotification.notifyBar(notificationSync, "Updating database with merge changes",
                        10, i+1, filesToUpdate.length());
            }
            helperNotification.notifyBar(notificationSync, "Merge complete.");
        }
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(userStopReceiver);
        wakeLock.release();
        wifiLock.release();
        super.onDestroy();
    }

    private static class UnauthorizedException extends Exception {
        public UnauthorizedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class UserStopServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()");
            stopSync("User stopped.", 1500);
        }
    }

    private void stopSync(String msg, long millisInFuture) {
        if(processDownload!=null) {
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

    private void startDownloads() {
        if ((processDownload==null || !processDownload.isAlive()) && RepoSync.getRemainingSize()>0) {
            Log.i(TAG, "START ProcessDownload");
            processDownload = new ProcessDownload("ProcessDownload");
            processDownload.start();
        }
    }

    private boolean checkCompleted() {
        if(RepoSync.getTotalSize()>0 && RepoSync.getRemainingSize()<1) {
            final String msg = "No more files to download.";
            final String msg2 = "All " + RepoSync.getTotalSize() + " files" +
                    " have been retrieved successfully.";
            Log.i(TAG, msg);
            runOnUiThread(() -> helperToast.toastLong(msg + "\n\n" + msg2));
            stopSync("Sync complete.", -1);
            return true;
        } else if (RepoSync.getTotalSize()<=0){
            Log.i(TAG, "No files to download.");
            runOnUiThread(() -> helperToast.toastLong("No files to download." +
                    "\n\nYou can use JaMuz (Linux/Windows) to " +
                    "export a list of files to retrieve, based on playlists."));
            stopSync("No files to download.", 5000);
            return true;
        }
        return false;
    }

    class ProcessDownload extends ProcessAbstract {

        private List<DownloadTask> downloadServices;
        private ExecutorService pool;
        private int nbRetries=0;
        private final int maxNbRetries=10;//TODO: Make number of retries an option eventually
        private int nbFilesTotal;
        private int nbFiles;
        private int nbFailed;

        ProcessDownload(String name) {
            super(name);
            downloadServices= new ArrayList<>();
        }

        private void notifyBarProgress(Track track) {

            if(!track.getStatus().equals(Track.Status.REC)) {
                nbFailed++;
            } else {
                nbFiles++;
            }
            int remaining = nbFilesTotal - nbFiles;
            //FIXME: DO NOT use getRemainingFileSize and only getTotalFileSize at startup
            String bigText = "-" + remaining + "/" + nbFilesTotal +
                    "\n" + StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false) + "/" + RepoSync.getTotalFileSize() + "\n" +
                    "Attempt " + (nbRetries + 1) + "/" + maxNbRetries + ". " + nbFailed + " Error(s).\n";
            String msg = "Downloading ... ";
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, msg, nbFilesTotal, nbFiles, false,
                    true, true,
                    msg + "\n" + bigText));
        }

        @Override
        public void run() {
            boolean completed=false;
            try {
                do {
                    checkAbort();
                    if(!startDownloads()) {
                        break;
                    }
                    if(checkCompleted()) {
                        completed=true;
                        break;
                    }
                    checkAbort();
                    nbRetries++;
                    int sleepSeconds=nbRetries*10;
                    runOnUiThread(() -> helperNotification.notifyBar(notificationDownload,
                            "Waiting " + sleepSeconds + "s before attempt " +
                                    (nbRetries + 1) + "/" + maxNbRetries));
                    sleep(sleepSeconds*1000);
                    checkAbort();
                } while (nbRetries<maxNbRetries);
            } catch (InterruptedException e) {
                Log.i(TAG, "ProcessDownload received InterruptedException");
            }
            if(!completed && !checkCompleted()) {
                stopSync("Sync done but NOT complete :(", -1);
            }
            //FIXME: checkCompleted can stop sync as downloads are complete though check info files may still run !!
        }

        private boolean startDownloads() {
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, "Starting download ... "));
            pool = Executors.newFixedThreadPool(20); //TODO: Make number of threads an option
            downloadServices= new ArrayList<>();
            nbFilesTotal=0;
            nbFiles=0;
            nbFailed=0;
            for (Track track : RepoSync.getDownloadList()) {
                track.getTags(true);
                DownloadTask downloadTask = new DownloadTask(track, this::notifyBarProgress, clientInfo);
                downloadServices.add(downloadTask);
                pool.submit(downloadTask);
                nbFilesTotal++;
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void stopDownloads() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Stopping downloads ... "); //, 5000);
            });
            pool.shutdownNow();
            for(DownloadTask downloadService : downloadServices) {
                downloadService.abort();
            }
            abort();
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, "Download stopped.", 5000));
        }
    }

    static class DownloadTask extends ProcessAbstract implements Runnable {
        private final IListenerSyncDown callback;
        private final ClientInfo clientInfo;
        private final Track track;

        DownloadTask(Track track, IListenerSyncDown callback, ClientInfo clientInfo) {
            super("DownloadTask idFileServer="+track.getIdFileServer());
            this.track = track;
            this.callback = callback;
            this.clientInfo = clientInfo;
        }

        @Override
        public void run() {
            try {
                String url = "http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/download?id="+track.getIdFileServer();
                File destinationFile=new File(track.getPath());
                File destinationPath = destinationFile.getParentFile();
                destinationPath.mkdirs();
                checkAbort();
                Request request = new Request.Builder()
                        .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                        .url(url).build();
                Response response;
                if(clientDownload==null) {
                    clientDownload = new OkHttpClient.Builder()
                            //FIXME: Add and interceptor for file downloads, but need to handle better retries and notifications since it is shared among X instances
                            //.addInterceptor(new RetryInterceptor(5, 5, helperNotification, notificationDownload))
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build();
                }
                response = clientDownload.newCall(request).execute();
                if(!response.isSuccessful()) {
                    throw new UnauthorizedException(response.message());
                }
                if (destinationFile.exists()) {
                    boolean fileDeleted = destinationFile.delete();
                    Log.v("fileDeleted", fileDeleted + "");
                }
                boolean fileCreated = destinationFile.createNewFile();
                Log.v("fileCreated", fileCreated + "");
                BufferedSink sink = Okio.buffer(Okio.sink(destinationFile));
                sink.writeAll(response.body().source());
                sink.close();
                RepoSync.checkReceivedFile(track);
            } catch (InterruptedException e) {
                Log.w(TAG, "Download interrupted for "+track.getRelativeFullPath(), e);
            } catch (Exception e) {
                Log.e(TAG, "Error downloading "+track.getRelativeFullPath(), e);
            }
            callback.setStatus(track);
        }
    }
}