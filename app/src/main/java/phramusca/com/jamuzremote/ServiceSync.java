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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
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
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_KIDS";

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
            processDownload.stopDownloads();
        }
        if (clientSync != null) {
            clientSync.close(false, msg, millisInFuture, true);
        }
        sendMessage("enableSync");
        stopSelf();
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
        public void onDisconnected(boolean reconnect, final String msg, final long millisInFuture, boolean enable) {
            if (!msg.equals("")) {
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, msg, millisInFuture));
            }
            if (!reconnect && enable) {
                sendMessage("enableSync");
                stopSelf();
            }
        }
    }

    private void startSync() {
        if ((processDownload==null || !processDownload.isAlive()) && RepoSync.getRemainingSize()>0) {
            Log.i(TAG, "START ProcessDownload");
            processDownload = new ProcessDownload("ProcessDownload", this);
            processDownload.start();
        }
        if (!checkCompleted() && clientSync != null) {
            clientSync.close(false, "Sync part done", 2000, false);
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

        private void notifyBarProgress() {
            int nbFilesTotal = RepoSync.getTotalSize();
            int remaining = RepoSync.getRemainingSize();
            int progress = nbFilesTotal- remaining;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("-").append(remaining).append("/").append(nbFilesTotal)
                    .append("\n").append(StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false)).append("\n");
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
            stringBuilder.append(nbErrors).append(" Error(s)\n");
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
        public synchronized void run() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Starting download ... ");

            });
            bench = new Benchmark(RepoSync.getRemainingSize(), 10);
            pool = Executors.newFixedThreadPool(5);
            int canal=100;
            for (Track track : RepoSync.getDownloadList()) {
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
            if(!checkCompleted()) {
                //FIXME: Restart process. Some tracks are still missing. Why ? How ?
                stopSync("Sync done but NOT complete :(", 10000);
            }
        }

        private synchronized void stopDownloads() {
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Closing"); //, 5000);
            });
            pool.shutdownNow();
            for(DownloadTask downloadService : downloadServices) {
                downloadService.abort();
            }
            abort();
            runOnUiThread(() -> {
                helperNotification.notifyBar(notificationDownload, "Closed", 5000);
            });
        }
    }

    private class DownloadTask extends ProcessAbstract implements Runnable {
        private final int canal;
        private final IListenerSyncDown callback;
        private Track track;
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
            } catch (Exception e) {
                setStatus("Err. "+e.getMessage(), null);
                Log.e(TAG, "Error downloading "+track.getRelativeFullPath(), e);
                //FIXME: Put file back in queue
            }
            callback.setStatus();
        }

        private void setStatus(String text, Track track) {
            status=text+(track==null?"":" "+StringManager.humanReadableByteCount(track.getSize(), false));
        }
    }
}