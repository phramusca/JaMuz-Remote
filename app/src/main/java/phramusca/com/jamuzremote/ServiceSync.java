package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

/**
 *
 * @author phramusca
 */
public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_REMOTE";

    private ProcessDownload processDownload;
    private ClientInfo clientInfo;
    private Benchmark bench;
    private Notification notificationSync;
    private Notification notificationSyncScan;
    private BroadcastReceiver userStopReceiver;
    private int nbFiles;
    private int nbDeleted;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    @Override
    public void onCreate(){
        notificationSync = new Notification(this, NotificationId.SYNC, "Sync");
        notificationSyncScan = new Notification(this, NotificationId.SYNC_SCAN, "Sync");
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

        new Thread() {
            public void run() {
                helperNotification.notifyBar(notificationSync, getString(R.string.readingList));
                RepoSync.read();
                helperNotification.notifyBar(notificationSync, getString(R.string.connecting));
                bench = new Benchmark(RepoSync.getRemainingSize(), 10);

                String version = getVersion();
                if(!version.equals("1")) {
                    stopSync("Server version \""+version+"\" is not supported.", 5000);
                    return;
                }

                if(!getTags()) {
                    //TODO: then what ?
                }
                if(!getGenres()) {
                    //TODO: then what ?
                }
                if(!requestMerge()) {
                    //TODO: then what ?
                }

                Pair<Integer, Integer>  filesInfos = getFilesInfos();
                if(filesInfos==null) {
                    stopSync("Error receiving filesInfos: "+filesInfos+".", 5000);
                    return;
                }
                int maxIdFileServer=filesInfos.first;
                int nbFilesServer=filesInfos.second;

                //FIXME: Get maxIdFileRemote and only do the following if maxIdFileRemote<maxIdFileServer

                //Get server library
                int nbFilesInBatch=10;
                Map<Integer, Track> filesServer = new HashMap<>();
                for (int i=0; i<=nbFilesServer; i = i + nbFilesInBatch) {
                    filesServer.putAll(getFiles(i, nbFilesInBatch));
                }

                int i =0;
                for (Track trackServer:filesServer.values()) {
                    Track trackRemote = RepoSync.getFile(trackServer.getIdFileServer());
                    if(trackRemote==null) {
                        helperNotification.notifyBar(notificationSync,
                                "Inserting files", 10, i+1, filesServer.size());
                        i++;
                        trackServer.setStatus(Track.Status.INFO);
                        HelperLibrary.musicLibrary.insertOrUpdateTrack(trackServer);
                    } else {
                        //FIXME: What ?
                    }
                }

                //FIXME: Prevent inserting Status.INFO tracks in playQueue

                //FIXME: Offer to download the file

                //FIXME: Continue from here:

/*
                Track trackServer = filesServer.get(i);

                Track track = RepoSync.getFile(i);
                if(track==null) {
                    //FIXME: Insert in db
                    track.setStatus(Track.Status.INFO);
                    HelperLibrary.musicLibrary.insertOrUpdateTrack(track);
                } else {
                    //FIXME: Based on both statuses, either:
                    //- Add to download list
                    //- Delete
                    //- merge ?
                }
                */

                //FIXME: Include this in above loop
                if(!getNewFiles()) {
                    //TODO: then what ?
                }
                startSync();

                if(!checkCompleted()) {
                    //TODO: then what ?
                }
            }
        }.start();
        return START_REDELIVER_INTENT;
    }

    private String getVersion() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/version").newBuilder();
//                urlBuilder.addQueryParameter("client", key);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            helperNotification.notifyBar(notificationSync, "Received version ... ");
            String body = response.body().string();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Pair<Integer, Integer> getFilesInfos() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files/maxId").newBuilder();
//                urlBuilder.addQueryParameter("client", key);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            helperNotification.notifyBar(notificationSync, "Received maxId ... ");
            String body = response.body().string();
            //TODO: use gson instead
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            final Integer maxId = (Integer) jObject.get("max");
            final Integer count = (Integer) jObject.get("count");
            return new Pair<>(maxId, count);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getTags() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/tags").newBuilder();
//                urlBuilder.addQueryParameter("client", key);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            helperNotification.notifyBar(notificationSync, "Received tags ... ");
            String body = response.body().string();
            //TODO: use gson instead
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
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean getGenres() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/genres").newBuilder();
//                urlBuilder.addQueryParameter("client", key);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            helperNotification.notifyBar(notificationSync, "Received tags ... ");
            String body = response.body().string();
            //TODO: use gson instead
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
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean requestMerge() {
        helperNotification.notifyBar(notificationSync,"Requesting statistics merge.");
        List<Track> tracks = RepoSync.getMergeList();
        for(Track track : tracks) {
            track.getTags(true);
        }

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files").newBuilder();
        String url = urlBuilder.build().toString();
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "FilesToMerge");
            JSONArray filesToMerge = new JSONArray();
            for (Track track : tracks) {
                filesToMerge.put(track.toJSONObject());
            }
            obj.put("files", filesToMerge);
            obj.put("user", clientInfo.toJSONObject());
            Request request = new Request.Builder()
                    .post(RequestBody.create(obj.toString(), MediaType.parse("application/json; charset=utf-8"))).url(url).build();
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                return false;
            }
            helperNotification.notifyBar(notificationSync,"Updating database with merge changes ... ");
            String body = response.body().string();
            //TODO: use gson instead
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            JSONArray filesToUpdate = (JSONArray) jObject.get("files");
            for (int i = 0; i < filesToUpdate.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) filesToUpdate.get(i),
                        getAppDataPath);
                if(fileReceived.readMetadata()) {
                    fileReceived.setStatus(Track.Status.REC);
                    HelperLibrary.musicLibrary.insertOrUpdateTrack(fileReceived);
                }
                helperNotification.notifyBar(notificationSync,
                        "Updating database with merge changes", 10, i+1, filesToUpdate.length());
            }
            runOnUiThread(() -> helperNotification.notifyBar(notificationSync, "Merge complete. Request new files ... ", 2000));
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<Integer, Track> getFiles(int idFrom, int nbFilesInBatch) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/files").newBuilder();
        urlBuilder.addQueryParameter("idFrom", String.valueOf(idFrom));
        urlBuilder.addQueryParameter("nbFilesInBatch", String.valueOf(nbFilesInBatch));
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                .get().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                return null;
            }
            String body = response.body().string();
            //TODO: use gson instead
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            helperNotification.notifyBar(notificationSync, "Received files from "+idFrom);
            Map<Integer, Track> newTracks = new HashMap<>();
            JSONArray files = (JSONArray) jObject.get("files");
            for (int i = 0; i < files.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) files.get(i),
                        getAppDataPath);
                newTracks.put(fileReceived.getIdFileServer(), fileReceived);
            }
            return newTracks;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getNewFiles() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/new-files").newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .addHeader("login", clientInfo.getLogin()+"-"+clientInfo.getAppId())
                .get().url(url).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                return false;
            }
            String body = response.body().string();
            //TODO: use gson instead
//                    final Gson gson = new Gson();
//                    Results fromJson = gson.fromJson(response.body().string(), Results.class);
//                    fromJson.chromaprint=chromaprint;
            final JSONObject jObject = new JSONObject(body);
            helperNotification.notifyBar(notificationSync, "Received new list of files to get");
            ArrayList<Track> newTracks = new ArrayList<>();
            JSONArray files = (JSONArray) jObject.get("files");
            helperNotification.notifyBar(notificationSync,
                    getString(R.string.syncCheckFilesOnDisk));
            RepoSync.reset();
            HelperLibrary.musicLibrary.updateStatus(); //FIXME: Do this different as checking files takes time and meanwhile, files are not available for playing
            for (int i = 0; i < files.length(); i++) {
                Track fileReceived = new Track(
                        (JSONObject) files.get(i),
                        getAppDataPath);
                fileReceived.setStatus(Track.Status.NEW);
                RepoSync.checkNewFile(fileReceived);
                newTracks.add(fileReceived);
                helperNotification.notifyBar(notificationSync,
                        getString(R.string.syncCheckFilesOnDisk), 50, i+1, files.length());
            }
            helperNotification.notifyBar(notificationSync,
                    "Updating database with new tracks ("+newTracks.size()+"). Please wait...");
            HelperLibrary.musicLibrary.insertTrackOrUpdateStatus(newTracks);
            scanAndDeleteUnwantedInThread(getAppDataPath);
            runOnUiThread(() -> helperNotification.notifyBar(notificationSync,
                    "Received "+newTracks.size()+" files to download.", 2000));
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(userStopReceiver);
        wakeLock.release();
        wifiLock.release();
        super.onDestroy();
    }

    public class UserStopServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()");
            stopSync("User stopped.", 5000);
        }
    }

    private void stopSync(String msg, long millisInFuture) {
        if(processDownload!=null) {
            processDownload.stopDownloads();
            processDownload = null;
        }
        if (!msg.equals("")) {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationSync, msg, millisInFuture);
                helperToast.toastLong(msg);
            });
        }
        sendMessage("enableSync");
        stopSelf();
    }

    private void startSync() {
        if ((processDownload==null || !processDownload.isAlive()) && RepoSync.getRemainingSize()>0) {
            Log.i(TAG, "START ProcessDownload");
            processDownload = new ProcessDownload("ProcessDownload", this);
            processDownload.start();
        }
    }

    private boolean checkCompleted() {
        if(RepoSync.getTotalSize()>0 && RepoSync.getRemainingSize()<1) {
            final String msg = "No more files to download.";
            final String msg2 = "All " + RepoSync.getTotalSize() + " files" +
                    " have been retrieved successfully.";
            Log.i(TAG, msg);
            runOnUiThread(() -> {
                helperToast.toastLong(msg + "\n\n" + msg2);
            });
            stopSync("Sync complete.", 20000);
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

    private void scanAndDeleteUnwantedInThread(final File path) {
        ProcessAbstract processAbstract = new ProcessAbstract(
                "Thread.ActivityMain.ScanUnWantedRepoSync") {
            public void run() {
                try {
                    if(!path.getAbsolutePath().equals("/")) {
                        nbFiles = 0;
                        nbDeleted = 0;
                        browseDb();
                        nbFiles = 0;
                        browseFS(path);
                        runOnUiThread(() -> helperNotification.notifyBar(notificationSyncScan,
                                "Deleted "+nbDeleted+" unrequested files.",
                                10000));
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.ActivityMain.ScanUnWantedRepoSync InterruptedException");
                }
            }

            private void browseDb() {
                List<Track> tracks = HelperLibrary.musicLibrary.getTracks(Track.Status.DEL);
                for(Track track : tracks) {
                    File file = new File(track.getPath());
                    if(!file.exists() || file.delete()) {
                        nbDeleted++;
                        HelperLibrary.musicLibrary.deleteTrack(track.getPath());
                    }
                    nbFiles++;
                    helperNotification.notifyBar(notificationSyncScan,
                            "Scan Db. Deleted "+nbDeleted+" unrequested so far.",
                            50,
                            nbFiles, tracks.size());
                }
            }

            private void browseFS(File path) throws InterruptedException {
                checkAbort();
                if (path.isDirectory()) {
                    File[] files = path.listFiles();
                    if (files != null) {
                        if(files.length>0) {
                            for (File file : files) {
                                checkAbort();
                                if (file.isDirectory()) {
                                    browseFS(file);
                                }
                                else {
                                    if (RepoSync.checkFile(getAppDataPath, file.getAbsolutePath()))
                                    { nbDeleted++; } else {
                                        //RepoSync.checkFile deletes file. Not counting deleted
                                        //to match RepoSync.getTotalSize() at the end (hopefully)
                                        nbFiles++;
                                    }
                                    helperNotification.notifyBar(notificationSyncScan,
                                            "Scan files. Deleted "+nbDeleted+" unrequested so far.",
                                            50,
                                            nbFiles, RepoSync.getTotalSize());
                                }
                            }
                        } else {
                            Log.i(TAG, "Deleting empty folder "+path.getAbsolutePath());
                            //noinspection ResultOfMethodCallIgnored
                            path.delete();
                        }
                    }
                }
            }
        };
        processAbstract.start();
    }

    private class ProcessDownload extends ProcessAbstract {

        private List<DownloadTask> downloadServices;
        private final Notification notificationDownload;
        private ExecutorService pool;
        private int nbRetries=0;
        private final int maxNbRetries=10;//TODO: Make number of retries an option eventually

        ProcessDownload(String name, Context context) {
            super(name);
            notificationDownload = new Notification(context, NotificationId.SYNC_DOWN, "Sync");
            downloadServices= new ArrayList<>();
        }

        private void notifyBarProgress() {
            int nbFilesTotal = RepoSync.getTotalSize();
            int remaining = RepoSync.getRemainingSize();
            int progress = nbFilesTotal- remaining;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("-").append(remaining).append("/").append(nbFilesTotal)
                    .append("\n").append(StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false)).append("/").append(RepoSync.getTotalFileSize()).append("\n");
            StringBuilder stringBuilder2 = new StringBuilder();
            int nbErrors = 0;
            for(DownloadTask downloadService : downloadServices) {
                if(downloadService.status.startsWith("Err.")) {
                    nbErrors++;
                }
                else if(!downloadService.status.equals("")) {
                    stringBuilder2.append(downloadService.canal).append(": ").append(downloadService.status).append(" | ");
                }
            }
            stringBuilder.append("Attempt ").append(nbRetries+1).append("/").append(maxNbRetries).append(". ").append(nbErrors).append(" Error(s).\n");
            stringBuilder.append(stringBuilder2.toString());
            String bigText = stringBuilder.toString();
            String msg = "Downloading ... " +bench.getLast();
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, msg, nbFilesTotal, progress, false,
                        true, true,
                        msg + "\n" + bigText);
            });
        }

        @Override
        public void run() {
            boolean completed=false;
            do {
                startDownloads();
                if(checkCompleted()) {
                    completed=true;
                    break;
                }
                nbRetries++;
                try {
                    int sleepSeconds=nbRetries*10;
                    runOnUiThread(() -> {
                        helperNotification.notifyBar(notificationDownload,
                                new StringBuilder()
                                        .append("Waiting ").append(sleepSeconds)
                                        .append("s before attempt ")
                                        .append(nbRetries+1).append("/").append(maxNbRetries)
                                    .toString());
                    });
                    sleep(sleepSeconds*1000);
                } catch (InterruptedException e) {
                    break;
                }
            } while (nbRetries<maxNbRetries);

            if(!completed && !checkCompleted()) {
                stopSync("Sync done but NOT complete :(", 10000);
            }
        }

        private void startDownloads() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Starting download ... ");

            });
            bench = new Benchmark(RepoSync.getRemainingSize(), 10);
            pool = Executors.newFixedThreadPool(5); //TODO: Make number of threads an option
            downloadServices= new ArrayList<>();
            int canal=100;
            for (Track track : RepoSync.getDownloadList()) {
                track.getTags(true);
                DownloadTask downloadTask = new DownloadTask(track, canal++, () -> notifyBarProgress());
                downloadServices.add(downloadTask);
                pool.submit(downloadTask);
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Download stopped.", 5000);
            });
        }
    }

    private class DownloadTask extends ProcessAbstract implements Runnable {
        private final int canal;
        private final IListenerSyncDown callback;
        private final Track track;
        private String status="";

        private DownloadTask(Track track, int canal, IListenerSyncDown callback) {
            super("DownloadTask "+canal);
            this.track = track;
            this.canal = canal;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                setStatus("Req.", track);
                //callback.setStatus();
                String url = "http://"+clientInfo.getAddress()+":"+(clientInfo.getPort()+1)+"/download?id="+track.getIdFileServer();
                File destinationPath = new File(new File(track.getPath()).getParent());
                destinationPath.mkdirs();
                String destinationFile=new File(getAppDataPath, track.getRelativeFullPath()).getAbsolutePath();
                BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                checkAbort();
                double fileSize = track.getSize();
                // TODO: Find best. Make a benchmark (and use it in notification progres bar)
                //https://stackoverflow.com/questions/8748960/how-do-you-decide-what-byte-size-to-use-for-inputstream-read
                byte[] buf = new byte[8192];
                int bytesRead;
                int bytesReceived=0;
                while (fileSize > 0 && (bytesRead = in.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                    checkAbort();
                    fileOutputStream.write(buf, 0, bytesRead);
                    fileSize -= bytesRead;
                    bytesReceived=bytesReceived+bytesRead;
                    setStatus("Down. "+StringManager.humanReadableByteCount(bytesReceived, false)+" /", track);
                }
                fileOutputStream.close();
                setStatus("Rec.", track);
                RepoSync.checkReceivedFile(getAppDataPath, track);
                status="";
                bench.get(track.getSize());
            } catch (InterruptedException e) {
                setStatus("Interrupted", null);
                Log.w(TAG, "Download interrupted for "+track.getRelativeFullPath(), e);
            } catch (Exception e) {
                setStatus("Err. "+e.getMessage(), null);
                Log.e(TAG, "Error downloading "+track.getRelativeFullPath(), e);
            }
            callback.setStatus();
        }

        private void setStatus(String text, Track track) {
            status=text+(track==null?"":" "+StringManager.humanReadableByteCount(track.getSize(), false));
        }
    }
}