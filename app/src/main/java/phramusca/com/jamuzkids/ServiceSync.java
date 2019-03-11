package phramusca.com.jamuzkids;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author phramusca
 */
public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE_SCAN_KIDS";

    private ClientSync clientSync;
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

        final ClientInfo clientInfo = (ClientInfo)intent.getSerializableExtra("clientInfo");

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
                clientSync = new ClientSync(clientInfo, new CallBackSync());
                clientSync.connect();
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
        if (clientSync != null) {
            clientSync.close(false, msg, millisInFuture);
        } else {
            sendMessage("enableSync");
            stopSelf();
        }
    }

    class CallBackSync implements ICallBackSync {

        private final String TAG = CallBackSync.class.getName();

        @Override
        public void receivedJson(final String json) {
            try {
                final JSONObject jObject = new JSONObject(json);
                String type = jObject.getString("type");
                switch(type) {
                    case "StartSync":
                        requestNextFile();
                        break;
                    case "insertDeviceFileSAck":
                        JSONArray jsonArray = (JSONArray) jObject.get("filesAcked");
                        if (jsonArray.length() == 1) {
                            Track fileReceived = new Track(
                                    (JSONObject) jsonArray.get(0),
                                    getAppDataPath);
                            notifyBar("Ack+", fileReceived);
                            RepoSync.receivedAck(fileReceived);
                            bench.get(fileReceived.getSize());
                        } else {
                            notifyBar("Received ack from server");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Track fileReceived = new Track(
                                        (JSONObject) jsonArray.get(i),
                                        getAppDataPath);
                                //FIXME: Display progress
                                RepoSync.receivedAck(fileReceived);
                            }
                            bench = new Benchmark(RepoSync.getRemainingSize(), 10);
                        }
                        requestNextFile();
                        break;
                    case "FilesToGet":
                        helperNotification.notifyBar(notificationSync, "" +
                                "Received new list of files to get");
                        Map<Integer, Track> newTracks = new HashMap<>();
                        JSONArray files = (JSONArray) jObject.get("files");
                        for (int i = 0; i < files.length(); i++) {
                            Track fileReceived = new Track(
                                    (JSONObject) files.get(i),
                                    getAppDataPath);
                            fileReceived.setStatus(Track.Status.NEW);
                            newTracks.put(fileReceived.getIdFileServer(), fileReceived);
                        }
                        helperNotification.notifyBar(notificationSync,
                                getString(R.string.syncCheckFilesOnDisk));
                        //FIXME: Display a progress bar as process can take some time
                        RepoSync.set(newTracks);
                        /*helperNotification.notifyBar(notificationSync, "Checking files on disk", nbTotal, progress, false, true, true, "Big Text");
                        */
                        scanAndDeleteUnwantedInThread(getAppDataPath);
                        requestNextFile();
                        break;
                    case "mergeListDbSelected":
                        helperNotification.notifyBar(notificationSync,
                                "Updating database with merge changes ... ");
                        JSONArray filesToUpdate = (JSONArray) jObject.get("files");
                        //FIXME: Display merge progress
                        for (int i = 0; i < filesToUpdate.length(); i++) {
                            Track fileReceived = new Track(
                                    (JSONObject) filesToUpdate.get(i),
                                    getAppDataPath);
                            fileReceived.readTags();
                            HelperLibrary.musicLibrary.insertOrUpdateTrackInDatabase(fileReceived);
                        }
                        stopSync("Sync complete.", 20000);
                        stopSelf();
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
                                    clientSync.request("requestNewFiles");
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
        public void receivedFile(final Track fileInfoReception) {
            Log.i(TAG, "Received file\n"+fileInfoReception
                    +"\nRemaining : "+ RepoSync.getRemainingSize()
                    +"/"+ RepoSync.getTotalSize());
            notifyBar("Rec.", fileInfoReception);
            RepoSync.receivedFile(getAppDataPath, fileInfoReception);
            requestNextFile();
        }

        @Override
        public void receivingFile(final Track fileInfoReception) {
            notifyBar("Down", fileInfoReception);
        }

        @Override
        public void connected() {
            sendMessage("connectedSync");
            helperNotification.notifyBar(notificationSync, "Connected ... ");
        }

        @Override
        public void disconnected(boolean reconnect, final String msg, final long millisInFuture) {
            if (!msg.equals("")) {
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, msg, millisInFuture));
            }
            if (!reconnect) {
                sendMessage("enableSync");
                stopSelf();
            }
        }
    }

    //FIXME: For displaying check files on disk when receiving a new list to be retrieved
  /*  private void notifyBar() {
        int max = RepoSync.getTotalSize();
        int remaining = RepoSync.getRemainingSize();
        int progress = max- remaining;

        String bigText = "-"+ remaining + "/" + max
                + "\n-" + StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false)
                + "\n" + (fileInfoReception==null?"":fileInfoReception.getRelativeFullPath());

        String msg = text
                +(fileInfoReception==null
                ?"":
                (" "+StringManager.humanReadableByteCount(fileInfoReception.getSize(), false))
        )
                +bench.getLast();

        helperNotification.notifyBar(notificationSync, msg, max, progress, false,
                true, true,
                msg+"\n"+bigText);
    }*/

    private void notifyBar(String text) {
        notifyBar(text, null);
    }

    private void notifyBar(String text, Track fileInfoReception) {
        int max = RepoSync.getTotalSize();
        int remaining = RepoSync.getRemainingSize();
        int progress = max- remaining;

        String bigText = "-"+ remaining + "/" + max
                + "\n-" + StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), false)
                + "\n" + (fileInfoReception==null?"":fileInfoReception.getRelativeFullPath());

        String msg = text
                +(fileInfoReception==null
                    ?"":
                    (" "+StringManager.humanReadableByteCount(fileInfoReception.getSize(), false))
                )
                +bench.getLast();

        helperNotification.notifyBar(notificationSync, msg, max, progress, false,
                true, true,
                msg+"\n"+bigText);
    }

    private void requestNextFile() {

        //First, Acknowledge (ACK) reception of received files (REC)
        // (server will insert in deviceFile and stat source tables and ack back)
        List<Track> received = RepoSync.getReceived();
        if(received.size()>0) {
            if (received.size() == 1) {
                notifyBar("Ack.", received.get(0));
            } else {
                notifyBar("Sending ack to server and waiting ack from server ... ");
            }
            sendMessage("refreshSpinner(true)");
            clientSync.ackFilesReception(received);
        } else {
            //Second, request a NEW
            final Track fileInfoReception = RepoSync.takeNew();
            Log.i(TAG, "requestNextFile file: \n"+fileInfoReception);
            if (fileInfoReception != null) {
                new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> notifyBar("Req.", fileInfoReception));
                        clientSync.requestFile(fileInfoReception);
                    }
                }.start();
            } else if(RepoSync.getTotalSize()>0) {
                final String msg = "No more files to download.";
                final String msg2 = "All " + RepoSync.getTotalSize() + " files" +
                        " have been retrieved successfully.";
                Log.i(TAG, msg);
                runOnUiThread(() -> {
                    helperToast.toastLong(msg + "\n\n" + msg2);
                    helperNotification.notifyBar(notificationSync,
                            "Getting list of files for stats merge.");
                });
                //FIXME: Request merge before getting new list of devices
                List<Track> tracks = HelperLibrary.musicLibrary.getTracks(Track.Status.ACK);
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync,
                        "Requesting statistics merge."));
                for(Track track : tracks) {
                    track.getTags(true);
                }
                clientSync.requestMerge(tracks);
            } else {
                Log.i(TAG, "No files to download.");
                runOnUiThread(() -> helperToast.toastLong("No files to download." +
                        "\n\nYou can use JaMuz (Linux/Windows) to " +
                        "export a list of files to retrieve, based on playlists."));
                stopSync("No files to download.", 5000);
            }
        }
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
}