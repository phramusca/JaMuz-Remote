package phramusca.com.jamuzkids;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
        new Thread() {
            public void run() {
                helperNotification.notifyBar(notificationSync, getString(R.string.readingList));
                RepoSync.read(getAppDataPath);
                bench = new Benchmark(RepoSync.getRemainingSize(), 10);
                helperNotification.notifyBar(notificationSync, getString(R.string.connecting));
                clientSync = new ClientSync(clientInfo, new CallBackSync());
                clientSync.connect();
            }
        }.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(userStopReceiver);
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
                        requestNextFile(false);
                        break;
                    case "insertDeviceFileSAck":
                        JSONArray jsonArray = (JSONArray) jObject.get("filesAcked");
                        if (jsonArray.length() == 1) {
                            FileInfoReception fileReceived = new FileInfoReception(
                                    (JSONObject) jsonArray.get(0));
                            notifyBar("Ack.", fileReceived);
                            RepoSync.receivedAck(fileReceived);
                            bench.get(fileReceived.size);
                        } else {
                            notifyBar("Received ack from server");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                FileInfoReception fileReceived = new FileInfoReception(
                                        (JSONObject) jsonArray.get(i));
                                RepoSync.receivedAck(fileReceived);
                            }
                            bench = new Benchmark(RepoSync.getRemainingSize(), 10);
                        }
                        requestNextFile();
                        break;
                    case "FilesToGet":
                        helperNotification.notifyBar(notificationSync, "" +
                                "Received new list of files to get");
                        Map<Integer, FileInfoReception> newTracks = new HashMap<>();
                        JSONArray files = (JSONArray) jObject.get("files");
                        for (int i = 0; i < files.length(); i++) {
                            FileInfoReception fileReceived = new FileInfoReception(
                                    (JSONObject) files.get(i));
                            newTracks.put(fileReceived.idFile, fileReceived);
                        }
                        helperNotification.notifyBar(notificationSync,
                                "Checking if files are already on disk ... ");
                        RepoSync.set(getAppDataPath, newTracks);
                        scanAndDeleteUnwantedInThread(getAppDataPath);
                        requestNextFile();
                        break;
                    case "mergeListDbSelected":
                        helperNotification.notifyBar(notificationSync,
                                "Updating database with merge changes ... ");
                        JSONArray filesToUpdate = (JSONArray) jObject.get("files");
                        //FIXME: Display merge progress
                        for (int i = 0; i < filesToUpdate.length(); i++) {
                            FileInfoReception fileReceived = new FileInfoReception(
                                    (JSONObject) filesToUpdate.get(i));
                            HelperLibrary.musicLibrary.insertOrUpdateTrackInDatabase(
                                    new File(getAppDataPath, fileReceived.relativeFullPath)
                                            .getAbsolutePath(), fileReceived);
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
        public void receivedFile(final FileInfoReception fileInfoReception) {
            Log.i(TAG, "Received file\n"+fileInfoReception
                    +"\nRemaining : "+ RepoSync.getRemainingSize()
                    +"/"+ RepoSync.getTotalSize());
            notifyBar("Rec.", fileInfoReception);
            RepoSync.checkFile(getAppDataPath, fileInfoReception,  FileInfoReception.Status.LOCAL);
            requestNextFile();
        }

        @Override
        public void receivingFile(final FileInfoReception fileInfoReception) {
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

    private void notifyBar(String text) {
        notifyBar(text, null);
    }

    private void notifyBar(String text, FileInfoReception fileInfoReception) {
        int max = RepoSync.getTotalSize();
        int remaining = RepoSync.getRemainingSize();
        int progress = max- remaining;

        String bigText = "-"+ remaining + "/" + max
                + "\n-" + StringManager.humanReadableByteCount(RepoSync.getRemainingFileSize(), true)
                + "\n" + (fileInfoReception==null?"":fileInfoReception.relativeFullPath);

        String msg = text
                +(fileInfoReception==null
                    ?"":
                    (" "+StringManager.humanReadableByteCount(fileInfoReception.size, false))
                )
                +bench.getLast();

        helperNotification.notifyBar(notificationSync, msg, max, progress, false,
                true, true,
                msg+"\n"+bigText);
    }

    private void requestNextFile() {
        requestNextFile(true);
    }

    private void requestNextFile(final boolean scanLibrary) {
        //First inserting LOCAL in database if not already there
        List<FileInfoReception> localFiles = RepoSync.getLocal();
        if(localFiles.size()>1)  {
            notifyBar("Checking if files are already in database ... ");
        }
        if(localFiles.size()>0) {
            //Get tracks from database matching local tracks
            List<Track> tracksInDb =
                    HelperLibrary.musicLibrary.getTracks(localFiles, getAppDataPath);
            if(localFiles.size()>tracksInDb.size()) {
                //If some local files are not in database, inserting them
                HashMap<String, Track> tracksInDbMap = new HashMap<>();
                for (Track track : tracksInDb) {
                    tracksInDbMap.put(track.getPath(), track);
                }
                if(localFiles.size()==1) {
                    notifyBar("Read", localFiles.get(0));
                } else {
                    notifyBar("Reading files metadata for insertion ... ");
                }
                List<Track> tracksNotYetInDb = new ArrayList<>();
                List<FileInfoReception> localFilesInserted = new ArrayList<>();
                for (FileInfoReception fileInfoReception : localFiles) {
                    String fullPath = new File(getAppDataPath, fileInfoReception.relativeFullPath).getAbsolutePath();
                    if (!tracksInDbMap.containsKey(fullPath)) {
                        tracksNotYetInDb.add(HelperLibrary.musicLibrary.getTrack(fullPath, fileInfoReception));
                        localFilesInserted.add(fileInfoReception);
                    }
                }
                if(localFiles.size()==1) {
                    notifyBar("Ins.", localFiles.get(0));
                } else {
                    notifyBar("Inserting missing tracks in database ... ");
                }
                if (HelperLibrary.musicLibrary.insertTracks(tracksNotYetInDb)) {
                    for (FileInfoReception fileInfoReception : localFilesInserted) {
                        /*notifyBar("Ins.", fileInfoReception);*/
                        RepoSync.receivedInDb(fileInfoReception);
                    }
                }
            } else {
                for (FileInfoReception fileInfoReception : localFiles) {
                    RepoSync.receivedInDb(fileInfoReception);
                }
            }
        }
        //Second, ack reception of the files IN_DB
        // (server will insert in deviceFile and statsource tables and ack back)
        List<FileInfoReception> inDbFiles = RepoSync.getInDb();
        if(inDbFiles.size()>0) {
            if (inDbFiles.size() == 1) {
                notifyBar("Ack+", inDbFiles.get(0));
            } else {
                notifyBar("Sending ack to server and waiting ack from server ... ");
            }
            sendMessage("refreshSpinner(true)");

            clientSync.ackFilesReception(inDbFiles);
        } else {
            //Finally request a NEW
            final FileInfoReception fileInfoReception = RepoSync.takeNew();
            Log.i(TAG, "requestNextFile file: \n"+fileInfoReception);
            if (fileInfoReception != null) {
                new Thread() {
                    @Override
                    public void run() {
                        //Save in case of crash or android kill or reboot or whatever issue can occur
                        //but not too often not to destroy storage
                        //Is every 10 ack. enough or too much ?
                        if(((RepoSync.getRemainingSize()-1) % 10) == 0) {
                            RepoSync.save();
                        }
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
                    helperNotification.notifyBar(notificationSync, "Getting list of files for stats merge.");
                });
                List<Track> tracks = new Playlist("FilesToMerge", false).getTracks();
                runOnUiThread(() -> helperNotification.notifyBar(notificationSync, "Requesting statistics merge."));
                for(Track track : tracks) {
                    track.getTags(true);
                }
                clientSync.requestMerge(tracks, getAppDataPath);

                //FIXME: Delete from db whenever deleting a file in "internal" folder
                //          (may already be done, check that FIRST)
                //So that it is not required to scan (deleted) after sync (below)

                Log.i(TAG, "Updating library:" + scanLibrary);
                if (scanLibrary) {
                    scanAndDeleteUnwantedInThread(getAppDataPath);

                    //TODO: Scan only "internal" folder (scan deleted only), not the user folder
                    sendMessage("checkPermissionsThenScanLibrary");
                }
            } else {
                Log.i(TAG, "No files to download.");
                runOnUiThread(() -> helperToast.toastLong("No files to download.\n\nYou can use JaMuz (Linux/Windows) to " +
                        "export a list of files to retrieve, based on playlists."));
                stopSync("No files to download.", 5000);
            }
        }
    }

    private void scanAndDeleteUnwantedInThread(final File path) {
        ProcessAbstract processAbstract = new ProcessAbstract(
                "Thread.MainActivity.ScanUnWantedRepoSync") {
            public void run() {
                try {
                    if(!path.getAbsolutePath().equals("/")) {
                        nbFiles = 0;
                        nbDeleted = 0;
                        browseFS(path);
                        runOnUiThread(() -> helperNotification.notifyBar(notificationSyncScan,
                                "Deleted "+nbDeleted+" unrequested files.",
                                10000));
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.MainActivity.ScanUnWantedRepoSync InterruptedException");
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
                                    String absolutePath=file.getAbsolutePath();
                                    String relativeFullPath = absolutePath.substring(
                                            getAppDataPath.getAbsolutePath().length()+1);
                                    if(!RepoSync.checkFile(getAppDataPath, relativeFullPath)) {
                                        //RepoSync.checkFile deletes file. Not couting deleted
                                        //to match RepoSync.getTotalSize() at the end (hopefully)
                                        nbFiles++;
                                    } else { nbDeleted++; }
                                    helperNotification.notifyBar(notificationSyncScan,
                                            "Deleting unrequested: "+nbDeleted+" so far.",
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