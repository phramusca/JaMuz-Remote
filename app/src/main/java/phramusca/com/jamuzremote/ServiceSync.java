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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author phramusca
 */
public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_REMOTE";

    private ClientSync clientSync;
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
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyPowerWakelockTag");
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
                clientSync = new ClientSync(clientInfo, new ListenerSync(), true);
                if(clientSync.connect()) {
                    clientSync.request("requestTags");
                }
            }
        }.start();
        return START_REDELIVER_INTENT;
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
            processDownload.stop(msg, millisInFuture);
        }
        if (clientSync != null) {
            clientSync.close(false, msg, millisInFuture);
        } else {
            sendMessage("enableSync");
            stopSelf();
        }
    }

    class ListenerSync implements IListenerSync {

        private final String TAG = ListenerSync.class.getName();

        @Override
        public void onReceivedJson(final String json) {
            try {
                final JSONObject jObject = new JSONObject(json);
                String type = jObject.getString("type");
                switch(type) {
                    case "StartSync":
                        startSync();
                        break;
                    case "FilesToGet":
                        helperNotification.notifyBar(notificationSync, "" +
                                "Received new list of files to get");
                        ArrayList<Track> newTracks = new ArrayList<>();
                        JSONArray files = (JSONArray) jObject.get("files");
                        helperNotification.notifyBar(notificationSync,
                                getString(R.string.syncCheckFilesOnDisk));
                        RepoSync.reset();
                        HelperLibrary.musicLibrary.updateStatus();
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
                                "Inserting "+newTracks.size()+" tracks in database. Please wait...");
                        HelperLibrary.musicLibrary.insertTrackOrUpdateStatus(newTracks);
                        bench = new Benchmark(RepoSync.getRemainingSize(), 10);
                        scanAndDeleteUnwantedInThread(getAppDataPath);
                        runOnUiThread(() -> helperNotification.notifyBar(notificationSync,
                                "Received "+newTracks.size()+" files to dowload.", 2000));
                        startSync();
                        break;
                    case "mergeListDbSelected":
                        helperNotification.notifyBar(notificationSync,
                                "Updating database with merge changes ... ");
                        JSONArray filesToUpdate = (JSONArray) jObject.get("files");
                        for (int i = 0; i < filesToUpdate.length(); i++) {
                            Track fileReceived = new Track(
                                    (JSONObject) filesToUpdate.get(i),
                                    getAppDataPath);
                            if(fileReceived.readTags()) {
                                fileReceived.setStatus(Track.Status.REC);
                                HelperLibrary.musicLibrary.insertOrUpdateTrack(fileReceived);
                            }
                            helperNotification.notifyBar(notificationSync,
                                    "Updating database with merge changes", 10, i+1, filesToUpdate.length());
                        }
                        runOnUiThread(() -> helperNotification.notifyBar(notificationSync, "Merge complete. Request new files ... ", 2000));
                        clientSync.request("requestNewFiles");
                        break;
                    case "tags":
                        helperNotification.notifyBar(notificationSync, "Received tags ... ");
                        new Thread() {
                            public void run() {
                                try {
                                    final JSONArray jsonTags = (JSONArray) jObject.get("tags");
                                    final List<String> newTags = new ArrayList<>();
                                    for (int i = 0; i < jsonTags.length(); i++) {
                                        newTags.add((String) jsonTags.get(i));
                                    }
                                    RepoTags.set(newTags);
                                    sendMessage("setupTags");
                                    clientSync.request("requestGenres");
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }.start();
                        break;
                    case "genres":
                        helperNotification.notifyBar(notificationSync, "Received genres ... ");
                        new Thread() {
                            public void run() {
                                try {
                                    final JSONArray jsonGenres = (JSONArray) jObject.get("genres");
                                    final List<String> newGenres = new ArrayList<>();
                                    for (int i = 0; i < jsonGenres.length(); i++) {
                                        final String genre = (String) jsonGenres.get(i);
                                        newGenres.add(genre);
                                    }
                                    RepoGenres.set(newGenres);
                                    sendMessage("setupGenres");
                                    requestMerge();
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }.start();
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onReceivedFile(final Track fileInfoReception) {
        }

        @Override
        public void onReceivingFile(final Track fileInfoReception) {
        }

        @Override
        public void onConnected() {
            sendMessage("connectedSync");
            helperNotification.notifyBar(notificationSync, "Connected ... ");
        }

        @Override
        public void onDisconnected(boolean reconnect, final String msg, final long millisInFuture) {
            if (!msg.equals("")) {
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, msg, millisInFuture));
            }
            if (!reconnect) {
                sendMessage("enableSync");
                stopSelf();
            }
        }
    }

    private void startSync() {
        if (RepoSync.getRemainingSize()>0) {
            Log.i(TAG, "START ProcessDownload");
            processDownload = new ProcessDownload("ProcessDownload", this);
            processDownload.start();
        }
        checkCompleted();
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

    private void requestMerge() {
        runOnUiThread(() -> helperNotification.notifyBar(notificationSync,
                "Requesting statistics merge."));
        List<Track> tracks = RepoSync.getMergeList();
        for(Track track : tracks) {
            track.getTags(true);
        }
        clientSync.requestMerge(tracks);
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
        private Notification notificationDownload;
        private ExecutorService pool;

        ProcessDownload(String name, Context context) {
            super(name);
            notificationDownload = new Notification(context, NotificationId.SYNC_DOWN, "Sync");
            downloadServices= new ArrayList<>();
        }

        private void notifyBarProgress(Notification notification, int every) {
            int nbFilesTotal = RepoSync.getTotalSize();
            every=nbFilesTotal<every?nbFilesTotal/10:every;
            int remaining = RepoSync.getRemainingSize();
            int progress = nbFilesTotal- remaining;
            //if(((progress-1) % (every>0?every:1)) == 0) { //To prevent UI from freezing
                Log.i(TAG, "!!!!!!!!!!!!!! Notify Download !!!!!!!!!!!!!!");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("-").append(remaining).append("/").append(nbFilesTotal)
                        .append("\n").append(StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false));
                if(processDownload!=null) {
                    for(DownloadTask downloadService : processDownload.downloadServices) {
                        if(downloadService.clientSyncDownload!=null && downloadService.clientSyncDownload.isConnected()) {
                            stringBuilder.append("\n").append(downloadService.canal).append(": ").append(downloadService.status);
                        }
                    }
                }
                String bigText = stringBuilder.toString();
                String msg = "Downloading ... " +bench.getLast();
                runOnUiThread(() -> {
                    helperNotification.notifyBar(notification, msg, nbFilesTotal, progress, false,
                            true, true,
                            msg + "\n" + bigText);
                });
            //}
        }

        @Override
        public synchronized void run() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Starting download ... ");

            });
            pool = Executors.newFixedThreadPool(2);
            int canal=100;
            for (Track track : RepoSync.getDownloadList()) {
                DownloadTask downloadTask = new DownloadTask(track, canal++,
                        () -> notifyBarProgress(notificationDownload, 20));
                downloadServices.add(downloadTask);
                pool.submit(downloadTask);
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(checkCompleted()) {
                stop("Download complete", 5000);
            } else {
                //FIXME: Restart process. Some tracks are still missing. Why ? How ?
                stop("Download done but incomplete :(", 5000);
            }
        }

        private synchronized void stop(String msg, long millisInFuture) {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Closing"); //, 5000);
            });
            for(DownloadTask service : downloadServices) {
                service.close(msg, millisInFuture);
            }
            pool.shutdownNow();
            this.abort();
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Closed", 5000);
            });
        }
    }

    //FIXME !!!!!!! SYNC works but no progress displayed + gui freezes !!! :(
    private class DownloadTask extends ProcessAbstract implements Runnable {
        private final int canal;
        private final IListenerSyncDown callback;
        private ClientSync clientSyncDownload;
        private Track track;
        private String status="";
        private boolean completed = false;

        private DownloadTask(Track track, int canal, IListenerSyncDown callback) {
            super("DownloadTask "+canal);
            this.track = track;
            this.canal = canal;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                download();
            } catch (InterruptedException e) {
                close("Interrupted", 5000);
            }
        }

        synchronized void download() throws InterruptedException {
            clientSyncDownload = new ClientSync(new ClientInfo(clientInfo, canal), new ListenerSync(), false);
            status=getString(R.string.connecting);
            /*bench = new Benchmark(RepoSync.getRemainingSize(), 10);*/
            if(clientSyncDownload.connect()) {
                setStatus("Req.", track);
                clientSyncDownload.requestFile(track);
                while(!completed) {
                    checkAbort();
                    /*Thread.sleep(3000);*/
                }
            }
        }

        public synchronized void close(String msg, long millisInFuture) {
            if(clientSyncDownload!=null) {
                clientSyncDownload.close(false, msg, millisInFuture);
            }
            abort();
        }

        private synchronized void clean() {
            RepoSync.checkDownloadedFile(track);
            clientSyncDownload = null;
            completed=true;
        }

        private void setStatus(String text, Track track) {
            status=text+" "+StringManager.humanReadableByteCount(track.getSize(), false);
        }

        class ListenerSync implements IListenerSync {

            private final String TAG = ServiceSync.ListenerSync.class.getName();

            @Override
            public void onReceivedJson(final String json) {
            }

            @Override
            public void onReceivedFile(final Track receivedTrack) {
                Log.i(TAG, "Received file\n"+receivedTrack
                        +"\nRemaining : "+ RepoSync.getRemainingSize()
                        +"/"+ RepoSync.getTotalSize());
                /*bench.get(receivedTrack.getSize());*/
                setStatus("Rec.", receivedTrack);
                RepoSync.checkReceivedFile(getAppDataPath, receivedTrack);
                track=receivedTrack;
                callback.receivedFile();
                completed=true;
                close("Downloaded ", 1000);
            }

            @Override
            public void onReceivingFile(final Track track) {
                setStatus("Down.", track);
            }

            @Override
            public void onConnected() {
                status="Connected ... ";
            }

            @Override
            public void onDisconnected(boolean reconnect, final String msg, final long millisInFuture) {
                if (!msg.equals("")) {
                    status=msg;
                }
                clean();
            }
        }
    }
}